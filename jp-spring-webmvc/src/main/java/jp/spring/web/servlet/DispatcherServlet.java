package jp.spring.web.servlet;

import jp.spring.ioc.context.ApplicationContext;
import jp.spring.web.util.UrlPathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 1/3/2017.
 */
public class DispatcherServlet extends FrameworkServlet {

    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private List<HandlerMapping> handlerMappings;

    protected void initStrategies(ApplicationContext context) {

    }

    private void initiHandlerMappings(ApplicationContext context) {
        this.handlerMappings = null;

        try {
            HandlerMapping handlerMapping = (HandlerMapping) context.getBean(HANDLER_MAPPING_BEAN_NAME);
            this.handlerMappings = Collections.singletonList(handlerMapping);
        } catch (Exception e) {
            //Ignore, we'all add a default HandlerMapping later;
        }

    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) {
        //by default, do nothing
    }

    @Override
    protected void initServletBean() throws ServletException {

    }
}
