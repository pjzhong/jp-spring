package jp.spring.mvc.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import java.net.HttpCookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jp.spring.http.MIME;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Simple Handler Args Resolver
 */
public class HandlerArgResolver {

  /** HttpRequest */
  private FullHttpRequest request;
  /** HttpResponse */
  private FullHttpResponse response;
  /** 请求处理者 */
  private Handler handler;
  /** 路径参数 */
  private Map<String, String> paths;
  /** 请求参数 */
  private Map<String, List<String>> params;
  /** Cookies */
  private Map<String, HttpCookie> cookies;
  /** Hanlder所需参数 */
  private Object[] args;

  private HandlerArgResolver() {
    this.paths = Collections.emptyMap();
    this.cookies = Collections.emptyMap();
    this.params = Collections.emptyMap();
    this.args = ArrayUtils.EMPTY_OBJECT_ARRAY;
  }

  private HandlerArgResolver(Pair<Handler, Map<String, String>> routed,
      FullHttpRequest request, FullHttpResponse response) {
    this.handler = routed.getLeft();
    this.paths = routed.getRight();
    this.request = request;
    this.response = response;
  }

  public static HandlerArgResolver resolve(Pair<Handler, Map<String, String>> routed,
      FullHttpRequest request, FullHttpResponse response) {
    return new HandlerArgResolver(routed, request, response);
  }

  private synchronized void parseQueryParam() {
    if (this.params != null) {
      return;
    }

    Map<String, List<String>> parameters = new HashMap<>(
        new QueryStringDecoder(request.uri()).parameters());

    String type = request.headers().get(HttpHeaderNames.CONTENT_TYPE, "").toLowerCase();
    MIME format = MIME.parse(type);

    if (format == MIME.APPLICATION_X_WWW_FORM_URLENCODED) {
      String s = request.content().toString(CharsetUtil.UTF_8);
      parameters.putAll(new QueryStringDecoder(s, false).parameters());
    }

    this.params = Collections.unmodifiableMap(parameters);
  }

  private synchronized void parseCookie() {
    if (this.cookies != null) {
      return;
    }

    String cookieLine = request.headers().get(HttpHeaderNames.COOKIE);
    List<HttpCookie> cookies =
        StringUtils.isBlank(cookieLine) ? Collections.emptyList() : HttpCookie.parse(cookieLine);
    this.cookies = Collections
        .unmodifiableMap(cookies.stream().collect(Collectors.toMap(HttpCookie::getName, h -> h)));
  }

  private synchronized void parseArgs() {
    if (args != null) {
      return;
    }

    if (handler == null) {
      args = ArrayUtils.EMPTY_OBJECT_ARRAY;
      return;
    }

    List<MethodParameter> parameters = handler.getParameters();
    Object[] args = new Object[parameters.size()];
    for (int i = 0; i < args.length; i++) {
      args[i] = parameters.get(i).getConverter().apply(this);
    }
    this.args = args;
  }

  public Map<String, List<String>> getParams() {
    if (params == null) {
      parseQueryParam();
    }
    return ObjectUtils.defaultIfNull(params, Collections.emptyMap());
  }

  public Map<String, HttpCookie> getCookies() {
    if (cookies == null) {
      parseCookie();
    }

    return ObjectUtils.defaultIfNull(cookies, Collections.emptyMap());
  }

  public FullHttpRequest getRequest() {
    return request;
  }

  public FullHttpResponse getResponse() {
    return response;
  }

  public Map<String, String> getPaths() {
    return ObjectUtils.defaultIfNull(paths, Collections.emptyMap());
  }

  public Object[] getArgs() {
    if (args == null) {
      parseArgs();
    }
    return ObjectUtils.defaultIfNull(args, ArrayUtils.EMPTY_OBJECT_ARRAY);
  }
}
