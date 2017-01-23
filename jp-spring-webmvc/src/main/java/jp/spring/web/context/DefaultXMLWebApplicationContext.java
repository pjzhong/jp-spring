package jp.spring.web.context;


import jp.spring.aop.helper.AspectHelper;

import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.context.impl.ClassPathXmlApplicationContext;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.handler.HandlerMapping;
import jp.spring.web.handler.HandlerMappingBuilder;
import jp.spring.web.handler.impl.DefaultHandlerMapping;

import jp.spring.web.handler.impl.DefaultHandlerMappingBuilder;
import jp.spring.web.handler.impl.DefaultHanlderInvoker;
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
        HandlerMappingBuilder builder = new DefaultHandlerMappingBuilder();
        DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping();

        List<Handler> handlers;
        for(String beanName : beanNames) {
            handlers = builder.buildHandler(beanName, beanFactory.getType(beanName));
            handlerMapping.addHandlers(handlers);
        }

        //注册 handlerMapping 和 handlerInvoker到 factory 里面去。 ！！！这个手动注册虽然不好, 但在没想到其它方法之前，先这样吧......
        BeanDefinition handlerDefinition = new BeanDefinition();
        handlerDefinition.setBeanClass(HandlerMapping.class);
        handlerDefinition.setBean(handlerMapping);
        beanFactory.registerBeanDefinition(HandlerMapping.DEFAULT_HANDLER_MAPPING, handlerDefinition);

        BeanDefinition invokerDefinition = new BeanDefinition();
        invokerDefinition.setBeanClass(HandlerInvoker.class);
        invokerDefinition.setBean(new DefaultHanlderInvoker());
        beanFactory.registerBeanDefinition(HandlerInvoker.DEFAULT_HANDLER_INVOKER, invokerDefinition);
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
