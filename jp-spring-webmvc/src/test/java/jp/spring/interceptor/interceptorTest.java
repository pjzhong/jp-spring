package jp.spring.interceptor;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/27/2017.
 */
public class interceptorTest {

    @Test
    public void test1() {
        Pattern pattern = Pattern.compile("/example");

        String test = "/asdfasdf/example/test/45";

        System.out.println(pattern.matcher(test).find());
    }
}
