package jp.spring.web.handler.impl;

import java.net.HttpCookie;
import java.util.Map;
import java.util.Optional;
import jp.spring.web.annotation.CookieParam;
import jp.spring.web.handler.Filler;
import jp.spring.web.handler.HandlerArgResolver;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;

public class CookieFiller implements Filler<Object> {


  private CookieParam cookie;
  /**
   * 参数类型
   */
  private Class<?> type;
  /**
   * 参数名
   */
  private String name;

  private CookieFiller(CookieParam c, String name, Class<?> type) {
    this.type = type;
    this.cookie = c;
    this.name = StringUtils.isBlank(c.value()) ? name : c.value();
  }

  public static CookieFiller of(CookieParam c, String name, Class<?> type) {
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
