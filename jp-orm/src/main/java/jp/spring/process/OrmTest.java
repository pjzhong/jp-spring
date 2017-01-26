package jp.spring.process;

import jp.spring.ioc.stereotype.Component;

/**
 * Created by Administrator on 1/24/2017.
 */
@Component("ormTest")
public class OrmTest {

    public OrmTest() {
        System.out.println("I am OrmTest");
    }

    public String toString() {
        return "I am OrmTest";
    }
}
