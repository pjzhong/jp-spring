package jp.spring.web.servlet.handler;

import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public interface UrlMappingBuilder {

    public List<UrlMapping> buildUrlMapping(String name, Class<?> controller);
}
