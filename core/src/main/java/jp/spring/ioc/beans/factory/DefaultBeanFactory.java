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
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.InjectField;
import jp.spring.ioc.beans.PropertyValue;
import jp.spring.ioc.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 12/25/2016. 简单的规定了如何获取和注册Bean, 至于如何创造Bean，则留给子类去实现
 */
public class DefaultBeanFactory implements BeanFactory {

  private final Map<String, BeanDefinition> beanNameDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

  private final Map<Class<?>, String[]> beanNamesByType = new ConcurrentHashMap<Class<?>, String[]>();

  private final Map<Class<?>, List<?>> beansByType = new ConcurrentHashMap<>();

  private final Map<Class<? extends Annotation>, List<String>> beanNamesByAnnotation = new ConcurrentHashMap<>();

  private final List<String> beanDefinitionIds = new ArrayList<String>();

  private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

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
  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
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

  public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) throws Exception {
    this.beanPostProcessors.add(beanPostProcessor);
  }

  protected void beforeInitializeBean() throws Exception {
    if (!beanPostProcessors.isEmpty()) {
      for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
        beanPostProcessor.postProcessBeforeInitialization();
      }
    }
  }

  protected Object afterInitializeBean(Object bean, String name) {
    if (!beanPostProcessors.isEmpty()) {
      for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
        bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
      }
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

    List<A> beans = new ArrayList<A>();
    for (String beanDefinitionName : beanDefinitionIds) {
      if (type.isAssignableFrom(beanNameDefinitionMap.get(beanDefinitionName).getClazz())) {
        beans.add((A) getBean(beanDefinitionName));
      }
    }
    if (!TypeUtil.isEmpty(beans)) {
      beansByType.put(type, beans);
    }
    return beans;
  }

  public List<String> getBeanNamesForType(Class<?> targetType) {
    List<String> result = new ArrayList<String>();

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

    if (!TypeUtil.isEmpty(result)) {
      beanNamesByType.put(targetType, result.toArray(new String[result.size()]));
    }
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

    if (!TypeUtil.isEmpty(result)) {
      beanNamesByAnnotation.put(annotation, Collections.unmodifiableList(result));
    }
    return result;
  }

  public Properties getProperties() {
    return properties;
  }

  protected Object doCreateBean(BeanDefinition beanDefinition) {
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

  protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
    return beanDefinition.getClazz().newInstance();
  }

  protected void resolveDependency(Object bean, BeanDefinition beanDefinition) throws Exception {
    List<InjectField> fields = beanDefinition.getInjectFields();
    if (TypeUtil.isEmpty(fields)) {
      return;
    }

    /* 两种情况:
     * 1.没有@Qualifier, 那么根据类型来获取注入对象。多个取第一个
     * 2.用户添加了@Qualifier, 使用@Qualifier的值来获取注入对象
     */
    Object value = null;
    for (InjectField injectField : beanDefinition.getInjectFields()) {

      if (StringUtils.isBlank(injectField.getQualifier())) {
        Map<String, Object> matchingBeans = findAutowireCandidates(
            injectField.getType());
        if (!TypeUtil.isEmpty(matchingBeans)) {
          value = matchingBeans.entrySet().iterator().next().getValue();
        }
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

  protected void injectPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {
    List<PropertyValue> values = beanDefinition.getPropertyValues();
    if (values == null) {
      return;
    }

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

  protected Map<String, Object> findAutowireCandidates(Class<?> requiredType) {
    Map<String, Object> result = null;

    try {
      List<String> candidateNames = getBeanNamesForType(requiredType);
      result = new LinkedHashMap<>(candidateNames.size());
      for (String candidateName : candidateNames) {
        result.put(candidateName, getBean(candidateName));
      }
    } catch (Exception e) {
      result = null;
    }

    return result;
  }
}
