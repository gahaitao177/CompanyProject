package com.caiyi.financial.nirvana.bill.bank;

import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import java.util.Map;

/**
 * Created by ljl on 2017/2/20.
 */
public class HuaXiaBank extends AbstractHttpService {

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
            Map<String,String> headers = loginContext.getHeaders();
            headers.put("Host","sbank.hxb.com.cn");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            String url = "https://sbank.hxb.com.cn/easybanking/jsp/indexComm.jsp";
            headers.put("Host","sbank.hxb.com.cn");
            headers.put("Accept","text/html, application/xhtml+xml, */*");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            errHtml = httpGet(url,loginContext);
            url = "https://sbank.hxb.com.cn/easybanking/jsp/login/login.jsp";
            errHtml = httpGet(url,loginContext);
            if (errHtml.contains("SetPasswordEncryptionKey")){
                String passEncryText= errHtml.substring(errHtml.indexOf("SetPasswordEncryptionKey"));
                passEncryText = passEncryText.substring(0,passEncryText.indexOf(";"));
                String pubkey = passEncryText.split("\\,")[0].replaceAll("SetPasswordEncryptionKey","")
                        .replaceAll("[\\'\\(]","");
                boolean success = hackPassword(bean,"",pubkey);
                if (success){
                    String passwordHack = bean.getPasswordHackStr();
                    Element formEle = Jsoup.parse(errHtml).getElementById("loginForm");
                    Map<String,String> params = setFormParams(formEle);
                    params.put("realpass",passwordHack);
                    headers.put("Accept","image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
                    url = "https://sbank.hxb.com.cn/easybanking/validateservlet";
                    String yzm = getYzm(url,bean.getCuserId(),loginContext);
                    cc.set(bean.getCuserId() + bean.getBankId() + "huaXia_cookieStore",loginContext.getCookieStore());
                    cc.set(bean.getCuserId() + bean.getBankId() + "huaXia_params",params);
                    return yzm;
                }else{
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
                }
            }else{
                logger.info("cuserId="+bean.getCuserId()+"---获取页面pubkey出错,errHtml>>"+errHtml);
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("网络异常,请稍后");
            }
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 获取图片验证码异常---", e);
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
     * 登录密码控件加密
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int login(Channel bean, MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try{
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "huaXia_cookieStore");
            Object paramsObj = cc.get(bean.getCuserId() + bean.getBankId() + "huaXia_params");
            if (cookieStore==null || paramsObj==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            //登录url
            String url = "https://sbank.hxb.com.cn/easybanking/login.do?";
            Map<String,String> params = (Map<String, String>) paramsObj;

            params.put("customerMacAddr","74-d4-35-ce-0a-97");
            params.put("loginWay","3");
            params.put("idType","10");//证件类型(身份证号)
            params.put("idNo",bean.getDencryIdcard());//证件号码
            params.put("validateNo",bean.getBankRand());
            errHtml = httpPost(url,params,loginContext);
            System.out.println(errHtml);
            String errMsg = "";
            Element errEle = Jsoup.parse(errHtml).getElementById("mess");
            if (errEle!=null){
                errMsg = errEle.text().replaceAll("\\s*","");
            }
            if (StringUtils.isEmpty(errMsg)){
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc("登录成功,开始解析账单.....");
            }else{
                logger.info("cuserId=="+bean.getCuserId()+";errMsg>>>"+errMsg);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc(errMsg);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.info("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 登录接口异常---"+errHtml, e);
            bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
            bean.setBusiErrCode(-1);
        }finally {
            if (loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }
}
