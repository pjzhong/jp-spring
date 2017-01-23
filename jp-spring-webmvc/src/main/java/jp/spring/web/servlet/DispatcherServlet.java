package jp.spring.web.servlet;


import jp.spring.ioc.context.WebApplicationContext;

import jp.spring.web.context.ProcessContext;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.handler.HandlerMapping;

import jp.spring.web.util.FileUtils;
import jp.spring.web.util.UrlPathHelper;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 1/3/2017.
 */
public class DispatcherServlet extends FrameworkServlet {

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
            System.out.println("DispatcherServlet init failed");
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = urlPathHelper.getLookupPathForRequest(request);

        if(isStaticResource(response, path)) {
            return;
        }

        Handler handler = handlerMapping.getHandler(request);
        if(handler == null) {
            response.setStatus(response.SC_NOT_FOUND);
            return;
        }

        //Build context
        ProcessContext
                .buildContext()
                .set(ProcessContext.REQUEST, request)
                .set(ProcessContext.RESPONSE, response)
                .set(ProcessContext.REQUEST_URL, path);
        try {
            handlerInvoker.invokeHandler(handler);
        } catch (Exception e) {

        } finally {
            ProcessContext.destoryContext();
        }

    }

    protected boolean isStaticResource(HttpServletResponse response, String path) {
        int index = path.lastIndexOf(".");
        if(index > -1 && index < path.length() - 1) {
            String ext = path.substring(index + 1).toLowerCase();

            if(FileUtils.ALLOWED_EXTENSION.contains(ext)) {
                response.setHeader("Content-type", FileUtils.getMimeType(ext) + ";charset=UTF-8");
                String fileLocation = this.getServletContext().getRealPath(path);
                try {
                    FileUtils.copy(fileLocation, response.getOutputStream());
                } catch (IOException e) {
                    response.setStatus(response.SC_NOT_FOUND);
                }
                return true;
            }
        }
        return false;
    }
}
