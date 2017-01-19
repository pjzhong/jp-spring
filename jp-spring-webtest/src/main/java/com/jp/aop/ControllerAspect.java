package com.jp.aop;

import jp.spring.aop.annotation.After;
import jp.spring.aop.annotation.Before;
import jp.spring.aop.annotation.Pointcut;
import jp.spring.aop.impl.JoinPointParameter;
import jp.spring.ioc.stereotype.Aspect;

/**
 * Created by Administrator on 1/19/2017.
 */
@Aspect
@Pointcut("execution(com.jp.controller.*.*())")
public class ControllerAspect {

    private long begin;

    @Before
    public void before(JoinPointParameter parameter) {
        System.out.println(parameter.getMethod() + " begin");
        begin = System.nanoTime();
    }

    @After
    public void after(JoinPointParameter parameter) {
        System.out.println(parameter.getMethod() + " end cost:" + (System.nanoTime() - begin));
    }

}
