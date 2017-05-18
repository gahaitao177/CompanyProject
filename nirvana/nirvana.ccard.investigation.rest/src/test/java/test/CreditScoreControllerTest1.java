package test;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lizhijie on 2016/12/14.
 */
public class CreditScoreControllerTest1 extends TestSupport {

    @Test
    public void  TestCreditLife() throws  Exception{
//        MvcResult result = mockMvc.perform((post("/notcontrol/investigation/getCreditLife.go")
//                .param("hskcityid", "100")
//        ))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//        String str = result.getResponse().getContentAsString();
//        System.out.println("strAA:"+str);
//        Assert.assertTrue("test1".equals(str));
    }

    @Test
    public void  TestHistoryScores() throws  Exception{
        String appId="lt2DO01612271Q15LXZ5KL13VKGYH9N00";
        String accessToken="%2BNEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvvON%2FC7lo0c7Xzs6WcQxWBeoQs6jugZ%" +
                "2FyyEhwogHsxZGaK%2BQiZWqE8ypSyuKB%2FslhvDp77SNM9iEPsKXRcZDGBeyHZ0agC67T47tzzYPEpeM1DlRw" +
                "JC7BEWQ4eFHrvBZB%2B11DoXjPCLBw%3D%3D";
        MvcResult result = mockMvc.perform((post("control/investigation/getHistoryCreditScores.go")
                .param("hskcityid", "100")
                .param("appId", appId)
                .param("accessToken",accessToken)
                .param("forceCaculate","1")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString();
        System.out.println("strBB:"+str);
//        Assert.assertTrue("test1".equals(str));
    }
}
