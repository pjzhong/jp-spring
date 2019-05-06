package jp.spring.ioc.beans.scan.classloaderhandler;

import java.net.URL;
import java.net.URLClassLoader;
import jp.spring.ioc.beans.scan.scanner.ClasspathFinder;

/**
 * Created by Administrator on 2017/10/31.
 */
public class URLClassLoaderHandler implements ClassLoaderHandler {

  @Override
  public boolean handle(final ClassLoader classloader, final ClasspathFinder classpathFinder) {
    boolean handled = false;
    if (classloader instanceof URLClassLoader) {
      final URL[] urls = ((URLClassLoader) classloader).getURLs();
      if (urls != null) {
        for (final URL url : urls) {
          if (url != null) {
            handled = classpathFinder.addClasspathElement(url.toString());
          }
        }
      }
    }
    return handled;
  }
}
