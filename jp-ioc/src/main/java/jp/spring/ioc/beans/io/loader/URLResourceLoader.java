package jp.spring.ioc.beans.io.loader;

import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.resources.UrlResource;

import java.net.URL;

/**
 * Created by Administrator on 12/25/2016.
 */
public class URLResourceLoader implements ResourceLoader {

    @Override
    public Resource getResource(String location) {
        URL resource = this.getClass().getClassLoader().getResource(location);
        return new UrlResource(resource);
    }
}
