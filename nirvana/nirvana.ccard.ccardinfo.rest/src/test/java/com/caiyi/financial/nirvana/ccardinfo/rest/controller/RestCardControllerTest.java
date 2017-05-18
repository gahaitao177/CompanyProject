package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by shaoqinghua on 2017/1/9.
 */
public class RestCardControllerTest extends TestSupport {

    @Test
    public void queryCardIndex() throws Exception {
        MvcResult result = mockMvc.perform((post("/credit/qCardIndex2.go")
                .param("cityid", "101")
                //.param("cuserId", "97b24cc8210")
                //.param("ibankids", "1,2,3,4,5")
                //.param("iclient", "1")
                //.param("source", "6000")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }


    @Test
    public void queryFilterCondition() throws Exception {
        MvcResult result = mockMvc.perform((post("/credit/qFilterCondition2.go")
                .param("cityid", "101")
                //.param("bankid", "-1")
                //.param("useid", "-1")
                //.param("ps", "10")
                //.param("pn", "1")

        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }

    @Test
    public void queryFilterCard() throws Exception {
        MvcResult result = mockMvc.perform((post("/credit/qCardFilter2.go")
                //.param("cityid","101")
                //.param("bankid","-1")
                //.param("useid","-1")
                //.param("ps","10")
                //.param("pn","1")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }


    @Test
    public void queryCardDetail() throws Exception {
        MvcResult result = mockMvc.perform((post("/credit/qCardDetail2.go")
                .param("cardid","6006")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }
}
