package jp.spring.web.handler.impl;

import jp.spring.web.handler.Filler;
import jp.spring.web.handler.HandlerArgResolver;

/**
 * 默认转换器
 *
 * @author ZJP
 * @since 2019年04月23日 11:16:05
 **/
public class NullFiller implements Filler<Object> {

  public static NullFiller NULL = new NullFiller();

  private NullFiller() {
  }

  /**
   * 永远返回Null
   *
   * @param handlerArgResolver 参数提供
   * @since 2019年04月23日 11:16:34
   */
  @Override
  public Object apply(HandlerArgResolver handlerArgResolver) {
    return null;
  }
}
