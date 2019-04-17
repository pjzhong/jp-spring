package com.jp.interceptors;

import jp.spring.mvc.annotation.Intercept;
import jp.spring.mvc.interceptor.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 1/27/2017.
 */
@Intercept(url = "/example/*")
public class TestInterceptor2 implements Interceptor {

    @Override
    public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("I am TestInterceptor2 before");
        return true;
    }

    @Override
    public void afterHandle(HttpServletRequest request, HttpServletResponse response, Object handle) {
        System.out.println("I am TestInterceptor2 after");
    }
}