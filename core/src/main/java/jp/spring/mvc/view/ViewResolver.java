package jp.spring.mvc.view;

/**
 * Created by Administrator on 1/13/2017.
 */
public interface ViewResolver {

    String RESOLVER_NAME = ViewResolver.class + ".root";

    String DEFAULT_ENCODING = "utf-8";

    void toPage(String path) throws Exception;
}