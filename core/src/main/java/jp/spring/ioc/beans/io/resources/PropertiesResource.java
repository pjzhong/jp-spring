package jp.spring.ioc.beans.io.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import jp.spring.ioc.beans.io.Resource;

/**
 * Created by Administrator on 1/23/2017.
 */
public class PropertiesResource implements Resource {

  private File file;

  private Properties properties;

  public PropertiesResource() {
  }

  public PropertiesResource(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }


  @Override
  public InputStream getInputStream() throws IOException {
    return new FileInputStream(file);
  }
}
