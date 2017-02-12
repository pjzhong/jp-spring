package jp.spring.process.transaction;

import jp.spring.aop.ClassFilter;
import jp.spring.aop.MethodMatcher;
import jp.spring.aop.Pointcut;
import jp.spring.ioc.stereotype.Service;
import jp.spring.ioc.util.JpUtils;
import jp.spring.orm.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2/12/2017.
 */
public class TransactionPointcut implements Pointcut {

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodFilter() {
        return this;
    }

    @Override
    public boolean match(Class<?> cls) {
        return JpUtils.isAnnotated(cls, Service.class);
    }

    @Override
    public boolean match(Method method) {
        return JpUtils.isAnnotated(method, Transactional.class);
    }
}
