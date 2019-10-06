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

    Target target = (Target) factory.getBean(TypeUtil.simpleName(Target.class));
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

  @Component
  public static class B {

  }

  @Test
  void parentFieldInjected() {
    BeanFactory factory = new DefaultApplicationContext();

    Child child = (Child) factory.getBean(TypeUtil.simpleName(Child.class));
    assertNotNull(child);
    assertTrue(Child.class.isAssignableFrom(child.getClass()));

    assertNotNull(child.a);
    assertEquals(child.a.getClass(), A.class);

    assertNotNull(child.b);
    assertEquals(child.b.getClass(), B.class);
  }

  public static class Parent {

    @Autowired
    public A a;
  }

  @Component
  public static class Child extends Parent {

    @Autowired
    public B b;
  }


}
