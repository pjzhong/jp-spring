package jp.spring.ioc.beans.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 12/25/2016.
 */
public interface Resource {

  InputStream getInputStream() throws IOException;
}
