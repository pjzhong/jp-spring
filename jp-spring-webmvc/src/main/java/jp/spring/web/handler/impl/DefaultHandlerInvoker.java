package jp.spring.web.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.*;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.handler.MultipartResolver;
import jp.spring.web.interceptor.Interceptor;
import jp.spring.web.support.MultiPartRequest;
import jp.spring.web.support.MultipartFiles;
import jp.spring.web.support.RequestMethodParameter;
import jp.spring.web.util.WebUtil;
import jp.spring.web.view.ViewResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/23/2017.
 */
public class DefaultHandlerInvoker implements HandlerInvoker {

    private boolean isInitialized = false;

    private ViewResolver viewResolver = null;
    private MultipartResolver multipartResolver = null;
    private String REDIRECT = "redirect:";

    public void init() {
        if(isInitialized) {
            return;
        }

        WebApplicationContext applicationContext = WebUtil.getWebContext();
        try {
            viewResolver =  (ViewResolver) applicationContext.getBean(ViewResolver.RESOLVER_NAME);
            multipartResolver = (MultipartResolver) applicationContext.getBean(MultipartResolver.DEFAULT_MULTI_PART_RESOLVER);
            isInitialized = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void invokeHandler(Handler handler) throws Exception {
        init();// initialize first

        HttpServletRequest request = ProcessContext.getRequest();
        HttpServletResponse response = ProcessContext.getResponse();

        //Is multiPart request?

        if(multipartResolver.isMultiPart(request)) {
            request = multipartResolver.resolveMultipart(request);
            ProcessContext.getContext().set(ProcessContext.REQUEST, request);
        }


        Object result;
        Object controller;
        boolean flag = false;
        for(Interceptor interceptor : handler.getInterceptors()) {
            flag = interceptor.beforeHandle(request, response, handler);
            if(!flag) {
                return;
            }
        }
        controller = WebUtil.getWebContext().getBean(handler.getBeanName());
        result = handler.invoker(controller, autowiredParameter(handler));
        for(Interceptor interceptor :  handler.getInterceptors()) {
            interceptor.afterHandle(request, response, handler);
        }


        if(handler.isResponseBody()) {
            response.setHeader("Context-type", "application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(result));
        } else {
            String pagePath = (String) result;
            if(!StringUtils.isEmpty(pagePath)) {
                if(pagePath.startsWith(REDIRECT)) {
                    response.sendRedirect(pagePath.substring(REDIRECT.length()));
                } else {
                    viewResolver.toPage(pagePath);
                }
            }
        }
    }


    protected Object[] autowiredParameter(Handler handler) throws Exception {
        if(JpUtils.isEmpty(handler.getRequestMethodParameters())) {
            return null;
        }

        List<RequestMethodParameter> methodParameters = handler.getRequestMethodParameters();
        Object[] paras = new Object[methodParameters.size()];

        for(int i = 0; i < methodParameters.size(); i++) {
            paras[i] = autowiredParameter(handler, methodParameters.get(i));
        }

        return paras;
    }

    /**
     * 开始进行参数注入
     * @param handler
     * @param  parameter
     * */
    protected Object autowiredParameter(Handler handler, RequestMethodParameter parameter) throws Exception {
         if(HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
             return ProcessContext.getRequest();
         } else if(HttpServletResponse.class.isAssignableFrom(parameter.getType())) {
             return ProcessContext.getResponse();
         } else  if(HttpSession.class.isAssignableFrom(parameter.getType())) {
             return ProcessContext.getSession();
         } else if(MultipartFiles.class.isAssignableFrom(parameter.getType())) {
             if(ProcessContext.getRequest() instanceof MultiPartRequest) {// return upload file
                 return ((MultiPartRequest) ProcessContext.getRequest()).getMultipartFiles();
             }
         }

        if(parameter.isPrimitiveType() && parameter.isHasAnnotation()) {
            String name = null, value = null;
            //因为UrlMapping都是UrlMappingBuilder创造的，所以确保了有value()这个方法.....
            Method method =  parameter.getValueMethod();
            method.setAccessible(true);
            name = (String) method.invoke(parameter.getAnnotation(), null);
            if(name.isEmpty()) {
                return null;
            }

            Class<?> annotationType = parameter.getAnnotation().annotationType();
            if(PathVariable.class.equals(annotationType)) {
                String url = ProcessContext.getContext().getString(ProcessContext.REQUEST_URL);
                value = handler.getPathVariable(url, name);
            } else if(RequestParam.class.equals(annotationType)) {
                if(parameter.getType().isArray()) {
                   return ProcessContext.getRequest().getParameterValues(name);
                } else {
                    value = ProcessContext.getRequest().getParameter(name);
                }
            } else if(RequestHeader.class.equals(annotationType)) {
                value = ProcessContext.getRequest().getHeader(name);
            } else if(CookieValue.class.equals(annotationType)) {
                Cookie[] cookies = ProcessContext.getRequest().getCookies();
                if(!JpUtils.isEmpty(cookies)) {
                    for(int i = 0; i < cookies.length; i++) {
                        if(name.equals(cookies[i].getName())) {
                            value = cookies[i].getValue();
                            break;
                        }
                    }
                }
            }
            Object targetValue = JpUtils.convert(value, parameter.getType());
            return targetValue;
        }


        return autowiredParameter(parameter.getType());
    }

    /**
     * Inject POJO and handle file upload
     * use fastJson to deserialization. value can't be inject if the target Object did not
     * provide the corresponding setter
     * @param paramClass
     * */
    private Object autowiredParameter(Class<?> paramClass)  throws Exception {
        HttpServletRequest request = ProcessContext.getRequest();
        Object result = null;

        String contentType = request.getContentType();
        if( (!StringUtils.isEmpty(contentType))
                && (contentType.startsWith("application/json")) ) {
            String content = readText(request);
            if(!StringUtils.isEmpty(content)) {
                JSONObject json = JSON.parseObject(content);
                result = JSON.toJavaObject(json, paramClass);
            }
        } else {
            JSONObject json = new JSONObject();
            format(paramClass, json, request.getParameterMap());
            result = JSON.toJavaObject(json, paramClass);//
        }
        return result;
    }

    /**
     * 根据POJO，对数据的格式进行一些设置
     * @param paramClass(POJO)
     * */
    private static void format(Class<?> paramClass, JSONObject json, Map<String, String[]> parameterMap) {
        json.putAll(parameterMap);
        String[] values = null;
        Field field;

        for(Map.Entry<String, Object> entry : json.entrySet()) {
            values = (String[]) entry.getValue();
            if(values != null && values.length == 1) {
                try {
                    field = paramClass.getDeclaredField(entry.getKey());
                    if(null != field) {
                        if(field.getType().isArray()) {
                          //Simply skip
                        } else if(Collection.class.isAssignableFrom(field.getType())) {
                            entry.setValue(Arrays.asList(values));
                        } else {
                            entry.setValue(values[0]);
                        }
                    }
                } catch (Exception e) {
                   //ignore it
                }
            }
        }
    }

    /**
     * read json data from web request
     * This method will be called when the Context-Type is :application/json
     * @param request
     * */
    private static String readText(HttpServletRequest request) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(request.getInputStream(), "UTF-8");
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[256];
            int read = 0;
            while((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        } catch (IOException e) {

        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return null;
    }
}
