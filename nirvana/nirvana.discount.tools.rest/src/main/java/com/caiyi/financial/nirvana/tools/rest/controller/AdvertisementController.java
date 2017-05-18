package com.caiyi.financial.nirvana.tools.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.discount.tools.bean.FeedBackBean;
import com.caiyi.financial.nirvana.discount.tools.bean.IdfaBean;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by zhukai on 2016/12/5.
 *
 * 广告合作相关
 */
@RestController
public class AdvertisementController {
    // MD5 盐
    private final String SALT_KEY = "16H0M1OOPQZL21GE030D728B72";

    // idfa保存 签名的key
    private final String IDFA_SIGN_KEY = "987hjbf8a!s97*dtqw^be7^73";
    //idfa保存 加密用的盐
    private final String IDFA_SIGN_SALT = "932847yjisddabf7qwih09e8yriu*^98";

    @Resource(name = Constant.HSK_TOOL)
    IDrpcClient client;
    private static Logger log = LoggerFactory.getLogger(AdvertisementController.class);

    @RequestMapping("/notcontrol/credit/ad/click.go")
    public JSONObject clickIdfa(IdfaBean idfaBean) {
        log.info("into>>>>>>>>>>>>>>>>>>>>>IdfaController");
        log.info("into>>>>>>>>>>>>>>>>>>>>>IdfaController" + idfaBean);
        JSONObject result = new JSONObject();
        //如果Appid为空则返回
        if (CheckUtil.isNullString(idfaBean.getAppid())) {
            result.put("code", 1);
            result.put("result", "AppID不能为空");
            return result;
        }
        //如果idfa为空则返回
        if (CheckUtil.isNullString(idfaBean.getIdfa())) {
            result.put("code", 2);
            result.put("result", "用户idfa不能为空");
            return result;
        }
        //如果Sign为空则返回
        if (CheckUtil.isNullString(idfaBean.getSign())) {
            result.put("code", 3);
            result.put("result", "参数签名不能为空");
            return result;
        }
        if (CheckUtil.isNullString(idfaBean.getCallback()) || CheckUtil.isNullString(idfaBean.getSource())
                || CheckUtil.isNullString(idfaBean.getTimestamp())){
            result.put("code", 4);
            result.put("result", "非法的参数");
            return result;
        }
        if (!getMd5Sign(idfaBean)) {
            result.put("code", 4);
            result.put("result", "参数签名错误");
            return result;
        }
        String saveResult = client.execute(new DrpcRequest("advertisement", "saveAdIdfa", idfaBean));
        return JSONObject.parseObject(saveResult);
    }

    /***
     *  判读签名是否正确
     * @param idfaBean
     * @return
     */
    private boolean getMd5Sign(IdfaBean idfaBean) {
        boolean result = false;
        try {
            StringBuffer sb = new StringBuffer();
            String sign = idfaBean.getSign();
            sb.append("appid=" + idfaBean.getAppid());
            sb.append("&callback=" + idfaBean.getCallback());
            sb.append("&idfa=" + idfaBean.getIdfa());
            sb.append("&source=" + idfaBean.getSource());
            sb.append("&timestamp=" + idfaBean.getTimestamp());
            sb.append(SALT_KEY);
            String localSign = MD5Util.compute(sb.toString());
            log.info("拼接字符串" + sb.toString());
            log.info("localSign<<<<<<<<<<<<=" + localSign);
            log.info("sign<<<<<<<<<<<<=" + sign);
            if (sign.equals(localSign)) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    /**
     *
     * @param backBean
     * @return
     */
    @RequestMapping("credit/checkIdfa.go")
    public String checkIosUserExist(FeedBackBean backBean){
        JSONObject result = new JSONObject();
        result.put("code","1");
        result.put("desc","success");
        String idfa = backBean.getIdfa();
        log.info("idfa:" + idfa + ",source:" + backBean.getSource());
        if(CheckUtil.isNullString(idfa)){
            result.put("code","0");
            result.put("desc","参数错误");
            return result.toJSONString();
        }
        return client.execute(new DrpcRequest("FeedBackBolt","checkIosUserExist",backBean));
    }

    /**
     *
     * @param backBean
     * @return
     */
    @RequestMapping("credit/iosIdfaSave.go")
    public BoltResult iosIdfaSave(FeedBackBean backBean){
        BoltResult result = new BoltResult("1","success");
        String idfa = backBean.getIdfa();
        log.info("idfa:" + idfa + ",source:" + backBean.getSource());
        if(CheckUtil.isNullString(idfa)){
            result.setCode("1");
            result.setDesc("参数错误");
            return result;
        }
        if (backBean.getSource() == null){
            backBean.setSource(0);
        }
        // 如果是新版本 有验证签名 和加密操作
        boolean isSigned = false;
        if (!CheckUtil.isNullString(backBean.getSignMsg())){
            isSigned = checkIdfaSaveSign(backBean);
            if (isSigned){
                String realIdfa = CaiyiEncryptIOS.dencryptStr(idfa);
                log.info("解密后idfa:" + realIdfa);
                realIdfa = realIdfa.replace(IDFA_SIGN_SALT, "");
                backBean.setIdfa(realIdfa);
            } else {
                result.setCode("0");
                result.setDesc("验签失败");
                return result;
            }
        }
        result = client.execute(new DrpcRequest("FeedBackBolt", "iosIdfaSave", backBean), BoltResult.class);
        String callBackUrl = String.valueOf(result.getData());
        log.info(idfa + "callBackUrl  " + callBackUrl);
        if (isSigned && !CheckUtil.isNullString(callBackUrl) && callBackUrl.contains("http")) {
            String callBackReslt = HttpClientUtil.callHttpPost_Map(callBackUrl, null);
            log.info("callBackUrl" + callBackReslt);
        }
        result.setData("");
        return result;
    }

    /**
     *
     * @param backBean
     * @return
     */
    private boolean checkIdfaSaveSign(FeedBackBean backBean){
        boolean result = false;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("signType=" + backBean.getSignType());
            sb.append("&key=" +  IDFA_SIGN_KEY);
            sb.append("&idfa=" +  backBean.getIdfa());
            sb.append("&timestamp=" + backBean.getTimeStamp());
//            log.info("sbstr:" + sb.toString());
            String localSign = MD5Util.compute(sb.toString()).toUpperCase();
            log.info("localSign:" + localSign);
            log.info("getSignMsg:" + backBean.getSignMsg());
            if (localSign.equals(backBean.getSignMsg())){
                result = true;
            }
        }catch (Exception e){
            log.error("checkIdfaSaveSign:" ,e);
        }
        log.info(backBean.getIdfa() + (result?"验签成功":"验签失败"));
        return result;
    }
}
