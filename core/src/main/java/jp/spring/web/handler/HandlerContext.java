package jp.spring.web.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import java.net.HttpCookie;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jp.spring.web.handler.Router.Route;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Simple Handler Args Resolver
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
  /** Handler所需参数 */
  private Object[] args;

  private HandlerContext(Route<Handler> routed,
      FullHttpRequest request, FullHttpResponse response) {
    this.route = routed;
    this.request = request;
    this.response = response;
  }

  public static HandlerContext resolve(Route<Handler> routed,
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
}
