package com.jp.controller;

import jp.spring.ioc.stereotype.Controller;
import jp.spring.mvc.annotation.RequestMapping;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.annotation.ResponseBody;

/**
 * Created by Administrator on 1/27/2017.
 */
@Controller
public class JpController {

    @RequestMapping("zjp")
    @ResponseBody
    public String zjp() {
        return "zjp";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "index";
    }
}
