package jp.spring.mvc.handler.impl;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.handler.Converter;
import jp.spring.mvc.handler.HandlerArgResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

public class RequestParamConverter implements Converter<Object> {

  /**
   * 参数标记
   */
  private RequestParam reqParam;
  private Type type;

  private RequestParamConverter(RequestParam q, Type type) {
    this.type = type;
    this.reqParam = q;
  }

  public static RequestParamConverter of(RequestParam q, Type type) {
    return new RequestParamConverter(q, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    Map<String, List<String>> params = args.getParams();
    List<String> values = params.getOrDefault(reqParam.value(), Collections.emptyList());

    Class<?> rawType = getRawClass(type);
    if (TypeUtil.isSimpleType(rawType)) {
      return TypeUtil.convert(values.isEmpty() ? "" : values.get(0), rawType);
    } else if (rawType.isArray()) {
      return apply(values, rawType);
    } else {
      return apply(values, type);
    }
  }


  private Object apply(List<String> strings, Class<?> array) {
    Class<?> compType = (Class<?>) TypeUtils.getArrayComponentType(array);
    if (!TypeUtil.isSimpleType(compType)) {
      return null;
    }

    Object result =  Array.newInstance(compType, strings.size());

    for (int i = 0, size = strings.size(); i < size; i++) {
      Array.set(result, i, TypeUtil.convert(strings.get(i), compType));
    }
    return result;
  }


  public Object apply(List<String> strings, Type type) {
    Class<?> rawType = getRawClass(type);
    if (!TypeUtils.isAssignable(type, Collection.class)) {
      return null;
    }

    // Must be ParameterizedType
    if (!(type instanceof ParameterizedType)) {
      return null;
    }

    // Must have 1 type parameter
    ParameterizedType parType = (ParameterizedType) type;
    if (parType.getActualTypeArguments().length != 1) {
      return null;
    }

    Class<?> elementType = getRawClass(parType.getActualTypeArguments()[0]);
    if (!TypeUtil.isSimpleType(elementType)) {
      return null;
    }

    Collection<Object> collection = null;
    try {
      if (Objects.equals(rawType, List.class)) {
        collection = new ArrayList<>();
      } else if (Objects.equals(rawType, Set.class)) {
        collection = new HashSet<>();
      } else {
        collection = (Collection<Object>) ConstructorUtils
            .invokeConstructor(rawType, ArrayUtils.EMPTY_OBJECT_ARRAY);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to create Collection " + type);
    }

    for (int i = 0, size = strings.size(); i < size; i++) {
      collection.add(TypeUtil.convert(strings.get(i), elementType));
    }

    return collection;
  }

  /**
   * Returns the raw class of the given type.
   */
  private static Class<?> getRawClass(Type type) {
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
