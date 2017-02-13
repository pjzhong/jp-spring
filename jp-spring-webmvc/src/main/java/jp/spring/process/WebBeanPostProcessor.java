package jp.spring.process;

import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.beans.io.reader.AbstractBeanDefinitionReader;
import jp.spring.ioc.beans.io.reader.AnnotationBeanDefinitionReader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.web.annotation.Intercept;
import jp.spring.web.handler.*;
import jp.spring.web.handler.impl.DefaultHandlerInvoker;
import jp.spring.web.handler.impl.DefaultHandlerMapping;
import jp.spring.web.handler.impl.DefaultHandlerMappingBuilder;
import jp.spring.web.handler.impl.DefaultMultipartResolver;
import jp.spring.web.interceptor.InterceptMatch;
import jp.spring.web.interceptor.Interceptor;
import jp.spring.web.view.impl.DefaultViewResolver;
import jp.spring.web.view.ViewResolver;
import jp.spring.web.view.impl.FreemarkerResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/25/2017.
 */
@Component
public class WebBeanPostProcessor implements BeanPostProcessor ,BeanFactoryAware {

    private AbstractBeanFactory beanFactory;

    @Value("page.extension")
    private String pageExtension = "";

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if(beanFactory instanceof AbstractBeanFactory) {
            this.beanFactory = (AbstractBeanFactory) beanFactory;
        }
    }

    @Override
    public void postProcessBeforeInitialization() throws Exception {
        HandlerMapping handlerMapping = buildHandlerMapping(beanFactory);


        //目前因为ioc模块的类扫描器性能速度还没优化到最好，因此没开启对jp-spring的全项目扫描。
        // 这个手动注册虽然不好, 但在没想到其它方法之前，先这样吧......
        beanFactory.registerBeanDefinition(HandlerMapping.DEFAULT_HANDLER_MAPPING, new BeanDefinition(HandlerMapping.class, handlerMapping));
        beanFactory.registerBeanDefinition(HandlerInvoker.DEFAULT_HANDLER_INVOKER, new BeanDefinition(HandlerInvoker.class, new DefaultHandlerInvoker()));
        AbstractBeanDefinitionReader reader = AnnotationBeanDefinitionReader.getInstance();
        switch (pageExtension) {
            case "ftl":
            case ".ftl":beanFactory.registerBeanDefinition(ViewResolver.RESOLVER_NAME,  reader.loadBeanDefinition(FreemarkerResolver.class));break;
            default:beanFactory.registerBeanDefinition(ViewResolver.RESOLVER_NAME,  reader.loadBeanDefinition(DefaultViewResolver.class));
        }
        beanFactory.registerBeanDefinition(MultipartResolver.DEFAULT_MULTI_PART_RESOLVER, reader.loadBeanDefinition(DefaultMultipartResolver.class));
    }

    private HandlerMapping buildHandlerMapping(AbstractBeanFactory beanFactory) throws Exception{
        List<String> controllerNames = beanFactory.getBeanNamByAnnotation(Controller.class);

        DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping();
        if(!JpUtils.isEmpty(controllerNames)) {
            HandlerMappingBuilder builder = new DefaultHandlerMappingBuilder();
            List<Handler> handlers;
            for(String beanName : controllerNames) {
                handlers = builder.buildHandler(beanName, beanFactory.getType(beanName));
                handlerMapping.addHandlers(handlers);
            }
        }

        //Assembling interceptors
        List<InterceptMatch> interceptMatches = buildInterceptMatch(beanFactory);
        if(!JpUtils.isEmpty(interceptMatches)) {
            Map<String, List<Handler>> handlerMap = handlerMapping.getHandlerMap();
            for(String url : handlerMap.keySet()) {
                List<Interceptor> interceptors = new ArrayList<>();
                for(InterceptMatch interceptMatch : interceptMatches) {
                    if(interceptMatch.match(url)) {
                        interceptors.add(interceptMatch.getInterceptor());
                    }
                }

                if(!JpUtils.isEmpty(interceptors)) {
                    List<Handler> handlers = handlerMap.get(url);
                    for(Handler handler : handlers) {
                        handler.addInterceptors(interceptors);
                    }
                }
            }
        }

        return  handlerMapping;
    }

    /**
     * 为了每一个interceptor创建一个匹配器
     * */
    private List<InterceptMatch> buildInterceptMatch(AbstractBeanFactory beanFactory) throws Exception {
        List<String> interceptorNames = beanFactory.getBeanNamByAnnotation(Intercept.class);
        List<InterceptMatch> interceptors = null;
        if(!JpUtils.isEmpty(interceptorNames)) {
            interceptors = new ArrayList<InterceptMatch>();
            InterceptMatch interceptMatch;
            Interceptor interceptor ;
            String expression = null;
            for(String name : interceptorNames) {
                interceptor = (Interceptor) beanFactory.getBean(name);
                expression = beanFactory.getType(name).getAnnotation(Intercept.class).url();
                interceptMatch = new InterceptMatch(interceptor, expression);
                interceptors.add(interceptMatch);
            }
        }

        return interceptors;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
