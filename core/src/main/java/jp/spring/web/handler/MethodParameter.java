package jp.spring.web.handler;

/**
 * Controller 参数信息封装
 */
public class MethodParameter {

  private Class<?> type;
  private Adapter<?> adapter;

  public MethodParameter(Class<?> type, Adapter<?> adapter) {
    this.type = type;
    this.adapter = adapter;
  }

  public Class<?> getType() {
    return type;
  }


  public Object parse(HandlerContext args) {
    return adapter.apply(args);
  }
}
