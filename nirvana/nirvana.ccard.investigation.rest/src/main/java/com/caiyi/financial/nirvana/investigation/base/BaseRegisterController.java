package com.caiyi.financial.nirvana.investigation.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.investigation.util.InvestigationHelper;
import com.hsk.cardUtil.HpClientUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by terry on 2016/10/24.
 */
public abstract class BaseRegisterController extends BaseAbstractLoginContorller{

    public void investCheckIdentity(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
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

            Object object =  memCachedClient.get(bean.getCuserId() + "zhenxinRegCookie");
            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinVISession");
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
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/userReg.do?method=initReg");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            Map<String, String> parames = new LinkedHashMap<String, String>();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("method", method);
            parames.put("userInfoVO.name", username);
            parames.put("userInfoVO.certType", "0");
            parames.put("userInfoVO.certNo", idcardno);
            parames.put("_@IMGRC@_", bean.getCode());
            parames.put("1", "on");
            url = "https://ipcrs.pbccrc.org.cn/userReg.do";

            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

            if (isExpired(errcontent,bean,response)) {
                return;
            }
            org.jsoup.nodes.Element reghtml = Jsoup.parse(errcontent);
            org.jsoup.nodes.Element errorhtml = reghtml.getElementById("_error_field_");
            String msg = "";
            if (errorhtml != null) {
                msg = errorhtml.text();
                if (!CheckUtil.isNullString(msg)) {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc(msg);
                    logger.info(bean.getCuserId() + "注册失败 " + msg);
                    return;
                }
            }
            if (errcontent.indexOf("请输入您的有效证件号码，英文字母区分大小写") != -1) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请输入您的有效证件号码，英文字母区分大小写");
                logger.info(bean.getCuserId() + "注册失败 请输入您的有效证件号码，英文字母区分大小写[" + errcontent + "]");
                return;
            }

            Elements methods = reghtml.getElementsByAttributeValue("name", "method");
            if (methods.size() < 0) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请输入您的有效证件号码，英文字母区分大小写");
                logger.info(bean.getCuserId() + "注册失败 请输入您的有效证件号码，英文字母区分大小写[" + errcontent + "]");
                return;
            }
            TOKEN = reghtml.getElementsByAttributeValue("name", "org.apache.struts.taglib.html.TOKEN").get(0).val();
            method = methods.get(0).val();
            if (!"saveUser".equals(method)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请输入您的有效证件号码，英文字母区分大小写");
                logger.info(bean.getCuserId() + "注册失败 请输入您的有效证件号码，英文字母区分大小写[" + errcontent + "]");
                return;
            }
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("验证有效身份证成功");
            logger.info(bean.getCuserId() + "验证有效身份证成功 username[" + username + "] idcardno["+idcardno+"]");
            sessionID = TOKEN + "@" + method;
            memCachedClient.set(bean.getCuserId() + "zhenxinRegCookie", cookieStore, 1000 * 60 * 50);
            memCachedClient.set(bean.getCuserId() + "zhenxinRegSession", sessionID, 1000 * 60 * 50);
            memCachedClient.delete(bean.getCuserId() + "zhenxinVISession");
        } catch (Exception e) {
            logger.info("zhenXinAuthenticationId  errcontent[" + errcontent + "]  ");
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("验证身份证失败,请稍后再试");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }


    /**
     * 征信注册检验用户名接口
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     */
    public void investCheckAccountUsed(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";

        try {
            if (CheckUtil.isNullString(bean.getLoginname())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("登录名不能为空");
                return;
            }
            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinRegCookie");
            if (object == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("页面已失效请重新注册");
                return;
            }
            String loginname = "";
            if ("1".equals(bean.getClient())) {
                loginname = CaiyiEncryptIOS.dencryptStr(bean.getLoginname());
            } else {
                loginname = CaiyiEncrypt.dencryptStr(bean.getLoginname());
            }
            if (CheckUtil.isNullString(loginname)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("登录名加密不正确！");
                return;
            }

            CookieStore cookieStore = (CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/userReg.do");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            Map<String, String> parames = new LinkedHashMap<String, String>();
            url = "https://ipcrs.pbccrc.org.cn/userReg.do?num=" + Math.random();
            parames.put("method", "checkRegLoginnameHasUsed");
            parames.put("loginname", loginname);

            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if (isExpired(errcontent,bean,response)) {
                return;
            }
            errcontent = errcontent.replaceAll("\n", "");
            if ("1".equals(errcontent.trim())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("用户名已存在");
                logger.info(bean.getCuserId() + " 用户名已存在[" + loginname + "]=" + errcontent);
                return;
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("用户名可用");
                memCachedClient.set(bean.getCuserId() + "zhenxinRegCookie", cookieStore, 1000 * 60 * 50);
                logger.info(bean.getCuserId() + " 用户名可用[" + loginname + "]");
            }
        } catch (Exception e) {
            logger.error((bean.getCuserId() + " zhenXinCheckRegLoginnameHasUsed errcontent[" + errcontent + "]  "), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("用户名认证失败");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

    /**
     * 征信注册接口
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investRegister(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient = null;
        String url = "";
        String errcontent = "";
        try {
            if (CheckUtil.isNullString(bean.getLoginname())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("登录名不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getPassword()) || CheckUtil.isNullString(bean.getConfirmpassword())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("登录密码或者二次密码不能为空");
                return;
            }
//            if (CheckUtil.isNullString(bean.getMailAddress())) {
//                bean.setBusiErrCode(0);
//                bean.setBusiErrDesc("邮箱地址不能为空");
//                return;
//            }
            if (CheckUtil.isNullString(bean.getMobileTel())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("手机号码不能为空");
                return;
            }
            if (CheckUtil.isNullString(bean.getCode())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("手机验证码不能为空");
                return;
            }

            Object object = memCachedClient.get(bean.getCuserId() + "zhenxinRegCookie");
            Object object2 = memCachedClient.get(bean.getCuserId() + "zhenxinRegSession");
            Object tcidobj = memCachedClient.get(bean.getCuserId() + "zhenxinRegRcid");

            if (object == null || object2 == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("页面已失效请重新注册");
                return;
            }
            if (tcidobj == null) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("请先获取短信验证码");
                return;
            }
            String loginname = "";
            String pwdword = "";
            String confirmpassword = "";
            if ("1".equals(bean.getClient())) {
                loginname = CaiyiEncryptIOS.dencryptStr(bean.getLoginname());
                pwdword = CaiyiEncryptIOS.dencryptStr(bean.getPassword());
                confirmpassword = CaiyiEncryptIOS.dencryptStr(bean.getConfirmpassword());
            } else {
                loginname = CaiyiEncrypt.dencryptStr(bean.getLoginname());
                pwdword = CaiyiEncrypt.dencryptStr(bean.getPassword());
                confirmpassword = CaiyiEncrypt.dencryptStr(bean.getConfirmpassword());
            }
            if (CheckUtil.isNullString(loginname) || CheckUtil.isNullString(pwdword) || CheckUtil.isNullString(confirmpassword)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("登录名或者密码加密不正确！");
                logger.info(bean.getCuserId() + " 加密参数不正确 " + bean.getLoginname() + " " + bean.getPassword() + " " + bean.getConfirmpassword());
                return;
            }


            String tcid = (String) tcidobj;
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
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/userReg.do?method=initReg");
            String userIp = InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            Map<String, String> parames = new LinkedHashMap<String, String>();
            parames.put("org.apache.struts.taglib.html.TOKEN", TOKEN);
            parames.put("method", method);
            parames.put("tcId", tcid);
            parames.put("userInfoVO.loginName", loginname);
            parames.put("userInfoVO.password", pwdword);
            parames.put("userInfoVO.confirmpassword", confirmpassword);
            parames.put("userInfoVO.email", bean.getMailAddress());
            parames.put("userInfoVO.mobileTel", bean.getMobileTel());
            parames.put("userInfoVO.verifyCode", bean.getCode());
            parames.put("userInfoVO.smsrcvtimeflag","2");
            parames.put("counttime", "30");


            requestHeaderMap.remove("X-Requested-With");
            requestHeaderMap.remove("Content-Type");
            url = "https://ipcrs.pbccrc.org.cn/userReg.do";
//            errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            errcontent =httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            if (errcontent.contains("https://ipcrs.pbccrc.org.cn/page/common/error1.jsp")){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("系统繁忙,请稍后再试");
                return;
            }
            if (errcontent.contains("302#")||errcontent.contains("301#")){
                logger.info(bean.getCuserId() +"302 301==="+errcontent);
                url=errcontent.replace("302#","").replace("301#","");
                logger.info(bean.getCuserId() +"302 301 url==="+url);
                parames.clear();
                errcontent =httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
            }
            if (errcontent.indexOf("您在个人信用信息平台已注册成功") != -1) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("您在个人信用信息平台已注册成功");
                bean.setLoginname(loginname);
                bean.setPassword(pwdword);
                try{
                    String jsonRes = client.execute(new DrpcRequest("registerBolt", "saveLoginAccount", bean));
                    JSONObject jsonObj = JSON.parseObject(jsonRes);
                    String code=jsonObj.getString("code");
                    String desc=jsonObj.getString("desc");
                    if (code.equals("1")){
                        logger.info("保存用户征信账号成功 loginname="+loginname+" pwd="+bean.getPassword()+" client="+bean.getClient()+" desc="+desc);
                    }else{
                        logger.info("保存用户征信账号失败 loginname="+loginname+" pwd="+bean.getPassword()+" client="+bean.getClient()+" desc="+desc);
                    }
                }catch (Exception e){
                    logger.error(bean.getCuserId()+" registerBolt --saveLoginAccount 异常",e);
                }
                memCachedClient.delete(bean.getCuserId() + "zhenxinRegCookie");
                memCachedClient.delete(bean.getCuserId() + "zhenxinRegSession");
                memCachedClient.delete(bean.getCuserId() + "zhenxinRegRcid");
            } else {
                org.jsoup.nodes.Element reghtml = Jsoup.parse(errcontent);
                org.jsoup.nodes.Element errorhtml = reghtml.getElementById("_error_field_");
                String msg = "";
                if (errorhtml != null) {
                    msg = errorhtml.text();
                    if (!CheckUtil.isNullString(msg)) {
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc(msg);
                        logger.info(bean.getCuserId() + "注册失败 " + msg);
                    }
                } else {
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("注册失败");
                    logger.info(bean.getCuserId() + " zhenXinRegistered 注册失败 TOKEN[" + TOKEN + "] loginname[" + loginname + "] " +
                            " pwdword[" + pwdword + "] confirmpassword[" + confirmpassword + "] email[" + bean.getMailAddress() + "]" +
                            " mobile[" + bean.getMobileTel() + "] verifyCode[" + bean.getCode() + "] errcontent " + errcontent);
                    logger.info(bean.getCuserId()+" parames json "+JSONObject.toJSON(parames));

                }
            }
        } catch (Exception e) {
            logger.error((bean.getCuserId() + " zhenXinRegistered errcontent[" + errcontent + "]  "), e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("注册失败,请稍后再试");
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }








    public static String httpPost(String url, Map<String, String> headers, Map<String, String> parames, HttpClient httpclient, HttpContext localContext, String encode, RequestConfig requestConfig) {
        String context = "";
        HttpPost httpPost = null;
        InputStream in = null;

        try {
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if(headers != null) {
                Iterator e = headers.keySet().iterator();

                while(e.hasNext()) {
                    String entity = (String)e.next();
                    httpPost.setHeader(entity, (String)headers.get(entity));
                }
            }

            if(parames != null) {
                ArrayList e1 = new ArrayList();
                Iterator entity1 = parames.entrySet().iterator();

                while(entity1.hasNext()) {
                    Map.Entry statusCode = (Map.Entry)entity1.next();
                    e1.add(new BasicNameValuePair((String)statusCode.getKey(), (String)statusCode.getValue()));
                }

                UrlEncodedFormEntity statusCode1 = new UrlEncodedFormEntity(e1, encode);
                httpPost.setEntity(statusCode1);
            }

            HttpResponse e2 = httpclient.execute(httpPost, localContext);
            HttpEntity entity2 = e2.getEntity();
            String statusCode2 = e2.getStatusLine().toString();
            int scode=e2.getStatusLine().getStatusCode();
            if(scode!=200 && scode!=201) {
                if (scode==301||scode==302){
                    context=scode+"#"+e2.getFirstHeader("Location").getValue();
                }
                System.out.println(context);
            } else {
                StringBuffer buffer = new StringBuffer();
                in = entity2.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));

                String temp;
                while((temp = br.readLine()) != null) {
                    buffer.append(temp);
                    buffer.append("\n");
                }

                context = buffer.toString();
                in.close();
            }
        } catch (Exception var19) {
            var19.printStackTrace();
        } finally {
            if(httpPost != null) {
                httpPost.abort();
            }

        }

        return context;
    }





    /**
     * 征信注册获取手机动态码
     *
     * @param bean
     * @param request
     * @param response
     */
    public void investGetAcvitaveCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        CloseableHttpClient httpClient=null;
        String url="";
        String errcontent="";

        try {
            if (CheckUtil.isNullString(bean.getMobileTel())) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("手机号码不能为空");
                return;
            }
            Object object=memCachedClient.get(bean.getCuserId()+"zhenxinRegCookie");
            if (object==null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("页面已失效请重新注册");
                return;
            }

            CookieStore cookieStore=(CookieStore) object;
            httpClient = HpClientUtil.getHttpsClient();//HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
            localContext.setAttribute("http.cookie-store", cookieStore);
            Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
            requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
            requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/userReg.do");
            requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            String userIp=InvestigationHelper.getRealIp(request).trim();
            if (!CheckUtil.isNullString(userIp)) {
                requestHeaderMap.put("X-Forwarded-For", userIp);
            }
            Map<String, String> parames=new LinkedHashMap<String, String>();
            url="https://ipcrs.pbccrc.org.cn/userReg.do";
            parames.clear();
            parames.put("method", "getAcvitaveCode");
            parames.put("mobileTel", bean.getMobileTel());
            errcontent=HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);

            if (isExpired(errcontent,bean,response)) {
                return;
            }
            String tcId=errcontent.trim();

            if (!CheckUtil.isNullString(tcId)) {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("认证码已发送");
                logger.info(bean.getCuserId()+ "认证码已发送 ["+tcId+"]  ");
                memCachedClient.set(bean.getCuserId()+"zhenxinRegCookie", cookieStore , 1000*60*50);
                memCachedClient.set(bean.getCuserId()+"zhenxinRegRcid", tcId , 1000*60*50);
            }else {
                logger.info(bean.getMobileTel() + "认证码已发送 [" + tcId + "]  ");
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("认证码发送失败");
            }
        } catch (Exception e) {
            logger.error((bean.getCuserId()+ " zhenXinGetAcvitaveCode errcontent["+errcontent+"]  "),e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("认证码发送失败");
        }finally{
            if (httpClient!=null) {
                httpClient.close();
            }
            //XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
        }
    }

}
