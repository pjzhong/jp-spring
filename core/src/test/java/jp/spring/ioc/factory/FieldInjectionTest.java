package jp.spring.ioc.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Component;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;

public class FieldInjectionTest {

  @Test
  void fieldInjected() {
    BeanFactory factory = new DefaultApplicationContext();

    Target target = (Target) factory.getBean(TypeUtil.determinedName(Target.class));
    assertNotNull(target);
    assertTrue(Target.class.isAssignableFrom(target.getClass()));
    assertNotNull(target.a);
    assertEquals(target.a.getClass(), A.class);
  }

  @Component
  public static class Target {

    @Autowired
    private A a;
  }

  @Component
  public static class A {

  }

}
