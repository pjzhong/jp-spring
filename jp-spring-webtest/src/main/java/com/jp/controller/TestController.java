package com.jp.controller;

import com.jp.ClassScan;
import com.jp.Model.User;
import com.jp.service.OutputService;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.beans.io.loader.ClassResourceLoader;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.process.AopTest;
import jp.spring.process.WebTest;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.RequestParam;

import java.net.URL;
import java.util.List;


/**
 * Created by Administrator on 1/11/2017.
 */
@Controller
@RequestMapping("/example")
public class TestController {

    @Autowired
    WebTest webTest;

    @Autowired
    AopTest aopTest;

    @Autowired
    OutputService outputService;

    @RequestMapping(value = "/test/{one}", method = RequestMethod.GET)
    public String test2(@PathVariable("one") Integer one, User user, @RequestParam("number") Float number) {
        System.out.println(outputService);
        outputService.output(one);
        outputService.output(user);
        outputService.output(number);
        outputService.output(webTest);
        outputService.output(aopTest);

        return "test";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String test() {
        return "test";
    }
}



