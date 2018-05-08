package jp.spring.web.handler.impl;

import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.*;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerMappingBuilder;
import jp.spring.web.support.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/10/2017.
 * 负责在mvc模块启动的时候，创建handler并交给HandlerMapping来负责映射
 */
public class DefaultHandlerMappingBuilder implements HandlerMappingBuilder {

    private final Pattern PATTERN_PATH_VARIABLE = Pattern.compile("(\\{([^}]+)\\})");

    public DefaultHandlerMappingBuilder() {}

    /**
     * URL on class level default is '/' and
     * on method level  is '' (If use did not provide a specified vale).
     *
     * */
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
                if(!StringUtils.isEmpty(clazzUrl)) {//classUrl 永远以 "/"， 结尾不管
                    if(!clazzUrl.startsWith("/")) {
                        clazzUrl = "/" + clazzUrl ;
                    }

                    if(clazzUrl.endsWith("/")) {
                        clazzUrl = clazzUrl.substring(0, clazzUrl.length() - 1);
                    }
                } else {
                    clazzUrl = "/";
                }
            }
        }

        List<Handler> handlers = new ArrayList<>();
        Method[] methods = controller.getMethods();
        Handler handler = null;
        for(Method method : methods) {
            if(JpUtils.isAnnotated(method, RequestMapping.class)) {
                urls = method.getAnnotation(RequestMapping.class).value();
                if(StringUtils.isEmpty(urls)) {//urls为空，取方法名作默认url
                    handler = new Handler(method, name);
                    handler = buildHandler(handler, clazzUrl, null);
                    handlers.add(handler);
                } else {
                    for(String url : urls) {
                        try {
                            handler = new Handler(method, name);
                            handler = buildHandler(handler, clazzUrl, url);
                            handlers.add(handler);
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

        if(!StringUtils.isEmpty(methodUrl)) {
            if(methodUrl.startsWith("/") ) {
                methodUrl = methodUrl.substring(1, methodUrl.length());
            }
            if(methodUrl.endsWith("/")) {
                methodUrl = methodUrl.substring(0, methodUrl.length() - 1);
            }
            if(!classUrl.endsWith("/")) {
                classUrl = classUrl + "/";
            }
            if(PATTERN_PATH_VARIABLE.matcher(methodUrl).find()) {
                buildRegexUrl(handler, classUrl, methodUrl, paramAnnotations);
            }
            handler.setUrl(classUrl + methodUrl);
        } else {
            handler.setUrl(classUrl);
        }

        // return Json or not?
        if(JpUtils.isAnnotated(handler.getMethod(), ResponseBody.class)
                || (handler.getMethod().getReturnType() != String.class) ) {
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
     * 将会变成:/example/([-+]?\d+)/([-+]?\d+(\.\d+))
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
            handler.setUrlExpression("^" + classUrl + urlExpression + "$");
            handler.setPathVariableMap(pathVariableMap);
        }
    }

    /**
     * 反射请求方法的参数，并封装进RequestMethodParameter
     * */
    private Handler buildHandlerParameter(Handler handler, Annotation[][] paramAnnotations) {
        if(!JpUtils.isEmpty(paramAnnotations)) {
            int length = handler.getMethod().getParameters().length;
            handler.setMethodParameters(new ArrayList<MethodParameter>());

            for(int i = 0; i < length; i++) {
                buildHandlerParameter(handler, i);
            }
        }
        return handler;
    }

    private Handler buildHandlerParameter(Handler handler, int index) {
        Method method = handler.getMethod();
        MethodParameter parameter = new MethodParameter();

        Class<?> parameterType = method.getParameterTypes()[index];
        /**设置属性*/
        parameter.setMethod(handler.getMethod());
        parameter.setParameterIndex(index);
        parameter.setParameterType(parameterType);
        parameter.setPrimitiveType(JpUtils.isSimpleType(parameterType));
        if(Collection.class.isAssignableFrom(parameterType)) {
            ParameterizedType type = (ParameterizedType) method.getGenericParameterTypes()[index];
            Class<?> actualType = (Class<?>) type.getActualTypeArguments()[0];
            parameter.setGenericType(actualType);
        }

        /**处理Annotation
         * 目前只允许参数只能有一个标记，多了会无效
         * */
        Annotation[] annotation = method.getParameterAnnotations()[index];
        if (!JpUtils.isEmpty(annotation)) {
            Method valueMethod = JpUtils.findMethod(annotation[0].getClass(), "value");
            String name = null;
            try {
                if (valueMethod != null) {
                    valueMethod.setAccessible(true);
                    name = (String) valueMethod.invoke(annotation[0], null);
                    parameter.setName(name);
                    parameter.setAnnotation(annotation[0]);
                }
            } catch (Exception e) {/*ignore it*/}
        }

        handler.getMethodParameters().add(parameter);
        return handler;
    }
}
