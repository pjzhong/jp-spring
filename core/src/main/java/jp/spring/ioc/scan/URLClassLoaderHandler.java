package jp.spring.ioc.scan;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */
public class URLClassLoaderHandler implements ClassLoaderHandler {

  private boolean WINDOWS = File.separatorChar == '\\';

  @Override
  public List<String> handle(ClassLoader classloader) {
    List<String> res = new ArrayList<>();
    if (classloader instanceof URLClassLoader) {
      URL[] urls = ((URLClassLoader) classloader).getURLs();
      if (urls == null) {
        return Collections.emptyList();
      }

      for (URL url : urls) {
        String filePath = url.getFile();
        //Handle Windows path starting with a drive designation as an absolute path
        if (WINDOWS) {
          if (1 < filePath.length()
              && (filePath.startsWith("/") || filePath.startsWith("\\"))
              && Character.isLetter(filePath.charAt(1))
          ) {
            filePath = filePath.substring(1);
          }
        }

        try {
          filePath = URLDecoder.decode(filePath, "utf-8");
          res.add(filePath);
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }

      }
    }
    return res;
  }


}
