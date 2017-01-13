package jp.spring.web.view;

import jp.spring.web.context.ProcessContext;
import jp.spring.web.util.FileUtils;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by Administrator on 1/13/2017.
 */
public class DefaultViewResolver implements ViewResolver {

    private String folder;

    private String extension = ".jsp";

    @Override
    public ViewResolver getResolver() {
        return this;
    }

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
        return folder + (pagePath.startsWith("/") ? pagePath.substring(1) : pagePath) + extension;
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
        this.extension = extension;
    }
}
