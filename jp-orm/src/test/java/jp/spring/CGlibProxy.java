package jp.spring;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 1/17/2017.
 */
public class CGlibProxy implements MethodInterceptor {

    private static final CGlibProxy instance = new CGlibProxy();

    public static CGlibProxy getInstance() {
        return  instance;
    }

    public <T> T getProxy(Class<T> cls) {
        return (T) Enhancer.create(cls, this);
    }


    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("asdfasdfasdf");
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("asdfasdfasdf");
        return result;
    }
}
