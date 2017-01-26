package jp.spring.web.context;

import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.context.impl.ClassPathPropertiesApplicationContext;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultWebApplicationContext extends ClassPathPropertiesApplicationContext implements WebApplicationContext {

    public DefaultWebApplicationContext(String location) throws Exception{
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
