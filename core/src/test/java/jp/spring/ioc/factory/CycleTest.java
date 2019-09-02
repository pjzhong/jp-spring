package jp.spring.ioc.factory;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Component;
import jp.spring.ioc.annotation.Service;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;

class CycleTest {

  @Test
  void test() {
    BeanFactory context = new DefaultApplicationContext();
    Both both = (Both) context.getBean(TypeUtil.determinedName(Both.class));

    assertNotNull(both);
    assertNotNull(both.a);
    assertNotNull(both.b);
    assertEquals(both.b.getA(), both.a);
    assertEquals(both.a.getB(), both.b);
  }

  public interface A {

    B getB();
  }

  @Service
  public static class AImpl implements A {

    @Autowired
    B b;

    @Override
    public B getB() {
      return b;
    }
  }

  public interface B {

    A getA();
  }

  @Service
  public static class BImpl implements B {

    @Autowired
    A a;

    @Override
    public A getA() {
      return a;
    }
  }


  @Component
  public static class Both {

    @Autowired
    A a;

    @Autowired
    B b;
  }

}
