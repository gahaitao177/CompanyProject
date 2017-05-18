package com.caiyi.financial.nirvana.discount.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by been on 16/4/20.
 */
@Controller
public class DemoController {
    @RequestMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "world") String name,
                        Model model) {
        System.out.println("hello, world");
        if (name.equals("hello")) {
            return "hello";
        } else {
            return "index";
        }
    }

    @RequestMapping("/simple")
    public
    @ResponseBody
    String simple() {
        return "simple";
    }


}
