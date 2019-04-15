package com.jp.controller;

import com.jp.Model.User;
import com.jp.service.OutputService;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.RequestParam;
import jp.spring.web.annotation.ResponseBody;


/**
 * Created by Administrator on 1/11/2017.
 */
@Controller
@RequestMapping("/example")
public class TestController {

  @Autowired
  OutputService outputService;

  @RequestMapping(value = "/test{one}/hi", method = RequestMethod.GET)
  public String test2(@PathVariable("one") Integer one, User user,
      @RequestParam("number") Float[] number) {
    System.out.println(outputService);
    outputService.output(one);
    outputService.output(user);
    outputService.output(number);
    return "test";
  }

  @RequestMapping(value = "/user", method = RequestMethod.POST)
  @ResponseBody
  public User test(User user) {
    System.out.println(user);
    return user;
  }
}



