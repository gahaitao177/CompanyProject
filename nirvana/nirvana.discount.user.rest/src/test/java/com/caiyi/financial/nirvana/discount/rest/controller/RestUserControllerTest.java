package com.caiyi.financial.nirvana.discount.rest.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by wenshiliang on 2016/11/18.
 */
public class RestUserControllerTest extends TestSupport {
//    RestUserController restUserController;

    @Test
    public void quickLogin()throws Exception{

        String uid = "15200000010";
//        String pwd = "123456";
//        pwd = DigestUtils.md5Hex(pwd);

        String pwd = "3fcdd2102e9071060a3b7a3aae6989f3";
        String merchantacctId = "130313001";//130313001（安卓） 130313002（iso）
        String signType = "1";
        //android：A9FK25RHT487ULMI  ios:A9FK25RHT487ULMI)
        String key = "A9FK25RHT487ULMI";
        String signMsgVal = "";
        /*signMsgVal = WebUtil.appendParam(signMsgVal, "signType", signType);
        signMsgVal = WebUtil.appendParam(signMsgVal, "merchantacctId", merchantacctId);
        signMsgVal = WebUtil.appendParam(signMsgVal, "uid", uid);
        signMsgVal = WebUtil.appendParam(signMsgVal, "pwd", pwd);
        signMsgVal = WebUtil.appendParam(signMsgVal, "key", key);*/
        signMsgVal = DigestUtils.md5Hex(signMsgVal).toUpperCase();

        MvcResult result = mockMvc.perform((post("/user/login.go")
                .param("uid", uid)
                .param("pwd",pwd)
                .param("merchantacctId",merchantacctId)
                .param("signType",signType)
                .param("signMsg",signMsgVal)
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

}