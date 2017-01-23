package jp.spring.web.handler.impl;

import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.*;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerMappingBuilder;
import jp.spring.web.handler.support.RequestMethodParameter;
import jp.spring.web.util.UrlPathHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultHandlerMappingBuilder implements HandlerMappingBuilder {

    public DefaultHandlerMappingBuilder() {}

    @Override
    public List<Handler> buildHandler(String name, Class<?> controller) {
        if(!JpUtils.isAnnotated(controller, Controller.class)) {
            return null;
        }

        String[] urls = null;
        String clazzUrl = "";

        if(JpUtils.isAnnotated(controller, RequestMapping.class)) {
           urls = controller.getAnnotation(RequestMapping.class).value();
            if(StringUtils.isEmpty(urls) || urls.length > 1) {
                throw new IllegalArgumentException("Incorrect use of @RequestMapping on " + controller);
            } else {
                clazzUrl = urls[0];
                if(!StringUtils.isEmpty(clazzUrl) && !clazzUrl.startsWith("/")) {
                    clazzUrl = "/" + clazzUrl ;
                }
            }
        }

        List<Handler> handlers = new ArrayList<>();
        Method[] methods = controller.getMethods();
        Handler handler = null;
        for(Method method : methods) {
            if(JpUtils.isAnnotated(method, RequestMapping.class)) {
                method.setAccessible(true);
                urls = method.getAnnotation(RequestMapping.class).value();
                boolean hasUrl = false;
                handler = new Handler(method, name);
                for(String url : urls) {
                    try {
                        if(!StringUtils.isEmpty(url)) {
                            if(!url.startsWith("/")) {
                                url = "/" + url;
                            }
                            handler = buildHandler(handler, clazzUrl, url);
                            handlers.add(handler);
                            hasUrl = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(!hasUrl) {
                    handler.setUrl(clazzUrl + method.getName());
                    handlers.add(handler);
                }
            }
        }

        return handlers;
    }

    private Handler buildHandler(Handler handler, String classUrl, String url) {
        if(UrlPathHelper.PATTERN_PATH_VARIABLE.matcher(url).find()) {
            Annotation[][] methodParamAnnos = handler.getMethod().getParameterAnnotations();
            if(!JpUtils.isEmpty(methodParamAnnos)) {
                Map<String, Integer> pathVariableMap = new HashMap<>();
                Annotation[] paramAnnos = null;
                Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
                Class<?> paramType;

                int index = 1; //the regex index in url;
                String key = null, regexExp = null;
                String urlExpression = url;
                for(int i = 0; i < methodParamAnnos.length; i++) {
                    paramAnnos = methodParamAnnos[i];
                    for(Annotation annotation : paramAnnos) {
                        if(PathVariable.class.equals(annotation.annotationType())) {
                            paramType = paramTypes[i];
                            key = ((PathVariable) annotation).value();
                            pathVariableMap.put(key, index);

                            if(String.class.equals(paramType)) {
                                regexExp = "([^/])";
                            } else if (Integer.TYPE.equals(paramType) || Integer.class.equals(paramType)
                                    || Long.TYPE.equals(paramType) || Long.class.equals(paramType)) {
                                regexExp = "([-+]?\\d+)";
                            } else if (Double.TYPE.equals(paramType) || Double.class.equals(paramType)
                                    || Float.TYPE.equals(paramType) || Float.class.equals(paramType)) {
                                regexExp = "([-+]?\\d+(\\.\\d+))";
                                index++;
                            } else if (Boolean.TYPE.equals(paramType) || Boolean.class.equals(paramType)) {
                                regexExp = "(true|false|y|n|yes|no|1|0)";
                            }
                            index++;
                            urlExpression = urlExpression.replace("{" + key + "}", regexExp);
                            break;// each parameter can only be attached one @PathVariable
                        }
                    }
                }
                handler.setUrl(classUrl + url);
                handler.setUrlExpression("^" + classUrl + urlExpression + "$");
                handler.setPathVariableMap(pathVariableMap);
            }
        } else {
            handler.setUrl(classUrl + url);
        }

        buildHandlerParameter(handler);
        return handler;
    }

    private Handler buildHandlerParameter(Handler handler) {
        Annotation[][] paramAnnotations = handler.getMethod().getParameterAnnotations();
        if(!JpUtils.isEmpty(paramAnnotations)) {
            Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
            handler.setRequestMethodParameters(new ArrayList<RequestMethodParameter>());

            for(int i = 0; i < paramAnnotations.length; i++) {
                buildHandlerParameter(handler, paramTypes[i], paramAnnotations[i]);
            }
        }

        return handler;
    }

    private Handler buildHandlerParameter(Handler handler, Class<?> paramType, Annotation[] annotation) {
        List<RequestMethodParameter> methodParameters = handler.getRequestMethodParameters();

        RequestMethodParameter parameter = new RequestMethodParameter();
        parameter.setType(paramType);
        parameter.setPrimitiveType(JpUtils.isPrimietive(paramType));
        if(annotation != null && annotation.length > 0) {
            Class<?> annotationType = annotation[0].annotationType();
            if(annotationType.equals(RequestParam.class)
                    || annotationType.equals(PathVariable.class)
                    || annotationType.equals(RequestHeader.class)
                    || annotationType.equals(CookieValue.class)) {
                parameter.setAnnotation(annotation[0]);
                Method valueMethod = JpUtils.findMethod(annotation[0].getClass(), "value");
                parameter.setValueMethod(valueMethod);
            }
        }
        methodParameters.add(parameter);

        return handler;
    }
}
