package jp.spring.web.handler.impl;

import java.net.HttpCookie;
import java.util.Map;
import java.util.Optional;
import jp.spring.util.TypeUtil;
import jp.spring.web.annotation.CookieParam;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;
import org.apache.commons.lang3.StringUtils;

public class CookieAdapter implements Adapter<Object> {


  /** 参数类型 */
  private Class<?> type;
  /** 参数名 */
  private String name;

  private CookieAdapter(CookieParam c, String name, Class<?> type) {
    this.type = type;
    this.name = StringUtils.isBlank(c.value()) ? name : c.value();
  }

  public static CookieAdapter of(CookieParam c, String name, Class<?> type) {
    return new CookieAdapter(c, name, type);
  }

  @Override
  public Object apply(HandlerContext args) {
    Map<String, HttpCookie> cookies = args.getCookies();
    Optional<String> value = Optional.ofNullable(cookies.get(name))
        .map(HttpCookie::getValue);
    return TypeUtil.convertToSimpleType(value.orElse(""), type);
  }
}
