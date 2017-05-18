package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.bill.bank.GuangDaBank;
import com.caiyi.financial.nirvana.bill.bank.PingAnBank;
import com.caiyi.financial.nirvana.bill.bank.ZhaoShangBank;
import com.caiyi.financial.nirvana.bill.bank.ZhongXinBank;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.mock.BankDeployByFile;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HpClientUtil;
import com.security.client.QuerySecurityInfoById;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yaoruikang on 16/5/26.
 */
@RestController
public class BankController {
    private static Logger logger = LoggerFactory.getLogger(BankController.class);
    public static String LOC = "/opt/export/image";
    public final static String MD5_KEY = "13da83f8-d230-46f9-a2b4-853b883bea38";

    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    @Autowired
    MemCachedClient cc;

    {
        System.out.println("-------BankController chushihua");
    }


    @RequestMapping("/notcontrol/credit/getuserip.go")
    public void getuserip(Channel bean, HttpServletRequest request, HttpServletResponse response){

        String ip = request.getRemoteAddr();
        if (ip.indexOf("192.168") > -1 || ip.indexOf("127.0.0") > -1) {
            String xf = request.getHeader("X-Forwarded-For");
            if(xf != null){
                ip = xf.split(",")[0];
            }
        }
        System.out.println(ip);

        logger.info(bean.getCuserId() + " getuserip X-FORWARDED-FOR=["+ request.getHeader("X-FORWARDED-FOR")+"] getRemoteAddr ["+request.getRemoteAddr()+"] " +
                "Proxy-Client-IP["+request.getHeader("Proxy-Client-IP")+"]  WL-Proxy-Client-IP["+request.getHeader("WL-Proxy-Client-IP")+"] " +
                "HTTP_CLIENT_IP["+request.getHeader("HTTP_CLIENT_IP")+"]");


        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        if (session.isNew()) {
            session.setMaxInactiveInterval(300);
        }
        try {
            BufferedImage localBufferedImage = null;
            Object cuserId = request.getAttribute("cuserId");
            if (cuserId != null) {
                bean.setCuserId(String.valueOf(cuserId));
            }

            if ("21".equals(bean.getBankId())) {//招商银行
                localBufferedImage = ZhaoShangBank.getZSVcode(bean, logger);
            } else if ("3".equals(bean.getBankId())) {//光大银行
                //localBufferedImage = GuangDaBank.getRandomImage(bean, logger,cc);//getGDVcode(bean);
            } else if ("7".equals(bean.getBankId())) {//平安银行
                localBufferedImage = PingAnBank.getPAVcode(bean, logger);
//			}else if ("13".equals(bean.getBankId())) {//建设
//				localBufferedImage = getJianSheVcode(bean);
            } else if ("4".equals(bean.getBankId()) || "9".equals(bean.getBankId()) || "19".equals(bean.getBankId()) || "11".equals(bean.getBankId())) {//有插件的银行获取验证码
//                localBufferedImage = MultiBankLoginUtil.getMultiBankVcode(bean,context);
            }
            if (localBufferedImage != null) {
                ServletOutputStream localServletOutputStream = response.getOutputStream();
                ImageIO.write(localBufferedImage, "PNG", localServletOutputStream);
                localServletOutputStream.flush();
                localServletOutputStream.close();
                return;
            } else if (localBufferedImage == null && ("4".equals(bean.getBankId()) || "9".equals(bean.getBankId())
                    || "19".equals(bean.getBankId()) || "11".equals(bean.getBankId()))) {
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("无效银行ID");
            }
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        } catch (Exception e) {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取图片验证码异常");
            logger.error(bean.getCuserId() + " getBankVerifyCode 获取图片验证码异常", e);
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }



    }

    @RequestMapping("/control/credit/bankLogin.go")
    public void bankLogin(Channel channel, HttpServletResponse response, HttpServletRequest request){
        System.out.println("test success");

        //Event4jClient.eventTest(EventEnum.start, "bankLogin.go", "yrk", request.getRemoteAddr());

//        System.out.print("WebSocketServer.getOnlineCount()=" + WebSocketServer.getOnlineCount());


//        String result = client.execute(new DrpcRequest("bank", "setCreditId", channel));
//
//        channel=JSONObject.parseObject(result,Channel.class);
//        System.out.println("bankLogin resultresult="+result);
//        try {
//           response.setContentType("text/javascript;charset=UTF-8");
//           response.getWriter().print(result);
//       }catch (Exception e){
//            e.printStackTrace();
//       }

    }

    @RequestMapping("/control/credit/bankVerifyCode.go")
    public void getBankVerifyCode(Channel bean, HttpServletRequest request, HttpServletResponse response){
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        if (session.isNew()) {
            session.setMaxInactiveInterval(300);
        }
        try {
            BufferedImage localBufferedImage = null;
            Object cuserId = request.getAttribute("cuserId");
            if (cuserId != null) {
                bean.setCuserId(String.valueOf(cuserId));
            }

            if ("21".equals(bean.getBankId())) {//招商银行
                localBufferedImage = ZhaoShangBank.getZSVcode(bean, logger);
            } else if ("3".equals(bean.getBankId())) {//光大银行
                //localBufferedImage = GuangDaBank.getRandomImage(bean, logger, cc);//getGDVcode(bean);
            } else if ("7".equals(bean.getBankId())) {//平安银行
                localBufferedImage = PingAnBank.getPAVcode(bean, logger);
//			}else if ("13".equals(bean.getBankId())) {//建设
//				localBufferedImage = getJianSheVcode(bean);
            } else if ("4".equals(bean.getBankId()) || "9".equals(bean.getBankId()) || "19".equals(bean.getBankId()) || "11".equals(bean.getBankId())) {//有插件的银行获取验证码
//                localBufferedImage = MultiBankLoginUtil.getMultiBankVcode(bean,context);
            }
            if (localBufferedImage != null) {
                ServletOutputStream localServletOutputStream = response.getOutputStream();
                ImageIO.write(localBufferedImage, "PNG", localServletOutputStream);
                localServletOutputStream.flush();
                localServletOutputStream.close();
                return;
            } else if (localBufferedImage == null && ("4".equals(bean.getBankId()) || "9".equals(bean.getBankId())
                    || "19".equals(bean.getBankId()) || "11".equals(bean.getBankId()))) {
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("无效银行ID");
            }
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        } catch (Exception e) {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取图片验证码异常");
            logger.error(bean.getCuserId() + " getBankVerifyCode 获取图片验证码异常", e);
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }

    }

    @RequestMapping("/control/credit/getBankBill.go")
    public void set_bank_data(Channel bean, HttpServletRequest request, HttpServletResponse response)throws Exception{
        String result = client.execute(new DrpcRequest("bank", "setCreditId", bean));
        bean = JSONObject.parseObject(result, Channel.class);
        System.out.print(bean);
        if (bean.getBusiErrCode() == 3) {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
            return;
        }

        String ckey = bean.getCuserId() + bean.getBankId() + "bankSession";
        Object object = cc.get(ckey);
        String bankSessionId = "";
        if (object != null) {
            bankSessionId = (String) object;
        }
        int code = 0;
        if ("10".equals(bean.getBankId()) || "1".equals(bean.getBankId()) || "5".equals(bean.getBankId()) || "16".equals(bean.getBankId()) || "13".equals(bean.getBankId())) {
            bean.setBankSessionId(bankSessionId);
            code = 1;
//            return 1;
        } else if ("9".equals(bean.getBankId()) || "11".equals(bean.getBankId()) || "4".equals(bean.getBankId()) || "19".equals(bean.getBankId())) {
//            return MultiBankLoginUtil.verifyMultiBankLogin(bean,context);
        } else {
            if (!CheckUtil.isNullString(bankSessionId)) {
                bean.setBankSessionId(bankSessionId);
                if ("2".equals(bean.getBankId())) {
                    code = verifyMsgZX(bean);
                } else if ("3".equals(bean.getBankId())) {//光大银行需要短信验证码
                    GuangDaBank service = new GuangDaBank();
                    code = service.verifyMsgGD(bean, cc);
                } else {
//                return 1;
                    code = 1;
                }
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("验证码失效");
                logger.info(bean.getCuserId() + bean.getBankId() + "bankSession" + " 验证码失效");
//                return 0;
                code = 0;
            }
        }

        if (code == 0) {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
            return;
        }

        result = client.execute(new DrpcRequest("bank", "createBankBillTask", bean));
        bean = JSONObject.parseObject(result, Channel.class);
        if (bean.getCode().equals("500")) {
            logger.info(bean.getCuserId() + " desc=" + bean.getDesc());
            XmlUtils.writeXml(bean.getBusiErrCode(), "调用服务失败", response);
        } else {
            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }

    }

    public int verifyMsgZX(Channel bean) {
        String vcode = bean.getBankRand();
        String isclent = bean.getClient();
        String channel = "";
        String source = "";
        String idcard = "";
        String bankpwd = "";
        String userIp = "";
        String bankSessionId = bean.getBankSessionId();
        try {
            if (bean.getBusiErrCode() == 3) {
                bean.setBusiErrCode(0);
                return 0;
            }
            //String [] ps= StringUtil.splitter(bean.getBankSessionId(), "@");
            String[] ps = null;
            if (!StringUtils.isEmpty(bean.getBankSessionId())) {
                ps = bean.getBankSessionId().split("@");
            }
            if (ps == null || ps.length < 2) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请求失败!");
                return 0;
            }
            channel = ps[0];
            source = ps[1];
            userIp = ps[2];
            if (CheckUtil.isNullString(channel) || CheckUtil.isNullString(source)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请求失败!");
                return 0;
            }
            if ("0".equals(bean.getType())) {
                if (CheckUtil.isNullString(bean.getBankPwd()) || CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getIskeep()) || CheckUtil.isNullString(bean.getBankPwd())) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("非法操作,缺少必要参数");
                    return 0;
                }
                if ("1".equals(isclent)) {
                    bankpwd = CaiyiEncryptIOS.dencryptStr(bean.getBankPwd());
                    idcard = CaiyiEncryptIOS.dencryptStr(bean.getIdCardNo());
                } else {
                    bankpwd = CaiyiEncrypt.dencryptStr(bean.getBankPwd());
                    idcard = CaiyiEncrypt.dencryptStr(bean.getIdCardNo());
                }
            } else if ("1".equals(bean.getType())) {

                if ("0".equals(bean.getIskeep())) {
                    //已保存密码
                    QuerySecurityInfoById ssi = new QuerySecurityInfoById();
                    ssi.setUid(bean.getCuserId());
                    ssi.setCreditId(bean.getCreditId());
                    //ssi.setSign(MD5Util.compute(ssi.getUid() + ssi.getCreditId() + MD5_KEY));
                    ssi.setSign(MD5Util.compute(ssi.getUid() + ssi.getCreditId() + MD5_KEY));
                    ssi.setServiceID("2000");
                    String s = ssi.call(30);
                    logger.info("s=" + s);
                    if (CheckUtil.isNullString(s)) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("无效的银行卡信息");
                        return 0;
                    }
                   /* JXmlWapper xml=JXmlWapper.parse(s);
                    Element ele=xml.getXmlRoot();
                    String errcode=ele.getAttributeValue("errcode");*/
                    Document doc = XmlTool.stringToXml(s);
                    org.dom4j.Element ele = doc.getRootElement();
                    String errcode = ele.attributeValue("errcode");
                    if ("0".equalsIgnoreCase(errcode)) {
                       /* idcard=ele.getChildText("accountName");
                        bankpwd=ele.getChildText("accountPwd");*/
                        idcard = XmlTool.getElementValue("accountName", ele);
                        bankpwd = XmlTool.getElementValue("accountPwd", ele);
                    } else {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("无效的银行卡信息");
                        return 0;
                    }
                } else {
                    if (CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getBankPwd())) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("身份证或查询密码为空！");
                        return 0;
                    }
                    if ("1".equals(isclent)) {
                        idcard = CaiyiEncryptIOS.dencryptStr(bean.getIdCardNo());
                    } else {
                        idcard = CaiyiEncrypt.dencryptStr(bean.getIdCardNo());
                    }
                }
                if (!CheckUtil.isNullString(bean.getBankPwd())) {
                    if ("1".equals(isclent)) {
                        bankpwd = CaiyiEncryptIOS.dencryptStr(bean.getBankPwd());
                    } else {
                        bankpwd = CaiyiEncrypt.dencryptStr(bean.getBankPwd());
                    }
                }

            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("无效的操作类型");
                return 0;
            }

            Object object = cc.get(bean.getCuserId() + "zhongXinCookie");
            if (object == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                return 0;
            }
            CookieStore cookieStore = (CookieStore) object;
            CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();

            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> propertys = new HashMap<String, String>();
            propertys.put("Accept-Encoding", "gzip, deflate");
            propertys.put("Host", "creditcard.ecitic.com");
            propertys.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
            propertys.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            propertys.put("Connection", "Keep-Alive");
            propertys.put("Cache-Control", "private");
            propertys.put("Accept-Language", "zh-CN");
            propertys.put("Content-Type", "text/xml; charset=GBK");
            if (!CheckUtil.isNullString(userIp)) {
                propertys.put("X-Forwarded-For", userIp);
            }

            String loginXml = "<request><logonEbank phonesecretcode=\"" + bankpwd + "\"  logintype=\"02\" idtype=\"1\" idnumber=\"" + idcard + "\" "
                    + "valicode=\"" + vcode + "\" from=\"INNER_EBANK\" channel=\"" + channel + "\" source=\"" + source + "\" /></request>";
            logger.info(bean.getCuserId() + " zhong xin loginXml=" + loginXml);
            String url = "https://creditcard.ecitic.com/citiccard/cppnew/entry.do?func=entryEbankFun&date=" + System.currentTimeMillis();
            String context = HpClientUtil.httpPost(url, propertys, loginXml, httpClient, localContext, "GBK", requestConfig);
            if (CheckUtil.isNullString(context)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请求失败!");
                return 0;
            }
            logger.info(bean.getCuserId() + " zhong xin logonEbank request=" + context);
        /*    JXmlWapper jxml=JXmlWapper.parse(context);
            String recode=ele.getChild("returninfo").getAttributeValue("retcode");
            String message=ele.getChild("returninfo").getAttributeValue("message");*/
            Document doc = XmlTool.stringToXml(context);
            org.dom4j.Element ele = doc.getRootElement();
            String recode = ele.element("returninfo").attributeValue("retcode");
            String message = ele.element("returninfo").attributeValue("message");
            if (!"0".equals(recode)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(message);
                logger.info(bean.getCuserId() + " zhong xin request error=" + message);
                return 0;
            }
            cc.set(bean.getCuserId() + "zhongXinCookie", cookieStore, 1000 * 60 * 50);
            //String sendSmsFlag=ele.getChild("re_userinfo").getAttributeValue("sendSmsFlag");
            String sendSmsFlag = ele.element("re_userinfo").attributeValue("sendSmsFlag");
            if ("01".equals(sendSmsFlag)) {//需要短信验证
                bean.setBusiErrCode(2);
                bean.setBusiErrDesc("需要短信验证");
                bean.setBusiXml("<phoneCode>true</phoneCode>");

                return 0;
            } else if ("00".equals(sendSmsFlag)) {
                StringBuffer sb = new StringBuffer();
                cookieStore = (CookieStore) localContext.getAttribute("http.cookie-store");
                for (int i = 0; i < cookieStore.getCookies().size(); i++) {
                    Cookie cookie = cookieStore.getCookies().get(i);
                    String name = cookie.getName();
                    String value = cookie.getValue();
                    if (i != 0) {
                        sb.append("|");
                    }
                    sb.append(name);
                    sb.append("#");
                    sb.append(value);
                    logger.info(bean.getCuserId() + " zhong xin cookie name=" + name + " value=" + value);

                }
                bankSessionId = sb.toString();
                bean.setBankSessionId(bankSessionId + "@" + userIp);
                logger.info(bean.getCuserId() + " zhong xin bankSessionId=" + bankSessionId);
                return 1;
            }
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc(message);
            return 0;
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败!");
            logger.error(bean.getCuserId() + " zhong xin verifyMsgZX异常", e);

        }
        return 0;
    }

    private RequestConfig getRequestConfig() {
        RequestConfig.custom().setConnectTimeout(20000);
        RequestConfig.custom().setSocketTimeout(20000);
        RequestConfig.custom().setConnectionRequestTimeout(20000);
        RequestConfig requestConfig = RequestConfig.custom().build();
        return requestConfig;
    }

    @RequestMapping("/notcontrol/credit/readBankConfig.go")
    public String readBankConfigList(Channel bean, HttpServletResponse response) {
        BankDeployByFile deployImpl = new BankDeployByFile();
        String rest = "";
        try{
            rest = deployImpl.readBankConfig(bean.getBankId());
        }catch (Exception e){
            logger.info("readBankConfig.exception",e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("读取银行帐号配置出错");
        }
        return rest;
    }

    @RequestMapping("/notcontrol/credit/refreshDeployResult.go")
    public String refreshDeployResult(Channel bean, HttpServletResponse response) {
        String rest = "";
        try{
            rest = BankDeployByFile.refreshDeployResult(bean.getBankId());
        }catch (Exception e){
            logger.info("refreshDeployResult.exception",e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("刷新银行登录配置出错");
        }
        return rest;
    }
}
