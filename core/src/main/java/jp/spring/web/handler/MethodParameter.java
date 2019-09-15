package jp.spring.web.handler;

/**
 * Controller 参数信息封装
 */
public class MethodParameter {

  private Class<?> type;
  private Adapter<?> converter;

  public MethodParameter(Class<?> type, Adapter<?> converter) {
    this.type = type;
    this.converter = converter;
  }

  public Class<?> getType() {
    return type;
  }


  public Adapter<?> getConverter() {
    return converter;
  }
}
