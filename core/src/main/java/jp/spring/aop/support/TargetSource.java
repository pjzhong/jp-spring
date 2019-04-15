package jp.spring.aop.support;

import java.lang.reflect.Method;

/**
 * 用于AOP实现，此类封装了被代理的原对象的基本信息
 */
public class TargetSource {

    private Class<?> targetClass;

    private Class<?>[] interfaces;

    private Object targetObject;

    private Method targetMethod;

    private Object[] methodParams;

    public TargetSource(Object target, Method targetMethod, Object[] methodParams) {
        this.targetObject = target;
        this.targetClass = target.getClass();
        this.interfaces = target.getClass().getInterfaces();
        this.targetMethod = targetMethod;
        this.methodParams = methodParams;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }
}
