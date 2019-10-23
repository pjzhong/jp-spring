package com.web.controller;

import jp.spring.web.annotation.Controller;
import jp.spring.web.annotation.RequestMapping;

@Controller
@RequestMapping("interceptor")
public class InterceptorController {

  @RequestMapping
  public Integer plusOne(Integer i) {
    return i;
  }

}
