package com.caiyi.financial.nirvana.tools.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lichuanshun on 16/12/15.
 */
public class AdvertisementControllerTest extends TestSupport{
    /**
     *  add by lcs 20161216
     * @throws Exception
     */
    @Test
    public void clickIdfa() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/credit/ad/click.go")
                .param("appid","hsk")
                .param("idfa","testlcs20161218002")
                .param("source","6000")
                .param("callback","http://www.huishuaka.com/credit/iosIdfaSave.go")
                .param("timestamp","14822038189")
                .param("sign","06dc94214df9a6fd5f0ac0cc36bc338e")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    @Test
    public void checkIosUserExist() throws Exception {
        MvcResult result = mockMvc.perform((post("/credit/checkIdfa.go")
                .param("appid","hsk")
                .param("idfa","testlcs20161220003")
                .param("source","6000")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    @Test
    public void iosIdfaSave() throws Exception {
        MvcResult result = mockMvc.perform((post("/credit/iosIdfaSave.go")
                .param("signMsg","10C0E817CDAF8AB689A8C8818157FA99")
                .param("idfa","XBnWA4/qi1PszE6CGSaSpJvfW1prfv2ERYpdLgBYAiRH/m/BOj/SMatbJ6zoQU6C/3JhZ0Erw+lUca+ea7tRF1tqcXQWt5Rf9lUsQOVfqwU=")
                .param("source","6000")
                .param("signType","2")
                .param("timeStamp","1484027747.879863")

        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    @Test
    public void packageIcon() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/common/iosAppInfo.go")
                .param("appid","hsk")
                .param("idfa","testlcs20161220003")
                .param("source","6000")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    @Test
    public void billInstallmentConf() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/tool/billInstallmentConf.go")
                .param("appid","hsk")
                .param("dataversion","0")
                .param("source","6000")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

}