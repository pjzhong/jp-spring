package jp.spring.aop.support;

import java.lang.reflect.Method;
import java.util.List;
import jp.spring.aop.Proxy;
import net.sf.cglib.proxy.MethodProxy;

/**
 * inspired by smart-framework
 *
 *
 * link:https://git.oschina.net/huangyong/smart-framework
 */
public class ProxyChain {

  private List<Proxy> proxyList;
  private int currentProxyIndex = 0;

  private TargetSource target;
  private MethodProxy methodProxy;
  private Object methodResult;

  public ProxyChain(Object target, Method targetMethod, Object[] methodParams,
      MethodProxy methodProxy, List<Proxy> proxies) {
    this.target = new TargetSource(target, targetMethod, methodParams);
    this.methodProxy = methodProxy;
    this.proxyList = proxies;
  }

  public TargetSource getTarget() {
    return target;
  }

  public Object doProxyChain() {
    if (currentProxyIndex < proxyList.size()) {
      proxyList.get(currentProxyIndex++).doProxy(this);
    } else {
      try {
        methodResult = methodProxy
            .invoke(getTarget().getTargetObject(), getTarget().getMethodParams());
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }
    }

    return methodResult;
  }
}