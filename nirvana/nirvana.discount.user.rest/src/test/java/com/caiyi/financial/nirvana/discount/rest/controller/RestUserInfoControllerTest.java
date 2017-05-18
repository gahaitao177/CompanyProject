package com.caiyi.financial.nirvana.discount.rest.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by wenshiliang on 2016/11/21.
 */
public class RestUserInfoControllerTest extends TestSupport {

    @Autowired
    RestUserInfoController restUserInfoController;

    @Test
    public void qCollectInfo()throws Exception{
        MvcResult result = mockMvc.perform((post("/user/qCollectInfo.go")
                .param("accessToken",accessToken)
                .param("appId",appId)
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    @Test
    public void cheapCollectDel() throws Exception {
        MvcResult result = mockMvc.perform((post("/user/cheapCollectDel.go")
                .param("accessToken",accessToken)
                .param("appId",appId)
                .param("storeId","80132")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

}