package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpResponse;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;

/**
 * HTTP Response适配器
 *
 * @author ZJP
 * @since 2019年10月06日 21:27:10
 **/
public class ResponseAdapter implements Adapter<FullHttpResponse> {

  public static ResponseAdapter response = new ResponseAdapter();

  private ResponseAdapter() {
  }

  @Override
  public FullHttpResponse apply(HandlerContext arg) {
    return arg.getResponse();
  }
}
