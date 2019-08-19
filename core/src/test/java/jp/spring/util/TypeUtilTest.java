package jp.spring.util;

import static jp.spring.util.TypeUtil.determinedName;
import static jp.spring.util.TypeUtil.isAnnotated;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.spring.mvc.annotation.Controller;
import jp.spring.util.TypeUtilTest.AnnotatedParentClass.AnnotatedChildClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


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
    List<Method> methods = TypeUtil.findMethods(AnnotatedChildClass.class, Tag.class);
    assertEquals(4, methods.size());
    for (Method m : methods) {
      assertNotNull(m.getAnnotation(Tag.class));
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
    @Disabled
    final static class AnnotatedChildClass extends AnnotatedParentClass {

      void packageNotInclude() {
      }

      protected void protectedNotInclude() {
      }

      private void privateNotInclude() {
      }

      public void publicNotInclude() {
      }

      @Tag("")
      void packageMethod() {
      }

      @Tag("")
      protected void protectedMethod() {
      }

      @Tag("")
      private void privateMethod() {

      }

      @Tag("")
      public void publicMethod() {
      }
    }
  }


  @Test
  public void isAnnotated_methodTest() {
    class allTestAnnotated {

      @Test
      @Disabled
      void packageMethod() {
      }

      @Test
      @Disabled
      protected void protectedMethod() {
      }

      @Test
      @Disabled
      private void privateMethod() {

      }

      @Test
      @Disabled
      public void publicMethod() {
      }
    }

    Method[] methods = allTestAnnotated.class.getDeclaredMethods();
    for (Method m : methods) {
      assertTrue(isAnnotated(m, Test.class));
      assertTrue(isAnnotated(m, Disabled.class));
      assertFalse(isAnnotated(m, AfterEach.class));
    }
  }

  @Test
  public void rawTypeTest() throws NoSuchFieldException, NoSuchMethodException {
    assertEquals(Box.class, TypeUtil.getRawClass(Box.class));

    Field data = Box.class.getDeclaredField("data");
    assertEquals(Object.class, TypeUtil.getRawClass(data.getGenericType()));

    Field listDate = Box.class.getDeclaredField("listData");
    assertEquals(List.class, TypeUtil.getRawClass(listDate.getGenericType()));
    assertEquals(Object.class, TypeUtil
        .getRawClass(((ParameterizedType) listDate.getGenericType()).getActualTypeArguments()[0]));

    Field wildcardData = Box.class.getDeclaredField("wildcardData");
    assertEquals(List.class, TypeUtil.getRawClass(wildcardData.getGenericType()));
    assertEquals(Object.class, TypeUtil
        .getRawClass(
            ((ParameterizedType) wildcardData.getGenericType()).getActualTypeArguments()[0]));

    Field arrayData = Box.class.getDeclaredField("arrayData");
    assertEquals(Object[].class, TypeUtil.getRawClass(arrayData.getGenericType()));
    assertEquals(Object.class, TypeUtil
        .getRawClass(
            ((GenericArrayType) arrayData.getGenericType()).getGenericComponentType()));

    Method setDataMethod = Box.class.getDeclaredMethod("setData", Object.class);
    Type[] types = setDataMethod.getGenericParameterTypes();
    assertEquals(Object.class, TypeUtil.getRawClass(types[0]));

    Method setListData = Box.class.getDeclaredMethod("setListData", List.class);
    Type listTypes = setListData.getGenericParameterTypes()[0];
    assertEquals(List.class, TypeUtil.getRawClass(listTypes));
    assertEquals(Object.class,
        TypeUtil.getRawClass(((ParameterizedType) listTypes).getActualTypeArguments()[0]));

    Method setWildcardData = Box.class.getDeclaredMethod("setWildcardData", List.class);
    Type wildcard = setWildcardData.getGenericParameterTypes()[0];
    assertEquals(List.class, TypeUtil.getRawClass(wildcard));
    assertEquals(Object.class,
        TypeUtil.getRawClass(((ParameterizedType) wildcard).getActualTypeArguments()[0]));

    Method setArrayData = Box.class.getDeclaredMethod("setArrayData", Object[].class);
    Type setArray = setArrayData.getGenericParameterTypes()[0];
    assertEquals(Object[].class, TypeUtil.getRawClass(setArray));
    assertEquals(Object.class,
        TypeUtil.getRawClass(((GenericArrayType) setArray).getGenericComponentType()));
  }

  private class Box<T> {

    private T data;
    private List<T> listData;
    private List<? extends T> wildcardData;
    private T[] arrayData;

    public void setData(T data) {
      this.data = data;
    }

    public void setListData(List<T> listData) {
      this.listData = listData;
    }

    public void setWildcardData(List<? extends T> wildcardData) {
      this.wildcardData = wildcardData;
    }

    public void setArrayData(T[] arrayData) {
      this.arrayData = arrayData;
    }
  }

  @Test
  public void integerBoxTest() throws NoSuchFieldException, NoSuchMethodException {
    assertEquals(IntegerBox.class, TypeUtil.getRawClass(IntegerBox.class));

    Field data = IntegerBox.class.getDeclaredField("data");
    assertEquals(Integer.class, TypeUtil.getRawClass(data.getGenericType()));

    Field listDate = IntegerBox.class.getDeclaredField("listData");
    assertEquals(List.class, TypeUtil.getRawClass(listDate.getGenericType()));
    assertEquals(Integer.class, TypeUtil
        .getRawClass(((ParameterizedType) listDate.getGenericType()).getActualTypeArguments()[0]));

    Field wildcardData = IntegerBox.class.getDeclaredField("wildcardData");
    assertEquals(List.class, TypeUtil.getRawClass(wildcardData.getGenericType()));
    assertEquals(Integer.class, TypeUtil
        .getRawClass(
            ((ParameterizedType) wildcardData.getGenericType()).getActualTypeArguments()[0]));

    Field arrayData = IntegerBox.class.getDeclaredField("arrayData");
    assertEquals(Integer[].class, TypeUtil.getRawClass(arrayData.getGenericType()));
    assertEquals(Integer.class, TypeUtil
        .getRawClass(
            ((GenericArrayType) arrayData.getGenericType()).getGenericComponentType()));

    Method setData = IntegerBox.class.getDeclaredMethod("setData", Integer.class);
    Type[] types = setData.getGenericParameterTypes();
    assertEquals(Integer.class, TypeUtil.getRawClass(types[0]));

    Method setListData = IntegerBox.class.getDeclaredMethod("setListData", List.class);
    Type listTypes = setListData.getGenericParameterTypes()[0];
    assertEquals(List.class, TypeUtil.getRawClass(listTypes));
    assertEquals(Integer.class,
        TypeUtil.getRawClass(((ParameterizedType) listTypes).getActualTypeArguments()[0]));

    Method setWildcard = IntegerBox.class.getDeclaredMethod("setWildcardData", List.class);
    Type wildcard = setWildcard.getGenericParameterTypes()[0];
    assertEquals(List.class, TypeUtil.getRawClass(wildcard));
    assertEquals(Integer.class,
        TypeUtil.getRawClass(((ParameterizedType) wildcard).getActualTypeArguments()[0]));

    Method setArrayData = IntegerBox.class.getDeclaredMethod("setArrayData", Integer[].class);
    Type setArray = setArrayData.getGenericParameterTypes()[0];
    assertEquals(Integer[].class, TypeUtil.getRawClass(setArray));
    assertEquals(Integer.class,
        TypeUtil.getRawClass(((GenericArrayType) setArray).getGenericComponentType()));
  }

  private class IntegerBox<T extends Integer> {

    private T data;
    private T[] arrayData;
    private List<T> listData;
    private List<? extends T> wildcardData;

    public void setData(T data) {
      this.data = data;
    }

    public void setListData(List<T> listData) {
      this.listData = listData;
    }

    public void setWildcardData(List<? extends T> wildcardData) {
      this.wildcardData = wildcardData;
    }

    public void setArrayData(T[] arrayData) {
      this.arrayData = arrayData;
    }
  }


}
