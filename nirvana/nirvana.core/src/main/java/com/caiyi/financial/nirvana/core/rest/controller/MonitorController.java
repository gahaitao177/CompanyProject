package com.caiyi.financial.nirvana.core.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/10/25.
 */

@RestController
public class MonitorController {

    private static Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @RequestMapping("/notcontrol/monitor/monitorService.go")
    public String monitorTomcat(HttpServletRequest request, HttpServletResponse response){


      String returnMessage = null;

      String serverName =  request.getServerName();//返回当前页面所在的服务器的名字

      int status = response.getStatus();//获取当前tomcat运行状态码
      int serverPort =  request.getServerPort();//返回当前页面所在的服务器使用的端口
      /*
      String contextPath =  request.getContextPath();//返回当前页面所在的应用的名字*/

      if(status >= 200 && status <= 299){
          returnMessage = "当前Tomcat启动正常:{" + "serverName:" + serverName  + ",serverPort:" + serverPort + ",status:" + status+ "}";
      }else {
          returnMessage = "当前Tomcat启动异常:{" + "serverName:" + serverName  + ",serverPort:" + serverPort+ ",status:" + status+ "}";
      }

        return returnMessage;
    }
}
