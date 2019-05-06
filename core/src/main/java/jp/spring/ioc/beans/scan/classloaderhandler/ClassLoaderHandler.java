package jp.spring.ioc.beans.scan.classloaderhandler;


import jp.spring.ioc.beans.scan.scanner.ClasspathFinder;

/**
 * Created by Administrator on 2017/10/31.
 */
public interface ClassLoaderHandler {
    boolean handle(final ClassLoader classloader, final ClasspathFinder classpathFinder) throws Exception;
}
