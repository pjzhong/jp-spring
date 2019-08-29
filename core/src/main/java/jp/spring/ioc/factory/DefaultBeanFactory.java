package jp.spring.ioc.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import jp.spring.ioc.BeansException;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.ArrayUtils;
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

  private Map<String, BeanDefinition> definitions = new ConcurrentHashMap<>();
  private Map<String, Object> beans = new ConcurrentHashMap<>();
  private Map<String, Object> earlyBeans = new ConcurrentHashMap<>();
  private Map<String, Supplier<?>> beanFactories = new ConcurrentHashMap<>();

  private List<BeanPostProcessor> beanPostProcessors = Collections.emptyList();

  private Properties properties = new Properties();

  public DefaultBeanFactory() {
  }

  public void refresh() {
    try {
      registerDependency(this.getClass(), this);

      beforeInitializeBean();
    } catch (Exception e) {
      throw new RuntimeException("Error Raised when refresh factory", e);
    }
  }

  @Override
  public void registerBeanDefinition(BeanDefinition definition) {
    boolean dup = definitions.putIfAbsent(definition.getName(), definition) != null;
    if (dup) {
      throw new IllegalArgumentException("Bean name " + definition.getName() + " must be unique");
    }
  }

  @Override
  public Object getBean(String name) {
    if (StringUtils.isBlank(name)) {
      throw new BeansException("Bean Name Can't not be blank");
    }

    Object res = beans.get(name);
    if (res != null) {
      return res;
    }

    BeanDefinition definition = definitions.get(name);
    if (definition == null) {
      throw new IllegalArgumentException("No bean named " + name + " is defined");
    }
    res = definition.getBean();
    if (res == null) {
      synchronized (definition) {
        Object bean = doGetBean(definition);
        bean = afterInitializeBean(bean, name);
        res = bean;
      }
    }
    return res;
  }

  @Override
  public <T> T getBean(Class<T> requiredType) {
    throw new UnsupportedOperationException();
  }

  private Object doGetBean(BeanDefinition definition) {
    Object bean = null;
    try {
      bean = definition.getClazz().newInstance();
      //TODO IT SOLVED CIRCLE DEPENDENCY, But Also public not-full-construct object to outside
      beans.put(definition.getName(), bean);

      resolveDependency(bean, definition);
      injectPropertyValue(bean, definition);
      postConstruct(bean, definition);
    } catch (Exception e) {
      throw new BeansException(e);
    }
    return bean;
  }

  private <T> T getSinglton(String name) {
    Object obj = beans.get(name);
    if (obj == null) {
      synchronized (this.beans) {
        obj = earlyBeans.get(name);
        if (obj == null) {
          Supplier<?> factory = beanFactories.get(name);
          if (factory != null) {
            obj = factory.get();
            earlyBeans.put(name, obj);
            beanFactories.remove(name);
          }
        }
      }
    }

    return (T) obj;
  }

  private void resolveDependency(Object bean, BeanDefinition beanDefinition) throws Exception {
    /* 两种情况:
     * 1.没有@Qualifier, 那么根据类型来获取注入对象。多个取第一个
     * 2.用户添加了@Qualifier, 使用@Qualifier的值来获取注入对象
     */

    for (InjectField injectField : beanDefinition.getInjectFields()) {
      Object value = null;
      if (StringUtils.isNotBlank(injectField.getQualifier())) {
        value = getBean(injectField.getQualifier());
      } else {
        Class<?> type = injectField.getType();
        String name = definitions.values().stream()
            .filter(def -> type.isAssignableFrom(def.getClazz()))
            .map(BeanDefinition::getName)
            .findFirst().orElse(null);
        if (StringUtils.isNotBlank(name)) {
          value = getBean(name);
        }
      }

      if (value == null && injectField.isRequired()) {
        throw new BeansException(String
            .format("Inject %s to %s failed", injectField.getType(),
                beanDefinition.getClazz()));
      }

      injectField.inject(bean, value);
    }
  }

  /**
   * Handle value annotation
   *
   * @param bean the object
   * @param definition the object configuration
   * @since 2019年06月16日 14:35:57
   */
  private void injectPropertyValue(Object bean, BeanDefinition definition) throws Exception {

    for (PropertyValue propertyValue : definition.getPropertyValues()) {
      Object value = null;
      String strValue = getProperties().getProperty(propertyValue.getName());
      if ((strValue != null) && TypeUtil.isSimpleType(propertyValue.getField().getType())) {
        value = TypeUtil.convertToSimpleType(strValue, propertyValue.getField().getType());
      }

      if (value == null && propertyValue.isRequired()) {
        throw new BeansException(String.format("Inject %s to %s failed", propertyValue.getName(),
            definition.getClassName()));
      }

      if (value != null) {
        propertyValue.inject(bean, value);
      }
    }
  }

  private void postConstruct(Object bean, BeanDefinition definition)
      throws InvocationTargetException, IllegalAccessException {
    Method post = definition.getPost();
    if (post != null) {
      post.invoke(bean, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }
  }

  @Override
  public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void registerDependency(Class<?> dependencyType, Object autowiredValue) {
    String name = StringUtils.uncapitalize(dependencyType.getSimpleName());
    boolean dup = beans.putIfAbsent(name, autowiredValue) != null;
    if (dup) {
      throw new IllegalArgumentException("Bean name " + name + " must be unique");
    }
    BeanDefinition definition = new BeanDefinition(name, dependencyType, autowiredValue);
    definitions.put(definition.getName(), definition);
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
    BeanDefinition definition = definitions.get(name);
    return definition != null ? definition.getClazz() : null;
  }

  public <A> List<A> getBeansByType(Class<A> type) {
    List<Object> beans = new ArrayList<>();
    for (BeanDefinition definition : definitions.values()) {
      if (type.isAssignableFrom(definition.getClazz())) {
        beans.add(getBean(definition.getName()));
      }
    }

    return (List<A>) beans;
  }

  public List<String> getBeanNamByAnnotation(Class<? extends Annotation> annotation) {
    List<String> result = new ArrayList<>();
    BeanDefinition beanDefinition;
    for (String beanName : definitions.keySet()) {
      beanDefinition = definitions.get(beanName);
      if (TypeUtil.isAnnotated(beanDefinition.getClazz(), annotation)) {
        result.add(beanName);
      }
    }

    return result;
  }

  public Properties getProperties() {
    return properties;
  }
}
