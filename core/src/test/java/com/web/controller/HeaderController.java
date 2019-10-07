package com.web.controller;

import java.util.Arrays;
import java.util.List;
import jp.spring.web.annotation.Controller;
import jp.spring.web.annotation.RequestHeader;
import jp.spring.web.annotation.RequestMapping;

@Controller
public class HeaderController {

  @RequestMapping
  public String header(@RequestHeader("content-type") String h) {
    return h;
  }

  @RequestMapping
  public List<String> headers(@RequestHeader("content-type") String h, @RequestHeader String host) {
    return Arrays.asList(h, host);
  }

  @RequestMapping
  public Integer custom(@RequestHeader Integer custom) {
    return custom;
  }

}
