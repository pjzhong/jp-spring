package jp.spring;

import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.web.context.DefaultWebApplicationContext;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
