package jp.spring.web.handler.impl;

import jp.spring.web.annotation.PathVariable;
import jp.spring.web.handler.Filler;
import jp.spring.web.handler.HandlerArgResolver;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;

public class PathVariableFiller implements Filler<Object> {

  /**
   * 参数标记
   */
  private PathVariable path;
  /**
   * 目标类型
   */
  private Class<?> type;
  /**
   * 名字
   */
  private String name;

  private PathVariableFiller(PathVariable path, String name, Class<?> type) {
    this.type = type;
    this.path = path;
    this.name = StringUtils.isBlank(path.value()) ? name : path.value();
  }

  public static PathVariableFiller of(PathVariable path, String name, Class<?> type) {
    return new PathVariableFiller(path, name, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    String pathVar = args.getPaths().getOrDefault(name, "");
    return TypeUtil.convertToSimpleType(pathVar, type);
  }
}
