package com.caiyi.financial.nirvana.bill.bank;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCookieStore;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by terry on 2016/7/4
 */
public class ZhongXinBank extends AbstractHttpService{


    /**
     * 获取图片验证码
     * @param bean 参数对象
     * @return base64格式的图片验证码
     **/
    public String setYzm(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            BasicCookieStore zxCookieStore = (BasicCookieStore) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
            HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
            logger.info(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy"+"=="+proxy);
            if (zxCookieStore==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重试");
                return "";
            }
            loginContext = createContextProxy(zxCookieStore,proxy);
            Map<String,String> requestHeader = loginContext.getHeaders();
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                requestHeader.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            //获取图片验证码
            String url = "https://creditcard.ecitic.com/citiccard/ucweb/newvalicode.do";
            requestHeader.put("Accept", "image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
            requestHeader.put("Host", "creditcard.ecitic.com");
            requestHeader.put("Referer", "https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
            requestHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
            String yzm = getYzm(url, bean.getCuserId(), loginContext);
            cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
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
     * 中信银行执行登录接口
     * @param bean 参数封装
     * @param cc 缓存对象
     * @return 请求结果:1：成功 0：失败
     */
    public int login(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try{
            //执行js,密码加密
            String encrypwd = getEncrypwd(bean.getDencryBankPwd());
            if (StringUtils.isEmpty(encrypwd)) {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("系统环境异常,中信密码加密失败");
                return 0;
            }
            List<String> notProxys = new ArrayList<>();
            notProxys.add("123.206.104.35:4998");
            //设置代理ip
            HttpHost proxy = getHttpProxy(false,notProxys);
            logger.info("cuserId=" + bean.getCuserId() + ";proxy_ip==" + proxy);
            loginContext = createContextProxy(new BasicCookieStore(), proxy);
            Map<String, String> requestHeader = loginContext.getHeaders();

            String url = "https://creditcard.ecitic.com/citiccard/ucweb/entry.do";
            requestHeader.put("Host", "creditcard.ecitic.com");
            requestHeader.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            requestHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                requestHeader.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp+","+proxy.getHostName());
            }
            errHtml = httpGet(url, loginContext);

            //是否需要图片验证码
            requestHeader.put("Accept", "application/json, text/javascript, */*; q=0.01");
            requestHeader.put("Content-Type", "application/json");
            url = "https://creditcard.ecitic.com/citiccard/ucweb/initvalidcode.do?date=" + System.currentTimeMillis();
            errHtml = httpGet(url, loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";是否进行图片验证>>>>" + errHtml);
            JSONObject jsonData = new JSONObject();
            jsonData.put("isBord",false);
            jsonData.put("loginType","01");
            jsonData.put("page","new");
            jsonData.put("phone",bean.getDencryIdcard());
            jsonData.put("source","PC");
            jsonData.put("memCode", encrypwd);
            jsonData.put("valiCode",bean.getBankRand());
            String retMsg = "";
            //中信识别验证码登录
            loop:for (int i = 0; i < yzNum; i++) {
                requestHeader.put("Referer", "https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
                requestHeader.put("Accept", "image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
                //获取图片验证码
                url = "https://creditcard.ecitic.com/citiccard/ucweb/newvalicode.do";
                String imgBase64 = getYzm(url, bean.getCuserId(), loginContext);
                if (i==yzNum-1){
                    bean.setBankRand(imgBase64);
                    bean.setBusiErrCode(BillConstant.needimg);
                    bean.setBusiErrDesc("自动识别图片失败,请手动输入!");
                    cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
                    cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy",proxy);
                    return 0;
                }
                if (StringUtils.isEmpty(imgBase64)){//获取验证码失败
                    continue;
                }
                String bankrand = distinguishCode(bean,imgBase64,bean.getBankId(),"1");
                if (!StringUtils.isEmpty(bankrand)){
                    jsonData.put("valiCode",bankrand);
                    String jsStr = jsonData.toString();
                    url = "https://creditcard.ecitic.com/citiccard/ucweb/login.do?date="+System.currentTimeMillis();
                    requestHeader.put("Accept", "application/json, text/javascript, */*; q=0.01");
                    requestHeader.put("Content-Type","application/json");
                    requestHeader.put("Referer", "https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
                    String contentType = "application/json";
                    errHtml = httpRequestJson(url,jsStr,contentType,loginContext);
                    logger.info("cuserId=="+bean.getCuserId()+";登录参数>>>bankPwd="+bean.getBankPwd()+";jsStr="+ jsStr+";中信银行登录结果页面>>>"+errHtml);
                    if (StringUtils.isEmpty(errHtml)){//请求失败,刷新页面
                        url = "https://creditcard.ecitic.com/citiccard/ucweb/entry.do";
                        requestHeader.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                        requestHeader.put("Content-Type", "application/x-www-form-urlencoded");
                        httpGet(url, loginContext);
                        continue;
                    }
                    JSONObject retObj = JSONObject.parseObject(errHtml);
                    if(retObj.containsKey("retMsg")){
                        retMsg = retObj.getString("retMsg");
                    }
                    if (retMsg.contains("图形验证码")){//验证码错误,重新识别
                        continue;
                    } else {
                        break loop;
                    }
                }
            }
            return loginResult(bean,errHtml,loginContext,cc,proxy);
        }catch (Exception e){
            logger.info(getClass().getSimpleName() + " cuserId:"+bean.getCuserId()+" 登录接口异常---"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
            return 0;
        }finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
    }

    /**
     * 用户输入验证码后登录接口
     * @param bean
     * @param cc
     * @return
     */
    public int loginAfter(Channel bean,MemCachedClient cc){
        LoginContext loginContext = null;
        try {
            BasicCookieStore zxCookieStore = (BasicCookieStore) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
            HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
            logger.info(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy" + "==" + proxy);
            if (zxCookieStore == null) {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重试");
                return 0;
            }

            loginContext = createContextProxy(zxCookieStore, proxy);
            //执行js,密码加密
            String encrypwd = getEncrypwd(bean.getDencryBankPwd());
            if (StringUtils.isEmpty(encrypwd)) {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("系统环境异常,中信密码加密失败");
                return 0;
            }
            String url = "https://creditcard.ecitic.com/citiccard/ucweb/login.do?date="+System.currentTimeMillis();
            loginContext.getHeaders().put("Accept", "application/json, text/javascript, */*; q=0.01");
            loginContext.getHeaders().put("Content-Type","application/json");
            loginContext.getHeaders().put("Referer", "https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
            loginContext.getHeaders().put("Host", "creditcard.ecitic.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                loginContext.getHeaders().put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            String contentType = "application/json";
            JSONObject jsonData = new JSONObject();
            jsonData.put("isBord",false);
            jsonData.put("loginType","01");
            jsonData.put("page","new");
            jsonData.put("phone",bean.getDencryIdcard());
            jsonData.put("source","PC");
            jsonData.put("memCode", encrypwd);
            jsonData.put("valiCode",bean.getBankRand());
            String jsStr = jsonData.toString();
            String errHtml = httpRequestJson(url,jsStr,contentType,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";登录参数>>>bankPwd="+bean.getBankPwd()+";jsStr="+ jsStr+";中信银行登录结果页面>>>"+errHtml);
            return loginResult(bean,errHtml,loginContext,cc,proxy);
        }catch (Exception e){
            logger.error("cuserId="+bean.getCuserId()+":"+getClass().getSimpleName() + "---异常", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
            return 0;
        }finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
    }

    private int loginResult(Channel bean, String errHtml,LoginContext loginContext,MemCachedClient cc,HttpHost proxy){
        if (StringUtils.isEmpty(errHtml)){
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("网络繁忙!请稍后再试");
            return 0;
        }
        JSONObject retObj = JSONObject.parseObject(errHtml);
        String retMsg = "";
        String rtnCode = "";
        String needValiCode = "";
        if(retObj.containsKey("retMsg")){
            retMsg = retObj.getString("retMsg");
        }
        if(retObj.containsKey("rtnCode")){
            rtnCode = retObj.getString("rtnCode");
        }
        if (retObj.containsKey("needValiCode")){
            needValiCode = retObj.getString("needValiCode");
        }
        if(!"0000000".equals(rtnCode)){
            logger.info("cuserId="+bean.getCuserId()+";"+retMsg);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc(retMsg);
            return 0;
        }else{
            cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
            cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy",proxy);
            logger.info(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy"+"=="+proxy);
            if (needValiCode.equals("true")){
                bean.setBusiErrCode(BillConstant.needmsg);
                bean.setBusiErrDesc("需要短信验证");
                bean.setPhoneCode("true");
                return 0;
            }
            String cookieStr = loginContext.getCookieStr();
            logger.info("bankSessionId=="+cookieStr);
            bean.setBankSessionId(cookieStr);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("登录成功,开始解析账单");
            return 1;
        }
    }

    /**
     * 发送短信验证码
     * @param bean 参数对象
     */
    public int getSms(Channel bean,MemCachedClient cc){
        LoginContext loginContext = null;
        try{
            Map<String,String> params = new HashMap<>();
            BasicCookieStore zxCookieStore = (BasicCookieStore) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
            HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
            logger.info(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy"+"=="+proxy);
            if (zxCookieStore==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重试");
                return 0;
            }
            loginContext = createContextProxy(zxCookieStore,proxy);
            Map<String,String> requestHeader = loginContext.getHeaders();
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                requestHeader.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            requestHeader.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            requestHeader.put("Host","creditcard.ecitic.com");
            requestHeader.put("Referer","https://creditcard.ecitic.com/citiccard/ucweb/entry.do");
            requestHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");

            String url = "https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do";
            httpGet(url,loginContext);

            url = "https://creditcard.ecitic.com/citiccard/ucweb/sendSms.do?date="+System.currentTimeMillis();
            requestHeader.put("Accept","application/json, text/javascript, */*; q=0.01");
            requestHeader.put("Content-Type","multipart/form-data");
            requestHeader.put("Referer","https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do");
            String errHtml = httpPost(url,params,loginContext);
            logger.info(bean.getCuserId()+" 短信发送结果:"+errHtml);
            if (StringUtils.isEmpty(errHtml)){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("网络繁忙,请稍后再试!");
                return 0;
            }
            JSONObject retObj = JSONObject.parseObject(errHtml);
            //短信验证码已发送到您的手机：159 **** 1216，请注意查收）
            String rtnCode = retObj.getString("rtnCode");
            String phone = retObj.getString("phone");
            String rtnMsg = retObj.getString("rtnMsg");
            if(rtnCode.equals("0000000")){
                logger.info("cuserId="+bean.getCuserId()+";短信验证码已发送到您的手机："+phone+"，请注意查收");
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc("短信验证码发送成功");
                bean.setPhoneCode(phone);
                cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
                cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy",proxy);
                return 1;
            }else{
                logger.info(bean.getCuserId()+",errMsg="+rtnMsg);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc(rtnMsg);
                return 0;
            }
        }catch (Exception e){
            logger.error("cuserId="+bean.getCuserId()+":"+getClass().getSimpleName() + "---异常", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
            return 0;
        }finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }

    }

    /**
     * 检测短信验证码
     */
    public int checkSms(Channel bean,MemCachedClient cc){
        LoginContext loginContext = null;
        try{
            JSONObject smsJson = new JSONObject();
            smsJson.put("smsCode",bean.getBankRand());
            String url = "https://creditcard.ecitic.com/citiccard/ucweb/checkSms.do?date="+System.currentTimeMillis();
            BasicCookieStore zxCookieStore = (BasicCookieStore) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore");
            HttpHost proxy = (HttpHost) cc.get(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy");
            logger.info(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy"+"=="+proxy);
            if (zxCookieStore==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重试");
                return 0;
            }
            loginContext = createContextProxy(zxCookieStore,proxy);
            Map<String,String> requestHeader = loginContext.getHeaders();
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                requestHeader.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            requestHeader.put("Host","creditcard.ecitic.com");
            requestHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.3 Safari/537.36");
            requestHeader.put("Accept","application/json, text/javascript, */*; q=0.01");
            requestHeader.put("Content-Type","application/json");
            requestHeader.put("Referer","https://creditcard.ecitic.com/citiccard/ucweb/sendSmsInit.do");
            String contentType = "application/json";
            String jsStr = smsJson.toString();
            String errHtml = httpRequestJson(url,jsStr,contentType,loginContext);
            logger.info(bean.getCuserId()+" 短信验证结果:"+errHtml);
            if (StringUtils.isEmpty(errHtml)){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("网络繁忙!请稍后再试");
                return 0;
            }
            JSONObject retObj = JSONObject.parseObject(errHtml);
            //短信验证码校验成功
            String rtnCode = retObj.getString("rtnCode");
            String rtnMsg = retObj.getString("rtnMsg");
            if(!rtnCode.equals("0000000")){
                logger.info("cuserId=="+bean.getCuserId()+",短信验证码检测失败:"+rtnMsg);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc(rtnMsg);
                return 0;
            }
            bean.setBusiErrCode(BillConstant.success);
            bean.setBusiErrDesc("登录成功,开始解析账单");
            String cookieStr = loginContext.getCookieStr();
            logger.info("cuserId="+bean.getCuserId()+"bankSessionId=="+cookieStr);
            bean.setBankSessionId(cookieStr);
            cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_cookieStore",loginContext.getCookieStore());
            cc.set(bean.getCuserId() + bean.getBankId() + "zhongxin_proxy",proxy);
            return 1;
        }catch (Exception e){
            logger.error("cuserId="+bean.getCuserId()+":"+getClass().getSimpleName() + "---异常", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
            return 0;
        }finally {
            if (loginContext!=null){
                loginContext.close();
            }
        }
    }

    /**
     * 中信银行执行task接口(有时需要短信验证)
     * @param bean bean对象
     * @param client drpc对象
     * @return 执行结果 0:失败 1:成功
     */
    public int taskReceve(Channel bean, IDrpcClient client, MemCachedClient cc){
        //第二次请求携带图片验证码
        String bankRand = bean.getBankRand();
        if (StringUtils.isEmpty(bankRand)){
            client.execute(Constant.HSK_BILL_BANK,new DrpcRequest("bank", "billTaskConsume", bean));
        }
        int ret = dencrypt_data(bean);//参数解密
        if (ret==0){
           return ret;
        }
        if (bean.getDencryIdcard().length()>11){
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("中信导账单改版,请使用手机号码重新导入账单");
            return 0;
        }
        int code;
        if (StringUtils.isEmpty(bankRand)){//第一次进入登录接口,调用登录前检查,看是否需要图片验证码
            code = login(bean,cc);
        } else {
            code = loginAfter(bean,cc);
        }
        if (2==bean.getBusiErrCode()){//需要短信验证
            bean.setCode("3");
        } else if (3==bean.getBusiErrCode()){//需要图片验证码
            bean.setCode("2");
        }else if (1==bean.getBusiErrCode()){//登录成功
            bean.setCode("1");
        }else{//登录失败
            bean.setCode("0");
        }
        changeCode(bean,client);
        return code;
    }

    public String getEncrypwd(String pwd){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        String md5Js = getClass().getResource("/zhongxin/md5.js").getFile();
        FileReader reader;// 执行指定脚本
        String encrypwd = "";//加密后返回
        String func = "hex_md5";//加密function名
        try {
            reader = new FileReader(md5Js);
            engine.eval(reader);
            if(engine instanceof Invocable) {
                Invocable invoke = (Invocable)engine;    //调用merge方法，并传入两个参数
                encrypwd = (String)invoke.invokeFunction(func, pwd);
                logger.info("encrypwd = " + encrypwd);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            logger.error(getClass().getSimpleName() + " ----FileNotFoundException 异常", e);
            return null;
        }catch (ScriptException e) {
            logger.error(getClass().getSimpleName() + " ----jsScriptException 异常", e);
            return null;
        }catch (NoSuchMethodException e) {
            logger.error(getClass().getSimpleName() + " ----js method not found 异常", e);
            return null;
        }catch (IOException e) {
            logger.error(getClass().getSimpleName() + " ----IO 异常", e);
            return null;
        }
        return encrypwd;
    }
}
