package com.caiyi.financial.nirvana.discount.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lizhijie on 2017/2/9.
 */
public class RestHskUserControllerTest extends TestSupport {

    @Test
    public void  registerChkTest() throws Exception {
        MvcResult result = mockMvc.perform((post("/user/registerchk.go")
                .param("uid", "18717861758")
                .param("pwd","123456")
                .param("source","6000")
                .param("key","www")
                .param("timeStamp","1221")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    @Test
    public void  registerTest() throws Exception {
//        uid=17726552835812646275&appMgr=1&imei=OUM3QUQwRDQ0NzE3QzYyRTU4MzEyOUFBRjM4MDEwMTcsMDg6MDA6Mjc6YmU6Yjg6ZjY%3D
//                &packagename=com.huishuaka.credit&mtype=1&yzm=123456&userType=1
//                &hskcityid=100&iclient=0&pwd=123456&source=5000&appVersion=300&adcode=900000
        MvcResult result = mockMvc.perform((post("/user/register.go")
                .param("uid", "13071293274")
                .param("pwd","123456")
                .param("source","6000")

                .param("yzm","123456")
                .param("yzmType","0")
                .param("iclient","0")
                .param("ipAddr","127.0.0.1")
                .param("mobileType","0")

                .param("key","www")
                .param("timeStamp","1221")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("注册:"+str);
    }

    /**
     * 微信登录
     * @throws Exception
     */
    @Test
    public void  weChartLoginTest() throws Exception {
//        appVersion=300&source=5000&mtype=1&uuid=0ea833cd-b224-4843-b67f-233c704b9981&iclient=0&
// code=031UTBGM1aLwG31fzwHM12zzGM1UTBG-&appId=wxb22995abdbe38782&source=5000
        MvcResult result = mockMvc.perform((post("/user/wechatLogin.go")
                .param("appVersion", "300")
                .param("source","5000")

                .param("mtype","1")
                .param("uuid","0ea833cd-b224-4843-b67f-233c704b9981")
                .param("iclient","0")
                .param("ipAddr","127.0.0.1")
                .param("code","011iZL3z17ZmEf0XwF0z16dV3z1iZL3a")
 
                .param("appId","wxb22995abdbe38782")
                .param("secret","046786df610b4daba8db7983072c702e")
        ))
//                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("微信登录:"+str);
    }

    /**
     * 普通用户登录
     * @throws Exception
     */
    @Test
    public void  loginTest() throws Exception {
//        uid=15216611690&pwd9188=17e72eb65528fd35b812dd646be2d7a5&appMgr=1&signType=1&packagename=com.huishuaka.credit&mtype=1
// &merchantacctId=130313001&hskcityid=100&iclient=0&pwd=987d407ada675529b1814061477f0ac9&source=5000&appVersion=300&
//                signMsg=52F192F677DD8B49533655D0C474575E&adcode=900000
        MvcResult result = mockMvc.perform((post("/user/login.go")
                .param("appVersion", "300")
                .param("source","5000")
                .param("appMgr","1")
                .param("packagename","com.huishuaka.credit")

                .param("pwd9188","17e72eb65528fd35b812dd646be2d7a5")
                .param("pwd","987d407ada675529b1814061477f0ac9")
                .param("uid","15216611690")
                .param("iclient","0")
                .param("hskcityid","100")
                .param("mtype","1")
                .param("merchantacctId","130313001")
                .param("ipAddr","127.0.0.1")

                .param("adcode","900000")
                .param("signMsg","52F192F677DD8B49533655D0C474575E")
        ))
//                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("普通登录:"+str);
    }

    /**
     * 普通用户登录
     * @throws Exception
     */
    @Test
    public void  resetPwdNotLogin9188Test() throws Exception {
//        uid=15216611690&pwd9188=17e72eb65528fd35b812dd646be2d7a5&appMgr=1&signType=1&packagename=com.huishuaka.credit&mtype=1
// &merchantacctId=130313001&hskcityid=100&iclient=0&pwd=987d407ada675529b1814061477f0ac9&source=5000&appVersion=300&
//                signMsg=52F192F677DD8B49533655D0C474575E&adcode=900000
        MvcResult result = mockMvc.perform((post("/user/resetPwdNotLogin.go")
                .param("appVersion", "300")
                .param("source","5000")
                .param("appMgr","1")
                .param("packagename","com.huishuaka.credit")
                //发送验证码
//                .param("timeStamp","1487754528672")
//                .param("key","9248b5fa602f2719fc0324cee6c6f17f")
//                .param("mobileNo","15216611690")
//                .param("actionName","sendYzm")
//                .param("iclient","0")
//                .param("hskcityid","100")
//                .param("mtype","1")
//                .param("uuid","249e1d76-342d-40a2-8c92-8d49a2bf0fb9")
//                .param("ipAddr","127.0.0.1")

                .param("newPwd","123456")
                .param("actionName","reSetPwd")
                .param("yzm","123456")
                .param("ipAddr","127.0.0.1")
                .param("mobileNo","15216611690")

        ))
//                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("普通登录:"+str);
    }

    @Test
    public void  sendMessageTest() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/user/sendMessage.go")
                .param("uid", "13071293274")
                .param("source","6000")
                .param("iclient","0")
                .param("ipAddr","127.0.0.1")
                .param("mobileType","0")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }
    @Test
    public void  quickLoginTest() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/user/quickLogin.go")
                .param("uid", "13071293274")
                .param("source","6000")
                .param("yzm","949294")
                .param("iclient","0")
                .param("ipAddr","127.0.0.1")
                .param("mobileType","0")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }
}


