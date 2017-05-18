package com.caiyi.financial.nirvana.discount.rest.controller;

import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.discount.user.bean.CommodityBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/8/19.
 */
@RestController
public class RestDemoController {

    public static Logger LOGGER = LoggerFactory.getLogger(RestDemoController.class);

    @RequestMapping("/control/test1.go")
    public String test1(HttpServletRequest request, CommodityBean bean, BaseBean baseBean){
        Map<String,Object> map =  (Map)request.getParameterMap();
        for(Map.Entry<String,Object> entry : map.entrySet()){
            LOGGER.info("{},{}",entry.getKey(),entry.getValue());
        }
        LOGGER.info("baseBean getCuserId{}",baseBean.getCuserId());
        Enumeration enumeration =  request.getAttributeNames();
        while(enumeration.hasMoreElements()){
            LOGGER.info("{}",enumeration.nextElement());
        }
        LOGGER.info("{}",request.getAttribute("cuserId"));
        LOGGER.info("{}",request.getAttribute("tttttt"));
        LOGGER.info("{}",request.getAttribute("ctitle"));
        LOGGER.info("getCuserId{}",bean.getCuserId());
        LOGGER.info("getAppId{}",bean.getAppId());
        LOGGER.info("getAccessToken{}",bean.getAccessToken());

        LOGGER.info("test1");
        return "test1";
    }

    @RequestMapping("/notcontrol/test2.go")
    public String test2(String cname, CommodityBean bean){
        LOGGER.info("getCtitle{}",bean.getCtitle());
        LOGGER.info("cname{}",cname);
        return cname;
    }

    @RequestMapping("/notcontrol/test3.go")
    public String test3(String cname, CommodityBean bean){
        throw new RuntimeException("测试异常");
    }

    @RequestMapping("/notcontrol/test4.go")
    public BoltResult test4(){
        return new BoltResult(BoltResult.SUCCESS,"1111111");
    }

}
