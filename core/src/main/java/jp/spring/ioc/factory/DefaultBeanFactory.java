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
import java.util.stream.Collectors;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.NoUniqueBeansException;
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

  private final Map<String, Object> beans = new ConcurrentHashMap<>();
  private final Map<String, Object> earlyBeans = new ConcurrentHashMap<>();
  private final Map<String, Supplier<?>> beanFactories = new ConcurrentHashMap<>();
  private final Map<String, BeanDefinition> definitions = new ConcurrentHashMap<>();

  private List<BeanPostProcessor> beanPostProcessors = Collections.emptyList();

  private Properties properties = new Properties();

  public DefaultBeanFactory() {
  }

  public void refresh() {
    try {
      registerDependency(TypeUtil.simpleClassName(this.getClass()), this);

      beforeInitializeBean();
    } catch (Exception e) {
      throw new RuntimeException("Error Raised when refresh factory", e);
    }
  }


  @Override
  public Object getBean(String name) {
    if (StringUtils.isBlank(name)) {
      throw new BeansException("Bean Name Can't not be blank");
    }
    return doGetBean(name);
  }

  @Override
  public void registerDependency(String name, Object object) {
    synchronized (this.beans) {
      Object oldObject = this.beans.get(name);
      if (oldObject != null && definitions.containsKey(name)) {
        throw new IllegalArgumentException("Could not register object [" + object +
            "] under bean name '" + name + "': because it's already registers");
      }
      addSingleton(name, object);
      definitions.put(name, new BeanDefinition(name, object.getClass()));
    }
  }

  private Object doGetBean(String name) {
    Object bean;
    bean = getSingleton(name);
    if (bean == null) {
      BeanDefinition definition = definitions.get(name);
      if (definition == null) {
        throw new BeansException("No bean " + name + " is defined");
      }

      bean = getSingleton(name, () -> createBean(definition));
    }
    return bean;
  }

  private Object getSingleton(String name) {
    Object obj = beans.get(name);
    if (obj == null) {
      synchronized (this.beans) {
        obj = earlyBeans.get(name);
        if (obj == null) {
          Supplier<?> factory = beanFactories.get(name);
          if (factory != null) {
            obj = factory.get();
            beanFactories.remove(name);
          }
        }
      }
    }

    return obj;
  }

  private Object getSingleton(String name, Supplier<?> factory) {
    synchronized (this.beans) {
      Object singletonObject = this.beans.get(name);
      if (singletonObject == null) {
        try {
          singletonObject = factory.get();
          addSingleton(name, singletonObject);
        } catch (Exception e) {
          singletonObject = this.beans.get(name);
          if (singletonObject == null) {
            throw e;
          }
        }
      }
      return singletonObject;
    }
  }

  private void addSingleton(String name, Object singleton) {
    synchronized (this.beans) {
      this.beans.put(name, singleton);
      this.earlyBeans.remove(name);
      this.beanFactories.remove(name);
    }
  }

  private Object createBean(BeanDefinition definition) throws BeansException {
    Object bean = instantBean(definition);

    resolveDependency(bean, definition);
    injectPropertyValue(bean, definition);
    postConstruct(bean, definition);
    bean = afterInitializeBean(bean, definition.getName());
    return bean;
  }

  private Object instantBean(BeanDefinition definition) {
    Object obj = beans.get(definition.getName());
    if (obj == null) {
      synchronized (this.beans) {
        try {
          obj = definition.getClazz().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
        this.earlyBeans.put(definition.getName(), obj);
        this.beans.remove(definition.getName());
        this.beanFactories.remove(definition.getName());
      }
    }
    return obj;
  }

  private void resolveDependency(Object bean, BeanDefinition beanDefinition)
      throws RuntimeException {
    for (InjectField injectField : beanDefinition.getInjectFields()) {
      Object value;
      if (StringUtils.isNotBlank(injectField.getQualifier())) {
        value = getBean(injectField.getQualifier());
      } else {
        Class<?> type = injectField.getType();
        List<String> names = definitions.values().stream()
            .filter(def -> type.isAssignableFrom(def.getClazz()))
            .map(BeanDefinition::getName)
            .collect(Collectors.toList());

        if (names.isEmpty() && injectField.isRequired()) {
          throw new BeansException(String
              .format("Inject %s to %s failed, no such bean exists", injectField.getType(),
                  beanDefinition.getClazz()));
        }

        if (names.size() > 1) {
          throw new NoUniqueBeansException(
              String.format("type:%s is not Unique, found:%s", injectField.getType(), names));
        }

        value = getBean(names.get(0));
      }

      try {
        injectField.inject(bean, value);
      } catch (Exception e) {
        throw new BeansException(e);
      }
    }
  }

  /**
   * Handle value annotation
   *
   * @param bean the object
   * @param definition the object configuration
   * @since 2019年06月16日 14:35:57
   */
  private void injectPropertyValue(Object bean, BeanDefinition definition) throws RuntimeException {

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
        try {
          propertyValue.inject(bean, value);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private void postConstruct(Object bean, BeanDefinition definition) {
    Method post = definition.getPost();
    if (post != null) {
      try {
        post.invoke(bean, ArrayUtils.EMPTY_OBJECT_ARRAY);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }


  @Override
  public void registerBeanDefinition(BeanDefinition definition) {
    boolean dup = definitions.putIfAbsent(definition.getName(), definition) != null;
    if (dup) {
      throw new IllegalArgumentException("Bean name [" + definition.getName() + "] is duplicated");
    }
  }

  @Deprecated
  private void beforeInitializeBean() throws Exception {
    List<BeanPostProcessor> processors = getBeansByType(BeanPostProcessor.class);
    beanPostProcessors = processors;
    for (BeanPostProcessor beanPostProcessor : processors) {
      beanPostProcessor.postProcessBeforeInitialization();
    }
  }

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

  public List<String> getNamesByAnnotation(Class<? extends Annotation> annotation) {
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
