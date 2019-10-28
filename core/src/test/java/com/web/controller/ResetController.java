package com.web.controller;

import java.util.HashMap;
import java.util.Map;
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
  public Map<String, Object> number(@PathVariable Float number, String name) {
    Map<String, Object> map = new HashMap<>();
    map.put("number", number);
    map.put("name", name);
    return map;
  }

  @RequestMapping("/{one}/{two}")
  public Map<String, Object> multi(@PathVariable String name, @PathVariable Integer one,
      @PathVariable Double two) {
    Map<String, Object> expected = new HashMap<>();
    expected.put("one", one);
    expected.put("name", name);
    expected.put("two", two);
    return expected;
  }
}
