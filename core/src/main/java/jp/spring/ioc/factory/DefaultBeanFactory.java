package jp.spring.ioc.factory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.util.TypeUtil;
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

  private Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();

  private List<BeanPostProcessor> beanPostProcessors = Collections.emptyList();

  private Properties properties = new Properties();

  public DefaultBeanFactory() {

  }

  public void refresh() {
    try {
      //TODO register known dependency, like factory and application context
      // let application context do this job
      Class<?> clazz = this.getClass();
      BeanDefinition selfDef = new BeanDefinition(clazz, this);
      registerBeanDefinition(StringUtils.uncapitalize(clazz.getSimpleName()), selfDef);

      beforeInitializeBean();
    } catch (Exception e) {
      throw new RuntimeException("Error Raised when refresh factory", e);
    }
  }

  @Override
  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
    if (beanDefinitions.containsKey(name)) {
      throw new IllegalArgumentException("Bean name " + name + " must be unique");
    }
    beanDefinitions.put(name, beanDefinition);
  }

  @Override
  public Object getBean(String name) {
    BeanDefinition beanDefinition = beanDefinitions.get(name);
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

  @Override
  public <T> T getBean(Class<T> requiredType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void registerDependency(Class<?> dependencyType, Object autowiredValue) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  private void beforeInitializeBean() throws Exception {
    List<BeanPostProcessor> processors = getBeansByType(BeanPostProcessor.class);
    beanPostProcessors = processors;
    for (BeanPostProcessor beanPostProcessor : processors) {
      beanPostProcessor.postProcessBeforeInitialization();
    }
  }

  @Deprecated
  private Object afterInitializeBean(Object bean, String name) {
    for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
      bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
    }

    return bean;
  }

  //various getters
  public Class<?> getType(String name) {
    BeanDefinition definition = beanDefinitions.get(name);
    return definition != null ? definition.getClazz() : null;
  }

  public <A> List<A> getBeansByType(Class<A> type) {
    List<A> beans = new ArrayList<>();
    for (String beanDefinitionName : beanDefinitions.keySet()) {
      if (type.isAssignableFrom(beanDefinitions.get(beanDefinitionName).getClazz())) {
        beans.add((A) getBean(beanDefinitionName));
      }
    }

    return beans;
  }

  private List<String> getBeanNamesForType(Class<?> targetType) {
    List<String> result = new ArrayList<>();

    boolean matchFound;
    for (String beanName : beanDefinitions.keySet()) {
      BeanDefinition beanDefinition = beanDefinitions.get(beanName);
      matchFound = targetType.isAssignableFrom(beanDefinition.getClazz());
      if (matchFound) {
        result.add(beanName);
      }
    }

    return result;
  }

  public List<String> getBeanNamByAnnotation(Class<? extends Annotation> annotation) {
    List<String> result = new ArrayList<>();
    BeanDefinition beanDefinition;
    for (String beanName : beanDefinitions.keySet()) {
      beanDefinition = beanDefinitions.get(beanName);
      if (TypeUtil.isAnnotated(beanDefinition.getClazz(), annotation)) {
        result.add(beanName);
      }
    }

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
            beanDefinition.getClassName()));
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
