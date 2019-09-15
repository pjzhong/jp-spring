package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerArgResolver;

public class RequestAdapter implements Adapter<FullHttpRequest> {

  public static RequestAdapter request = new RequestAdapter();

  private RequestAdapter() {
  }

  @Override
  public FullHttpRequest apply(HandlerArgResolver arg) {
    return arg.getRequest();
  }
}
