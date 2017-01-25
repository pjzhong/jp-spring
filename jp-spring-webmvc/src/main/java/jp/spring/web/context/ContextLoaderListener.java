package jp.spring.web.context;

import jp.spring.ioc.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Administrator on 1/10/2017.
 */
public class ContextLoaderListener implements ServletContextListener{

    /**
     * 默认只载入根目录下的配置文件
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce)  {
        ServletContext servletContext = sce.getServletContext();
   /*     String configLocation = servletContext.getInitParameter("contextConfigLocation");*/
        String configLocation = "/";
        try {
            WebApplicationContext webApplicationContext = new DefaultWebApplicationContext(configLocation);
            servletContext.setAttribute(webApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }


}
