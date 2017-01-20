package com.jp.aop;

import jp.spring.aop.annotation.After;
import jp.spring.aop.annotation.Before;
import jp.spring.aop.annotation.Pointcut;
import jp.spring.aop.impl.JoinPointParameter;
import jp.spring.aop.support.TargetSource;
import jp.spring.ioc.stereotype.Aspect;

/**
 * Created by Administrator on 1/19/2017.
 */
@Aspect
@Pointcut("execution(com.jp.controller.*.*())")
public class ControllerAspect {

    private long begin;

    @Before
    public void before(TargetSource target) {
        begin = System.nanoTime();
    }

    @After
    public void after(TargetSource target) {
        System.out.println("cost:" + (System.nanoTime() - begin));
    }

}
