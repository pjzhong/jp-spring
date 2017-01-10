package jp.spring.web.servlet.handler.impl;

import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.servlet.handler.UrlMapping;
import jp.spring.web.servlet.handler.UrlMappingBuilder;
import jp.spring.web.util.UrlPathHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 1/10/2017.
 */
public class DefaultUrlMappingBuilder implements UrlMappingBuilder{

    public DefaultUrlMappingBuilder() {}

    @Override
    public UrlMapping buildUrlMapping(String name, Class<?> controller) {
        String[] urls = null;
        String clazzUrl = "";

        if(JpUtils.isAnnotated(controller, RequestMapping.class)) {
            urls = controller.getAnnotation(RequestMapping.class).value();
            if(StringUtils.isEmpty(urls) || urls.length > 1) {
                throw new IllegalArgumentException("Incorrect use of @RequestMapping on " + controller);
            } else {
                clazzUrl = urls[0];
            }
        }

        Method[] methods = controller.getMethods();
        UrlMapping urlMapping = null;
        for(Method method : methods) {
            if(JpUtils.isAnnotated(method, RequestMapping.class)) {
                method.setAccessible(true);
                urls = method.getAnnotation(RequestMapping.class).value();
                boolean hasUrl = false;
                for(String url : urls) {
                    try {
                       buildUrlMapping(name, method, clazzUrl, url);
                        hasUrl = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(!hasUrl) {
                    urlMapping = new UrlMapping(method, name);
                }
            }
        }

        return urlMapping;
    }

    private static UrlMapping buildUrlMapping(String beanName, Method method, String classUrl, String url) {
        UrlMapping urlMapping = new UrlMapping(method, beanName);

        if(UrlPathHelper.PATTERN_PATH_VARIABLE.matcher(url).find()) {
            Annotation[][] methodParamAnnos = method.getParameterAnnotations();
            if(!JpUtils.isEmpty(methodParamAnnos)) {
                Map<String, Integer> pathVariableMap = new HashMap<>();
                Annotation[] paramAnnos = null;
                Class<?>[] paramTypes = method.getParameterTypes();
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
                urlMapping.setUrl(classUrl + url);
                urlMapping.setUrlExpression("^" + classUrl + urlExpression + "$");
                urlMapping.setPathVariableMap(pathVariableMap);
            } else {
                urlMapping.setUrl(classUrl + url);
            }
        }
        return urlMapping;
    }
}
