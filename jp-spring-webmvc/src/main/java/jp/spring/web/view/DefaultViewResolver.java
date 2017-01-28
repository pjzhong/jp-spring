package jp.spring.web.view;

import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.FileUtils;
import jp.spring.web.context.ProcessContext;

/**
 * Created by Administrator on 1/13/2017.
 */
@Component
public class DefaultViewResolver implements ViewResolver {

    @Value(value = "page.folder", required = true)
    private String folder = "/";

    @Value(value = "page.extension", required = true)
    private String extension = ".jsp";

    public void toPage(String pagePath) throws Exception{
        if(extension.equals(".jsp")) {
            String page = getPage(pagePath);
            ProcessContext.getRequest().getRequestDispatcher(page)
                    .forward(ProcessContext.getRequest(), ProcessContext.getResponse());
        } else {
            ProcessContext.getResponse().setHeader("Context-type", "text/html;charset=UTF-8");
            String page = ProcessContext.getRequest().getServletContext().
                    getRealPath( getPage(pagePath));
            FileUtils.copy(page, ProcessContext.getResponse().getOutputStream());
        }
    }

    private String getPage(String pagePath) {
        return folder + (pagePath.startsWith("/") ? pagePath.substring(1) : pagePath) + getExtension();
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        if(!folder.endsWith("/")) {
            folder = folder + "/";
        }

        if(!folder.startsWith("/")) {
            folder = "/" + folder;
        }

        this.folder = folder;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        if(!extension.startsWith(".")) {
            extension = "." + extension;
        }
        this.extension = extension;
    }
}
