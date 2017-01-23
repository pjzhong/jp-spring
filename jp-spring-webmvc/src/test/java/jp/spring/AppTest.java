package jp.spring;

import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.web.context.DefaultWebApplicationContext;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    public static void main(String[] args) throws Exception {
        String path = "classpath:spring/test.xml";
        WebApplicationContext webApplicationContext = new DefaultWebApplicationContext(path);
    }
}
