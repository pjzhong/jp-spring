package jp.spring.web.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jp.spring.util.TypeUtil;
import jp.spring.web.annotation.CookieParam;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestHeader;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.RequestParam;
import jp.spring.web.annotation.ResponseBody;
import jp.spring.web.handler.impl.CookieAdapter;
import jp.spring.web.handler.impl.HeaderAdapter;
import jp.spring.web.handler.impl.NullAdapter;
import jp.spring.web.handler.impl.PathParamAdapter;
import jp.spring.web.handler.impl.RequestAdapter;
import jp.spring.web.handler.impl.RequestParamAdapter;
import jp.spring.web.handler.impl.ResponseAdapter;
import jp.spring.web.interceptor.InterceptMatch;
import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * HttpResourceModel contains information needed to handle Http call for a given path. Used as a
 * destination in {@code Router} to route URI paths to right Http end points.
 */
public class Handler {

  private static Set<Class<? extends Annotation>> required = Collections
      .unmodifiableSet(new HashSet<>(Arrays.asList(
          RequestParam.class,
          RequestHeader.class,
          CookieParam.class,
          PathVariable.class)));

  private final String beanName;
  private final Method method;
  private final String url;
  private final RequestMethod[] httpMethods;
  private final boolean responseBody;
  private List<MethodParameter> parameters = null;
  private List<InterceptMatch> interceptors;

  public Handler(String url, RequestMethod[] httpMethods, Method method, String beanName,
      List<InterceptMatch> interceptors) {
    this.url = url;
    this.method = method;
    this.beanName = beanName;
    this.interceptors = interceptors;
    this.httpMethods = httpMethods;

    Set<Class<? extends Annotation>> anns = new HashSet<>();
    for (Annotation a : method.getAnnotations()) {
      anns.add(a.annotationType());
    }
    this.responseBody = anns.contains(ResponseBody.class);
  }

  public Object invoke(Object obj, Object[] args) throws Exception {
    method.setAccessible(true);
    return method.invoke(obj, args);
  }

  public Method getMethod() {
    return method;
  }

  public String getUrl() {
    return url;
  }

  public String getBeanName() {
    return beanName;
  }

  public List<MethodParameter> getParameters() {
    if (parameters == null) {
      synchronized (this) {
        parameters = buildParameter(this);
      }
    }

    return parameters;
  }

  public List<InterceptMatch> getInterceptors() {
    return interceptors;
  }

  public RequestMethod[] getHttpMethods() {
    return httpMethods;
  }

  public boolean hasMethod(RequestMethod method) {
    for (RequestMethod m : httpMethods) {
      if (m == method) {
        return true;
      }
    }
    return false;
  }

  public boolean isResponseBody() {
    return responseBody;
  }

  private static List<MethodParameter> buildParameter(Handler handler) {
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
        if (required.contains(a.annotationType())) {
          anno = a;
          count++;
        }
      }

      Class<?> rawClass = TypeUtil.getRawClass(type);
      boolean standard = TypeUtils.isAssignable(rawClass, FullHttpRequest.class) || TypeUtils
          .isAssignable(rawClass, FullHttpResponse.class);

      Adapter<?> filler = NullAdapter.NULL;
      if (standard) {
        filler = createStandard(type);
      } else {
        // validate annotations
        if (count <= 0) {
          throw new IllegalArgumentException(
              String.format("%s-%s missing required Annotation%n", method.getName(), p.getName())
          );
        } else if (1 < count) {
          throw new IllegalArgumentException(
              String
                  .format("%s-%s too much  required Annotation%n", method.getName(), p.getName())
          );
        }

        filler = createConverter(anno, type, p);
      }

      result.add(new MethodParameter(p.getType(), filler));
    }
    return Collections.unmodifiableList(result);
  }

  private static Adapter<Object> createConverter(Annotation a, Type type, Parameter p) {
    // create convertToSimpleType
    Class<? extends Annotation> aType = a.annotationType();
    String pName = p.getName();
    if (PathVariable.class.isAssignableFrom(aType)) {
      return PathParamAdapter.of((PathVariable) a, pName, (Class<?>) type);
    } else if (RequestParam.class.isAssignableFrom(aType)) {
      return RequestParamAdapter.of((RequestParam) a, pName, type);
    } else if (RequestHeader.class.isAssignableFrom(aType)) {
      return HeaderAdapter.of((RequestHeader) a, pName, (Class<?>) type);
    } else if (CookieParam.class.isAssignableFrom(aType)) {
      return CookieAdapter.of((CookieParam) a, pName, (Class<?>) type);
    } else {
      return NullAdapter.NULL;
    }
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

  @Override
  public String toString() {
    return "Handler{" + ", url='" + url + '\''
        + "method=" + method
        + ", beanName='" + beanName + '\''
        + '}';
  }
}
