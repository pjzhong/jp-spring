package jp.spring.web.servlet.handler.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jp.spring.ioc.util.JpUtils;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.servlet.handler.UrlHandlerMapping;
import jp.spring.web.servlet.handler.UrlMapping;
import jp.spring.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultUrlHandlerMapping implements UrlHandlerMapping {

    private static final Multimap<String, UrlMapping> URL_MAP = ArrayListMultimap.create();

    private static final List<UrlMapping> PATHVARIABLE_URL_MAP = new ArrayList<UrlMapping>();

    UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public UrlMapping getUrlMapping(HttpServletRequest request) {
        String path = urlPathHelper.getLookupPathForRequest(request);
        String method = request.getMethod();

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return match(method, path);
    }

    private UrlMapping match(String requestMethod, String path) {
        Collection<UrlMapping> urlMappings = URL_MAP.get(path);
        if(JpUtils.isEmpty(urlMappings)) {
            for(UrlMapping urlMapping : PATHVARIABLE_URL_MAP) {
                if(urlMapping.getUrlPattern().matcher(path).find()) {
                    urlMappings = URL_MAP.get(urlMapping.getUrl());
                    break;
                }
            }
        }

        if(!JpUtils.isEmpty(urlMappings)) {
            RequestMethod[] allowedMethods;
            for(UrlMapping urlMapping : urlMappings) {
                allowedMethods = urlMapping.getMethod().getAnnotation(RequestMapping.class).method();

                for(RequestMethod allowedMethod : allowedMethods) {
                    if(allowedMethod.name().equals(requestMethod)) {
                        return urlMapping;
                    }
                }
            }
        }

        return null;
    }

    public void addUrlMappings(List<UrlMapping> urlMappings) {
        for(UrlMapping urlMapping : urlMappings) {
            if(urlMapping == null) {
                continue;
            }
            if(urlMapping.isHasPathVariable()) {
                PATHVARIABLE_URL_MAP.add(urlMapping);
            }

            URL_MAP.put(urlMapping.getUrl(), urlMapping);;
        }
    }
}
