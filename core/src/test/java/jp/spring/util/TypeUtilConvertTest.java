package jp.spring.util;

import static jp.spring.util.TypeUtil.convertToSimpleType;
import static jp.spring.util.TypeUtil.isSimpleType;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TypeUtilConvertTest {

  @Test
  public void isSimpleTypeTest() {
    assertTrue(isSimpleType(String.class));
    assertTrue(isSimpleType(Boolean.class));
    assertTrue(isSimpleType(Boolean.TYPE));
    assertTrue(isSimpleType(Byte.class));
    assertTrue(isSimpleType(Byte.TYPE));
    assertTrue(isSimpleType(Character.class));
    assertTrue(isSimpleType(Character.TYPE));
    assertTrue(isSimpleType(Short.class));
    assertTrue(isSimpleType(Short.TYPE));
    assertTrue(isSimpleType(Integer.class));
    assertTrue(isSimpleType(Integer.TYPE));
    assertTrue(isSimpleType(Long.class));
    assertTrue(isSimpleType(Long.TYPE));
    assertTrue(isSimpleType(Double.class));
    assertTrue(isSimpleType(Double.TYPE));
    assertTrue(isSimpleType(Float.class));
    assertTrue(isSimpleType(Float.TYPE));

    assertFalse(isSimpleType(List.class));
    assertFalse(isSimpleType(ArrayList.class));
    assertFalse(isSimpleType(Map.class));
    assertFalse(isSimpleType(HashMap.class));
  }

  @Test
  public void convertTest() {
    assertThat("abc", is(convertToSimpleType("abc", String.class)));

    assertThat(true, is(convertToSimpleType("1", Boolean.TYPE)));
    assertThat(true, is(convertToSimpleType("1", Boolean.class)));
    assertThat(true, is(convertToSimpleType("true", Boolean.TYPE)));
    assertThat(true, is(convertToSimpleType("true", Boolean.class)));
    assertThat(false, is(convertToSimpleType("0", Boolean.TYPE)));
    assertThat(false, is(convertToSimpleType("0", Boolean.class)));
    assertThat(false, is(convertToSimpleType("false", Boolean.TYPE)));
    assertThat(false, is(convertToSimpleType("false", Boolean.class)));

    assertThat((byte) 1, is(convertToSimpleType("1", Byte.TYPE)));
    assertThat((byte) 1, is(convertToSimpleType("1", Byte.class)));

    assertThat('1', is(convertToSimpleType("123", Character.TYPE)));
    assertThat('1', is(convertToSimpleType("1234", Character.class)));
    assertThat('\0', is(convertToSimpleType("", Character.TYPE)));

    assertThat((short) 1, is(convertToSimpleType("1", Short.TYPE)));
    assertThat((short) 1, is(convertToSimpleType("1", Short.class)));

    assertThat(1, is(convertToSimpleType("1", Integer.TYPE)));
    assertThat(1, is(convertToSimpleType("1", Integer.class)));

    assertThat(1L, is(convertToSimpleType("1", Long.TYPE)));
    assertThat(1L, is(convertToSimpleType("1", Long.class)));

    assertThat(1.0F, is(convertToSimpleType("1", Float.TYPE)));
    assertThat(1.0F, is(convertToSimpleType("1", Float.class)));

    assertThat(1.0, is(convertToSimpleType("1", Double.TYPE)));
    assertThat(1.0, is(convertToSimpleType("1", Double.class)));
  }

  @Test
  public void convertDefaultTest() {
    assertThat(null, is(convertToSimpleType(null, String.class)));
    assertThat("", is(convertToSimpleType("", String.class)));

    assertThat(false, is(convertToSimpleType(null, Boolean.TYPE)));
    assertThat(false, is(convertToSimpleType("", Boolean.TYPE)));
    assertThat(null, is(convertToSimpleType(null, Boolean.class)));
    assertThat(null, is(convertToSimpleType("", Boolean.class)));

    assertThat((byte) 0, is(convertToSimpleType(null, Byte.TYPE)));
    assertThat(null, is(convertToSimpleType(null, Byte.class)));

    assertThat('\0', is(convertToSimpleType(null, Character.TYPE)));
    assertThat('\0', is(convertToSimpleType("", Character.TYPE)));
    assertThat(null, is(convertToSimpleType(null, Character.class)));
    assertThat(null, is(convertToSimpleType("", Character.class)));

    assertThat((short) 0, is(convertToSimpleType(null, Short.TYPE)));
    assertThat((short) 0, is(convertToSimpleType("", Short.TYPE)));
    assertThat(null, is(convertToSimpleType(null, Short.class)));
    assertThat(null, is(convertToSimpleType(null, Short.class)));

    assertThat(0, is(convertToSimpleType(null, Integer.TYPE)));
    assertThat(0, is(convertToSimpleType("", Integer.TYPE)));
    assertThat(null, is(convertToSimpleType("", Integer.class)));
    assertThat(null, is(convertToSimpleType(null, Integer.class)));

    assertThat(0L, is(convertToSimpleType(null, Long.TYPE)));
    assertThat(0L, is(convertToSimpleType("", Long.TYPE)));
    assertThat(null, is(convertToSimpleType("", Long.class)));
    assertThat(null, is(convertToSimpleType(null, Long.class)));

    assertThat(0.0F, is(convertToSimpleType(null, Float.TYPE)));
    assertThat(0.0F, is(convertToSimpleType("", Float.TYPE)));
    assertThat(null, is(convertToSimpleType("", Float.class)));
    assertThat(null, is(convertToSimpleType(null, Float.class)));

    assertThat(0.0, is(convertToSimpleType("", Double.TYPE)));
    assertThat(0.0, is(convertToSimpleType(null, Double.TYPE)));
    assertThat(null, is(convertToSimpleType("", Double.class)));
    assertThat(null, is(convertToSimpleType(null, Double.class)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void convertToNotSimpleType() {
    convertToSimpleType("1", List.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void convertToNull() {
    convertToSimpleType("1", null);
  }
}
