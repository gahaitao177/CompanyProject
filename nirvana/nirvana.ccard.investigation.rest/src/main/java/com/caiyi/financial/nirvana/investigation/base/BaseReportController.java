package com.caiyi.financial.nirvana.investigation.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
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
import java.util.Map;

/**
 * Created by terry on 2016/10/25.
 */
public class BaseReportController extends BaseController{

    /**
     * 征信信用报告问题认证获取
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    public void investGetQuestions(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
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
            if (errcontent.indexOf("问题验证") != -1) {
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

                if (errcontent.indexOf("不再保留，是否继续") != -1) {
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
                        return;
                    }
                }


                TOKEN = qhtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                org.jsoup.nodes.Element qustion = qhtml.getElementsByClass("qustion").first();
                logger.info(bean.getCuserId() + " zhengxintempqustion:" + qustion.html());
                Elements lis = qustion.select("li");
                int g = 0;
                parames.clear();
                StringBuffer queXml = new StringBuffer();
                for (int i = 0; i < lis.size(); i++) {
                    org.jsoup.nodes.Element qdoc = lis.get(i);
                    String qus = qdoc.toString();
                    if (qus.indexOf("<p>问题") != -1) {

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
                        queXml.append("<question value=\"" + title.replaceAll("\"", "") + "\" quesnum=\"" + (g + 1) + "\" >\n");
                        queXml.append("<options value=\"" + answer1.replaceAll("\"", "") + "\" num=\"1\" />\n");
                        queXml.append("<options value=\"" + answer2.replaceAll("\"", "") + "\" num=\"2\" />\n");
                        queXml.append("<options value=\"" + answer3.replaceAll("\"", "") + "\" num=\"3\" />\n");
                        queXml.append("<options value=\"" + answer4.replaceAll("\"", "") + "\" num=\"4\" />\n");
                        queXml.append("<options value=\"" + answer5.replaceAll("\"", "") + "\" num=\"5\" />\n");
                        queXml.append("</question>\n");


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
//				org.apache.struts.taglib.html.TOKEN=21d267030eea8cfd7e42daa8965bb428&method=&authtype=2&ApplicationOption=25&ApplicationOption=24&ApplicationOption=21
                parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
                parames.put("authtype", "2");
                parames.put("ApplicationOption", "25");
                parames.put("ApplicationOption", "24");
                parames.put("ApplicationOption", "21");
                memCachedClient.set(bean.getCuserId() + "zhenxinApplyQuestionsParames", parames, 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
                bean.setBusiErrCode(1);
                bean.setBusiXml(queXml.toString());
            } else if (errcontent.contains("submitQS")) {
                if(bean.getFrom() == null || !bean.getFrom().equals("1")){
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("非常抱歉，由于您的申请信用信息需要电话动态码验证，请您暂时去官网申请");
                    logger.info(bean.getCuserId()+" zhenXinGetQuestion 申请信用信息需要电话动态码验证 errorhtml["+errcontent+"] loginname["+loginname+"] loginpwd["+loginpwd+"]");
                    return;
                }
                //Modified By zhaojie 2016/10/8 14:40:14
                Document applyhtml = Jsoup.parse(errcontent);
                String TOKEN = applyhtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                parames.clear();
                parames.put("method", "send");
                parames.put("verifyCode", "");
                url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
                errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
                logger.info("请求短信快捷验证：" + errcontent);
                if (errcontent.contains("success")) {
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc("短信验证码请求成功");
                    memCachedClient.set(bean.getCuserId() + "zhenxinReportMessageCheckToken", TOKEN, 1000 * 60 * 50);
                    memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("获取申请报告问题失败");
                }
                logger.info(bean.getCuserId() + " zhenXinGetQuestion 申请信用信息需要电话动态码验证 errorhtml[" + errcontent + "] loginname[" + loginname + "] loginpwd[" + loginpwd + "]");
                return;
            }else if(errcontent.contains("银行卡验证") || errcontent.contains("数字证书验证")){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("暂不支持当前验证方式,请到官网申请.");
                return;
            } else {
                Document loginHtml = Jsoup.parse(errcontent);
                String msg = loginHtml.getElementById("_error_field_").html();
                if (CheckUtil.isNullString(msg)) {
                    org.jsoup.nodes.Element msgele = loginHtml.getElementById("_@MSG@_");
                    if (msgele != null) {
                        msg = msgele.text();
                    }

                }
                logger.info(bean.getCuserId() + " zhenXinApplyReport 获取问题失败原因[" + msg + "] errcontent[" + errcontent + "]  ");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(msg);
            }
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", loginname, 1000 * 60 * 50);
        } catch (Exception e) {
            logger.error(bean.getCuserId() + " zhenXinGetQuestion异常 errorhtml[" + errcontent + "] loginname[" + loginname + "] loginpwd[" + loginpwd + "]", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("获取申请报告问题失败");
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
     * 征信信用报告申请
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investApplyReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            if (CheckUtil.isNullString(bean.getOptions())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("问题申请报告不存在或已失效，请重新申请！");
                return;
            }
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinApplyQuestionsParames");
            Object loginobject = memCachedClient.get(bean.getCuserId() + "zhenxinLoginname");
            Object loginpwdobject = memCachedClient.get(bean.getCuserId() + "zhenxinPwdword");

            if (object == null || loginobject == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                return;
            }
            if (object2 == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("问题申请报告不存在或已失效，请重新申请！");
                return;
            }
            String loginname = String.valueOf(loginobject);
            String loginpwd = String.valueOf(loginpwdobject);
            bean.setLoginname(loginname);
            bean.setPassword(loginpwd);
            CookieStore cookieStore = (CookieStore) object;
            Map<String, String> parames = (HashMap<String, String>) object2;
            //String[] ops = StringUtil.splitter(bean.getOptions(), "o");
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
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/reportAction.do?method=applicationReport");
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=submitKBA";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if (isExpired(errcontent,bean,response)) {
                return;
            }
            Document rpdoc = Jsoup.parse(errcontent);
            if (errcontent.indexOf("已提交") != -1) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("您的信用信息查询请求已提交，请在24小时后访问平台获取结果，身份验证不通过请重新申请。为保障您的信息安全，您申请的信用信息将于7日后自动清理，请及时获取查询结果。");
                memCachedClient.delete(bean.getCuserId() + "zhenxinApplyQuestionsParames");
                logger.info(bean.getCuserId() + " 您的信用信息查询请求已提交 zhenXinApplyReport " + errcontent);


            } else if (errcontent.indexOf("正在受理") != -1) {
                Elements elements = rpdoc.getElementsByClass("erro_div1");
                if (elements != null && elements.size() > 0) {
                    String msg = elements.first().text();
                    bean.setBusiErrDesc(msg);
                } else {
                    bean.setBusiErrDesc("您提交的个人信用报告查询申请正在受理，请耐心等待");
                }
                logger.info(bean.getCuserId() + " 申请正在受理 zhenXinApplyReport " + errcontent);
                memCachedClient.delete(bean.getCuserId() + "zhenxinApplyQuestionsParames");
                bean.setBusiErrCode(1);

            } else {
                org.jsoup.nodes.Element errorhtml = rpdoc.getElementById("_error_field_");
                if (errorhtml != null) {
                    String msg = errorhtml.text();
                    if (!CheckUtil.isNullString(msg)) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc(msg);
                        logger.info(bean.getCuserId() + " 信用申请提交失败  zhenXinApplyReport " + msg);
                    }
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("申请提交失败");
                    logger.info(bean.getCuserId() + " 信用申请提交失败 zhenXinApplyReport " + errcontent);
                }
            }
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
            //更新状态
            client.execute(new DrpcRequest("investLogin", "checkReportExists", bean));
        } catch (Exception e) {
            logger.info(bean.getCuserId() + " zhenXinApplyReport申请提交失败  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("申请提交失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }
    /**
     * 征信信用短信快捷申请
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investSpeedApplyReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("手机动态码不能为空.");
                return;
            }
            Object cookieObj = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            Object tokenObj = memCachedClient.get(bean.getCuserId() + "zhenxinReportMessageCheckToken");
            Object loginobject = memCachedClient.get(bean.getCuserId() + "zhenxinLoginname");
            Object loginpwdobject = memCachedClient.get(bean.getCuserId() + "zhenxinPwdword");

            if (loginpwdobject == null || loginobject == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                return;
            }
            if (cookieObj == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                return;
            }
            if (tokenObj == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("会话不存在或已失效，请重新申请！");
                return;
            }
            String loginname = String.valueOf(loginobject);
            String loginpwd = String.valueOf(loginpwdobject);
            bean.setLoginname(loginname);
            bean.setPassword(loginpwd);
            CookieStore cookieStore = (CookieStore) cookieObj;
            String TOKEN = (String) tokenObj;
            httpClient = HpClientUtil.getHttpsClient();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/reportAction.do?method=applicationReport");
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=submitQS";
            Map<String, String> parames = new HashMap<String, String>();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("authtype", "5");
            parames.put("method", "submitQS");
            parames.put("ApplicationOption", "21");
            parames.put("verifyCode", bean.getCode());

            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

            if (isExpired(errcontent,bean,response)) {
                return;
            }

            if (errcontent.indexOf("不再保留，是否继续") != -1) {
                if ("0".equals(bean.getIskeep())) {
                    Document againApplyhtml = Jsoup.parse(errcontent);
                    TOKEN = againApplyhtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                    parames.clear();
                    parames.put("method", "verify");
                    parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
                    parames.put("authtype", "5");
                    parames.put("ApplicationOption", "21");
                    url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
                    errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk");
                } else {
                    bean.setBusiErrCode(2);
                    bean.setBusiErrDesc("您的个人信用信息产品已存在。若继续申请查询，现有的个人信用信息产品将不再保留，是否继续？ ");
                    return;
                }
            }
            Document rpdoc = Jsoup.parse(errcontent);
            if (errcontent.indexOf("动态码输入错误或已过期，请重新输入") != -1) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("动态码输入错误或已过期，请重新输入.");
                return;
            } else if (errcontent.indexOf("已提交") != -1) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("您的信用信息查询请求已提交，请在24小时后访问平台获取结果，身份验证不通过请重新申请。为保障您的信息安全，您申请的信用信息将于7日后自动清理，请及时获取查询结果。");
            } else if (errcontent.indexOf("正在受理") != -1) {
                Elements elements = rpdoc.getElementsByClass("erro_div1");
                if (elements != null && elements.size() > 0) {
                    String msg = elements.first().text();
                    bean.setBusiErrDesc(msg);
                } else {
                    bean.setBusiErrDesc("您提交的个人信用报告查询申请正在受理，请耐心等待");
                }
                bean.setBusiErrCode(1);
            } else {
                org.jsoup.nodes.Element errorhtml = rpdoc.getElementById("_error_field_");
                if (errorhtml != null) {
                    String msg = errorhtml.text();
                    if (!CheckUtil.isNullString(msg)) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc(msg);
                        logger.info(bean.getCuserId() + " 信用申请提交失败  zhenXinApplyReport " + msg);
                    }
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("申请提交失败");
                    logger.info(bean.getCuserId() + " 信用申请提交失败 zhenXinApplyReport " + errcontent);
                }
            }
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
        } catch (Exception e) {
            logger.info(bean.getCuserId() + " zhenXinApplyReport申请提交失败  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("申请提交失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 征信获取信用报告
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investViewReport(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("认证码不能为空");
                return;
            }
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            Object loginobject = memCachedClient.get(bean.getCuserId() + "zhenxinLoginname");
            Object loginpwdobject = memCachedClient.get(bean.getCuserId() + "zhenxinPwdword");
            if (object == null || loginobject == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                return;
            }

            logger.info(bean.getCuserId() + " msgCode=" + bean.getCode());
            String loginname = String.valueOf(loginobject);
            String loginpwd = String.valueOf(loginpwdobject);
            bean.setLoginname(loginname);
            bean.setPassword(loginpwd);
            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport");
            Map<String, String> parames = new HashMap<String, String>();
            parames.put("method", "checkTradeCode");
            parames.put("code", bean.getCode());
            parames.put("reportformat", "24");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

            if (isExpired(errcontent,bean,response)) {
                return;
            }

            errcontent = errcontent.replaceAll("\n", "").trim();
            String html1 = "";
            if ("0".equals(errcontent)) {
                //System.out.println("-------------------------个人信用信息概要--------------------------");
                requestHeaderMap.remove("X-Requested-With");
                requestHeaderMap.remove("Content-Type");
                parames.clear();
                parames.put("reportformat", "24");
                parames.put("tradeCode", bean.getCode());
                url = "https://ipcrs.pbccrc.org.cn/summaryReport.do?method=viewReport";
                errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
                html1 = errcontent;
            } else {
                logger.info(bean.getCuserId() + " errcontent1=" + errcontent);
            }

            String html2 = "";
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            parames.clear();
            parames.put("method", "checkTradeCode");
            parames.put("code", bean.getCode());
            parames.put("reportformat", "21");
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            errcontent = errcontent.replaceAll("\n", "").trim();
            if ("0".equals(errcontent)) {
                //System.out.println("--------------------------个人信用信息报告-------------------------");
                requestHeaderMap.remove("X-Requested-With");
                requestHeaderMap.remove("Content-Type");
                parames.clear();
                parames.put("reportformat", "21");
                parames.put("tradeCode", bean.getCode());
                url = "https://ipcrs.pbccrc.org.cn/simpleReport.do?method=viewReport";
                errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
                html2 = errcontent;
            } else {
                logger.info(bean.getCuserId() + " errcontent2=" + errcontent);
            }

            if (CheckUtil.isNullString(html2)){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("您的个人信用报告正在处理中，官网还未生成全部报告！为了您的数据完整请您稍后再试！");
                return;

            }
//            if (CheckUtil.isNullString(html1)){
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("您的个人信用信息概要正在处理中，官网还未生成全部报告！为了您的数据完整请您稍后再试！");
//                return;
//
//            }
//            if (CheckUtil.isNullString(html1) && CheckUtil.isNullString(html2)) {
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("获取信用报告失败！您未申请或者申请未通过，请重新申请！");
//                return;
//            }
            bean.setReportHtml1(html1);
            bean.setReportHtml2(html2);
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("获取信用报告成功");

            String res = client.execute(new DrpcRequest("investReport", "analyticalReport", bean));
            JSONObject jsonObj = JSON.parseObject(res);
            bean.setBusiErrCode((int) jsonObj.get("code"));
            bean.setBusiErrDesc(jsonObj.get("desc").toString());


        } catch (Exception e) {
            logger.info(bean.getCuserId() + " zhenXinViewReport  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("获取信用报告失败！");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 查询征信报告记录
     *
     * @param bean
     * @param request
     * @param response
     */
    public void queryUserCreditreFerence(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            String jsonRes = client.execute(new DrpcRequest("investReport", "queryUserReference", bean));
            JSONObject jsonObj = JSON.parseObject(jsonRes);
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<Resp code=\"" + jsonObj.get("code") + "\" desc=\"" + jsonObj.get("desc") + "\">");
            sb.append(jsonObj.get("xml"));
            sb.append("</Resp>");
            XmlUtils.writeXml(sb.toString(), response);
        } catch (Exception e) {
            XmlUtils.writeXml(0, "查询失败", response);
        }
    }

    /**
     * 检测是否已经收到申请回执短信
     * @param bean
     * @param request
     * @param response
     */
    public void investCheckMsgRecived(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");

            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/menu.do");
            Map<String, String> parames = new HashMap<String, String>();
            parames.put("method", "queryReport");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if(errcontent == null || errcontent.equals("")){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请求失败");
            }else if(errcontent.contains("您的信用信息查询请求已提交")){
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("未收到短信验证码");
            }else{
                bean.setBusiErrCode(2);
                bean.setBusiErrDesc("已收到短信验证码");
            }

        } catch (Exception e) {
            logger.info(bean.getCuserId() + " zhenXinViewReport  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    public void investCheckApplyStatus(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {

        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");

            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/menu.do");
            Map<String, String> parames = new HashMap<String, String>();
            parames.put("method", "applicationReport");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            Document doc = Jsoup.parse(errcontent);
            Elements status = doc.getElementsByClass("span-12");
            if (status.text() != null) {
                if (status.text().contains("处理中")) {
                    bean.setBusiErrCode(2);
                    bean.setBusiErrDesc(status.text().replaceAll("[()]", ""));
                } else if (status.text().contains("已生成")) {
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc(status.text().replaceAll("[()]", ""));
                } else {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("未申请");
                }
            }
        } catch (Exception e) {
            logger.info(bean.getCuserId() + " zhenXinViewReport  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

}
