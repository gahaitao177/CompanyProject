package com.caiyi.financial.nirvana.bill.bank.bill;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created byLinxingyu on 2016/12/20.
 */
public class BillConsumeTest extends TestSupport {

    /**
     * 账单流水测试
     * @throws Exception
     */
    @Test
    public void queryBillStream() throws Exception {
        MvcResult result = mockMvc.perform((post("/control/credit/queryBillStream.go")
                .param("billId", "3715")
                .param("appId", "lc2O0WL1UX612R1TFS2PH022I627N62D8")
                .param("accessToken", "+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvutl2y/TXWNgksCB+TCFcvEJOf4YaTRJFmqYJ3uL+fTlp3n6h7CXnorHsJ1arMfJb5P8c9o0ecrheuVEDeLuuwoXwd9SsZiiuX0UxIN+HvJvZtiix0dAOpB+mVdgLPZ4uTj0hLv3gRWTg==")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }


    /**
     * 消费类型测试
     * @throws Exception
     */
    @Test
    public void queryConsumeType() throws Exception {
        MvcResult result = mockMvc.perform((post("/control/credit/queryConsumeType.go")
                .param("billId", "3111")
                .param("appId", "lc2O0WL1UX612R1TFS2PH022I627N62D8")
                .param("accessToken", "+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvutl2y/TXWNgksCB+TCFcvEJOf4YaTRJFmqYJ3uL+fTlp3n6h7CXnorHsJ1arMfJb5P8c9o0ecrheuVEDeLuuwoXwd9SsZiiuX0UxIN+HvJvZtiix0dAOpB+mVdgLPZ4uTj0hLv3gRWTg==")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

}
