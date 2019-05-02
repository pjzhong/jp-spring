package com.jp.aop;

import java.lang.reflect.Method;
import jp.spring.aop.annotation.After;
import jp.spring.aop.annotation.Before;
import jp.spring.aop.annotation.Pointcut;
import jp.spring.aop.support.TargetSource;
import jp.spring.ioc.stereotype.Aspect;

/**
 * Created by Administrator on 1/19/2017.
 */
@Aspect
@Pointcut("com.jp.controller.*.*()")
public class ControllerAspect {

  private static ThreadLocal<Long> begin = new ThreadLocal<Long>();

  @Before
  public void before(TargetSource target) {
    begin.set(System.currentTimeMillis());
  }

  @After
  public void after(TargetSource target) {
    Method m = target.getTargetMethod();
    System.out.format("%s.%s cost:%s%n", m.getDeclaringClass().getName(),
        m.getName(), (System.currentTimeMillis() - begin.get()));
    begin.remove();
  }

}
