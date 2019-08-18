package jp.spring.aop;

import jp.spring.aop.support.ProxyChain;

/**
 * Created by Administrator on 1/19/2017.
 */
public interface Proxy {

  void doProxy(ProxyChain proxyChain);
}
