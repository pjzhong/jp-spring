package jp.spring.web.context;

import jp.spring.aop.BaseAspect;
import jp.spring.aop.helper.AspectHelper;
import jp.spring.aop.impl.AspectBeanPostProcessor;
import jp.spring.aop.impl.ExecutionAspectProxy;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.context.impl.ClassPathXmlApplicationContext;
import jp.spring.ioc.stereotype.Aspect;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.servlet.handler.UrlHandlerMapping;
import jp.spring.web.servlet.handler.UrlMapping;
import jp.spring.web.servlet.handler.UrlMappingBuilder;
import jp.spring.web.servlet.handler.impl.DefaultUrlHandlerMapping;
import jp.spring.web.servlet.handler.impl.DefaultUrlMappingBuilder;
import jp.spring.web.view.ViewResolver;

import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultXMLWebApplicationContext extends ClassPathXmlApplicationContext implements WebApplicationContext {

    public DefaultXMLWebApplicationContext(String location) throws Exception{
        super(location);
    }

    @Override
    protected void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
        super.loadBeanDefinitions(beanFactory);
        initControllers(beanFactory);
        initAspect(beanFactory);
    }

    private void initControllers(AbstractBeanFactory beanFactory) throws Exception {
        List<String> beanNames = beanFactory.getBeanNamByAnnotation(Controller.class);
        UrlMappingBuilder builder = new DefaultUrlMappingBuilder();
        DefaultUrlHandlerMapping urlHandlerMapping = new DefaultUrlHandlerMapping();

        List<UrlMapping> urlMappings;
        for(String beanName : beanNames) {
            urlMappings = builder.buildUrlMapping(beanName, beanFactory.getType(beanName));
            urlHandlerMapping.addUrlMappings(urlMappings);

        }

        BeanDefinition urlHandlerDefinition = new BeanDefinition();
        urlHandlerDefinition.setBeanClass(DefaultUrlHandlerMapping.class);
        urlHandlerDefinition.setBean(urlHandlerMapping);
        beanFactory.registerBeanDefinition(UrlHandlerMapping.URL_HANDLER_MAPPING, urlHandlerDefinition);
    }

    private void initAspect(AbstractBeanFactory beanFactory) throws Exception {
       try {
           AspectHelper.getInstance().initAspect(beanFactory);
       } catch (ClassNotFoundException e) {
           //User didn't import aop package , simply skip
       }
    }

    @Override
    public Object getBean(String name) throws Exception {

        if(name.equals(ViewResolver.RESOLVER_NAME)) {
           List<ViewResolver> resolvers =  beanFactory.getBeansForType(ViewResolver.class);
           if(!resolvers.isEmpty()) {
               return resolvers.get(0);
           }
        }

        return beanFactory.getBean(name);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }

}
