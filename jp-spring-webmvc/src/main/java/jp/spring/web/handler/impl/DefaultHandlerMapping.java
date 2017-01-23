package jp.spring.web.handler.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import jp.spring.ioc.util.JpUtils;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerMapping;

import jp.spring.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultHandlerMapping implements HandlerMapping {

    private static final Multimap<String, Handler> URL_MAP = ArrayListMultimap.create();

    private static final List<Handler> PATH_VARIABLE_URL_MAP = new ArrayList<Handler>();

    UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public Handler getHandler(HttpServletRequest request) {
        String path = urlPathHelper.getLookupPathForRequest(request);
        String method = request.getMethod();

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return match(method, path);
    }

    private Handler match(String requestMethod, String path) {
        Collection<Handler> urlMappings = URL_MAP.get(path);
        if(JpUtils.isEmpty(urlMappings)) {
            for(Handler urlMapping : PATH_VARIABLE_URL_MAP) {
                if(urlMapping.getUrlPattern().matcher(path).find()) {
                    urlMappings = URL_MAP.get(urlMapping.getUrl());
                    break;
                }
            }
        }

        if(!JpUtils.isEmpty(urlMappings)) {
            RequestMethod[] allowedMethods;
            for(Handler urlMapping : urlMappings) {
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

    public void addHandlers(Collection<Handler> handlers) {
        for(Handler handler : handlers) {
            if(handler == null) {
                continue;
            }

            if(handler.isHasPathVariable()) {
                PATH_VARIABLE_URL_MAP.add(handler);
            }

            URL_MAP.put(handler.getUrl(), handler);
        }
    }

}
