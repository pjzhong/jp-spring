package jp.spring.mvc.handler.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.handler.Converter;
import jp.spring.mvc.handler.HandlerArgResolver;

public class RequestParamConverter implements Converter<Object> {

  /**
   * 参数标记
   */
  private RequestParam reqParam;
  private Class<?> type;

  private RequestParamConverter(RequestParam q, Class<?> type) {
    this.type = type;
    this.reqParam = q;
  }

  public static RequestParamConverter of(RequestParam q, Class<?> type) {
    return new RequestParamConverter(q, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    Map<String, List<String>> params = args.getParams();
    List<String> values = params.getOrDefault(reqParam.value(), Collections.emptyList());
    return TypeUtil.convert(values.isEmpty() ? "" : values.get(0), type);
  }
}
