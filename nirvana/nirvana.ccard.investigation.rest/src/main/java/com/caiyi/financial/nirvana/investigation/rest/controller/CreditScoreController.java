package com.caiyi.financial.nirvana.investigation.rest.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.credit.utils.LoanUtil;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by jianghao on 2016/11/29.
 * 征信接口
 */
@RestController
public class CreditScoreController {

    private static Logger logger = LoggerFactory.getLogger(CreditScoreController.class);
    @Resource(name = Constant.HSK_CCARD_INVESTIGATION)
    public IDrpcClient client;

    @Resource(name = Constant.HSK_CCARD_INFO)
    public IDrpcClient clientCard;

    /**
     * 征信首页接口
     */
    @SetUserDataRequired
    @RequestMapping("/notcontrol/investigation/creditScoreIndex.go")
    public JSONObject creditScoreIndex(CreditScoreBean creditScoreBean) {
        long start = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        logger.info("userid:" + creditScoreBean.getCuserId());
        if (StringUtils.isEmpty(creditScoreBean.getCuserId())) {
            jsonObject.put("code", 1);
            jsonObject.put("desc", "用户未登录");
            jsonObject.put("data", "");
            return jsonObject;
        }
        String jsonRes = client.execute(new DrpcRequest("creditScore", "CreditScoreIndex", creditScoreBean));
        jsonObject = JSONObject.parseObject(jsonRes);
        long takeTime = System.currentTimeMillis() - start;
        logger.info("creditScoreIndex请求消耗时间:" + takeTime);
        if (takeTime > 3000) {
            logger.info("creditScoreIndex请求消耗时间:{},用户id:" + creditScoreBean.getCuserId());
        }
        return jsonObject;
    }

//    /**
//     * 更新征信数据
//     */
//    @RequestMapping("/notcontrol/investigation/updateCreditData.go")
//    public JSONObject updateZxData(CreditScoreBean creditScoreBean) {
//        String jsonRes = client.execute(new DrpcRequest("creditScore", "updateZxData", creditScoreBean));
//        JSONObject jsonBoltResult = JSONObject.parseObject(jsonRes);
//        return jsonBoltResult;
//    }
//
//    /**
//     * 更新信用卡数据
//     */
//    @RequestMapping("/notcontrol/investigation/updateCreditCardData.go")
//    public JSONObject updateXykData(CreditScoreBean creditScoreBean) {
//        String jsonRes = client.execute(new DrpcRequest("creditScore", "updateXykData", creditScoreBean));
//        JSONObject jsonBoltResult = JSONObject.parseObject(jsonRes);
//        return jsonBoltResult;
//    }
//
//    /**
//     * 更新公积金数据
//     */
//    @RequestMapping("/notcontrol/investigation/updateProvidentFundData.go")
//    public JSONObject updateGjjData(CreditScoreBean creditScoreBean) {
//        String jsonRes = client.execute(new DrpcRequest("creditScore", "updateGjjData", creditScoreBean));
//        JSONObject jsonBoltResult = JSONObject.parseObject(jsonRes);
//        return jsonBoltResult;
//    }

//    /**
//     * 更新社保数据
//     */
//    @RequestMapping("/notcontrol/investigation/updateSocialSecurity.go")
//    public JSONObject updateSbData(CreditScoreBean creditScoreBean) {
//        String jsonRes = client.execute(new DrpcRequest("creditScore", "updateSbData", creditScoreBean));
//        JSONObject jsonBoltResult = JSONObject.parseObject(jsonRes);
//        return jsonBoltResult;
//    }

    /**
     * 获取信用特权数据
     */
    @RequestMapping("/control/investigation/creditPrivilege.go")
    public JSONObject creditPrivilege(CreditScoreBean creditScoreBean) {
        String jsonRes = client.execute(new DrpcRequest("creditScore", "creditPrivilege", creditScoreBean));
        JSONObject jsonBoltResult = JSONObject.parseObject(jsonRes);
        return jsonBoltResult;
    }

    /**
     * 信用生活
     *
     * @param creditScoreBean
     * @return
     */
    @RequestMapping("/notcontrol/investigation/getCreditLife.go")
    public JSONObject queryCreditLife(CreditScoreBean creditScoreBean) {
        long start = System.currentTimeMillis();
        String boltResult = client.execute(new DrpcRequest("creditScore", "queryCreditLife", creditScoreBean));
        logger.info("特权和banner消耗时间:{}", (System.currentTimeMillis() - start));
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject jsonBoltResult = JSONObject.parseObject(boltResult);
        if (jsonBoltResult != null) {
            if (jsonBoltResult.get("banners") != null) {
                data.put("Banners", jsonBoltResult.get("banners"));
            }
            if (jsonBoltResult.get("privileges") != null) {
                data.put("Boutique", jsonBoltResult.get("privileges"));
            }
            long cardStart = System.currentTimeMillis();
            String cardInfo = clientCard.execute(new DrpcRequest("cardYouyu", "queryTopOneCard", creditScoreBean.getHskcityid()));
            if (StringUtils.isNotEmpty(cardInfo)) {
                JSONObject tmpCardJson = JSONObject.parseObject(cardInfo);
                JSONObject newCardJson = new JSONObject();
                if (tmpCardJson != null) {
                    newCardJson.put("picUrl", tmpCardJson.get("picUrl"));
                    newCardJson.put("cardName", tmpCardJson.get("cardName"));
                    newCardJson.put("cardId", tmpCardJson.get("cardId"));
                    JSONArray list = new JSONArray();
                    list.add(newCardJson);
                    data.put("cards", list);
                }
            }
            long cardEnd = System.currentTimeMillis();
            logger.info("办卡消耗时间:{}", (cardEnd - cardStart));
            JSONObject loanInfoJson = LoanUtil.getZhengxinHotLoan(creditScoreBean.getAdcode(),
                    creditScoreBean.getIclient(), creditScoreBean.getPackagename(), creditScoreBean.getSource());
            if (loanInfoJson != null) {
                JSONArray loanList = loanInfoJson.getJSONArray("data");
                data.put("Loans", loanList);
            }
            logger.info("贷款消耗时间:{}", (System.currentTimeMillis() - cardEnd));
            result.put("code", 1);
            result.put("data", data);
            result.put("desc", "请求数据成功");
        } else {
            result.put("code", 0);
            result.put("desc", "服务异常");
        }
        logger.info("信用生活总消耗时间:{}", (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * 查询历史积分
     *
     * @param creditScoreBean
     * @return
     */
    @RequestMapping("/control/investigation/getHistoryCreditScores.go")
    public JSONObject getHistoryCreditScores(CreditScoreBean creditScoreBean) {
        JSONObject result = new JSONObject();
        JSONObject resultClient;
        String scoreInfo = client.execute(new DrpcRequest("creditScore", "getHistoryScores", creditScoreBean));
        resultClient = JSONObject.parseObject(scoreInfo);
        if (resultClient != null) {
            result.put("code", "1");
            result.put("desc", "请求信息成功");
            result.put("data", resultClient);
        } else {
            result.put("code", "0");
            result.put("desc", "历史积分程序异常");
        }
        return result;
    }


    /**
     * 查询新征信分数
     *
     * @param creditScoreBean
     * @return 征信分数
     */
    @RequestMapping("/control/investigation/getNewCreditScore.go")
    public BoltResult getNewCreditScore(CreditScoreBean creditScoreBean) {
        long start = System.currentTimeMillis();
        BoltResult boltResult = new BoltResult();
        DrpcRequest drpcRequest = new DrpcRequest("newCreditScoreBolt", "getFinalCreditScore", creditScoreBean);
        try {
            String newCreditScore = client.execute(drpcRequest);
            JSONObject result = JSONObject.parseObject(newCreditScore);
            if (result != null && "1".equals(result.get("code"))) {
                boltResult.setData(result.get("data"));
                boltResult.setDesc("查询成功");
                boltResult.setCode("1");
            }else {
                boltResult.setCode("0");
                boltResult.setDesc("该用户暂时没有分数");
                boltResult.setData("该用户暂时没有分数");
            }
        } catch (Exception e) {
            boltResult.setCode("0");
            boltResult.setData("网络错误");
            boltResult.setDesc("网络错误");
        } finally {
            long end = System.currentTimeMillis();
            logger.info("查询新征信分数接口耗时：" + (end - start));
            return boltResult;
        }
    }


}
