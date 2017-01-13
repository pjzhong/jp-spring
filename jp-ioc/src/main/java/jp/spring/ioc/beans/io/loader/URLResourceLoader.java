package jp.spring.ioc.beans.io.loader;

import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.resources.UrlResource;

import java.net.URL;
import java.util.*;

/**
 * Created by Administrator on 12/25/2016.
 */
public class URLResourceLoader implements ResourceLoader {

    @Override
    public Resource[] getResource(String location) {
        Set<Resource> resources = new LinkedHashSet<Resource>();
        URL resource = this.getClass().getClassLoader().getResource(location);
        resources.add(new UrlResource(resource));
        return resources.toArray(new Resource[resources.size()]);
    }
}
