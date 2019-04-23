package jp.spring.mvc.handler.impl;

import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.PathVariable;
import jp.spring.mvc.handler.Converter;
import jp.spring.mvc.handler.HandlerArgResolver;

public class PathVariableConverter implements Converter<Object> {

  /**
   * 参数标记
   */
  private PathVariable path;
  /**
   * 目标类型
   */
  private Class<?> type;

  private PathVariableConverter(PathVariable path, Class<?> type) {
    this.type = type;
    this.path = path;
  }

  public static PathVariableConverter of(PathVariable name, Class<?> type) {
    return new PathVariableConverter(name, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    String pathVar = args.getPaths().getOrDefault(path.value(), "");
    return TypeUtil.convert(pathVar, type);
  }
}
