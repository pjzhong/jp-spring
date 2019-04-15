package jp.spring.web.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.context.DefaultWebApplicationContext;

/**
 * Created by Administrator on 1/10/2017. web-mvc模块启动器
 */
public class WebIocInit {

  /**
   * 默认只载入根目录下的配置文件
   */
  public static void init(ServletContext servletContext) {

    String configLocation = "/";
    try {
      DefaultWebApplicationContext webApplicationContext = new DefaultWebApplicationContext(
          configLocation);
      servletContext.setAttribute(webApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
          webApplicationContext);

      /*先手动执行各项属性初始化*/
      ServletRegistration defaultRegistration = servletContext
          .getServletRegistration("default");
      String resourceFolder = webApplicationContext
          .getProperty("resource.folder");
      if (!StringUtils.isEmpty(resourceFolder)) {
        for (String path : resourceFolder.split("\\s*;\\s*")) {
          defaultRegistration.addMapping(path + "/*");
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
