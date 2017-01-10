package jp.spring.web.servlet.handler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/10/2017.
 */
public class UrlMapping {

    private final String beanName;

    private final Method method;

    private boolean hasPathVairable = false;

    private Pattern urlPattern = null;

    private String url;
    /**
     * key: path variable name, value: path variable regex index in urlPattern
     * */
    private Map<String, Integer> pathVariableMap = null;

    public UrlMapping(Method method, String beanName) {
        this.method = method;
        this.beanName = beanName;
    }

    public Method getMethod() {
        return method;
    }

    public void setUrlExpression(String urlExpression) {
        this.hasPathVairable = true;
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

    public Map<String, Integer> getPathVariableMap() {
        return pathVariableMap;
    }

    public void setPathVariableMap(Map<String, Integer> pathVariableMap) {
        this.pathVariableMap = pathVariableMap;
    }

    public boolean isHasPathVairable() {
        return hasPathVairable;
    }

    @Override
    public String toString() {
        return "UrlMapping{" +
                "method=" + method +
                ", beanName='" + beanName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
