package jp.spring.mvc.handler.impl;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.handler.Filler;
import jp.spring.mvc.handler.HandlerArgResolver;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

public class RequestParamFiller implements Filler<Object> {

  /**
   * 参数标记
   */
  private RequestParam reqParam;
  /**
   * 类型
   */
  private Type type;
  /**
   * 参数名
   */
  private String name;

  private RequestParamFiller(RequestParam q, String name, Type type) {
    this.type = type;
    this.reqParam = q;
    this.name = StringUtils.isBlank(q.value()) ? name : q.value();
  }

  public static RequestParamFiller of(RequestParam q, String name, Type type) {
    return new RequestParamFiller(q, name, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    Map<String, List<String>> params = args.getParams();
    List<String> values = params.getOrDefault(name, Collections.emptyList());

    Class<?> rawType = TypeUtil.getRawClass(type);
    if (TypeUtil.isSimpleType(rawType)) {
      return TypeUtil.convertToSimpleType(values.isEmpty() ? "" : values.get(0), rawType);
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

    Object result = Array.newInstance(compType, strings.size());

    for (int i = 0, size = strings.size(); i < size; i++) {
      Array.set(result, i, TypeUtil.convertToSimpleType(strings.get(i), compType));
    }
    return result;
  }


  public Object apply(List<String> strings, Type type) {
    Class<?> rawType = TypeUtil.getRawClass(type);
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

    Class<?> elementType = TypeUtil.getRawClass(parType.getActualTypeArguments()[0]);
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
      collection.add(TypeUtil.convertToSimpleType(strings.get(i), elementType));
    }

    return collection;
  }


}
