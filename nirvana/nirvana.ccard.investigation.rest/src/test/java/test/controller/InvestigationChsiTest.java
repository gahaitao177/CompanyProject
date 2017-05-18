package test.controller;

import com.alibaba.fastjson.JSON;
import org.apache.storm.messaging.ConnectionWithStatus;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by shaoqinghua on 2017/1/24.
 */
public class InvestigationChsiTest extends TestSupport {

    @Test
    public void getChsiImgCode() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/investigation/getChsiImgCode.go")
                .param("cuserId", "84d45b82c303")
                .param("imgCodeType", "3")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);

    }

    @Test
    public void chsiLoginTest() throws Exception {
        MvcResult result = mockMvc.perform((post("/control/investigation/chsiLogin.go")
                //.param("accessToken", "+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvutl2y/TXWNgksCB+TCFcvEJOf4YaTRJFmqYJ3uL+fTlp3n6h7CXnorHsJ1arMfJb5P8c9o0ecrheuVEDeLuuwoXwd9SsZiiuX0UxIN+HvJvZtiix0dAOpB+mVdgLPZ4uTj0hLv3gRWTg==")
                //.param("appId", "lc2O0WL1UX612R1TFS2PH022I627N62D8")
                //.param("cuserId", "84d45b82c303")
                //.param("username", "185158064@qq.com")
                //.param("password", "lqwlove123456789")
                //.param("iclient", "0")
                //.param("code","吐水腊")
                //.param("username", "13791842839")
                //.param("password", "1234a5678")
                //.param("username", "15921401216")
                //.param("password", "91889188")
                //.param("username", "185158064@qq.com")
                //.param("password", "lqwlove123456789")
                //.param("username", "492348312@qq.com")
                //.param("password", "xiaohuoban443")
                //.param("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuCoSwTSXn4K5Ky5kgWhfTljBiTBRE0Tcc1FnpOTQBlNMQu3AfxoGQvfjKijiAObOpPn186FujZYCqpOSVvV7uWFlMDxTn6pPg9DQ5+yvMeylpfgDDPkCtatuoqdSDsHkFydbmD0AtR9A==")
                //.param("appId", "lt2017ZZS020WI4Z110F4PV05OQ3S5NL5")
                //.param("cuserId", "c7d0c1ea-b0a9-43de-a637-7936ef8e0c02")
                //.param("username", "125816149@qq.comM")
                //.param("password", "jl125816")
                //.param("iclient", "0")
                //.param("accessToken",
                //        "+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuhHPpikQHg8x1ERPBohrnU1viYFNq5AetzYJ4rxV6pmHUn05/ice7H5tXOrFgKkEruxlgmrh+Nua4YuM8KStuYzx/Y//Q9/N89g1f6o6JuGYCyofDVMjM4pwW9kYOUtiTgG+Sx3mg1Gg==")
                //.param("appId", "ltSX20170ID20KDIQ910T0213G3L4QNQ7")
                ////.param("cuserId", "8dba725f215")
                //.param("username", "409650132@qq.com")
                //.param("password", "900215CX")
                //.param("iclient", "0")
                //.param("iskeep","1")
                //.param("code","3D2n4c")
                .param("accessToken",
                        "+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuH7vjmOtcarqbqs9oQ1WzIq/Q1u46fVR1M0M1EuLPdcKuT0FSNdagl2NpIaEAYGV97ff89EOS+9SUnA6POlxBkTXNvha2VZlQboJMXB3Zpdz4z85fyISjMEDF9bQOU9XW5vWTuVNVqKw==")
                .param("appId", "ltRZC20N170O2P1IFK0HTL10052797PQ1")
                .param("username", "185158064@qq.com")
                .param("password", "lqwlove123456789")
                .param("iclient", "0")
                //.param("iskeep","1")
                //.param("code","3D2n4c")

        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }

    @Test
    public void loadChsiDegrees() throws Exception {
        MvcResult result = mockMvc.perform((post("/control/investigation/loadChsiDegrees.go")
                //.param("cuserId", "8dba725f2152M")
                .param("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvuCoSwTSXn4K5Ky5kgWhfTljBiTBRE0Tcc1FnpOTQBlNMQu3AfxoGQvfjKijiAObOpPn186FujZYCqpOSVvV7uWFlMDxTn6pPg9DQ5+yvMeylpfgDDPkCtatuoqdSDsHkFydbmD0AtR9A==")
                .param("appId","lt2017ZZS020WI4Z110F4PV05OQ3S5NL5")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }

    @Test
    public void checkMobileExistTest() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/investigation/checkMobileExist.go")
                .param("cuserId", "84d45b82c303")
                .param("mphone", "15700048090")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }


    @Test
    public void getRegisterVerifyCode() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/investigation/getRegisterVerifyCode.go")
                .param("cuserId", "84d45b82c303")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);
    }

    @Test
    public void getMessageSecurityCodeTest() throws Exception {
        MvcResult result = mockMvc.perform((post("/notcontrol/investigation/getMessageSecurityCode.go")
                .param("cuserId", "84d45b82c303")
                .param("captch", "巾胜")
                .param("mphone", "15700048090")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString();
        System.out.println(str);

    }

}
