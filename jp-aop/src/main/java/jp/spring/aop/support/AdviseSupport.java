package jp.spring.aop.support;

import jp.spring.aop.BaseAspect;
import jp.spring.aop.Pointcut;
import jp.spring.aop.support.TargetSource;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Created by Administrator on 1/19/2017.
 */
public class AdviseSupport {

    private TargetSource targetSource;

    private MethodInterceptor interceptor;

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }
}
