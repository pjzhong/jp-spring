package jp.spring.aop;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 1/18/2017.
 */
public interface MethodMatcher {

  boolean match(Method method);
}
