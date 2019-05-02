package jp.spring.mvc.handler.impl;

import io.netty.handler.codec.http.FullHttpResponse;
import jp.spring.mvc.handler.Filler;
import jp.spring.mvc.handler.HandlerArgResolver;

public class ResponseFiller implements Filler<Object> {

  public static ResponseFiller response = new ResponseFiller();

  private ResponseFiller() {
  }

  @Override
  public FullHttpResponse apply(HandlerArgResolver arg) {
    return arg.getResponse();
  }
}
