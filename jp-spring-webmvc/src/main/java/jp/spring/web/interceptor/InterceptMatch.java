package jp.spring.web.interceptor;


import jp.spring.ioc.util.JpUtils;
import jp.spring.web.annotation.Intercept;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/27/2017.
 * 对 Interceptor 进行封装。方便处理和管理
 */
public class InterceptMatch {

    private Pattern pattern;

    private Interceptor interceptor;

    public InterceptMatch(Class<?> interceptorClass, Interceptor interceptor) {
        if(JpUtils.isAnnotated(interceptorClass, Intercept.class)) {
            String value = interceptorClass.getAnnotation(Intercept.class).url();
            pattern = Pattern.compile("^" + value );
            this.interceptor = interceptor;
        }
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public boolean match(String str) {
        return pattern != null ? pattern.matcher(str).find() : false;
    }
}
