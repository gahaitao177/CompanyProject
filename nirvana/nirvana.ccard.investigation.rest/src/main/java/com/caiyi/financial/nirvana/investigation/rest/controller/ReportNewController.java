package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.base.BaseReportController;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
//@RequestMapping("/credit")
public class ReportNewController extends BaseReportController{
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 征信信用报告问题认证获取
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    @RequestMapping("/control/investigation/zxGetQuestionsNew.go")
//    @RequestMapping("/zxGetQuestionsNew.go")
    public void investGetQuestions(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        Map<String,String> map=new HashMap<>();
        String url = "";
        String errcontent = "";
        String loginname = "";
        String loginpwd = "";
        try {
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            Object loginobject = memCachedClient.get(bean.getCuserId() + "zhenxinLoginname");
            Object loginPwdobject = memCachedClient.get(bean.getCuserId() + "zhenxinPwdword");
            if (object == null || loginobject == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                responseJson(bean, response);
                return;
            }
            loginname = String.valueOf(loginobject);
            loginpwd = (String) loginPwdobject;
            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/menu.do");
            Map<String, String> parames = new HashMap<String, String>();
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=applicationReport";
            errcontent = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);


            if (isExpired(errcontent,bean,response)) {
                return;
            }

            if (errcontent.contains("问题验证")) {
                Document applyhtml = Jsoup.parse(errcontent);
                String TOKEN = applyhtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                parames.clear();
                parames.put("method", "checkishasreport");
                parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
                parames.put("authtype", "2");
                parames.put("ApplicationOption", "25");
                parames.put("ApplicationOption", "24");
                parames.put("ApplicationOption", "21");
                url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
                errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

                if (errcontent.contains("不再保留，是否继续")) {
                    if ("0".equals(bean.getIskeep())) {
                        Document againApplyhtml = Jsoup.parse(errcontent);
                        TOKEN = againApplyhtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                        parames.clear();
                        parames.put("method", "verify");
                        parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
                        parames.put("authtype", "2");
                        parames.put("ApplicationOption", "25");
                        parames.put("ApplicationOption", "24");
                        parames.put("ApplicationOption", "21");
                        url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
                        errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk");
                    } else {
                        bean.setBusiErrCode(2);
                        bean.setBusiErrDesc("您的个人信用信息产品已存在。若继续申请查询，现有的个人信用信息产品将不再保留，是否继续？ ");
                        responseJson(bean,response);
                        return;
                    }
                }




                logger.info(bean.getCuserId() + " zhengxintemp:" + errcontent);
                Document qhtml = Jsoup.parse(errcontent);

                if (errcontent.contains("messages")) {
                    String messages = qhtml.getElementById("messages").text();
                    if (messages.contains("目前系统尚未收录足够的信息对您的身份进行“问题验证”")) {
                        messages = "目前系统尚未收录足够的信息对您的身份进行“问题验证”。";
                    }
                    logger.info(bean.getCuserId() + " zhenXinGetQuestion loginname[" + loginname + "] messages[" + messages + "]");
                    if (!CheckUtil.isNullString(messages)) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc(messages);
                        responseJson(bean,response);
                        return;
                    }
                }


                TOKEN = qhtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                org.jsoup.nodes.Element qustion = qhtml.getElementsByClass("qustion").first();
                logger.info(bean.getCuserId() + " zhengxintempqustion:" + qustion.html());
                Elements lis = qustion.select("li");
                int g = 0;
                parames.clear();
//                StringBuffer queXml = new StringBuffer();
                StringBuffer queJson = new StringBuffer();

                queJson.append("{\"code\":\"1\",\"desc\":\"获取问题列表成功\",\"questions\": [");

                for (int i = 0; i < lis.size(); i++) {
                    org.jsoup.nodes.Element qdoc = lis.get(i);
                    String qus = qdoc.toString();
                    if (qus.contains("<p>问题")) {

                        String title = qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].question").first().val();
                        String answer1 = qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].options1").first().val();
                        String answer2 = qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].options2").first().val();
                        String answer3 = qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].options3").first().val();
                        String answer4 = qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].options4").first().val();
                        String answer5 = qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].options5").first().val();


//						System.out.println("问题"+(g+1)+":"+title);
//						System.out.println("答案1："+answer1);
//						System.out.println("答案2："+answer2);
//						System.out.println("答案3："+answer3);
//						System.out.println("答案4："+answer4);
//						System.out.println("答案5："+answer5);

//                        queXml.append("<question value=\"" + title.replaceAll("\"", "") + "\" quesnum=\"" + (g + 1) + "\" >\n");
//                        queXml.append("<options value=\"" + answer1.replaceAll("\"", "") + "\" num=\"1\" />\n");
//                        queXml.append("<options value=\"" + answer2.replaceAll("\"", "") + "\" num=\"2\" />\n");
//                        queXml.append("<options value=\"" + answer3.replaceAll("\"", "") + "\" num=\"3\" />\n");
//                        queXml.append("<options value=\"" + answer4.replaceAll("\"", "") + "\" num=\"4\" />\n");
//                        queXml.append("<options value=\"" + answer5.replaceAll("\"", "") + "\" num=\"5\" />\n");
//                        queXml.append("</question>\n");


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

                        parames.put("kbaList[" + g + "].derivativecode", qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].derivativecode").first().val());
                        parames.put("kbaList[" + g + "].businesstype", qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].businesstype").first().val());
                        parames.put("kbaList[" + g + "].questionno", qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].questionno").first().val());
                        parames.put("kbaList[" + g + "].kbanum", qhtml.getElementsByAttributeValue("name", "kbaList[" + g + "].kbanum").first().val());
                        parames.put("kbaList[" + g + "].question", title);
                        parames.put("kbaList[" + g + "].options1", answer1);
                        parames.put("kbaList[" + g + "].options2", answer2);
                        parames.put("kbaList[" + g + "].options3", answer3);
                        parames.put("kbaList[" + g + "].options4", answer4);
                        parames.put("kbaList[" + g + "].options5", answer5);
//						String answerresult = "";
//						parames.put("kbaList["+g+"].answerresult", answerresult);
//						parames.put("kbaList["+g+"].options", answerresult);
                        g++;
                    }
                }
                queJson.append("]}");
//				org.apache.struts.taglib.html.TOKEN=21d267030eea8cfd7e42daa8965bb428&method=&authtype=2&ApplicationOption=25&ApplicationOption=24&ApplicationOption=21
                parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
                parames.put("authtype", "2");
                parames.put("ApplicationOption", "25");
                parames.put("ApplicationOption", "24");
                parames.put("ApplicationOption", "21");
                memCachedClient.set(bean.getCuserId() + "zhenxinApplyQuestionsParames", parames, 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
                bean.setBusiErrCode(1);
//                bean.setBusiXml(queXml.toString());
                XmlUtils.writeJson(queJson.toString(), response);
                return;
            } else if (errcontent.contains("submitQS")) {
//                if(bean.getFrom() == null || !bean.getFrom().equals("1")){
//                    bean.setBusiErrCode(0);
//                    bean.setBusiErrDesc("非常抱歉，由于您的申请信用信息需要电话动态码验证，请您暂时去官网申请");
//                    logger.info(bean.getCuserId()+" zhenXinGetQuestion 申请信用信息需要电话动态码验证 errorhtml["+errcontent+"] loginname["+loginname+"] loginpwd["+loginpwd+"]");
//                    return;
//                }
                //Modified By zhaojie 2016/10/8 14:40:14
                String phone="";
                Document applyhtml = Jsoup.parse(errcontent);
                if (errcontent.contains("user_text")){
                    phone=applyhtml.select(".user_text").first().text();
                }else{
                    phone="***********";
                }
                String TOKEN = applyhtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                parames.clear();
                parames.put("method", "send");
                parames.put("verifyCode", "");
                url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
                errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
                logger.info("请求短信快捷验证：" + errcontent);
                Map<String,String> rmap=new HashMap<>();
                if (errcontent.contains("success")) {
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc("短信验证码请求成功");
                    bean.setPhoneCode(phone);
                    memCachedClient.set(bean.getCuserId() + "zhenxinReportMessageCheckToken", TOKEN, 1000 * 60 * 50);
                    memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("获取申请报告问题失败");
                }


                rmap.put("code", String.valueOf(bean.getBusiErrCode()));
                rmap.put("desc", bean.getBusiErrDesc());
                if (!CheckUtil.isNullString(bean.getPhoneCode())){
                    rmap.put("phone", bean.getPhoneCode());
                }
                XmlUtils.writeJson(JSONObject.toJSON(rmap).toString(), response);
                logger.info(bean.getCuserId() + " zhenXinGetQuestion 申请信用信息需要电话动态码验证 errorhtml[" + errcontent + "] " +
                        "loginname[" + loginname + "] loginpwd[" + loginpwd + "] phone["+phone+"]");
                return;
            }else if(errcontent.contains("银行卡验证") || errcontent.contains("数字证书验证")){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("暂不支持当前验证方式,请到官网申请.");
                responseJson(bean, response);
                return;
            } else {
                Document loginHtml = Jsoup.parse(errcontent);
                String msg ="";
                if (errcontent.contains("_error_field_")){
                    msg = loginHtml.getElementById("_error_field_").html();
                    if (CheckUtil.isNullString(msg)) {
                        org.jsoup.nodes.Element msgele = loginHtml.getElementById("_@MSG@_");
                        if (msgele != null) {
                            msg = msgele.text();
                        }

                    }
                }else{
                    msg="获取申请报告问题失败";
                }
                logger.info(bean.getCuserId() + " zhenXinApplyReport 获取问题失败原因[" + msg + "] errcontent[" + errcontent + "]  ");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(msg);
                responseJson(bean, response);
            }
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", loginname, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinPwdword", loginpwd, 1000 * 60 * 50);

        } catch (Exception e) {
            logger.error(bean.getCuserId() + " zhenXinGetQuestion异常 errorhtml[" + errcontent + "] loginname[" + loginname + "] loginpwd[" + loginpwd + "]", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("获取申请报告问题失败");
            responseJson(bean, response);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }


    /**
     * 征信信用报告申请
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/control/investigation/zxApplyReportNew.go")
//    @RequestMapping("/zxApplyReportNew.go")
    public void investApplyReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investApplyReport(bean, request, response);
        if(bean.getBusiErrCode()==1){
            bean.setClientType("20");
            if (("您的信用信息查询请求已提交，请在24小时后访问平台获取结果，身份验证不通过请重新申请。" +
                    "为保障您的信息安全，您申请的信用信息将于7日后自动清理，请及时获取查询结果。").equals(bean.getBusiErrDesc())){
                //更新征信操作状态
                bean.setApplyDate(new Date());
            }else if(bean.getBusiErrDesc().contains("正在受理")){
                try {
                    String msg=bean.getBusiErrDesc();
                    if (msg.contains("于")&&msg.contains("秒")){
                        String date=msg.substring(msg.indexOf("于")+1, msg.indexOf("秒"));
                        date=date.replaceAll("年", "-").replaceAll("月", "-").replaceAll("日", " ")
                                .replaceAll("时", ":").replaceAll("分", ":");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        bean.setApplyDate(sdf.parse(date));
                        logger.info(bean.getCuserId() + "--investApplyReport date=" + date);
                    }
                }catch (Exception e){
                    bean.setApplyDate(new Date());
                }
            }

            String rcode=client.execute(new DrpcRequest("investLogin", "updateAccount", bean));
            logger.info(bean.getCuserId()+"--investApplyReport rcode="+rcode);
        }
        logger.info(bean.getCuserId()+"--investApplyReport desc="+bean.getBusiErrDesc());
        responseJson(bean, response);
    }



    /**
     * 征信信用短信快捷申请
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxSpeedApplyReportNew.go")
    public void investSpeedApplyReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investSpeedApplyReport(bean, request, response);
        if(bean.getBusiErrCode()==1){
            bean.setClientType("20");
            if (("您的信用信息查询请求已提交，请在24小时后访问平台获取结果，身份验证不通过请重新申请。" +
                    "为保障您的信息安全，您申请的信用信息将于7日后自动清理，请及时获取查询结果。").equals(bean.getBusiErrDesc())){
                //更新征信操作状态
                bean.setApplyDate(new Date());
            }else if(bean.getBusiErrDesc().contains("正在受理")){
                try {
                    String msg=bean.getBusiErrDesc();
                    if (msg.contains("于")&&msg.contains("秒")){
                        String date=msg.substring(msg.indexOf("于")+1, msg.indexOf("秒"));
                        date=date.replaceAll("年", "-").replaceAll("月", "-").replaceAll("日", " ")
                                .replaceAll("时", ":").replaceAll("分", ":");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        bean.setApplyDate(sdf.parse(date));
                        logger.info(bean.getCuserId() + "--investSpeedApplyReport date=" + date);
                    }
                }catch (Exception e){
                    bean.setApplyDate(new Date());
                }
            }
            String rcode=client.execute(new DrpcRequest("investLogin", "updateAccount", bean));
            logger.info(bean.getCuserId()+"--investSpeedApplyReport rcode="+rcode);
        }
        responseJson(bean,response);
    }


    /**
     * 征信获取信用报告
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/control/investigation/zxViewReportNew.go")
    public void investViewReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investViewReport(bean, request, response);
        if (bean.getBusiErrCode()==1){
            //更新征信操作状态
            bean.setClientType("50");
            bean.setLoginname(null);
            bean.setPassword(null);
            bean.setUpdate(new Date());
            String rcode=client.execute(new DrpcRequest("investLogin", "updateAccount", bean));
            logger.info(bean.getCuserId() + "--zxViewReportNew rcode=" + rcode);
        }
        responseJson(bean,response);
    }

    /**
     * 查询征信报告记录
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/control/investigation/queryUserCreditreFerenceNew.go")
//    @RequestMapping("/queryUserCreditreFerenceNew.go")
    public void queryUserCreditreFerence(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            String jsonRes = client.execute(new DrpcRequest("investReport", "queryUserReferenceByLoginname", bean));
            XmlUtils.writeJson(jsonRes,response);
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询失败");
            responseJson(bean, response);
        }
    }


    /**
     * 查询短信发送状态
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/control/investigation/zxCheckSendAgainStatus.go")
    public void investCheckSendAgainStatus(Channel bean, HttpServletRequest request, HttpServletResponse response){
        String errcontent = "";
        try {
            memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            Object cookieObj = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            if (cookieObj == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                responseJson(bean, response);
                return ;
            }
            CookieStore cookieStore = (CookieStore) cookieObj;
            CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            localContext.setAttribute("http.cookie-store", cookieStore);
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/menu.do");
            String url = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport";
            errcontent = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
            Document checkouthtml = Jsoup.parse(errcontent);
            String radiobutton1 = checkouthtml.getElementById("radiobutton1").toString();
            String radiobutton2 = checkouthtml.getElementById("radiobutton2").toString();
            String radiobutton3 = checkouthtml.getElementById("radiobutton3").toString();
            logger.info(bean.getCuserId() + "investCheckSendAgainStatus radiobutton1=" + radiobutton1 + " radiobutton2=" + radiobutton2 + " radiobutton3=" + radiobutton3);

            bean.setTaskId("-1");
            if (!radiobutton1.contains("disabled")) {
                bean.setTaskId(checkouthtml.getElementById("radiobutton1").val());
            }
//            else if (!radiobutton2.contains("disabled")){
//                bean.setTaskId(checkouthtml.getElementById("radiobutton2").val());
//            }else if (!radiobutton3.contains("disabled")){
//                bean.setTaskId(checkouthtml.getElementById("radiobutton3").val());
//            }
            logger.info(bean.getCuserId()+" zhenXinLogin TaskId="+bean.getTaskId());
            if ("-1".equals(bean.getTaskId())){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("个人信用报告未生成，不满足发送短信条件");
            }else{
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("个人信用报告已生成，满足发送短信条件");
                memCachedClient.set(bean.getCuserId() + bean.getLoginname() + "sendAgainStatus", bean.getTaskId(), 1000 * 60 * 10);
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
            }
        }catch (Exception e){
            logger.error(bean.getCuserId()+" investCheckSendAgainStatus 异常errcontent="+errcontent,e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询失败");
        }
        responseJson(bean, response);
    }


    /**
     * 重新发送身份证验证码
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/control/investigation/zxSendAgain.go")
    public void investCheckSendAgain(Channel bean, HttpServletRequest request, HttpServletResponse response){
        String errcontent = "";
        try {
            memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            Object cookieObj = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            if (cookieObj == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                responseJson(bean, response);
                return ;
            }

            Object sendAgainflag = memCachedClient.get(bean.getCuserId() + bean.getLoginname() +"sendAgainStatus");
            if (sendAgainflag==null){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("没有检查是否有发送资格");
                responseJson(bean, response);
                logger.info(bean.getCuserId() + " zxCheckSendAgain fail 没有检查CheckSendAgainStatus");
                return ;
            }
            String sendAgainStatus=String.valueOf(sendAgainflag);
            CookieStore cookieStore = (CookieStore) cookieObj;
            CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            Map<String, String> parames = new HashMap<String, String>();
            localContext.setAttribute("http.cookie-store", cookieStore);
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            String url = "https://ipcrs.pbccrc.org.cn/reportAction.do?num="+Math.random();
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport");
            requestHeaderMap.put("X-Requested-With","XMLHttpRequest");
            requestHeaderMap.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");

            parames.put("method","sendAgain");
            parames.put("reportformat",sendAgainStatus);
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if (errcontent.contains("success")){
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
                logger.info(bean.getCuserId()+" investCheckSendAgain sendAgainStatus["+sendAgainStatus+"] success 重新发送身份证验证码成功"+errcontent);
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("重新发送身份证验证码成功");
            }else {
                logger.info(bean.getCuserId()+" investCheckSendAgain sendAgainStatus["+sendAgainStatus+"] fail 重新发送身份证验证码失败"+errcontent);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("重新发送身份证验证码失败");
            }
        }catch (Exception e){
            logger.error(bean.getCuserId() + " investCheckSendAgain 异常errcontent=" + errcontent, e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("重新发送身份证验证码失败");
        }
        responseJson(bean, response);
    }

}