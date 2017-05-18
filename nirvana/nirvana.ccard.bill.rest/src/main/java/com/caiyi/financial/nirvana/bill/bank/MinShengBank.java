package com.caiyi.financial.nirvana.bill.bank;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.Map;

/**
 * Created by ljl on 2017/2/6.
 * 民生银行网银导入账单
 */
public class MinShengBank extends AbstractHttpService{

    /**
     * 获取图片验证码
     * @param bean 参数对象
     * @return base64格式的图片验证码
     */
    public String setYzm(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "minSheng_cookieStore");
            if (cookieStore==null){
                cookieStore = new BasicCookieStore();
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            Map<String, String> headers = loginContext.getHeaders();
            headers.put("Host","nper.cmbc.com.cn");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            headers.put("Accept","image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");

            String url = "https://nper.cmbc.com.cn/pweb/GenTokenImg.do?random="+Math.random();
            String yzm = getYzm(url, bean.getCuserId(), loginContext);
            cc.set(bean.getCuserId() + bean.getBankId() + "minSheng_cookieStore",loginContext.getCookieStore());
            return yzm;
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行获取图片验证码异常---", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("获取验证码失败");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return null;
    }


    /**
     * 民生银行调用密码加密接口
     * @param bean
     * @param cc
     * @return
     */
    public int login(Channel bean, MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try{
            if (StringUtils.isEmpty(bean.getBankRand())){//首次登陆
                String url = "https://nper.cmbc.com.cn/pweb/static/login.html";
                loginContext = createLoginContext(new BasicCookieStore());
                Map<String, String> headers = loginContext.getHeaders();
                headers.put("Host","nper.cmbc.com.cn");
                headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
                errHtml = httpGet(url,loginContext);

                headers.put("Accept","application/json, text/plain, */*");
                headers.put("Content-Type","application/json;charset=utf-8");
                headers.put("Referer","https://nper.cmbc.com.cn/pweb/static/login.html");
                url = "https://nper.cmbc.com.cn/pweb/UserTokenCheckCtrl.do";
                errHtml = httpRequestJson(url,"{}","application/json",loginContext);
                logger.info("cuserId=="+bean.getCuserId()+" 是否进行图片验证>>>>"+errHtml);
                String tokenShowFlag = "";
                String tokenCheckFlag = "";
                JSONObject checkCtrl = JSONObject.parseObject(errHtml);
                if (checkCtrl.containsKey("TokenShowFlag")){
                    tokenShowFlag = checkCtrl.get("TokenShowFlag").toString();
                }
                if (checkCtrl.containsKey("TokenCheckFlag")){
                    tokenCheckFlag = checkCtrl.get("TokenCheckFlag").toString();
                }
                if (!"false".equals(tokenShowFlag) && !"false".equals(tokenCheckFlag)){
                    url = "https://nper.cmbc.com.cn/pweb/GenTokenImg.do?random="+Math.random();
                    headers.put("Accept","image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
                    String base64Img = getYzm(url, bean.getCuserId(), loginContext);
                    bean.setBankRand(base64Img);
                    bean.setBusiErrCode(BillConstant.needimg);
                    bean.setBusiErrDesc("需要图片验证");
                    cc.set(bean.getCuserId() + bean.getBankId() + "minSheng_cookieStore",loginContext.getCookieStore());
                    return 0;
                }
                return loginExecute(bean,loginContext);
            }else {
                Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "minSheng_cookieStore");
                if (cookieStore==null){
                    cookieStore = new BasicCookieStore();
                }
                loginContext = createLoginContext((BasicCookieStore) cookieStore);
                loginContext.getHeaders().put("Host","nper.cmbc.com.cn");
                loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
                loginContext.getHeaders().put("Referer","https://nper.cmbc.com.cn/pweb/static/login.html");
                return loginExecute(bean,loginContext);
            }
        } catch (Exception e){
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 民生网银登录接口异常---"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }

    private int loginExecute(Channel bean,LoginContext loginContext)throws Exception{
        String url = "https://nper.cmbc.com.cn/pweb/GenerateRand.do";
        loginContext.getHeaders().put("Accept","application/json, text/javascript, */*; q=0.01");
        String errHtml = httpGet(url,loginContext);
        JSONObject restObj = JSONObject.parseObject(errHtml);
        String timespan = "";
        if (restObj.containsKey("RandNum")){
            timespan = restObj.get("RandNum").toString();
            boolean success = hackPassword(bean,timespan,"");
            if (success){
                String passwordHackStr = bean.getPasswordHackStr();
                loginContext.getHeaders().put("Accept","application/json, text/plain, */*");
                loginContext.getHeaders().put("Content-Type","application/json;charset=utf-8");
                JSONObject paramJson = new JSONObject();
                url = "https://nper.cmbc.com.cn/pweb/clogin.do";
                paramJson.put("PwdResult",passwordHackStr);
                paramJson.put("CspName",null);
                paramJson.put("BankId","9999");
                paramJson.put("LoginType","C");
                paramJson.put("_locale","zh_CN");
                paramJson.put("UserId",bean.getDencryIdcard());
                paramJson.put("_vTokenName",bean.getBankRand());
                paramJson.put("_UserDN",null);
                paramJson.put("_asii",9);
                paramJson.put("_targetPath",null);

                errHtml = httpRequestJson(url,paramJson.toJSONString(),"application/json",loginContext);
                logger.info("cuserId=="+bean.getCuserId()+";民生登录结果页>>"+errHtml);
                String errMsg = "";
                if (errHtml.contains("jsonError")){
                    String messageInfo = errHtml.substring(errHtml.indexOf("_exceptionMessage"));
                    messageInfo = messageInfo.substring(0,messageInfo.indexOf(","));
                    errMsg = messageInfo.replaceAll("[_exceptionMessage\"\\:]","");
                }
                if (StringUtils.isEmpty(errMsg)){
                    url = "https://nper.cmbc.com.cn/pweb/static/main.html";
                    loginContext.getHeaders().put("Accept","text/html, application/xhtml+xml, */*");
                    httpGet(url,loginContext);
                    bean.setBankSessionId(loginContext.getCookieStr());
                    bean.setBusiErrCode(BillConstant.success);
                    bean.setBusiErrDesc("登录成功,开始解析账单");
                    return 1;
                } else{
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc(errMsg);
                }
            }else{
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
            }
        }else{
            logger.info("cuserId="+bean.getCuserId()+"---民生获取timespan页面异常,errHtml>>"+errHtml);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("网络异常,请稍后");
        }
        return 0;
    }
}
