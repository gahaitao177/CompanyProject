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

import java.util.*;

/**
 * Created by ljl on 2017/2/7.
 * 浦发银行网银导入账单
 */
public class PuFaBank extends AbstractHttpService{
    
    /**
     * 浦发银行网银登录方法
     * @param bean 参数对象
     * @return 执行结果 0:失败 1:成功
     */
    public int login(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object spanObj = cc.get(bean.getCuserId() + bean.getBankId() + "puFa_getTimespan");
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "puFa_cookieStore");
            Object paramsObj = cc.get(bean.getCuserId() + bean.getBankId() + "puFaLoginParams");
            if (cookieStore==null || spanObj==null || paramsObj==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            String timespan = (String) spanObj;
            boolean success = hackPassword(bean,timespan,"");
            if (success){
                loginContext = createLoginContext((BasicCookieStore) cookieStore);
                loginContext.setEncoding("GBK");
                Map<String,String> headers = loginContext.getHeaders();
                String userIp = bean.getIpAddr();
                if (!StringUtils.isEmpty(userIp)) {
                    headers.put("X-Forwarded-For", userIp);
                    logger.info(bean.getCuserId() + " userip=" + userIp);
                }
                headers.put("Host","cardsonline.spdbccc.com.cn");
                headers.put("Accept","text/html, application/xhtml+xml, */*");
                headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
                Map<String,String> params = (Map<String, String>) paramsObj;
                params.put("SubmitType","2");
                params.put("IdType","01");
                params.put("IdNo",bean.getDencryIdcard());
                params.put("Password",bean.getPasswordHackStr());
                params.put("navigator","msie");
                params.put("Token",bean.getBankRand());
                String url = "https://cardsonline.spdbccc.com.cn/icard/login.do";
                errHtml = httpPost(url,params,loginContext);
                logger.info("cuserId=="+bean.getCuserId()+";浦发登录结果页面>>"+errHtml);
                if (errHtml.contains("var displayErr")){//错误提示
                    String script = errHtml.substring(errHtml.indexOf("var displayErr"));
                    String text = script.split("\\;")[0];
                    String errMsg = text.split("\\=")[1].replaceAll("\\'","")
                            .replaceAll("\\;","").replaceAll("\\s*","");
                    if (errMsg.contains("目前我们无法完成您的请求")){
                        errMsg = "账号不存在或信用卡查询密码输入有误";
                    }
                    bean.setBusiErrDesc(errMsg);
                    bean.setBusiErrCode(BillConstant.fail);
                }else if (errHtml.contains("目前我们无法完成您的请求")){
                    logger.info("cuserId="+bean.getCuserId()+"---登录错误页面异常,errHtml>>"+errHtml);
                    String errMsg = "账号不存在或信用卡查询密码输入有误";
                    bean.setBusiErrDesc(errMsg);
                    bean.setBusiErrCode(BillConstant.fail);
                }else if (errHtml.contains("网银登录动态验证码")){
                    bean.setBusiErrCode(BillConstant.needmsg);
                    bean.setBusiErrDesc("需要短信验证");
                    Element formEle = Jsoup.parse(errHtml).getElementsByAttributeValue("name","form1").first();
                    params = setFormParams(formEle);
                    cc.set(bean.getCuserId() + bean.getBankId() + "puFaGetSmsParams",params);
                }else{//浦发银行官网目前固定需要短信验证,如果没有出现,保存请求后页面,做请求异常处理
                    logger.info("cuserId="+bean.getCuserId()+"---进入短信验证页面错误,errHtml>>"+errHtml);
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc("网络异常,请稍后再试!");
                }
                cc.set(bean.getCuserId() + bean.getBankId() + "puFa_cookieStore",loginContext.getCookieStore());
            }else{
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("服务器环境异常,请稍后再试!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " ---浦发登录接口异常---"+errHtml, e);
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
     * 设置图片验证码
     * 本地测试保存图片到硬盘,测试或线上环境返回base64码
     * @param bean
     */
    public String setYzm(Channel bean,MemCachedClient cc) {
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            loginContext = createLoginContext(new BasicCookieStore());
            String url = "https://cardsonline.spdbccc.com.cn/icard/icardlogin.do?_locale=zh_CN";
            Map<String,String> headers = loginContext.getHeaders();
            loginContext.setEncoding("GBK");
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                loginContext.getHeaders().put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            headers.put("Host","cardsonline.spdbccc.com.cn");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            errHtml = httpGet(url,loginContext);
            Document loginDoc = Jsoup.parse(errHtml);
            Elements divEles = loginDoc.select("div.loginmainc");
            String timespan = "";
            for (Element divEle:divEles){
                String divHtml = divEle.html();
                if (divHtml.indexOf("writePassObject")!=-1){
                    divHtml = divHtml.substring(divHtml.indexOf("writePassObject"));
                    String passObject = divHtml.substring(0,divHtml.indexOf(")"));
                    String[] objVals = passObject.split(",");
                    timespan = objVals[objVals.length-2].replaceAll("\"","").replaceAll("\\s*","");
                }
            }
            if (!StringUtils.isEmpty(timespan)){
                headers.put("Host","cardsonline.spdbccc.com.cn");
                headers.put("Accept","image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
                headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
                String imgUrl = "https://cardsonline.spdbccc.com.cn/icard/CaptchaImg?date="+new Date().getTime();
                String yzm = getYzm(imgUrl, bean.getCuserId(), loginContext);
                Element formEle = Jsoup.parse(errHtml).getElementsByAttributeValue("name","form1").first();
                Map<String,String> params = setFormParams(formEle);;

                cc.set(bean.getCuserId() + bean.getBankId() + "puFa_getTimespan",timespan);
                cc.set(bean.getCuserId() + bean.getBankId() + "puFa_cookieStore",loginContext.getCookieStore());
                cc.set(bean.getCuserId() + bean.getBankId() + "puFaLoginParams",params);
                return yzm;
            }else{
                logger.info("cuserId="+bean.getCuserId()+"---获取页面timespan出错,errHtml>>"+errHtml);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("网络异常,请稍后再试!");
            }
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " ---获取图片验证异常---"+errHtml, e);
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return null;
    }

    /**
     * 获取短信验证码
     * @param bean
     * @return
     */
    public int getSms(Channel bean,MemCachedClient cc){
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "puFa_cookieStore");
            Object paramsObj = cc.get(bean.getCuserId() + bean.getBankId() + "puFaGetSmsParams");
            if (cookieStore==null || paramsObj==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            loginContext.setEncoding("GBK");
            Map<String, String> headers = loginContext.getHeaders();
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                headers.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            headers.put("Host","cardsonline.spdbccc.com.cn");
            headers.put("Accept","*/*");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            String url = "https://cardsonline.spdbccc.com.cn/icard/ResendLoginMobilePwd.do";
            Map<String,String> params = (Map<String, String>) paramsObj;
            Object tokenObj = cc.get(bean.getSourceCode() + "puFa_getSmsToken");
            if (tokenObj!=null){
                params.put("_ajaxToken", (String) tokenObj);
            }
            errHtml = httpPost(url,params,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";浦发短信发送结果页面>>"+errHtml);
            if (errHtml.contains("Message")){
                String message = errHtml.substring(errHtml.indexOf("<Message>")
                        ,errHtml.indexOf("</Message>")).replaceAll("<Message>","").replaceAll("\\s*","");
                logger.info("cuserId="+bean.getCuserId()+";发送短信结果为:"+message);
                if (message.contains("重发动态密码成功")){
                    String token = errHtml.substring(errHtml.indexOf("<Token>")
                            ,errHtml.indexOf("</Token>")).replaceAll("<Token>","");
                    cc.set(bean.getCuserId() + bean.getBankId() + "puFa_cookieStore",loginContext.getCookieStore());
                    cc.set(bean.getSourceCode() + "puFa_getSmsToken",token);

                    bean.setBusiErrCode(BillConstant.success);
                    bean.setBusiErrDesc("短信验证码发送成功!");
                    return 1;
                }else{
                    bean.setBusiErrCode(BillConstant.fail);
                    bean.setBusiErrDesc(message);
                }
            }else{
                logger.info("cuserId="+bean.getCuserId()+"---请求页面异常,errHtml>>"+errHtml);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("网络异常,请稍后再试!");
            }
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 短信发送异常---"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("环境异常,稍后重试");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }

    /**
     * 获取短信验证码
     * @param bean
     * @return
     */
    public int checkSms(Channel bean,MemCachedClient cc){
        LoginContext loginContext = null;
        String errHtml = "";
        try {
            Object cookieStore = cc.get(bean.getCuserId() + bean.getBankId() + "puFa_cookieStore");
            Object tokenObj = cc.get(bean.getSourceCode() + "puFa_getSmsToken");
            if (cookieStore==null || tokenObj==null){
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc("缓存已失效,请重新开始!");
                return 0;
            }
            cc.delete(bean.getSourceCode() + "puFa_getSmsToken");
            loginContext = createLoginContext((BasicCookieStore) cookieStore);
            loginContext.setEncoding("GBK");
            Map<String, String> headers = loginContext.getHeaders();
            String userIp = bean.getIpAddr();
            if (!StringUtils.isEmpty(userIp)) {
                headers.put("X-Forwarded-For", userIp);
                logger.info(bean.getCuserId() + " userip=" + userIp);
            }
            headers.put("Host","cardsonline.spdbccc.com.cn");
            headers.put("Accept","text/html, application/xhtml+xml, */*");
            headers.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            String token = (String) tokenObj;
            Map<String,String> params = new HashMap<>();
            params.put("_viewReferer","mobileCheck");
            params.put("_locale","zh_CN");
            params.put("MobilePasswd",bean.getBankRand().replaceAll("\\s*",""));
            params.put("LoginedFlag","1");
            params.put("_ajaxToken",token);

            String url = "https://cardsonline.spdbccc.com.cn/icard/checkMobilePwd.do";
            errHtml = httpPost(url,params,loginContext);
            logger.info("cuserId=="+bean.getCuserId()+";浦发银行短信验证结果页面>>>"+errHtml);
            Element errDiv = Jsoup.parse(errHtml).getElementById("errorbox");
            String errMsg = "";
            if (errDiv!=null){
                errMsg = errDiv.text().replaceAll("\\s*","");
            }else{
                Elements errEles = Jsoup.parse(errHtml).select("p.error_text");
                for (Element element:errEles){
                    errMsg = element.text();
                }
            }
            if (StringUtils.isEmpty(errMsg)){//验证成功
                bean.setBusiErrCode(BillConstant.success);
                bean.setBusiErrDesc("登录成功,开始解析账单");
                bean.setBankSessionId(loginContext.getCookieStr());
                Document loginDoc = Jsoup.parse(errHtml);
                Element htmlTypeEle = loginDoc.getElementsByAttributeValue("name","htmlType").first();
                String htmlType = htmlTypeEle.val();
                Element tokenEle = loginDoc.getElementsByAttributeValue("name","_transToken").first();
                String _transToken = tokenEle.attr("value");
                Element menuEle = loginDoc.getElementsByAttributeValue("name","_delQuickMenu").first();
                String _delQuickMenu = menuEle.val();
                //抓取账单保存
                url = "https://cardsonline.spdbccc.com.cn/icard/queryDetailCrad.do?transName=&CardNo=&_viewReferer=defaultError&_locale=zh_CN" +
                        "&SelectedMenuId=menu3_11_1_0&ChangeAcctFlag=&htmlType="+htmlType+"&_delQuickMenu="+_delQuickMenu+"&_transToken="+_transToken;
                errHtml = httpGet(url,loginContext);
                Element formEle = Jsoup.parse(errHtml).getElementById("form1");
                params = setFormParams(formEle);
                Element acctList = Jsoup.parse(errHtml).getElementById("AcctList");
                logger.info("taskId=="+bean.getTaskid()+";查询浦发银行卡列表>>>"+acctList.html());
                Elements options = acctList.children();
                List<String> htmlAccounts = new ArrayList<>();
                List<String> htmlBillSettled = new ArrayList<>();
                List<String> htmlBillUnsettled = new ArrayList<>();
                for (Element acctOpt:options){
                    String acctText = acctOpt.text();
                    if (acctText.contains("信用卡")){
                        String acctVal = acctOpt.val();
                        params.put("CardNo",acctVal);
                        params.put("AcctList",acctVal);
                        url = "https://cardsonline.spdbccc.com.cn/icard/queryDetailCradConf.do";
                        errHtml = httpPost(url,params,loginContext);
                        String creditAcctNo = "";
                        List<String> htmlList = parseToList(errHtml);
                        int acctIndex = htmlList.indexOf("卡号");
                        if (acctIndex!=-1){
                            creditAcctNo = htmlList.get(acctIndex+3);
                        }
                        if (StringUtils.isEmpty(creditAcctNo)){
                            logger.info("taskId=="+bean.getTaskid()+";未获取到信用卡号,errHtml>"+errHtml);
                            //SaveWrongBill.saveWrongBill(String.valueOf(bean.getBankid()),bean.getTaskid(),new Exception(),errHtml);
                        }else{
                            String cardNoPre = "卡号:"+creditAcctNo+"@@@";
                            //查询账户信息页面
                            url = "https://cardsonline.spdbccc.com.cn/icard/queryAcctInfo.do?transName=&CardNo="+acctVal+"&_viewReferer=defaultError&_locale=zh_CN" +
                                    "&SelectedMenuId=menu2_1_1_0&ChangeAcctFlag=&htmlType="+htmlType+"&_delQuickMenu="+_delQuickMenu+"&_transToken="+_transToken;
                            errHtml = httpGet(url,loginContext);
                            if (isTrueHtml(bean,errHtml)){
                                String acctStr = cardNoPre + lessHtml(errHtml);
                                //查询积分信息
                                url = "https://cardsonline.spdbccc.com.cn/icard/rewardPointsQuery.do?transName=&CardNo="+acctVal+
                                        "&_viewReferer=defaultError&_locale=zh_CN&SelectedMenuId=menu6_1_1_0&ChangeAcctFlag=" +
                                        "&htmlType="+htmlType+"&_delQuickMenu="+_delQuickMenu+"&_transToken="+_transToken;
                                errHtml = httpGet(url,loginContext);
                                List<String> pointList = parseToList(errHtml);
                                String availablepoint = "0.00";
                                if (pointList.indexOf("当前可用积分")!=-1){
                                    int pointIndex = pointList.indexOf("当前可用积分")+2;
                                    availablepoint = pointList.get(pointIndex).replaceAll("[^0-9.]", "");
                                }
                                acctStr = acctStr + "@@@当前可用积分:"+availablepoint;
                                htmlAccounts.add(acctStr);
                            }

                            //查询未出账单
                            url = "https://cardsonline.spdbccc.com.cn/icard/queryRecentTransDetails.do?transName=" +
                                    "&CardNo="+acctVal+"&_viewReferer=defaultError&_locale=zh_CN&SelectedMenuId=menu2_1_1_1&ChangeAcctFlag=" +
                                    "&htmlType="+htmlType+"&_delQuickMenu="+_delQuickMenu+"&_transToken="+_transToken;
                            errHtml = httpGet(url,loginContext);
                            if (isTrueHtml(bean,errHtml)){
                                htmlBillUnsettled.add(cardNoPre+lessHtml(errHtml));
                            }

                            //查询已出账单
                            url = "https://cardsonline.spdbccc.com.cn/icard/preQueryRecentBills.do?transName=&CardNo="+acctVal+"&_viewReferer=" +
                                    "defaultError&_locale=zh_CN&SelectedMenuId=menu2_1_1_2&ChangeAcctFlag=" +
                                    "&htmlType="+htmlType+"&_delQuickMenu="+_delQuickMenu+"&_transToken="+_transToken;
                            errHtml = httpGet(url,loginContext);
                            if (isTrueHtml(bean,errHtml)) {
                                Document billDoc = Jsoup.parse(errHtml);
                                formEle = billDoc.getElementsByAttributeValue("name", "form1").first();
                                params = setFormParams(formEle);
                                Element billMonths = billDoc.getElementsByAttributeValue("name", "BillsMonth").first();
                                for (Element monthOpt : billMonths.children()) {
                                    String month = monthOpt.attr("value");//2017-02
                                    params.put("BillsMonth", month);
                                    url = "https://cardsonline.spdbccc.com.cn/icard/queryRecentBills.do";
                                    errHtml = httpPost(url, params, loginContext);
                                    if (isTrueHtml(bean,errHtml)){
                                        String cmonthPre = "当前月份:"+month+"@@@";
                                        String billSettledStr = cardNoPre + cmonthPre + lessHtml(errHtml);
                                        htmlBillSettled.add(billSettledStr);

                                        Document billInDoc = Jsoup.parse(errHtml);
                                        formEle = billInDoc.getElementsByAttributeValue("name","form1").first();
                                        params = setFormParams(formEle);
                                        params.put("BillsMonth", month);
                                        Element nextInput = billInDoc.getElementsByAttributeValue("name","Next").first();
                                        int max = 5;
                                        while (max>0 && nextInput!=null && !nextInput.outerHtml().contains("disabled")) {//账单分页
                                            String pageRecord = errHtml.substring(errHtml.indexOf("var pageCurrentRecord"));
                                            pageRecord = pageRecord.substring(0, pageRecord.indexOf(";")).split("\\=")[1]
                                                    .replace("new Array", "").replaceAll("\\(", "").replaceAll("\\)", "")
                                                    .replaceAll("\"", "").replaceAll("\\s*", "");
                                            String[] valueArr = pageRecord.split(",");
                                            for (int i = 0; i < valueArr.length; i++) {
                                                String valuelen = valueArr[i];
                                                String[] tmp = valuelen.split("|");
                                                params.put(tmp[0], tmp[1]);
                                            }
                                            errHtml = httpPost(url, params, loginContext);
                                            String settledPageStr = cardNoPre + cmonthPre + lessHtml(errHtml);
                                            for(Map.Entry entry: params.entrySet()){
                                                logger.info("参数>>"+entry.getKey()+" = "+entry.getValue());
                                            }
                                            logger.info("cuserId>>>"+bean.getCuserId()+";查询到浦发已出账单分页页面>>>"+errHtml);
                                            if (isTrueHtml(bean,errHtml)){
                                                htmlBillSettled.add(settledPageStr);
                                                formEle = Jsoup.parse(errHtml).getElementsByAttributeValue("name","form1").first();
                                                params = setFormParams(formEle);
                                                params.put("BillsMonth", month);
                                                nextInput = Jsoup.parse(errHtml).getElementsByAttributeValue("name","Next").first();
                                            }else {
                                                nextInput = null;
                                            }
                                            max--;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                cc.set(bean.getCuserId()+";"+bean.getBankId()+";"+"htmlAccount",htmlAccounts);
                cc.set(bean.getCuserId()+";"+bean.getBankId()+";"+"htmlBillSettled",htmlBillSettled);
                cc.set(bean.getCuserId()+";"+bean.getBankId()+";"+"htmlBillUnsettled",htmlBillUnsettled);
                return 1;
            }else{
                logger.info("cuserId=="+bean.getCuserId()+";短信验证不成功:"+errMsg);
                bean.setBusiErrCode(BillConstant.fail);
                bean.setBusiErrDesc(errMsg);
                return 0;
            }
        } catch (Exception e) {
            logger.error("cuserId:"+bean.getCuserId()+getClass().getSimpleName() + " 浦发短信验证异常--"+errHtml, e);
            bean.setBusiErrCode(BillConstant.fail);
            bean.setBusiErrDesc("短信验证异常,稍后重试");
        } finally {
            if(loginContext!=null){
                loginContext.close();
            }
        }
        return 0;
    }

    public boolean isTrueHtml(Channel bean,String errHtml){
        if (errHtml.contains("登录已失效")
                || errHtml.contains("页面已失效")
                ||errHtml.contains("该期无账单或账单尚未出")
                ||errHtml.contains("目前我们无法完成您的请求")){
            //logger.info("cuserId=="+bean.getCuserId()+";errHtml>>"+errHtml);
            return false;
        }
        return true;
    }
}
