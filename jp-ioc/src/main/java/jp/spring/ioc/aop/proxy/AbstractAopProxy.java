package jp.spring.ioc.aop.proxy;


import jp.spring.ioc.aop.AdviseSupport;
import jp.spring.ioc.aop.proxy.AopProxy;

/**
 * Created by Administrator on 12/27/2016.
 */
public abstract class AbstractAopProxy implements AopProxy {

    protected AdviseSupport adviseSupport;

    public AbstractAopProxy(AdviseSupport adviseSupport) {
        this.adviseSupport = adviseSupport;
    }
}
