package jp.spring.aop.impl;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 1/19/2017.
 */
public class JoinPointParameter {

    private Method method;

    private Object[] args;

    public JoinPointParameter() {}

    public JoinPointParameter(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
