package jp.spring.aop;

import jp.spring.aop.support.ProxyChain;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;


/**
 * Created by Administrator on 1/20/2017.
 */
public class ProxyFactory {

    private static ProxyFactory instance = null;

    private ProxyFactory() {
    }

    public synchronized static ProxyFactory getInstance() {
        if(instance == null) {
            instance = new ProxyFactory();
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(final Object target, final List<Proxy> proxyList) {
        return (T) Enhancer.create(target.getClass(), target.getClass().getInterfaces(), new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                ProxyChain proxyChain = new ProxyChain(target, method, args, proxy, proxyList);
                return  proxyChain.doProxyChain();
            }
        });
    }
}
