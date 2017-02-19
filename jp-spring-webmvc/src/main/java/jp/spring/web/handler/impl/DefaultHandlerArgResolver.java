package jp.spring.web.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.ioc.util.TypeConvertUtils;
import jp.spring.web.annotation.CookieValue;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestHeader;
import jp.spring.web.annotation.RequestParam;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerArgResolver;
import jp.spring.web.support.MethodParameter;
import jp.spring.web.support.MultiPartRequest;
import jp.spring.web.support.MultipartFiles;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by Administrator on 2/19/2017.
 */
public class DefaultHandlerArgResolver implements HandlerArgResolver {

    private static final Object UN_RESOLVE = new Object();

    @Override
    public Object[] resolve(Handler handler) {
        return resolveParameter(handler);
    }

    protected Object[] resolveParameter(Handler handler)  {
        if(JpUtils.isEmpty(handler.getMethodParameters())) {
            return null;
        }

        List<MethodParameter> methodParameters = handler.getMethodParameters();
        Object[] paras = new Object[methodParameters.size()];

        Matcher pathVariableMatcher = null;
        if(handler.hasPathVariable()) {
            String url = (String) ProcessContext.getContext().get(ProcessContext.REQUEST_URL);
            pathVariableMatcher = handler.getPathVariableMatcher(url);
        }
        MethodParameter parameter = null;
        for(int i = 0; i < methodParameters.size(); i++) {
            parameter = methodParameters.get(i);
            //没有任何标记，那先处理标准类型
            if(!parameter.hasAnnotation()) {
                paras[i] = resolveStandParameter(parameter);
                if(paras[i] != UN_RESOLVE) { continue; }
            }

            if(parameter.isPrimitiveType() || JpUtils.isSimpleTypeArray(parameter.getParameterType())) {
                paras[i] = resolveParameter(handler, pathVariableMatcher, methodParameters.get(i));
            } else {
                paras[i] = resolvePOJOParam(parameter);
            }
        }

        return paras;
    }

    private Object resolveStandParameter(MethodParameter parameter) {
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

        return UN_RESOLVE;
    }

    /**
     * 开始进行参数注入
     * @param handler
     * @param  parameter
     * */
    protected Object resolveParameter(Handler handler, Matcher matcher, MethodParameter parameter)  {
        Object paramValue = null;
        String name = parameter.getName();
        if(StringUtils.isEmpty(name)) {
            return null;
        }

        Class<?> annotationType = parameter.getAnnotation().annotationType();
        if(PathVariable.class.equals(annotationType)) {
           paramValue = resolvePathVariable(name, handler, matcher);
        } else if(RequestParam.class.equals(annotationType)) {
            Map<String, Object> map = (Map<String, Object>) ProcessContext.getContext().get(ProcessContext.PARAMETER_MAP);
            paramValue = map.get(name);
        } else if(RequestHeader.class.equals(annotationType)) {
            paramValue = ProcessContext.getRequest().getHeader(name);
        } else if(CookieValue.class.equals(annotationType)) {
            Cookie[] cookies = ProcessContext.getRequest().getCookies();
            if(!JpUtils.isEmpty(cookies)) {
                for(int i = 0; i < cookies.length; i++) {
                    if(name.equals(cookies[i].getName())) {
                        paramValue = cookies[i].getValue();
                        break;
                    }
                }
            }
        }

        return checkType(paramValue, parameter);
    }

    protected Object resolvePathVariable(String name, Handler handler, Matcher matcher) {
        int index = handler.getPathVariableIndexMap().get(name);
        return  matcher.groupCount() >= index ? StringUtils.decode(matcher.group(index)) : null;
    }

    protected Object resolvePOJOParam(MethodParameter parameter) {
        Object object = null;

        //封装了所以请求参数
        Map<String, String[]> parameterMap = (Map<String, String[]>)ProcessContext.getContext().get(ProcessContext.PARAMETER_MAP);
       //Fields of POJO
        Map<String, Class<?>> fieldMap = parameter.getFieldMap();

        //formatting datas
        Map<String, Object> valueMap = new HashMap<String, Object>();
        String key;
        Class<?> type;
        for(Map.Entry<String, Class<?>> fieldEntry : fieldMap.entrySet()) {
            if(parameterMap.containsKey(fieldEntry.getKey())) {
                key = fieldEntry.getKey();
                type = fieldEntry.getValue();
                if(type.isArray() ||
                        Collection.class.isAssignableFrom(type) ) {
                    valueMap.put(key, Arrays.asList( parameterMap.get(key)));
                } else {
                    valueMap.put(key, parameterMap.get(key)[0]);
                }
            }
        }

        try {
            JSONObject json = new JSONObject(valueMap);
            object = JSON.toJavaObject(json, parameter.getParameterType());
        } catch (Exception e) {
            //ignore
        }

        return object;
    }

    protected Object checkType(Object value, MethodParameter parameter) {
        Object newValue = null;
        if(value != null && parameter != null) {
            Class<?> paramType = parameter.getParameterType();
            if(parameter.isPrimitiveType()) { //base type
                if(value.getClass().isArray()) {
                    value = Array.get(value, 0);
                }
                newValue = TypeConvertUtils.convertToBasic(value, paramType);
            } else{ //collection or array
                newValue = TypeConvertUtils.convertToCollection(value, paramType, parameter.getType());
            }
        }
        return newValue;
    }
}
