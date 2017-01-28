package jp.spring.web.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.*;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.handler.support.RequestMethodParameter;
import jp.spring.web.interceptor.Interceptor;
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

    ViewResolver viewResolver = null;

    @Override
    public void invokeHandler(Handler handler) throws Exception {
        HttpServletRequest request = ProcessContext.getRequest();
        HttpServletResponse response = ProcessContext.getResponse();

        Object result;
        Object controller;
        if(JpUtils.isEmpty(handler.getInterceptors())) {
            controller = WebUtil.getWebContext().getBean(handler.getBeanName());
            result = handler.invoker(controller, autowiredParameter(handler));
        } else {
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
                interceptor.afterHandler(request, response, handler);
            }
        }

        if(JpUtils.isAnnotated(handler.getMethod(), ResponseBody.class) || !(result instanceof String) ) {
            response.setHeader("Context-type", "application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(result));
        } else if(result instanceof String ) {
            String pagePath = (String) result;
            if(!StringUtils.isEmpty(pagePath)) {
                String[] pagePaths = pagePath.split(":");
                if(pagePaths[0].equals("redirect")) {
                    response.sendRedirect(pagePaths[1]);
                } else {
                    if(viewResolver == null) {
                        viewResolver =  (ViewResolver) WebUtil.getWebContext().getBean(ViewResolver.RESOLVER_NAME);
                    }
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

    protected Object autowiredParameter(Handler handler, RequestMethodParameter parameter) throws Exception {
         if(HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
             return ProcessContext.getRequest();
         } else if(HttpServletResponse.class.isAssignableFrom(parameter.getType())) {
             return ProcessContext.getResponse();
         } else  if(HttpSession.class.isAssignableFrom(parameter.getType())) {
             return ProcessContext.getSession();
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
                value = ProcessContext.getRequest().getParameter(name);
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
        } else {
            return autowiredParameter(parameter.getType());
        }
    }

    /**
     * Inject POJO
     * @param paramClass
     * */
    private static Object autowiredParameter(Class<?> paramClass)  throws Exception {
        HttpServletRequest request = ProcessContext.getRequest();
        Object dto = null;
        System.out.println(request.getContentType());
        if(RequestMethod.GET.name().equals(request.getMethod())
                || request.getContentType().contains( "application/x-www-form-urlencoded")) {
            JSONObject json = new JSONObject();
            format(paramClass, json);
            System.out.println(json);
            dto = JSON.toJavaObject(json, paramClass);//使用FastJson，通过键值对的形式进行构造目标对象然后注入...
            //如果对象没有对应的Getter和Setter，属性将无法注入。
            //有需要的话请使用反射来赋值....
        } else if(request.getContentType().startsWith("application/json")) {
            String content = readText(request);
            System.out.println(content);
            if(!StringUtils.isEmpty(content)) {
                JSONObject json = JSON.parseObject(content);
                dto = JSON.toJavaObject(json, paramClass);
            }
        }
        return dto;
    }

    /**
     * This method will be called when inject a pojo
     * */
    private static void format(Class<?> paramClass, JSONObject json) {
        json.putAll(ProcessContext.getRequest().getParameterMap());
        String[] values = null;
        Field field;

        for(Map.Entry<String, Object> entry : json.entrySet()) {
            values = (String[]) entry.getValue();
            if(values != null && values.length == 1) {
                try {
                    field = paramClass.getDeclaredField(entry.getKey());
                    if(null != field) {
                        if(field.getType().isArray()) {
                            // pass
                        } else if(Collection.class.isAssignableFrom(field.getType())) {
                            entry.setValue(Arrays.asList(values));
                        } else {
                            entry.setValue(values[0]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
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
