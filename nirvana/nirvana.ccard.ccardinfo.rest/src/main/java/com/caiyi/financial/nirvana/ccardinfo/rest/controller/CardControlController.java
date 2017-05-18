package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.credit.utils.LoanUtil;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by lichuanshun on 16/10/20.
 * 有关卡的新接口 写在此类中
 */
@RestController
public class CardControlController {
    private static Logger logger = LoggerFactory.getLogger(CardControlController.class);
    @Resource(name = Constant.HSK_CCARD_INFO)
    private IDrpcClient drpcClient;
    @Resource(name = Constant.HSK_USER)
    private IDrpcClient userClient;

    @SetUserDataRequired
    @RequestMapping("/notcontrol/credit/newHomePage.go")
    public JSONObject queryNewIndex(Card card){
        JSONObject newIndex = new JSONObject();
        logger.info("queryNewIndex uid:" +  card.getCuserId());
        newIndex.put("code","1");
        newIndex.put("desc","查询");
        //
        JSONObject data = new JSONObject();

        JSONArray bannerArr = new JSONArray();

        JSONArray quricArr = new JSONArray();

        JSONArray hotNewsArr = new JSONArray();

        JSONArray boutiqueArr = new JSONArray();

        JSONObject rank = new JSONObject();

        JSONArray welfareArr = new JSONArray();

        JSONArray loan = new JSONArray();

        // 查询旧数据
        try {
            String result = userClient.execute(new DrpcRequest("HomePageBolt", "homePage", card));
            JSONObject oldHomeJson = JSONObject.parseObject(result);
            logger.info(oldHomeJson.toJSONString());
            if ("1".equals(oldHomeJson.getString("code"))) {
                JSONArray allOldDataArr = oldHomeJson.getJSONArray("data");
                for (Object dataObj : allOldDataArr) {
                    JSONObject tempDataJson = JSONObject.parseObject(dataObj.toString());
                    if ("BANNER".equals(tempDataJson.getString("type"))) {
                        bannerArr.add(tempDataJson);
                    }
                    if ("HOTMSG".equals(tempDataJson.getString("type"))) {
                        hotNewsArr.add(tempDataJson);
                    }
                    if ("SEM".equals(tempDataJson.getString("type"))) {
                        boutiqueArr.add(tempDataJson);
                    }
                }
            }
            // 查询新配置
            String newConfStr = drpcClient.execute(new DrpcRequest("cardYouyu", "newHomeIndex",card));
            JSONObject newConfJson = JSONObject.parseObject(newConfStr);
            if (newConfJson != null){
                if (newConfJson.get("quick") != null){
                    quricArr = newConfJson.getJSONArray("quick");
                }
                if (newConfJson.get("welfare") != null){
                    welfareArr = newConfJson.getJSONArray("welfare");
                }
                if (newConfJson.get("topCard") != null){
                    rank = newConfJson.getJSONObject("topCard");
                }
            }
        } catch (Exception e) {
            logger.error("queryNewIndex",e);
        }
        JSONObject loanResult = LoanUtil.getHskHotLoan(card.getAdcode(),card.getIclient(),card.getPackagename()
                ,card.getSource());
        if ("1".equals(loanResult.getString("code"))) {
            loan = loanResult.getJSONArray("data");
        }
        data.put("banners",bannerArr);
        data.put("quicks",quricArr);
        data.put("hotnews",hotNewsArr);
        data.put("boutique",boutiqueArr);
        data.put("rank",rank);
        data.put("welfare",welfareArr);
        data.put("loan",loan);
        newIndex.put("data",data);
        return newIndex;
    }


    /**
     * 人气榜单
     * @since 惠刷卡第十四次迭代
     * @param card
     * @return
     */
    @RequestMapping("/notcontrol/credit/queryTopCards.go")
    public String queryTopTenCards(Card card) {
        return drpcClient.execute(new DrpcRequest("cardYouyu", "queryTopTenCards",card));
    }
    /**
     * 第十三次迭代 新版本办卡首页下发
     * add by lcs 20161026
     *
     * @return
     */
    @RequestMapping("/notcontrol/credit/cardapplyindex.go")
    public String cardApplyIndex(Card card) {
        logger.info("CardControlController:cardApplyIndex start " + card.getHskcityid());
        String cardResult = drpcClient.execute(new DrpcRequest("cardYouyu", "cardApplyIndex", card));
        return cardResult;
    }

    /**
     * 中信银行callBack
     *
     * @return
     */
    @RequestMapping("/notcontrol/credit/eciticcallback.go")
    public String eciticCallBack(Card card, HttpServletRequest request) {
        //加个注
        String adid = request.getParameter("para1");
        String orderid = request.getParameter("para2");
        String time = request.getParameter("para3");
        logger.info("adid:" + adid + ",orderid:" + orderid + ",time:" + time);
        card.setBankid("2");
        card.setAdid(adid);
        card.setOrderid(orderid);
        card.setTimestamp(time);
        card.setIstatus("1");
        return bankCallBack(card);
    }

    /**
     * 银行统一回调接口
     *
     * @return
     */
    private String bankCallBack(Card card) {
        String cardResult = drpcClient.execute(new DrpcRequest("cardYouyu", "saveBankCallBack", card));
        return cardResult;
    }

    /**
     * 银行统一回调接口
     *
     * @return
     */
    private String queryOrderId(Card card) {
        logger.info("orderid:" + card.getOrderid() + ",bankid:" + card.getBankid());
        String cardResult = drpcClient.execute(new DrpcRequest("cardYouyu", "queryCallBackInfo", card));
        return cardResult;
    }

    /**
     * 查询用户状态
     *
     * @param card
     * @param request
     * @return
     */
    @RequestMapping("/notcontrol/card/checkOrderId.go")
    public String checkEciticOrderId(Card card, HttpServletRequest request) {
        JSONObject cardResult = new JSONObject();
        cardResult.put("code", "0");
        cardResult.put("desc", "订单不存在");
        card.setBankid("2");
        String queryResult = queryOrderId(card);
        logger.info("queryResult:" + queryResult);
        if ("null".equals(queryResult) || "0".equals(queryResult)) {
            cardResult.put("code", "0");
            cardResult.put("desc", "订单不存在");
        } else {
            cardResult.put("code", "1");
            cardResult.put("desc", "订单存在");
        }
        return cardResult.toJSONString();
    }

    /**
     * 查询银行 功能的开关 比如bk=1 表示办卡开启
     *
     * @param card
     * @return
     */
    @RequestMapping("/notcontrol/card/qBankFlag.go")
    public BoltResult queryBankFlagByBankId(Card card) {
        BoltResult boltResult = new BoltResult();
        if (StringUtils.isEmpty(card.getBankid())) {
            boltResult.setCode("0");
            boltResult.setDesc("银行id不能为空");
            return boltResult;
        } else if (!StringUtils.isNumeric(card.getBankid())) {
            boltResult.setCode("0");
            boltResult.setDesc("参数有误");
            return boltResult;
        }
        boltResult = drpcClient.execute(
                new DrpcRequest("cardYouyu", "queryBankFlagByBankId", card.getBankid()),BoltResult.class);
        if(boltResult==null){
            boltResult = new BoltResult();
            boltResult.setCode("0");
            boltResult.setDesc("程序异常");
        }
        return boltResult;
    }

}
