package jp.spring.web.servlet;

/**
 * Created by Administrator on 1/3/2017.
 */
public class HandlerExecutionChain {

    private Object handler;

    public HandlerExecutionChain() {}


    public HandlerExecutionChain(Object handler) {
        this.handler = handler;
    }

    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }
}
