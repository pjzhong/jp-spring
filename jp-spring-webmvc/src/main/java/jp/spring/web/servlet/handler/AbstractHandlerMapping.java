package jp.spring.web.servlet.handler;

import jp.spring.ioc.context.ApplicationContext;
import jp.spring.ioc.context.ApplicationContextAware;
import jp.spring.web.servlet.HandlerExecutionChain;
import jp.spring.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 1/3/2017.
 */
public abstract class AbstractHandlerMapping  implements HandlerMapping, ApplicationContextAware {

    private Object defaultHandler;

    private ApplicationContext applicationContext;

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = getHandlerInternal(request);
        if(handler == null) {
            handler = getDefaultHandler();
        }
        if(handler == null) {
            return null;
        }

        if(handler instanceof String) {
            String handlerName = (String) handler;
            handler = getApplicationContext().getBean(handlerName);
        }

        return new  HandlerExecutionChain();
    }

    protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;


    public Object getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(Object defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
