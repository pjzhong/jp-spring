package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpResponse;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerArgResolver;

public class ResponseAdapter implements Adapter<FullHttpResponse> {

  public static ResponseAdapter response = new ResponseAdapter();

  private ResponseAdapter() {
  }

  @Override
  public FullHttpResponse apply(HandlerArgResolver arg) {
    return arg.getResponse();
  }
}
