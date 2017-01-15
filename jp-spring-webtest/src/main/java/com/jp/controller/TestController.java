package com.jp.controller;

import com.jp.Model.User;
import com.jp.service.OutputService;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.RequestParam;


/**
 * Created by Administrator on 1/11/2017.
 */
@Controller
public class TestController {

    @Autowired
    OutputService outputService;

    public TestController() {
        System.out.println("Hello I am TestController");
    }


    @RequestMapping(value = "/test/{one}", method = RequestMethod.GET)
    public String test2(@PathVariable("one") Integer one, User user, @RequestParam("number") Float number) {
        System.out.println(outputService);
        outputService.output(one);
        outputService.output(user);
        outputService.output(number);
        return "test";
    }

    @RequestMapping(value = "/test456465", method = RequestMethod.POST)
    public String test() {
        return "test";
    }
}



