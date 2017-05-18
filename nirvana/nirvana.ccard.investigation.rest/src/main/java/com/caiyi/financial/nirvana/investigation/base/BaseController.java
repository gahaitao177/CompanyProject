package com.caiyi.financial.nirvana.investigation.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.util.InvestigationHelper;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by terry on 2016/10/26.
 */
public abstract class BaseController {
    public Logger logger = LoggerFactory.getLogger(BaseController.class);
    public int res_picNums= SystemConfig.getInt("file.res_picNums");
    @Resource(name = Constant.HSK_CCARD_INVESTIGATION)
    public IDrpcClient client;
    @Autowired
    public MemCachedClient memCachedClient;




    public void getBase64Img(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        if (session.isNew()) {
            session.setMaxInactiveInterval(300);
        }
        ServletOutputStream localServletOutputStream = null;
        BufferedImage localBufferedImage = null;
        CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
        try {
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();

            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }

            String content = "";
            String url = "";

            url = "https://ipcrs.pbccrc.org.cn/";
            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/");
            url = "https://ipcrs.pbccrc.org.cn/top1.do";
            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

            url = "https://ipcrs.pbccrc.org.cn/index1.do";
            content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/top1.do");

            if ("0".equals(bean.getType())) {
                //登录
                url = "https://ipcrs.pbccrc.org.cn/login.do?method=initLogin";
                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

                url = "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp";
                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

                if (content.indexOf("org.apache.struts.taglib.html.TOKEN") == -1) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("加载首页错误");
                    return;
                }
                Document reghtml = Jsoup.parse(content);
                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();
                String date = reghtml.getElementsByAttributeValue("name", "date").get(0).val();

                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);

                String sessionID = TOKEN + "@" + method + "@" + date;
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginSession", sessionID, 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
            } else if ("1".equals(bean.getType())) {
                //注册
                url = "https://ipcrs.pbccrc.org.cn/userReg.do?method=initReg";
                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
                Document reghtml = Jsoup.parse(content);
                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();
                System.out.println(TOKEN);
                System.out.println(method);
                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/userReg.do?method=initReg");
                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);
                String sessionID = TOKEN + "@" + method;
                memCachedClient.set(bean.getCuserId() + "zhenxinVISession", sessionID, 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinRegCookie", cookieStore, 1000 * 60 * 50);

            } else if ("2".equals(bean.getType())) {
                //重置密码
                url = "https://ipcrs.pbccrc.org.cn/login.do?method=initLogin";
                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

                url = "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp";
                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

                if (content.indexOf("org.apache.struts.taglib.html.TOKEN") == -1) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("加载首页错误");
                    return;
                }
                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
                //首页
                url = "https://ipcrs.pbccrc.org.cn/resetPassword.do?method=init";
                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);

                Document reghtml = Jsoup.parse(content);
                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();

                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/resetPassword.do?method=init");
                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);

                String sessionID = TOKEN + "@" + method;
                memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdSession", sessionID, 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinResetPwdCookie", cookieStore, 1000 * 60 * 50);
            } else if ("3".equals(bean.getType())) {
                //找回用户名
                url = "https://ipcrs.pbccrc.org.cn/findLoginName.do?method=init";
                content = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
                Document reghtml = Jsoup.parse(content);
                String TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
                String method = reghtml.getElementsByAttributeValue("name", "method").get(0).val();
                System.out.println(TOKEN);
                System.out.println(method);
                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/findLoginName.do?method=init");
                url = "https://ipcrs.pbccrc.org.cn/imgrc.do?a=" + System.currentTimeMillis() + ";";
                localBufferedImage = HpClientUtil.getRandomImageOfJPEG(url, requestHeaderMap, httpClient, localContext, requestConfig);

                String sessionID = TOKEN + "@" + method;
                memCachedClient.set(bean.getCuserId() + "zhenxinFdnSession", sessionID, 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinFdnCookie", cookieStore, 1000 * 60 * 50);
            }
            if (localBufferedImage != null) {
                bean.setSign(InvestigationHelper.GetImageBase64(localBufferedImage, "jpeg"));
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("获取base64位图片码成功");
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("加载验证码图片失败");
            }
        } catch (Exception e) {
            logger.error("getBase64Img 异常",e);
        }
    }


    /**
     * 征信登录
     * @param bean
     * @param request
     */
    public void investLogin(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (CheckUtil.isNullString(bean.getLoginname()) || CheckUtil.isNullString(bean.getPassword())) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("账号或者密码不能为空！");
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
            return;
        }
        if (CheckUtil.isNullString(bean.getCode())) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("验证码不能为空！");
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
            return;
        }
        String loginname = "";
        String pwdword = "";
        if ("1".equals(bean.getClient())) {
            loginname = CaiyiEncryptIOS.dencryptStr(bean.getLoginname());
            pwdword = CaiyiEncryptIOS.dencryptStr(bean.getPassword());
        } else {
            loginname = CaiyiEncrypt.dencryptStr(bean.getLoginname());
            pwdword = CaiyiEncrypt.dencryptStr(bean.getPassword());
        }

        if (CheckUtil.isNullString(loginname) || CheckUtil.isNullString(pwdword)) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("账号或者密码加密不正确！");
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
            return;
        }

        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginSession");
            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            if (object == null || object2 == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("验证码失效,请重新刷新");
                return;
            }
            String sessionID = (String) object;
            String[] paras = sessionID.split("@");
            String TOKEN = paras[0];
            String method = paras[1];
            String date = paras[2];

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


            Map<String, String> parames = new HashMap<String, String>();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("method", method);
            parames.put("date", date);
            parames.put("loginname", loginname);
            parames.put("password", pwdword);
            parames.put("_@IMGRC@_", bean.getCode());
            url = "https://ipcrs.pbccrc.org.cn/login.do";
            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

            if (isExpired(errcontent,bean,response)) {
                return;
            }

            if (errcontent.indexOf("请输入登录名") != -1) {
                Document loginHtml = Jsoup.parse(errcontent);
                String msg = loginHtml.getElementById("_error_field_").html();
                if (CheckUtil.isNullString(msg)) {
                    org.jsoup.nodes.Element msgele = loginHtml.getElementById("_@MSG@_");
                    if (msgele != null) {
                        msg = msgele.text();
                    }
                }
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(msg);
                return;
            }

            //查看信用明细认证的页面（可判断是否有记录可查询）
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/menu.do");
            url = "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport";
            errcontent = HpClientUtil.httpGet(url, requestHeaderMap, httpClient, localContext, "gbk", false, requestConfig);
            Document checkouthtml = Jsoup.parse(errcontent);
            String radiobutton1 = checkouthtml.getElementById("radiobutton1").toString();
            String radiobutton2 = checkouthtml.getElementById("radiobutton2").toString();
            String radiobutton3 = checkouthtml.getElementById("radiobutton3").toString();
            logger.info(bean.getCuserId() + "-------------------------查看信用明细认证的页面----------------------------radiobutton1=" + radiobutton1 + " radiobutton2=" + radiobutton2 + " radiobutton3=" + radiobutton3);


            bean.setTaskId("-1");
			if (!radiobutton1.contains("disabled")) {
                bean.setTaskId(checkouthtml.getElementById("radiobutton1").val());
            }else if (!radiobutton2.contains("disabled")){
                bean.setTaskId(checkouthtml.getElementById("radiobutton2").val());
            }else if (!radiobutton3.contains("disabled")){
                bean.setTaskId(checkouthtml.getElementById("radiobutton3").val());
            }
            logger.info(bean.getCuserId()+" zhenXinLogin TaskId="+bean.getTaskId());

//			if (radiobutton1.indexOf("disabled")!=-1&&radiobutton2.indexOf("disabled")!=-1&&radiobutton3.indexOf("disabled")!=-1) {
            bean.setBusiErrCode(1);
//			}else {
//				bean.setBusiErrCode(2);
//			}
            bean.setBusiErrDesc("登录成功");
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie", cookieStore, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", loginname, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinPwdword", pwdword, 1000 * 60 * 50);
            bean.setLoginname(loginname);
            bean.setPassword(pwdword);
            //在数据库中检查是否已经存在报表
            String jsonRes = client.execute(new DrpcRequest("investLogin", "checkReportExists", bean));
            JSONObject jsonObj = JSON.parseObject(jsonRes);
            bean.setBusiErrCode(Integer.parseInt(jsonObj.get("code").toString()));
            bean.setBusiErrDesc(jsonObj.get("desc").toString());
        } catch (Exception e) {
            logger.error(bean.getCuserId() + " zhenXinLogin异常 errorhtml[" + errcontent + "]", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("登录失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    public void responseJson(Channel bean,HttpServletResponse response){
        Map<String,String> map=new HashMap<>();
        map.put("code", String.valueOf(bean.getBusiErrCode()));
        map.put("desc", bean.getBusiErrDesc());
        XmlUtils.writeJson(JSONObject.toJSON(map).toString(), response);
    }

    public boolean isExpired(String errcontent,Channel bean,HttpServletResponse response){
        if (errcontent.contains("系统已退出")) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("由于您长时间未进行任何操作，系统已退出，如需继续使用请您重新登录。 ");
            responseJson(bean, response);
            return true;
        }
        return false;
    }
}
