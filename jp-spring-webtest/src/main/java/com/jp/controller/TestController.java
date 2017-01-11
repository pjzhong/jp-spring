package com.jp.controller;

import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;


/**
 * Created by Administrator on 1/11/2017.
 */
@Controller
public class TestController {

    public TestController() {
        System.out.println("Hello I am TestController");
    }


    @RequestMapping(value = "/test/{one}", method = RequestMethod.GET)
    public String test2(@PathVariable("one") Integer one) {
        return "test";
    }

    @RequestMapping(value = "/test456465", method = RequestMethod.POST)
    public String test() {
        return "test";
    }
}
