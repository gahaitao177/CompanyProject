package com.caiyi.financial.nirvana.bill.bank;

import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
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
 * Created by ljl on 2017/2/7.
 * 上海银行网银导入账单
 */
public class ShangHaiBank extends AbstractHttpService{

    /**
     * 获取图片验证码
     * @param bean 参数对象
     * @return base64格式的图片验证码
     **/
    public String setYzm(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "shangHai_cookieStore");
            if (cookieStore==null){
                cookieStore = new BasicCookieStore();
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            String url = "https://ebanks.bankofshanghai.com/pweb/GenTokenImg.do?random="+Math.random();
            loginContext.getHeaders().put("Host","ebanks.bankofshanghai.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            loginContext.getHeaders().put("Accept","image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
            String yzm = getYzm(url,bean.getCuserId(),loginContext);
            cc.set(bean.getCuserId() + bean.getBankId() + "shangHai_cookieStore",loginContext.getCookieStore());
            return yzm;
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " ---获取图片验证异常---"+errHtml, e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("环境异常,稍后重试");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return null;
    }

    /**
     * 上海银行网银登录方法
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int login(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "shangHai_cookieStore");
            if (StringUtils.isEmpty(bean.getBankRand())){
                cookieStore = new BasicCookieStore();
            }
            if (cookieStore==null){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("缓存失效,请重新导入");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            Map<String,String> headers = loginContext.getHeaders();
            headers.put("Host","ebanks.bankofshanghai.com");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            headers.put("Accept","text/html, application/xhtml+xml, */*");
            String url = "https://ebanks.bankofshanghai.com/pweb/prelogin.do?LoginType=R&_locale=zh_CN";
            errHtml = httpGet(url,loginContext);
            Document firstDoc = Jsoup.parse(errHtml);
            Element formEle = firstDoc.getElementsByAttributeValue("name","form2").first();
            Map<String,String> params = setFormParams(formEle);
            if (errHtml.indexOf("var ts")!=-1){
                String tsJs = errHtml.substring(errHtml.indexOf("var ts"));
                tsJs = tsJs.substring(0,tsJs.indexOf(";"));
                String timespan = tsJs.replaceAll("[^0-9]", "");
                logger.info("timespan>>>"+timespan);
                boolean success = hackPassword(bean,timespan,"");
                if (success){
                    String passwordHack = bean.getPasswordHackStr();
                    params.put("Password", passwordHack);
                    params.put("LoginId",bean.getDencryIdcard());
                    params.put("MachineCode",bean.getMachineCode());
                    String bankRand = bean.getBankRand().replaceAll("\\s*","");
                    //没有图片验证码,需要自动识别验证码后登录
                    if (StringUtils.isEmpty(bankRand)){
                        url = "https://ebanks.bankofshanghai.com/pweb/GenTokenImg.do?random="+Math.random();
                        headers.put("Accept","image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
                        headers.put("Referer","https://ebanks.bankofshanghai.com/pweb/prelogin.do?LoginType=R&_locale=zh_CN");
                        String imgBase64 = getYzm(url,bean.getCuserId(),loginContext);
                        //调用自动识别验证码接口
                        loop:for (int i=0;i<yzNum;i++){
                            if (i==yzNum-1){
                                bean.setBankRand(imgBase64);
                                bean.setBusiErrCode(3);
                                bean.setBusiErrDesc("图片自动识别失败,请手动输入!");
                                return 0;
                            }
                            if (!StringUtils.isEmpty(imgBase64)){//获取验证码失败
                                bankRand = distinguishCode(bean,imgBase64,bean.getBankId(),"1");
                                if (StringUtils.isEmpty(bankRand)){
                                    continue;
                                }else {
                                    break loop;
                                }
                            }
                        }
                    }
                    params.put("_vTokenName",bankRand);
                    headers.put("Accept","*/*");
                    headers.put("pe-ajax","true");
                    headers.put("pe-encoding","UTF-8");
                    headers.put("Referer","https://ebanks.bankofshanghai.com/pweb/prelogin.do?LoginType=R&_locale=zh_CN");
                    url = "https://ebanks.bankofshanghai.com/pweb/login.do";
                    errHtml = httpPost(url,params,loginContext);
                    cc.set(bean.getCuserId() + bean.getBankId() + "shangHai_cookieStore",loginContext.getCookieStore());
                    String errMsg = "";
                    Element spanEle = Jsoup.parse(errHtml).select("span.errors_def").first();
                    if (spanEle!=null){
                        errMsg = spanEle.text().replaceAll("\\s*","");
                    }
                    if (StringUtils.isEmpty(errMsg)){
                        if (errHtml.contains("PEAjaxError")){
                            String messageInfo = errHtml.substring(errHtml.indexOf("_exceptionMessage"));
                            messageInfo = messageInfo.substring(0,messageInfo.indexOf(","));
                            errMsg = messageInfo.replaceAll("[_exceptionMessage\"\\:]","").replaceAll("\\s*","");
                        }
                    }
                    logger.info("cuserId=="+bean.getCuserId()+";errMsg>>>"+errMsg);
                    if (errMsg.contains("校验码有误")){
                        url = "https://ebanks.bankofshanghai.com/pweb/GenTokenImg.do?random="+Math.random();
                        headers.put("Accept","image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
                        headers.put("Referer","https://ebanks.bankofshanghai.com/pweb/prelogin.do?LoginType=R&_locale=zh_CN");
                        String imgBase64 = getYzm(url,bean.getCuserId(),loginContext);
                        bean.setBankRand(imgBase64);
                        bean.setBusiErrCode(3);
                        bean.setBusiErrDesc("验证码错误,请重新输入");
                        return 0;
                    }
                    logger.info("cuserId=="+bean.getCuserId()+";上海登录页面>>>>"+lessHtml(errHtml));
                    if (StringUtils.isEmpty(errMsg)){
                        if (errHtml.contains("动态密码")){//需要短信验证
                            formEle = Jsoup.parse(errHtml).getElementsByAttributeValue("name","form1").first();
                            params = setFormParams(formEle);
                            cc.set(bean.getCuserId() + bean.getBankId() + "shangHai_SmSParams",params);
                            bean.setBusiErrCode(BillConstant.needmsg);
                            bean.setBusiErrDesc("需要短信验证");
                            return 0;
                        } else if (errHtml.contains("您当前的网银登录密码安全强度较弱")){
                            logger.info("cuserId"+bean.getCuserId()+":密码强度太弱,提示需要重置登录密码,才能登录>>>>");
                            bean.setBusiErrCode(0);
                            bean.setBusiErrDesc("您当前的网银登录密码安全强度较弱,请到上海网银官网重置查询密码!");
                            return 0;
                        } else{
                            bean.setBusiErrCode(BillConstant.success);
                            bean.setBusiErrDesc("登录成功,开始解析账单");
                            bean.setBankSessionId(loginContext.getCookieStr());
                            return 1;
                        }
                    }else {
                        logger.info("登录失败:"+errMsg);
                        bean.setBusiErrCode(BillConstant.fail);
                        bean.setBusiErrDesc(errMsg);
                        return 0;
                    }
                }else{
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
                }
            }else{
                logger.info("cuserId="+bean.getCuserId()+"---获取页面timespan出错,errHtml>>"+errHtml);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("网络异常,请稍后再试!");
            }

        }catch (Exception e) {
            e.printStackTrace();
            logger.info("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " ---登录接口异常---"+errHtml, e);
            bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
            bean.setBusiErrCode(BillConstant.fail);
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }

    /**
     * 获取短信验证码
     * @param bean 参数封装对象
     * @return 方法执行结果 0:失败 1:成功
     */
    public int getSms(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "shangHai_cookieStore");
            Object paramsObj = cc.get(bean.getCuserId() + bean.getBankId() + "shangHai_SmSParams");
            if (cookieStore == null || paramsObj == null) {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            loginContext.getHeaders().put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            loginContext.getHeaders().put("Host","ebanks.bankofshanghai.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            String url = "https://ebanks.bankofshanghai.com/pweb/OTPPreAuthForBPDC.do";
            Map<String,String> params = new HashMap<>();
            params.put("_TokenType","FC");
            params.put("_AuthenticateType","001000");
            params.put("_MsgContent","DeviceAuthticateConfirm.0");
            errHtml = httpPost(url,params,loginContext);
            Document smsDoc = Jsoup.parse(errHtml);
            Element spanEle = smsDoc.getElementById("SpanCheckCode");
            if (spanEle!=null){
                String rtnMsg = spanEle.text().replaceAll("\\s*","");
                logger.info("cuserid=="+bean.getCuserId()+";上海银行短信验证码发送结果："+rtnMsg);
                if (rtnMsg.contains("已向您") && rtnMsg.contains("手机发送动态密码")){
                    bean.setBusiErrCode(BillConstant.success);
                    bean.setBusiErrDesc("短信发送成功");
                    params = (Map<String, String>) paramsObj;
                    params.put("Challenge",smsDoc.getElementById("Challenge").val());
                    params.put("CheckCode",smsDoc.getElementById("CheckCode").val());
                    params.put("_Challenge",smsDoc.getElementById("_Challenge").val());
                    params.put("_OtpCheckCode",smsDoc.getElementById("_OtpCheckCode").val());
                    cc.set(bean.getCuserId() + bean.getBankId() + "shangHai_SmSParams",params);
                    cc.set(bean.getCuserId() + bean.getBankId() + "shangHai_cookieStore",loginContext.getCookieStore());
                    return 1;
                }else{
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc(rtnMsg);
                    return 0;
                }
            }else{
                logger.info("cuserid=="+bean.getCuserId()+";上海银行发送短信失败:"+errHtml);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("发送短信验证码失败");
                return 0;
            }
        } catch (Exception e) {
            logger.error("cuserId:" + bean.getCuserId() + getClass().getSimpleName() + " 上海短信发送异常---" + errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
        } finally {
            if (loginContext != null) {
                loginContext.close();
            }
        }
        return 0;
    }

    /**
     * 获取短信验证码
     * @param bean
     * @return 0:失败 1:成功
     */
    public int checkSms(Channel bean, MemCachedClient cc){
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "shangHai_cookieStore");
            Object paramsObj = cc.get(bean.getCuserId() + bean.getBankId() + "shangHai_SmSParams");
            if (cookieStore==null || paramsObj==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            loginContext.getHeaders().put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            loginContext.getHeaders().put("Host","ebanks.bankofshanghai.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            String url = "https://ebanks.bankofshanghai.com/pweb/DeviceAuthticate.do";
            Map<String,String> params = (Map<String, String>) paramsObj;
            params.put("_ChooseAuthMode","O");
            params.put("_OtpPassword",bean.getBankRand());
            params.put("AuthModRadio","O");
            params.put("OtpPWD",bean.getBankRand());
            params.put("button","%E4%B8%8B%E4%B8%80%E6%AD%A5");
            errHtml = httpPost(url,params,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";上海银行短信验证结果页面>>"+lessHtml(errHtml));
            String errMsg = "";
            if (errHtml.contains("PEAjaxError")){
                String messageInfo = errHtml.substring(errHtml.indexOf("_exceptionMessage"));
                messageInfo = messageInfo.substring(0,messageInfo.indexOf(","));
                errMsg = messageInfo.replaceAll("[_exceptionMessage\"\\:]","").replaceAll("\\s*","");
            }
            if (StringUtils.isEmpty(errMsg)){
                Elements spanEle = Jsoup.parse(errHtml).select("span#EEE");
                if (spanEle!=null && spanEle.size()>0){
                    errMsg = spanEle.first().text().replaceAll("\\s*","");
                }
                logger.info("cuserId=="+bean.getCuserId()+";errMsg>>>"+errMsg);
                if (StringUtils.isEmpty(errMsg)){
                    if (errHtml.contains("您当前的网银登录密码安全强度较弱")){
                        bean.setBusiErrCode(0);
                        bean.setBusiErrDesc("您当前的网银登录密码安全强度较弱,请到上海网银官网重置查询密码!");
                        return 0;
                    }
                    bean.setBusiErrCode(BillConstant.success);
                    bean.setBusiErrDesc("登录成功,开始解析账单");
                    bean.setBankSessionId(loginContext.getCookieStr());
                    return 1;
                }else{
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc(errMsg);
                    return 0;
                }
            }else{
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc(errMsg);
                return 0;
            }
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 上海短信验证异常--"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }
}
