package jp.spring.web.servlet;


import jp.spring.ioc.context.WebApplicationContext;

import jp.spring.web.context.ProcessContext;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.handler.HandlerMapping;

import jp.spring.web.util.UrlPathHelper;
import jp.spring.web.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 1/3/2017.
 */
@WebServlet(loadOnStartup = 1, urlPatterns = "/")
public class DispatcherServlet extends FrameworkServlet {

    private final static Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private static WebApplicationContext webApplicationContext;

    private static HandlerMapping handlerMapping;

    private static HandlerInvoker handlerInvoker;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public void init() {
        try {
            webApplicationContext = (WebApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            handlerMapping = (HandlerMapping) webApplicationContext.getBean(handlerMapping.DEFAULT_HANDLER_MAPPING);
            handlerInvoker = (HandlerInvoker) webApplicationContext.getBean(HandlerInvoker.DEFAULT_HANDLER_INVOKER);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = urlPathHelper.getLookupPathForRequest(request);
            Handler handler = handlerMapping.getHandler(request, path);
            if(handler == null) {
                response.sendError(response.SC_NOT_FOUND,  " Not Found");
                logger.error(path + " Not Found");
                return;
            }

            //Build context
            ProcessContext
                    .buildContext()
                    .set(ProcessContext.REQUEST, request)
                    .set(ProcessContext.RESPONSE, response)
                    .set(ProcessContext.REQUEST_URL, path);

            handlerInvoker.invokeHandler(handler);
        } catch (Exception e) {
            logger.error("Error raised in {}", e);
            WebUtil.sendError(response.SC_INTERNAL_SERVER_ERROR, e.getMessage(), response);
        } finally {
            ProcessContext.destroyContext();
        }
    }
}
