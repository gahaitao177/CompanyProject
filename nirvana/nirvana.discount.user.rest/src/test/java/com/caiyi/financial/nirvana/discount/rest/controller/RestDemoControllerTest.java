package com.caiyi.financial.nirvana.discount.rest.controller;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Created by wenshiliang on 2016/11/17.
 * 测试的示例
 */
public class RestDemoControllerTest extends test.TestSupport  {




    @Test
    public void test1Success() throws Exception {
        MvcResult result = mockMvc.perform((post("/control/test1.go")
                .param("userName", "admin")
                .param("password", "1")
                .param("accessToken",accessToken)
                .param("appId",appId)
//                .param("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvsB5PNTA5TfvGWe477uP8ISLF2XEpha7+emwapbIVJQGidGbXxRBjds+eniz3cgxHnN5ChUzJY3G00MR3WotRX4l2Hento2uu9JliS4B9QVEqD5EcwyyikUQWeeq84fYByG7mvXuMXoZQ==")
//                .param("appId","lt20M161HPG1ZGA1K704P520VR1IEH215")
                ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
        Assert.assertTrue("test1".equals(str));
    }

    @Test
    public void test1Error() throws Exception {
        MvcResult result = mockMvc.perform((post("/control/test1.go")
                .param("userName", "admin")
                .param("password", "1")
//                .param("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvsB5PNTA5TfvGWe477uP8ISLF2XEpha7+emwapbIVJQGidGbXxRBjds+eniz3cgxHnN5ChUzJY3G00MR3WotRX4l2Hento2uu9JliS4B9QVEqD5EcwyyikUQWeeq84fYByG7mvXuMXoZQ==")
//                .param("appId","lt20M161HPG1ZGA1K704P520VR1IEH215")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
        Assert.assertTrue("test1".equals(str));
    }

}