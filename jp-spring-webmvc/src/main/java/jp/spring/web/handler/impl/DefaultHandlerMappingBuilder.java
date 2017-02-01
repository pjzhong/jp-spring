package jp.spring.web.handler.impl;

import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.*;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerMappingBuilder;
import jp.spring.web.support.RequestMethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultHandlerMappingBuilder implements HandlerMappingBuilder {

    private final Pattern PATTERN_PATH_VARIABLE = Pattern.compile("(\\{([^}]+)\\})");

    public DefaultHandlerMappingBuilder() {}

    @Override
    public List<Handler> buildHandler(String name, Class<?> controller) {
        if(!JpUtils.isAnnotated(controller, Controller.class)) {
            return null;
        }

        String[] urls = null;
        String clazzUrl = "/";

        //处理Class级别的URL
        if(JpUtils.isAnnotated(controller, RequestMapping.class)) {
           urls = controller.getAnnotation(RequestMapping.class).value();
            if(StringUtils.isEmpty(urls) || urls.length > 1) {
                throw new IllegalArgumentException("Incorrect use of @RequestMapping on " + controller);
            } else {
                clazzUrl = urls[0];
                if( (!StringUtils.isEmpty(clazzUrl)) && !clazzUrl.startsWith("/")) {
                    clazzUrl = "/" + clazzUrl ;
                }
            }
        }

        List<Handler> handlers = new ArrayList<>();
        Method[] methods = controller.getMethods();
        Handler handler = null;
        for(Method method : methods) {
            if(JpUtils.isAnnotated(method, RequestMapping.class)) {
                handler = new Handler(method, name);
                urls = method.getAnnotation(RequestMapping.class).value();
                if(StringUtils.isEmpty(urls)) {//urls为空，取方法名作默认url
                    handler = buildHandler(handler, clazzUrl, null);
                    handlers.add(handler);
                } else {
                    for(String url : urls) {
                        try {
                            if(!StringUtils.isEmpty(url)) {
                                if(url.startsWith("/")) {
                                    url = url.substring(1);
                                }
                                if(url.endsWith("/")) {
                                    url = url.substring(0, url.length() - 1);
                                }
                                handler = buildHandler(handler, clazzUrl, url);
                                handlers.add(handler);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return handlers;
    }

    private Handler buildHandler(Handler handler, String classUrl, String methodUrl) {
        Annotation[][] paramAnnotations = handler.getMethod().getParameterAnnotations();

        if(StringUtils.isEmpty(methodUrl)) {
            handler.setUrl(classUrl);
        } else if (PATTERN_PATH_VARIABLE.matcher(methodUrl).find()) {
            buildRegexUrl(handler, classUrl, methodUrl, paramAnnotations);
        } else {
            handler.setUrl(classUrl + methodUrl);
        }

        // return Json or not?
        if(JpUtils.isAnnotated(handler.getMethod(), ResponseBody.class) || !(handler.getMethod().getReturnType() == String.class) ) {
            handler.setResponseBody(true);
        }

        buildHandlerParameter(handler,paramAnnotations);
        return handler;
    }

    /**
     * 开始创建regexUrl：
     * @ReqquestMapping("/example/{one}/{two}")
     * method(@PathVariable('one") Integer, @PatVariable("two") Float)
     *
     * 将会:/example/([-+]?\d+)/([-+]?\d+(\.\d+))
     * */
    private void buildRegexUrl(Handler handler, String classUrl, String url, Annotation[][] paramAnnotations) {
        if(!JpUtils.isEmpty(paramAnnotations)) {
            Map<String, Integer> pathVariableMap = new HashMap<>();
            Annotation[] annotations = null;
            Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
            Class<?> paramType;

            int index = 1; //the regex index in url;
            String key = null, regexExp = null;
            String urlExpression = url;
            for(int i = 0; i < paramAnnotations.length; i++) {
                annotations = paramAnnotations[i];
                for(Annotation annotation : annotations) {
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
    }

    /**
     * 反射请求方法的参数，并封装进RequestMethodParameter
     * */
    private Handler buildHandlerParameter(Handler handler, Annotation[][] paramAnnotations) {
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
        RequestMethodParameter parameter = new RequestMethodParameter();
        parameter.setType(paramType);
        parameter.setPrimitiveType(JpUtils.isPrimietive(paramType));
        if(!JpUtils.isEmpty(annotation) && parameter.isPrimitiveType()) {
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
        handler.getRequestMethodParameters().add(parameter);

        return handler;
    }
}
