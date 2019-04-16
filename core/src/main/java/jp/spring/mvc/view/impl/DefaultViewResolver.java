package jp.spring.mvc.view.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.FileUtils;
import jp.spring.mvc.context.ProcessContext;
import jp.spring.mvc.view.AbstractViewResolver;

/**
 * Created by Administrator on 1/13/2017.
 */
@Component
public class DefaultViewResolver extends AbstractViewResolver {

    @Override
    public void toPage(String pagePath) throws Exception{
        HttpServletRequest request = ProcessContext.getRequest();
        HttpServletResponse response = ProcessContext.getResponse();

        if(getExtension().equals(".jsp")) {
            String page = getPagePath(pagePath);
            request.getRequestDispatcher(page).forward(request, response);
        } else {
            ProcessContext.getResponse().setHeader("Context-type", "text/html;charset=UTF-8");
            String page = ProcessContext.getServletContext().getRealPath( getPagePath(pagePath));
            FileUtils.copy(page, response.getOutputStream());
        }
    }

}
