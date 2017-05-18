package com.caiyi.financial.nirvana.bill.bank;

import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.bill.util.ExcelCreateUserAction;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * Created by ljl on 2017/1/19
 * 农业银行网银导入账单
 */
public class NongYeBank extends AbstractHttpService {

    /**
     * 获取图片验证码
     * @param bean 参数对象
     * @return base64格式的图片验证码
     */
    public String setYzm(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        try {
            String url = "https://perbank.abchina.com/SelfBank/LogonImageCodeAct.ebf";
            loginContext = createLoginContext(new BasicCookieStore());
            Map<String, String> headers = loginContext.getHeaders();
            headers.put("Host","perbank.abchina.com");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            headers.put("Accept","text/html, application/xhtml+xml, */*");
            String yzm = getYzm(url, bean.getCuserId(), loginContext);
            cc.set(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore",loginContext.getCookieStore());
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
     * 银行网银登录方法
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int login(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try{
            loginContext = createLoginContext(new BasicCookieStore());
            loginContext.setEncoding("gbk");
            loginContext.getHeaders().put("Host","perbank.abchina.com");
            loginContext.getHeaders().put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");

            String url = "https://perbank.abchina.com/SelfBank/netBank/zh_CN/entrance/logonSelf.aspx";
            errHtml = httpGet(url,loginContext);
            String script = Jsoup.parse(errHtml).select("script").html();
            if (script.contains("ts=")){
                script = script.substring(script.indexOf("ts="));
                String text = script.split("\\;")[0];
                String timespan = text.replaceAll("[^0-9]", "");
                boolean success = hackPassword(bean, timespan, "");
                if (success){
                    Element formEle = Jsoup.parse(errHtml).getElementById("form1");
                    Map<String,String> params = setFormParams(formEle);
                    params.put("pwdField",bean.getPasswordHackStr());
                    params.put("MachineCode",bean.getMachineCode());
                    params.put("MachineInfo",bean.getMachineInfo());
                    params.put("pwdFieldKeys","fromAcctPswd");
                    params.put("PICCODE","");
                    params.put("username",bean.getDencryIdcard());
                    String username = bean.getDencryIdcard();
                    String queryMode = "1";
                    boolean rs1 = username.matches("^[\\d]{16}$");
                    boolean rs2 = username.matches("^[\\d]{19}$");;
                    if (rs1||rs2){
                        queryMode="3";
                    }
                    if (username.length()==16&& username.matches("^16[0-9]{14}")){
                        queryMode="0";
                    }

                    if(username.length()== 18 && ExcelCreateUserAction.IDCardValidate(username)) {
                        queryMode="2";
                    }
                    params.put("QueryMode",queryMode);
                    url = "https://perbank.abchina.com/SelfBank/netBank/zh_CN/entrance/UPstartUpHtmlSessionAction.ebf";
                    errHtml = httpPost(url,params,loginContext);
                    logger.info("cuserId=="+bean.getCuserId()+";农行登录结果页面>>>"+errHtml);
                    cc.set(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore",loginContext.getCookieStore());
                    if (errHtml.contains("var errroCode")){
                        logger.info("cuserId=="+bean.getCuserId()+";农行登录页面错误:登录账号:"+bean.getDencryIdcard()
                                +";错误信息>>"+errHtml);
                        String errText = errHtml.substring(errHtml.indexOf("var errroCode"));
                        errText = errText.split("\\;")[0];
                        String errCode = errText.split("\\=")[1].replaceAll("\"","");
                        if (errCode.contains("CI17")){
                            //客户尚未注册该渠道服务[CI17]
                            bean.setBusiErrCode(BillConstant.fail);
                            bean.setBusiErrDesc("用户不存在,请进入农行官网用户名登录页面进行注册开通");
                        }else if (errCode.contains("CI19")){
                            //帐户未注册渠道服务或已注销[CI19]
                            bean.setBusiErrCode(BillConstant.fail);
                            bean.setBusiErrDesc("卡号不存在,或未开通网银");
                        }else if (errCode.contains("CI85")){
                            //密码错误
                            bean.setBusiErrCode(BillConstant.fail);
                            bean.setBusiErrDesc("密码错误");
                        }else if (errCode.contains("CI86")){
                            bean.setBusiErrCode(BillConstant.fail);
                            bean.setBusiErrDesc("密码已锁定");
                        }else if (errCode.contains("4102")){
                            bean.setBusiErrCode(0);
                            bean.setBusiErrDesc("获取加密密码串错误");
                        }else{
                            logger.info("cuserId>>"+bean.getCuserId()+";---农行登录未抓取到错误信息,errHtml>>"+errHtml);
                            bean.setBusiErrCode(BillConstant.fail);
                            bean.setBusiErrDesc("未识别的错误信息");
                        }
                        return 0;
                    }else if (errHtml.contains("用户名登录-短信验证")){//需要短信验证
                        String mobileText = errHtml.substring(errHtml.indexOf("var mobile"));
                        String phone = mobileText.split("\\;")[0].replaceAll("[^0-9]", "");
                        formEle = Jsoup.parse(errHtml).getElementById("SelfHelpVerifySmsForm");
                        String action = formEle.attr("action");
                        params = setFormParams(formEle);
                        params.put("action",action);
                        cc.set(bean.getCuserId() + bean.getBankId() + "nongYe_getSmsToken",phone);
                        cc.set(bean.getCuserId() + bean.getBankId() + "nongYeCheckSmsParams",params);
                        bean.setBusiErrCode(BillConstant.needmsg);
                        bean.setBusiErrDesc("需要短信验证");
                        return 0;
                    }else{
                        bean.setBusiErrCode(BillConstant.success);
                        bean.setBusiErrDesc("登录成功,开始解析账单");
                        bean.setBankSessionId(loginContext.getCookieStr());
                        return 1;
                    }
                }else{
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
                }
            }else{
                logger.info("cuserId="+bean.getCuserId()+"---获取页面timespan出错,errHtml>>"+errHtml);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("网络异常,请稍后");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行登录接口异常---"+errHtml, e);
            bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
            bean.setBusiErrCode(BillConstant.fail);
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
            Object tokenObj = cc.get(bean.getCuserId() + bean.getBankId() + "nongYe_getSmsToken");
            if (cookieStore==null || tokenObj==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            loginContext.setEncoding("GBK");
            String mobile = (String) tokenObj;
            String url = "https://perbank.abchina.com/SelfBank/netBank/zh_CN/SendSmsVerifyCodeAct.ebf?mobile=" + mobile + "&isValidMac=&sendType=17";
            errHtml = httpGet(url,loginContext);
            cc.set(bean.getCuserId() + bean.getBankId() + "nongye_cookieStore",loginContext.getCookieStore());
            String returnCode = "";
            if (errHtml.contains("ReturnCode")){
                returnCode = errHtml.substring(errHtml.indexOf("<ReturnCode>"),errHtml.indexOf("</ReturnCode>"))
                        .replaceAll("<ReturnCode>","").replaceAll("\\s*","");
            }
            if (returnCode.equals("0000")){
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc("短信验证码发送成功!");
                return 1;
            }else{
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("短信验证码发送失败");
                return 0;
            }
        } catch (Exception e){
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行获取短信验证码异常---"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
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
            Object paramsObj = cc.get(bean.getCuserId()+bean.getBankId() + "nongYeCheckSmsParams");
            if (cookieStore==null || paramsObj==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            loginContext.setEncoding("GBK");
            Map<String,String> params = (Map<String,String>) paramsObj;
            params.put("verifycode",bean.getBankRand());//短信验证码
            String action = params.get("action");
            String url = "https://perbank.abchina.com"+action;
            errHtml = httpPost(url,params,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";农行短信验证结果页面>>>>"+errHtml);
            Elements errEles = Jsoup.parse(errHtml).select("div.errorcont");
            String errMsg = "";
            for (Element element:errEles){
                Elements liEles = element.getElementsByTag("li");
                if (liEles!=null && liEles.size()>2){
                    String text = liEles.get(1).text();
                    String[] textArg = text.split("\\:");
                    if (textArg.length>1){
                        errMsg = textArg[1].replaceAll("\\s*","");
                    }else{
                        errMsg = textArg[0].replaceAll("\\s*","");
                    }
                }else{
                    errMsg = "未识别的错误信息";
                    logger.info("cuserId=="+bean.getCuserId()+";---短信验证,未获取到错误信息>>errHtml="+errHtml);
                }
            }
            logger.info("cuserId=="+bean.getCuserId()+";errMsg>>"+errMsg);
            if (!StringUtils.isEmpty(errMsg)){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc(errMsg);
                return 0;
            }else{
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc("登录成功,开始解析账单");
                bean.setBankSessionId(loginContext.getCookieStr());
                return 1;
            }
        } catch (Exception e){
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 农行获取短信验证码异常---"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("短信验证码检测异常");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }

}
