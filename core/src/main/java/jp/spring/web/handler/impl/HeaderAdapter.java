package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.HttpHeaders;
import java.lang.reflect.Type;
import jp.spring.util.TypeUtil;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;

/**
 * HTTP头部参数
 *
 * @author ZJP
 * @since 2019年04月23日 11:55:04
 **/
public class HeaderAdapter implements Adapter<Object> {

  /** 参数名 */
  private String name;
  /** 目标类型 */
  private Type type;

  private HeaderAdapter(String name, Type type) {
    this.type = type;
    this.name = name;
  }

  public static HeaderAdapter of(String name, Type type) {
    return new HeaderAdapter(name, type);
  }

  @Override
  public Object apply(HandlerContext args) {
    HttpHeaders headers = args.getRequest().headers();
    return TypeUtil.convertToSimpleType(headers.get(name), TypeUtil.getRawClass(type));
  }
}
