package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.base.BaseLoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mario on 2016/7/19 0019.
 * 征信账户登录相关接口移植
 */
@Controller
@RequestMapping("/credit")
public class LoginController extends BaseLoginController{
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);
//    @Resource(name = Constant.HSK_CCARD_INVESTIGATION)
//    private IDrpcClient client;
//
//
//    @Autowired
//    MemCachedClient memCachedClient;
    /**
     * 获取验证码
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxVerifyCode.go")
    public void getVerifyCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.getVerifyCode(bean,request,response);
//        response.setContentType("image/jpeg");
//        response.setHeader("Pragma", "no-cache");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setDateHeader("Expires", 0);
//        HttpSession session = request.getSession();
//        if (session.isNew()) {
//            session.setMaxInactiveInterval(300);
//        }
//        ServletOutputStream localServletOutputStream = null;
//        BufferedImage localBufferedImage = null;
//        CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
//        try {
//            localServletOutputStream = response.getOutputStream();
//            CookieStore cookieStore = new BasicCookieStore();
//            HttpContext localContext = new BasicHttpContext();
//            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
//            localContext.setAttribute("http.cookie-store", cookieStore);
//            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
//
//            String userIp = InvestigationHelper.getRealIp(request).trim();
//            if (!CheckUtil.isNullString(userIp)) {
//                requestHeaderMap.put("X-Forwarded-For", userIp);
//            }
//
//            String content = "";
//            String url = "";
//
//            url = "https://ipcrs.pbccrc.org.cn/";
//            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/");
//            url = "https://ipcrs.pbccrc.org.cn/top1.do";
//            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//            url = "https://ipcrs.pbccrc.org.cn/index1.do";
//            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/top1.do");
//
//            if ("0".equals(bean.getType())) {
//                //登录
//                url = "https://ipcrs.pbccrc.org.cn/login.do?method=initLogin";
//                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//                url = "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp";
//                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//                if (content.indexOf("org.apache.struts.taglib.html.TOKEN") == -1) {
//                    bean.setBusiErrCode(1);
//                    bean.setBusiErrDesc("加载首页错误");
//                }
//                Document reghtml = Jsoup.parse(content);
//                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
//                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();
//                String date = reghtml.getElementsByAttributeValue("name", "date").get(0).val();
//
//                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
//                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
//                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);
//
//                String sessionID = TOKEN + "@" + method + "@" + date;
//                memCachedClient.set(bean.getCuserId() + "zhenxinLoginSession", sessionID, 1000 * 60 * 50);
//                memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
//            } else if ("1".equals(bean.getType())) {
//                //注册
//                url = "https://ipcrs.pbccrc.org.cn/userReg.do?method=initReg";
//                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//                Document reghtml = Jsoup.parse(content);
//                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
//                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();
//                System.out.println(TOKEN);
//                System.out.println(method);
//                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/userReg.do?method=initReg");
//                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
//                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);
//                String sessionID = TOKEN + "@" + method;
//                memCachedClient.set(bean.getCuserId() + "zhenxinVISession", sessionID, 1000 * 60 * 50);
//                memCachedClient.set(bean.getCuserId() + "zhenxinRegCookie", cookieStore, 1000 * 60 * 50);
//
//            } else if ("2".equals(bean.getType())) {
//                //重置密码
//                url = "https://ipcrs.pbccrc.org.cn/login.do?method=initLogin";
//                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//                url = "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp";
//                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//                if (content.indexOf("org.apache.struts.taglib.html.TOKEN") == -1) {
//                    bean.setBusiErrCode(1);
//                    bean.setBusiErrDesc("加载首页错误");
//                }
//                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
//                //首页
//                url = "https://ipcrs.pbccrc.org.cn/resetPassword.do?method=init";
//                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//
//                Document reghtml = Jsoup.parse(content);
//                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
//                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();
//
//                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/resetPassword.do?method=init");
//                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
//                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);
//
//                String sessionID = TOKEN + "@" + method;
//                memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdSession", sessionID, 1000 * 60 * 50);
//                memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdCookie", cookieStore, 1000 * 60 * 50);
//            } else if ("3".equals(bean.getType())) {
//                //找回用户名
//                url = "https://ipcrs.pbccrc.org.cn/findLoginName.do?method=init";
//                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//                Document reghtml = Jsoup.parse(content);
//                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
//                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();
//                System.out.println(TOKEN);
//                System.out.println(method);
//                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/findLoginName.do?method=init");
//                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
//                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);
//
//                String sessionID = TOKEN + "@" + method;
//                memCachedClient.set(bean.getCuserId() + "zhenxinFdnSession", sessionID, 1000 * 60 * 50);
//                memCachedClient.set(bean.getCuserId() + "zhenxinFdnCookie", cookieStore, 1000 * 60 * 50);
//            }
//            if (localBufferedImage != null) {
//                ImageIO.write(localBufferedImage, "PNG", localServletOutputStream);
//                localServletOutputStream.flush();
//                localServletOutputStream.close();
//            } else {
//                bean.setBusiErrCode(1);
//                bean.setBusiErrDesc("加载验证码图片失败");
//                XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
//            }
//        } catch (Exception e) {
//            logger.error("getZhenXinVerifyCode异常", e);
//        } finally {
//            if (httpClient != null) {
//                httpClient.close();
//            }
//        }
    }

    /**
     * 征信登录
     *
     * @param bean
     * @param request
     */
    @RequestMapping("/zxLogin.go")
    public void investLogin(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investOldLogin(bean, request, response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);

//        if (CheckUtil.isNullString(bean.getLoginname()) || CheckUtil.isNullString(bean.getPassword())) {
//            bean.setBusiErrCode(0);
//            bean.setBusiErrDesc("账号或者密码不能为空！");
//            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
//            return;
//        }
//        if (CheckUtil.isNullString(bean.getCode())) {
//            bean.setBusiErrCode(0);
//            bean.setBusiErrDesc("验证码不能为空！");
//            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
//            return;
//        }
//        String loginname = "";
//        String pwdword = "";
//        if ("1".equals(bean.getClient())) {
//            loginname = CaiyiEncryptIOS.dencryptStr(bean.getLoginname());
//            pwdword = CaiyiEncryptIOS.dencryptStr(bean.getPassword());
//        } else {
//            loginname = CaiyiEncrypt.dencryptStr(bean.getLoginname());
//            pwdword = CaiyiEncrypt.dencryptStr(bean.getPassword());
//        }
//        if (CheckUtil.isNullString(loginname) || CheckUtil.isNullString(pwdword)) {
//            bean.setBusiErrCode(0);
//            bean.setBusiErrDesc("账号或者密码加密不正确！");
//            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
//            return;
//        }
//
//        CloseableHttpClient httpClient = null;
//        String url = "";
//        String errcontent = "";
//        try {
//            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginSession");
//            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
//            if (object == null || object2 == null) {
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("验证码失效,请重新刷新");
//            }
//            String sessionID = (String) object;
//            String[] paras = sessionID.split("@");
//            String TOKEN = paras[0];
//            String method = paras[1];
//            String date = paras[2];
//
//            CookieStore cookieStore = (CookieStore) object2;
//            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
//            HttpContext localContext = new BasicHttpContext();
//            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
//            localContext.setAttribute("http.cookie-store", cookieStore);
//            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
//            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
//
//            String userIp = InvestigationHelper.getRealIp(request).trim();
//            if (!CheckUtil.isNullString(userIp)) {
//                requestHeaderMap.put("X-Forwarded-For", userIp);
//            }
//
//
//            Map<String, String> parames = new HashMap<String, String>();
//            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
//            parames.put("method", method);
//            parames.put("date", date);
//            parames.put("loginname", loginname);
//            parames.put("password", pwdword);
//            parames.put("_@IMGRC@_", bean.getCode());
//            url = "https://ipcrs.pbccrc.org.cn/login.do";
//            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
//            if (errcontent.indexOf("请输入登录名") != -1) {
//                Document loginHtml = Jsoup.parse(errcontent);
//                String msg = loginHtml.getElementById("_error_field_").html();
//                if (CheckUtil.isNullString(msg)) {
//                    org.jsoup.nodes.Element msgele = loginHtml.getElementById("_@MSG@_");
//                    if (msgele != null) {
//                        msg = msgele.text();
//                    }
//                }
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc(msg);
//                return;
//            }
//
//            //查看信用明细认证的页面（可判断是否有记录可查询）
//            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/menu.do");
//            url = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport";
//            errcontent = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
//            Document checkouthtml = Jsoup.parse(errcontent);
//            String radiobutton1 = checkouthtml.getElementById("radiobutton1").toString();
//            String radiobutton2 = checkouthtml.getElementById("radiobutton2").toString();
//            String radiobutton3 = checkouthtml.getElementById("radiobutton3").toString();
//            logger.info(bean.getCuserId() + "-------------------------查看信用明细认证的页面----------------------------radiobutton1=" + radiobutton1 + " radiobutton2=" + radiobutton2 + " radiobutton3=" + radiobutton3);
//
////			if (radiobutton1.indexOf("disabled")!=-1&&radiobutton2.indexOf("disabled")!=-1&&radiobutton3.indexOf("disabled")!=-1) {
//            bean.setBusiErrCode(1);
////			}else {
////				bean.setBusiErrCode(2);
////			}
//            bean.setBusiErrDesc("登录成功");
//            memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
//            memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", loginname, 1000 * 60 * 50);
//            memCachedClient.set(bean.getCuserId() + "zhenxinPwdword", pwdword, 1000 * 60 * 50);
//
//            //在数据库中检查是否已经存在报表
//            String jsonRes = client.execute(new DrpcRequest("investLogin", "checkReportExists", bean));
//            JSONObject jsonObj = JSON.parseObject(jsonRes);
//            bean.setBusiErrCode(Integer.parseInt(jsonObj.get("code").toString()));
//            bean.setBusiErrDesc(jsonObj.get("desc").toString());
//        } catch (Exception e) {
//            logger.error(bean.getCuserId() + " zhenXinLogin异常 errorhtml[" + errcontent + "]", e);
//            bean.setBusiErrCode(0);
//            bean.setBusiErrDesc("登录失败");
//        } finally {
//            if (httpClient != null) {
//                httpClient.close();
//            }
//            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
//        }
    }

    /**
     * 征信登出
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/zxLoginOut.go")
    public void investLoginOut(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investLoginOut(bean,request,response);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
//        CloseableHttpClient httpClient = null;
//        String url = "";
//        String errcontent = "";
//        try {
//            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
//            if (object == null) {
//                bean.setBusiErrCode(-1);
//                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
//            }
//            CookieStore cookieStore = (CookieStore) object;
//            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
//            HttpContext localContext = new BasicHttpContext();
//            // 设置请求和传输超时时间
//            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
//            localContext.setAttribute("http.cookie-store", cookieStore);
//            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
//            Map<String, String> parames = new HashMap<String, String>();
//            String userIp = InvestigationHelper.getRealIp(request).trim();
//            if (!CheckUtil.isNullString(userIp)) {
//                requestHeaderMap.put("X-Forwarded-For", userIp);
//            }
//            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
//            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
//            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/top2.do");
//            parames.clear();
//            parames.put("method", "loginOut");
//            url = "https://ipcrs.pbccrc.org.cn/login.do?num=" + Math.random();
//            HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
//            bean.setBusiErrCode(1);
//            bean.setBusiErrDesc("登出成功");
//            memCachedClient.delete(bean.getCuserId() + "zhenxinLoginCookie");
//        } catch (Exception e) {
//            logger.error(bean.getCuserId() + "zhenXinLoginOut 异常 errorhtml[" + errcontent + "]", e);
//            bean.setBusiErrCode(0);
//            bean.setBusiErrDesc("登出成功");
//        } finally {
//            if (httpClient != null) {
//                httpClient.close();
//            }
//            XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
//        }
    }
}
