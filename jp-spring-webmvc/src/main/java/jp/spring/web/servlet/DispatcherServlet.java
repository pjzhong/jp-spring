package jp.spring.web.servlet;

import jp.spring.web.servlet.handler.UrlMapping;
import jp.spring.web.servlet.handler.impl.DefaultUrlMappingBuilder;
import jp.spring.web.util.FileUtils;
import jp.spring.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by Administrator on 1/3/2017.
 */
public class DispatcherServlet extends FrameworkServlet {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public void init() {

    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) {
        String path = urlPathHelper.getLookupPathForRequest(request);

        if(isStaticResource(response, path)) {
            return;
        }

        UrlMapping urlMapping = DefaultUrlMappingBuilder.

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
