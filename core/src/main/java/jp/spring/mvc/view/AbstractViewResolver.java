package jp.spring.mvc.view;

import jp.spring.ioc.beans.factory.annotation.Value;

/**
 * Created by Administrator on 2/12/2017.
 */
public abstract class AbstractViewResolver implements ViewResolver {
    @Value(value = "page.folder", required = true)
    private String folder = "/";

    @Value(value = "page.extension", required = true)
    private String extension = ".jsp";

    public String getPagePath(String pagePath) {
        return getFolder() + (pagePath.startsWith("/") ? pagePath.substring(1): pagePath) + getExtension();
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
