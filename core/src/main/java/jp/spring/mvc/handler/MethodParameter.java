package jp.spring.mvc.handler;

/**
 * Controller 参数信息封装
 */
public class MethodParameter {

  private Class<?> type;
  private Filler<Object> converter;

  public MethodParameter(Class<?> type, Filler<Object> converter) {
    this.type = type;
    this.converter = converter;
  }

  public Class<?> getType() {
    return type;
  }


  public Filler<Object> getConverter() {
    return converter;
  }
}
