package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.credit.utils.LoanUtil;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by lichuanshun on 16/12/9.
 * 有鱼金融现相关接口
 */
@RestController
public class YouyuController {
    private static Logger logger = LoggerFactory.getLogger(CardControlController.class);
    @Resource(name = Constant.HSK_CCARD_INFO)
    private IDrpcClient drpcClient;

    /**
     * 人气榜单
     * @since 惠刷卡第十四次迭代
     * @param card
     * @return
     */
    @RequestMapping("/notcontrol/youyu/homepage.go")
    public BoltResult queryTopTenCards(Card card) {
        BoltResult boltResult = new BoltResult();
        boltResult.setCode("1");
        boltResult.setDesc("success");
        String result = drpcClient.execute(new DrpcRequest("cardYouyu", "youYuHomePage",card));
        JSONObject homeJson = JSONObject.parseObject(result);
        logger.info("homeJson"+homeJson);
        JSONObject data = homeJson.getJSONObject("data");
        if (data == null){
            boltResult.setCode("0");
            boltResult.setDesc("查询失败");
            return boltResult;
        }
        JSONArray loan = new JSONArray();
        JSONObject loanResult = LoanUtil.getYouyuHotLoan(card.getAdcode(),card.getIclient(),card.getPackagename()
                ,card.getSource());
        if ("1".equals(loanResult.getString("code"))) {
            loan = loanResult.getJSONArray("data");
        }
        data.put("hotLoan",loan);
        boltResult.setData(data);
        return boltResult;
    }

    /**
     * 有鱼金融资讯首页
     * @param card
     * @return
     */
    @SetUserDataRequired
    @RequestMapping("/notcontrol/youyu/queryYouyuNews.go")
    public BoltResult queryYouYuNews(Card card){
        if (CheckUtil.isNullString(card.getCuserId())){
            card.setCuserId("hsk");
        }
        return drpcClient.execute(new DrpcRequest("cardYouyu","queryNewsPage",card),BoltResult.class);
    }

    /**
     * 资讯收藏
     * @param card
     * @return
     */
    @RequestMapping("/control/youyu/newsCollect.go")
    public BoltResult newsCollect(Card card) {
        return drpcClient.execute(new DrpcRequest("cardYouyu", "newsCollection", card),BoltResult.class);
    }

    /**
     * 资讯收藏查询
     * @param card
     * @return
     */
    @RequestMapping("/control/youyu/queryNewsCollect.go")
    public BoltResult queryNewsCollect(Card card) {
        return drpcClient.execute(new DrpcRequest("cardYouyu", "queryNewsCollect", card),BoltResult.class);
    }
}
