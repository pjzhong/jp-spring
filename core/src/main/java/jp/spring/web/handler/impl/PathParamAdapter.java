package jp.spring.web.handler.impl;

import java.lang.reflect.Type;
import jp.spring.util.TypeUtil;
import jp.spring.web.handler.Adapter;
import jp.spring.web.handler.HandlerContext;

/**
 * 路径参数适配器
 *
 * @author ZJP
 * @since 2019年10月06日 21:26:23
 **/
public class PathParamAdapter implements Adapter<Object> {

  /** 目标类型 */
  private Type type;
  /** 名字 */
  private String name;

  private PathParamAdapter(String name, Type type) {
    this.type = type;
    this.name = name;
  }

  public static PathParamAdapter of(String name, Type type) {
    return new PathParamAdapter(name, type);
  }

  @Override
  public Object apply(HandlerContext args) {
    String pathVar = args.getPaths().getOrDefault(name, "");
    return TypeUtil.convertToSimpleType(pathVar, TypeUtil.getRawClass(type));
  }
}
