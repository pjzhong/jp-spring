package jp.spring.aop;

import java.util.List;
import jp.spring.aop.support.ProxyChain;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;


/**
 * Created by Administrator on 1/20/2017.
 */
public class ProxyFactory {

  private static ProxyFactory instance = null;

  private ProxyFactory() {
  }

  public synchronized static ProxyFactory getInstance() {
    if (instance == null) {
      instance = new ProxyFactory();
    }

    return instance;
  }

  @SuppressWarnings("unchecked")
  public <T> T createProxy(final Object target, final List<Proxy> proxyList) {
    return (T) Enhancer.create(target.getClass(), target.getClass().getInterfaces(),
        (MethodInterceptor) (obj, method, args, proxy) -> {
          if (method.getDeclaringClass() == Object.class) {
            return method.invoke(target, args);
          } else {
            ProxyChain proxyChain = new ProxyChain(target, method, args, proxy, proxyList);
            return proxyChain.doProxyChain();
          }
        });
  }
}
