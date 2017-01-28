package com.jp.interceptors;

import jp.spring.web.annotation.Intercept;
import jp.spring.web.interceptor.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 1/27/2017.
 */
@Intercept(url = "/example")
public class TestInterceptor2 implements Interceptor {

    @Override
    public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("I am test2 before");
        return true;
    }

    @Override
    public void afterHandler(HttpServletRequest request, HttpServletResponse response, Object handle) {
        System.out.println("I am test2 after");
    }
}
