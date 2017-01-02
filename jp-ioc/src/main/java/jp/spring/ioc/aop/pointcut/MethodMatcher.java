package jp.spring.ioc.aop.pointcut;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 12/26/2016.
 */
public interface MethodMatcher {

    boolean matches(Method method, Class<?> targetClass);
}
