package com.yy.ana.api.action;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2017/5/27.
 */
public class BaseAction {
    protected Map<String, Object> getReturnObject(HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", "0");

        return map;
    }
}
