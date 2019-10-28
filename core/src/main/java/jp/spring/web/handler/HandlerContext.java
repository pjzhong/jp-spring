package jp.spring.web.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import java.net.HttpCookie;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jp.spring.ioc.factory.BeanFactory;
import jp.spring.web.handler.Router.Route;
import jp.spring.web.interceptor.InterceptMatch;
import jp.spring.web.interceptor.Interceptor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * A HandlerContext
 */
public class HandlerContext {

  /** HttpRequest */
  private FullHttpRequest request;
  /** HttpResponse */
  private FullHttpResponse response;
  /** 路由 */
  private Route<Handler> route;
  /** 请求参数 */
  private Map<String, List<String>> params;
  /** Cookies */
  private Map<String, HttpCookie> cookies;
  /** 处理参数 */
  private Object[] args;
  /** 处理结果 */
  private Object result;

  private HandlerContext(Route<Handler> routed,
      FullHttpRequest request, FullHttpResponse response) {
    this.route = routed;
    this.request = request;
    this.response = response;
  }

  public static HandlerContext build(Route<Handler> routed,
      FullHttpRequest request, FullHttpResponse response) {
    return new HandlerContext(routed, request, response);
  }

  public Map<String, List<String>> getParams() {
    return ObjectUtils.defaultIfNull(params, Collections.emptyMap());
  }

  public void setParams(Map<String, List<String>> params) {
    this.params = params;
  }

  public void setCookies(Map<String, HttpCookie> cookies) {
    this.cookies = cookies;
  }

  public Map<String, HttpCookie> getCookies() {
    return cookies;
  }

  public FullHttpRequest getRequest() {
    return request;
  }

  public FullHttpResponse getResponse() {
    return response;
  }

  public Map<String, String> getPaths() {
    return route.getPathParams();
  }

  private void parseArgs() {
    if (args != null) {
      return;
    }

    if (route == null) {
      return;
    }

    List<MethodParameter> parameters = route.getTarget().getParameters();
    Object[] args = new Object[parameters.size()];
    for (int i = 0; i < args.length; i++) {
      args[i] = parameters.get(i).parse(this);
    }
    this.args = args;
  }

  public Object[] getArgs() {
    if (args == null) {
      parseArgs();
    }
    return ObjectUtils.defaultIfNull(args, ArrayUtils.EMPTY_OBJECT_ARRAY);
  }

  public Handler getHandler() {
    return route.getTarget();
  }

  public Object getResult() {
    return result;
  }

  public HandlerContext setResult(Object result) {
    this.result = result;
    return this;
  }

  public Object invoke(BeanFactory factory) throws Exception {
    LinkedList<Interceptor> intercepts = new LinkedList<>();
    Handler handler = route.getTarget();

    boolean go = true;
    for (InterceptMatch match : handler.getInterceptors()) {
      Interceptor i = match.getInterceptor(factory);
      go = i.beforeHandle(this);
      intercepts.addFirst(i);
      if (!go) {
        break;
      }
    }

    if (go) {
      Object o = factory.getBean(handler.getBeanName());
      result = handler.invoke(o, getArgs());
      intercepts.forEach(i -> i.afterHandle(this));
    }

    return result;
  }

}
