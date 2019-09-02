package jp.spring.ioc.factory;

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
