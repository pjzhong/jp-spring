package jp.spring.mvc.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.interceptor.Interceptor;
import jp.spring.mvc.support.MethodParameter;

/**
 * HttpResourceModel contains information needed to handle Http call for a given path. Used as a
 * destination in {@code Router} to route URI paths to right Http end points.
 */
public class Handler {

  private final String beanName;
  private final Method method;
  private final String url;
  private final Set<RequestMethod> httpMethods;
  private Set<Annotation> annotations = Collections.emptySet();
  private List<MethodParameter> methodParameters = Collections.emptyList();
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
  public boolean match(String path) {
    return false;
  }

  @Deprecated
  public boolean hasPathVariable() {
    return false;
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

  public Map<String, Integer> getPathVariableIndexMap() {
    return Collections.emptyMap();
  }

  public List<MethodParameter> getMethodParameters() {
    return methodParameters;
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
