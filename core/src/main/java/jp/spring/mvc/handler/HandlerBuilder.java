package jp.spring.mvc.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.mvc.annotation.RequestMapping;
import jp.spring.mvc.annotation.RequestMethod;

class HandlerBuilder {

  List<Handler> buildHandler(String name, Class<?> controller) {
    if (!JpUtils.isAnnotated(controller, Controller.class)) {
      return Collections.emptyList();
    }

    String[] clazzUrl = getBase(controller);
    List<Handler> handlers = new ArrayList<>();
    Arrays.stream(controller.getMethods()).filter(m -> JpUtils.isAnnotated(m, RequestMapping.class))
        .forEach(m -> {
          String[] urls = getPath(m);
          RequestMethod[] httpMethods = getHttpMethods(m);
          for (String base : clazzUrl) {
            for (String url : urls) {
              String absolutePath = cleanPath(String.format("%s/%s", base, url));
              handlers.add(new Handler(absolutePath, httpMethods, m, name));
            }
          }
        });
    return handlers.isEmpty() ? Collections.emptyList() : handlers;
  }

  /**
   * 获取Controller级别的URL
   *
   * @param controller 类
   * @author ZJP
   * @since 2019年04月18日 10:09:50
   **/
  private String[] getBase(Class<?> controller) {
    String[] clazzUrl = null;
    //处理Class级别的URL
    if (JpUtils.isAnnotated(controller, RequestMapping.class)) {
      clazzUrl = controller.getAnnotation(RequestMapping.class).value();
    }
    if (JpUtils.isEmpty(clazzUrl)) {
      clazzUrl = new String[]{""};
    }

    return clazzUrl;
  }

  /**
   * 获取方法级别的URL
   *
   * @param m 方法
   * @since 2019年04月18日 10:10:33
   */
  private String[] getPath(Method m) {
    String[] urls = m.getAnnotation(RequestMapping.class).value();
    if (StringUtils.isEmpty(urls)) {//urls为空，取方法名作默认url
      urls = new String[]{m.getName()};
    }
    return urls;
  }

  private String cleanPath(String url) {
    return url.replaceAll("/+", "/");
  }

  private RequestMethod[] getHttpMethods(Method m) {
    return m.getAnnotation(RequestMapping.class).method();
  }

}
