package jp.spring.mvc.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import java.util.Optional;
import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.handler.Filler;
import jp.spring.mvc.handler.HandlerArgResolver;

/**
 * @author ZJP
 * @since 2019年04月23日 11:55:04
 **/
public class HeaderFiller implements Filler<Object> {

  /**
   * 参数标记
   */
  private RequestHeader reqHeader;
  /**
   * 目标类型
   */
  private Class<?> type;

  private HeaderFiller(RequestHeader reqHeader, Class<?> type) {
    this.type = type;
    this.reqHeader = reqHeader;
  }

  public static HeaderFiller of(RequestHeader reqHeader, Class<?> type) {
    return new HeaderFiller(reqHeader, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    Optional<String> headLine = Optional
        .ofNullable(args.getRequest())
        .map(FullHttpRequest::headers)
        .map(hs -> hs.get(reqHeader.value()));
    return TypeUtil.convert(headLine.orElse(""), type);
  }
}
