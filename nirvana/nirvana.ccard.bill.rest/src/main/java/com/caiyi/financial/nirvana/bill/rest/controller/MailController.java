package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSON;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.bill.util.BankHelper;
import com.caiyi.financial.nirvana.bill.util.mail.NtesMail126;
import com.caiyi.financial.nirvana.bill.util.mail.NtesMail163;
import com.caiyi.financial.nirvana.bill.util.mail.SinaMail;
import com.caiyi.financial.nirvana.bill.util.mail.TencentMail;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.XmlTool;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import com.security.client.QuerySecurityInfoById;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

/**
 * Created by Mario on 2016/7/28 0028.
 */
@RestController
@RequestMapping("/credit")
public class MailController {
    public final static String MD5_KEY = "13da83f8-d230-46f9-a2b4-853b883bea38";
    private static Logger logger = LoggerFactory.getLogger(MailController.class);
    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    @Autowired
    MemCachedClient cc;

    /**
     * 查询账单任务状态
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/getEmailBill.go")
    public void getEmailBill(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            bean.setIpAddr(BankHelper.getRealIp(request).trim());
            //setMailCreditId
            String res = client.execute(new DrpcRequest("billMail", "setMailCreditId", bean));
            bean = JSON.parseObject(res, Channel.class);
            if (bean.getBusiErrCode() == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            //mailLogin
            int ecode = checkEmailAccount(bean);
            if (ecode == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            if ("0".equals(bean.getMailType())) {
                TencentMail.login(bean, logger);
            } else if ("2".equals(bean.getMailType())) {
                NtesMail163.login(bean, logger);
            } else if ("3".equals(bean.getMailType())) {
                NtesMail126.mailLogin(bean, logger);
            } else if ("4".equals(bean.getMailType())) {
                SinaMail.mailLogin(bean, logger);
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("暂不支持的邮箱登录类型");
            }
            if (bean.getBusiErrCode() == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            //createEmailBillTask
            res = client.execute(new DrpcRequest("billMail", "createEmailBillTask", bean));
            bean = JSON.parseObject(res, Channel.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getEmailBill 异常 " + e.getMessage());
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 获取银行验证码
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/getEmailCode.go")
    public void getEmailCode(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            bean.setIpAddr(BankHelper.getRealIp(request).trim());
            //setMailCreditId
            String res = client.execute(new DrpcRequest("billMail", "setMailCreditId", bean));
            bean = JSON.parseObject(res, Channel.class);
            if (bean.getBusiErrCode() == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            //getEmailVerifyCode
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            HttpSession session = request.getSession();
            if (session.isNew()) {
                session.setMaxInactiveInterval(300);
            }
            try {
                ServletOutputStream localServletOutputStream = response.getOutputStream();
                BufferedImage localBufferedImage = null;

                int ecode = checkEmailAccount(bean);
                if (ecode == 0) {
                    XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                    return;
                }
                if ("0".equals(bean.getMailType())) {//QQ邮箱
                    String imgBase64Str = TencentMail.getVerifyCode(bean, logger);
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] buffer = decoder.decodeBuffer(imgBase64Str);
                    localBufferedImage = ImageIO.read(new ByteArrayInputStream(buffer));
                } else if ("2".equals(bean.getMailType())) {
                    String imgBase64Str = NtesMail163.getVerifycode(bean, logger);
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] buffer = decoder.decodeBuffer(imgBase64Str);
                    localBufferedImage = ImageIO.read(new ByteArrayInputStream(buffer));
                } else if ("3".equals(bean.getMailType())) {
                    String imgBase64Str = NtesMail126.getVerifycode(bean, logger);//126邮箱
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] buffer = decoder.decodeBuffer(imgBase64Str);
                    localBufferedImage = ImageIO.read(new ByteArrayInputStream(buffer));
                } else {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("无效邮箱请求");
                }
                if (localBufferedImage != null) {
                    ImageIO.write(localBufferedImage, "PNG", localServletOutputStream);
                    localServletOutputStream.flush();
                    localServletOutputStream.close();
                } else if (localBufferedImage == null) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("获取验证码失败,请刷新重试");
                }
            } catch (Exception e) {
                logger.error(bean.getCuserId() + " getEmailVerifyCode异常", e);
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error(bean.getCuserId() + " getEmailCode异常", e);
            e.printStackTrace();
        }
    }

    /**
     * 验证短信验证码并生成任务
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/checkEmailCode.go")
    public void checkEmailCode(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            bean.setIpAddr(BankHelper.getRealIp(request).trim());
            //setMailCreditId
            String res = client.execute(new DrpcRequest("billMail", "setMailCreditId", bean));
            bean = JSON.parseObject(res, Channel.class);
            if (bean.getBusiErrCode() == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            //checkEmailCode
            logger.info("进入图片验证码检测接口>>>>>>>>>>>>>>>>>>>>>>" + bean.getMailType());
            int ecode = checkEmailAccount(bean);
            if (ecode == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            if ("0".equals(bean.getMailType())) {//支持 QQ邮箱
                TencentMail.checkEmailCode(bean, logger);
            } else if ("2".equals(bean.getMailType())) {
                NtesMail163.checkEmailCode(bean, logger);
            } else if ("3".equals(bean.getMailType())) {//126邮箱
                NtesMail126.checkEmailCode(bean, logger);
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("无效邮箱请求");
            }
            if (bean.getBusiErrCode() == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            //createEmailBillTask
            res = client.execute(new DrpcRequest("billMail", "createEmailBillTask", bean));
            bean = JSON.parseObject(res, Channel.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getEmailBill 异常 " + e.getMessage());
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }


    /**
     * 邮箱登录
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/mailLogin.go")
    public void mailLogin(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            int ecode = checkEmailAccount(bean);
            if (ecode == 0) {
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            if ("0".equals(bean.getMailType())) {
                TencentMail.login(bean, logger);
            } else if ("2".equals(bean.getMailType())) {
                NtesMail163.login(bean, logger);
            } else if ("3".equals(bean.getMailType())) {
                NtesMail126.mailLogin(bean, logger);
            } else if ("4".equals(bean.getMailType())) {
                SinaMail.mailLogin(bean, logger);
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("暂不支持的邮箱登录类型");
            }
        } catch (Exception e) {
            logger.error((bean.getCuserId() + " mailLogin error"), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("邮箱登录失败");
        } finally {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 邮箱读取账单
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/updateBillInfoByMail.go")
    public void updateBillInfoByMail(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            String res = client.execute(new DrpcRequest("billMail", "updateBillInfoByMail", bean));
            bean = JSON.parseObject(res, Channel.class);
        } catch (Exception e) {
            logger.error("updateBillInfoByMail 异常" + e.getMessage());
            e.printStackTrace();
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 邮箱读取账单
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/billInfoByMail.go")
    public void billInfoByMail(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            String res = client.execute(new DrpcRequest("billMail", "billInfoByMail", bean));
            bean = JSON.parseObject(res, Channel.class);
        } catch (Exception e) {
            logger.error("billInfoByMail 异常" + e.getMessage());
            e.printStackTrace();
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /***********************************************************
     * 重构checkEmailAccount by lcs 20160510
     *
     * @param bean
     * @return
     * @throws Exception
     */
    private int checkEmailAccount(Channel bean) throws Exception {
        String isclent = bean.getClient();
        String mailAddress = "";
        String mailPwd = "";
        String indePwd = "";
        // 获取mailType add by lcs
        logger.info("getOutsideId-" + bean.getOutsideId());
        logger.info("getType-" + bean.getType() + bean.getIskeep());
        if (!StringUtils.isEmpty(bean.getOutsideId())) {
            String mailtype = CaiyiEncrypt.dencryptStr(bean.getOutsideId());
            logger.info("mailtype-" + mailtype);
            if (mailtype.contains("-")) {
                bean.setMailType(mailtype.split("-")[1]);
            }
        }

        HashMap<String, String> supportMail = new HashMap<String, String>();
        supportMail.put("0", "true");// qq邮箱
        supportMail.put("2", "true");// 163邮箱
        supportMail.put("3", "true");// 126邮箱

        // 是否支持
        if (!supportMail.containsKey(bean.getMailType())) {
            logger.info(bean.getCuserId() + " 该邮箱暂不支持 mailtype[" + bean.getMailType() + "]");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("该邮箱暂不支持");
            return 0;
        }
        //  导入  或 未保存密码更新
        if ("4".equals(bean.getType()) || ("5".equals(bean.getMailType()) && !"0".equals(bean.getIskeep()))) {
            if (CheckUtil.isNullString(bean.getMailAddress()) || CheckUtil.isNullString(bean.getMailPwd())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("1邮箱账号和密码不能为空！");
                logger.info(bean.getCuserId() + " 邮箱账号或者密码为空 address[" + bean.getMailAddress() + "] mailpwd[" + bean.getMailPwd() + "]");
                return 0;
            }
            if ("1".equals(isclent)) {
                mailAddress = CaiyiEncryptIOS.dencryptStr(bean.getMailAddress());
                mailPwd = CaiyiEncryptIOS.dencryptStr(bean.getMailPwd());
                if ("0".equals(bean.getMailType())) {
                    indePwd = CaiyiEncryptIOS.dencryptStr(bean.getConfirmpassword());
                }
            } else {
                mailAddress = CaiyiEncrypt.dencryptStr(bean.getMailAddress());
                mailPwd = CaiyiEncrypt.dencryptStr(bean.getMailPwd());
                if ("0".equals(bean.getMailType())) {
                    indePwd = CaiyiEncrypt.dencryptStr(bean.getConfirmpassword());
                }
            }
        } else if ("5".equals(bean.getType())) {  //更新
            if ("0".equals(bean.getIskeep())) {
                //已保存密码
                QuerySecurityInfoById ssi = new QuerySecurityInfoById();
                ssi.setUid(bean.getCuserId());
                ssi.setCreditId(bean.getCreditId());
                ssi.setSign(MD5Util.compute(ssi.getUid() + ssi.getCreditId() + MD5_KEY));
                ssi.setServiceID("2000");
                String s = ssi.call(30);
                logger.info(bean.getCuserId() + " 查询邮箱账号返回 s=" + s);
                if (CheckUtil.isNullString(s)) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("无效的邮箱账号信息");
                    return 0;
                }
                Document xml = XmlTool.stringToXml(s);
                Element ele = xml.getRootElement();
                String errcode = ele.attributeValue("errcode");
                if ("0".equalsIgnoreCase(errcode)) {
                    mailAddress = ele.elementText("accountName");
                    mailPwd = ele.elementText("accountPwd");
                    if ("0".equals(bean.getMailType())) {
                        indePwd = ele.elementText("specialPwd");
                    }
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("无效的邮箱账号信息");
                    return 0;
                }
            }
        }
        bean.setLoginname(mailAddress);
        bean.setPassword(mailPwd);
        if ("1".equals(isclent)) {
            bean.setMailAddress(CaiyiEncryptIOS.encryptStr(mailAddress));
            bean.setMailPwd(CaiyiEncryptIOS.encryptStr(mailPwd));
            bean.setConfirmpassword(CaiyiEncryptIOS.encryptStr(indePwd));
        } else {
            bean.setMailAddress(CaiyiEncrypt.encryptStr(mailAddress));
            bean.setMailPwd(CaiyiEncrypt.encryptStr(mailPwd));
            bean.setConfirmpassword(CaiyiEncrypt.encryptStr(indePwd));
        }
        return 1;
    }
}
