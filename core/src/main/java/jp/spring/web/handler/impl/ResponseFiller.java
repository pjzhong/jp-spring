package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpResponse;
import jp.spring.web.handler.Filler;
import jp.spring.web.handler.HandlerArgResolver;

public class ResponseFiller implements Filler<Object> {

  public static ResponseFiller response = new ResponseFiller();

  private ResponseFiller() {
  }

  @Override
  public FullHttpResponse apply(HandlerArgResolver arg) {
    return arg.getResponse();
  }
}
