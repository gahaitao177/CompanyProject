package com.caiyi.financial.nirvana.discount.ccard.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.discount.ccard.bean.Demo;
import com.caiyi.financial.nirvana.discount.ccard.core.TestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * Created by wenshiliang on 2016/4/22.
 */
public class DemoServiceTest extends TestSupport{

    @Autowired
    DemoService demoService;


//    @Test
//    @Rollback(false)
    public void testAddTest() throws Exception {
       int count =  demoService.addTest("t1","t2");
        System.out.println("结果-----------"+count);
//       int count =  demoService.addTest("run","t2");
//        System.out.println("结果-----------"+count);
//       int count =  demoService.addTest("error","t2");
//        System.out.println("结果-----------"+count);
    }

    @Test
    public void testSelect() throws Exception {
        List<Map<String,Object>> list = demoService.select();
        System.out.println(JSONObject.toJSONString(list));
    }

    @Test
    public void testSelect2() throws Exception {
        List<Demo> list = demoService.select2();
        System.out.println(JSONObject.toJSONString(list));
    }

    @Test
    public void testSelect3() throws Exception {
        List<Demo> list = demoService.select3();
        System.out.println(JSONObject.toJSONString(list));
    }
    @Test
    public void testSelect4() throws Exception {
        List<Map<String,Object>> list = demoService.select4();
        System.out.println(JSONObject.toJSONString(list));
    }
}