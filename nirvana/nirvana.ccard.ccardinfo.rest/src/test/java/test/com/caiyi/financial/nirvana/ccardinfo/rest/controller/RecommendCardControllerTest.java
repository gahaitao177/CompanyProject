package test.com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lizhijie on 2017/1/11.
 */
public class RecommendCardControllerTest extends TestSupport {

    /**
     *  推荐卡
     * @throws Exception
     */
    @Test
    public void queryRecommendCards() throws Exception{
        MvcResult result = mockMvc.perform((post("/notcontrol/card/queryRecommendCards.go")
                .param("adcode","310100")
//                .param("ps","2")
//                .param("pn","2")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("推荐卡列表:"+str);
    }
    @Test
    public void queryRecommendCardDetail() throws Exception{
        MvcResult result=mockMvc.perform((post("/notcontrol/card/queryRecommendCardDetail.go")
                .param("cardId","2222")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }
    @Test
    public void updateClickCount() throws Exception{
        MvcResult result=mockMvc.perform((post("/notcontrol/card/updateClickCount.go")
                .param("cardId","3")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();

        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("更新点击量:"+str);
    }
}
