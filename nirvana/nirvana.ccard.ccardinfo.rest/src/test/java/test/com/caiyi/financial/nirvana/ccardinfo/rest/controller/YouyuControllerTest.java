package test.com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by lichuanshun on 16/12/8.
 */
public class YouyuControllerTest extends TestSupport {


    /**
     *  有鱼金融
     * @throws Exception
     */
    @Test
    public void queryYouYuNews() throws Exception{
        MvcResult result = mockMvc.perform((post("/notcontrol/youyu/queryYouyuNews.go")
                .param("hskcityid","101")
                .param("adcode","150110")
                .param("cuserId","test123s")
//                .param("packagename","testcredit")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }
    /**
     *  有鱼金融
     * @throws Exception
     */
    @Test
    public void newsCollect() throws Exception{
        MvcResult result = mockMvc.perform((post("/notcontrol/youyu/homepage.go")
                .param("hskcityid","101")
                .param("adcode","150110")
                .param("source","6000")
//                .param("packagename","testcredit")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    /**
     *  有鱼金融
     * @throws Exception
     */
    @Test
    public void testYouyuHomePage() throws Exception{
        MvcResult result = mockMvc.perform((post("/notcontrol/youyu/homepage.go")
                .param("hskcityid","101")
                .param("adcode","150110")
                .param("source","6000")
//                .param("packagename","testcredit")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    /**
     * 新版首页
     * @throws Exception
     */
    @Test
    public void testNewIndex() throws Exception{
        MvcResult result = mockMvc.perform((post("/notcontrol/credit/newHomePage.go")
                .param("hskcityid","101")
                .param("adcode","150110")
                .param("source","6000")
                .param("packagename","testcredit")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }


    /**
     * 人气榜单
     * @throws Exception
     */
    @Test
    public void testTopCards() throws Exception{
        MvcResult result = mockMvc.perform((post("http://hsk.gs.9188.com/notcontrol/credit/queryTopCards.go")
                .param("hskcityid","101")
                .param("adcode","150110")
        ))
                .andExpect(status().isOk()) //status 是否为200
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println(str);
    }

    /**
     * 查询订单是否存在
     * @throws Exception
     */
    @Test
    public void testOrderById() throws  Exception{
        MvcResult result = mockMvc.perform((post("http://hsk.gs.9188.com/notcontrol/card/checkOrderId.go")
                .param("orderid","lcs")
                .param("bankid","2")
        ))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString();
        System.out.print(str);

    }
}
