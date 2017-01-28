package jp.spring.web.view;

/**
 * Created by Administrator on 1/13/2017.
 */
public interface ViewResolver {

    String RESOLVER_NAME = ViewResolver.class + ".root";

    void toPage(String path) throws Exception;
}
