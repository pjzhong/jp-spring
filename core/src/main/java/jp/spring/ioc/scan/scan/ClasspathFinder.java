package jp.spring.ioc.scan.scan;


import java.util.ArrayList;
import java.util.List;
import jp.spring.ioc.scan.ClassLoaderHandler;
import jp.spring.ioc.scan.URLClassLoaderHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/10/31. This is class is a toy, refactor it when you know more
 * about classLoaders todo refactor this
 */
public class ClasspathFinder {

  private final List<String> rawClassPathStrings = new ArrayList<>();

  public ClasspathFinder() {
    //for convenient, only handler sun.misc.Launcher$AppClassLoader
    ClassLoaderHandler handler = new URLClassLoaderHandler();
    for (ClassLoader loader : ClassLoaderFinder.findEvnClassLoader()) {
      try {
        handler.handle(loader, this);
      } catch (Exception e) {
        //todo say something about what happened;
      }
    }
  }

  public List<String> getRawClassPathStrings() {
    return rawClassPathStrings;
  }

  /**
   * Add a classpath element relative to a base file. May be called by a ClassLoaderHandler to add
   * classpath elements that it knows about.
   *
   * @return true (and add the classpath element) if pathElement is not null or empty, otherwise
   * return false.
   */
  public boolean addClasspathElement(final String pathElement) {
    if (StringUtils.isNotBlank(pathElement)) {
      rawClassPathStrings.add(pathElement);
      return true;
    }
    return false;
  }

}
