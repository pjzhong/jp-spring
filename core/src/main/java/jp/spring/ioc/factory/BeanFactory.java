package jp.spring.ioc.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * The Root Factory Interface
 *
 * @since 2019年06月08日 21:25:10
 **/
public interface BeanFactory {

  /**
   * Return an instance, which may be shared or independent, of the specified bean.
   *
   * @param name name - the name of the bean to retrieve
   * @return an instance of the bean
   * @since 2019年06月08日 21:55:23
   */
  Object getBean(String name);

  /**
   * Return the bean instance that uniquely matches the given object type, if any
   *
   * @param requiredType type the bean must match; can be an interface or superclass
   * @return an instance of the single bean matching the required type
   * @since 2019年06月08日 21:49:30
   */
  <T> T getBean(Class<T> requiredType);

  /**
   * Find all beans which are annotated with the supplied Annotation type, returning a Map of bean
   * names with corresponding bean instances.
   *
   * @param annotationType the type of annotation to look for
   * @return Returns: a Map with the matching beans, containing the bean names as keys and the
   * corresponding bean instances as values
   * @since 2019年06月08日 21:36:44
   */
  Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType);

  /**
   * Register a special dependency type with corresponding autowired value.
   *
   * @param dependencyType the dependency type to register.
   * @param autowiredValue the corresponding autowired value.
   * @since 2019年06月09日 10:04:03
   */
  void registerDependency(Class<?> dependencyType, Object autowiredValue);

  /**
   * Register a special dependency type with corresponding  definition
   *
   * @param definition the bean definition
   * @since 2019年06月09日 10:04:03
   */
  void registerBeanDefinition(BeanDefinition definition) throws Exception;


  /**
   * Register a special dependency bean with corresponding  definition
   *
   * @param definition the bean definition
   * @since 2019年06月09日 10:04:03
   */
  void registerBeanDefinition(BeanDefinition definition, Object bean) throws Exception;
}
