package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import java.util.Optional;
import jp.spring.util.TypeUtil;
import jp.spring.web.annotation.RequestHeader;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;
import org.apache.commons.lang3.StringUtils;

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
  private Class<?> type;

  private HeaderAdapter(RequestHeader reqHeader, String name, Class<?> type) {
    this.type = type;
    this.name = StringUtils.isBlank(reqHeader.value()) ? name : reqHeader.value();
  }

  public static HeaderAdapter of(RequestHeader reqHeader, String name, Class<?> type) {
    return new HeaderAdapter(reqHeader, name, type);
  }

  @Override
  public Object apply(HandlerContext args) {
    Optional<String> headLine = Optional
        .ofNullable(args.getRequest())
        .map(FullHttpRequest::headers)
        .map(hs -> hs.get(name));
    return TypeUtil.convertToSimpleType(headLine.orElse(null), type);
  }
}
