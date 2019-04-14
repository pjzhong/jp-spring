package jp.spring.web.handler.impl;

import com.alibaba.fastjson.JSON;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerArgResolver;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.interceptor.Interceptor;
import jp.spring.web.util.WebUtil;
import jp.spring.web.view.ViewResolver;

/**
 * Created by Administrator on 1/23/2017.
 */
public class DefaultHandlerInvoker implements HandlerInvoker {

    private boolean isInitialized = false;

    private ViewResolver viewResolver = null;

    private HandlerArgResolver argResolver;

    private String REDIRECT = "redirect:";

    public void init() {
        if(isInitialized) {
            return;
        }

        WebApplicationContext applicationContext = WebUtil.getWebContext();
        try {
            viewResolver =  (ViewResolver) applicationContext.getBean(ViewResolver.RESOLVER_NAME);
            argResolver = new DefaultHandlerArgResolver();
            isInitialized = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void invokeHandler(Handler handler) throws Exception {
        init();// initialize first

        HttpServletRequest request = ProcessContext.getRequest();
        HttpServletResponse response = ProcessContext.getResponse();

        Object result;
        Object controller;
        boolean flag = false;
        for(Interceptor interceptor : handler.getInterceptors()) { // run the filters
            flag = interceptor.beforeHandle(request, response, handler);
            if(!flag) { return; }
        }
        controller = WebUtil.getWebContext().getBean(handler.getBeanName());
        Object[] args = argResolver.resolve(handler);
        result = handler.invoker(controller, args);
        for(Interceptor interceptor :  handler.getInterceptors()) { //run the filters
            interceptor.afterHandle(request, response, handler);
        }


        if(handler.isResponseBody()) {
            response.setHeader("Context-type", "application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(result));
        } else {
            String pagePath = (String) result;
            if(!StringUtils.isEmpty(pagePath)) {
                if(pagePath.startsWith(REDIRECT)) {
                    String url = pagePath.substring(REDIRECT.length());
                    url = url.startsWith("/") ? url : "/" + url;
                    url = request.getContextPath() + url;
                    response.sendRedirect(url);
                } else {
                    viewResolver.toPage(pagePath);
                }
            }
        }
    }

}
