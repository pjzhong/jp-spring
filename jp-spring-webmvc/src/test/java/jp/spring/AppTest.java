package jp.spring;

import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.web.context.DefaultXMLWebApplicationContext;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    public static void main(String[] args) throws Exception {
        String path = "classpath:spring/test.xml";
        WebApplicationContext webApplicationContext = new DefaultXMLWebApplicationContext(path);
    }
}
