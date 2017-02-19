package jp.spring.web.handler;

import jp.spring.web.interceptor.Interceptor;
import jp.spring.web.support.MethodParameter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一个HttpRequest的具体处理者
 */
public class Handler {
    private final String beanName;

    private final Method method;

    private List<MethodParameter> methodParameters;

    private boolean hasPathVariable = false;

    private boolean isResponseBody = false;

    private Pattern urlPattern = null;

    private String url;

    private List<Interceptor> interceptors = new ArrayList<>();

    /**
     * key: path variable name, value: path variable regex index in urlPattern
     * */
    private Map<String, Integer> pathVariableMap = null;

    public Handler(Method method, String beanName) {
        this.method = method;
        this.beanName = beanName;
    }

    public Matcher getPathVariableMatcher(String readUrl) {
/*        if(!hasPathVariable) {
            return null;
        }

        Matcher m = urlPattern.matcher(readUrl);
        if(m.find()) {
            int index = pathVariableMap.get(pathVariableName);
            return m.groupCount() >= index ? StringUtils.decode(m.group(index)) : null;
        }*/
        Matcher matcher = urlPattern.matcher(readUrl);
        return matcher.find() ? matcher : null;
    }

    public Object invoker(Object obj, Object[] args) throws Exception {
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    public boolean match(String path) {
        return urlPattern.matcher(path).find();
    }

    /*Getters and setters*/
    public boolean hasPathVariable() {
        return hasPathVariable;
    }

    public boolean isResponseBody() {
        return isResponseBody;
    }

    public void setResponseBody(boolean responseBody) {
        isResponseBody = responseBody;
    }

    public Method getMethod() {
        return method;
    }

    public void setUrlExpression(String urlExpression) {
        this.hasPathVariable = true;
        this.urlPattern = Pattern.compile(urlExpression);
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

    public Map<String, Integer> getPathVariableIndexMap() {
        return pathVariableMap;
    }

    public void setPathVariableMap(Map<String, Integer> pathVariableMap) {
        this.pathVariableMap = pathVariableMap;
    }

    public List<MethodParameter> getMethodParameters() {
        return methodParameters;
    }

    public void setMethodParameters(List<MethodParameter> methodParameters) {
        this.methodParameters = methodParameters;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void addInterceptors(Collection<Interceptor> interceptors) {
        this.interceptors.addAll(interceptors);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Handler{");
        sb.append(", url='").append(url).append('\'');
        sb.append("method=").append(method);
        sb.append(", beanName='").append(beanName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
