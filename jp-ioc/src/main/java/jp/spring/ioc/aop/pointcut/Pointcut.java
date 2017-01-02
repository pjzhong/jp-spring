package jp.spring.ioc.aop.pointcut;

/**
 * Created by Administrator on 12/26/2016.
 */
public interface Pointcut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
