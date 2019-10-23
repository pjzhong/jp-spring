package jp.spring.web.handler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.ResponseBody;
import jp.spring.web.interceptor.InterceptMatch;

/**
 * HttpResourceModel contains information needed to handle Http call for a given path. Used as a
 * destination in {@code Router} to route URI paths to right Http end points.
 */
public class Handler {

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
    this.responseBody = Stream.of(method.getAnnotations())
        .anyMatch(a -> a.annotationType() == ResponseBody.class);
  }

  public Object invoke(Object obj, Object[] args) throws Exception {
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
        parameters = HandlerParamBuilder.build(this);
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

  @Override
  public String toString() {
    return "Handler{" + ", url='" + url + '\''
        + "method=" + method
        + ", beanName='" + beanName + '\''
        + '}';
  }
}
