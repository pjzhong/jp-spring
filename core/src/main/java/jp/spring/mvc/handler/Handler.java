package jp.spring.mvc.handler;

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
import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.CookieValue;
import jp.spring.mvc.annotation.PathVariable;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.handler.impl.CookieFiller;
import jp.spring.mvc.handler.impl.HeaderFiller;
import jp.spring.mvc.handler.impl.NullFiller;
import jp.spring.mvc.handler.impl.PathVariableFiller;
import jp.spring.mvc.handler.impl.RequestFiller;
import jp.spring.mvc.handler.impl.RequestParamFiller;
import jp.spring.mvc.handler.impl.ResponseFiller;
import jp.spring.mvc.interceptor.InterceptMatch;
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
          CookieValue.class,
          PathVariable.class)));

  private final String beanName;
  private final Method method;
  private final String url;
  private final Set<RequestMethod> httpMethods;
  private Set<Annotation> annotations = Collections.emptySet();
  private List<MethodParameter> parameters = null;
  private List<InterceptMatch> interceptors = Collections.emptyList();

  public Handler(String url, RequestMethod[] httpMethods, Method method, String beanName,
      List<InterceptMatch> interceptors) {
    this.url = url;
    this.method = method;
    this.beanName = beanName;
    this.interceptors = interceptors;
    this.httpMethods = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(httpMethods)));
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

  public Set<RequestMethod> getHttpMethods() {
    return httpMethods;
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

      Filler<Object> filler = NullFiller.NULL;
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

        filler = createConverter(anno, type);
      }

      result.add(new MethodParameter(p.getType(), annotations, filler));
    }
    return Collections.unmodifiableList(result);
  }

  private static Filler<Object> createConverter(Annotation a, Type type) {
    // create convert
    Class<? extends Annotation> aType = a.annotationType();
    if (PathVariable.class.isAssignableFrom(aType)) {
      return PathVariableFiller.of((PathVariable) a, (Class<?>) type);
    } else if (RequestParam.class.isAssignableFrom(aType)) {
      return RequestParamFiller.of((RequestParam) a, type);
    } else if (RequestHeader.class.isAssignableFrom(aType)) {
      return HeaderFiller.of((RequestHeader) a, (Class<?>) type);
    } else if (CookieValue.class.isAssignableFrom(aType)) {
      return CookieFiller.of((CookieValue) a, (Class<?>) type);
    } else {
      return NullFiller.NULL;
    }
  }

  private static Filler<Object> createStandard(Type type) {
    if (TypeUtils.isAssignable(type, FullHttpRequest.class)) {
      return RequestFiller.request;
    } else if (TypeUtils.isAssignable(type, FullHttpResponse.class)) {
      return ResponseFiller.response;
    } else {
      return NullFiller.NULL;
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Handler{");
    sb.append(", url='").append(url).append('\'');
    sb.append("method=").append(method);
    sb.append(", beanName='").append(beanName).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
