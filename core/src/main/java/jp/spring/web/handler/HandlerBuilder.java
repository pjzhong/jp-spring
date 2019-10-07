package jp.spring.web.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jp.spring.util.TypeUtil;
import jp.spring.web.annotation.Controller;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.interceptor.InterceptMatch;
import org.apache.commons.lang3.ObjectUtils;

class HandlerBuilder {

  static List<Handler> buildHandler(String name, Class<?> type) {
    return buildHandler(name, type, Collections.emptyList());
  }

  static List<Handler> buildHandler(String name, Class<?> type,
      List<InterceptMatch> intercepts) {
    if (!TypeUtil.isAnnotated(type, Controller.class)) {
      return Collections.emptyList();
    }

    String[] clazzUrl = getBase(type);
    List<Handler> handlers = new ArrayList<>();
    for (Method m : type.getMethods()) {
      // Is handler method
      if (TypeUtil.isAnnotated(m, RequestMapping.class)) {
        m.setAccessible(true);

        String[] urls = getPath(m);
        RequestMethod[] httpMethods = getHttpMethods(m);
        for (String base : clazzUrl) {
          for (String url : urls) {
            String absolutePath = Router.cleanPath(String.format("/%s/%s", base, url));
            List<InterceptMatch> matches = intercepts.stream().filter(p -> p.match(url)).collect(
                Collectors.toList());
            handlers.add(new Handler(absolutePath, httpMethods, m, name, matches));
          }
        }
      }
    }
    return handlers.isEmpty() ? Collections.emptyList() : handlers;
  }

  /**
   * 获取Controller级别的URL
   *
   * @param controller 类
   * @author ZJP
   * @since 2019年04月18日 10:09:50
   **/
  private static String[] getBase(Class<?> controller) {
    String[] clazzUrl = null;
    //处理Class级别的URL
    if (TypeUtil.isAnnotated(controller, RequestMapping.class)) {
      clazzUrl = controller.getAnnotation(RequestMapping.class).value();
    }
    if (ObjectUtils.isEmpty(clazzUrl)) {
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
  private static String[] getPath(Method m) {
    String[] urls = m.getAnnotation(RequestMapping.class).value();
    if (ObjectUtils.isEmpty(urls)) {//urls为空，取方法名作默认url
      urls = new String[]{m.getName()};
    }
    return urls;
  }

  private static RequestMethod[] getHttpMethods(Method m) {
    return m.getAnnotation(RequestMapping.class).method();
  }


}
