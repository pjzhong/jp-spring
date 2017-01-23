package jp.spring.ioc.beans.io.loader;

import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.resources.PropertiesResource;
import jp.spring.ioc.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/23/2017.
 */
public class PropertiesResourceLoader implements ResourceLoader {

    @Override
    public Resource[] getResource(String strLocation) {

        String[] locations = strLocation.split(";");

        List<PropertiesResource> propertiesResources = new ArrayList<>();
        List<File> temp = null;
        for(String location : locations) {
            temp = FileUtils.findFiles(location, ".properties");
            for(File file : temp) {
                propertiesResources.add(new PropertiesResource(file));
            }
        }

        return propertiesResources.toArray(new PropertiesResource[propertiesResources.size()]);
    }
}