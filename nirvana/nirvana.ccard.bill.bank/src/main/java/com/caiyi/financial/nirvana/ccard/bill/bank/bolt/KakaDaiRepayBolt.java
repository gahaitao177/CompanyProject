package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.BankImportService;
import com.caiyi.financial.nirvana.ccard.bill.bank.util.RSAUtil;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.RepaymentOrderDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.apache.commons.codec.binary.Base64;
import org.apache.storm.task.TopologyContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
/**
 * Created by dengh on 2016/7/7.
 */
@Bolt(boltId = "KakaDaiRepayBolt", parallelismHint = 1, numTasks = 1)
public class KakaDaiRepayBolt extends BaseBolt  {
    public BankImportService bankImportService;
    // 正式私钥
    public  String privateKey="MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAhbjxXnbBh9m/YlrHAQJZJ0UTWPk4SkBxFg0L4uwz5yl8SupiyQOOLnZKERwXW7nPmxOQgM3xdJB63ujWLRJQuQIDAQABAkAWvQQOFBOEjjpE9zqvqT6Ptuc7UeWIerzACyfiRlftDpixbVDIO9ar7CaIy8jJAcgLMyURssZpK/c5QSmkbv0hAiEA4aFZ7RG4MRv0jl0juFPs0gW2kxChShz6H/dDLVv1M0sCIQCXuK3/PJwQ/pgY8pWlrJbAo5/g13XLRYXOYUxHGm0FiwIgX2HvexnHjPMtclWLeSv5bFu/7/HSzVrsnkDQJEfAo3sCIQCAbgA7sl6ZtCmj1JUAbRwfbWKEvV4k93DQxmP/PEtVCQIhAJRcTH7M0f7PbINSjXlzHj9jUkrUGJ8yNcLSwNqMBExp";
    // 测试私钥
    public  String privateKeyBate ="MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAk100nlv/WlrTkKmNdBwURQWneGofoIQoV77TLOVhffSRDNDjsNm6fmgQJeM7RNcITwv+NLAh+jVv0XxWIvt2xwIDAQABAkEAh/470OiVfozTMW1HXR+MlSXipv1IsplDobY4q/YDQnhC92EnCSLhLvxVNMghfNp9ztfR6htiFf9397MnvGPEAQIhAOxenwgEdWAf8SfnuTY8Ff6UydRlFL9zxL4OKZx653G/AiEAn5pEJoiXX7YcaLZRv2RVyM7BWPB3CGCkpeF7OiHvLPkCIBAIFuc3TjK31+Zp/BDmoGNE+i9yr6aQlo6BbWcUmvAHAiEAkwCY4tEOc9adpgi/lMRKixF8XnnleS7il/Lt+CZHUKkCIQChqk9biDo5qAiqZDNh4DgAhQ/0XeFLe/MxgDPTnFWwzQ==";
    // 渠道号
    private String CHANNEL_ID = "huishuaka"; // 渠道号

    // add  by lcs 20170405 卡卡贷
    public static final String KAKADAI_URL = "https://third.kkcredit.cn";
    @Override
    protected void _prepare(Map map, TopologyContext topologyContext) {
        logger.info("---------------------KakaDaiRepayBolt _prepare");
        bankImportService = getBean(BankImportService.class);
    }
/***
 * @param  bean
 * @return  url String
 * ("billId") String billId,@BoltParam("sourceCode")String source ,@BoltParam("isBeta")Boolean isBeta )
 * */

    public  String getsignature(Channel bean,RepaymentOrderDto rod){
        String result = "";

        String source = bean.getSourceCode();
        Boolean isBeta = bean.getIsBeta();

        String urlBeta = "http://beta.kkcredit.cn/#/wcIndex/init?";
        String url = "http://www.kkcredit.cn/#/wcIndex/init?";

        String publicKey ="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAJNdNJ5b/1pa05CpjXQcFEUFp3hqH6CEKFe+0yzlYX30kQzQ47DZun5oECXjO0TXCE8L/jSwIfo1b9F8ViL7dscCAwEAAQ==";
        String uuid = rod.getCuser_id(); // 用户唯一uid

        String timestam = rod.getTime_stamp(); // 时间戳

        String  data = "";
        String signature ="";
        data = uuid +source + String.valueOf(timestam);
        logger.info(" data开始生成："+ data );
        try {

            signature ="";
            if(bean.getIsBeta() == true){
                signature = signBase64(data, privateKeyBate);

            }else{
                signature = signBase64(data, privateKey);
            }

            boolean flag =  verifyBase64(data,publicKey,signature );
            logger.info("检测---flag：" + flag);
            logger.info("数字签名生成："+ signature );

            signature = signature.replaceAll("\\+", "%2B");
            signature = signature.replaceAll("=", "%3D");

            logger.info("数字签名生成替代之后："+ signature );
        } catch (Exception e) {
            logger.info("生成数字签名失败");
            e.printStackTrace();
        }

        return signature;
    }
    /***
     * 创建卡卡贷
     * **/
    @BoltController
    public JSONObject CreateNewBill(Channel bean){

        bean.setSourceCode(SystemConfig.get("kakadai_repay_channelid"));
        String billId = bean.getBillId();
        String source = bean.getSourceCode();
        Boolean isBeta = bean.getIsBeta();
        JSONObject jsonObj = new JSONObject();
        logger.info("卡卡贷还款，创建新订单.");
        try {
            RepaymentOrderDto rod = new RepaymentOrderDto();
            //查询账单数据
            if(billId !=null && billId.length()>0){
                BankBillDto  bbd = bankImportService.getUserBankBillById(billId);
                logger.info("卡卡贷还款,查询账单成功：billid=" + billId);
                //获取参数,初始化新订单的dto
                rod.setCchannel_id(source);
                logger.info("渠道ID,channel_id = " + source);
                rod.setCcard_tail(bbd.getIcard4num()); // 后四位
                rod.setCcard_name(bbd.getUname());
                rod.setIamount(bbd.getIshouldrepayment());
                Date curDate = new Date();
                rod.setTime_stamp(curDate.getTime() + "");
                rod.setIbill_id(Integer.parseInt(billId));
                rod.setCuser_id(bbd.getCuserid());
                rod.setDapply_time(new java.sql.Timestamp(curDate.getTime()));
//                rod.setIpay_type(1);
                rod.setIpay_type(2);
                rod.setCpartner_id(this.getNewPartnerId());

            }else{
                rod.setCchannel_id(source);
                rod.setCcard_tail(bean.getCard4Num());
                String shouldRepayment = bean.getShouldRepayment();
                if(shouldRepayment !=null&& shouldRepayment.length()>0){
                    rod.setIamount(Double.valueOf(shouldRepayment));
                }

                Date curDate = new Date();
                rod.setTime_stamp(curDate.getTime() + "");
               // rod.setIbill_id(Integer.parseInt(billId));
                rod.setCuser_id(bean.getCuserId());
                rod.setDapply_time(new java.sql.Timestamp(curDate.getTime()));
                rod.setIpay_type(1);
                rod.setCpartner_id(this.getNewPartnerId());
            }
            String signature =getsignature(bean,rod );// 获取数字签名
            rod.setCsign(signature);
            String endUrl = "uuid="+rod.getCuser_id() +"&source="+source+"&timestamp="
                    +rod.getTime_stamp()+ "&signature="+signature + "&pageshow=true";
            String urlBeta = "http://beta.kkcredit.cn/#/wcIndex/init?";
            String url = "http://www.kkcredit.cn/#/wcIndex/init?";
            // add by lcs 20170405
            url = KAKADAI_URL + "/#/wcIndex/init?";
            //
            urlBeta = urlBeta + endUrl;
            url     = url + endUrl ;
            String params = "";
            if(isBeta ==null){
                params = url;
            }else{
                if(isBeta ){
                    params = urlBeta;
                }else{
                    params = url;
                }
            }
            bankImportService.createRepaymentOrder(rod);
            logger.info("订单创建成功. partner_id = " + rod.getCpartner_id());
            jsonObj.put("code", 1);
            jsonObj.put("desc", "订单创建成功");
            jsonObj.put("data", params);
            return jsonObj;
        } catch (Exception e) {
            logger.warn("异常:" + e.getMessage(), e);
            jsonObj.put("code", 0);
            jsonObj.put("desc", "新订单创建失败");
            jsonObj.put("data", "");
            return jsonObj;
        }

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

    public static String signBase64(String  data,String privateKey)throws Exception{
        byte[]sb5 = RSAUtil.sign(data.getBytes("utf-8"), Base64.decodeBase64(privateKey));

        return Base64.encodeBase64String(sb5);
    }


    /**解密
     * data为uuid+source+timestamp参数组合字符串
     * @param data 数据
     * @param publicKey  公钥
     * @param sign 签名字符串
     */
    public static boolean verifyBase64(String data,String publicKey,String sign)throws Exception{

        return  true;
    }







}
