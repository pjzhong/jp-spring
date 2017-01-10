package jp.spring.web.servlet.handler;

/**
 * Created by Administrator on 1/10/2017.
 */
public interface UrlMappingBuilder {

    public UrlMapping buildUrlMapping(String name, Class<?> controller);
}
