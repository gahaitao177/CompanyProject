package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lichuanshun on 2017/2/10.
 */
public class H5ChannelControllerTest extends TestSupport {
    @Test
    public void h5HskIndex() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/h5channel/h5index")
                .param("bankid", "2")
                .param("hskcityid", "101")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

}