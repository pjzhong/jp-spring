package jp.spring.aop.impl;


import jp.spring.aop.BaseAspect;
import jp.spring.aop.Pointcut;
import jp.spring.aop.annotation.After;
import jp.spring.aop.annotation.Before;
import jp.spring.ioc.util.JpUtils;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 1/19/2017.
 */
public class ExecutionAspect extends BaseAspect {

    private Pointcut pointcut;

    private Object interceptor;

    private Method beforeMethod;

    private Method afterMethod;

    public ExecutionAspect(Class<?> aspectClass, Object aspectObject) {
        String expression = aspectClass.getAnnotation(jp.spring.aop.annotation.Pointcut.class).value();
        pointcut = new ExecutionPointcut(expression);

        interceptor = aspectObject;
        List<Method> methods = JpUtils.findMethods(aspectClass, Before.class);
        if(!JpUtils.isEmpty(methods)) {
            beforeMethod = methods.get(0);
        }

        methods = JpUtils.findMethods(aspectClass, After.class);
        if(!JpUtils.isEmpty(methods)) {
            afterMethod = methods.get(0);
        }
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        JoinPointParameter parameter = new JoinPointParameter(method, args);
        if(hasAfterMethod()) {
            beforeMethod.setAccessible(true);
            beforeMethod.invoke(interceptor, parameter);
        }

        method.setAccessible(true);
        Object result = method.invoke(obj, args);

        if(hasAfterMethod()) {
            afterMethod.setAccessible(true);
            afterMethod.invoke(interceptor, parameter);
        }

        return result;
    }

    private boolean hasBeforeMethod() {
        return beforeMethod != null;
    }

    private boolean hasAfterMethod() {
        return afterMethod != null;
    }


    @Override
    public boolean match(Class<?> cls) {
        return pointcut.match(cls);
    }

    @Override
    public boolean match(Method method) {
        return pointcut.match(method);
    }
}
