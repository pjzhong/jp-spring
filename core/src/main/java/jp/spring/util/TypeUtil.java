package jp.spring.util;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class TypeUtil {

  private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT;
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

    Map<Class<?>, Function<String, Object>> primitive_parser = new HashMap<>();
    Function<String, Object> booleanParse = s -> s.equals("1") || s.equals("true");
    primitive_parser.put(Boolean.class, booleanParse);
    primitive_parser.put(Boolean.TYPE, booleanParse);

    Function<String, Object> characterParse = s -> s.isEmpty() ? '\0' : s.charAt(0);
    primitive_parser.put(Character.class, characterParse);
    primitive_parser.put(Character.TYPE, characterParse);

    primitive_parser.put(Byte.class, Byte::parseByte);
    primitive_parser.put(Byte.TYPE, Byte::parseByte);
    primitive_parser.put(Short.class, Short::parseShort);
    primitive_parser.put(Short.TYPE, Short::parseShort);
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

  /**
   * Returns whether the given {@code type} is a string, primitive or primitive wrapper ({@link
   * Boolean}, {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link
   * Double}, {@link Float}).
   *
   * @param clazz The class to query or null.
   * @return true if the given {@code type} is a string, primitive or primitive wrapper ({@link
   * Boolean}, {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link
   * Double}, {@link Float}).
   * @since 3.1
   */
  public static boolean isSimpleType(Class<?> clazz) {
    return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz == String.class;
  }

  /**
   * convertToSimpleType a String {@code value} to String, primitive or primitive wrapper ({@link
   * Boolean}, {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link
   * Double}, {@link Float})
   *
   * @return the converted value
   */
  public static Object convertToSimpleType(String value, Class<?> target) {
    Exception exception = null;
    try {
      if (String.class == target) {//String just return
        return value;
      } else if (target.isPrimitive()) {//primitive
        Function<String, Object> parse = DEFAULT_PRIMITIVE_PARSER.get(target);
        return StringUtils.isBlank(value) ? PRIMITIVE_DEFAULT.get(target) : parse.apply(value);
      } else if (ClassUtils.isPrimitiveWrapper(target)) {
        Function<String, Object> parse = DEFAULT_PRIMITIVE_PARSER.get(target);
        return (ObjectUtils.isEmpty(parse) || StringUtils.isBlank(value)) ? null
            : parse.apply(value);
      }
    } catch (Exception e) {
      exception = e;
    }
    throw new IllegalArgumentException(
        String.format("converted [%s] to %s failed", value, target), exception);
  }


  /**
   * check the given {@code element} is annotated with the given {$code annotation}
   *
   * @param element the class to query
   * @param annotation the annotation need to find
   * @return true if the given {@code element} is annotated with the given @{code annotation}
   * @since 2019年08月19日 09:26:51
   */
  public static <A extends Annotation> boolean isAnnotated(AnnotatedElement element,
      Class<A> annotation) {
    if (element.getAnnotation(annotation) != null) {
      return true;
    }

    for (Annotation a : element.getAnnotations()) {
      if (a.annotationType().getAnnotation(annotation) != null) {
        return true;
      }
    }

    return false;
  }

  /**
   * Get a single {@link Annotation} of {@code annotationType} from the supplied {@link
   * AnnotatedElement}, where the annotation is either <em>present</em> or
   * <em>meta-present</em> on the {@code AnnotatedElement}.
   * <p>Note that this method supports only a single level of meta-annotations.
   *
   * @param annotatedElement the {@code AnnotatedElement} from which to get the annotation
   * @param annotationType the annotation type to look for, both locally and as a meta-annotation
   * @return the first matching annotation, or {@code null} if not found
   * @since 3.1
   */
  public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement,
      Class<A> annotationType) {
    A annotation = annotatedElement.getAnnotation(annotationType);
    if (annotation == null) {
      for (Annotation metaAnn : annotatedElement.getAnnotations()) {
        annotation = metaAnn.annotationType().getAnnotation(annotationType);
        if (annotation != null) {
          break;
        }
      }
    }
    return annotation;

  }

  /**
   * find all methods on the give {@code class} with the given {@code annotation} which
   * RetentionPolicy much include {@link RetentionPolicy#RUNTIME}, including public, protected,
   * default (package) access, and private methods, but excluding inherited methods.
   *
   * @return the List of {@code Method} objects representing all the methods with the given {@code
   * annotation} of this class
   * @since 2019年08月18日 22:38:16
   */
  public static <A extends Annotation> List<Method> findMethods(Class<?> clazz,
      Class<A> annotation) {
    Method[] methods = clazz.getDeclaredMethods();
    if (ObjectUtils.isEmpty(methods)) {
      return Collections.emptyList();
    }
    List<Method> list = new LinkedList<>();
    for (Method method : methods) {
      if (TypeUtil.isAnnotated(method, annotation)) {
        list.add(method);
      }
    }
    return list;
  }

  /**
   * get the name of the given class, the first character is uncapitalize
   *
   * @return the name
   */
  public static String simpleClassName(Class<?> beanClass) {
    return StringUtils.uncapitalize(beanClass.getSimpleName());
  }

  /**
   * Returns the raw class of the given type.
   */
  public static Class<?> getRawClass(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    }
    if (type instanceof ParameterizedType) {
      return getRawClass(((ParameterizedType) type).getRawType());
    }
    // For TypeVariable and WildcardType, returns the first upper bound.
    if (type instanceof TypeVariable) {
      return getRawClass(((TypeVariable) type).getBounds()[0]);
    }
    if (type instanceof WildcardType) {
      return getRawClass(((WildcardType) type).getUpperBounds()[0]);
    }
    if (type instanceof GenericArrayType) {
      Class<?> componentClass = getRawClass(((GenericArrayType) type).getGenericComponentType());
      return Array.newInstance(componentClass, 0).getClass();
    }
    // This shouldn't happen as we captured all implementations of Type above (as or Java 8)
    throw new IllegalArgumentException(
        "Unsupported type " + type + " of type class " + type.getClass());
  }
}
