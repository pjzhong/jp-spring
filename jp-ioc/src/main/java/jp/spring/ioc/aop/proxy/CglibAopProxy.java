package jp.spring.ioc.aop.proxy;

import jp.spring.ioc.aop.AdviseSupport;
import jp.spring.ioc.aop.ReflectiveMethodInvocation;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 12/27/2016.
 */
public class CglibAopProxy extends AbstractAopProxy {

    public CglibAopProxy(AdviseSupport adviseSupport) {
        super(adviseSupport);
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(adviseSupport.getTargetSource().getTargetClass());
        enhancer.setInterfaces(adviseSupport.getTargetSource().getInterfaces());
        enhancer.setCallback(new DynamicAdvisedInterceptor(adviseSupport));
        Object enhanced = enhancer.create();
        return enhanced;
    }

    private static class DynamicAdvisedInterceptor implements net.sf.cglib.proxy.MethodInterceptor {

        private AdviseSupport advised;

        private MethodInterceptor delegateMethodInterceptor;

        private DynamicAdvisedInterceptor(AdviseSupport advised) {
            this.advised = advised;
            this.delegateMethodInterceptor = advised.getMethodInterceptor();
        }


        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if(advised.getMethodMatcher() == null
                    || advised.getMethodMatcher().matches(method, advised.getTargetSource().getTargetClass())) {
                return delegateMethodInterceptor.invoke(new CglibMethodInvocation(advised.getTargetSource().getTarget(), method, args, methodProxy));
            }

            //如果不符合，就简单的执行java反射.............
            return new CglibMethodInvocation(advised.getTargetSource().getTarget(), method, args, methodProxy).proceed();
        }
    }

    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] args, MethodProxy methodProxy) {
            super(target, method, args);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return this.methodProxy.invoke(this.getTarget(), this.getArgs());
        }
    }
}
