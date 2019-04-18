package jp.spring.mvc.context;

import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.impl.ClassPathPropertiesApplicationContext;

/**
 * Created by Administrator on 1/10/2017.
 */
public class WebApplicationContext extends ClassPathPropertiesApplicationContext {

  /**
   * Context attribute to bind root WebApplicationContext to on successful startup.
   * <p>Note: If the startup of the root context fails, this attribute can contain
   * an exception or error as value. Use WebApplicationContextUtils for convenient lookup of the
   * root WebApplicationContext.
   */
  public static String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE =
      WebApplicationContext.class.getName() + ".ROOT";

  public WebApplicationContext(String location) throws Exception {
    super(location);
  }

  @Override
  public Object getBean(String name) throws Exception {
    return beanFactory.getBean(name);
  }

  @Override
  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
    beanFactory.registerBeanDefinition(name, beanDefinition);
  }

  public String getProperty(String key) {
    return beanFactory.getProperties().getProperty(key);
  }

}
