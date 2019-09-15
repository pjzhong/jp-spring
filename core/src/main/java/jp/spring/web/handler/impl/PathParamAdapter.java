package jp.spring.web.handler.impl;

import jp.spring.util.TypeUtil;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerArgResolver;
import org.apache.commons.lang3.StringUtils;

public class PathParamAdapter implements Adapter<Object> {

  /** 目标类型 */
  private Class<?> type;
  /** 名字 */
  private String name;

  private PathParamAdapter(PathVariable path, String name, Class<?> type) {
    this.type = type;
    this.name = StringUtils.isBlank(path.value()) ? name : path.value();
  }

  public static PathParamAdapter of(PathVariable path, String name, Class<?> type) {
    return new PathParamAdapter(path, name, type);
  }

  @Override
  public Object apply(HandlerArgResolver args) {
    String pathVar = args.getPaths().getOrDefault(name, "");
    return TypeUtil.convertToSimpleType(pathVar, type);
  }
}
