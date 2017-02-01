package jp.spring.interceptor;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/27/2017.
 */
public class interceptorTest {

    @Test
    public void test1() {
        Pattern pattern = Pattern.compile("^([/]?)([\\w/\\*]*)(/\\*){1}$");

        System.out.println(pattern.matcher(null).find());

        String test = "/exmaple1231324665465/asdf*sdf/example/test/45/4565456460/456456*/*";

        System.out.println(pattern.matcher(test).find());
        System.out.println(test.lastIndexOf("/"));


        int index = test.lastIndexOf("/");
        String prefix = test.substring(0, index).replace("*", "[\\w]*");
        String  suffix = test.substring(index).replace("/*", "[\\w/\\.]*");

        System.out.println(prefix + "/" + suffix);
    }
}
