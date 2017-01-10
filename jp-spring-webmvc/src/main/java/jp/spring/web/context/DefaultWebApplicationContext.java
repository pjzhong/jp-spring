package jp.spring.web.context;

import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.context.impl.ClassPathXmlApplicationContext;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.servlet.handler.UrlMapping;
import jp.spring.web.servlet.handler.UrlMappingBuilder;
import jp.spring.web.servlet.handler.impl.DefaultUrlHandlerMapping;
import jp.spring.web.servlet.handler.impl.DefaultUrlMappingBuilder;

import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultWebApplicationContext extends ClassPathXmlApplicationContext implements WebApplicationContext {

    public DefaultWebApplicationContext(String location) throws Exception{
        super(location);
    }

    @Override
    protected void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
        super.loadBeanDefinitions(beanFactory);

        List<String> beanNames = beanFactory.getBeanNamByAnnotation(Controller.class);
        UrlMappingBuilder builder = new DefaultUrlMappingBuilder();
        DefaultUrlHandlerMapping urlHanlderMapping = new DefaultUrlHandlerMapping();

        UrlMapping urlMapping;
        for(String beanName : beanNames) {
            urlMapping = builder.buildUrlMapping(beanName, beanFactory.getType(beanName));
        }

    }

    @Override
    public Object getBean(String name) throws Exception {
        return beanFactory.getBean(name);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }

}
