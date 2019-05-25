package jp.spring.ioc.scan;


import java.util.List;

/**
 * Created by Administrator on 2017/10/31.
 */
public interface ClassLoaderHandler {

  List<String> handle(final ClassLoader classloader) throws Exception;
}
