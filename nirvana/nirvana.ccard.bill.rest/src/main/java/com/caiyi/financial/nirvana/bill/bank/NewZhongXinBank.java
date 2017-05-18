package com.caiyi.financial.nirvana.bill.bank;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ljl on 2017/4/7.
 */
public class NewZhongXinBank extends AbstractHttpService{

    /**
     * 获取图片验证码
     * @param bean 参数对象
     * @return base64格式的图片验证码
     **/
    public String setYzm(Channel bean, MemCachedClient cc) {
        LoginContext loginContext = null;
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
            if (cookieStore==null){
                cookieStore = new BasicCookieStore();
            }
            HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
            loginContext = createContextProxy((BasicCookieStore) cookieStore,proxy);
            loginContext.getHeaders().put("Host","creditcard.ecitic.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
            loginContext.getHeaders().put("Accept","image/webp,image/*,*/*;q=0.8");
            String imgUrl = "https://creditcard.ecitic.com/citiccard/ucweb/valicode.do?time="+System.currentTimeMillis();
            String yzm = getYzm(imgUrl,bean.getCuserId(),loginContext);
            cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
            return yzm;
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " ---获取图片验证异常---", e);
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return null;
    }

    /**
     * 中信银行执行登录接口
     * @param bean 参数封装
     * @param cc 缓存对象
     * @return 请求结果:1：成功 0：失败
     */
    public int login(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            if (StringUtils.isEmpty(bean.getBankRand())){//自动识别验证码登录
                HttpHost proxy = getHttpProxy(false,new ArrayList<>());
                loginContext = createContextProxy(new BasicCookieStore(),proxy);
                loginContext.getHeaders().put("Host","creditcard.ecitic.com");
                loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
                String userIp = bean.getIpAddr();
                if (!StringUtils.isEmpty(userIp)) {
                    loginContext.getHeaders().put("X-Forwarded-For", userIp);
                    logger.info(bean.getCuserId() + " userip=" + userIp);
                }
                //中信首页
                String url = "https://creditcard.ecitic.com/citiccard/ucweb/oldinit.do";
                httpGet(url,loginContext);
                //获取图片验证码
                loginContext.getHeaders().put("Accept","image/webp,image/*,*/*;q=0.8");
                loginContext.getHeaders().put("Referer","https://creditcard.ecitic.com/citiccard/ucweb/oldinit.do");
                String imgUrl = "https://creditcard.ecitic.com/citiccard/ucweb/valicode.do?time="+System.currentTimeMillis();
                String imgBase64 = getYzm(imgUrl,bean.getCuserId(),loginContext);
                cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
                cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy",proxy);
                if (StringUtils.isEmpty(imgBase64)){//获取验证码失败
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc("获取验证码失败");
                    return 0;
                }
                String bankrand = distinguishCode(bean,imgBase64,bean.getBankId(),"1");
                if (StringUtils.isEmpty(bankrand)){
                    bean.setBankRand(imgBase64);
                    bean.setBusiErrCode(3);
                    bean.setBusiErrDesc("自动识别验证码失败");
                    return 0;
                }
                bean.setBankRand(bankrand);
                return loginExecute(bean,loginContext,cc);
            } else {//手动输入验证码登录
                Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
                if (cookieStore==null){
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("缓存失效,请重新导入!");
                    return 0;
                }
                HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
                loginContext = createContextProxy((BasicCookieStore) cookieStore,proxy);
                loginContext.getHeaders().put("Host","creditcard.ecitic.com");
                loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
                loginContext.getHeaders().put("Referer","https://creditcard.ecitic.com/citiccard/ucweb/oldinit.do");
                String userIp = bean.getIpAddr();
                if (!StringUtils.isEmpty(userIp)) {
                    loginContext.getHeaders().put("X-Forwarded-For", userIp);
                    logger.info(bean.getCuserId() + " userip=" + userIp);
                }
                return loginExecute(bean,loginContext,cc);
            }
        } catch (Exception e) {
            logger.info("cuserId:"+bean.getCuserId() + " 中信登录接口异常---"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
        }
        return 1;
    }

    private int loginExecute(Channel bean,LoginContext loginContext,MemCachedClient cc){
        try{
            //登录
            String url = "https://creditcard.ecitic.com/citiccard/ucweb/login.do?date="+System.currentTimeMillis();
            loginContext.getHeaders().put("Accept","application/json, text/javascript, */*; q=0.01");
            loginContext.getHeaders().put("Content-Type","application/json");
            JSONObject params = new JSONObject();
            params.put("loginType","02");//持卡人证件登录
            params.put("idType","1");//居民身份证登录
            params.put("idNumber",bean.getDencryIdcard());
            params.put("spCode",bean.getDencryBankPwd());
            params.put("valiCode",bean.getBankRand());
            params.put("isBord",false);
            params.put("source","PC");
            params.put("page","old");
            String errHtml = httpRequestJson(url,params.toString(),"application/json",loginContext);
            logger.info("cuserId=="+bean.getCuserId()+" 中信登录结果页面>>>"+errHtml);
            if (StringUtils.isEmpty(errHtml)){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("网络异常,请稍后重试!");
                return 0;
            }
            JSONObject restObj = JSONObject.parseObject(errHtml);
            String rtnCode = "";
            String retMsg = "";
            String needValiCode = "";
            if (restObj.containsKey("rtnCode")){
                rtnCode = restObj.get("rtnCode").toString();
            }
            if (restObj.containsKey("retMsg")){
                retMsg = restObj.get("retMsg").toString();
            }
            if (restObj.containsKey("needValiCode")){
                needValiCode = restObj.get("needValiCode").toString();
            }
            if (!"0000000".equals(rtnCode)){
                logger.info("cuserid=="+bean.getCuserId()+";登录失败>>>"+retMsg);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(retMsg);
                return 0;
            } else {
                cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
                if ("true".equals(needValiCode)){//需要短信验证
                    bean.setBusiErrCode(2);
                    bean.setBusiErrDesc("需要短信验证");
                    return 0;
                }
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("登录成功,开始解析账单!");
                bean.setBankSessionId(loginContext.getCookieStr());
                return 1;
            }
        } catch (Exception e){
            logger.error("cuserId="+bean.getCuserId()+" 中信执行登录请求异常", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
            return 0;
        } finally {
            if (loginContext!=null){
                loginContext.close();
            }
        }
    }

    /**
     * 中信发送短信接口
     * @param bean
     * @param cc
     * @return
     */
    public int getSms(Channel bean,MemCachedClient cc){
        LoginContext loginContext = null;
        try{
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
            if (cookieStore==null){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("缓存失效,请重新导入!");
                return 0;
            }
            HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
            loginContext = createContextProxy((BasicCookieStore) cookieStore,proxy);
            Map<String,String> params = new HashMap<>();
            Map<String,String> requestHeader = loginContext.getHeaders();
            requestHeader.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            requestHeader.put("Host","creditcard.ecitic.com");
            requestHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
            requestHeader.put("Referer","https://creditcard.ecitic.com/citiccard/ucweb/oldinit.do");
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                loginContext.getHeaders().put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            String url = "https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do?date="+System.currentTimeMillis();
            httpGet(url,loginContext);

            requestHeader.put("Accept","application/json, text/javascript, */*; q=0.01");
            requestHeader.put("Content-Type","multipart/form-data");
            requestHeader.put("Referer",url);
            url = "https://creditcard.ecitic.com/citiccard/ucweb/sendSms.do?date="+System.currentTimeMillis();
            String errHtml = httpPost(url,params,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+" 中信银行发送短信验证码结果页面>>>"+errHtml);
            JSONObject retObj = JSONObject.parseObject(errHtml);
            //短信验证码已发送到您的手机：159 **** 1216，请注意查收）
            String rtnCode = retObj.getString("rtnCode");
            String phone = retObj.getString("phone");
            String rtnMsg = retObj.getString("rtnMsg");
            if(rtnCode.equals("0000000")){
                logger.info("cuserId=="+bean.getCuserId()+" 中信短信验证码已发送到您的手机："+phone+"，请注意查收");
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("短信发送成功");
                cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
                return 1;
            }else{
                logger.info("cuserId=="+bean.getCuserId()+" 中信短信发送失败："+rtnMsg);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("短信发送失败");
                return 0;
            }
        } catch (Exception e){
            logger.error("cuserId="+bean.getCuserId()+" 中信发送短信异常", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
            return 0;
        } finally {
            if (loginContext!=null){
                loginContext.close();
            }
        }
    }

    /**
     * 检测短信验证码
     */
    public int checkSms(Channel bean,MemCachedClient cc){
        LoginContext loginContext = null;
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
            if (cookieStore==null){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("缓存失效,请重新导入!");
                return 0;
            }
            HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
            loginContext = createContextProxy((BasicCookieStore) cookieStore,proxy);
            Map<String,String> requestHeader = loginContext.getHeaders();
            requestHeader.put("Host","creditcard.ecitic.com");
            requestHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
            requestHeader.put("Accept","application/json, text/javascript, */*; q=0.01");
            requestHeader.put("Content-Type","application/json");
            requestHeader.put("Referer","https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do");
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                loginContext.getHeaders().put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            JSONObject smsJson = new JSONObject();
            smsJson.put("smsCode",bean.getBankRand());
            String jsStr = smsJson.toString();
            String url = "https://creditcard.ecitic.com/citiccard/ucweb/checkSms.do?date="+System.currentTimeMillis();
            String errHtml = httpRequestJson(url,jsStr,"application/json",loginContext);
            logger.info("cuserId=="+bean.getCuserId()+" 中信短信验证码验证结果页面>>>"+errHtml);
            JSONObject retObj = JSONObject.parseObject(errHtml);
            //短信验证码校验成功
            String rtnCode = retObj.getString("rtnCode");
            String rtnMsg = retObj.getString("rtnMsg");
            if(!rtnCode.equals("0000000")){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc(rtnMsg);
                return 0;
            } else {
                cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("登录成功,开始解析账单");
                bean.setBankSessionId(loginContext.getCookieStr());
                return 1;
            }
        } catch (Exception e){
            logger.error("cuserId="+bean.getCuserId()+" 中信短信验证异常", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
            return 0;
        } finally {
            if (loginContext!=null){
                loginContext.close();
            }
        }
    }
}
