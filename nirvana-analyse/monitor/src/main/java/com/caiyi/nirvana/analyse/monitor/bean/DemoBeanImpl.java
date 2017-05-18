package com.caiyi.nirvana.analyse.monitor.bean;

import com.rbc.frame.ServiceContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by been on 2017/3/8.
 */
public class DemoBeanImpl {
    public int test(DemoBean bean, ServiceContext context, HttpServletRequest request,
                    HttpServletResponse response) {
        System.out.println("test");
        return 0;
    }
}
