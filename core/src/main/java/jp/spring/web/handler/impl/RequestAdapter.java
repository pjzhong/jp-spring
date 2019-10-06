package jp.spring.web.handler.impl;

import io.netty.handler.codec.http.FullHttpRequest;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;

/**
 * HTTP请求适配器
 *
 * @author ZJP
 * @since 2019年10月06日 21:26:38
 **/
public class RequestAdapter implements Adapter<FullHttpRequest> {

  public static RequestAdapter request = new RequestAdapter();

  private RequestAdapter() {
  }

  @Override
  public FullHttpRequest apply(HandlerContext arg) {
    return arg.getRequest();
  }
}
