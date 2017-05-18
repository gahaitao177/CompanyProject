package test.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lichuanshun on 16/12/8.
 */
public class CreditScoreControllerTest extends TestSupport {
    //获取信用特权数据test
    @Test
    public void testPrivilege() throws Exception{
        MvcResult result = mockMvc.perform((post("/control/investigation/creditPrivilege.go")
                .param("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvutl2y/TXWNgksCB+TCFcvEJOf4YaTRJFmqYJ3uL+fTlp3n6h7CXnorHsJ1arMfJb5P8c9o0ecrheuVEDeLuuwoXwd9SsZiiuX0UxIN+HvJvZtiix0dAOpB+mVdgLPZ4uTj0hLv3gRWTg==")
                .param("appId","lc2O0WL1UX612R1TFS2PH022I627N62D8")
                .param("cuserId","84d45b82c303")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("信用特权数据:"+str);
    }
    @Test
    public void testCreditScoreIndex() throws Exception{
        MvcResult result = mockMvc.perform((post("/notcontrol/investigation/creditScoreIndex.go")
//                .param("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvusk7vgrZBZk2MHHJSdubFwWOyO7qRqSXPca3SLpbVLYo67B/y6QUn5Ixn+iyWaHYAOYmrpyh0LIk54MF0AbfY44yQ5oH7x0XH9iF4L+YFAv87gj4SoLPdO82vCLAYNepC5qCzgtvh93w==")
//                .param("appId","ltPZ20Y1S6H1WDM21M4111C2S0Q909GF9")
                .param("accessToken","+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvfBkBZPPfhYAyp1jsmlMmKEgVyhQFTACmHV1g30Y6ymj5gyudmpQuy1VsN1vqEmuiA+nQwjyjiWGzgnyqx1wGNFxhXvtaV6CdA79f/A3ZYb5ktzMyRew04y2D4aUBs2kUVEDex8yTA4Q==")
                .param("appId","lc20P1KIT7P0G2AUL21A01Z30J4WI5151")
                .param("cuserId","5862f07a6902")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("征信首页数据:"+str);
    }
}
