package jp.spring.ioc.beans.io.resources;

import jp.spring.ioc.beans.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Administrator on 1/23/2017.
 */
public class PropertiesResource implements Resource{

    private File file;

    private Properties properties;

    public PropertiesResource() {}

    public PropertiesResource(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
