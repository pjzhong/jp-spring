package jp.spring.web.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.annotation.*;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerArgResolver;
import jp.spring.web.handler.HandlerInvoker;
import jp.spring.web.interceptor.Interceptor;
import jp.spring.web.support.MethodParameter;
import jp.spring.web.support.MultiPartRequest;
import jp.spring.web.support.MultipartFiles;
import jp.spring.web.util.WebUtil;
import jp.spring.web.view.ViewResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
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

    private HandlerArgResolver argResolver;

    private String REDIRECT = "redirect:";

    public void init() {
        if(isInitialized) {
            return;
        }

        WebApplicationContext applicationContext = WebUtil.getWebContext();
        try {
            viewResolver =  (ViewResolver) applicationContext.getBean(ViewResolver.RESOLVER_NAME);
            argResolver = new DefaultHandlerArgResolver();
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
        Object[] args = argResolver.resolve(handler);
        result = handler.invoker(controller, args);
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
        if(JpUtils.isEmpty(handler.getMethodParameters())) {
            return null;
        }

        List<MethodParameter> methodParameters = handler.getMethodParameters();
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
    protected Object autowiredParameter(Handler handler, MethodParameter parameter) throws Exception {
         if(HttpServletRequest.class.isAssignableFrom(parameter.getParameterType())) {
             return ProcessContext.getRequest();
         } else if(HttpServletResponse.class.isAssignableFrom(parameter.getParameterType())) {
             return ProcessContext.getResponse();
         } else  if(HttpSession.class.isAssignableFrom(parameter.getParameterType())) {
             return ProcessContext.getSession();
         } else if(MultipartFiles.class.isAssignableFrom(parameter.getParameterType())) {
             if(ProcessContext.getRequest() instanceof MultiPartRequest) {// return upload file
                 return ((MultiPartRequest) ProcessContext.getRequest()).getMultipartFiles();
             }
         }

        if(parameter.isPrimitiveType() && parameter.hasAnnotation()) {
            String name = parameter.getName(), value = null;
            if(StringUtils.isEmpty(name)) {
                return null;
            }

            Class<?> annotationType = parameter.getAnnotation().annotationType();
            if(PathVariable.class.equals(annotationType)) {
                String url = ProcessContext.getContext().getString(ProcessContext.REQUEST_URL);
/*                value = handler.getPathVariableMatcher(url, name);*/
            } else if(RequestParam.class.equals(annotationType)) {
                String[] values = ProcessContext.getRequest().getParameterValues(name);
                if(values != null && value.length() == 1) {
                   value = values[0];
                } else {
                   return ProcessContext.getRequest().getParameterValues(name);
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
            Object targetValue = JpUtils.convert(value, parameter.getParameterType());
            return targetValue;
        }


        return autowiredParameter(parameter.getParameterType());
    }

    /**
     * Inject POJO and handle file upload
     * use fastJson to deserialization. value can't be inject if the target Object did not
     * provide the corresponding setter
     * @param paramClass
     * */
    private Object autowiredParameter(Class<?> paramClass) {
       Object result = null;

        try {
            JSONObject json = new JSONObject();
            Map<String, Object> paramMap = (Map<String, Object>) ProcessContext.getContext().get(ProcessContext.PARAMETER_MAP);
            format(paramClass, json, paramMap);
            result = JSON.toJavaObject(json, paramClass);//
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据POJO，对数据的格式进行一些设置
     * @param paramClass(POJO)
     * */
    private static void format(Class<?> paramClass, JSONObject json, Map<String, Object> parameterMap) {
        json.putAll(parameterMap);
        String[] values = null;
        Field field;

        for(Map.Entry<String, Object> entry : json.entrySet()) {
            values = (String[]) entry.getValue();
            if(!StringUtils.isEmpty(values)) {
                try {
                    field = paramClass.getDeclaredField(entry.getKey());
                    if(null != field) {
                        if(field.getType().isArray()
                                || Collection.class.isAssignableFrom(field.getType()) ) {
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
}
