package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.MessageService;
import com.caiyi.financial.nirvana.ccard.bill.bank.util.KafkaService;
import com.caiyi.financial.nirvana.ccard.bill.bank.util.LocalConfig;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.WxRepayCB;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.RepaymentOrderDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.annotation.BoltParam;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created By zhaojie 2016/7/7 10:45:48
 */
@Bolt(boltId = "wxRepay", parallelismHint = 1, numTasks = 1)
public class WeiChatRepayBolt extends BaseBolt {
    private BankImportService bankImportService;
    @Autowired
    private MessageService messageService;
    /**
     * 渠道号
     */
    private String CHANNEL_ID = "12";
    private String SHOWWXPAYTITLE = "1";
    private String SIGN_KEY = "c6d565ef762ffb119c8707db2882b57f";
    /**
     * HSK bankId 与 财付通 bank_alias,bank_type 映射Map
     * List 中 0是bank_alias，1是bank_type
     */
    Map<Integer, List<String>> mapBankTenpay;
    private String MESSAGE="message";

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        bankImportService = getBean(BankImportService.class);
        messageService=getBean(MessageService.class);
        //初始化映射字典
        mapBankTenpay = new LinkedHashMap<>();
        mapBankTenpay.put(10, new ArrayList<String>() {{
            add("cib");
            add("3001");
        }});//兴业银行
        mapBankTenpay.put(11, new ArrayList<String>() {{
            add("cmbc");
            add("3013");
        }});//民生银行
        mapBankTenpay.put(7, new ArrayList<String>() {{
            add("pab");
            add("3014");
        }});//平安银行
        mapBankTenpay.put(2, new ArrayList<String>() {{
            add("citic");
            add("3015");
        }});//中信银行
        mapBankTenpay.put(3, new ArrayList<String>() {{
            add("ceb");
            add("3021");
        }});//光大银行
        mapBankTenpay.put(4, new ArrayList<String>() {{
            add("abc");
            add("3024");
        }});//农业银行
        mapBankTenpay.put(14, new ArrayList<String>() {{
            add("icbc");
            add("1050");
        }});//工商银行
        mapBankTenpay.put(21, new ArrayList<String>() {{
            add("cmb");
            add("3017");
        }});//招商银行
        mapBankTenpay.put(1, new ArrayList<String>() {{
            add("gdb");
            add("3018");
        }});//广发银行
        mapBankTenpay.put(16, new ArrayList<String>() {{
            add("comm");
            add("3019");
        }});//交通银行
        mapBankTenpay.put(13, new ArrayList<String>() {{
            add("ccb");
            add("3022");
        }});//建设银行
        mapBankTenpay.put(15, new ArrayList<String>() {{
            add("boc");
            add("3027");
        }});//中国银行
        //mapBankTenpay.put(999,new ArrayList<String>(){{add("srcb"); add("3028");}});//上海农商
        mapBankTenpay.put(66, new ArrayList<String>() {{
            add("nbcb");
            add("3008");
        }});//宁波银行
        //mapBankTenpay.put(999,new ArrayList<String>(){{add("hkb"); add("3009");}});//汉口银行
        //mapBankTenpay.put(999,new ArrayList<String>(){{add("njcb"); add("3010");}});//南京银行
        mapBankTenpay.put(61, new ArrayList<String>() {{
            add("jsb");
            add("3011");
        }});//江苏银行
        mapBankTenpay.put(64, new ArrayList<String>() {{
            add("hzb");
            add("3012");
        }});//杭州银行
        mapBankTenpay.put(9, new ArrayList<String>() {{
            add("spdb");
            add("3023");
        }});//浦发银行
        mapBankTenpay.put(19, new ArrayList<String>() {{
            add("bosh");
            add("3025");
        }});//上海银行
        mapBankTenpay.put(12, new ArrayList<String>() {{
            add("bob");
            add("3026");
        }});//北京银行

        this.CHANNEL_ID = SystemConfig.get("weichat_repay_channelid");

        logger.info("---------------------WeiChatRepayBolt _prepare");
        logger.info("---------------------WeiChatRepayBolt CHANNEL_ID = " + this.CHANNEL_ID);
    }

    /**
     * 创建新的还款订单，返回新订单信息
     *
     * @param bean
     * @return
     */
    @BoltController
    public JSONObject newRepayOrder(Channel bean) {
        JSONObject jsonObj = new JSONObject();
        logger.info("微信还款，创建新订单.");
        try {
            //查询账单数据
            BankBillDto bbd = bankImportService.getUserBankBillById(bean.getBillId());
            if (bbd.getUname() == null || bbd.getUname().isEmpty()) {
//                logger.info("无法获取银行卡开户名,billid=" + bean.getBillId());
//                jsonObj.put("code", 0);
//                jsonObj.put("desc", "请更新您的账单后再试.");
//                jsonObj.put("params", "");
//                return jsonObj;
                bbd.setUname("请输入姓名");
            }
            if (bbd.getIcard4num() == null || bbd.getIcard4num().isEmpty()) {
                logger.info("无法获取银行卡尾号,billid=" + bean.getBillId());
                jsonObj.put("code", 0);
                jsonObj.put("desc", "请更新您的账单后再试.");
                jsonObj.put("params", "");
                return jsonObj;
            }
            logger.info("微信还款,查询账单成功：billid=" + bean.getBillId());
            //获取参数,初始化新订单的dto
            RepaymentOrderDto rod = new RepaymentOrderDto();
            rod.setCchannel_id(this.CHANNEL_ID);
            logger.info("渠道ID,channel_id = " + this.CHANNEL_ID);
            List<String> listTemp = mapBankTenpay.get(bbd.getIbankid());
            if (listTemp == null || listTemp.size() == 0) {
                logger.info("不支持的银行,hsk银行ID:" + bbd.getIbankid());
                jsonObj.put("code", 0);
                jsonObj.put("desc", "该银行暂不支持");
                jsonObj.put("data", "");
                return jsonObj;
            }
            rod.setCbank_alias(listTemp.get(0));
            rod.setCcard_tail(bbd.getIcard4num());
            rod.setCcard_name(bbd.getUname());
            Date curDate = new Date();
            rod.setTime_stamp(curDate.getTime() + "");
            rod.setIbill_id(Integer.parseInt(bean.getBillId()));
            rod.setCuser_id(bbd.getCuserid());
            rod.setCbank_type(listTemp.get(1));
            rod.setDapply_time(new java.sql.Timestamp(curDate.getTime()));
            rod.setIpay_type(1);
            rod.setCpartner_id(this.getNewPartnerId());
            if (bean.getRepayAmount() == null || bean.getRepayAmount().equals("")) {
                rod.setIamount(bbd.getIshouldrepayment());
            } else {
                rod.setIamount(Double.parseDouble(bean.getRepayAmount()));
            }
            //拼接参数,按照字段名的ASCII码从小到大排序（字典序）
            String params = "amount=" + Integer.toString((int) (rod.getIamount() * 100));
            params += "&bank_alias=" + rod.getCbank_alias();
            params += "&card_name=" + rod.getCcard_name();
            params += "&card_tail=" + rod.getCcard_tail();
            params += "&channel_id=" + rod.getCchannel_id();
            params += "&time_stamp=" + rod.getTime_stamp();

            //计算Sign
            String sign = this.createSign(params, this.SIGN_KEY);
            rod.setCsign(sign);
            //存入数据库
            bankImportService.createRepaymentOrder(rod);
            //以下字段不参与计算Sign
            params += "&partner_id=" + rod.getCpartner_id();
            params += "&showwxpaytitle=" + this.SHOWWXPAYTITLE;
            params += "&sign=" + sign;
            logger.info("订单创建成功. partner_id = " + rod.getCpartner_id());
            jsonObj.put("code", 1);
            jsonObj.put("desc", "订单创建成功");

            JSONObject jsonData = new JSONObject();
            jsonData.put("amount", ((int) (rod.getIamount().floatValue() * 100)) + "");
            jsonData.put("bank_alias", rod.getCbank_alias());
            jsonData.put("card_name", rod.getCcard_name());
            jsonData.put("card_tail", rod.getCcard_tail());
            jsonData.put("channel_id", rod.getCchannel_id());
            jsonData.put("partner_id", rod.getCpartner_id());
            jsonData.put("showwxpaytitle", this.SHOWWXPAYTITLE);
            jsonData.put("time_stamp", rod.getTime_stamp());
            jsonData.put("sign", sign);
            logger.info(bean.getCuserId()+ jsonData);
            jsonObj.put("data", jsonData);
            return jsonObj;
        } catch (Exception e) {
            logger.warn("异常:" + e.getMessage());
            e.printStackTrace();
            jsonObj.put("code", 0);
            jsonObj.put("desc", "新订单创建失败");
            jsonObj.put("data", "");
            return jsonObj;
        }
    }

    /**
     * 还款结果
     * 提供给WX的回调接口，接受还款结果数据
     *
     * @param bean
     */
    @BoltController
    public JSONObject wxRepayCallBack(WxRepayCB bean) {
        logger.info("微信还款回调接口,全部参数:" + JSON.toJSONString(bean));
        JSONObject jsonObj = new JSONObject();
        try {
            String string1 = "amount=" + bean.getAmount();
            string1 += "&bank_type=" + bean.getBank_type();
            string1 += "&card_tail=" + bean.getCard_tail();
            string1 += "&partner_id=" + bean.getPartner_id();
            string1 += "&repay_time=" + bean.getRepay_time();
            string1 += "&state=" + bean.getState();
            string1 += "&wx_repay_no=" + bean.getWx_repay_no();

            String cSign = this.createSign(string1, this.SIGN_KEY);
            logger.info("计算sign值:" + cSign);
            //校验不通过
            if (!cSign.equals(bean.getSign().toUpperCase())) {
                logger.info("校验不通过.");
                jsonObj.put("code", "0");
                jsonObj.put("desc", "failed");
                return jsonObj;
            }
            logger.info("校验通过.");
            //校验通过,更新数据库
            RepaymentOrderDto rod = bankImportService.getRepaymentOrderByPartnerId(bean.getPartner_id());
            logger.info("查询订单成功,partnerId:" + bean.getPartner_id());
            if(rod!=null) {
                rod.setIwx_amount(Integer.parseInt(bean.getAmount()) / 100.0);
                rod.setCbank_type(bean.getBank_type());
                java.sql.Timestamp jstt = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getRepay_time()).getTime());
                rod.setDrepay_time(jstt);
                rod.setIstate(bean.getState());
                rod.setCwx_repay_no(bean.getWx_repay_no());
                rod.setCwx_sign(bean.getSign().toUpperCase());
                bankImportService.updateRepayOrder(rod);
                logger.info("更新订单状态成功.");

                //添加消息提醒
               Integer resultSaveMsg= messageService.fillMessage(bean, bean.getState());
                if(resultSaveMsg>0){
                    sendMessage(rod.getCuser_id());
                }
                jsonObj.put("code", "1");
                jsonObj.put("desc", "success");
                return jsonObj;
            }else {
                jsonObj.put("code", "0");
                jsonObj.put("desc", "没有查询到有效参数");
                return jsonObj;
            }
        } catch (Exception e) {
            logger.warn("异常:" + e.getMessage());
            e.printStackTrace();
            jsonObj.put("code", "0");
            jsonObj.put("desc", "failed");
            return jsonObj;
        }
    }

    /**
     * 支付结果
     * 提供给WX的回调接口，接受支付结果数据
     *
     * @param bean
     */
    @BoltController
    public JSONObject wxPayCallBack(WxRepayCB bean) {
        logger.info("微信还款回调接口,全部参数:" + JSON.toJSONString(bean));
        JSONObject jsonObj = new JSONObject();
        try {
            String string1 = "amount=" + bean.getAmount();
            string1 += "&bank_type=" + bean.getBank_type();
            string1 += "&card_tail=" + bean.getCard_tail();
            string1 += "&partner_id=" + bean.getPartner_id();
            string1 += "&pay_time=" + bean.getPay_time();
            string1 += "&wx_repay_no=" + bean.getWx_repay_no();

            String cSign = this.createSign(string1, this.SIGN_KEY);
            logger.info("计算sign值:" + cSign);
            if (!cSign.equals(bean.getSign().toUpperCase())) {
                logger.info("校验不通过.");
                jsonObj.put("code", "0");
                jsonObj.put("desc", "failed");
                return jsonObj;
            }
            logger.info("校验通过.");
            //校验通过,更新数据库
            RepaymentOrderDto rod = bankImportService.getRepaymentOrderByPartnerId(bean.getPartner_id());
            logger.info("查询订单成功,partnerId:" + bean.getPartner_id());
            Double tmp=Double.parseDouble(bean.getAmount());
            rod.setIwx_amount(tmp/100.0);
            rod.setCbank_type(bean.getBank_type());
            java.sql.Timestamp jstt = new java.sql.Timestamp(new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").parse(bean.getPay_time()).getTime());
            rod.setDpay_time(jstt);
            rod.setIstate(1);//支付成功
            rod.setCwx_repay_no(bean.getWx_repay_no());
            rod.setCwx_sign(bean.getSign().toUpperCase());

            bankImportService.updateRepayOrder(rod);
            logger.info("更新订单状态成功.");

            //添加消息提醒
            Integer resultSaveMsg=messageService.fillMessage(bean,1);
            if(resultSaveMsg>0){
                sendMessage(rod.getCuser_id());
            }
            jsonObj.put("code", "1");
            jsonObj.put("desc", "success");
            return jsonObj;
        } catch (Exception e) {
            logger.warn("异常:" + e.getMessage());
            e.printStackTrace();
            jsonObj.put("code", "0");
            jsonObj.put("desc", "failed");
            return jsonObj;
        }
    }

    /**
     * 查询订单信息
     *
     * @param partnerId
     * @return
     */
    @BoltController
    public JSONObject queryRepayOrder(@BoltParam("partner_id") String partnerId) {
        JSONObject jsonObj = new JSONObject();
        logger.info("还款，查询订单信息.");
        try {
            //查询账单数据
            RepaymentOrderDto rod = bankImportService.getRepaymentOrderByPartnerId(partnerId);
            if (rod != null) {
                logger.info("还款,查询账单成功：partnerId=" + partnerId);
                jsonObj.put("code", "1");
                jsonObj.put("desc", "查询成功");
                jsonObj.put("data", rod);
            } else {
                logger.info("还款,查询账单失败：partnerId=" + partnerId);
                jsonObj.put("code", "0");
                jsonObj.put("desc", "未查询到订单数据.");
                jsonObj.put("data", rod);
            }
        } catch (Exception e) {
            logger.warn("异常:" + e.getMessage());
            e.printStackTrace();
            jsonObj.put("code", "0");
            jsonObj.put("desc", "查询失败");
            jsonObj.put("data", "");
            return jsonObj;
        }
        return jsonObj;
    }

    /**
     * 产生新的PartnerId
     *
     * @return
     */
    private String getNewPartnerId() {
        Integer seqId = bankImportService.getSeqRepayOrderNextVal();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String partnerId = sdf.format(new Date());
        partnerId += this.CHANNEL_ID;
        partnerId += String.format("%010d", seqId);
        return partnerId;
    }

    /**
     * 生成签名
     * 在string1最后拼接上key=signKey，得到stringSignTemp字符串，并对stringSignTemp进行md5运算，
     * 再将得到的字符串所有字符转换为大写，得到sign值signValue。
     *
     * @return signValue
     */
    public static String createSign(String string1, String signKey) {
        String stringSignTemp = string1 + "&key=" + signKey;
        String signValue = "";
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] arrayMd5 = md5.digest(stringSignTemp.getBytes("GBK"));
            StringBuffer buff = new StringBuffer();
            for (byte b : arrayMd5) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    buff.append("0").append(Integer.toHexString(0xFF & b));
                } else {
                    buff.append(Integer.toHexString(0xFF & b));
                }
            }
            signValue = buff.toString();
            signValue = signValue.toUpperCase();
            return signValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void sendMessage(String userId){
        String messageTopic= LocalConfig.getString("kafka.message.send.task");
        JSONObject messageContend=new JSONObject();
        messageContend.put("mothed",MESSAGE);
        messageContend.put("type",1);
        messageContend.put("userId",userId);
        if (KafkaService.pushToTopic(messageTopic,messageContend.toJSONString())){
            logger.info("消息提醒发送成功，用户={}",userId);
        }else {
            logger.info("消息提醒发送失败，用户={}",userId);
        }
    }
}
