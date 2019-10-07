package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.spring.util.TypeUtil;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;
import org.apache.commons.lang3.StringUtils;

public class CookieAdapter implements Adapter<Object> {


  /** 参数类型 */
  private Type type;
  /** 参数名 */
  private String name;

  private CookieAdapter(String name, Type type) {
    this.type = type;
    this.name = name;
  }

  public static CookieAdapter of(String name, Type type) {
    return new CookieAdapter(name, type);
  }

  @Override
  public Object apply(HandlerContext args) {
    Map<String, HttpCookie> cookies = parseCookie(args);
    Optional<String> value = Optional.ofNullable(cookies.get(name))
        .map(HttpCookie::getValue);
    return TypeUtil.convertToSimpleType(value.orElse(""), TypeUtil.getRawClass(type));
  }

  private Map<String, HttpCookie> parseCookie(HandlerContext context) {
    FullHttpRequest request = context.getRequest();
    String cookieLine = request.headers().get(HttpHeaderNames.COOKIE);
    List<HttpCookie> c =
        StringUtils.isBlank(cookieLine) ? Collections.emptyList() : HttpCookie.parse(cookieLine);
    Map<String, HttpCookie> cookies = Collections
        .unmodifiableMap(c.stream().collect(Collectors.toMap(HttpCookie::getName, h -> h)));

    context.setCookies(cookies);
    return cookies;
  }
}
