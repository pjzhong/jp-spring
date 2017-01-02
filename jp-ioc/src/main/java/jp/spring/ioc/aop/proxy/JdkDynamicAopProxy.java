package jp.spring.ioc.aop.proxy;

import jp.spring.ioc.aop.AdviseSupport;
import jp.spring.ioc.aop.ReflectiveMethodInvocation;
import jp.spring.ioc.aop.proxy.AopProxy;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于jdk的动态代理
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler{

    private AdviseSupport advised;

    public JdkDynamicAopProxy(AdviseSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(getClass().getClassLoader(),  advised.getTargetSource().getInterfaces(), this);
    }

    @Override
    public Object invoke(final  Object proxy, final Method method, final Object[] args) throws Throwable {
        MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
        if(advised.getMethodInterceptor() != null && advised.getMethodMatcher().matches(method, advised.getTargetSource().getClass()) ) {
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
        } else {
            return method.invoke(advised.getTargetSource().getTarget(), args);
        }
    }
}
