package jp.spring.mvc.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.CookieValue;
import jp.spring.mvc.annotation.PathVariable;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.handler.impl.CookieConverter;
import jp.spring.mvc.handler.impl.HeaderConverter;
import jp.spring.mvc.handler.impl.NullConverter;
import jp.spring.mvc.handler.impl.PathVariableConverter;
import jp.spring.mvc.handler.impl.RequestParamConverter;
import jp.spring.mvc.interceptor.Interceptor;

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
  private List<Interceptor> interceptors = Collections.emptyList();

  public Handler(String url, RequestMethod[] httpMethods, Method method, String beanName) {
    this.url = url;
    this.method = method;
    this.beanName = beanName;
    this.httpMethods = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(httpMethods)));
  }

  public Object invoke(Object obj, Object[] args) throws Exception {
    method.setAccessible(true);
    return method.invoke(obj, args);
  }

  @Deprecated
  public boolean isResponseBody() {
    return false;
  }

  @Deprecated
  public Matcher getPathVariableMatcher(String s) {
    return null;
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

  public List<Interceptor> getInterceptors() {
    return interceptors;
  }

  public Set<RequestMethod> getHttpMethods() {
    return httpMethods;
  }

  public void addInterceptors(Collection<Interceptor> interceptors) {
    this.interceptors.addAll(interceptors);
  }

  private static List<MethodParameter> buildParameter(Handler handler) {
    Method method = handler.getMethod();
    Parameter[] parameters = method.getParameters();
    if (parameters.length <= 0) {
      return Collections.emptyList();
    }

    List<MethodParameter> result = new ArrayList<>();
    for (Parameter p : parameters) {
      Annotation[] annotations = p.getAnnotations();
      if (annotations.length <= 0) {
        throw new IllegalArgumentException(
            String.format("%s-%s missing Annotation%n", method.getName(), method.getName())
        );
      }

      int count = 0;
      Annotation anno = null;
      for (Annotation a : annotations) {
        if (required.contains(a.annotationType())) {
          anno = a;
          count++;
        }
      }

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

      Converter<Object> converter =
          TypeUtil.isSimpleType(p.getType()) ? createConverter(anno, p) : NullConverter.NULL;
      result.add(new MethodParameter(p.getType(), annotations, converter));
    }
    return Collections.unmodifiableList(result);
  }

  private static Converter<Object> createConverter(Annotation a, Parameter p) {
    // create convert
    Class<? extends Annotation> aType = a.annotationType();
    if (PathVariable.class.isAssignableFrom(aType)) {
      return PathVariableConverter.of((PathVariable) a, p.getType());
    } else if (RequestParam.class.isAssignableFrom(aType)) {
      return RequestParamConverter.of((RequestParam) a, p.getType());
    } else if (RequestHeader.class.isAssignableFrom(aType)) {
      return HeaderConverter.of((RequestHeader) a, p.getType());
    } else if (CookieValue.class.isAssignableFrom(aType)) {
      return CookieConverter.of((CookieValue) a, p.getType());
    } else {
      return NullConverter.NULL;
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
