package jp.spring.util;

import static jp.spring.util.TypeUtil.determinedName;
import static jp.spring.util.TypeUtil.isAnnotated;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.spring.mvc.annotation.Controller;
import jp.spring.util.TypeUtilTest.AnnotatedParentClass.AnnotatedChildClass;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class TypeUtilTest {

  @Test
  public void determinedNameTest() {
    assertEquals("list", determinedName(List.class));
    assertEquals("typeUtilTest", determinedName(this.getClass()));

    assertNotEquals("Map", determinedName(Map.class));
    assertNotEquals("HashMap", determinedName(HashMap.class));
  }

  @Test
  public void findMethodTest() {
    List<Method> methods = TypeUtil.findMethods(AnnotatedChildClass.class, Test.class);
    assertEquals(4, methods.size());
    for (Method m : methods) {
      assertNotNull(m.getAnnotation(Test.class));
    }
  }

  @Test
  public void isAnnotated_classTest() {
    assertTrue(isAnnotated(AnnotatedChildClass.class, Controller.class));
    assertTrue(isAnnotated(AnnotatedParentClass.class, Controller.class));
    assertFalse(isAnnotated(AnnotatedChildClass.class, Deprecated.class));
    assertFalse(isAnnotated(AnnotatedParentClass.class, Deprecated.class));
  }

  @Controller
  static class AnnotatedParentClass {

    public void fromParent() {
    }

    @Controller
    final static class AnnotatedChildClass extends AnnotatedParentClass {

      void packageNotInclude() {
      }

      protected void protectedNotInclude() {
      }

      private void privateNotInclude() {
      }

      public void publicNotInclude() {
      }

      @Test
      void packageMethod() {
      }

      @Test
      protected void protectedMethod() {
      }

      @Test
      private void privateMethod() {

      }

      @Test
      public void publicMethod() {
      }
    }
  }


  @Test
  public void isAnnotated_methodTest() {
    class allTestAnnotated {

      @Test
      @Rule
      void packageMethod() {
      }

      @Test
      @Rule
      protected void protectedMethod() {
      }

      @Test
      @Rule
      private void privateMethod() {

      }

      @Test
      @Rule
      public void publicMethod() {
      }
    }

    Method[] methods = allTestAnnotated.class.getDeclaredMethods();
    for (Method m : methods) {
      assertTrue(isAnnotated(m, Test.class));
      assertTrue(isAnnotated(m, Rule.class));
      assertFalse(isAnnotated(m, After.class));
    }
  }


}
