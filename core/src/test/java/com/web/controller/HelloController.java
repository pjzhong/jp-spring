package com.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jp.spring.web.annotation.Controller;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestParam;

@Controller
public class HelloController {

  @RequestMapping
  public String str(@RequestParam String hello) {
    return hello;
  }

  @RequestMapping
  public Map<String, Object> multiParams(String hello, Long age, List<String> names) {
    Map<String, Object> res = new HashMap<>();
    res.put("hello", hello);
    res.put("age", age);
    res.put("names", names);
    return res;
  }

  @RequestMapping
  public Integer[] arrayParams(Integer[] ages) {
    return ages;
  }

  @RequestMapping
  public List<Double> listParams(List<Double> doubles) {
    return doubles;
  }

  @RequestMapping
  public Set<Double> setParams(Set<Double> doubles) {
    return doubles;
  }

}
