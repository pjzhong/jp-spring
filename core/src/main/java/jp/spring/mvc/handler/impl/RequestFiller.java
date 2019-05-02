package jp.spring.mvc.handler.impl;

import jp.spring.mvc.handler.Filler;
import jp.spring.mvc.handler.HandlerArgResolver;

public class RequestFiller implements Filler<Object> {

  public static RequestFiller request = new RequestFiller();

  private RequestFiller() {
  }

  @Override
  public Object apply(HandlerArgResolver arg) {
    return arg.getRequest();
  }
}
