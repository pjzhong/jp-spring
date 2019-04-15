package jp.spring.web.interceptor;


import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.spring.ioc.util.StringUtils;

/**
 * Created by Administrator on 1/27/2017.
 * 对 Interceptor 进行封装。处理拦截表达式，为之后的运行做准备
 */
public class InterceptMatch {

    private static Matcher RULE = Pattern.compile("^([/]?)([\\w/\\*]*)(/\\*){1}$").matcher("");

    private Matcher pattern;

    private Interceptor interceptor;

    public InterceptMatch() {}

    public InterceptMatch(Interceptor interceptor) {
      this.interceptor = interceptor;
    }

    public InterceptMatch(Interceptor interceptor, String expression) {
        this(interceptor);
        setExpression(expression);
    }

    public void setExpression(String expression) {
        if(StringUtils.isEmpty(expression) || !RULE.reset(expression).find()) {
           throw new IllegalArgumentException("Illegal Intercept Expression");
        }

        int index = expression.lastIndexOf("/");
        String prefix = expression.substring(0, index).replace("*", "[\\w\\{\\}]]*");
        String suffix = expression.substring(index).replace("/*", "[\\w/\\.]*");
        pattern = Pattern.compile(prefix + suffix).matcher("");
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public boolean match(String str) {
        return pattern != null ? pattern.reset(str).find() : false;
    }
}
