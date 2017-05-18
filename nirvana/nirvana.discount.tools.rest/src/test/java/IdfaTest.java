import com.caiyi.financial.nirvana.discount.tools.bean.IdfaBean;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import test.TestSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


/**
 * Created by zhukai on 2016/12/6.
 */
public class IdfaTest extends TestSupport {

    @Test
    public void test1() throws Exception {
        IdfaBean idfaBean=new IdfaBean();

        MvcResult result = mockMvc.perform((post("/notcontrol/ad.mycompany.cn/ad/click.do")
                .param("appid", "003").param("idfa","003").param("source","src").param("callback","callback").param("timestamp","timestamp")
                .param("sign","d7540129c40027cc1e98a9379f068d4d")

        ))
//                .andDo(print())
                .andReturn();
        String str = result.getResponse().getContentAsString(); //返回值
        System.out.println("###############################################");
        System.out.println(str);
          }
}
