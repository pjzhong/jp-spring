package jp.spring.ioc.scan.scan;


import java.util.ArrayList;
import java.util.List;
import jp.spring.ioc.scan.ClassLoaderHandler;
import jp.spring.ioc.scan.URLClassLoaderHandler;

/**
 * Created by Administrator on 2017/10/31. This is class is a toy, refactor it when you know more
 * about classLoaders todo refactor this
 */
public class ClasspathFinder {

  private final List<String> rawClassPathStrings = new ArrayList<>();

  public ClasspathFinder(ScanConfig config) {
    //for convenient, only handler sun.misc.Launcher$AppClassLoader
    ClassLoaderHandler handler = new URLClassLoaderHandler();
    for (ClassLoader loader : config.getLoaders()) {
      try {
        rawClassPathStrings.addAll(handler.handle(loader));
      } catch (Exception e) {
        //todo say something about what happened;
      }
    }
  }

  public List<String> getRawClassPathStrings() {
    return rawClassPathStrings;
  }
}
