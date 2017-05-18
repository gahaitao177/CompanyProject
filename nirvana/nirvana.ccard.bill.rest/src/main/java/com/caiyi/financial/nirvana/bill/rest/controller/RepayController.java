package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.WxRepayCB;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mario on 2016/7/7 0007.
 *
 * update by lcs 20160909 重写此类的方法
 */
@RestController
public class RepayController {
    private static Logger logger = LoggerFactory.getLogger(RepayController.class);
    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;

    /**
     * 生成新的订单,返回用户账单信息
     * 微信，卡卡贷共用
     * type,billId
     * @param bean
     *
     * update by lcs 20160909  重写此方法 notcontrol-->>control 直接返回
     */
    @RequestMapping("/control/credit/newRepayOrder.go")
    public String newRepayOrder(Channel bean) {
        String jsonRes = "";
        if(CheckUtil.isNullString(bean.getType())){
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("code","0");
            jsonResult.put("desc","新订单创建失败");
            jsonRes = jsonResult.toJSONString();
        }else{
            //还款方式 1:微信,2:卡卡贷
            if("1".equals(bean.getType())){
                jsonRes = client.execute(new DrpcRequest("wxRepay", "newRepayOrder", bean));
            }else if("2".equals(bean.getType())){
                // 卡卡贷
                jsonRes = client.execute(new DrpcRequest("KakaDaiRepayBolt","CreateNewBill",bean));
            }
        }
        logger.info("jsonRes:"+jsonRes);
        return jsonRes;
    }

    /**
     * 微信回调接口,接收还款结果，更新账单信息
     * @param bean
     */
    @RequestMapping("/notcontrol/wxRepayCallBack.go")
    public String weiChatRepayCallBack(WxRepayCB bean, HttpServletRequest request) {
        logger.info("进入微信到账回调 wxRepayCallBack");
        String result= client.execute(new DrpcRequest("wxRepay", "wxRepayCallBack", bean));
        logger.info("调用到账消息推送 wxRepayCallBack");
//        sendMessage(bean.getPartner_id());
        return result;
    }

    /**
     * 微信回调接口,接收支付结果，更新账单信息
     * @param bean
     */
    @RequestMapping("/notcontrol/wxPayCallBack.go")
    public String weiChatPayCallBack(WxRepayCB bean){
        logger.info("进入微信支付回调 wxPayCallBack");
        String rt= client.execute(new DrpcRequest("wxRepay", "wxPayCallBack", bean));
        logger.info("调用到账消息推送 wxPayCallBack");
//        sendMessage(bean.getPartner_id());
        return  rt;
    }

    /**
     * 根据partnerId查询订单信息
     * @param bean
     * @param response
     */
    @RequestMapping("/control/credit/queryRepayOrder.go")
    public String queryRepayOrder(WxRepayCB bean, HttpServletResponse response) {
       return client.execute(new DrpcRequest("wxRepay", "queryRepayOrder", bean));
    }
    private  void sendMessage(String partner_id){
//        logger.info("进入消息的推送 websocket");
//        String userId=client.execute(new DrpcRequest("message","getUserIdByPartnerId",partner_id));
//        logger.info("查询到的用户信息={}",userId);
//        JSONObject resultJson=JSONObject.parseObject(userId);
//        if(resultJson!=null&&"1".equals(resultJson.getString("code"))){
//            WebSocketSession socketSession= MessageHandler.webSocketMap.get(resultJson.getString("data"));
//            JSONObject json=new JSONObject();
//            if(socketSession!=null) {
//                try {
//                    String count=client.execute(new DrpcRequest("message","getValidMessageCount",resultJson.getString("data")));
//                    JSONObject countJson=JSONObject.parseObject(count);
//                    if("1".equals(countJson.getString("code"))) {
//                        json.put("type",1);
//                    }else {
//                        json.put("type",0);
//                    }
//                    socketSession.sendMessage(new TextMessage(json.toJSONString()));
//                    logger.info("controller 用户={}，发送消息={}", resultJson.getString("data"), json);
//                } catch (IOException e) {
//                    logger.info("controller 用户={}，发送异常={}", resultJson.getString("data"), e.toString());
//                }
//            }
//        }
    }
}
