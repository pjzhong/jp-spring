package jp.spring.mvc.handler.impl;

import java.net.HttpCookie;
import java.util.Map;
import java.util.Optional;
import jp.spring.mvc.annotation.CookieValue;
import jp.spring.mvc.handler.Filler;
import jp.spring.mvc.handler.HandlerArgResolver;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;

public class CookieFiller implements Filler<Object> {


  private CookieValue cookie;
  /**
   * 参数类型
   */
  private Class<?> type;
  /**
   * 参数名
   */
  private String name;

  private CookieFiller(CookieValue c, String name, Class<?> type) {
    this.type = type;
    this.cookie = c;
    this.name = StringUtils.isBlank(c.value()) ? name : c.value();
  }

  public static CookieFiller of(CookieValue c, String name, Class<?> type) {
    return new CookieFiller(c, name, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    Map<String, HttpCookie> cookies = args.getCookies();
    Optional<String> value = Optional.ofNullable(cookies.get(name))
        .map(HttpCookie::getValue);
    return TypeUtil.convertToSimpleType(value.orElse(""), type);
  }
}
