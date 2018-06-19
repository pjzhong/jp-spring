package jp.spring.ioc.cycle;

import jp.spring.ioc.context.ApplicationContext;
import jp.spring.ioc.context.impl.ClassPathPropertiesApplicationContext;
import static org.junit.Assert.*;


import org.junit.Test;

public class CycleTest {

    @Test
    public void test() throws Exception {
        ApplicationContext context = new ClassPathPropertiesApplicationContext("/");
        Both both = (Both) context.getBean("both");

        assertNotNull(both);
        assertNotNull(both.a);
        assertNotNull(both.b);
        assertEquals(both.b.getA() , both.a);
        assertEquals(both.a.getB() , both.b);
    }
}
