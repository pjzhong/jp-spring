package com.jp.interceptors;


import jp.spring.mvc.annotation.Intercept;
import jp.spring.mvc.interceptor.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 1/27/2017.
 */
@Intercept(url = "/example/test*/*")
public class TestInterceptor implements Interceptor {

    @Override
    public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println(request.getRequestURI());
        return true;
    }

    @Override
    public void afterHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        System.out.println("Handler has handled " + request.getRequestURI());
    }
}