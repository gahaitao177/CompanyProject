package com.caiyi.financial.nirvana.investigation.base;

import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.util.InvestigationHelper;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by terry on 2016/10/25.
 */
public class BaseResetPwdController extends BaseController{
    /**
     * 征信忘记密码认证用户信息
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    public void investGoToResetPwd(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            if (CheckUtil.isNullString(bean.getLoginname())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("登录名不能为空");
                return;
            }
                if (CheckUtil.isNullString(bean.getIdCardNo()) || CheckUtil.isNullString(bean.getUsername())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("身份id和真实姓名不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("验证码不能为空");
                return;
            }


            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinResetPwdSession");
            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinResetPwdCookie");
            if (object == null || object2 == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("验证码失效,请重新申请");
                return;
            }
            String sessionID = (String) object;
            String[] paras = sessionID.split("@");
            String TOKEN = paras[0];
            String method = paras[1];

            String loginname = "";
            String username = "";
            String idcard = "";

            if ("1".equals(bean.getClient())) {
                loginname = CaiyiEncryptIOS.dencryptStr(bean.getLoginname());
                username = CaiyiEncryptIOS.dencryptStr(bean.getUsername());
                idcard = CaiyiEncryptIOS.dencryptStr(bean.getIdCardNo());
            } else {
                loginname = CaiyiEncrypt.dencryptStr(bean.getLoginname());
                username = CaiyiEncrypt.dencryptStr(bean.getUsername());
                idcard = CaiyiEncrypt.dencryptStr(bean.getIdCardNo());
            }
            if (CheckUtil.isNullString(loginname) || CheckUtil.isNullString(username) || CheckUtil.isNullString(idcard)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("参数加密不正确！");
                return;
            }


            CookieStore cookieStore = (CookieStore) object2;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            Map<String, String> parames = new LinkedHashMap<String, String>();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("method", method);
            parames.put("certNo", idcard);
            parames.put("certType", "0");
            parames.put("name", username);
            parames.put("loginname", loginname);
            parames.put("_@IMGRC@_", bean.getCode());
            url = "https://ipcrs.pbccrc.org.cn/resetPassword.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

            if (isExpired(errcontent,bean,response)) {
                return;
            }
            Document errorDoc = Jsoup.parse(errcontent);
            org.jsoup.nodes.Element errorhtml = errorDoc.getElementById("_error_field_");
            org.jsoup.nodes.Element msghtml = errorDoc.getElementById("_@MSG@_");

            String msg = "";
            if (errorhtml != null) {
                msg = errorhtml.text();
                if (!CheckUtil.isNullString(msg)) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc(msg);
                    logger.info(bean.getCuserId() + " 重置密码  zhenXinGoToResetPwd " + msg);
                    return;
                }
            }
            if (msghtml != null) {
                msg = msghtml.text();
                if (!CheckUtil.isNullString(msg)) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc(msg);
                    logger.info(bean.getCuserId() + " 重置密码  zhenXinGoToResetPwd " + msg);
                    return;
                }
            }
            if (errcontent.indexOf("确认新密码") == -1) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("重置密码失败，请重试");
                logger.info(bean.getCuserId() + " 重置密码  zhenXinGoToResetPwd");
                return;
            }
            Elements phone = errorDoc.getElementsByClass("user_text");
            String userPhone = "";
            if (phone != null && phone.size() > 0) {
                userPhone = "<phone>" + phone.first().text() + "</phone>";
                bean.setPhoneCode(phone.first().text());
            } else {
                userPhone = "<phone>********</phone>";
                bean.setPhoneCode("********");
            }

            logger.info(bean.getCuserId() + " 重置密码  zhenXinGoToResetPwd ");
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("验证用户信息成功");
            bean.setBusiXml(userPhone);
            memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdCookie", cookieStore, 1000 * 60 * 50);
            memCachedClient.delete(bean.getCuserId() + "zhenxinResetPwdSession");
        } catch (Exception e) {
            logger.error((bean.getCuserId() + " zhenXinGoToResetPwd errcontent[" + errcontent + "]  "), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("重置密码失败，请重试");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 忘记密码短信验证码
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investGetResetActivateCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";

        try {

            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinResetPwdCookie");
            if (object == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("页面已失效请重新申请");
                return;
            }
            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();

            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/resetPassword.do");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            Map<String, String> parames = new LinkedHashMap<String, String>();

            parames.put("method", "getAcvitaveCode");
            parames.put("counttime", "119");
            url = "https://ipcrs.pbccrc.org.cn/resetPassword.do?num=" + Math.random();
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if (isExpired(errcontent,bean,response)) {
                return;
            }
            String tcId = errcontent.trim();

            if ("success".equals(tcId)) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("认证码已发送");
                memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdCookie", cookieStore, 1000 * 60 * 50);
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("认证码发送失败");
                logger.info(bean.getCuserId() + "认证码发送失败 [" + tcId + "]  ");
            }
        } catch (Exception e) {
            logger.error((bean.getCuserId() + " zhenXinGetResetAcvitaveCode errcontent[" + errcontent + "]  "), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("认证码发送失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 重置密码申请问题
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investGetResetQuestions(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";

        try {
            if (CheckUtil.isNullString(bean.getPassword()) || CheckUtil.isNullString(bean.getConfirmpassword())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("新密码和确认新密不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码不能为空");
                return;
            }

            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinResetPwdCookie");
            if (object == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("页面已失效请重新申请");
                return;
            }

            String pwdword = "";
            String confirmpassword = "";

            if ("1".equals(bean.getClient())) {
                pwdword = CaiyiEncryptIOS.dencryptStr(bean.getPassword());
                confirmpassword = CaiyiEncryptIOS.dencryptStr(bean.getConfirmpassword());
            } else {
                pwdword = CaiyiEncrypt.dencryptStr(bean.getPassword());
                confirmpassword = CaiyiEncrypt.dencryptStr(bean.getConfirmpassword());
            }

            if (CheckUtil.isNullString(pwdword) || CheckUtil.isNullString(confirmpassword)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("参数加密不正确！");
                return;
            }

            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();

            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/resetPassword.do");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            Map<String, String> parames = new LinkedHashMap<String, String>();
            parames.put("method", "resetPassword");
            parames.put("counttime", "1");
            parames.put("password", pwdword);
            parames.put("confirmpassword", confirmpassword);
            parames.put("verifyCode", bean.getCode());
            url = "https://ipcrs.pbccrc.org.cn/resetPassword.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if (isExpired(errcontent,bean,response)) {
                return;
            }

            Document rpdoc = Jsoup.parse(errcontent);
            if (errcontent.indexOf("确认新密码：") != -1) {
                //验证没有通过
                org.jsoup.nodes.Element errorhtml = rpdoc.getElementById("_error_field_");
                String msg = "";
                if (errorhtml != null) {
                    msg = errorhtml.text();
                    if (!CheckUtil.isNullString(msg)) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc(msg);
                        logger.info(bean.getCuserId() + " 重置密码  zhenXinGetResetQuestion " + msg);
                        return;
                    }
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("重置密码失败，请重试");
                    logger.info(bean.getCuserId() + " 重置密码  zhenXinGetResetQuestion " + errcontent);
                    return;
                }
            }

            if (errcontent.contains("因平台收录信息不足，无法提问题验证您的身份，请使用其它验证方式")){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("因平台收录信息不足，无法提问题验证您的身份，请暂时登录官网使用其它验证方式。");
                logger.info(bean.getCuserId() + " 因平台收录信息不足，无法提问题验证您的身份，请使用其它验证方式  zhenXinGetResetQuestion " + errcontent);
                return;
            }

            String TOKEN = rpdoc.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
            parames.clear();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("method", "chooseCertify");
            parames.put("authtype", "2");
            url = "https://ipcrs.pbccrc.org.cn/resetPassword.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk");

            rpdoc = Jsoup.parse(errcontent);
            TOKEN = rpdoc.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
            parames.clear();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("method", "saveKbaApply");

            org.jsoup.nodes.Element qustion = rpdoc.getElementsByClass("qustion").first();
            Elements lis = qustion.select("li");
            int g = 0;
            StringBuffer queXml = new StringBuffer();
            for (int i = 0; i < lis.size(); i++) {
                org.jsoup.nodes.Element qdoc = lis.get(i);
                String qus = qdoc.toString();
                if (qus.indexOf("<p>问题") != -1) {

                    String title = rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].question").first().val();
                    String answer1 = rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].options1").first().val();
                    String answer2 = rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].options2").first().val();
                    String answer3 = rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].options3").first().val();
                    String answer4 = rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].options4").first().val();
                    String answer5 = rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].options5").first().val();

                    System.out.println("问题" + (g + 1) + ":" + title);
                    System.out.println("答案1：" + answer1);
                    System.out.println("答案2：" + answer2);
                    System.out.println("答案3：" + answer3);
                    System.out.println("答案4：" + answer4);
                    System.out.println("答案5：" + answer5);
                    queXml.append("<question value=\"" + title.replaceAll("\"", "") + "\" quesnum=\"" + (g + 1) + "\" >\n");
                    queXml.append("<options value=\"" + answer1.replaceAll("\"", "") + "\" num=\"1\" />\n");
                    queXml.append("<options value=\"" + answer2.replaceAll("\"", "") + "\" num=\"2\" />\n");
                    queXml.append("<options value=\"" + answer3.replaceAll("\"", "") + "\" num=\"3\" />\n");
                    queXml.append("<options value=\"" + answer4.replaceAll("\"", "") + "\" num=\"4\" />\n");
                    queXml.append("<options value=\"" + answer5.replaceAll("\"", "") + "\" num=\"5\" />\n");
                    queXml.append("</question>\n");


                    parames.put("kbaList[" + g + "].derivativecode", rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].derivativecode").first().val());
                    parames.put("kbaList[" + g + "].businesstype", rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].businesstype").first().val());
                    parames.put("kbaList[" + g + "].questionno", rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].questionno").first().val());
                    parames.put("kbaList[" + g + "].kbanum", rpdoc.getElementsByAttributeValue("name", "kbaList[" + g + "].kbanum").first().val());
                    parames.put("kbaList[" + g + "].question", title);
                    parames.put("kbaList[" + g + "].options1", answer1);
                    parames.put("kbaList[" + g + "].options2", answer2);
                    parames.put("kbaList[" + g + "].options3", answer3);
                    parames.put("kbaList[" + g + "].options4", answer4);
                    parames.put("kbaList[" + g + "].options5", answer5);
                    g++;
                }
            }
            memCachedClient.set(bean.getCuserId() + "zhenxinresetPwdApplyQuestionsParames", parames, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdCookie", cookieStore, 1000 * 60 * 50);
            bean.setBusiErrCode(1);
            bean.setBusiXml(queXml.toString());
        } catch (Exception e) {
            logger.error((bean.getCuserId() + " zhenXinGetResetQuestion errcontent[" + errcontent + "]  "), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("重置密码验证失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
            sb.append(bean.getBusiXml());
            sb.append("</Resp>");
            XmlUtils.writeXml(sb.toString(), response);
        }
    }

    /**
     * 重置密码申请提交
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investApplyResetPwd(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            if (CheckUtil.isNullString(bean.getOptions())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("重置密码问题答案不能为空");
                return;
            }

            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinResetPwdCookie");
            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinresetPwdApplyQuestionsParames");
            if (object == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                return;
            }
            if (object2 == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("重置密码问题申请不存在或已失效，请重新申请！");
                return;
            }
            CookieStore cookieStore = (CookieStore) object;
            Map<String, String> parames = (LinkedHashMap<String, String>) object2;
            String[] ops = null;
            if (!CheckUtil.isNullString(bean.getOptions())) {
                ops = bean.getOptions().split("o");
            }
            if (ops == null || ops.length == 0) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("信用报告问题答案数量不对等！请回答所有的问题");
                return;
            }
            for (int i = 0; i < ops.length; i++) {
                parames.put("kbaList[" + i + "].answerresult", ops[i]);
                parames.put("kbaList[" + i + "].options", ops[i]);
            }
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/resetPassword.do");
            url = "https://ipcrs.pbccrc.org.cn/resetPassword.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if (isExpired(errcontent,bean,response)) {
                return;
            }
//
            if (errcontent.contains("提交成功")) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("您的重置密码申请提交成功，请耐心等待。您可在24小时后登录平台查看验证结果");
                memCachedClient.delete(bean.getCuserId() + "zhenxinResetPwdCookie");
                memCachedClient.delete(bean.getCuserId() + "zhenxinresetPwdApplyQuestionsParames");
            }else if (errcontent.contains("申请已提交")){
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(" 您的重置密码申请已提交，系统会对您的相关信息进行审核，审核结果将于24小时后反馈到您的手机，请注意查收");
                memCachedClient.delete(bean.getCuserId() + "zhenxinResetPwdCookie");
                memCachedClient.delete(bean.getCuserId() + "zhenxinresetPwdApplyQuestionsParames");
            }else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("申请提交失败");
                Document rpdoc = Jsoup.parse(errcontent);
                org.jsoup.nodes.Element errorhtml = rpdoc.getElementById("_error_field_");
                if (errorhtml != null) {
                    String msg = errorhtml.text();
                    if (!CheckUtil.isNullString(msg)) {
                        bean.setBusiErrCode(-1);
                        bean.setBusiErrDesc(msg);
                        logger.info(bean.getCuserId() + " 重置密码申请提交失败  zhenXinApplyResetPwd " + msg);
                    }
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("重置密码失败，请重试");
                    logger.info(bean.getCuserId() + " 重置密码申请提交失败  zhenXinApplyResetPwd " + errcontent);
                }
            }
        } catch (Exception e) {
            logger.info("zhenXinApplyResetPwd 重置密码申请提交失败  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("重置密码申请失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 找回用户名
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    public void investFindLoginName(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("验证码不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getUsername())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("用户真实姓名不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getIdCardNo())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("用户身份证ID不能为空");
                return;
            }
            String username = "";
            String idcardno = "";
            if ("1".equals(bean.getClient())) {
                username = CaiyiEncryptIOS.dencryptStr(bean.getUsername());
                idcardno = CaiyiEncryptIOS.dencryptStr(bean.getIdCardNo());
            } else {
                username = CaiyiEncrypt.dencryptStr(bean.getUsername());
                idcardno = CaiyiEncrypt.dencryptStr(bean.getIdCardNo());
            }
            if (CheckUtil.isNullString(username) || CheckUtil.isNullString(idcardno)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("用户真实姓名或者身份证ID加密不正确！");
                return;
            }

            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinFdnCookie");
            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinFdnSession");
            if (object == null || object2 == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("验证码已经失效，请重试");
                return;
            }
            String sessionID = (String) object2;
            String[] paras = sessionID.split("@");
            String TOKEN = paras[0];
            String method = paras[1];
            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/findLoginName.do?method=init");

            Map<String, String> parames = new HashMap<String, String>();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("method", method);
            parames.put("name", username);
            parames.put("certType", "0");
            parames.put("certNo", idcardno);
            parames.put("_@IMGRC@_", bean.getCode());
            url = "https://ipcrs.pbccrc.org.cn/findLoginName.do";

            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

            if (isExpired(errcontent,bean,response)) {
                return;
            }

            org.jsoup.nodes.Element reghtml = Jsoup.parse(errcontent);
            org.jsoup.nodes.Element errorhtml = reghtml.getElementById("_error_field_");
            org.jsoup.nodes.Element errorhtml2 = reghtml.getElementById("_@MSG@_");
            String msg = "";
            String msg2 = "";
            if (errorhtml != null) {
                msg = errorhtml.text();
            }
            if (errorhtml2 != null) {
                msg2 = errorhtml2.text();
            }
            msg = CheckUtil.isNullString(msg) ? msg2 : msg;
            if (!CheckUtil.isNullString(msg)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(msg);
                logger.info(bean.getCuserId() + "找回用户名失败 错误: " + msg);
                return;
            }

            if (errcontent.contains("您的登录名已短信发送至平台预留的手机号码")) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("您的登录名已短信发送至平台预留的手机号码，请查收。");
            } else {
                logger.info("找回登录名，未知页面 zxFindLoginName  errcontent[" + errcontent + "]  ");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("未知错误");
            }
        } catch (Exception e) {
            logger.info("zxFindLoginName  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("找回用户名失败,请稍后再试");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

}
