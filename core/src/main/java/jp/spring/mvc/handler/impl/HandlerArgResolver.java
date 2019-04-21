package jp.spring.mvc.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import java.net.HttpCookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.spring.http.BodyFormat;
import jp.spring.ioc.util.IocUtil;
import jp.spring.mvc.annotation.CookieValue;
import jp.spring.mvc.annotation.PathVariable;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.MethodParameter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Simple Handler Args Resolver
 */
public class HandlerArgResolver {

  private static HandlerArgResolver EMPTY = new HandlerArgResolver();

  private Map<String, List<String>> params;
  private Map<String, HttpCookie> cookies;
  private Object[] args = ArrayUtils.EMPTY_OBJECT_ARRAY;

  public static HandlerArgResolver resolve(Pair<Handler, Map<String, String>> routed,
      FullHttpRequest request) {
    Handler handler = routed.getLeft();
    List<MethodParameter> parameters = handler.getParameters();
    if (ObjectUtils.isEmpty(parameters)) {
      return EMPTY;
    }

    HandlerArgResolver resolver = new HandlerArgResolver();
    resolver.resolver(routed, request);

    return resolver;
  }

  private synchronized void resolver(Pair<Handler, Map<String, String>> routed,
      FullHttpRequest request) {
    Handler handler = routed.getLeft();
    List<MethodParameter> parameters = handler.getParameters();
    if (ObjectUtils.isEmpty(parameters)) {
      return;
    }
    Object[] result = new Object[parameters.size()];
    int idx = 0;

    //TODO ADD Collection AND Arrays Support
    //TODO MAKE this code More Readable
    for (MethodParameter p : parameters) {
      if (p.hasAnnotation(RequestParam.class)) {
        parseQueryParam(request);
        String name = p.getAnnotation(RequestParam.class).value();
        List<String> param = getParams().getOrDefault(name, Collections.emptyList());
        String value = param.isEmpty() ? null : param.get(0);
        result[idx] = IocUtil.convert(value, p.getType());
      } else if (p.hasAnnotation(CookieValue.class)) {
        parseCookie(request);
        String name = p.getAnnotation(CookieValue.class).value();
        Optional<String> cookie = Optional.ofNullable(getCookies().get(name))
            .map(HttpCookie::getValue);
        result[idx] = IocUtil.convert(cookie.orElse(null), p.getType());
      } else if (p.hasAnnotation(PathVariable.class)) {
        String name = p.getAnnotation(PathVariable.class).value();
        result[idx] = IocUtil.convert(routed.getRight().get(name), p.getType());
      } else if (p.hasAnnotation(RequestHeader.class)) {
        String name = p.getAnnotation(RequestHeader.class).value();
        String header = request.headers().get(name);
        result[idx] = IocUtil.convert(header, p.getType());
      }
      idx++;
    }

    this.args = result;
  }

  private void parseQueryParam(FullHttpRequest request) {
    if (this.params != null) {
      return;
    }

    Map<String, List<String>> parameters = new HashMap<>(
        new QueryStringDecoder(request.uri()).parameters());

    String type = request.headers().get(HttpHeaderNames.CONTENT_TYPE, "").toLowerCase();
    BodyFormat format = BodyFormat.parse(type);

    if (format == BodyFormat.APPLICATION_X_WWW_FORM_URLENCODED) {
      String s = request.content().toString(CharsetUtil.UTF_8);
      parameters.putAll(new QueryStringDecoder(s, false).parameters());
    }

    this.params = Collections.unmodifiableMap(parameters);
  }

  private void parseCookie(FullHttpRequest request) {
    if (this.cookies != null) {
      return;
    }

    String cookieLine = request.headers().get(HttpHeaderNames.COOKIE);
    List<HttpCookie> cookies = HttpCookie.parse(cookieLine);
    this.cookies = Collections
        .unmodifiableMap(cookies.stream().collect(Collectors.toMap(HttpCookie::getName, h -> h)));
  }

  public Map<String, List<String>> getParams() {
    return ObjectUtils.defaultIfNull(params, Collections.emptyMap());
  }

  public Map<String, HttpCookie> getCookies() {
    return ObjectUtils.defaultIfNull(cookies, Collections.emptyMap());
  }

  public Object[] getArgs() {
    return args;
  }
}
