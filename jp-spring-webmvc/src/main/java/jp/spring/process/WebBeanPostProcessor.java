package jp.spring.process;

import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.io.reader.AnnotationBeanDefinitionReader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.web.annotation.Intercept;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.handler.HandlerMapping;
import jp.spring.web.handler.HandlerMappingBuilder;
import jp.spring.web.handler.impl.DefaultHandlerInvoker;
import jp.spring.web.handler.impl.DefaultHandlerMapping;
import jp.spring.web.handler.impl.DefaultHandlerMappingBuilder;
import jp.spring.web.interceptor.InterceptMatch;
import jp.spring.web.interceptor.Interceptor;
import jp.spring.web.view.DefaultViewResolver;
import jp.spring.web.view.ViewResolver;

import java.util.ArrayList;
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
        HandlerMapping handlerMapping = buildHandlerMapping(beanFactory);
        //注册 handlerMapping, handlerInvoker和 viewResolver factory 里面去。 ！！！这个手动注册虽然不好, 但在没想到其它方法之前，先这样吧......
        beanFactory.registerBeanDefinition(HandlerMapping.DEFAULT_HANDLER_MAPPING, new BeanDefinition(HandlerMapping.class, handlerMapping));
        beanFactory.registerBeanDefinition(HandlerInvoker.DEFAULT_HANDLER_INVOKER, new BeanDefinition(HandlerInvoker.class, new DefaultHandlerInvoker()));
        beanFactory.registerBeanDefinition(ViewResolver.RESOLVER_NAME,  AnnotationBeanDefinitionReader.getInstance().loadBeanDefinition(DefaultViewResolver.class));
    }

    private HandlerMapping buildHandlerMapping(AbstractBeanFactory beanFactory) throws Exception{
        List<InterceptMatch> interceptMatches = buildInterceptMatch(beanFactory);
        List<String> controllerNames = beanFactory.getBeanNamByAnnotation(Controller.class);

        DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping();
        if(!JpUtils.isEmpty(controllerNames)) {
            HandlerMappingBuilder builder = new DefaultHandlerMappingBuilder();

            List<Handler> handlers;
            for(String beanName : controllerNames) {
                handlers = builder.buildHandler(beanName, beanFactory.getType(beanName));

                if(!JpUtils.isEmpty(interceptMatches)) {
                    for(Handler handler : handlers) {//分配interceptors到目标handler
                        List<Interceptor> interceptors = new ArrayList<>();
                        for(InterceptMatch interceptMatch : interceptMatches) {
                            if(interceptMatch.match(handler.getUrl())) {
                                interceptors.add(interceptMatch.getInterceptor());
                            }
                        }
                        handler.setInterceptors(interceptors);
                    }
                }

                handlerMapping.addHandlers(handlers);
            }

        }

        return  handlerMapping;
    }

    private List<InterceptMatch> buildInterceptMatch(AbstractBeanFactory beanFactory) throws Exception {
        List<String> interceptorNames = beanFactory.getBeanNamByAnnotation(Intercept.class);
        List<InterceptMatch> interceptors = null;
        if(!JpUtils.isEmpty(interceptorNames)) {
            interceptors = new ArrayList<InterceptMatch>();
            InterceptMatch handleIntercept;
            for(String name : interceptorNames) {
                Interceptor interceptor = (Interceptor) beanFactory.getBean(name);
                handleIntercept = new InterceptMatch(interceptor.getClass(), interceptor);
                interceptors.add(handleIntercept);
            }
        }

        return interceptors;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
