package com.caiyi.financial.nirvana.bill.bank;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ljl on 2017/3/27.
 */
public class NewNongYeBank extends AbstractHttpService{
    /**
     * 获取图片验证码
     * @param bean 参数对象
     * @return base64格式的图片验证码
     */
    public String setYzm(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            loginContext = createLoginContext(new BasicCookieStore());
            Map<String, String> headers = loginContext.getHeaders();
            headers.put("Host","perbank.abchina.com");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            headers.put("Accept","text/html, application/xhtml+xml, */*");
            //农行首页
            String url = "https://perbank.abchina.com/EbankSite/startup.do";
            errHtml = httpGet(url,loginContext);
            Element formEle = Jsoup.parse(errHtml).getElementById("userNameForm");
            String formHtml = formEle.html();
            String randomText = formHtml.substring(formHtml.indexOf("randomText"));
            randomText = randomText.substring(0,randomText.indexOf(","));
            String pubkey = randomText.replaceAll("randomText","")
                    .replaceAll("\"","").replaceAll("\\:","").replaceAll("\\s*","");
            String  timespanJs = errHtml.substring(errHtml.indexOf("password.GetMachineCode"));
            timespanJs = timespanJs.substring(0,timespanJs.indexOf(";"));
            String[] timespans = timespanJs.split(",");
            String timespan = "";
            if (timespans.length>1){
                timespan = timespans[1].replaceAll("[^0-9]", "");
            }
            logger.info("cuserId=="+bean.getCuserId()+" --pubkey>>>"+pubkey+";timespan>>>"+timespan);
            if (StringUtils.isEmpty(pubkey) || StringUtils.isEmpty(timespan)){
                logger.info("cuserId="+bean.getCuserId()+"---获取页面timespan出错,errHtml>>"+errHtml);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("网络异常,请稍后再试!");
                return null;
            }
            //获取图片验证码
            url = "https://perbank.abchina.com/EbankSite/LogonImageCodeAct.do?r="+Math.random();
            headers.put("Accept","image/png, image/svg+xml, image*//*;q=0.8, **/*//*;q=0.5");
            headers.put("Referer","https://perbank.abchina.com/EbankSite/startup.do");
            String yzm = getYzm(url, bean.getCuserId(), loginContext);
            cc.set(bean.getCuserId() + bean.getBankId() + "nongye_getTimespan",timespan+"@"+pubkey);
            cc.set(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore",loginContext.getCookieStore());
            Map<String,String> params = setFormParams(formEle);
            cc.set(bean.getCuserId() + bean.getBankId() + "nongye_loginParams",params);
            return yzm;
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行获取图片验证码异常---", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("获取验证码失败");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return null;
    }

    /**
     *银行网银登录方法
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int login(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try{
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore");
            if (cookieStore==null){
                cookieStore = new BasicCookieStore();
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            loginContext.getHeaders().put("Host","perbank.abchina.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            loginContext.getHeaders().put("Accept","application/json, text/javascript, **/*//*; q=0.01");
            loginContext.getHeaders().put("Referer","https://perbank.abchina.com/EbankSite/startup.do");
            //图片验证码验证
            String url = "https://perbank.abchina.com/EbankSite/VerifyPicCodeAct.do?picCode="+bean.getBankRand()+"&r="+Math.random();
            errHtml = httpGet(url,loginContext);
            System.out.println(errHtml);
            JSONObject restJson = JSONObject.parseObject(errHtml);
            String err_code = restJson.get("errorCode").toString();
            String err_msg = restJson.get("errorMsg").toString();
            if (!"0000".equals(err_code)){
                logger.info("cuserId=="+bean.getCuserId()+";图片验证错误>>>"+errHtml);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(err_msg);
                return 0;
            }
            String spans = (String) cc.get(bean.getCuserId() + bean.getBankId() + "nongye_getTimespan");
            String timespan = spans.split("\\@")[0];
            String pubkey = spans.split("\\@")[1];
            boolean success = hackPassword(bean,timespan,pubkey);
            if (success){
                Map<String,String> params = (Map<String, String>) cc.get(bean.getCuserId() + bean.getBankId() + "nongye_loginParams");
                params.put("username",bean.getDencryIdcard());
                params.put("MachineCode",bean.getMachineCode());
                params.put("MachineInfo",bean.getMachineInfo());
                params.put("code",bean.getBankRand());
                params.put("picCode",bean.getBankRand());
                params.put("pwdField",bean.getPasswordHackStr());
                params.put("pwdFieldKeys","fromAcctPswd");
                params.put("plattype","Win32");
                url = "https://perbank.abchina.com/EbankSite/upLogin.do";
                loginContext.getHeaders().put("Accept","text/html, application/xhtml+xml, */*");
                loginContext.getHeaders().put("Content-Type","application/x-www-form-urlencoded");
                loginContext.getHeaders().put("Referer","https://perbank.abchina.com/EbankSite/startup.do");
                errHtml = httpPost(url,params,loginContext);
                Element formEle = Jsoup.parse(errHtml).getElementById("redirectForm");
                String errMsg = "";
                if (formEle!=null){
                    Element errEle = formEle.getElementsByAttributeValue("name","error").first();
                    errMsg = errEle.val();
                }
                logger.info("cuserId=="+bean.getCuserId()+";农业银行登录结果页>>>"+errHtml);
                if (!StringUtils.isEmpty(errMsg)){
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc(errMsg);
                    return 0;
                } else {
                    if (errHtml.contains("用户名登录-短信验证")){
                        bean.setBusiErrCode(2);
                        bean.setBusiErrDesc("需要短信验证");
                        String phoneNum = Jsoup.parse(errHtml).getElementById("securityPhone").val();
                        Element vefifySms = Jsoup.parse(errHtml).getElementById("SelfHelpVerifySmsForm");
                        Map<String,String> smsParams =  setFormParams(vefifySms);
                        cc.set(bean.getCuserId() + bean.getBankId() + "nongye_PhoneNum",phoneNum);
                        cc.set(bean.getCuserId() + bean.getBankId() + "nongye_SmsParams",smsParams);
                        return 0;
                    }
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("登录成功,开始解析账单!");
                    bean.setBankSessionId(loginContext.getCookieStr());
                }
                return 1;
            }else{
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行登录接口异常---"+errHtml, e);
            bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
            bean.setBusiErrCode(-1);
        }finally {
            if (loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }

    //农行网银获取短信接口
    public int getSms(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore");
            Object phoneObj = cc.get(bean.getCuserId() + bean.getBankId() + "nongye_PhoneNum");
            if (cookieStore==null||phoneObj==null){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("缓存失效,请重新导入");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            String phoneNum = (String) phoneObj;
            loginContext.getHeaders().put("Host","perbank.abchina.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            loginContext.getHeaders().put("Accept","text/plain, */*; q=0.01");
            loginContext.getHeaders().put("Referer","https://perbank.abchina.com/EbankSite/upLogin.do");
            String url = "https://perbank.abchina.com/EbankSite/SendSmsVerifyCodeAct.ebf";
            Map<String,String> params = new HashMap<>();
            params.put("mobileNoField","securityPhone");
            params.put("isValidMac","0");
            params.put("sendType","17");
            params.put("mobile",phoneNum);
            errHtml = httpPost(url,params,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";农行发送短信结果页面>>>"+errHtml);
            if (StringUtils.isEmpty(errHtml)){
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("短信发送成功");
                cc.set(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore",loginContext.getCookieStore());
                return 1;
            } else {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信发送失败");
                return 0;
            }
        } catch (Exception e){
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行获取短信验证码异常---"+errHtml, e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("短信验证码发送失败");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }

    //农行网银短信验证接口
    public int checkSms(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore");
            Object paramsObj = cc.get(bean.getCuserId() + bean.getBankId() + "nongye_SmsParams");
            if (cookieStore==null||paramsObj==null){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("缓存失效,请重新导入");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            Map<String,String> smsParams = (Map<String, String>) paramsObj;
            loginContext.getHeaders().put("Host","perbank.abchina.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            loginContext.getHeaders().put("Accept","text/html, application/xhtml+xml, */*");
            loginContext.getHeaders().put("Referer","https://perbank.abchina.com/EbankSite/upLogin.do");
            String url = "https://perbank.abchina.com/EbankSite/SelfHelpVerifySmsCodeAct.ebf";
            smsParams.put("isValidMac","0");
            smsParams.put("verifycode",bean.getBankRand());
            errHtml = httpPost(url,smsParams,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";农业短信验证结果页>>>"+errHtml);
            Document vefifyDoc = Jsoup.parse(errHtml);
            Elements pEles = vefifyDoc.select("p.m-logo-p");
            if (pEles!=null && pEles.size()>0){
                String title = pEles.get(0).text().replaceAll("\\s*","");
                if ("个人网上银行-错误页面".equals(title)){
                    Elements divEles = vefifyDoc.select("div.m-register-yes");
                    String errMsg = "未知错误信息";
                    if (divEles!=null && divEles.size()>0){
                        Elements liEles = divEles.first().getElementsByTag("li");
                        if (liEles!=null && liEles.size()>=2){
                            errMsg = liEles.get(1).text();
                        }
                    }
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc(errMsg);
                    return 0;
                }
            }
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("短信验证成功,开始解析账单!");
            bean.setBankSessionId(loginContext.getCookieStr());
            return 1;
        } catch (Exception e){
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行获取短信验证码异常---"+errHtml, e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("短信验证码发送失败");
            return 0;
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
    }
}
