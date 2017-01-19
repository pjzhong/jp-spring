package jp.spring.aop.impl;

import jp.spring.aop.support.AdviseSupport;
import jp.spring.aop.Proxy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 1/19/2017.
 */
public class CglibAopProxy implements Proxy, MethodInterceptor {

    //如果不用AdviseSupport封装一下，那么由BeanFactory生成的对象会在代理的途中被替换掉
    //所以才需要在封装多一层。
    private AdviseSupport support;

    public CglibAopProxy(AdviseSupport support) {
        this.support = support;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(support.getTargetSource().getTargetClass());
        enhancer.setInterfaces(support.getTargetSource().getInterfaces());
        enhancer.setCallback(this);
        Object enhanced = enhancer.create();
        return enhanced;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return support.getInterceptor().intercept(support.getTargetSource().getTarget(), method, args, proxy);
    }


}
