package jp.spring.aop;

/**
 * Created by Administrator on 1/18/2017.
 */
public interface Pointcut extends ClassFilter, MethodMatcher {

  ClassFilter getClassFilter();

  MethodMatcher getMethodFilter();
}
