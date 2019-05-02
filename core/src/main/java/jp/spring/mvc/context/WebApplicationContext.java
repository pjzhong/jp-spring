package jp.spring.mvc.context;

import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.impl.ClassPathPropertiesApplicationContext;

/**
 * Created by Administrator on 1/10/2017.
 */
public class WebApplicationContext extends ClassPathPropertiesApplicationContext {

  public WebApplicationContext(String location) throws Exception {
    super(location);
  }

  @Override
  public Object getBean(String name)  {
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
