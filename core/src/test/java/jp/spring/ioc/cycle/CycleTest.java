package jp.spring.ioc.cycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import jp.spring.ApplicationContext;
import jp.spring.DefaultApplicationContext;
import jp.spring.util.TypeUtil;
import org.junit.Test;

public class CycleTest {

  @Test
  public void test() throws Exception {
    ApplicationContext context = new DefaultApplicationContext();
    Both both = (Both) context.getBean(TypeUtil.determinedName(Both.class));

    assertNotNull(both);
    assertNotNull(both.a);
    assertNotNull(both.b);
    assertEquals(both.b.getA(), both.a);
    assertEquals(both.a.getB(), both.b);
  }
}
