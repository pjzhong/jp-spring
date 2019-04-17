package jp.spring.mvc.handler.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.mvc.annotation.RequestMapping;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.HandlerMappingBuilder;
import jp.spring.mvc.support.MethodParameter;

/**
 * Created by Administrator on 1/10/2017. 负责在mvc模块启动的时候，创建handler并交给HandlerMapping来负责映射
 */
public class DefaultHandlerMappingBuilder implements HandlerMappingBuilder {

  private final Pattern PATTERN_PATH_VARIABLE = Pattern.compile("(\\{([^}]+)\\})");

  public DefaultHandlerMappingBuilder() {
  }

  /**
   * URL on class level default is '/' and on method level  is '' (If use did not provide a
   * specified vale).
   */
  @Override
  public List<Handler> buildHandler(String name, Class<?> controller) {
    if (!JpUtils.isAnnotated(controller, Controller.class)) {
      return Collections.emptyList();
    }

    String[] clazzUrl = {""};

    //处理Class级别的URL
    if (JpUtils.isAnnotated(controller, RequestMapping.class)) {
      clazzUrl = controller.getAnnotation(RequestMapping.class).value();
    }

    List<Handler> handlers = new ArrayList<>();
    Arrays.stream(controller.getMethods()).filter(m -> JpUtils.isAnnotated(m, RequestMapping.class))
        .forEach(m -> {
          String[] urls = m.getAnnotation(RequestMapping.class).value();
          if (StringUtils.isEmpty(urls)) {//urls为空，取方法名作默认url
            urls = new String[1];
            urls[0] = m.getName();
          }
          buildParameters(m);

          //TODO build Parameters
        });
    return handlers.isEmpty() ? Collections.emptyList() : handlers;
  }


  private List<MethodParameter> buildParameters(Method method) {
    //TODO implement it
    return Collections.emptyList();
  }
}
