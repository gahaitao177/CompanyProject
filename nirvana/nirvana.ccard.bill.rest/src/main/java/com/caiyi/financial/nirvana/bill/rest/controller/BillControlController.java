package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.util.BillErrorAndTips;
import com.caiyi.financial.nirvana.bill.util.TempBankInfos;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
/**
 * Created by lichuanshun on 16/8/18.
 */
@RestController
public class BillControlController {

    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    private static Logger logger = LoggerFactory.getLogger(BillControlController.class);
    /**
     *
     * @param bean
     * created by lcs 20160821 卡管理列表
     */
    @RequestMapping("/control/credit/billManageIndex.go")
    public String billManageIndex(Channel bean){
        logger.info("bean cuserId:-------" +bean.getCuserId());
        String jsonres = client.execute(new DrpcRequest("billManage", "billIndex", bean));
        logger.info("------------billManageIndex:" +jsonres);
        return jsonres;
    }
    /**
     *
     * @param bean
     * @return
     * 修改卡片信息
     */
    @RequestMapping("/control/credit/updateBillInfo.go")
    public String updateBillInfo(Channel bean){
        logger.info("username:" + bean.getUsername() + ",card4num:" + bean.getCard4Num() + ",cashAmount:" + bean.getCashAmount());
        logger.info("bean cuserId:-------" +bean.getCuserId());
        String jsonres = client.execute(new DrpcRequest("billManage", "updateBillInfo", bean));
        return jsonres;
    }
    /**
     * 临时额度接口
     *
     * @param channel
     * @return
     * add by lcs 20160826
     */
    @SetUserDataRequired
    @RequestMapping("/notcontrol/credit/bankTempQuota.go")
    public String temporaryBanks(Channel channel){
        JSONObject res = new JSONObject();
        res.put("code","1");
        res.put("desc","查询成功");;
        // data
        JSONObject data = new JSONObject();
        data.put("articleUrl",TempBankInfos.KASHENURL);
        // 已经导入
        JSONArray importBillArr = new JSONArray();
        // 全部银行
        JSONArray allTempBankInfos = new JSONArray();
        //
        try {
            String uid = channel.getCuserId();
            logger.info("uid:"+uid);
            logger.info("desc"+channel.getBusiErrDesc());
            if (!CheckUtil.isNullString(uid)){
                // 获取已导入账单
                String jsonres = client.execute(new DrpcRequest("billManage", "billIndex", channel));
                JSONObject jsonResult = JSONObject.parseObject(jsonres);
                JSONArray billArrTemp = jsonResult.getJSONArray("data");
                if ("1".equals(jsonResult.getString("code")) && (billArrTemp != null && billArrTemp.size() > 0)){
                    //
                    for (Object bill :billArrTemp){
                        JSONObject importBill = JSONObject.parseObject(String.valueOf(bill));
                        JSONObject temlJson = new JSONObject();
                        temlJson.put("bankId",importBill.getString("bankid"));
                        temlJson.put("bankName",importBill.getString("bankname"));
                        if (!importBillArr.contains(temlJson)){
                            importBillArr.add(temlJson);
                        }
                    }
                }
            }
            allTempBankInfos = readJsonConfig(importBillArr);
            data.put("myBankList",importBillArr);
            data.put("otherBankList",allTempBankInfos);
        }catch (Exception e){
            logger.error("temporaryBanks:" ,e);
            res.put("code","0");
            res.put("desc","查询失败");
        }
        res.put("data",data);
        return res.toJSONString();
    }
    /**
     * 信用卡临时额度
     *
     * @param channel
     * @return
     * add by lcs 20160826
     */
    @RequestMapping("/notcontrol/credit/tempQuotaInfo.go")
    public String temporaryQuotaInfo(Channel channel){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","1");
        jsonObject.put("desc","查询成功");
        JSONObject data = new JSONObject();
        String bankId = channel.getBankId();
        try {
            data= TempBankInfos.getBankInfoById(bankId);
        }catch (Exception e){
            logger.error("temporaryQuotaInfo", e);
            jsonObject.put("code","0");
            jsonObject.put("desc","查询失败");
        }
        jsonObject.put("data",data);
        return  jsonObject.toJSONString();
    }

    /**
     * 账单导入错误信息提示
     * @param channel
     * @return
     */
    @RequestMapping("/notcontrol/credit/billErrorTips.go")
    public String errorTips(Channel channel){
        String type = channel.getType();
        String bankId = channel.getBankId();
        String code = channel.getCode();
        logger.info("billErrorTips:type" + type + ", bankid = " + bankId + ",code=" +code);
        JSONObject tips = new JSONObject();
        tips.put("code","1");
        tips.put("desc","success");
        tips.put("data",BillErrorAndTips.getErrorAndTips(type,bankId));
        return  tips.toJSONString();
    }

    /**
     * 名词解释
     * @param channel
     * @return
     */
    @RequestMapping("/notcontrol/bill/nounExplain.go")
    public String nounExplain(Channel channel) {
        String keyword = channel.getKeyword();
        String bankid = channel.getBankId();
        logger.info("keyword:" +keyword + ",bankid=" + bankid);
        JSONObject explain = new JSONObject();
        explain.put("code","1");
        explain.put("desc","success");
        explain.put("data",BillErrorAndTips.getNounExplain(keyword,bankid));
        return explain.toJSONString();
    }

    /**
     *
     * @param channel
     * @return
     */
    @RequestMapping("/notcontrol/billconf/refresh.go")
    public String refreshConf(Channel channel){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code","1");
        jsonObject.put("desc","success");
        TempBankInfos.refreshTempBankConf();
        BillErrorAndTips.refreshTipConf();
        BillErrorAndTips.refreshExplainConf();
        return  jsonObject.toJSONString();
    }

    /**
     * 读取json配置信息
     */
    private JSONArray readJsonConfig(JSONArray importedBank){
        return TempBankInfos.getBankArr(importedBank);
    }


    public static void main(String[] args){
//        String config  = SystemConfig.get("zk_connect");
//        System.out.println(config);

    }

}
