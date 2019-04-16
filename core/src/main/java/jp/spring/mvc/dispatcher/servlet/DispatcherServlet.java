package jp.spring.mvc.dispatcher.servlet;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.mvc.context.ProcessContext;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.handler.HandlerInvoker;
import jp.spring.mvc.handler.HandlerMapping;
import jp.spring.mvc.handler.MultipartResolver;
import jp.spring.mvc.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 1/3/2017.
 */
@WebServlet(loadOnStartup = 1, urlPatterns = "/")
public class DispatcherServlet extends FrameworkServlet {

  private final static Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

  private static WebApplicationContext webApplicationContext;

  private static HandlerMapping handlerMapping;

  private static HandlerInvoker handlerInvoker;

  private static MultipartResolver multipartResolver = null;

  @Override
  public void init() {
    try {
      WebIocInit.init(getServletContext());
      ProcessContext.setServletContext(getServletContext());
      webApplicationContext = (WebApplicationContext) getServletContext()
          .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
      handlerMapping = (HandlerMapping) webApplicationContext
          .getBean(handlerMapping.DEFAULT_HANDLER_MAPPING);
      handlerInvoker = (HandlerInvoker) webApplicationContext
          .getBean(HandlerInvoker.DEFAULT_HANDLER_INVOKER);
      multipartResolver = (MultipartResolver) webApplicationContext
          .getBean(MultipartResolver.DEFAULT_MULTI_PART_RESOLVER);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  @Override
  protected void doService(HttpServletRequest request, HttpServletResponse response) {
    try {
      String path = WebUtil.getLookupPathForRequest(request);
      Handler handler = handlerMapping.getHandler(request, path);
      if (handler == null) {
        response.sendError(response.SC_NOT_FOUND, " Not Found");
        logger.error(path + " Not Found. Request From:" + request.getRemoteAddr());
        return;
      }

      //Is multiPart request?
      if (multipartResolver.isMultiPart(request)) {
        request = multipartResolver.resolveMultipart(request);
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
