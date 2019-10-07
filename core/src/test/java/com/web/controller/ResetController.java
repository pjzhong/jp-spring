package com.web.controller;

import jp.spring.web.annotation.Controller;
import jp.spring.web.annotation.PathVariable;
import jp.spring.web.annotation.RequestMapping;

@Controller
@RequestMapping("/module/{name}")
public class ResetController {

  @RequestMapping("/")
  public String moduleName(@PathVariable String name) {
    return name;
  }

  @RequestMapping("{number}")
  public Float number(@PathVariable Float number) {
    return number;
  }
}
