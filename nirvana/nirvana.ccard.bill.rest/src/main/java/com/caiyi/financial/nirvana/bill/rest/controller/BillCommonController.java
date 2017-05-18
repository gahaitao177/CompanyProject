package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSON;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.bill.util.BankHelper;
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
import com.hsk.cardUtil.CookieUtil;
import com.hsk.cardUtil.HpClientUtil;
import com.hsk.cardUtil.HttpRequester;
import com.hsk.cardUtil.HttpRespons;
import com.security.client.QuerySecurityInfoById;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario on 2016/7/26 0026.
 * 账单相关接口移植
 */
@RestController
@RequestMapping("/credit")
public class BillCommonController {
    private static String LOC = "/opt/export/image";
    private static Logger logger = LoggerFactory.getLogger(BillCommonController.class);
    public final static String MD5_KEY = "13da83f8-d230-46f9-a2b4-853b883bea38";
    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    @Autowired
    MemCachedClient cc;

    /**
     * 账单消费分析
     *
     * @param bean
     * @param response
     */
    @RequestMapping("/billConsumeInfo.go")
    public void billConsumeInfo(Channel bean, HttpServletResponse response) {
        try {
            String res = client.execute(new DrpcRequest("billCommon", "billConsumeInfo", bean));
            bean = JSON.parseObject(res, Channel.class);
        } catch (Exception e) {
            logger.error("billConsumeInfo 异常");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求");
            e.printStackTrace();
        } finally {
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
            sb.append(bean.getBusiXml());
            sb.append("</Resp>");
            XmlUtils.writeXml(sb.toString(), response);
        }
    }

    /**
     * 导入账单接口2
     *
     * @param bean
     * @param response
     */
    @RequestMapping("/getBankBill2.go")
    public void getBankBill2(Channel bean, HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 获取银行验证码2
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/bankVerifyCode2.go")
    public void bankVerifyCode2(Channel bean, HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 初始导入银行账单
     *
     * @param bean
     * @param response
     */
    @RequestMapping("/getBankBillState.go")
    public void getBankBillState(Channel bean, HttpServletResponse response) {
        try {
            //getBillStateInZheMemcached
            if (CheckUtil.isNullString(bean.getTaskid())) {
                bean.setBusiErrCode(999);
                bean.setBusiErrDesc("非法操作,缺少必要参数");
            } else {
                Object err_state_obj = cc.get(bean.getTaskid() + "_state");
                Object err_desc_obj = cc.get(bean.getTaskid() + "_desc");
                if (err_state_obj != null && err_desc_obj != null) {
                    Integer err_code = (Integer) err_state_obj;
                    String err_desc = String.valueOf(err_desc_obj);
                    if (!CheckUtil.isNullString(err_desc)) {
                        bean.setBusiErrCode(err_code);
                        bean.setBusiErrDesc(err_desc);
                    }
                }
            }
            if(bean.getBusiErrCode() == 0){
                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                return;
            }
            //showTaskState
            String res = client.execute(new DrpcRequest("billCommon", "showTaskState", bean));
            bean = JSON.parseObject(res,Channel.class);
        } catch (Exception e) {
            logger.error("getBillStateInZheMemcached异常", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
            sb.append(bean.getBusiXml());
            sb.append("</Resp>");
            XmlUtils.writeXml(sb.toString(), response);
        }
    }


    /*************************************************
     * 老方法
     *
     * @param bean
     * @return
     */
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
                    ssi.setSign(MD5Util.compute(ssi.getUid() + ssi.getCreditId() + MD5_KEY));
                    ssi.setServiceID("2000");
                    String s = ssi.call(30);
                    logger.info("s=" + s);
                    if (CheckUtil.isNullString(s)) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("无效的银行卡信息");
                        return 0;
                    }
                    Document xml = XmlTool.stringToXml(s);
                    Element ele = xml.getRootElement();
                    String errcode = ele.attributeValue("errcode");
                    if ("0".equalsIgnoreCase(errcode)) {
                        idcard = ele.elementText("accountName");
                        bankpwd = ele.elementText("accountPwd");
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
            Document jxml = XmlTool.stringToXml(context);
            Element ele = jxml.getRootElement();
            String recode = ele.element("returninfo").attributeValue("retcode");
            String message = ele.element("returninfo").attributeValue("message");
            if (!"0".equals(recode)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(message);
                logger.info(bean.getCuserId() + " zhong xin request error=" + message);
                return 0;
            }
            cc.set(bean.getCuserId() + "zhongXinCookie", cookieStore, 1000 * 60 * 50);
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

    private BufferedImage getZSVcode(Channel bean) throws IOException {
        BufferedImage localBufferedImage = null;
        try {
            HttpRequester httprequest = new HttpRequester();
            HttpRespons hr = httprequest.sendGet("https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx");
            //获取招商银行的登录sessionID
            String content = hr.getContent();
            String newcontent = content.substring(content.indexOf("https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx"), content.indexOf("function DoLogin()")).trim();
            newcontent = newcontent.split(",")[1];
            String sessionID = newcontent.substring(newcontent.indexOf("\"") + 1, newcontent.lastIndexOf("\""));
            //验证码地址
            String vcodeUrl = "https://html.m.cmbchina.com/MobileHtml/Login/ExtraPwd.aspx?ClientNo=" + sessionID;
            localBufferedImage = CookieUtil.getRandomImage("GET", vcodeUrl, null, null, false, "d:/opt");
            cc.set(bean.getCuserId() + bean.getBankId() + "bankSession", sessionID);
        } catch (Exception e) {
            logger.error(bean.getCuserId() + " getZSVcode ", e);
        }

        return localBufferedImage;
    }

    private BufferedImage getPAVcode(Channel bean) {
        BufferedImage localBufferedImage = null;
        try {
            String cookies = "WEBTRENDS_ID=116.226.73.166-744009648.30459312;" +
                    " USER_TRACKING_COOKIE=115.231.133.13-1437701391839.839000000;" +
                    " MEDIA_SOURCE_NAME=creditcard.pingan.com;" +
                    " BIGipServerMTOA-paue_webPrdPool=1526996140.43893.0000;" +
                    " BIGipServerelis-pa18-nginx_DMZ_PrdPool=3607370924.40565.0000;" +
                    " BIGipServerTOA-sdc_DMZ_443_PrdPool=1543773356.43893.0000;" +
                    " WT-FPC=id=116.226.73.166-744009648.30459312:lv=1437716480593:ss=1437716480593:fs=1437716480593:pv_Num=1:vt_Num=1;";
            Map<String, String> propertys = new HashMap<String, String>();
            propertys.put("Cookie", cookies);
            HttpRequester httprequest = new HttpRequester();
            HttpRespons hr = httprequest.sendGet("https://m.pingan.com/xinyongka/index.screen?menuType=accountInfo?_=" + System.currentTimeMillis(), null, propertys);
            String jsession = hr.getCookieParam("JSESSIONID");
            //下载验证码
            String vcodeUrl = "https://m.pingan.com/xinyongka/ImageGif.do?rd=" + System.currentTimeMillis();
            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            requestHeaderMap.put("Cookie", cookies + jsession);
            String path = LOC + "/pianan/" + bean.getCuserId();
            CookieUtil.getRandom("GET", vcodeUrl, null, requestHeaderMap, false, path);
            localBufferedImage = ImageIO.read(new File(path + "/code.bmp"));
            cc.set(bean.getCuserId() + bean.getBankId() + "bankSession", jsession);
        } catch (Exception e) {
            logger.error(bean.getCuserId() + " getPAVcode ", e);
        }
        return localBufferedImage;
    }

    private BufferedImage getZXVcode(Channel bean, HttpServletRequest request) throws IOException {

        BufferedImage localBufferedImage = null;
//		HttpRequester request = new HttpRequester();
//        HttpRespons hr = request.sendGet("https://creditcard.ecitic.com/citiccard/cppnew/entry.do?func=entryebank&ebankPage=mainpage",null,null,"gbk");
//		String content=hr.getContent();
//		String cookie=hr.getCookie();
//
//		String channel=content.substring(content.indexOf("var channel"), content.indexOf("var source"));
//		String source =content.substring(content.indexOf("var source"), content.indexOf("var from"));
//		channel=channel.substring(channel.indexOf("\"")+1, channel.lastIndexOf("\""));
//		source=source.substring(source.indexOf("\"")+1, source.lastIndexOf("\""));
//		//验证码地址
//		String vcodeUrl="https://creditcard.ecitic.com/citiccard/cppnew/jsp/valicode.jsp?time="+System.currentTimeMillis();
//		Map<String, String> requestHeaderMap=new HashMap<String, String>();
//		requestHeaderMap.put("Cookie", cookie);
//		localBufferedImage=CookieUtil.getRandomImageOfJPEG("GET",vcodeUrl, null, requestHeaderMap);
//		String sessionID=cookie+"@"+channel+"@"+source;
//		CacheClient cc = CacheClient.getInstance();
//		cc.set(bean.getCuserId()+bean.getBankId()+"bankSession", sessionID);

        CloseableHttpClient httpClient = null;
        try {
            String url = "https://creditcard.ecitic.com/citiccard/cppnew/entry.do?func=entryebank&ebankPage=mainpage";
            String userIp = BankHelper.getRealIp(request).trim();
            httpClient = HttpClients.createDefault();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig requestConfig = getRequestConfig();
            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            requestHeaderMap.put("Accept-Encoding", "gzip, deflate");
            requestHeaderMap.put("Host", "creditcard.ecitic.com");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("Cache-Control", "private");
            requestHeaderMap.put("Accept-Language", "zh-CN");
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            logger.info(bean.getCuserId() + "zhongxin userIp=" + userIp);
            String content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "GBK");

            String channel = content.substring(content.indexOf("var channel"), content.indexOf("var source"));
            String source = content.substring(content.indexOf("var source"), content.indexOf("var from"));
            channel = channel.substring(channel.indexOf("\"") + 1, channel.lastIndexOf("\""));
            source = source.substring(source.indexOf("\"") + 1, source.lastIndexOf("\""));
            String vcodeUrl = "https://creditcard.ecitic.com/citiccard/cppnew/jsp/valicode.jsp?time=" + System.currentTimeMillis();
            localBufferedImage = HpClientUtil.getRandomImageOfJPEG(vcodeUrl, requestHeaderMap, httpClient, localContext, requestConfig);

            String sessionID = channel + "@" + source + "@" + userIp;
            cc.set(bean.getCuserId() + bean.getBankId() + "bankSession", sessionID);
            cc.set(bean.getCuserId() + "zhongXinCookie", cookieStore, 1000 * 60 * 50);
        } catch (Exception e) {
            logger.error(bean.getCuserId() + " getZXVcode", e);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }


        return localBufferedImage;
    }
}
