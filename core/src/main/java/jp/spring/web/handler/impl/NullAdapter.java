package jp.spring.web.handler.impl;

import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerArgResolver;

/**
 * 默认转换器
 *
 * @author ZJP
 * @since 2019年04月23日 11:16:05
 **/
public class NullAdapter implements Adapter<Object> {

  public static NullAdapter NULL = new NullAdapter();

  private NullAdapter() {
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
