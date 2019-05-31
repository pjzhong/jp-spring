package jp.spring.ioc.beans.factory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.InjectField;
import jp.spring.ioc.beans.PropertyValue;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.util.TypeUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 默认对象工厂
 *
 * TODO THE WHOLE FACTORY NEED TO REWRITE
 *
 * @author ZJP
 * @since 2019年05月28日 20:52:39
 **/
public class DefaultBeanFactory implements BeanFactory {

  private final Map<String, BeanDefinition> beanNameDefinitionMap = new ConcurrentHashMap<>();

  private final Map<Class<?>, String[]> beanNamesByType = new ConcurrentHashMap<>();

  private final Map<Class<?>, List<?>> beansByType = new ConcurrentHashMap<>();

  private final Map<Class<? extends Annotation>, List<String>> beanNamesByAnnotation = new ConcurrentHashMap<>();

  private final List<String> beanDefinitionIds = new ArrayList<>();

  private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

  private final Properties properties = new Properties();

  public DefaultBeanFactory() {

  }

  public void refresh() {
    try {
      beforeInitializeBean();
    } catch (Exception e) {
      throw new RuntimeException("Error Raised when refresh factory", e);
    }
  }

  @Override
  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
    if (beanNameDefinitionMap.containsKey(name)) {
      throw new IllegalArgumentException("Bean name " + name + " must be unique");
    }
    beanNameDefinitionMap.put(name, beanDefinition);
    beanDefinitionIds.add(name);
  }

  @Override
  public Object getBean(String name) {
    BeanDefinition beanDefinition = beanNameDefinitionMap.get(name);
    if (beanDefinition == null) {
      throw new IllegalArgumentException("No bean named " + name + " is defined");
    }
    Object bean = beanDefinition.getBean();
    if (bean == null) {
      bean = doCreateBean(beanDefinition);
      bean = afterInitializeBean(bean, name);
      beanDefinition.setBean(bean);
    }
    return beanDefinition.getBean();
  }

  public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
    this.beanPostProcessors.add(beanPostProcessor);
  }

  private void beforeInitializeBean() throws Exception {
    for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
      beanPostProcessor.postProcessBeforeInitialization();
    }
  }

  private Object afterInitializeBean(Object bean, String name) {
    for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
      bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
    }

    invokeAware(bean);
    return bean;
  }

  private void invokeAware(Object bean) {
    if (bean instanceof BeanFactoryAware) {
      ((BeanFactoryAware) bean).setBeanFactory(this);
    }
  }

  //various getters
  public Class<?> getType(String name) {
    if (beanNameDefinitionMap.get(name) != null) {
      return beanNameDefinitionMap.get(name).getClazz();
    }
    return null;
  }

  public <A> List<A> getBeansByType(Class<A> type) {
    if (beansByType.get(type) != null) {
      return (List<A>) beansByType.get(type);
    }

    List<A> beans = new ArrayList<>();
    for (String beanDefinitionName : beanDefinitionIds) {
      if (type.isAssignableFrom(beanNameDefinitionMap.get(beanDefinitionName).getClazz())) {
        beans.add((A) getBean(beanDefinitionName));
      }
    }

    if (ObjectUtils.isNotEmpty(beans)) {
      beansByType.put(type, beans);
    }
    return beans;
  }

  private List<String> getBeanNamesForType(Class<?> targetType) {
    List<String> result = new ArrayList<>();

    String[] temp = beanNamesByType.get(targetType);
    if (temp != null) {
      result = Arrays.asList(temp);
      return result;
    }

    boolean matchFound;
    for (String beanName : beanDefinitionIds) {
      BeanDefinition beanDefinition = beanNameDefinitionMap.get(beanName);
      matchFound = targetType.isAssignableFrom(beanDefinition.getClazz());
      if (matchFound) {
        result.add(beanName);
      }
    }

    beanNamesByType.put(targetType, result.toArray(new String[0]));
    return result;
  }

  public List<String> getBeanNamByAnnotation(Class<? extends Annotation> annotation) {
    if (beanNamesByAnnotation.get(annotation) != null) {
      return beanNamesByAnnotation.getOrDefault(annotation, Collections.emptyList());
    }

    List<String> result = new ArrayList<>();
    BeanDefinition beanDefinition;
    for (String beanName : beanDefinitionIds) {
      beanDefinition = beanNameDefinitionMap.get(beanName);
      if (TypeUtil.isAnnotated(beanDefinition.getClazz(), annotation)) {
        result.add(beanName);
      }
    }

    beanNamesByAnnotation.put(annotation, Collections.unmodifiableList(result));
    return result;
  }

  public Properties getProperties() {
    return properties;
  }

  private Object doCreateBean(BeanDefinition beanDefinition) {
    Object bean = null;
    try {
      bean = createBeanInstance(beanDefinition);
      beanDefinition.setBean(bean);
      resolveDependency(bean, beanDefinition);
      injectPropertyValue(bean, beanDefinition);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bean;
  }

  private Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
    return beanDefinition.getClazz().newInstance();
  }

  private void resolveDependency(Object bean, BeanDefinition beanDefinition) throws Exception {
    /* 两种情况:
     * 1.没有@Qualifier, 那么根据类型来获取注入对象。多个取第一个
     * 2.用户添加了@Qualifier, 使用@Qualifier的值来获取注入对象
     */

    for (InjectField injectField : beanDefinition.getInjectFields()) {
      Object value;
      if (StringUtils.isBlank(injectField.getQualifier())) {
        Map<String, Object> matchingBeans = findAutowireCandidates(
            injectField.getType());
        value = matchingBeans.entrySet().iterator().next().getValue();
      } else {
        value = getBean(injectField.getQualifier());
      }

      if (value == null && injectField.isRequired()) {
        throw new BeansException(String
            .format("Inject %s to %s failed", injectField.getType(),
                beanDefinition.getClazz()));
      }

      injectField.inject(bean, value);
    }
  }

  private void injectPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {

    for (PropertyValue propertyValue : beanDefinition.getPropertyValues()) {
      Object value = null;
      String strValue = getProperties().getProperty(propertyValue.getName());
      if ((strValue != null) && TypeUtil.isSimpleType(propertyValue.getField().getType())) {
        value = TypeUtil.convert(strValue, propertyValue.getField().getType());
      }

      if (value == null && propertyValue.isRequired()) {
        throw new BeansException(String.format("Inject %s to %s failed", propertyValue.getName(),
            beanDefinition.getBeanClassName()));
      }

      if (value != null) {
        propertyValue.inject(bean, value);
      }
    }
  }

  private Map<String, Object> findAutowireCandidates(Class<?> requiredType) {
    List<String> candidateNames = getBeanNamesForType(requiredType);
    Map<String, Object> result = new LinkedHashMap<>(candidateNames.size());
    for (String candidateName : candidateNames) {
      result.put(candidateName, getBean(candidateName));
    }
    return result;
  }
}
