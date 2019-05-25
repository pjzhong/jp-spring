package jp.spring.ioc.scan;


import jp.spring.ioc.scan.scan.ClasspathFinder;

/**
 * Created by Administrator on 2017/10/31.
 */
public interface ClassLoaderHandler {
    boolean handle(final ClassLoader classloader, final ClasspathFinder classpathFinder) throws Exception;
}
