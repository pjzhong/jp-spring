package jp.spring.aop;

import jp.spring.aop.annotation.After;
import jp.spring.aop.annotation.Before;
import jp.spring.aop.annotation.Pointcut;
import jp.spring.aop.impl.JoinPointParameter;
import jp.spring.ioc.stereotype.Aspect;

/**
 * Created by Administrator on 1/19/2017.
 */
@Aspect
@Pointcut("execution(jp.spring.ioc.*.*())")
public class AspectTest {

    private long begin;

    @Before
    public void testBefore(JoinPointParameter parameter) {
       begin = System.currentTimeMillis();
        System.out.println(parameter.getArgs().length);
        System.out.println(parameter.getMethod());
    }

    @After
    public void testAfter(JoinPointParameter parameter) {
        System.out.println("Time: " + (System.currentTimeMillis() - begin) + "ms");
    }

}
