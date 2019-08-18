package jp.spring.mvc.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import java.util.Optional;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.handler.Filler;
import jp.spring.mvc.handler.HandlerArgResolver;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ZJP
 * @since 2019年04月23日 11:55:04
 **/
public class HeaderFiller implements Filler<Object> {

  /**
   * 参数名
   */
  private String name;
  /**
   * 参数标记
   */
  private RequestHeader reqHeader;
  /**
   * 目标类型
   */
  private Class<?> type;

  private HeaderFiller(RequestHeader reqHeader, String name, Class<?> type) {
    this.type = type;
    this.reqHeader = reqHeader;
    this.name = StringUtils.isBlank(reqHeader.value()) ? name : reqHeader.value();
  }

  public static HeaderFiller of(RequestHeader reqHeader, String name, Class<?> type) {
    return new HeaderFiller(reqHeader, name, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    Optional<String> headLine = Optional
        .ofNullable(args.getRequest())
        .map(FullHttpRequest::headers)
        .map(hs -> hs.get(name));
    return TypeUtil.convertToSimpleType(headLine.orElse(null), type);
  }
}
