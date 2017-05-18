package com.caiyi.financial.nirvana.bill.bank;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.ForeheadRecord;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.utils.SpringContextUtilBro;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.CookieUtil;
import com.hsk.cardUtil.HpClientUtil;
import com.hsk.cardUtil.HttpRequester;
import com.hsk.cardUtil.HttpRespons;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import sun.misc.BASE64Encoder;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by terry on 2016/7/4.
 */
public class ZhaoShangBank extends AbstractHttpService {


    public String[] getIpPort() {
        if (proxyips != null && proxyips.length > 0) {
            List<String> proxyList = new ArrayList<>();
            proxyList.addAll(Arrays.asList(proxyips));
            String ip = proxyips[new Random().nextInt(proxyips.length)];
            String[] ipport = {ip.split(":")[0],ip.split(":")[1]};
            return ipport;
        }
        return null;
    }


    /***
     * 获取招商银行Base64位图片验证码字符串
     * @param bean
     * @return Base64位图片验证码字符串
     * @throws IOException
     */
    public String setYzm(Channel bean, MemCachedClient cc){
        String base64Img = null;
        try {

            String []ipport = getIpPort();
            if(ipport == null){
                logger.error("招商代理ip获取失败!");
            }
            String ip =  ipport[0];
            int choiseport = Integer.parseInt(ipport[1]);
            HttpHost proxy = new HttpHost(ip, choiseport, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient hc = HttpClients.custom().setRoutePlanner(routePlanner).build();

            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String, String> headers = new HashMap<>();
            headers.put("Host", "html.m.cmbchina.com");
            headers.put("Accept", "image/webp,image/*,*/*;q=0.8");

            Map<String, String> requestHeaderMap = new HashMap<String, String>();
            if (!CheckUtil.isNullString(ip)){
                headers.put("X-Forwarded-For", ip);
                logger.info(bean.getCuserId() + " zhaoshang-IP X-Forwarded-For=" + bean.getIpAddr());
            }
            String url= "https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx";
            //获取招商银行的登录sessionID
            String content = HpClientUtil.httpGet(url, requestHeaderMap, hc, localContext, "utf-8",false, requestConfig);
            String newcontent = content.substring(content.indexOf("https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx"), content.indexOf("function DoLogin()")).trim();
            newcontent = newcontent.split(",")[1];
            String sessionID = newcontent.substring(newcontent.indexOf("\"") + 1, newcontent.lastIndexOf("\""));
            //验证码地址
            String vcodeUrl = "https://html.m.cmbchina.com/MobileHtml/Login/ExtraPwd.aspx?ClientNo=" + sessionID;
            base64Img = getRandomImageOfJPEG(vcodeUrl, requestHeaderMap, hc, localContext, requestConfig);

            cc.set(bean.getCuserId() + bean.getBankId() + "bankSession", sessionID);
            cc.set(bean.getCuserId() + bean.getBankId() + "choiseip", ip,3600000);
            cc.set(bean.getCuserId() + bean.getBankId() + "choiseport", choiseport,3600000);
            System.out.print("----" + cc.get(bean.getCuserId() + bean.getBankId() + "bankSession"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return base64Img;
    }

    /***
     * 获取招商银行图片验证码
     * @param bean
     * @return BufferedImage 验证码对象
     * @throws IOException
     */
    public static BufferedImage getZSVcode(Channel bean, Logger logger) throws IOException {
        BufferedImage localBufferedImage = null;
        try {
            MemCachedClient cc = SpringContextUtilBro.getBean(MemCachedClient.class);
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
            System.out.print("----" + cc.get(bean.getCuserId() + bean.getBankId() + "bankSession"));
        } catch (Exception e) {
            logger.error(bean.getCuserId() + " getZSVcode ", e);
        }

        return localBufferedImage;
    }

    /***
     * 获取招商银行提额登录
     * @param bean
     * @param cc
     */
    public int taskReceve_te(Channel bean, MemCachedClient cc) {
        try {
            int ret = dencrypt_data(bean);//参数解密
            if (ret == 0) {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("服务器繁忙！");
                return ret;
            }
            int errCode = login(bean, cc);
            logger.info("-----------errCode=" + errCode + "----------------");
            if (errCode == 1) {
                bean.setBusiErrCode(BillConstant.needmsg_te);//需要短信验证码
                bean.setBusiErrDesc("登录成功，提额需要短信验证");
                bean.setPhoneCode("true");
            }else if (errCode == 2) {
                bean.setBusiErrCode(BillConstant.needmsg);//登陆成功
                bean.setBusiErrDesc("登录成功！");
                return 1;
            }else  if (errCode == 3) {
                bean.setBusiErrCode(BillConstant.needimg);//需要图片
                bean.setBusiErrDesc("图片验证码错误！");
            }else {
                bean.setBusiErrCode(BillConstant.fail);
            }
        } catch (Exception e) {
            logger.error("cuserId:" + bean.getCuserId() + getClass().getSimpleName() + " ---", e);
        }
        return 0;
    }

    /***
     * 获取招商银行登录
     * @param bean
     * @param cc
     */
    public int taskReceve(Channel bean, IDrpcClient client, MemCachedClient cc){
        try {
            int ret = dencrypt_data(bean);//参数解密
            if (ret == 0) {
                return ret;
            }
            String userIp = bean.getIpAddr();
            if(userIp!= null && !userIp.equals("")){
                cc.set(bean.getCuserId() + bean.getBankId() + "userIp",userIp);
            }
            //0发生异常 1登陆需要短信验证码 2直接登陆成功 3图片验证码错误重新输入
            int errCode = login(bean, cc);
            logger.info("-----------errCode=" + errCode + "----------------");
            if (errCode == 1) {
                bean.setBusiErrCode(BillConstant.needmsg);//需要短信验证码
                bean.setBusiErrDesc("需要短信验证");
                bean.setPhoneCode("true");
            } else  if (errCode == 2) {
                bean.setBusiErrCode(BillConstant.success);//登陆成功
                bean.setBusiErrDesc("登录成功！");
                return 1;
            }else  if (errCode == 3) {
                bean.setBusiErrCode(BillConstant.needimg);//需要图片
                bean.setBusiErrDesc("图片验证码错误！");
            }else {
                bean.setBusiErrCode(BillConstant.fail);
            }
            return 0;
        } catch (Exception e) {
            logger.error("cuserId:" + bean.getCuserId() + getClass().getSimpleName() + " ---", e);
        }
        return 0;

    }

    public int recognize_ZhaoShang_zd(Channel bean, MemCachedClient cc) {
        try {
            String sessionID = null;
            String idcard = bean.getDencryIdcard();
            String bankpwd = bean.getDencryBankPwd();

            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String, String> requestHeaderMap = new HashMap<>();
            requestHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
            requestHeaderMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1");
            requestHeaderMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            requestHeaderMap.put("Connection", "Keep-Alive");
            requestHeaderMap.put("Host", "html.m.cmbchina.com");
            Map<String, String> parames = new HashMap<String, String>();

            //招商登录
            for (int i = 0; i < yzNum; i++) {

                String []ipport = getIpPort();
                if(ipport == null){
                    logger.error(bean.getCuserId()+"招商代理ip获取失败!");
                }
                String ip =  ipport[0];
                int choiseport = Integer.parseInt(ipport[1]);
                HttpHost proxy = new HttpHost(ip, choiseport, "http");
                DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                CloseableHttpClient hc = HttpClients.custom().setRoutePlanner(routePlanner).build();

                String url="https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx";
                String content =HpClientUtil.httpGet(url, requestHeaderMap, hc, localContext, "utf-8",false, requestConfig);

                String newcontent = content.substring(content.indexOf("https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx"),content.indexOf("function DoLogin()")).trim();
                if (CheckUtil.isNullString(newcontent)){
                    logger.info(bean.getCuserId()+" 代理请求异常 ip ="+ip+" choiseport="+choiseport);
                    i--;
                    continue;
                }


                newcontent = newcontent.split(",")[1];
                sessionID = newcontent.substring(newcontent.indexOf("\"") + 1, newcontent.lastIndexOf("\""));
                //验证码地址
                String vcodeUrl = "https://html.m.cmbchina.com/MobileHtml/Login/ExtraPwd.aspx?ClientNo=" + sessionID;
                String base64Img =getRandomImageOfJPEG(vcodeUrl, requestHeaderMap, hc, localContext, requestConfig);
                url = enUrl + "?captcha=" + java.net.URLEncoder.encode(base64Img, "utf-8") + "&bankid=21&imgtype=3";
                //获取招商银行的登录sessionID
                HttpRequester httprequest = new HttpRequester();
                HttpRespons hr = httprequest.sendGet(url);
                content = hr.getContent();
                logger.info(bean.getCuserId() + " 自动识别验证码 " + content);
                JSONObject json = null;
                if (!CheckUtil.isNullString(content)) {
                    json = JSONObject.parseObject(content);
                } else {
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("自动识别验证码失败！");
                    continue;
                }

                String code = String.valueOf(json.get("code"));
                if (!"0".equals(code)) {
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("自动识别验证码失败！");
                    continue;
                }
                String bankrand = json.getString("text");

                parames.put("Command", "CMD_DOLOGIN");
                parames.put("ClientNo", sessionID);
                logger.info("banksessionid=" + sessionID);
                parames.put("XmlReq", "<PwdC>"+bankpwd+"</PwdC><ExtraPwdC>"+bankrand+"</ExtraPwdC><LoginMode>0</LoginMode><LoginByCook>false</LoginByCook><IDTypeC>01</IDTypeC><IDNoC>"+idcard+"</IDNoC><RememberFlag>true</RememberFlag><UserAgent>Opera/9.80 (Windows NT 6.1; WOW64; Opera Mobi/46154) Presto/2.11.355 Version/12.10</UserAgent><screenW>480</screenW><screenH>800</screenH><OS>Windows</OS>");
                logger.info(bean.getCuserId()+" 模拟银行登录开始............");
                url = "https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx";
                String rhtml = httpPost(url, requestHeaderMap, parames, hc, localContext, "UTF-8", requestConfig);

                if(rhtml.contains("请选择一个手机号码，并点击“发送验证码”按钮来发送验证码")){  //登录时需要短信验证码
                    cc.set(bean.getCuserId() + bean.getBankId() + "bankSession", sessionID,3600000);
                    cc.set(bean.getCuserId() + bean.getBankId() + "choiseip", ip,3600000);
                    cc.set(bean.getCuserId() + bean.getBankId() + "choiseport", choiseport,3600000);
                    return 1;
                }else {     //异常情况
                    if (rhtml.indexOf("errMsg =") != -1) {
                        rhtml = rhtml.substring(rhtml.indexOf("errMsg ="), rhtml.indexOf("typeof errMsg"));
                        String errMsg = rhtml.substring(rhtml.indexOf("\"") + 1, rhtml.lastIndexOf("\""));
                        logger.info("idcard=" + idcard + "获取账单失败，失败原因[登录失败-" + errMsg + "]");

                        if (errMsg.contains("无效查询密码") || errMsg.contains("您的查询密码输入错误，请重新输入")) {
                            bean.setBusiErrDesc("您的查询密码输入错误，请重新输入");
                            return 0;
                        }else if(errMsg.contains("您的查询密码输入错误次数已满，请点击“信用卡助手-密码重置”或“业务助手-信用卡重置密码”重新设置查询密码，感谢您的配合")){
                            bean.setBusiErrDesc("您的查询密码输入错误次数已满.");
                            return 0;
                        }
                    }else if (rhtml.indexOf("您设置的密码过于简单，为了您的帐户安全，建议您尽快修改") != -1) {
                        bean.setBusiErrDesc("解析失败，您设置的密码过于简单，为了您的帐户安全，建议您尽快修改！");
                        return 0;
                    } else if (rhtml.indexOf("亲爱的准信用卡客户，请您登录掌上生活客户端进行相关业务的查询与办理") != -1) {
                        bean.setBusiErrDesc("亲爱的准信用卡客户，请您登录掌上生活客户端进行相关业务的查询与办理");
                        return 0;
                    }else if (rhtml.indexOf("为了您的帐户安全，设置密码时请不要选用您的身份证、生日、手机号码、卡号、重复或连续等简单的数字。") != -1) {
                        bean.setBusiErrDesc("设置密码时请不要选用您的身份证、生日、手机号码、卡号、重复或连续等简单的数字");
                        return 0;
                    }else if (rhtml.indexOf("信用卡开卡及设定密码") != -1) {
                        bean.setBusiErrDesc("您的信用卡还未激活或者未设定密码！");
                        return 0;
                    }

                    if(i == yzNum){
                        String errMsg = "";
                        if (rhtml.indexOf("errMsg =") != -1) {
                            rhtml = rhtml.substring(rhtml.indexOf("errMsg ="), rhtml.indexOf("typeof errMsg"));
                            errMsg = rhtml.substring(rhtml.indexOf("\"") + 1, rhtml.lastIndexOf("\""));
                            logger.info("idcard=" + idcard + "获取账单失败，失败原因[登录失败-" + errMsg + "]");
                            if (errMsg.indexOf("无效登录请求") != -1) {
                                bean.setBusiErrDesc("无效登录请求,请重新刷新验证码！");
                                return 0;
                            } else if (errMsg.indexOf("无效附加码") != -1) {
                                bean.setBusiErrDesc("验证码错误,请重新输入！");
                                return 3;
                            } else {
                                bean.setBusiErrDesc(errMsg);
                                return 0;
                            }
                        }else if (rhtml.indexOf("频繁操作") != -1) {
                            bean.setBusiErrDesc("您的操作过于频繁，请稍后再试！");
                            return 0;
                        } else {
                            bean.setBusiErrDesc("解析失败，请重试！");
                            logger.info(bean.getCuserId()+" idcard=" + idcard + "获取账单失败，未获取到失败原因[" + rhtml + "]");
                            return 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("登录失败！请重试");
            logger.error(bean.getCuserId()+" idcard=" + bean.getCardNo() + "登录失败！请重试", e);
        }
        return 0;
    }


    public static String getRandomImageOfJPEG(String url, Map<String, String> headers, HttpClient httpclientme, HttpContext localContext, RequestConfig requestConfig) {
        HttpGet httpget = null;
        InputStream in = null;

        try {
            httpget = new HttpGet(url);
            httpget.setConfig(requestConfig);
            if(headers != null) {
                Iterator e = headers.keySet().iterator();

                while(e.hasNext()) {
                    String entity = (String)e.next();
                    httpget.setHeader(entity, (String)headers.get(entity));
                }
            }

            HttpResponse e1 = httpclientme.execute(httpget, localContext);
            HttpEntity entity1 = e1.getEntity();
            String statusCode = e1.getStatusLine().toString();
            if("HTTP/1.1 200 OK".equals(statusCode) || "HTTP/1.0 200 OK".equals(statusCode)) {
                in = entity1.getContent();
                byte[] var29 = new byte[in.available()];
                in.read(var29);
                in.close();
                BASE64Encoder var30 = new BASE64Encoder();
                String var14 = var30.encode(var29);

                return var14;
            }
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            if(httpget != null) {
                httpget.abort();
            }

        }

        return null;
    }

    public int recognize_ZhaoShang_sd(Channel bean, MemCachedClient cc) {
        String bankrand = bean.getBankRand();
        try {
            String bankSession = (String) cc.get(bean.getCuserId() + bean.getBankId() + "bankSession");
            String choiseip = (String) cc.get(bean.getCuserId() + bean.getBankId() + "choiseip");
            int choiseport = (int) cc.get(bean.getCuserId() + bean.getBankId() + "choiseport");
            if (bankSession ==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            String idcard = bean.getDencryIdcard();
            String bankpwd = bean.getDencryBankPwd();

            HttpHost proxy = new HttpHost(choiseip, choiseport, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient hc = HttpClients.custom().setRoutePlanner(routePlanner).build();

            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String, String> headers = new HashMap<>();

            String errMsg ="";
            headers.put("Host", "html.m.cmbchina.com");
            headers.put("Accept", "image/webp,image/*,*/*;q=0.8");
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                headers.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            //招商登录
            Map<String, String> parames = new HashMap<String, String>();
            parames.put("Command", "CMD_DOLOGIN");
            parames.put("ClientNo", bankSession);
            logger.info("banksessionid=" + bankSession);
            parames.put("XmlReq", "<PwdC>" + bankpwd + "</PwdC><ExtraPwdC>" + bankrand + "</ExtraPwdC><LoginMode>0</LoginMode><LoginByCook>false</LoginByCook><IDTypeC>01</IDTypeC><IDNoC>" + idcard + "</IDNoC><RememberFlag>true</RememberFlag><UserAgent>Opera/9.80 (Windows NT 6.1; WOW64; Opera Mobi/46154) Presto/2.11.355 Version/12.10</UserAgent><screenW>480</screenW><screenH>800</screenH><OS>Windows</OS>");
            logger.info("模拟银行登录开始............");
            String url = "https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx";
            String rhtml = httpPost(url, headers, parames, hc, localContext, "UTF-8", requestConfig);

            if(rhtml.contains("请选择一个手机号码，并点击“发送验证码”按钮来发送验证码")){  //登录时需要短信验证码
                return 1;
            }else {     //其它异常情况
                if (rhtml.indexOf("errMsg =") != -1) {
                    rhtml = rhtml.substring(rhtml.indexOf("errMsg ="), rhtml.indexOf("typeof errMsg"));
                    errMsg = rhtml.substring(rhtml.indexOf("\"") + 1, rhtml.lastIndexOf("\""));
                    logger.info("idcard=" + idcard + "获取账单失败，失败原因[登录失败-" + errMsg + "]");
                    if (errMsg.contains("无效查询密码") || errMsg.contains("您的查询密码输入错误，请重新输入")) {
                        bean.setBusiErrDesc("您的查询密码输入错误，请重新输入");
                        return 0;
                    }else if(errMsg.contains("您的查询密码输入错误次数已满，请点击“信用卡助手-密码重置”或“业务助手-信用卡重置密码”重新设置查询密码，感谢您的配合")){
                        bean.setBusiErrDesc("您的查询密码输入错误次数已满.");
                        return 0;
                    }

                    if (errMsg.indexOf("无效登录请求") != -1) {
                        bean.setBusiErrDesc("无效登录请求,请重新刷新验证码！");
                        return 0;
                    } else if (errMsg.indexOf("无效附加码") != -1) {
                        bean.setBusiErrDesc("验证码错误,请重新输入！");
                        return 3;
                    } else {
                        bean.setBusiErrDesc(errMsg);
                        return 0;
                    }
                }else if (rhtml.indexOf("信用卡开卡及设定密码") != -1) {
                    bean.setBusiErrDesc("您的信用卡还未激活或者未设定密码！");
                    return 0;
                } else if (rhtml.indexOf("频繁操作") != -1) {
                    bean.setBusiErrDesc("您的操作过于频繁，请稍后再试！");
                    return 0;
                } else if (rhtml.indexOf("您设置的密码过于简单，为了您的帐户安全，建议您尽快修改") != -1) {
                    bean.setBusiErrDesc("解析失败，您设置的密码过于简单，为了您的帐户安全，建议您尽快修改！");
                    return 0;
                } else if (rhtml.indexOf("亲爱的准信用卡客户，请您登录掌上生活客户端进行相关业务的查询与办理") != -1) {
                    bean.setBusiErrDesc("亲爱的准信用卡客户，请您登录掌上生活客户端进行相关业务的查询与办理");
                    return 0;
                } else {
                    bean.setBusiErrDesc("解析失败，请重试！");
                    logger.info("idcard=" + idcard + "获取账单失败，未获取到失败原因[" + rhtml + "]");
                    return 0;
                }
            }

        } catch (Exception e) {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("自动识别验证码失败！");
            logger.error("idcard=" + bean.getCardNo() + "自动识别验证码失败", e);
        }
        return 0;
    }

    /***
     * 获取招商银行提额登录
     * @param bean
     * @param cc
     */
    public int login(Channel bean, MemCachedClient cc) {
        //登录分为传图片验证码和自动解析验证码
        String bankrand = bean.getBankRand();
        if(bankrand == null || bankrand.equals("")){    //自动解析验证码
            return recognize_ZhaoShang_zd(bean,cc);
        }else {     //用户输入图片验证码
            return recognize_ZhaoShang_sd(bean,cc);
        }
    }

    /**
     * 登录发送短信验证码
     * @param cc
     * @param bean
     */
    public int getSms(Channel bean, MemCachedClient cc) {
        try {
            String bankSession = (String) cc.get(bean.getCuserId() + bean.getBankId() + "bankSession");
            String choiseip = (String) cc.get(bean.getCuserId() + bean.getBankId() + "choiseip");
            int choiseport = (int) cc.get(bean.getCuserId() + bean.getBankId() + "choiseport");
            if (bankSession == null || choiseip == null) {
                logger.info(bean.getCuserId() + bean.getBankId() + "bankSession" + "=" + bankSession +" choiseip:"+choiseip);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
                return 0;
            }

            HttpHost proxy = new HttpHost(choiseip, choiseport, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient hc = HttpClients.custom().setRoutePlanner(routePlanner).build();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String,String> headers = new HashMap<>();
            headers.put("Accept", "*/*");
            headers.put("Referer", "https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx");
            headers.put("Host","html.m.cmbchina.com");
            headers.put("User-Agent","Opera/9.80 (Windows NT 6.1; WOW64; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
            String userIp = choiseip;

            if (!StringUtils.isEmpty(userIp)) {
                headers.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            String url = "https://html.m.cmbchina.com/MobileHtml/Login/MsgVerify.aspx";

            Map<String, String> parames = new HashMap<String, String>();
            parames.put("$RequestMode$", "1");
            parames.put("MsgVerifyCmd", "Ajax_MSG_SEND");
            parames.put("MsgFlag_", "010000");
            parames.put("MsgCode", "");
           // parames.put("MsgTips_", "请选择一个手机号码，并点击“发送短信验证码”按钮来发送验证码。如果你已经取得验证码，请直接在下面的文本框中输入即可。");
            parames.put("ddlMobile", "1");
            parames.put("ClientNo", bankSession);
            parames.put("Command", "");

            logger.info("banksessionid=" + bankSession);
            String rhtml = httpPost(url, headers, parames, hc, localContext, "UTF-8", requestConfig);
            if(rhtml.contains("短信验证码已发送至手机：")){
                org.json.JSONObject jsonObject = new org.json.JSONObject(rhtml);
                String dispMsg = jsonObject.get("$SysResult$").toString();
                try {
                    dispMsg =  dispMsg.substring(dispMsg.indexOf("短信验证码已发送至手机"),dispMsg.lastIndexOf("。")+1);
                }catch (Exception e){
                    dispMsg = "短信验证码发送成功！";
                }
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc(dispMsg);
                return 1;
            }else {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("短信验证码发送失败！");
                return 0;
            }

        } catch (Exception e) {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("自动识别验证码失败！");
            logger.error("idcard=" + bean.getCardNo() + "自动识别验证码失败", e);
        }
        return 0;
    }

    /**
     * 提额发送短信验证码
     * @param cc
     * @param bean
     */
    public int getSmste(Channel bean, MemCachedClient cc) {
        String banksessionid = "";
        try {
            String bankSession = (String) cc.get(bean.getCuserId() + bean.getBankId() + "bankSession");
            String choiseip = (String) cc.get(bean.getCuserId() + bean.getBankId() + "choiseip");
            int choiseport = (int) cc.get(bean.getCuserId() + bean.getBankId() + "choiseport");
            if (bankSession == null || choiseip == null) {
                logger.info(bean.getCuserId() + bean.getBankId() + "bankSession" + "=" + bankSession +" choiseip:"+choiseip);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
                return 0;
            }

            HttpHost proxy = new HttpHost(choiseip, choiseport, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient hc = HttpClients.custom().setRoutePlanner(routePlanner).build();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String,String> headers = new HashMap<>();
            headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
            headers.put("Referer", "https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx");
            headers.put("Host","html.m.cmbchina.com");
            headers.put("User-Agent","Opera/9.80 (Windows NT 6.1; WOW64; Opera Mobi/46154) Presto/2.11.355 Version/12.10");

            //额度提升详情页面  0能提升临时额度 1不能   0能提升固定额度 1不能
            String urlstr = "https://html.m.cmbchina.com/MobileHtml/creditcard/m_limit/lm_limitmanagegen.aspx?ClientNo=" + banksessionid + "&Command=&XmlReq=";
            String errorContent = HpClientUtil.httpGet(urlstr, headers, hc, localContext, "utf-8",false, requestConfig);
            Document limitmDoc = null;
            StringBuilder url = new StringBuilder();
            String type = bean.getType();
            if (type.equals("2")) {  //固额
                //固定额度调整
                String teStr = errorContent.substring(errorContent.indexOf("function GotoTempChange()"), errorContent.indexOf("function GotoFixUp()"));
                String CreditAccNo = teStr.substring(teStr.indexOf("CreditAccNo") + 15, teStr.lastIndexOf("submitControl.addFieldByNameValue") - 16);
                String rtnURL = teStr.substring(teStr.indexOf("rtnURL") + 10, teStr.lastIndexOf("showWaitingPage") - 16);

                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_FixLimitManage.aspx?CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&rtnURL=");
                url.append(rtnURL);
                url.append("&Command=&DeviceTAB=TabDefault");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);

                //固定额度短信发送
                String MsgFlag_ = "";
                String SetLimit = bean.getUptomoney(); //申请额度
                String CaseNo = "";
                String ZXCode = "";
                limitmDoc = Jsoup.parse(errorContent);
                MsgFlag_ = limitmDoc.getElementById("cphBody_HtmlMsgVerify_MsgFlag_").val();
                String tgeStr = errorContent.substring(errorContent.indexOf("function OnMsgVerfyControlSubmit"), errorContent.indexOf("function changeAmount"));
                CaseNo = tgeStr.substring(tgeStr.indexOf("CaseNo") + 10, tgeStr.indexOf("ZXCode") - 55);
                ZXCode = tgeStr.substring(tgeStr.indexOf("ZXCode") + 10, tgeStr.indexOf("JumpFlag") - 55);


                url.setLength(0);
                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_FixLimitManage.aspx?$RequestMode$=1&MsgVerifyCmd=Ajax_MSG_SEND&MsgFlag_=");
                url.append(MsgFlag_);
                url.append("&MsgCode=");
                url.append("&MsgTips_=请选择一个手机号码，并点击“发送短信验证码”按钮来发送验证码。如果你已经取得验证码，请直接在下面的文本框中输入即可。");
                url.append("&ddlMobile=1");
                url.append("&CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&CaseNo=");
                url.append(CaseNo);
                url.append("&ZXCode=");
                url.append(ZXCode);
                url.append("&JumpFlag=Y");
                url.append("&SetLimit=");
                url.append(SetLimit);
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&Command=");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);

            } else if (type.equals("3")) {  //临时额度验证码发送
                //临时额度调整
                String teStr = errorContent.substring(errorContent.indexOf("function GotoTempChange()"), errorContent.indexOf("function GotoFixUp()"));
                String CreditAccNo = teStr.substring(teStr.indexOf("CreditAccNo") + 15, teStr.lastIndexOf("submitControl.addFieldByNameValue") - 16);
                String rtnURL = teStr.substring(teStr.indexOf("rtnURL") + 10, teStr.lastIndexOf("showWaitingPage") - 16);
                url.setLength(0);
                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_TempLimitManage.aspx?CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&rtnURL=");
                url.append(rtnURL);
                url.append("&Command=&DeviceTAB=TabDefault");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);
                //临时额度短信发送
                String MsgFlag_ = "";
                String SetLimit = "";
                String BeginDate = "";
                String EndDate = "";
                limitmDoc = Jsoup.parse(errorContent);
                if(errorContent.contains("您此次申请审批未通过")){
                    bean.setBusiErrDesc("您此次申请审批未通过，无法调整账户临时额度。");
                    return 0;
                }
                MsgFlag_ = limitmDoc.getElementById("cphBody_HtmlMsgVerify_MsgFlag_").val();
                if (limitmDoc.getElementById("cphBody_lbCurrentLimit") == null) {
                    SetLimit = limitmDoc.getElementById("cphBody_lbCreditLimit").text();
                } else {
                    SetLimit = limitmDoc.getElementById("cphBody_lbCurrentLimit").text();
                }

                SetLimit = SetLimit.substring(0, SetLimit.indexOf(".")).replaceAll("[^0-9.]", "");
                BeginDate = limitmDoc.getElementById("cphBody_inputWantedBegin").val();
                EndDate = limitmDoc.getElementById("cphBody_inputWantedEnd").val();
                url.setLength(0);
                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_TempLimitManage.aspx?$RequestMode$=1&MsgVerifyCmd=Ajax_MSG_SEND&MsgFlag_=");
                url.append(MsgFlag_);
                url.append("&MsgCode=");
                url.append("&MsgTips_=请选择一个手机号码，并点击“发送短信验证码”按钮来发送验证码。如果你已经取得验证码，请直接在下面的文本框中输入即可。");
                url.append("&ddlMobile=1");
                url.append("&CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&JumpFlag=Y");
                url.append("&SetLimit=");
                url.append(SetLimit);
                url.append("&BeginDate=");
                url.append(BeginDate);
                url.append("&EndDate=");
                url.append(EndDate);
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&Command=");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);

            }
            //判断短信是否发送成功
            if (errorContent != null && errorContent.contains("短信验证码已发送至手机")) {
                logger.info("cuserId" + bean.getCuserId() + "招商提额短信验证码发送成功>>>>>>>>>>>>>>>>>>>>>>>>>");
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc("短信验证码发送成功");
                return 1;
            } else {
                logger.info("cuserId" + bean.getCuserId() + "招商提额短信验证码获取失败>>>>>>" + errorContent);
                bean.setBusiErrCode(BillConstant.fail);
                if(errorContent.contains("您在24小时内验证码申请次数超过5次")){
                    bean.setBusiErrDesc("您在24小时内验证码申请次数超过5次。请在24小时后再试。");
                }else {
                    bean.setBusiErrDesc("短信验证码发送失败");
                }
                return 0;
            }
        } catch (Exception e) {
            logger.error("cuserId:" + bean.getCuserId() + getClass().getSimpleName() + " ---", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("系统繁忙,请稍后重试");
        }
        return 0;
    }

    /**
     * 验证短信验证码，登录验证
     * @param cc
     * @param bean
     */
    public int checkSms(Channel bean, MemCachedClient cc) {

        try {
            String bankSession = (String) cc.get(bean.getCuserId() + bean.getBankId() + "bankSession");
            String choiseip = (String) cc.get(bean.getCuserId() + bean.getBankId() + "choiseip");
            int choiseport = (int) cc.get(bean.getCuserId() + bean.getBankId() + "choiseport");
            String MsgCode = bean.getBankRand();//短信验证码
            if (bankSession == null || choiseip == null) {
                logger.info(bean.getCuserId() + bean.getBankId() + "bankSession" + "=" + bankSession +" choiseip:"+choiseip);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
                return 0;
            }

            HttpHost proxy = new HttpHost(choiseip, choiseport, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient hc = HttpClients.custom().setRoutePlanner(routePlanner).build();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String,String> headers = new HashMap<>();
            headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
            headers.put("Referer", "https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx");
            headers.put("Host","html.m.cmbchina.com");
            headers.put("User-Agent","Opera/9.80 (Windows NT 6.1; WOW64; Opera Mobi/46154) Presto/2.11.355 Version/12.10");
            String userIp = choiseip;
            if (!StringUtils.isEmpty(userIp)) {
                headers.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            String url = "https://html.m.cmbchina.com/MobileHtml/Login/MsgVerify.aspx";

            Map<String, String> parames = new HashMap<String, String>();
            parames.put("MsgFlag_", "110000");
            parames.put("MsgCode", MsgCode);
//            parames.put("MsgTips_", "请选择一个手机号码，并点击“发送短信验证码”按钮来发送验证码。如果你已经取得验证码，请直接在下面的文本框中输入即可。");
            parames.put("ddlMobile", "1");
            parames.put("Command", "CMD_VERIFY");
            parames.put("ClientNo", bankSession);
            parames.put("DeviceTAB", "TabDefault");

            bean.setBankSessionId(bankSession);
            logger.info("banksessionid=" + bankSession);
            String rhtml = httpPost(url, headers, parames, hc, localContext, "UTF-8", requestConfig);

            if (rhtml.indexOf("Logout") == -1) {//登录失败
               if (rhtml.indexOf("信用卡开卡及设定密码") != -1) {
                    bean.setBusiErrDesc("您的信用卡还未激活或者未设定密码！");
                    bean.setBusiErrCode(BillConstant.fail);
                    return 0;
                } else if (rhtml.indexOf("频繁操作") != -1) {
                    bean.setBusiErrDesc("您的操作过于频繁，请稍后再试！");
                    bean.setBusiErrCode(BillConstant.fail);
                    return 0;
                } else if (rhtml.indexOf("您设置的密码过于简单，为了您的帐户安全，建议您尽快修改") != -1) {
                    bean.setBusiErrDesc("解析失败，您设置的密码过于简单，为了您的帐户安全，建议您尽快修改！");
                    bean.setBusiErrCode(BillConstant.fail);
                    return 0;
                } else if (rhtml.indexOf("亲爱的准信用卡客户，请您登录掌上生活客户端进行相关业务的查询与办理") != -1) {
                    bean.setBusiErrDesc("亲爱的准信用卡客户，请您登录掌上生活客户端进行相关业务的查询与办理");
                    bean.setBusiErrCode(BillConstant.fail);
                    return 0;
                }else if(rhtml.indexOf("验证验证码失败") != -1){
                    bean.setBusiErrDesc("短信验证码错误,请重新输入！");
                    bean.setBusiErrCode(BillConstant.needmsg);
                    return 0;
                }else if(rhtml.indexOf("操作失败") != -1){
                   bean.setBusiErrDesc("操作失败,请重新登录！");
                   bean.setBusiErrCode(BillConstant.fail);
                   return 0;
               } else {
                    bean.setBusiErrDesc("解析失败，请重试！");
                    logger.info("获取账单失败，未获取到失败原因[" + rhtml + "]");
                    return 0;
                }
            }
            bean.setIpAddr(choiseip);

        } catch (Exception e) {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("登陆失败！");
        }
        return 1;
    }


    /**
     * 验证短信验证码，提交提额
     * @param cc
     * @param bean
     * @param client
     */
    public int checkSmsTe(Channel bean, MemCachedClient cc,IDrpcClient client) {
        String banksessionid = "";
        try {
            String bankSession = (String) cc.get(bean.getCuserId() + bean.getBankId() + "bankSession");
            String choiseip = (String) cc.get(bean.getCuserId() + bean.getBankId() + "choiseip");
            int choiseport = (int) cc.get(bean.getCuserId() + bean.getBankId() + "choiseport");
            if (bankSession == null || choiseip == null) {
                logger.info(bean.getCuserId() + bean.getBankId() + "bankSession" + "=" + bankSession +" choiseip:"+choiseip);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
                return 0;
            }

            HttpHost proxy = new HttpHost(choiseip, choiseport, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient hc = HttpClients.custom().setRoutePlanner(routePlanner).build();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String,String> headers = new HashMap<>();
            headers.put("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, multipart/mixed, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
            headers.put("Referer", "https://html.m.cmbchina.com/MobileHtml/Login/LoginC.aspx");
            headers.put("Host","html.m.cmbchina.com");
            headers.put("User-Agent","Opera/9.80 (Windows NT 6.1; WOW64; Opera Mobi/46154) Presto/2.11.355 Version/12.10");

            //额度提升详情页面  0能提升临时额度 1不能   0能提升固定额度 1不能
            String urlstr = "https://html.m.cmbchina.com/MobileHtml/creditcard/m_limit/lm_limitmanagegen.aspx?ClientNo=" + banksessionid + "&Command=&XmlReq=";
            String errorContent = HpClientUtil.httpGet(urlstr, headers, hc, localContext, "utf-8",false, requestConfig);
            Document limitmDoc = null;
            String CaseNo = "";
            String ZXCode = "";
            String MsgFlag_ = "";
            String SetLimit = bean.getUptomoney();  //临时额度申请金额
            String MsgCode = bean.getBankRand();//短信验证码
            ForeheadRecord foreheadRecord = new ForeheadRecord();
            StringBuilder url = new StringBuilder();
            String type = bean.getType();
            if (type.equals("2")) {  //固额
                //固定额度调整
                String teStr = errorContent.substring(errorContent.indexOf("function GotoTempChange()"),errorContent.indexOf("function GotoFixUp()"));
                String CreditAccNo = teStr.substring(teStr.indexOf("CreditAccNo")+15,teStr.lastIndexOf("submitControl.addFieldByNameValue")-16);
                String rtnURL = teStr.substring(teStr.indexOf("rtnURL")+10,teStr.lastIndexOf("showWaitingPage")-16);
                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_FixLimitManage.aspx?CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&rtnURL=");
                url.append(rtnURL);
                url.append("&Command=&DeviceTAB=TabDefault");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);

                //固额请求发送
                String Vincio = "";
                String CurrentLimit = "";
                String VincioLimit = "";
                String TempLimit = "";
                limitmDoc = Jsoup.parse(errorContent);
                MsgFlag_ = limitmDoc.getElementById("cphBody_HtmlMsgVerify_MsgFlag_").val();
                String tgeStr = errorContent.substring(errorContent.indexOf("function DoSubmit()"),errorContent.indexOf("function UpSubmitFunction()"));
                Vincio = tgeStr.substring(tgeStr.indexOf("Vincio")+10,tgeStr.indexOf("CurrentLimit")-55);
                CurrentLimit = tgeStr.substring(tgeStr.indexOf("CurrentLimit")+16,tgeStr.indexOf("VincioLimit")-55);
                VincioLimit = tgeStr.substring(tgeStr.indexOf("VincioLimit")+15,tgeStr.indexOf("TempLimit")-55);
                TempLimit = tgeStr.substring(tgeStr.indexOf("TempLimit")+13,tgeStr.indexOf("SetLimit")-55);


                url.setLength(0);
                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_FixLimitManage.aspx?MsgFlag_=");
                url.append(MsgFlag_);
                url.append("&MsgCode=");
                url.append(MsgCode);
                url.append("&MsgTips_=请选择一个手机号码，并点击“发送短信验证码”按钮来发送验证码。如果你已经取得验证码，请直接在下面的文本框中输入即可。");
                url.append("&ddlMobile=1");
                url.append("&ActionFlag=Y");
                url.append("&CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&Vincio=");
                url.append(Vincio);
                url.append("&CurrentLimit=");
                url.append(CurrentLimit);
                url.append("&VincioLimit=");
                url.append(VincioLimit);
                url.append("&TempLimit=");
                url.append(TempLimit);
                url.append("&SetLimit=");
                url.append(SetLimit);
                url.append("&Command=");
                url.append("CMD_DOSUBMIT");
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&DeviceTAB=");
                url.append("TabDefault");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);
                Document pointEles = Jsoup.parse(errorContent);
                String errMsg = "";
                if (errorContent != null && errorContent.contains("您的申请已受理，申请的固定额度将在两日后生效。")) {
                    logger.info("cuserId" + bean.getCuserId() + "招商固额提升申请成功>>>>>>>>>>>>>>>>>>>>>>>>>");
                    bean.setBusiErrCode(BillConstant.success);
                    try{
                        errMsg = pointEles.getElementsByClass("resultpage-content").get(0).text();
                        errMsg = errMsg.substring(0,errMsg.indexOf("<br />"));
                        bean.setBusiErrDesc(errMsg);
                    }catch (Exception e){
                        bean.setBusiErrDesc("固定额度提额申请成功");
                    }

                    //提额记录
                    foreheadRecord.setFrid(UUID.randomUUID().toString());
                    foreheadRecord.setIsprofix(0);
                    foreheadRecord.setIsstate(0);
                    foreheadRecord.setIbillid(bean.getBillId());
//                    foreheadRecord.setCreatetime(new Date());
                    if(CurrentLimit !=null && !CurrentLimit.equals("")){
                        foreheadRecord.setOldlimit(Double.parseDouble(CurrentLimit));
                    }
                    if( SetLimit!=null && !SetLimit.equals("")){
                        foreheadRecord.setSetlimit(Double.parseDouble(SetLimit));
                    }
                    client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("CardQuotaBolt", "saveForeheadRecord", foreheadRecord));
                    //更新bankbill表数据
                    BankBillDto bankBillDto = new BankBillDto();
                    bankBillDto.setIbillid(Integer.parseInt(bean.getBillId()));
                    bankBillDto.setIsFixed(2);
                    client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("CardQuotaBolt", "updateByPrimaryKeySelective", bankBillDto));
                    return 0;
                } else {
                    logger.info("cuserId" + bean.getCuserId() + "招商固额提升申请失败>>>>>>" + errorContent);
                    bean.setBusiErrCode(BillConstant.fail);
                    try{
                        errMsg = pointEles.getElementById("TEContent").text();
                    }catch (Exception e){
                        errMsg = "固额提升申请失败";
                    }
                    bean.setBusiErrDesc(errMsg);

                    //提额记录
                    foreheadRecord.setFrid(UUID.randomUUID().toString());
                    foreheadRecord.setIsprofix(0);
                    foreheadRecord.setIsstate(1);
                    foreheadRecord.setIbillid(bean.getBillId());
                    if(CurrentLimit !=null && !CurrentLimit.equals("")){
                        foreheadRecord.setOldlimit(Double.parseDouble(CurrentLimit));
                    }
                    if( SetLimit!=null && !SetLimit.equals("")){
                        foreheadRecord.setSetlimit(Double.parseDouble(SetLimit));
                    }
                    client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("CardQuotaBolt", "saveForeheadRecord", foreheadRecord));
                    return 0;
                }


            } else if (type.equals("3")) {
                //临时额度调整

                String teStr = errorContent.substring(errorContent.indexOf("function GotoTempChange()"),errorContent.indexOf("function GotoFixUp()"));
                String CreditAccNo = teStr.substring(teStr.indexOf("CreditAccNo")+15,teStr.lastIndexOf("submitControl.addFieldByNameValue")-16);
                String rtnURL = teStr.substring(teStr.indexOf("rtnURL")+10,teStr.lastIndexOf("showWaitingPage")-16);
                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_TempLimitManage.aspx?CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&rtnURL=");
                url.append(rtnURL);
                url.append("&Command=&DeviceTAB=TabDefault");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);

                //临时额度请求
                String BeginDate ="";
                String EndDate = "";
                String CurrentLimit = "";
                String cphBody_lbMaxLimit ="";   //最大临时额度
                limitmDoc = Jsoup.parse(errorContent);
                MsgFlag_ = limitmDoc.getElementById("cphBody_HtmlMsgVerify_MsgFlag_").val();
                String tgeStr = errorContent.substring(errorContent.indexOf("function Submit()"),errorContent.indexOf("function Cancel()"));
                CaseNo = tgeStr.substring(tgeStr.indexOf("CaseNo")+10,tgeStr.indexOf("ZXCode")-55);
//                ZXCode = tgeStr.substring(tgeStr.indexOf("ZXCode")+10,tgeStr.indexOf("showWaitingPage()")-20);

                try{
                    CurrentLimit = limitmDoc.getElementById("cphBody_lbCurrentLimit").text().replaceAll("[^0-9.]", "");
                }catch (Exception e){
                    CurrentLimit = limitmDoc.getElementById("cphBody_lbCreditLimit").text().replaceAll("[^0-9.]", "");
                }
                cphBody_lbMaxLimit = limitmDoc.getElementById("cphBody_lbMaxLimit").text().replaceAll("[^0-9.]", "");

                BeginDate = limitmDoc.getElementById("cphBody_inputWantedBegin").val();
                EndDate = limitmDoc.getElementById("cphBody_inputWantedEnd").val();
                url.setLength(0);
                url.append("https://html.m.cmbchina.com/MobileHtml/CreditCard/M_Limit/lm_TempLimitManage.aspx?SetLimit=");
                url.append(SetLimit);
                url.append("&BeginDate=");
                url.append(BeginDate);
                url.append("&EndDate=");
                url.append(EndDate);
                url.append("&MsgFlag_=");
                url.append(MsgFlag_);
                url.append("&MsgCode=");
                url.append(MsgCode);
                url.append("&MsgTips_=请选择一个手机号码，并点击“发送短信验证码”按钮来发送验证码。如果你已经取得验证码，请直接在下面的文本框中输入即可。");
                url.append("&ddlMobile=1");
                url.append("&CreditAccNo=");
                url.append(CreditAccNo);
                url.append("&CaseNo=");
                url.append(CaseNo);
                url.append("&ZXCode=");
                url.append(ZXCode);
                url.append("&Command=CMD_SUBMIT");
                url.append("&ClientNo=");
                url.append(banksessionid);
                url.append("&DeviceTAB=TabDefault");
                errorContent = HpClientUtil.httpGet(url.toString(), headers, hc, localContext, "utf-8",false, requestConfig);

                if (errorContent != null && errorContent.contains("临时额度申请成功，您的申请已审批通过")) {
                    logger.info("cuserId" + bean.getCuserId() + "招商临额提升申请成功>>>>>>>>>>>>>>>>>>>>>>>>>");
                    bean.setBusiErrCode(BillConstant.success);
                    bean.setBusiErrDesc("临时额度提额申请成功");

                    //提额记录
                    foreheadRecord.setFrid(UUID.randomUUID().toString());
                    foreheadRecord.setIsprofix(1);
                    foreheadRecord.setIsstate(0);
                    foreheadRecord.setIbillid(bean.getBillId());
                    if(CurrentLimit !=null && !CurrentLimit.equals("")){
                        foreheadRecord.setOldlimit(Double.parseDouble(CurrentLimit));
                    }
                    if( SetLimit!=null && !SetLimit.equals("")){
                        foreheadRecord.setSetlimit(Double.parseDouble(SetLimit));
                    }
                    client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("CardQuotaBolt", "saveForeheadRecord", foreheadRecord));
                    //更新bankbill表数据
                    BankBillDto bankBillDto = new BankBillDto();
                    bankBillDto.setIbillid(Integer.parseInt(bean.getBillId()));
                    bankBillDto.setItotalquota(Double.parseDouble(SetLimit));
                    bankBillDto.setNowzeroquota(SetLimit);
                    if(Double.parseDouble(cphBody_lbMaxLimit)>Double.parseDouble(SetLimit)){
                        bankBillDto.setIsZero(0);
                    }else {
                        bankBillDto.setIsZero(1);
                    }
                    client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("CardQuotaBolt", "updateByPrimaryKeySelective", bankBillDto));
                    return 0;
                } else {
                    logger.info("cuserId" + bean.getCuserId() + "招商临额提升申请失败>>>>>>" + errorContent);
                    bean.setBusiErrCode(BillConstant.fail);
                    Document pointEles = Jsoup.parse(errorContent);
                    String errMsg = pointEles.getElementById("TEContent").text();
                    bean.setBusiErrDesc(errMsg);

                    //提额记录
                    foreheadRecord.setFrid(UUID.randomUUID().toString());
                    foreheadRecord.setIsprofix(1);
                    foreheadRecord.setIsstate(1);
                    foreheadRecord.setIbillid(bean.getBillId());
                    if(CurrentLimit !=null && !CurrentLimit.equals("")){
                        foreheadRecord.setOldlimit(Double.parseDouble(CurrentLimit));
                    }
                    if( SetLimit!=null && !SetLimit.equals("")){
                        foreheadRecord.setSetlimit(Double.parseDouble(SetLimit));
                    }
                    client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("CardQuotaBolt", "saveForeheadRecord", foreheadRecord));
                    return 0;
                }
            }

        } catch (Exception e) {
            logger.error("cuserId:" + bean.getCuserId() + getClass().getSimpleName() + " ---", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("系统繁忙,请稍后重试");
        }
        return 0;
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
            if(!"HTTP/1.1 200 OK".equals(statusCode2) && !"HTTP/1.0 200 OK".equals(statusCode2)) {
                System.out.println(statusCode2);
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

}
