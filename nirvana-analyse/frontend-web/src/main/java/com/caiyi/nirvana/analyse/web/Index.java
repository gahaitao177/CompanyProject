package com.caiyi.nirvana.analyse.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by been on 2016/12/27.
 */

@Controller
public class Index extends BaseController {
    @RequestMapping({"/", "index"})
    public String index(Model model) {
        return "index";
    }


    @RequestMapping("/404")
    public String notFound(Model model) {
        return "404";
    }

    @RequestMapping("/tables")
    public String tables(Model model) {
        return "tables";
    }

    @RequestMapping("/calendar")
    public String calendar(Model model) {
        return "calendar";
    }

    @RequestMapping("/chart")
    public String chart(Model model) {
        return "chart";
    }

    @RequestMapping("/form")
    public String form(Model model) {
        return "form";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @RequestMapping("/sign-up")
    public String signUp(Model model) {
        return "sign-up";
    }


    @RequestMapping("/table-list")
    public String tableList(Model model) {
        return "table-list";
    }


    @RequestMapping("/table-list-img")
    public String tableListImg(Model model) {
        return "table-list-img";
    }


    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }

}
