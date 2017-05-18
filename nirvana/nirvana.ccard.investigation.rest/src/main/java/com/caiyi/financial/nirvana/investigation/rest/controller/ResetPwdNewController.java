package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.base.BaseResetPwdController;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Controller
public class ResetPwdNewController extends BaseResetPwdController{
    private static Logger logger = LoggerFactory.getLogger(ResetPwdNewController.class);

    /**
     * 征信忘记密码认证用户信息
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping("/control/investigation/zxGoToResetPwdNew.go")
    public void investGoToResetPwd(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investGoToResetPwd(bean, request, response);
        Map<String,String> map=new HashMap<>();
        map.put("code", String.valueOf(bean.getBusiErrCode()));
        map.put("desc", bean.getBusiErrDesc());
        map.put("phone",bean.getPhoneCode());
        XmlUtils.writeJson(JSONObject.toJSON(map).toString(), response);
    }

    /**
     * 忘记密码短信验证码
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxGetResetAcvitaveCodeNew.go")
    public void investGetResetActivateCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investGetResetActivateCode(bean, request, response);
        responseJson(bean, response);
    }

    /**
     * 重置密码申请问题
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxGetResetQuestionsNew.go")
    public void investGetResetQuestions(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";

        try {
            if (CheckUtil.isNullString(bean.getPassword()) || CheckUtil.isNullString(bean.getConfirmpassword())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("新密码和确认新密不能为空");
                responseJson(bean, response);
                return;
            }
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信验证码不能为空");
                responseJson(bean,response);
                return;
            }

            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinResetPwdCookie");
            if (object == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("页面已失效请重新申请");
                responseJson(bean, response);
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
                responseJson(bean,response);
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
                        responseJson(bean,response);
                        return;
                    }
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("重置密码失败，请重试");
                    logger.info(bean.getCuserId() + " 重置密码  zhenXinGetResetQuestion " + errcontent);
                    responseJson(bean,response);
                    return;
                }
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
//            StringBuffer queXml = new StringBuffer();

            StringBuffer queJson = new StringBuffer();
            queJson.append("{\"code\":\"1\",\"desc\":\"获取问题列表成功\",\"questions\": [");

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
//                    queXml.append("<question value=\"" + title.replaceAll("\"", "") + "\" quesnum=\"" + (g + 1) + "\" >\n");
//                    queXml.append("<options value=\"" + answer1.replaceAll("\"", "") + "\" num=\"1\" />\n");
//                    queXml.append("<options value=\"" + answer2.replaceAll("\"", "") + "\" num=\"2\" />\n");
//                    queXml.append("<options value=\"" + answer3.replaceAll("\"", "") + "\" num=\"3\" />\n");
//                    queXml.append("<options value=\"" + answer4.replaceAll("\"", "") + "\" num=\"4\" />\n");
//                    queXml.append("<options value=\"" + answer5.replaceAll("\"", "") + "\" num=\"5\" />\n");
//                    queXml.append("</question>\n");



                    if (g!=0){
                        queJson.append(",");
                    }
                    queJson.append("{\"quesnum\":\""+(g + 1)+"\",\"value\":\""+title.replaceAll("\"", "")+"\"");
                    queJson.append(",\"options\":[{\"num\": \"1\",\"value\":\""+answer1.replaceAll("\"", "")+"\"}");
                    queJson.append(",{\"num\": \"2\",\"value\":\""+answer2.replaceAll("\"", "")+"\"}");
                    queJson.append(",{\"num\": \"3\",\"value\":\""+answer3.replaceAll("\"", "")+"\"}");
                    queJson.append(",{\"num\": \"4\",\"value\":\""+answer4.replaceAll("\"", "")+"\"}");
                    queJson.append(",{\"num\": \"5\",\"value\":\""+answer5.replaceAll("\"", "")+"\"}");
                    queJson.append("]}");





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
            queJson.append("]}");
            memCachedClient.set(bean.getCuserId() + "zhenxinresetPwdApplyQuestionsParames", parames, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdCookie", cookieStore, 1000 * 60 * 50);
            bean.setBusiErrCode(1);
            XmlUtils.writeJson(queJson.toString(), response);
        } catch (Exception e) {
            logger.error((bean.getCuserId() + " zhenXinGetResetQuestion errcontent[" + errcontent + "]  "), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("重置密码验证失败");
            responseJson(bean,response);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }

        }
    }

    /**
     * 重置密码申请提交
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxApplyResetPwdNew.go")
    public void investApplyResetPwd(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investApplyResetPwd(bean,request,response);
        responseJson(bean,response);
    }

    /**
     * 找回用户名
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping("/control/investigation/zxFindLoginNameNew.go")
    public void investFindLoginName(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investFindLoginName(bean,request,response);
        responseJson(bean,response);
    }










}
