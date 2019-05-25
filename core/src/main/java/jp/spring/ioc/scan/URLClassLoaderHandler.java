package jp.spring.ioc.scan;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */
public class URLClassLoaderHandler implements ClassLoaderHandler {

  @Override
  public List<String> handle(ClassLoader classloader) {
    List<String> res = new ArrayList<>();
    if (classloader instanceof URLClassLoader) {
      URL[] urls = ((URLClassLoader) classloader).getURLs();
      if (urls != null) {
        for (URL url : urls) {
            res.add(url.toString());

        }
      }
    }
    return res;
  }
}
