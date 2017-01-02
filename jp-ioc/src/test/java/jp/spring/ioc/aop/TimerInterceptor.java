package jp.spring.ioc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by Administrator on 12/26/2016.
 */
public class TimerInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long time = System.nanoTime();
        System.out.println("Invocation of Method " + invocation.getMethod().getName() + " start");
        Object proceed = invocation.proceed();
        System.out.println("Invocation of method " + invocation.getMethod() + "end! takes " + (System.nanoTime() - time) + " nanoseconds");
        return proceed;
    }
}
