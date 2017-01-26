package jp.spring.process;

import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.io.reader.AnnotationBeanDefinitionReader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.handler.HandlerMapping;
import jp.spring.web.handler.HandlerMappingBuilder;
import jp.spring.web.handler.impl.DefaultHandlerInvoker;
import jp.spring.web.handler.impl.DefaultHandlerMapping;
import jp.spring.web.handler.impl.DefaultHandlerMappingBuilder;
import jp.spring.web.view.DefaultViewResolver;
import jp.spring.web.view.ViewResolver;

import java.util.List;

/**
 * Created by Administrator on 1/25/2017.
 */
@Component
public class WebBeanPostProcessor implements BeanPostProcessor ,BeanFactoryAware {

    private AbstractBeanFactory beanFactory;

    public WebBeanPostProcessor() {
        System.out.println("I am webBeanPostProcessor");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if(beanFactory instanceof AbstractBeanFactory) {
            this.beanFactory = (AbstractBeanFactory) beanFactory;
        }
    }

    @Override
    public void postProcessBeforeInitialization() throws Exception {
        List<String> beanNames = beanFactory.getBeanNamByAnnotation(Controller.class);
        HandlerMappingBuilder builder = new DefaultHandlerMappingBuilder();
        DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping();

        List<Handler> handlers;
        for(String beanName : beanNames) {
            handlers = builder.buildHandler(beanName, beanFactory.getType(beanName));
            handlerMapping.addHandlers(handlers);
        }

        //注册 handlerMapping 和 handlerInvoker到 factory 里面去。 ！！！这个手动注册虽然不好, 但在没想到其它方法之前，先这样吧......
        BeanDefinition definition = new BeanDefinition();
        definition.setBeanClass(HandlerMapping.class);
        definition.setBean(handlerMapping);
        beanFactory.registerBeanDefinition(HandlerMapping.DEFAULT_HANDLER_MAPPING, definition);

        definition = new BeanDefinition();
        definition.setBeanClass(HandlerInvoker.class);
        definition.setBean(new DefaultHandlerInvoker());
        beanFactory.registerBeanDefinition(HandlerInvoker.DEFAULT_HANDLER_INVOKER, definition);

        definition = AnnotationBeanDefinitionReader.getInstance().loadBeanDefinition(DefaultViewResolver.class);
        beanFactory.registerBeanDefinition(ViewResolver.RESOLVER_NAME, definition);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
