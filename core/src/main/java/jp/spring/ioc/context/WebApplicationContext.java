package jp.spring.ioc.context;

/**
 * Created by Administrator on 1/3/2017.
 */
public interface WebApplicationContext extends ApplicationContext{

    /**
     * Context attribute to bind root WebApplicationContext to on successful startup.
     * <p>Note: If the startup of the root context fails, this attribute can contain
     * an exception or error as value. Use WebApplicationContextUtils for convenient
     * lookup of the root WebApplicationContext.
     */
    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";
}
