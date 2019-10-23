package jp.spring.web.handler;

import static org.apache.commons.lang3.ArrayUtils.EMPTY_OBJECT_ARRAY;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import jp.spring.util.TypeUtil;
import jp.spring.web.annotation.CookieParam;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestHeader;
import jp.spring.web.annotation.RequestParam;
import jp.spring.web.handler.impl.CookieAdapter;
import jp.spring.web.handler.impl.HeaderAdapter;
import jp.spring.web.handler.impl.NullAdapter;
import jp.spring.web.handler.impl.PathParamAdapter;
import jp.spring.web.handler.impl.RequestAdapter;
import jp.spring.web.handler.impl.RequestParamAdapter;
import jp.spring.web.handler.impl.ResponseAdapter;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

class HandlerParamBuilder {

  private static Map<Class<? extends Annotation>, BiFunction<String, Type, Adapter<Object>>> required;

  static {
    Map<Class<? extends Annotation>, BiFunction<String, Type, Adapter<Object>>> adapters = new HashMap<>();
    adapters.put(PathVariable.class, PathParamAdapter::of);
    adapters.put(RequestParam.class, RequestParamAdapter::of);
    adapters.put(RequestHeader.class, HeaderAdapter::of);
    adapters.put(CookieParam.class, CookieAdapter::of);
    required = adapters;
  }

  public static List<MethodParameter> build(Handler handler) {
    Method method = handler.getMethod();
    Parameter[] parameters = method.getParameters();
    if (parameters.length <= 0) {
      return Collections.emptyList();
    }

    Type[] parameterTypes = method.getGenericParameterTypes();
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();

    List<MethodParameter> result = new ArrayList<>();
    for (int i = 0; i < parameterTypes.length; i++) {
      Annotation[] annotations = parameterAnnotations[i];
      Type type = parameterTypes[i];
      Parameter p = parameters[i];

      int count = 0;
      Annotation anno = null;
      for (Annotation a : annotations) {
        if (required.containsKey(a.annotationType())) {
          anno = a;
          count++;
        }
      }

      Class<?> rawClass = TypeUtil.getRawClass(type);
      boolean standard = TypeUtils.isAssignable(rawClass, FullHttpRequest.class) || TypeUtils
          .isAssignable(rawClass, FullHttpResponse.class);

      Adapter<?> filler;
      if (standard) {
        filler = createStandard(type);
      } else {
        // validate annotations
        if (1 < count) {
          throw new IllegalArgumentException(
              String
                  .format("%s-%s too much  required Annotation%n", method.getName(), p.getName())
          );
        }
        filler = anno == null ? RequestParamAdapter.of(p.getName(), type)
            : createConverter(anno, type, p);
      }

      result.add(new MethodParameter(p.getType(), filler));
    }
    return Collections.unmodifiableList(result);
  }

  private static Adapter<Object> createConverter(Annotation a, Type type, Parameter p) {
    // create convertToSimpleType
    String name = getName(a, p.getName());
    BiFunction<String, Type, Adapter<Object>> adapterFunction = required
        .get(a.annotationType());
    if (adapterFunction != null) {
      return adapterFunction.apply(name, type);
    } else {
      return NullAdapter.NULL;
    }
  }

  private static String getName(Annotation a, String def) {
    String name = null;
    try {
      name = (String) ClassUtils.getPublicMethod(a.annotationType(), "value")
          .invoke(a, EMPTY_OBJECT_ARRAY);
    } catch (Exception ignore) {
    }
    return StringUtils.isBlank(name) ? def : name;
  }

  private static Adapter<?> createStandard(Type type) {
    if (TypeUtils.isAssignable(type, FullHttpRequest.class)) {
      return RequestAdapter.request;
    } else if (TypeUtils.isAssignable(type, FullHttpResponse.class)) {
      return ResponseAdapter.response;
    } else {
      return NullAdapter.NULL;
    }
  }

}
