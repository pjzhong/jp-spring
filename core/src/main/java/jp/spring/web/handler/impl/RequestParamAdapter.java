package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jp.spring.util.TypeUtil;
import jp.spring.web.MIME;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * 请求参数适配器
 *
 * @author ZJP
 * @since 2019年10月06日 21:26:58
 **/
public class RequestParamAdapter implements Adapter<Object> {

  /** 类型 */
  private Type type;
  /** 参数名 */
  private String name;

  private RequestParamAdapter(String name, Type type) {
    this.type = type;
    this.name = name;
  }

  public static RequestParamAdapter of(String name, Type type) {
    return new RequestParamAdapter(name, type);
  }

  @Override
  public Object apply(HandlerContext args) {
    Map<String, List<String>> params = parseParams(args);
    List<String> values = params.getOrDefault(name, Collections.emptyList());

    Class<?> rawType = TypeUtil.getRawClass(type);
    if (TypeUtil.isSimpleType(rawType)) {
      return TypeUtil.convertToSimpleType(values.isEmpty() ? null : values.get(0), rawType);
    } else if (rawType.isArray()) {
      return convertToArray(values, rawType);
    } else {
      return convertToCollection(values, type);
    }
  }

  private Map<String, List<String>> parseParams(HandlerContext context) {
    FullHttpRequest request = context.getRequest();

    Map<String, List<String>> parameters = new HashMap<>(
        new QueryStringDecoder(request.uri()).parameters());

    String type = request.headers().get(HttpHeaderNames.CONTENT_TYPE, "").toLowerCase();
    MIME format = MIME.parse(type);

    if (format == MIME.APPLICATION_X_WWW_FORM_URLENCODED) {
      String s = request.content().toString(CharsetUtil.UTF_8);
      parameters.putAll(new QueryStringDecoder(s, false).parameters());
    }

    context.setParams(parameters);
    return parameters;
  }


  private Object convertToArray(List<String> strings, Class<?> array) {
    Class<?> compType = (Class<?>) TypeUtils.getArrayComponentType(array);
    if (!TypeUtil.isSimpleType(compType) || strings.isEmpty()) {
      return null;
    }

    Object result = Array.newInstance(compType, strings.size());

    for (int i = 0, size = strings.size(); i < size; i++) {
      Array.set(result, i, TypeUtil.convertToSimpleType(strings.get(i), compType));
    }
    return result;
  }


  public Object convertToCollection(List<String> strings, Type type) {
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
    if (!TypeUtil.isSimpleType(elementType) || strings.isEmpty()) {
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
