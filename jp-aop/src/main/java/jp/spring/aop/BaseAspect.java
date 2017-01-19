package jp.spring.aop;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Created by Administrator on 1/18/2017.
 */
public abstract class BaseAspect implements MethodInterceptor, ClassFilter, MethodMatcher{

}
