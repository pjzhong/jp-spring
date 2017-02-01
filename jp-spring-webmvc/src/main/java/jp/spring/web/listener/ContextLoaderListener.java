package jp.spring.web.listener;

import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.context.DefaultWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import java.util.Properties;

/**
 * Created by Administrator on 1/10/2017.
 */
@WebListener
public class ContextLoaderListener implements ServletContextListener{

    /**
     * 默认只载入根目录下的配置文件
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce)  {
        ServletContext servletContext = sce.getServletContext();
        String configLocation = "/";
        try {
            WebApplicationContext webApplicationContext = new DefaultWebApplicationContext(configLocation);
            servletContext.setAttribute(webApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);

            /*先手动执行各项属性初始化*/
            ServletRegistration defaultRegistration = sce.getServletContext().getServletRegistration("default");
            String resourceFolder = ((DefaultWebApplicationContext) webApplicationContext).getProperty("resource.folder");
            if(!StringUtils.isEmpty(resourceFolder)) {
                for(String path : resourceFolder.split("\\s*;\\s*")) {
                    defaultRegistration.addMapping(path + "/*");
                }
            }
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }


}
