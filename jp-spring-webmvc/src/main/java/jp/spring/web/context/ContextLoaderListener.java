package jp.spring.web.context;

import jp.spring.ioc.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Administrator on 1/10/2017.
 */
public class ContextLoaderListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce)  {
        ServletContext servletContext = sce.getServletContext();
        String configLocation = servletContext.getInitParameter("contextConfigLocation");
        try {
            WebApplicationContext webApplicationContext = new DefaultXMLWebApplicationContext(configLocation);
            servletContext.setAttribute(webApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
