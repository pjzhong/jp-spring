package jp.spring.web.handler;


import jp.spring.ioc.util.StringUtils;
import jp.spring.web.handler.support.RequestMethodParameter;
import jp.spring.web.interceptor.Interceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一个HttpRequest的具体处理者
 */
public class Handler {

    private List<Interceptor> interceptors;

    private final String beanName;

    private final Method method;

    private List<RequestMethodParameter> requestMethodParameters;

    private boolean hasPathVariable = false;

    private Pattern urlPattern = null;

    private String url;

    /**
     * key: path variable name, value: path variable regex index in urlPattern
     * */
    private Map<String, Integer> pathVariableMap = null;

    public Handler(Method method, String beanName) {
        this.method = method;
        this.beanName = beanName;
    }

    public String getPathVariable(String readUrl, String pathVariableName) {
        if(!hasPathVariable) {
            return null;
        }

        Matcher m = urlPattern.matcher(readUrl);
        if(m.find()) {
            int index = pathVariableMap.get(pathVariableName);
            return m.groupCount() >= index ? StringUtils.decode(m.group(index)) : null;
        }
        return  null;
    }

    public Object invoker(Object obj, Object[] args) throws Exception {
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    /*Getters and setters*/
    public Method getMethod() {
        return method;
    }

    public void setUrlExpression(String urlExpression) {
        this.hasPathVariable = true;
        this.urlPattern = Pattern.compile(urlExpression);
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBeanName() {
        return beanName;
    }

    public Map<String, Integer> getPathVariableMap() {
        return pathVariableMap;
    }

    public void setPathVariableMap(Map<String, Integer> pathVariableMap) {
        this.pathVariableMap = pathVariableMap;
    }

    public boolean isHasPathVariable() {
        return hasPathVariable;
    }

    public List<RequestMethodParameter> getRequestMethodParameters() {
        return requestMethodParameters;
    }

    public void setRequestMethodParameters(List<RequestMethodParameter> requestMethodParameters) {
        this.requestMethodParameters = requestMethodParameters;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public String toString() {
        return "Handler{" +
                "method=" + method +
                ", beanName='" + beanName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
