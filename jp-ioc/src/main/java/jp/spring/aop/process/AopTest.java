package jp.spring.aop.process;

import jp.spring.ioc.stereotype.Component;

/**
 * Created by Administrator on 1/24/2017.
 */
@Component("aopTest")
public class AopTest {
    public AopTest() {
        System.out.println("I am aopTest");
    }

    public String toString() {
        return "I am AopTest";
    }
}
