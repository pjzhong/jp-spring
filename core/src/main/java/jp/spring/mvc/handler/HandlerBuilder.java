package jp.spring.mvc.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jp.spring.ioc.beans.factory.DefaultBeanFactory;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.TypeUtil;
import jp.spring.mvc.annotation.RequestMapping;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.interceptor.InterceptMatch;
import org.apache.commons.lang3.ObjectUtils;

class HandlerBuilder {

  List<Handler> buildHandler(String name, DefaultBeanFactory factory,
      List<InterceptMatch> intercepts) {
    Class<?> controller = factory.getType(name);
    if (!TypeUtil.isAnnotated(controller, Controller.class)) {
      return Collections.emptyList();
    }

    String[] clazzUrl = getBase(controller);
    List<Handler> handlers = new ArrayList<>();
    for (Method m : controller.getMethods()) {
      // Is handler method
      if (!TypeUtil.isAnnotated(m, RequestMapping.class)) {
        continue;
      }

      String[] urls = getPath(m);
      RequestMethod[] httpMethods = getHttpMethods(m);
      for (String base : clazzUrl) {
        for (String url : urls) {
          String absolutePath = cleanPath(String.format("%s/%s", base, url));
          List<InterceptMatch> matches = intercepts.stream().filter(p -> p.match(url)).collect(
              Collectors.toList());
          handlers.add(new Handler(absolutePath, httpMethods, m, name, matches));
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
  private String[] getBase(Class<?> controller) {
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
  private String[] getPath(Method m) {
    String[] urls = m.getAnnotation(RequestMapping.class).value();
    if (ObjectUtils.isEmpty(urls)) {//urls为空，取方法名作默认url
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
