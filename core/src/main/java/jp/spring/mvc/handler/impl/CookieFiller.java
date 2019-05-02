package jp.spring.mvc.handler.impl;

import java.net.HttpCookie;
import java.util.Map;
import java.util.Optional;
import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.CookieValue;
import jp.spring.mvc.handler.Filler;
import jp.spring.mvc.handler.HandlerArgResolver;

public class CookieFiller implements Filler<Object> {


  private CookieValue cookie;
  /**
   * 参数类型
   */
  private Class<?> type;

  private CookieFiller(CookieValue c, Class<?> type) {
    this.type = type;
    this.cookie = c;
  }

  public static CookieFiller of(CookieValue c, Class<?> type) {
    return new CookieFiller(c, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    Map<String, HttpCookie> cookies = args.getCookies();
    Optional<String> value = Optional.ofNullable(cookies.get(cookie.value()))
        .map(HttpCookie::getValue);
    return TypeUtil.convert(value.orElse(""), type);
  }
}
