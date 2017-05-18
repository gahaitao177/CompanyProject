package com.caiyi.financial.nirvana.bill.util;

import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.bill.util.mail.NtesMail126;
import com.caiyi.financial.nirvana.bill.util.mail.NtesMail163;
import com.caiyi.financial.nirvana.bill.util.mail.TencentMail;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.rbc.http.client.Cert;
import com.rbc.http.client.CertHelper;
import com.security.client.QuerySecurityInfoById;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lichuanshun on 16/7/12.
 */
public class MailBillHelper {
    public static Logger logger = LoggerFactory.getLogger(MailBillHelper.class);
    public MailBillHelper(){
        System.out.println("MailBillHelper---------start");
        try {
            String certId = SystemConfig.get("security.certId");
            String certFile = SystemConfig.get("security.certFile");
            String preUrl = SystemConfig.get("security.preUrl");
            String agentCode = SystemConfig.get("security.agentCode");
            String encodeIng = SystemConfig.get("security.encodeIng");
            CertHelper ch = CertHelper.getHttpCert();
            Cert cert = new Cert(certId, agentCode, preUrl, certFile, encodeIng);
            ch.putCert(cert);
            logger.info("MailBillHelper 加载证书成功" );
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MailBillHelper 加载证书失败" + e);
        }
    }

    /**
     * 邮箱模拟登录接口
     * @param bean
     * @return
     */
    public static int mainLogin(Channel bean){
        logger.info("MailBillHelper mainLogin start");
        try {
            String ecode = checkEmailAccount(bean);
            if (!"1".equals(ecode)){
                return 0;
            }
            //
            String mailType = bean.getMailType();
            if (BillConstant.MAIL_QQ.equals(mailType)){
                if (CheckUtil.isNullString(bean.getCode())){
                    return TencentMail.login(bean,logger);
                } else {
                    return TencentMail.checkEmailCode(bean,logger);
                }
            } else if (BillConstant.MAIL_163.equals(mailType)){
                if (CheckUtil.isNullString(bean.getCode())){
                    return NtesMail163.login(bean,logger);
                }else {
                    return NtesMail163.checkEmailCode(bean,logger);
                }
            }else if (BillConstant.MAIL_126.equals(mailType)){
                if (CheckUtil.isNullString(bean.getCode())){
                    //return NtesMail126.mailLogin(bean,logger);
                    return NtesMail126.mailLogin(bean, logger);
                }else {
                    return NtesMail126.checkEmailCode(bean,logger);
                }
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("暂不支持的邮箱类型");
            }
        }catch (Exception e){
            logger.info("MailBillHelper mainLogin:" ,e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("邮箱登录失败");
        }
        return 0;
    }

    /**
     * 获取图片验证码
     * @param channel
     * @return
     */
    public static String getVerifyCode(Channel channel){
        String base64Str = "";
        try {
            String ecode = checkEmailAccount(channel);
            if (!"1".equals(ecode)){
                return null;
            }
            String mailType = channel.getMailType();
            if (BillConstant.MAIL_QQ.equals(mailType)){
                base64Str =  TencentMail.getVerifyCode(channel,logger);
            } else if (BillConstant.MAIL_163.equals(mailType)){
                base64Str =  NtesMail163.getVerifycode(channel,logger);
            }else if (BillConstant.MAIL_126.equals(mailType)){
                base64Str =  NtesMail126.getVerifycode(channel,logger);
            }else {
                channel.setBusiErrCode(0);
                channel.setBusiErrDesc("暂不支持的邮箱类型");
            }
        }catch (Exception e){
            channel.setBusiErrDesc("获取图片验证码异常");
            channel.setBusiErrCode(0);
        }
        return base64Str;
    }

    /**
     * 邮箱验证验证码
     */
    public static int checkEmailCode(Channel bean) throws Exception {
        logger.info("进入图片验证码检测接口>>>>>>>>>>>>>>>>>>>>>>"+bean.getMailType());
        String ecode=checkEmailAccount(bean);
        if (!"1".equals(ecode)) {
            return 0;
        }
        String mailType = bean.getMailType();
        if (BillConstant.MAIL_QQ.equals(mailType)){
            return TencentMail.checkEmailCode(bean,logger);
        } else if (BillConstant.MAIL_163.equals(mailType)){
            return NtesMail163.checkEmailCode(bean,logger);
        }else if (BillConstant.MAIL_126.equals(mailType)){
            return NtesMail126.checkEmailCode(bean,logger);
        }else {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("暂不支持的邮箱类型");
        }
        return 1;
    }


    /**
      验证邮箱合法
     */
    private static String checkEmailAccount(Channel bean){
        String result = "1";
        // 更新时 设置邮箱地址和邮箱类型
        setMailAddressAndType(bean);
        String maiType = bean.getMailType();
        String type = bean.getType();
        // 邮箱类型是否支持
        if (!BillConstant.supportMail.containsKey(maiType)){
            bean.setBusiErrDesc("该邮箱暂不支持");
            bean.setBusiErrCode(0);
            return "0";
        }
        //


        // 导入或者 更新未保存密码
        if (BillConstant.MAIL_BILL_IMPORT.equals(type) || (BillConstant.MAIL_BILL_UPDATE.equals(type) && !"0".equals(bean.getIskeep()))){
            setParamsWhenImport(bean);
            if (1 != bean.getBusiErrCode()){
                return "0";
            }
        } else if (BillConstant.MAIL_BILL_UPDATE.equals(type)){
            //更新账单
//            if ("0".equals(bean.getIskeep())) {
                setParamsWhenUpdate(bean);
                if (1 != bean.getBusiErrCode()){
                    return "0";
//                }
            }
        } else {
            logger.info("非法参数:" + type);
            bean.setBusiErrDesc("非法参数");
            bean.setBusiErrCode(0);
            return "0";
        }
        String mailAddress = bean.getMailAddress();
        String mailPwd = bean.getMailPwd();
        //  参数合法
        if (CheckUtil.isNullString(mailAddress) ||  CheckUtil.isNullString(mailPwd)){
            bean.setBusiErrDesc("邮箱账号密码不能为空");
            bean.setBusiErrCode(0);
            return "0";
        }
        return result;
    }

    /**
     *
     * @param bean
     */
    private static void setParamsWhenImport(Channel bean){
        //
        String mailAddress = bean.getMailAddress();
        String mailPwd = bean.getMailPwd();
        String maiType = bean.getMailType();
        String client = bean.getClient();

        //独立密码
        String indePwd = bean.getConfirmpassword();
        try {
            if (BillConstant.CLIENT_IOS.equals(client)){
                mailAddress = CaiyiEncryptIOS.dencryptStr(mailAddress);
                mailPwd = CaiyiEncryptIOS.dencryptStr(mailPwd);
                if (BillConstant.MAIL_QQ.equals(maiType)){
                    indePwd = CaiyiEncryptIOS.dencryptStr(indePwd);
                }
            } else {
                mailAddress = CaiyiEncrypt.dencryptStr(mailAddress);
                mailPwd = CaiyiEncrypt.dencryptStr(mailPwd);
                if (BillConstant.MAIL_QQ.equals(maiType)){
                    indePwd = CaiyiEncrypt.dencryptStr(indePwd);
                }

            }
            bean.setLoginname(mailAddress);
            bean.setPassword(mailPwd);
            resetAccountInfo(bean,mailAddress,mailPwd,indePwd);
        }catch (Exception e){
            e.printStackTrace();
            bean.setBusiErrDesc("查询异常");
            bean.setBusiErrCode(0);
        }

    }
    /**
     * 邮箱账单更新
     * @param bean
     */
    private static void setParamsWhenUpdate(Channel bean){
        try {
            logger.info("uid:" + bean.getCuserId() +  "creditid:" + bean.getCreditId());
            QuerySecurityInfoById ssi = new QuerySecurityInfoById();
            ssi.setUid(bean.getCuserId());
            ssi.setCreditId(bean.getCreditId());
            ssi.setSign(MD5Util.compute(bean.getCuserId() + bean.getCreditId() +  BillConstant.MD5_KEY));
            ssi.setServiceID("2000");
            String info = ssi.call(30);
//            logger.info("getSecurityInfo:" + info);
            if (CheckUtil.isNullString(info)){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("无效的邮箱账号信息");
                return;
            }
            /*JXmlWapper xmlWapper = JXmlWapper.parse(info);
            Element ele = xmlWapper.getXmlRoot();
            String resultCode = ele.getAttributeValue("errcode");*/

            Document doc = XmlTool.stringToXml(info);
            org.dom4j.Element ele = doc.getRootElement();
            String resultCode = ele.attributeValue("errcode");
            String mailAddress = bean.getMailAddress();
            String mailPwd = bean.getMailPwd();
            String maiType = bean.getMailType();
            //独立密码
            String indePwd = bean.getConfirmpassword();
            if ("0".equals(resultCode)){
                /*mailAddress = ele.getChildText("accountName");
                mailPwd = ele.getChildText("accountPwd");
                if (BillConstant.MAIL_QQ.equals(maiType)){
                    indePwd = ele.getChildText("specialPwd");
                }*/
                mailAddress = XmlTool.getElementValue("accountName", ele);
                mailPwd = XmlTool.getElementValue("accountPwd", ele);
                if (BillConstant.MAIL_QQ.equals(maiType)){
                    indePwd = XmlTool.getElementValue("specialPwd", ele);
                }
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("无效的邮箱账号信息");
                return ;
            }
            bean.setLoginname(mailAddress);
            bean.setPassword(mailPwd);
            resetAccountInfo(bean,mailAddress,mailPwd,indePwd);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("setParamsWhenUpdate error:" , e);
            bean.setBusiErrDesc("查询异常");
            bean.setBusiErrCode(0);
        }
    }

    /**
     *  保存加密后的账号信息
     */
    private static void resetAccountInfo(Channel bean, String mailAddress,String mailPwd,String indePwd){
        try {
            if ("1".equals(bean.getClient())){
                bean.setMailAddress(CaiyiEncryptIOS.encryptStr(mailAddress));
                bean.setMailPwd(CaiyiEncryptIOS.encryptStr(mailPwd));
                bean.setConfirmpassword(CaiyiEncryptIOS.encryptStr(indePwd));
            } else {
                bean.setMailAddress(CaiyiEncrypt.encryptStr(mailAddress));
                bean.setMailPwd(CaiyiEncrypt.encryptStr(mailPwd));
                bean.setConfirmpassword(CaiyiEncrypt.encryptStr(indePwd));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("resetAccountInfo",e);
        }

    }
    /**
     解析outSideId 设置邮箱地址和邮箱类型
     */
    private static void setMailAddressAndType(Channel bean){
        try {
            if (!CheckUtil.isNullString(bean.getOutsideId())){
                String mailAndType = CaiyiEncrypt.dencryptStr(bean.getOutsideId());
                if (!CheckUtil.isNullString(mailAndType) && mailAndType.contains("-")){
                    bean.setMailAddress(mailAndType.split("-")[0]);
                    bean.setMailType(mailAndType.split("-")[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
