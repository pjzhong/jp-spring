package jp.spring.ioc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public class TypeUtil {

  private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT;
  private static final Set<Class<?>> SIMPLE_TYPE;
  private static final Map<Class<?>, Function<String, Object>> DEFAULT_PRIMITIVE_PARSER;

  static {
    Map<Class<?>, Object> defaults = new HashMap<>();
    defaults.put(Boolean.TYPE, false);
    defaults.put(Character.TYPE, '\0');
    defaults.put(Byte.TYPE, (byte) 0);
    defaults.put(Short.TYPE, (short) 0);
    defaults.put(Integer.TYPE, 0);
    defaults.put(Long.TYPE, 0L);
    defaults.put(Float.TYPE, 0f);
    defaults.put(Double.TYPE, 0d);
    PRIMITIVE_DEFAULT = Collections.unmodifiableMap(defaults);

    Set<Class<?>> wrapper = new HashSet<>(
        Arrays.asList(
            Boolean.class,
            Byte.class,
            Short.class,
            Character.class,
            Integer.class,
            Long.class,
            Double.class,
            String.class
        )
    );
    SIMPLE_TYPE = Collections.unmodifiableSet(wrapper);

    Map<Class<?>, Function<String, Object>> primitive_parser = new HashMap<>();
    Function<String, Object> booleanParse = s -> s.equals("1") || s.equals("true");
    Function<String, Object> characterParse = s -> s.isEmpty() ? '\0' : s.charAt(0);
    primitive_parser.put(Boolean.class, booleanParse);
    primitive_parser.put(Boolean.TYPE, booleanParse);
    primitive_parser.put(Byte.class, Byte::parseByte);
    primitive_parser.put(Byte.TYPE, Byte::parseByte);
    primitive_parser.put(Short.class, Short::parseShort);
    primitive_parser.put(Short.TYPE, Short::parseShort);
    primitive_parser.put(Character.class, characterParse);
    primitive_parser.put(Character.TYPE, characterParse);
    primitive_parser.put(Integer.class, Integer::parseInt);
    primitive_parser.put(Integer.TYPE, Integer::parseInt);
    primitive_parser.put(Long.class, Long::parseLong);
    primitive_parser.put(Long.TYPE, Long::parseLong);
    primitive_parser.put(Float.class, Float::parseFloat);
    primitive_parser.put(Float.TYPE, Float::parseFloat);
    primitive_parser.put(Double.class, Double::parseDouble);
    primitive_parser.put(Double.TYPE, Double::parseDouble);
    DEFAULT_PRIMITIVE_PARSER = Collections.unmodifiableMap(primitive_parser);
  }

  public static <K, V> boolean isEmpty(Map<K, V> map) {
    return map == null || map.isEmpty();
  }

  public static <T> boolean isEmpty(T[] array) {
    return array == null || array.length == 0;
  }

  public static <T> boolean isEmpty(Collection<T> c) {
    return c == null || c.isEmpty();
  }

  public static <A extends Annotation> boolean isAnnotated(Method method, Class<A> annotate) {
    return method.getAnnotation(annotate) != null;
  }

  public static <A extends Annotation> boolean isAnnotated(Class<?> clazz, Class<A> annotate) {
    if (clazz.getAnnotation(annotate) != null) {
      return true;
    }

    Annotation[] annotations = clazz.getAnnotations();
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().getAnnotation(annotate) != null) {
        return true;
      }
    }

    return false;
  }

  public static <A extends Annotation> boolean isAnnotated(Field field, Class<A> annotate) {
    return field.getAnnotation(annotate) != null;
  }

  public static boolean isSimpleType(Class<?> clazz) {
    return clazz.isPrimitive() || SIMPLE_TYPE.contains(clazz);
  }


  /**
   * convert a String to primitive or wrapper value
   */
  public static Object convert(String value, Class<?> targetClass) {
    try {
      if (targetClass == null) {//No target
        return null;
      } else if (String.class == targetClass) {//String just return
        return value;
      } else if (targetClass.isPrimitive()) {//primitive
        Function<String, Object> parse = DEFAULT_PRIMITIVE_PARSER.get(targetClass);
        return StringUtils.isBlank(value) ? PRIMITIVE_DEFAULT.get(targetClass) : parse.apply(value);
      } else {//Wrapper value
        Function<String, Object> parse = DEFAULT_PRIMITIVE_PARSER.get(targetClass);
        return StringUtils.isBlank(value) || parse == null ? null : parse.apply(value);
      }
    } catch (Exception e) {
      throw new RuntimeException("Covert failed", e);
    }
  }

  public static <A extends Annotation> List<Method> findMethods(Class<?> clazz,
      Class<A> annotation) {
    Method[] methods = clazz.getDeclaredMethods();
    if (TypeUtil.isEmpty(methods)) {
      return null;
    }
    List<Method> list = new LinkedList<>();
    for (Method method : methods) {
      if (TypeUtil.isAnnotated(method, annotation)) {
        list.add(method);
      }
    }
    return list;
  }


  public static Method findMethod(Class<?> clazz, String name) {
    if (clazz != null && name != null) {
      Method[] methods = (clazz.isInterface() ? clazz.getMethods() : clazz.getDeclaredMethods());
      for (Method method : methods) {
        if (name.equals(method.getName())) {
          return method;
        }
      }
    }

    return null;
  }
}
