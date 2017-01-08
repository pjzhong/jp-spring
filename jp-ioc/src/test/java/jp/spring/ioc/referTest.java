package jp.spring.ioc;

import org.junit.Test;

/**
 * Created by Administrator on 12/26/2016.
 */
public class referTest {

    @Test
    public static void main(String[] args) {
        Object bean = null;
        Object value = bean;

        value = "asdkjf;lasjdf";

        System.out.println(bean);

        System.out.println(referTest.class);
    }
}
