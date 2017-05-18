package com.caiyi.financial.nirvana.bill.bank;

import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.bill.util.BillConstant;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.ForeheadRecord;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HpClientUtil;
import com.hsk.cardUtil.HttpClientHelper;
import com.hsk.cardUtil.HttpResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZhouPingHua
 * Date：2017/3/7.
 */
public class JiaoTongBank extends AbstractHttpService {


    /***
     * 获取交通银行提额登录
     * @param bean
     * @param cc
     */
    public void loginSendSms(Channel bean, MemCachedClient cc) {
        try {
            int errCode = recognize_JiaoTong(bean, cc);
            logger.info("-----------errCode=" + errCode + "----------------");
            if (errCode == 1) {
                bean.setBusiErrCode(BillConstant.needmsg);//需要短信验证码
                bean.setBusiErrDesc("登录成功，提额需要短信验证");
                bean.setPhoneCode("true");
            } else {
                bean.setBusiErrCode(BillConstant.fail);
            }
        } catch (Exception e) {
            logger.error("cuserId:" + bean.getCuserId() + getClass().getSimpleName() + " ---", e);
        }

    }

    /***
     * 获取交通银行提额登录
     * @param bean
     */
    public int recognize_JiaoTong(Channel bean, MemCachedClient cc) {
        String errHtml = "";
        try {
            int ret = dencrypt_data(bean);//参数解密
            if (ret == 0) {
                return ret;
            }
            String idcard = bean.getDencryIdcard();
            String bankpwd = bean.getDencryBankPwd();
            Map<String, Object> context = new HashMap<>();
            String errText = "";

            CloseableHttpClient hc = HttpClients.createDefault();
            CookieStore cookieStore = new BasicCookieStore();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String, String> headers = new HashMap<>();
            headers.put("Host", "creditcardapp.bankcomm.com");
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36");

            String userIp = bean.getIpAddr();
            if (null != userIp && !userIp.equals("")) {
                headers.put("X-Forwarded-For", userIp);
                logger.info(bean.getTaskid() + " userip=" + userIp);
            }
            Map<String, String> datas = new HashMap<String, String>();

            String url = "https://creditcardapp.bankcomm.com/idm/sso/login.html?service=https://creditcardapp.bankcomm.com/member/shiro-cas";
            errHtml = HpClientUtil.httpGet(url, headers, hc, localContext, "utf-8", false, requestConfig);
            Element loginForm = Jsoup.parse(errHtml).getElementById("tabCardNoForm");
            if (loginForm == null) {
                logger.info("itaskid=" + bean.getTaskid() + "获取账单失败，失败原因[登录失败-网络原因,无法打开网站,请稍后再试！]errHml>>" + errHtml);
                bean.setBusiErrDesc("网络原因,当前未获取网站响应,请稍后再试！");
                return 0;
            }

            setFormParams(loginForm, datas);
            headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
            url = "https://creditcardapp.bankcomm.com/idm/sso/keyboards.json?_=" + System.currentTimeMillis();
            errHtml = HpClientUtil.httpGet(url, headers, hc, localContext, "utf-8", false, requestConfig);
            errHtml = isTrueHtml(bean,url,errHtml,context,hc,headers,localContext,requestConfig);
            if (!(boolean) context.get("isTrueHtml")) {
                bean.setBusiErrDesc("网络问题,请稍后重试!");
                return 0;
            }
            org.json.JSONObject keyJson = new org.json.JSONObject(errHtml);
            JSONArray keyarray = keyJson.getJSONArray("keys");//图片验证码随机码参数,0-9按顺序对应
            String[] keys = new String[keyarray.length()];
            for (int k = 0; k < keyarray.length(); k++) {
                String key = keyarray.getString(k);
                keys[k] = key;
            }
            String pwdSeq = "";
            int[] val = new int[bankpwd.length()];
            for (int i = 0; i < bankpwd.length(); i++) {
                String s2 = bankpwd.substring(i, i + 1);
                val[i] = Integer.parseInt(s2);
            }
            for (int i = 0; i < val.length; i++) {
                int idx = val[i];
                pwdSeq += keys[idx] + "|";
            }
            pwdSeq = pwdSeq.substring(0, pwdSeq.length() - 1);
            datas.put("username", idcard);
            datas.put("realname", idcard);
            datas.put("password", bankpwd);
            datas.put("passwordseq", pwdSeq);
            datas.put("accept", "1");
            url = "https://creditcardapp.bankcomm.com/idm/sso/auth.html";
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Referer", "https://creditcardapp.bankcomm.com/idm/sso/login.html?service=https://creditcardapp.bankcomm.com/member/shiro-cas");
            errHtml = httpPost(url, hc, localContext, "UTF-8", requestConfig, headers, datas);
            String location = "";
            try {
                //如果返回结果为跳转路径，重定向
                while (errHtml.contains("302") || errHtml.contains("301")) {
                    location = errHtml.split(";")[1];
                    errHtml = HpClientUtil.httpGet(location, headers, hc, localContext, "utf-8", false, requestConfig);
                }
            } catch (Exception e) {
                logger.info("taskId==" + bean.getTaskid() + ";location>>" + location);
                bean.setBusiErrDesc("网络问题,请稍后重试!");
                return 0;
            }
            logger.info(bean.getTaskid() + " 交通银行登录跳转>>>>>>>>>>>>" + location);
            Document welDoc = Jsoup.parse(errHtml);
            //由于您长期未登录我行信用卡官方网站
            if (errHtml.contains("由于您长期未登录我行信用卡官方网站")) {
                bean.setBusiErrDesc("由于您长期未登录我行信用卡官方网站,需要进行验证,请到交通信用卡官网登录验证");
                return 0;
            }
            //尊敬的用户，为了您账户安全，请您完成验证，验证成功后即可完成登录，感谢配合!
            if (errHtml.contains("为了您账户安全，请您完成验证") || errHtml.contains("验证成功后即可完成登录")) {
                bean.setBusiErrDesc("由于您长期未登录我行信用卡官方网站,需要进行验证,请到交通信用卡官网登录验证");
                return 0;
            }
            Elements errorDivs = welDoc.select("div.errormsg");
            Element errorDiv = null;
            if (errorDivs != null && errorDivs.size() > 0)
                errorDiv = errorDivs.get(0);
            if (errorDiv != null) {
                errText = errorDiv.text().trim();
            }
            if (!(errText == null || "".equals(errText) || errText.trim().length() == 0)) {
                logger.info("获取账单失败，失败原因[登录失败-" + errText + "]");
                if (errText.contains("您输入的查询密码和信用卡号不匹配")) {
                    bean.setBusiErrDesc(errText);
                    return 0;
                }
                bean.setBusiErrDesc(errText);
                return 0;
            }
            Elements titleEles = welDoc.select("div.header-title");
            Element headerTitle = null;
            if (titleEles != null && titleEles.size() > 0) {
                headerTitle = titleEles.get(0);
            }
            String title = "";
            if (headerTitle != null) {
                title = headerTitle.text().trim();
            }
            if (StringUtils.isEmpty(title)) {
                Element formEle = welDoc.getElementById("form");
                if (formEle == null) {
                    bean.setBusiErrDesc("网络问题,请稍后重试!");
                    return 0;
                }
                datas.clear();
                setFormParams(formEle, datas);
                String action = formEle.attr("action");
                errHtml = HpClientUtil.httpPost(action, hc, localContext, "UTF-8", requestConfig, headers, datas);
                Elements titles = Jsoup.parse(errHtml).select("div.header-title");
                if (titles == null || titles.size() < 1) {
                    bean.setBusiErrDesc("网络问题,请稍后重试!");
                    return 0;
                }
                title = titles.first().text();
            }
            logger.info("跳转后页面title>>>>>>>>>>" + title);
            if (!title.contains("轻松账务")) {
                if (title.contains("查询密码重置")) {
                    bean.setBusiErrDesc("为了您的账户安全,请先去重置查询密码!");
                    return 0;
                }
                if (title.contains("信用卡激活")) {
                    bean.setBusiErrDesc("信用卡未激活,请先去交行柜面申请激活!");
                    return 0;
                } else if (title.contains("卡片激活")) {
                    bean.setBusiErrDesc("信用卡未激活,请先去交行柜面或到交行信用卡中心网站激活!");
                    return 0;
                } else {
                    url = "https://creditcardapp.bankcomm.com/member/member/service/billing/index.html";
                    errHtml = HpClientUtil.httpGet(location, headers, hc, localContext, "utf-8", false, requestConfig);
                    isTrueHtml(bean,url,errHtml,context,hc,headers,localContext,requestConfig);
                    if (!(boolean) context.get("isTrueHtml")) {
                        bean.setBusiErrDesc("网络问题,请稍后重试!");
                        return 0;
                    }
                }
            }
            cc.set(bean.getCuserId() + bean.getBankId() + "jiaotong_cookieStore",localContext.getAttribute("http.cookie-store"), 3600000);
            cc.set(bean.getCuserId() + bean.getBankId() + "jiaotong_idcard", bean.getDencryIdcard(), 3600000);
            return 1;
        } catch (Exception e) {
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("系统繁忙,请稍后重试！");
            logger.info(bean.getTaskid() + "错误页面=" + errHtml);
        }
        return 0;
    }


    /**
     * 发送短信验证码
     *
     * @param cc
     * @param bean
     */
    public int getSms(Channel bean, MemCachedClient cc) {
        CookieStore cookieStore = null;
        String idcard = "";
        try {
            Object jiaotong_cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "jiaotong_cookieStore");
            Object jiaotong_idcard = cc.get(bean.getCuserId() + bean.getBankId() + "jiaotong_idcard");

            if (jiaotong_cookieStore == null || jiaotong_idcard == null) {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
                return 0;
            } else {
                cookieStore = (CookieStore)jiaotong_cookieStore;
                idcard = (String) jiaotong_idcard;
            }
            CloseableHttpClient hc = HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String, String> headers = getBasicHeader();
            headers.put("Host", "creditcardapp.bankcomm.com");

            //额度提升详情页面  0能提升临时额度 1不能   0能提升固定额度 1不能
            String type = bean.getType();
            String idcardStar = idcard.substring(0, 4);   //信用卡前四位
            String idcardEnd = idcard.substring(idcard.length() - 4);  //信用卡后四位
            headers.put("Referer", "https://creditcardapp.bankcomm.com/member/member/adjust/limit.html?cardNo=" + idcardStar + " **** **** " + idcardEnd);
            String errorContent = "";
            if (type.equals("3")) {  //临时额度验证码发送
                //临时额度调整
                String url = "https://creditcardapp.bankcomm.com/member/member/payment/token.json?cardNo=" + idcardStar + "+****+****+" + idcardEnd;
                errorContent = HpClientUtil.httpGet(url, headers, hc, localContext, "utf-8", false, requestConfig);
            }
            Boolean success = false;
            try {
                JSONObject jsonObj = new JSONObject(errorContent);
                if (jsonObj.has("status") && jsonObj.has("sts") && jsonObj.getString("status").equals("true") && jsonObj.getString("sts").equals("true")) {
                    success = true;
                }
            } catch (Exception e) {
                logger.info("短信结果页面无法转换成json对象：errorContent=" + errorContent);
            }
            //判断短信是否发送成功
            if (success) {
                logger.info("cuserId" + bean.getCuserId() + "交通提额短信验证码发送成功>>>>>>>>>>>>>>>>>>>>>>>>>");
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc("短信验证码发送成功");
                cc.set(bean.getCuserId() + bean.getBankId() + "jiaotong_cookieStore",localContext.getAttribute("http.cookie-store"), 3600000);
                return 1;
            } else {
                logger.info("cuserId" + bean.getCuserId() + "交通提额短信验证码获取失败>>>>>>" + errorContent);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("短信验证码发送失败");
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
     * 验证短信验证码，提交提额
     *
     * @param cc
     * @param bean
     * @param client
     */
    public int checkSms(Channel bean, MemCachedClient cc, IDrpcClient client) {
        CookieStore cookieStore = null;
        String idcard = "";
        try {
            Object jiaotong_cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "jiaotong_cookieStore");
            Object jiaotong_idcard = cc.get(bean.getCuserId() + bean.getBankId() + "jiaotong_idcard");

            if (jiaotong_cookieStore == null || jiaotong_idcard == null) {
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存失效,请重新导入或更新账单");
                return 0;
            } else {
                cookieStore = (CookieStore)jiaotong_cookieStore;
                idcard = (String) jiaotong_idcard;
            }
            CloseableHttpClient hc = HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            // 设置请求和传输超时时间
            localContext.setAttribute("http.cookie-store", cookieStore);
            RequestConfig.custom().setConnectTimeout(30000);
            RequestConfig.custom().setSocketTimeout(30000);
            RequestConfig.custom().setConnectionRequestTimeout(30000);
            RequestConfig requestConfig = RequestConfig.custom().build();
            Map<String, String> headers = getBasicHeader();
            headers.put("Host", "creditcardapp.bankcomm.com");

            String idcardStar = idcard.substring(0, 4);   //信用卡前四位
            String idcardEnd = idcard.substring(idcard.length() - 4);  //信用卡后四位
            headers.put("Referer", "https://creditcardapp.bankcomm.com/member/member/service/raiselimit/raisesubmit.html");

            //额度提升详情页面  0能提升临时额度 1不能   0能提升固定额度 1不能
            String preLimitRMB = "";  //原额度
            String avaTmpLmt = "";  //卡片最大可提升额度
//            String raiseEndDate = getEndDate();   //临时额度到期时间
            String SetLimit = bean.getUptomoney();  //临时额度申请金额
            String MsgCode = bean.getBankRand();//短信验证码
            ForeheadRecord foreheadRecord = new ForeheadRecord();
            StringBuilder url = new StringBuilder();
            String urlstr = "https://creditcardapp.bankcomm.com/member/member/limit/info.json?cardNo=" + idcardStar + "+****+****+" + idcardEnd;
            headers.put("Referer", "https://creditcardapp.bankcomm.com/member/member/limit/manage/index.html?cardNo="+idcardStar+" **** **** "+idcardEnd);
            String errorContent = HpClientUtil.httpGet(urlstr, headers, hc, localContext, "utf-8", false, requestConfig);

            //获取原临时额度值
            try {
                JSONObject jsonObj = new JSONObject(errorContent);
                if (jsonObj.has("crdLmt")) {
                    preLimitRMB = jsonObj.getString("crdLmt");
                }
                if (jsonObj.has("avaTmpLmt")) {
                    avaTmpLmt = jsonObj.getString("avaTmpLmt");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("结果页面无法转换成json对象：errHtml=" + errorContent);
            }

            urlstr = "https://creditcardapp.bankcomm.com/member/member/adjust/limit.html?cardNo="+idcardStar+"%20****%20****%20"+idcardEnd;
            errorContent = HpClientUtil.httpGet(urlstr, headers, hc, localContext, "utf-8", false, requestConfig);
            Element raiseForm = Jsoup.parse(errorContent).getElementById("raiseForm");
            Map datas = new HashMap();
            setFormParams(raiseForm,datas);

            String type = bean.getType();
            if (type.equals("3")) {
                urlstr = "https://creditcardapp.bankcomm.com/member/member/service/raiselimit/raisesubmit.html";
                datas.put("raiseReason","03");
                datas.put("preLimitRMB",preLimitRMB);
                datas.put("expectRaise",SetLimit);
                datas.put("raiseReasonOther","");
                datas.put("token",MsgCode);
                errorContent = httpPost(urlstr, hc, localContext, "UTF-8", requestConfig, headers, datas);

                //临时额度调整
                if (errorContent != null && errorContent.contains("您的临时额度已完成调整")) {
                    logger.info("cuserId" + bean.getCuserId() + "交通临额提升申请成功>>>>>>>>>>>>>>>>>>>>>>>>>");
                    bean.setBusiErrCode(BillConstant.success);
                    bean.setBusiErrDesc("临时额度提额申请成功");

                    //提额记录
                    foreheadRecord.setFrid(UUID.randomUUID().toString());
                    foreheadRecord.setIsprofix(1);
                    foreheadRecord.setIsstate(0);
                    foreheadRecord.setIbillid(bean.getBillId());
                    if (preLimitRMB != null && !preLimitRMB.equals("")) {
                        foreheadRecord.setOldlimit(Double.parseDouble(preLimitRMB));
                    }
                    if (SetLimit != null && !SetLimit.equals("")) {
                        foreheadRecord.setSetlimit(Double.parseDouble(SetLimit));
                    }
                    client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("CardQuotaBolt", "saveForeheadRecord", foreheadRecord));


                    //更新bankbill表数据
                    BankBillDto bankBillDto = new BankBillDto();
                    bankBillDto.setIbillid(Integer.parseInt(bean.getBillId()));
                    bankBillDto.setItotalquota(Double.parseDouble(SetLimit));
                    bankBillDto.setNowzeroquota(SetLimit);
                    //计算该卡是否能继续提额
                    try {
                        if (null == avaTmpLmt || avaTmpLmt.equals("")) {
                            bankBillDto.setIsZero(1);
                        } else {
                            Double liftMeasure = Double.parseDouble(SetLimit) - Double.parseDouble(preLimitRMB);
                            if (Double.parseDouble(avaTmpLmt) > liftMeasure) {
                                bankBillDto.setIsZero(0);
                            } else {
                                bankBillDto.setIsZero(1);
                            }
                        }
                    } catch (Exception e) {
                        bankBillDto.setIsZero(1);
                    }
                    client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("CardQuotaBolt", "updateByPrimaryKeySelective", bankBillDto));
                    return 0;
                } else {
                    logger.info("cuserId" + bean.getCuserId() + "交通临额提升申请失败>>>>>>" + errorContent);
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("交通临额提升失败");

                    //提额记录
                    foreheadRecord.setFrid(UUID.randomUUID().toString());
                    foreheadRecord.setIsprofix(1);
                    foreheadRecord.setIsstate(1);
                    foreheadRecord.setIbillid(bean.getBillId());
                    if (preLimitRMB != null && !preLimitRMB.equals("")) {
                        foreheadRecord.setOldlimit(Double.parseDouble(preLimitRMB));
                    } 
                    if (SetLimit != null && !SetLimit.equals("")) {
                        foreheadRecord.setSetlimit(Double.parseDouble(SetLimit));
                    }
                    client.execute(Constant.HSK_BILL_BANK, new DrpcRequest("CardQuotaBolt", "saveForeheadRecord", foreheadRecord));
                    return 0;
                }
            }

        } catch (Exception e) {
            logger.error("cuserId:" + bean.getCuserId() + getClass().getSimpleName() + " ---", e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("系统繁忙,请稍后重试");
        } finally {
            cc.delete(bean.getCuserId() + bean.getBankId() + "jiaotong_hc");
            cc.delete(bean.getCuserId() + bean.getBankId() + "jiaotong_localContext");
            cc.delete(bean.getCuserId() + bean.getBankId() + "jiaotong_requestConfig");
            cc.delete(bean.getCuserId() + bean.getBankId() + "jiaotong_idcard");
        }
        return 0;
    }


    /**
     * 设置表单参数
     */
    public void setFormParams(Element form, Map<String, String> data) throws Exception {
        Elements inputs = form.getElementsByTag("input");
        data.clear();
        for (Element input : inputs) {
            String name = input.attr("name");
            String value = input.attr("value");
            data.put(name, value);
        }
    }

    /**
     * 默认临时额度提升时间为90天
     */
    public String getEndDate() {

        Calendar cld = Calendar.getInstance();
        cld.setTime(new Date());

        //调用Calendar类中的add()，增加时间量
        cld.add(Calendar.DATE, 90);
        String month =cld.get(Calendar.MONTH)+"";
        String date = cld.get(Calendar.DATE)+"";
        if(month.length()<2){
            month = "0"+month;
        }
        if(date.length()<2){
            date = "0"+date;
        }
        return cld.get(Calendar.YEAR) + "-" + month + "-" + date;
    }


    public String isTrueHtml(Channel bean,String url,String errHtml,Map<String, Object> context,CloseableHttpClient hc,Map<String,String> headers,HttpContext localContext,RequestConfig requestConfig){
        context.put("isTrueHtml",false);
        String errMsg = getErrMsg(errHtml);
        if (errMsg.equals("会员登录")){//系统退出
            return errHtml;
        }
        int circle = 0;
        while((StringUtils.isEmpty(errHtml)||errMsg.contains("服务超时")
                ||errMsg.contains("请稍后再试")) && circle<3){
            circle++;
            errHtml = HpClientUtil.httpGet(url, headers, hc, localContext, "utf-8",false, requestConfig);
            errMsg = getErrMsg(errHtml);
            if (errMsg.equals("会员登录")){//系统退出
                return errHtml;
            }
        }
        if (StringUtils.isEmpty(errHtml) ||errMsg.contains("服务超时")
                ||errMsg.contains("请稍后再试")||errMsg.equals("会员登录")){
            logger.info("taskId=="+bean.getTaskid()+";错误页面>>"+errHtml);
            return errHtml;
        }
        context.put("isTrueHtml",true);
        return errHtml;
    }

    public String getErrMsg(String errHtml) {
        Document errDoc = Jsoup.parse(errHtml);
        Elements titleEles = errDoc.select("div.header-title");
        Element headerTitle = null;
        if (titleEles != null && titleEles.size() > 0) {
            headerTitle = titleEles.get(0);
        }
        String errMsg = "";
        if (headerTitle != null) {
            errMsg = headerTitle.text().trim();
        }
        if ("会员登录".equals(errMsg)) {//退出页面
            return errMsg;
        }
        Element divEle = errDoc.getElementById("contentWrapper");
        if (divEle != null) {
            errMsg = divEle.text();
        } else {
            Elements bodyEle = errDoc.select("body.normal");
            if (bodyEle != null && bodyEle.size() > 0) {
                errMsg = bodyEle.get(0).text();
            }
        }
        return errMsg;
    }

    /*
    * 代理设置，待用
    * */
    public CloseableHttpClient getProxyClient(Channel bean, boolean isProxy, List<String> notProxys) {
        CloseableHttpClient httpClient;
        try {
            if (proxyips != null && proxyips.length > 0) {
                List<String> proxyList = new ArrayList<>();
                proxyList.addAll(Arrays.asList(proxyips));
                proxyList.removeAll(notProxys);
                if (!isProxy) {
                    proxyList.add("default");
                }
                int x = new Random().nextInt(proxyList.size());
                String ipStr = proxyList.get(x);
                logger.info("proxy ip==" + ipStr);
                if (ipStr.equals("default")) {
                    httpClient = HttpClients.createDefault();
                    logger.info(bean.getTaskid() + " chose ip default x==1 ------");
                } else {
                    String[] proxyinfo = StringUtils.split(ipStr, ":");
                    if (proxyinfo.length == 2) {
                        String ip = proxyinfo[0];
                        int port = Integer.parseInt(proxyinfo[1]);
                        HttpHost proxy = new HttpHost(ip, port, "http");
                        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
                        httpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
                        logger.info(bean.getTaskid() + " chose ip------" + ipStr);
                        System.out.println(bean.getTaskid() + " chose ip------" + ipStr);
                        bean.setIpAddr(ipStr);
                    } else {
                        httpClient = HttpClients.createDefault();
                        logger.info(" chose ip default proxyinfo.length!=2 ------");
                        return httpClient;
                    }
                }
            } else {
                httpClient = HttpClients.createDefault();//HttpClients.createDefault();
                logger.info(bean.getTaskid() + " chose ip default proxyips=null------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(bean.getTaskid() + " getProxyClient error", e);
            httpClient = HttpClients.createDefault();
            logger.info(bean.getTaskid() + " chose ip default getProxyClient error ------");

        }
        return httpClient;
    }

    public static String httpPost(String url, CloseableHttpClient httpClient, HttpContext localContext, String encode, RequestConfig requestConfig, Map<String, String> headers, Map<String, String> parames) {
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

            CloseableHttpResponse e2 = httpClient.execute(httpPost, localContext);
            HttpEntity entity2 = e2.getEntity();
            int statusCode2 = e2.getStatusLine().getStatusCode();
            if(200 == statusCode2) {
                StringBuffer location = new StringBuffer();
                in = entity2.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(in, encode));

                String temp;
                while((temp = br.readLine()) != null) {
                    location.append(temp);
                    location.append("\n");
                }

                in.close();
                context = location.toString();
            } else if(302 == statusCode2 || 301 == statusCode2) {
                String location1 = e2.getFirstHeader("Location").getValue();
                context = statusCode2 + ";" + location1;
            }
        } catch (Exception var24) {
            var24.printStackTrace();
        } finally {
            if(httpPost != null) {
                httpPost.abort();
            }

            if(in != null) {
                try {
                    in.close();
                } catch (IOException var23) {
                    var23.printStackTrace();
                }
            }

        }

        return context;
    }
}
