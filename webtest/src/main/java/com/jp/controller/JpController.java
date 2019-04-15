package com.jp.controller;

import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.ResponseBody;

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
