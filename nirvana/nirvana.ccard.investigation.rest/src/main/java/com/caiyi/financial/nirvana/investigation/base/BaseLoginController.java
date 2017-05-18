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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by terry on 2016/10/25.
 */
public abstract class BaseLoginController extends BaseAbstractLoginContorller{

    /**
     * 获取验证码
     *
     * @param bean
     * @param request
     * @param response
     */
    public void getVerifyCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {

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
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("加载首页错误");
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
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("加载首页错误");
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
                    response.setContentType("image/jpeg");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Cache-Control", "no-cache");
                    response.setDateHeader("Expires", 0);
                    localServletOutputStream = response.getOutputStream();
                    ImageIO.write(localBufferedImage, "PNG", localServletOutputStream);
                    localServletOutputStream.flush();
                    localServletOutputStream.close();
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("加载验证码图片失败");
                if ("1".equals(bean.getIsOld())){
                    Map<String,String> map=new HashMap<>();
                    map.put("code", String.valueOf(bean.getBusiErrCode()));
                    map.put("desc", bean.getBusiErrDesc());
                    XmlUtils.writeJson(JSONObject.toJSON(map).toString(), response);
                }else{
                    XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
                }

            }
        } catch (Exception e) {
            logger.error("getZhenXinVerifyCode异常", e);
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    /**
     * 征信登出
     * @param bean
     * @param request
     * @param response
     */
    public void investLoginOut(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
            if (object == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("你未登录或者您过久没有操作导致登录已失效,请重新登录");
                return;
            }
            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            Map<String, String> parames = new HashMap<String, String>();
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/top2.do");
            parames.clear();
            parames.put("method", "loginOut");
            url = "https://ipcrs.pbccrc.org.cn/login.do?num=" + Math.random();
            HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("登出成功");
            memCachedClient.delete(bean.getCuserId() + "zhenxinLoginCookie");
        } catch (Exception e) {
            logger.error(bean.getCuserId() + "zhenXinLoginOut 异常 errorhtml[" + errcontent + "]", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("登出成功");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }











}
