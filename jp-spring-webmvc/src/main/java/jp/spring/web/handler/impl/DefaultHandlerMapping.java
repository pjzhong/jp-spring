package jp.spring.web.handler.impl;

import jp.spring.ioc.util.JpUtils;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 1/10/2017.
 * 负责映射每个HttpRequest到指定的Handler
 */
public class DefaultHandlerMapping implements HandlerMapping {

    private static final Map<String, List<Handler>> HANDLER_MAP = new ConcurrentHashMap<>();

    private static final List<Handler> PATH_VARIABLE_URL_MAP = new ArrayList<Handler>();

    @Override
    public Handler getHandler(HttpServletRequest request, String path) {
        String method = request.getMethod();

        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return match(method, path);
    }

    private Handler match(String requestMethod, String path) {
        Collection<Handler> urlMappings = HANDLER_MAP.get(path);
        if(JpUtils.isEmpty(urlMappings)) {
            for(Handler urlMapping : PATH_VARIABLE_URL_MAP) {
                if(urlMapping.match(path)) {
                    urlMappings = HANDLER_MAP.get(urlMapping.getUrl());
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
        if(JpUtils.isEmpty(handlers)) {
            return;
        }

        for(Handler handler : handlers) {
            if(null != handler) {
                if(handler.hasPathVariable()) {
                    PATH_VARIABLE_URL_MAP.add(handler);
                }

                if(!HANDLER_MAP.containsKey(handler.getUrl())) {
                    HANDLER_MAP.put(handler.getUrl(), new ArrayList<Handler>());
                }

                HANDLER_MAP.get(handler.getUrl()).add(handler);
            }
        }
    }

    public Map<String, List<Handler>> getHandlerMap() {
        return HANDLER_MAP;
    }
}
