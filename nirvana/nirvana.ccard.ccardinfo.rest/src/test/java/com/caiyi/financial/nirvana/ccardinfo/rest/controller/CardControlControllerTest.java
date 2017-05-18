package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lizhijie on 2016/12/20.
 */
public class CardControlControllerTest extends TestSupport{
    @Test
    public void queryBankFlagByBankId() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/card/qBankFlag.go")
                .param("bankid", "2")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);

    }

}