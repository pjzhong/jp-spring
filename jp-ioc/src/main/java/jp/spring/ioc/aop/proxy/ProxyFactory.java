package jp.spring.ioc.aop.proxy;

import jp.spring.ioc.aop.AdviseSupport;

/**
 * Created by Administrator on 12/27/2016.
 */
public class ProxyFactory extends AdviseSupport implements AopProxy {

    @Override
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    protected  final AopProxy createAopProxy() {
        return new CglibAopProxy(this);
    }
}
