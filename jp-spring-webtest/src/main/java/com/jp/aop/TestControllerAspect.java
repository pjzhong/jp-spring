package com.jp.aop;

import jp.spring.aop.annotation.After;
import jp.spring.aop.annotation.Before;
import jp.spring.aop.annotation.Pointcut;
import jp.spring.aop.support.TargetSource;
import jp.spring.ioc.stereotype.Aspect;

import java.lang.annotation.Annotation;

/**
 * Created by Administrator on 1/20/2017.
 */
@Aspect
@Pointcut("execution(com.jp.controller.*.test*())")
public class TestControllerAspect {

    @Before
    public void before(TargetSource target) {
       System.out.println(target.getTargetMethod() + " begin");
    }

    @After
    public void after(TargetSource target) {
        System.out.println(target.getTargetMethod() + " end");
    }

    public static void main(String[] args) {
        for(Annotation annotation : TestControllerAspect.class.getAnnotations()) {
            System.out.println(annotation);
        }
    }
}
