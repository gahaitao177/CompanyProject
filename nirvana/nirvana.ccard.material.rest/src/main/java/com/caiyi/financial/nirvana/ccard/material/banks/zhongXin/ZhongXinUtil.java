package com.caiyi.financial.nirvana.ccard.material.banks.zhongXin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.banks.guangfa.BankUtils;
import com.caiyi.financial.nirvana.ccard.material.banks.guangfa.GuangFaSubmit;
import com.caiyi.financial.nirvana.ccard.material.banks.guangfa.HttpUtil;
import com.caiyi.financial.nirvana.ccard.material.banks.guangfa.Message;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyListener;
import com.caiyi.financial.nirvana.ccard.material.util.BankApplyStepEnum;
import com.caiyi.financial.nirvana.ccard.material.util.BankEnum;
import com.caiyi.financial.nirvana.ccard.material.util.bean.ErrorRequestBean;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Mario on 2016/2/18 0018.
 */
public class ZhongXinUtil {
    //测试标识
    private boolean DEBUG = false;//打印debug信息
    private boolean DATA_DEBUG = false;//伪造用户数据
    //合作SID
    //private String SID = "SJHSKAPP";
    private String SID = "SJHSK1";

    public static Logger logger = LoggerFactory.getLogger("ZhongXinUtil");
    /**
     * **对外接口方法 1
     */
    //传入办卡类型,请求短信验证码
    public int openSession(MaterialBean bean,MemCachedClient client) {//卡类型
        if (bean.getBusiErrCode() == 0) {
            return 0;
        }
        try {
            if (DATA_DEBUG) {
                baseInfoBean.cardType = "KPBZGR04";
                initAdapterMap();
                this.PID = mapCardKinds.get(baseInfoBean.cardType);
                baseInfoBean.phone = "15092385024";
                baseInfoBean.idCard = "330182199107283619";
            } else {
//                CacheClient mcc = CacheClient.getInstance();
                baseInfoBean.cardType = client.get("zhongxin_cardtype_" + bean.getModel().getCidcard()).toString();
                logger.info("从MemeoryCache中读取卡类型:" + baseInfoBean.cardType);
                initAdapterMap();
                this.PID = mapCardKinds.get(baseInfoBean.cardType);
                baseInfoBean.phone = bean.getModel().getCphone();
                baseInfoBean.idCard = bean.getModel().getCidcard();
                logger.info("收到新的办卡信息 卡类型:" + baseInfoBean.cardType + " 身份证号:" + baseInfoBean.idCard + " 电话:" + baseInfoBean.phone);
                logger.info("全部bean数据:" + JSON.toJSONString(bean));
            }

            initSessionCookies();
            //请求短信验证码
            String res = sendPhoneVerifyCode(baseInfoBean.phone);

            JSONObject obj = JSON.parseObject(res);
            if (obj.getString("code").equals("0")) {//成功
                //根据卡类型获取对应cookie
                if (DEBUG) {
                    MemCachedClient mcc = ZhongXinUtil.getMemCachedClient();
                    //以银行+身份证为Key
                    mcc.set("zhongxin_cookie_" + baseInfoBean.idCard, cookieString, 3600000);
                    System.out.println("cookie存储到MC:" + cookieString);
                } else {
                    //以银行+身份证为Key
                    client.set("zhongxin_cookie_" + baseInfoBean.idCard, cookieString, 3600000);
                }
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc(obj.getString("message"));
                logger.info("短信验证码请求成功." + "msg:" + obj.getString("message"));
                BankApplyListener.sendSucess(BankEnum.zhongxin, BankApplyStepEnum.phone_code);
                return 1;
            } else {
                bean.setBusiErrCode(0);
                if(obj.getString("message").contains("TRANSIENT")){
                    bean.setBusiErrDesc("短信验证码请求失败.");
                }else{
                    bean.setBusiErrDesc(obj.getString("message"));
                }
                logger.info("短信验证码请求失败." + "msg:" + obj.getString("message"));
                return 0;
            }
        } catch (Exception e) {
            logger.warn(e.toString());
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("网络异常,请稍后再试。");
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "异常:" + e.getMessage());
            errBean.setCerrordesc("请求短信验证码异常");
            errBean.setIerrortype(1);
            errBean.setCphone(baseInfoBean.phone);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.phone_code, errBean);
            return 0;
        }
    }

    /**
     * **对外接口方法 2
     */
    //传入短信验证码,开始申请卡片
    public int applayCard(MaterialBean bean,MemCachedClient client) {
        bean.setBusiErrCode(1);
        try {
            logger.info("收到新的申请卡片请求！");
            logger.info("全部bean数据:" + JSON.toJSONString(bean));
            int resultCode = 0;
            if (DATA_DEBUG) {
                baseInfoBean.idCard = "330182199107283619";
            } else {
                baseInfoBean.idCard = bean.getModel().getCidcard();
            }
            //从MemoryCache中读取cookie
            try {
                if (DEBUG) {
                    MemCachedClient mcc = ZhongXinUtil.getMemCachedClient();
                    cookieString = mcc.get("zhongxin_cookie_" + baseInfoBean.idCard).toString();
                    System.out.println("从MC中读取cookie:" + cookieString);
                } else {
//                    CacheClient cc = CacheClient.getInstance();
                    cookieString = client.get("zhongxin_cookie_" + baseInfoBean.idCard).toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("网络连接错误,请重新获取验证码.");
                ErrorRequestBean errBean = new ErrorRequestBean(bean, "异常applayCard 138:" + e.getMessage());
                errBean.setCerrordesc("MemoryCache读取失败");
                errBean.setIerrortype(0);
                errBean.setCphone(baseInfoBean.phone);
                BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.submit_apply, errBean);
                return 0;
            }
            logger.info("从MemoryCache读取cookie:" + cookieString);
            //初始化pagedata,需要年费信息
            initPageDataBean();
            logger.info("初始化pagedata.");
            //初始化基本信息
            initUserInfoData(bean,client);
            if (bean.getBusiErrCode() == -1) {
                logger.info("初始化用户信息失败.");
                return 0;
            }
            logger.info("初始化用户信息成功.");
            String res;
            //检测是否是老用户
            res = checkIsOldCustomer();
            logger.info("检测是否是老用户.");
            JSONObject jsonObj = JSON.parseObject(res);
            if (jsonObj.getString("code").equals("0")) {//请求成功
                if (jsonObj.getString("isRegularCustomer").equals("true")) {
                    //老用户,不再初始化.
                    //initOldUserInfoData(bean);
                    logger.info("检测到是老用户,拒绝.");
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("您已持有中信银行信用卡,请选择其他银行办理.");
                    BankApplyListener.sendSucess(BankEnum.zhongxin,BankApplyStepEnum.submit_apply);
                    return 0;
                    //resultCode = oldUserApply();
                } else {
                    //新用户
                    logger.info("检测到是新用户.");
                    resultCode = newUserApply(bean);
                }
            } else {//返回错误信息 jsonObj.getString("message")
                if (jsonObj.getString("message").contains("您的验证码输入有误") || jsonObj.getString("message").contains("您的验证码输入错误3次")) {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + jsonObj.getString("message").replaceAll("[A-Za-z-]", ""));
                } else if (jsonObj.getString("message").contains("请输入正确的信息")) {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "请重新获取验证码.");
                } else if (jsonObj.getString("message").contains("中文")) {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc(bean.getBusiErrDesc() + " 姓名中不能包含其他字符," + jsonObj.getString("message").replaceAll("[A-Za-z-]", ""));
                } else {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + jsonObj.getString("message").replaceAll("[A-Za-z-]", ""));
                }
                resultCode = 0;
            }
            return resultCode;
        } catch (Exception e) {
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("网络错误,请稍后再试.");
            e.printStackTrace();
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "异常applayCard 196:" + e.getMessage());
            errBean.setCerrordesc("申请异常");
            errBean.setIerrortype(0);
            errBean.setCphone(baseInfoBean.phone);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.submit_apply, errBean);
            return 0;
        }
    }

    //老用户提交流程
    private int oldUserApply() {
        String res = submitNewCardInfoforOldUser();
        JSONObject jsonObj = JSON.parseObject(res);
        if (jsonObj.getString("code").equals("0")) {
            //成功
            return 1;
        } else {
            //返回错误信息 jsonObj.getString("message")
            return 0;
        }
    }

    //新用户提交流程
    private int newUserApply(MaterialBean bean) {
        logger.info("新用户提交流程开始.");
        String res;
        JSONObject jsonObj;
        //住房信息
        res = submitHouseInfo();
        jsonObj = JSON.parseObject(res);
        if (!jsonObj.getString("code").equals("0")) {
            //返回错误信息
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + jsonObj.getString("message").replaceAll("[A-Za-z-]", ""));
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "住房信息提交失败"+jsonObj.getString("message"));
            errBean.setCerrordesc("住房信息提交失败." + jsonObj.getString("message"));
            errBean.setIerrortype(3);
            errBean.setCphone(baseInfoBean.phone);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.submit_apply, errBean);
            return 0;
        }
        logger.info("新用户,住房信息提交完成.");

        //请求网点信息，暂时不用
        /*res = httpRequest.sendPost(URL_NET_POINT, "{\"cityId\":\"" + houseInfoBean.cityCode + "\"}", cookieString);
        jsonObj = JSON.parseObject(res);
        if (DEBUG) {
            System.out.println(res);
        }
        if (!jsonObj.getString("code").equals("0")) {
            //返回错误信息
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + jsonObj.getString("message").replaceAll("[A-Za-z-]",""));
            return 0;
        }
        logger.info("新用户,网点信息请求完成.");*/
        //公司信息及办卡方式
        res = submitUnitInfo();
        jsonObj = JSON.parseObject(res);
        if (!jsonObj.getString("code").equals("0")) {
            //返回错误信息
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + jsonObj.getString("message").replaceAll("[A-Za-z-]", ""));
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "公司信息及办卡方式提交失败"+jsonObj.getString("message"));
            errBean.setCerrordesc("公司信息及办卡方式提交失败." + jsonObj.getString("message"));
            errBean.setIerrortype(3);
            errBean.setCphone(baseInfoBean.phone);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.submit_apply, errBean);
            return 0;
        }
        logger.info("新用户,公司信息及办卡方式信息提交完成.");
        //直系亲属,紧急联系人
        res = submitContactInfo();
        jsonObj = JSON.parseObject(res);
        if (!jsonObj.getString("code").equals("0")) {
            //返回错误信息
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + jsonObj.getString("message").replaceAll("[A-Za-z-]", ""));
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "直系亲属,紧急联系人提交失败"+jsonObj.getString("message"));
            errBean.setCerrordesc("直系亲属,紧急联系人提交失败."+jsonObj.getString("message"));
            errBean.setIerrortype(3);
            errBean.setCphone(baseInfoBean.phone);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.submit_apply, errBean);
            return 0;
        }
        logger.info("新用户,直系亲属,紧急联系人信息提交完成.");
        //提交验证
        res = insertCard();
        jsonObj = JSON.parseObject(res);
        if (!jsonObj.getString("code").equals("0")) {
            //返回错误信息 jsonObj.getString("message")
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + jsonObj.getString("message").replaceAll("[A-Za-z-]", ""));
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "最后一步提交验证提交失败"+jsonObj.getString("message"));
            errBean.setCerrordesc("最后一步提交验证提交失败."+jsonObj.getString("message"));
            errBean.setIerrortype(3);
            errBean.setCphone(baseInfoBean.phone);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.submit_apply, errBean);
            return 0;
        } else {/**申请成功**/
            bean.setBusiErrCode(1);
            JSONObject objTemp = (JSONObject) (JSON.parseArray(jsonObj.getString("desc")).get(0));
            bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + objTemp.getString("desc"));
            logger.info("申请成功:" + objTemp.getString("desc"));
            BankApplyListener.sendSucess(BankEnum.zhongxin, BankApplyStepEnum.submit_apply);
            return 1;
        }

    }

    /**
     * 申卡进度发送验证码
     *
     * @param bean
     * @return
     */
    public Message smsCodeForQueryApply(MaterialBean bean,MemCachedClient client) {
        String mobileNo = bean.getCphone();
        String idcardno = bean.getIdcardid();

        System.out.println("进入中信查询进度，手机号="+mobileNo+",身份证号="+idcardno);
        if (BankUtils.isEmpty(mobileNo)) {
            return Message.errorJson(bean, "手机号码为空");
        }
        if (BankUtils.isEmpty(idcardno)) {
            return Message.errorJson(bean, "身份证号码为空");
        }
        //发送验证码
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("func", "sendMsg");
        params.put("mobile", mobileNo);//手机号
        params.put("id_nbr", idcardno);

        HashMap<String, String> pros = new HashMap<String, String>();
        pros.put("Origin", "https://creditcard.ecitic.com");//固定
        pros.put("Referer", "https://creditcard.ecitic.com/citiccard/wap/cardappquery/app_inq.jsp");//固定
        pros.put("User-Agent", GuangFaSubmit.userAgent);

        HttpUtil http = new HttpUtil(URL_QUERYCARDAPPINFO, "utf-8", params, pros);
        String result = "";
        try {
            result = http.getResponseString();
            System.out.println("中信进度-验证码，手机号="+mobileNo+"，银行返回="+result);
        } catch (Exception e) {
            e.printStackTrace();
            return Message.errorJson(bean, e.getMessage());
        }

        String session = "";
        Map<String, List<String>> map = http.getHeaderFields();
        if (map.get("Set-Cookie").size() > 1) {
            session = map.get("Set-Cookie").get(1);
        } else {
            session = map.get("Set-Cookie").get(0);
        }

//        CacheClient cache = CacheClient.getInstance();
        client.set(mobileNo + "_session", session);

//        JXmlWapper xml = JXmlWapper.parse(result);
        Document dom= XmlTool.stringToXml(result);
        List<Element> child = dom.getRootElement().elements();
        if (child.size() > 0) {
            String code = child.get(0).attributeValue("retcode");
            String message = child.get(0).attributeValue("message");

            System.out.println("中信银行信用卡进度查询短信，手机号=" + mobileNo + "，发送结果：code=" + code + ",message=" + message);
            BankApplyListener.sendSucess(BankEnum.zhongxin, BankApplyStepEnum.phone_code);
            if ("0".equals(code)) {
                return Message.successJson(bean, message);
            } else {
                return Message.errorJson(bean, message);
            }
        } else {
            System.out.println("中信银行信用卡进度查询短信，手机号=" + mobileNo + "银行返回结果："+result);
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "申卡进度发送验证码失败"+result);
            errBean.setCerrordesc("申卡进度发送验证码失败."+result);
            errBean.setIerrortype(1);
            errBean.setCphone(mobileNo);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.phone_code, errBean);
            return Message.errorJson(bean, "查询失败");
        }
    }

    /**
     * 查询申卡进度
     *
     * @param bean
     * @return resultcode  -1 代表接口查询失败 0 申请中 ，1 申请通过，2申请拒绝，3没有申请记录
     */
    public Message queryCardAppInfo(MaterialBean bean,MemCachedClient client) {
        String mobile = bean.getCphone();
        String idcardno = bean.getIdcardid();
        String smsCode = bean.getPhoneauthcode();

        System.out.println("进入中信查询进度，手机号="+mobile+",身份证号="+idcardno+"，短信验证码="+smsCode);
        if (BankUtils.isEmpty(mobile)) {
            return Message.errorJson(bean, "手机号码为空");
        }
        if (BankUtils.isEmpty(idcardno)) {
            return Message.errorJson(bean, "身份证号码为空");
        }
        if (BankUtils.isEmpty(smsCode)) {
            return Message.errorJson(bean, "短信验证码为空");
        }
//        CacheClient cache = CacheClient.getInstance();
        String session = (String) client.get(mobile + "_session");

        if (BankUtils.isEmpty(session)) {
            return Message.errorJson(bean, "会话超时，请重新获取验证");
        }

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("func", "queryCardAppInfo");
        params.put("id_nbr", idcardno);
        params.put("msg_code", smsCode);

        HashMap<String, String> pros = new HashMap<String, String>();
        pros.put("Host", "creditcard.ecitic.com");//固定
        pros.put("Cookie", session);
        pros.put("Referer", "https://creditcard.ecitic.com/citiccard/wap/cardappquery/app_inq.jsp");//固定
        pros.put("User-Agent", GuangFaSubmit.userAgent);


        HttpUtil http = new HttpUtil(URL_QUERYCARDAPPINFO, "utf-8", params, pros);
        String result = null;
        try {
            result = http.getResponseString();
            System.out.println("中信进度-查询，手机号="+mobile+"，银行返回="+result);
        } catch (Exception e) {
            e.printStackTrace();
            return Message.errorJson(bean, e.getMessage());
        }
        Element root= XmlTool.stringToXml(result).getRootElement();
//       Element dom=document.getRootElement();
//        Document document=null;
//        List<Element> child = dom.getRootElement().elements();
        String retcode = root.element("returninfo").attributeValue("retcode");
        String message = root.element("returninfo").attributeValue("message");

        if (retcode.equals("0")) {//查询成功
            String app_count = root.element("cardInfo").attributeValue("app_count");
            int appCount = Integer.parseInt(app_count);
            System.out.println("code=" + retcode + ",message=" + message);
            String resultcode = "";
            String resultdesc = "";
            String remind = "";
            if (appCount > 0) {
                List<Element> c = root.elements("cardInfoSet");
                int i = 0;
                for (i = 0; i < c.size(); i++) {
                    Element child = c.get(i);
                    String cardName = child.attributeValue("app_card_desc");
                    if (matchCardName(cardName, bean.getCname())) {
                        break;
                    }
                }
                if (i == c.size()) {
                    i--;
                }
                Element child = c.get(i);
                String status = child.attributeValue("app_proc_status");//1:审核中，2:通过，3:不通过
                String cardName = child.attributeValue("app_card_desc");
                String date = child.attributeValue("rcv_date");

                if (date.length() == 8) {
                    date = date.substring(0, 4) + "年" + date.substring(4, 6) + "月" + date.substring(6, 8) + "日";
                }
                if ("1".equals(status)) {//审核中
                    status = "审核中";
                    resultcode = "0";
                    remind = "，从申请日起，7-15个工作日出审核结果";
                } else if ("2".equals(status)) {//通过
                    status = "通过";
                    resultcode = "1";
                    remind = "，我们会尽快以短信形式通知面签时间与网点";
                } else {//未通过
                    status = "未通过";
                    resultcode = "2";
                    remind = "，感谢您对中信信用卡的支持";
                }
                resultdesc = "您在[" + date + "]申请的[" + cardName + "],申请结果为：" + status + remind;
                JSONObject json = new JSONObject();
                json.put("resultcode", resultcode);
                json.put("resultdesc", resultdesc);
                json.put("resean", "");
                System.out.println("中信银行申卡进度查询成功，手机号=" + mobile + ",申请结果：" + resultdesc);
                bean.setCstatus(resultcode);
                BankApplyListener.sendSucess(BankEnum.zhongxin, BankApplyStepEnum.query_apply);
                return Message.successJson(bean, status, json.toString());
            } else {
                JSONObject json = new JSONObject();
                json.put("resultcode", "3");
                json.put("resultdesc", "没有查询到申卡记录");
                json.put("resean", "");
                System.out.println("没有查询到申卡记录");
                BankApplyListener.sendSucess(BankEnum.zhongxin, BankApplyStepEnum.query_apply);
                return Message.errorJson(bean, "没有查询到申卡记录", json.toString());
            }
        } else {//查询失败
            ErrorRequestBean errBean = new ErrorRequestBean(bean, "查询申卡进度失败"+result);
            errBean.setCerrordesc("查询申卡进度失败."+result);
            errBean.setIerrortype(3);
            errBean.setCphone(mobile);
            BankApplyListener.sendError(BankEnum.zhongxin, BankApplyStepEnum.query_apply, errBean);
            return Message.errorJson(bean, message);
        }
    }

    /**
     * 匹配银行信用卡名字和本地信用卡名
     *
     * @param bankCardName
     * @param myCardName
     * @return 匹配成功返回true
     */
    private boolean matchCardName(String bankCardName, String myCardName) {
        if (StringUtils.isEmpty(myCardName) || StringUtils.isEmpty(bankCardName)) {
            return false;
        }
        if (myCardName.contains("南航")) {
            if (bankCardName.contains("南航")) {
                return true;
            }
        }
        if (myCardName.contains("Signature")) {
            if (bankCardName.contains("Signature")) {
                return true;
            }
        }
        if (myCardName.contains("魔力")) {
            if (bankCardName.contains("魔力")) {
                return true;
            }
        }
        if (myCardName.contains("Q享") || myCardName.contains("q享")) {
            if (bankCardName.contains("Q享") || bankCardName.contains("q享")) {
                return true;
            }
        }
        if (myCardName.contains("i白金") || myCardName.contains("I白金")) {
            if (bankCardName.contains("I白金") || bankCardName.contains("i白金")) {
                return true;
            }
        }
        if (myCardName.contains("标准IC") || myCardName.contains("标准ic")) {
            if (bankCardName.contains("标准IC") || bankCardName.contains("标准ic")) {
                return true;
            }
        }
        if (myCardName.contains("标准信用卡")) {
            if (bankCardName.contains("标准信用卡")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 内部HttpRequest对象
     */
    private HttpRequest httpRequest = new HttpRequest();

    //一次会话的cookie字符串
    private String cookieString;
    //办卡连接
    private String URL_ROOT = "https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html";
    //卡类型标识
    private String PID;
    //开始申卡流程，访问一次后，cookie才可正式使用
    private String URL_INDEX_DO = "https://creditcard.ecitic.com/citiccard/newwap/applyInit/index.do";
    //初始化页面数据
    private String URL_PAGE_DATA = "https://creditcard.ecitic.com/citiccard/newwap/applyInit/applyCardInit.do";
    //获取网点信息
    private String URL_NET_POINT = "https://creditcard.ecitic.com/citiccard/newwap/loadNetPoint/loadNetpointData.do";
    //请求短信验证码
    private String URL_SEND_PHONE_CODE = "https://creditcard.ecitic.com/citiccard/newwap/applycard/generatePhoneCode.do";
    //汉字转拼音
    private String URL_HANZI_TO_PINYIN = "https://creditcard.ecitic.com/citiccard/newwap/other/hanziToPinyin.do";
    //个人基本信息
    private String URL_BASE_INFO_1 = "https://creditcard.ecitic.com/citiccard/newwap/applycard/isRegularCustomer.do";
    //住宅、联系方式
    private String URL_HOUSE_INFO_2 = "https://creditcard.ecitic.com/citiccard/newwap/applycard/houseInfoCommit.do";
    //公司信息及办卡方式
    private String URL_UNIT_INFO_3 = "https://creditcard.ecitic.com/citiccard/newwap/applycard/unitInfoComit.do";
    //直系亲属及紧急联系人资料
    private String URL_CONTRACT_INFO_4 = "https://creditcard.ecitic.com/citiccard/newwap/applycard/contactInfoCommit.do";
    //新卡提交验证,用cookie访问一次即可，成功返回code:0
    private String URL_SUBMIT_APPLY_5 = "https://creditcard.ecitic.com/citiccard/newwap/applycard/insertCard.do";
    //老用户新卡获取礼品
    private String URL_LOAD_GIFT = "https://creditcard.ecitic.com/citiccard/newwap/loadNetPoint/loadGiftData.do";
    //老用户新卡提交验证
    private String URL_HAVECARD_APPLY = "https://creditcard.ecitic.com/citiccard/newwap/applyHaveCard/havaCardApply.do";
    /**
     * 申卡进度查询接口
     */
    private String URL_QUERYCARDAPPINFO = "https://creditcard.ecitic.com/citiccard/wap/bind/login.do";


    /**
     * 初始化卡类型列表,岗位性质列表，单位性质列表
     */
    Map<String, String> mapCardKinds = new HashMap<>();
    Map<String, String> mapPostNature = new HashMap<>();
    Map<String, String> mapUnitNature = new HashMap<>();
    Map<String, String> mapFamilyRelation = new HashMap<>();

    private void initAdapterMap() {
        //URL:https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html?sid=SJHSKAPP&pid=CS0080
        mapCardKinds.put("KPBZGR04", "CS0080");//VISA人民币/美元双币普卡
        mapCardKinds.put("KPBZGR03", "CS0080");//VISA人民币/美元双币金卡
        //URL:https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html?sid=SJHSKAPP&pid=CS0002
        mapCardKinds.put("KPYLJKIC", "CS0002");//中信银联金卡
        //URL:https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html?sid=SJHSKAPP&pid=CS0083
        //此卡有年费选项
        mapCardKinds.put("KPIBJX", "CS0083");//中信i白金信用卡
        //URL:https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html?sid=SJHSKAPP&pid=CS0141
        mapCardKinds.put("KPSJQQJK", "CS0141");//中信Q享金卡
        //URL:https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html?sid=SJHSKAPP&pid=CS0136
        mapCardKinds.put("KPQBBJK", "CS0136");//中信Visa Signature信用卡
        //URL:https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html?sid=SJHSKAPP&pid=CS0004
        mapCardKinds.put("KPML0014", "CS0004");//魔力银联金卡
        //URL:https://creditcard.ecitic.com/citiccard/newwap/pages/AppCreditCard/applayCard-process.html?sid=SJHSKAPP&pid=CS0056
        mapCardKinds.put("KPNH0003", "CS0056");//中信南航明珠普卡
        mapCardKinds.put("KPNH0004", "CS0056");//中信南航明珠金卡
        //岗位性质
        mapPostNature.put("1", "04");
        mapPostNature.put("2", "02");
        mapPostNature.put("3", "03");
        mapPostNature.put("4", "02");
        //单位性质
        mapUnitNature.put("1", "1");
        mapUnitNature.put("2", "2");
        mapUnitNature.put("3", "5");
        mapUnitNature.put("4", "3");
        mapUnitNature.put("5", "4");
        mapUnitNature.put("6", "6");
        mapUnitNature.put("7", "7");
        //亲属关系
        mapFamilyRelation.put("1", "1");
        mapFamilyRelation.put("2", "3");
        mapFamilyRelation.put("3", "2");
        mapFamilyRelation.put("4", "4");
    }

    /**
     * 初始化会话cookie
     */
    private void initSessionCookies() {
        cookieString = httpRequest.getCookie(URL_ROOT + "?sid=" + this.SID + "&pid=" + this.PID);
        //applyInit/index.do,通过此校验，cookie才会生效
        httpRequest.sendPost(URL_INDEX_DO + "?reqtime=" + new Date().getTime(), "{\"sid\":\"" + this.SID + "\",\"pid\":\"" + this.PID + "\"}", cookieString);
        if (DEBUG) {
            System.out.println("本次会话cookie:\n     " + cookieString);
        }
    }

    /**
     * 初始化pageDataBean
     */
    private void initPageDataBean() {
        String json = httpRequest.sendGet(URL_PAGE_DATA, "", cookieString);
        pageDataBean = JSON.parseObject(json, ZhongXinPageDataBean.class);
        pageDataBean.initList();
        if (DEBUG) {
            System.out.println("\t\t\t初始化pageDataBean:");
            System.out.println("\t\t\t\tcode:" + pageDataBean.code + "   msg:" + pageDataBean.message);
            if (pageDataBean.yearMoneyArray != null) {
                System.out.println("\n\t\t\t\t年费:" + pageDataBean.yearMoneyArray);
            } else {
                System.out.println("\n\t\t\t\t年费:默认空");
            }
        }
    }

    /**
     * 将用户数据转化为银行数据
     */
    //pageDataBean
    private ZhongXinPageDataBean pageDataBean;
    private ZhongXinBaseInfoBean baseInfoBean = new ZhongXinBaseInfoBean();
    private ZhongXinHouseInfoBean houseInfoBean = new ZhongXinHouseInfoBean();
    private ZhongXinUnitInfoBean unitInfoBean = new ZhongXinUnitInfoBean();
    private ZhongXinContactInfoBean contactInfoBean = new ZhongXinContactInfoBean();
    private ZhongXinHaveCardInfoBean haveCardBean = new ZhongXinHaveCardInfoBean();

    /**
     * 初始化基本数据
     *
     * @param bean
     */
    private void initUserInfoData(MaterialBean bean,MemCachedClient client) {
        if (DATA_DEBUG) {
            baseInfoBean.endTime = "2021/01/25";
            baseInfoBean.zhName = "黄业宏";
            baseInfoBean.enName = this.hanZiToPinyin("黄业宏");
            baseInfoBean.idCard = "330182199107283619";
            baseInfoBean.isRecommend = "1";
            baseInfoBean.issuingAuthority = "建德市公安局";
            baseInfoBean.phone = "18301852978";//15821129261 18301852937
            baseInfoBean.recommendNum = "";
            baseInfoBean.startTime = "2011/01/25";
            baseInfoBean.verifyCode = "5656";
            baseInfoBean.cardType = "KPIBJX";
            initAdapterMap();
            this.PID = mapCardKinds.get(baseInfoBean.cardType);
            //年费
            if (pageDataBean.yearMoneyArray != null) {
                baseInfoBean.yearMoney = pageDataBean.listYearMoneyArray.get(0).flagId;
            } else {
                baseInfoBean.yearMoney = "";
            }

            //新用户,家庭住址
            houseInfoBean.provenceSelectName = "北京";
            houseInfoBean.citySelectName = "北京市";
            houseInfoBean.countySelectName = "朝阳区";
            houseInfoBean.address = "小庄金台西路8号";
            houseInfoBean.email = "123456789@qq.com";
            //根据市名获取provinceCode,cityCode,areaCode,postCode
            //json格式{cityCode:'1',cityName:'北京市',code:'85',areaCode:'010',postCode:'100000'},
            //第一个cityCode实际上是provinceCode，code才是cityCode.地址如果精确到区，区号继续更新
            JSONObject jsonCity = pageDataBean.getCityByCityName(houseInfoBean.citySelectName);
            if (jsonCity != null) {
                houseInfoBean.provinceCode = jsonCity.getString("cityCode");
                houseInfoBean.cityCode = jsonCity.getString("code");
                houseInfoBean.housePostCode = jsonCity.getString("postCode");
                //地址如果精确到区，区号继续更新
                houseInfoBean.areaCode = jsonCity.getString("areaCode");
                houseInfoBean.houseTelArea = jsonCity.getString("areaCode");
            } else {

            }
            //根据区，县名获取areaCode
            JSONObject jsonCounty = pageDataBean.getCountryByNameAndCityCode(houseInfoBean.cityCode, houseInfoBean.countySelectName);
            if (jsonCounty != null) {
                houseInfoBean.areaCode = jsonCounty.getString("code");
            } else {

            }
            //以下可不填
            houseInfoBean.houseTel = "";
            houseInfoBean.spid = "";
            houseInfoBean.spname = "";
            houseInfoBean.sptxtnm = "";

            //单位地址
            unitInfoBean.provenceSelectName = "北京";
            unitInfoBean.citySelectName = "北京市";
            unitInfoBean.countySelectName = "朝阳区";
            unitInfoBean.unitName = "西路八号宾馆";
            unitInfoBean.unitAddress = "小庄金台西路8号";
            //{code:'71152',name:'北京出国中心支行',ebankWayYZ:'1001',ebankWayXC:'XC49',areaId:'800'}
            unitInfoBean.ebankWayXC = "";//ebankWayXC:'XC49'
            unitInfoBean.ebankWayYZ = "";//ebankWayYZ:'1001'
            unitInfoBean.latticePoint = "0";//code:'71152'
            unitInfoBean.wayToCard = "02";//01,02,06 办卡方式
            unitInfoBean.postNature = "04";//岗位性质 "[{code:'01',name:'单位负责人级'},{code:'02',name:'部门负责人级'},{code:'03',name:'科室负责人级'},{code:'04',name:'一般员工'}]"
            unitInfoBean.unitNature = "3";//单位性质 "[{code:'1',name:'机关事业'},{code:'2',name:'国有'},{code:'3',name:'外商独资'},{code:'4',name:'合资/合作'},{code:'5',name:'股份制'},{code:'6',name:'民营'},{code:'7',name:'个体私营'},{code:'8',name:'其他'}]"
            unitInfoBean.unitTel = "87815555";
            //根据城市名获取code
            JSONObject unitJsonCity = pageDataBean.getCityByCityName(unitInfoBean.citySelectName);
            if (unitJsonCity != null) {
                unitInfoBean.citySelectCode = unitJsonCity.getString("code");
                unitInfoBean.provenceSelectCode = unitJsonCity.getString("cityCode");
                unitInfoBean.unitPostCode = unitJsonCity.getString("postCode");
                unitInfoBean.unitTelArea = unitJsonCity.getString("areaCode");
            } else {

            }
            //根据区，县名获取countySelectCode
            JSONObject unitJsonCounty = pageDataBean.getCountryByNameAndCityCode(unitInfoBean.citySelectCode, unitInfoBean.countySelectName);
            if (jsonCounty != null) {
                unitInfoBean.countySelectCode = jsonCounty.getString("code");
                unitInfoBean.regin = unitInfoBean.countySelectCode;
            } else {

            }

            //紧急联系人资料
            contactInfoBean.contactPhone = "15888899898";
            contactInfoBean.contactRelation = "朋友";
            contactInfoBean.emergencyContact = "老板";
            contactInfoBean.familyPhone = "15888899899";
            contactInfoBean.familyRelation = "3";
            contactInfoBean.relativesName = "黑梅";

            contactInfoBean.addressTel = "";
            contactInfoBean.addressTelArea = "";
            contactInfoBean.contactTel = "";
            contactInfoBean.contactTelAea = "";

        } else {//从MaterialBean中转化银行数据
            logger.info("中信:从MaterialBean中转化银行数据");
            if (DEBUG) {
                MemCachedClient mcc = ZhongXinUtil.getMemCachedClient();
                baseInfoBean.cardType = mcc.get("zhongxin_cardtype_" + bean.getModel().getCidcard()).toString();
            } else {
//                CacheClient mcc = CacheClient.getInstance();
                baseInfoBean.cardType = client.get("zhongxin_cardtype_" + bean.getModel().getCidcard()).toString();
            }
            initAdapterMap();
            this.PID = mapCardKinds.get(baseInfoBean.cardType);

            //证件有效期
            baseInfoBean.startTime = "";
            baseInfoBean.endTime = "";
            /**
             * 证件有效期不是必填，删掉 Modified By zhaojie 2016/3/11 11:25:25
             if (bean.getModel().getCidexpirationtime().contains(",")) {
             DateFormat dfOur = new SimpleDateFormat("yyyyMMdd");
             DateFormat dfBank = new SimpleDateFormat("yyyy/MM/dd");
             String[] dateArray = bean.getModel().getCidexpirationtime().split(",");
             try {
             if (dateArray.length == 1) {//只有截止日期 格式:"2016/02/18"
             Date endD = dfOur.parse(dateArray[0]);
             baseInfoBean.endTime = dfBank.format(endD);
             //提前10年作为起始日期
             Calendar cal = Calendar.getInstance();
             cal.setTime(endD);
             cal.add(Calendar.YEAR, -10);
             Date startD = cal.getTime();
             baseInfoBean.startTime = dfBank.format(startD);
             } else {
             Date startD = dfOur.parse(dateArray[0]);
             Date endD = dfOur.parse(dateArray[1]);
             baseInfoBean.startTime = dfBank.format(startD);
             baseInfoBean.endTime = dfBank.format(endD);
             }
             } catch (ParseException e) {
             e.printStackTrace();
             }
             } else {
             //返回错误信息
             bean.setBusiErrCode(-1);
             bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "中信银行不支持长期身份证办理,请修改数据.");
             }*/

            baseInfoBean.zhName = bean.getModel().getCname();
            if (baseInfoBean.zhName == null || baseInfoBean.zhName.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "用户中文名为空,请检查您的资料.");
                logger.info("中信,错误:用户中文名为空,请检查您的资料.");
                return;
            }
            baseInfoBean.enName = bean.getModel().getCenglishname();
            if (baseInfoBean.enName == null || baseInfoBean.enName.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "用户英文名为空,请检查您的资料.");
                logger.info("中信,错误:用户英文名为空,请检查您的资料.");
                return;
            }
            baseInfoBean.idCard = bean.getModel().getCidcard();
            if (baseInfoBean.idCard == null || baseInfoBean.idCard.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "用户证件号码为空,请检查您的资料.");
                logger.info("中信,错误:用户证件号码为空,请检查您的资料.");
                return;
            }
            baseInfoBean.isRecommend = "1";
            baseInfoBean.recommendNum = "";
            /**
             * 发证机关不是必填，删掉 Modified By zhaojie 2016/3/11 11:23:58
             CacheClient mcId = CacheClient.getInstance();
             baseInfoBean.issuingAuthority = mcId.get("zhongxin_idAddress_" + bean.getModel().getCidcard()).toString();
             if(baseInfoBean.issuingAuthority == null||baseInfoBean.issuingAuthority.equals("")){
             bean.setBusiErrCode(-1);
             bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "用户证件发证机关为空,请检查您的资料.");
             }
             */
            //发证机关默认空
            baseInfoBean.issuingAuthority = "";
            baseInfoBean.phone = bean.getModel().getCphone();
            if (baseInfoBean.phone == null || baseInfoBean.phone.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "用户手机号为空,请检查您的资料.");
                logger.info("中信,错误:用户手机号为空,请检查您的资料.");
                return;
            }
            baseInfoBean.verifyCode = bean.getPhoneauthcode();
            if (baseInfoBean.verifyCode == null || baseInfoBean.verifyCode.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "短信验证码为空,请检查数据无误后再提交.");
                logger.info("中信,错误:短信验证码为空,请检查您的资料.");
                return;
            }
            //年费
            if (pageDataBean.yearMoneyArray != null) {
                baseInfoBean.yearMoney = pageDataBean.listYearMoneyArray.get(0).flagId;
            } else {
                baseInfoBean.yearMoney = "";
            }

            //新用户,家庭住址
            String cacheData;
            if (DEBUG) {
                MemCachedClient mcc = ZhongXinUtil.getMemCachedClient();
                cacheData = mcc.get("zhongxin_data_" + bean.getModel().getCidcard()).toString();
            } else {
//                CacheClient mcc = CacheClient.getInstance();
                cacheData = client.get("zhongxin_data_" + bean.getModel().getCidcard()).toString();
            }
            logger.info("中信,从MemeoryCache中读取地址信息:" + cacheData);
            String homeAddress = cacheData.split("@")[0];
            houseInfoBean.provenceSelectName = homeAddress.split(",")[0];
            logger.info("中信,家庭住址省:" + houseInfoBean.provenceSelectName);
            houseInfoBean.citySelectName = homeAddress.split(",")[1];
            logger.info("中信,家庭住址市:" + houseInfoBean.citySelectName);
            houseInfoBean.countySelectName = homeAddress.split(",")[2];
            logger.info("中信,家庭住址区:" + houseInfoBean.countySelectName);
            houseInfoBean.address = bean.getModel().getChome_detailaddress();
            houseInfoBean.email = bean.getModel().getCemail();
            //根据市名获取provinceCode,cityCode,areaCode,postCode
            //json格式{cityCode:'1',cityName:'北京市',code:'85',areaCode:'010',postCode:'100000'},
            //第一个cityCode实际上是provinceCode，code才是cityCode.地址如果精确到区，区号继续更新
            JSONObject jsonCity = pageDataBean.getCityByCityName(houseInfoBean.citySelectName);
            if (jsonCity != null) {
                houseInfoBean.provinceCode = jsonCity.getString("cityCode");
                houseInfoBean.cityCode = jsonCity.getString("code");
                houseInfoBean.housePostCode = jsonCity.getString("postCode");
                //地址如果精确到区，区号继续更新
                houseInfoBean.areaCode = jsonCity.getString("areaCode");
                houseInfoBean.houseTelArea = jsonCity.getString("areaCode");
            } else {
                //返回错误信息
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "该城市暂不支持");
                logger.info("中信,错误(家庭住址):该城市暂不支持.");
                return;
            }
            //根据区，县名获取areaCode
            JSONObject jsonCounty = pageDataBean.getCountryByNameAndCityCode(houseInfoBean.cityCode, houseInfoBean.countySelectName);
            if (jsonCounty != null) {
                houseInfoBean.areaCode = jsonCounty.getString("code");
            } else {
                //返回错误信息
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "该地区暂不支持");
                logger.info("中信,错误(家庭住址):该地区暂不支持.");
                return;
            }
            //以下可不填
            houseInfoBean.houseTel = "";
            houseInfoBean.spid = "";
            houseInfoBean.spname = "";
            houseInfoBean.sptxtnm = "";

            //单位地址
            String unitAddress = cacheData.split("@")[1];
            unitInfoBean.provenceSelectName = unitAddress.split(",")[0];
            logger.info("中信,单位地址省:" + houseInfoBean.provenceSelectName);
            unitInfoBean.citySelectName = unitAddress.split(",")[1];
            logger.info("中信,单位地址市:" + houseInfoBean.citySelectName);
            unitInfoBean.countySelectName = unitAddress.split(",")[2];
            logger.info("中信,单位地址区:" + houseInfoBean.countySelectName);
            unitInfoBean.unitName = bean.getModel().getCcompanyname();
            unitInfoBean.unitAddress = bean.getModel().getCcompany_detailaddress();
            //网点信息 {code:'71152',name:'北京出国中心支行',ebankWayYZ:'1001',ebankWayXC:'XC49',areaId:'800'}
            unitInfoBean.ebankWayXC = "";//ebankWayXC:'XC49'//todo:网点入库,办卡方式解析
            unitInfoBean.ebankWayYZ = "";//ebankWayYZ:'1001'
            unitInfoBean.latticePoint = "0";//code:'71152'
            unitInfoBean.wayToCard = "02";//01,02,06 办卡方式
            unitInfoBean.postNature = mapPostNature.get(bean.getModel().getIdepartment());//岗位性质 "[{code:'01',name:'单位负责人级'},{code:'02',name:'部门负责人级'},{code:'03',name:'科室负责人级'},{code:'04',name:'一般员工'}]"
            unitInfoBean.unitNature = mapUnitNature.get(bean.getModel().getInatureofunit());//单位性质 "[{code:'1',name:'机关事业'},{code:'2',name:'国有'},{code:'3',name:'外商独资'},{code:'4',name:'合资/合作'},{code:'5',name:'股份制'},{code:'6',name:'民营'},{code:'7',name:'个体私营'},{code:'8',name:'其他'}]"
            unitInfoBean.unitTel = bean.getModel().getCcompany_telnum();
            //根据城市名获取code
            JSONObject unitJsonCity = pageDataBean.getCityByCityName(unitInfoBean.citySelectName);
            if (unitJsonCity != null) {
                unitInfoBean.citySelectCode = unitJsonCity.getString("code");
                unitInfoBean.provenceSelectCode = unitJsonCity.getString("cityCode");
                unitInfoBean.unitPostCode = unitJsonCity.getString("postCode");
                unitInfoBean.unitTelArea = unitJsonCity.getString("areaCode");
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "公司所在城市暂不支持");
                logger.info("中信,错误(公司住址):公司所在城市暂不支持.");
                return;
            }
            //根据区，县名获取countySelectCode
            JSONObject unitJsonCounty = pageDataBean.getCountryByNameAndCityCode(unitInfoBean.citySelectCode, unitInfoBean.countySelectName);
            if (unitJsonCounty != null) {
                unitInfoBean.countySelectCode = unitJsonCounty.getString("code");
                unitInfoBean.regin = unitInfoBean.countySelectCode;
            } else {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "公司所在地区暂不支持");
                logger.info("中信,错误(公司地址):公司所在地区暂不支持.");
                return;
            }

            //紧急联系人资料
            contactInfoBean.contactPhone = bean.getModel().getCemergencyphone();
            if (contactInfoBean.contactPhone == null || contactInfoBean.contactPhone.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "紧急联系人电话为空,请检查您的资料.");
                logger.info("中信,错误:紧急联系人电话为空,请检查您的资料..");
                return;
            }
            contactInfoBean.contactRelation = "朋友";
            contactInfoBean.emergencyContact = bean.getModel().getCemergencycontactname();
            if (contactInfoBean.emergencyContact == null || contactInfoBean.emergencyContact.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "紧急联系人为空,请检查您的资料.");
                logger.info("中信,错误:紧急联系人为空,请检查您的资料..");
                return;
            }
            contactInfoBean.familyPhone = bean.getModel().getCfamilyphonenum();
            if (contactInfoBean.familyPhone == null || contactInfoBean.familyPhone.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "亲属电话为空,请检查您的资料.");
                logger.info("中信,错误:亲属电话为空,请检查您的资料..");
                return;
            }
            contactInfoBean.familyRelation = mapFamilyRelation.get(bean.getModel().getIfamilyties());
            if (contactInfoBean.familyRelation == null || contactInfoBean.familyRelation.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "亲属关系为空,请检查您的资料.");
                logger.info("中信,错误:亲属关系为空,请检查您的资料..");
                return;
            }
            contactInfoBean.relativesName = bean.getModel().getFamilyname();
            if (contactInfoBean.relativesName == null || contactInfoBean.relativesName.equals("")) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc(bean.getBusiErrDesc() + " " + "亲属联系人为空,请检查您的资料.");
                logger.info("中信,错误:亲属联系人为空,请检查您的资料..");
                return;
            }
            //以下可不填
            contactInfoBean.contactTel = "";
            contactInfoBean.contactTelAea = "";
            contactInfoBean.addressTel = "";
            contactInfoBean.addressTelArea = "";

        }
    }

    /**
     * 初始化老用户用到的数据
     *
     * @param bean
     */
    private void initOldUserInfoData(MaterialBean bean) {
        if (DATA_DEBUG) {
            //老用户
            haveCardBean.cardType = "KPIBJX";
            haveCardBean.startTime = "2006/11/02";
            haveCardBean.endTime = "2016/11/02";
            haveCardBean.enName = "TANG ZHENG BING";
            haveCardBean.zhName = "唐正兵";
            haveCardBean.haveGift = "";
            haveCardBean.idCard = "532129198412150719";
            haveCardBean.isRecommend = "1";
            haveCardBean.issuingAuthority = "昆明市公安局五华分局";
            haveCardBean.myemail2 = "12345698@qq.com";
            haveCardBean.phoneServicePWD = "";
            haveCardBean.recommendNum = "";
            haveCardBean.regin = "";

            //年费
            if (pageDataBean.yearMoneyArray != null) {
                haveCardBean.yearMoney = pageDataBean.listYearMoneyArray.get(0).flagId;
            } else {
                haveCardBean.yearMoney = "";
            }

            haveCardBean.spid = "";
            haveCardBean.spname = "";
            haveCardBean.sptxtnm = "";

            haveCardBean.ebankWayXC = "";
            haveCardBean.ebankWayYZ = "";
            haveCardBean.latticePoint = "0";
            haveCardBean.wayToCard = "2";
            return;
        }
        //老用户
        haveCardBean.cardType = bean.getApplyBankCardId();
        haveCardBean.ebankWayXC = "";
        haveCardBean.ebankWayYZ = "";
        haveCardBean.latticePoint = "0";
        haveCardBean.wayToCard = "2";

        //计算证件有效期
        if (bean.getModel().getCidexpirationtime().contains(",")) {
            DateFormat dfOur = new SimpleDateFormat("yyyyMMdd");
            DateFormat dfBank = new SimpleDateFormat("yyyy/MM/dd");
            String[] dateArray = bean.getModel().getCidexpirationtime().split(",");
            try {
                if (dateArray.length == 1) {//只有截止日期 格式:"2016/02/18"
                    Date endD = dfOur.parse(dateArray[0]);
                    haveCardBean.endTime = dfBank.format(endD);
                    //提前10年作为起始日期
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endD);
                    cal.add(Calendar.YEAR, -10);
                    Date startD = cal.getTime();
                    haveCardBean.startTime = dfBank.format(startD);
                } else {
                    Date startD = dfOur.parse(dateArray[0]);
                    Date endD = dfOur.parse(dateArray[1]);
                    haveCardBean.startTime = dfBank.format(startD);
                    haveCardBean.endTime = dfBank.format(endD);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //返回错误信息

        }//年费
        if (pageDataBean.yearMoneyArray != null) {
            haveCardBean.yearMoney = pageDataBean.listYearMoneyArray.get(0).flagId;
        } else {
            haveCardBean.yearMoney = "";
        }
        haveCardBean.zhName = bean.getModel().getCname();
        haveCardBean.enName = bean.getModel().getCenglishname();
        haveCardBean.haveGift = "";
        haveCardBean.idCard = bean.getModel().getCidcard();
        haveCardBean.isRecommend = "1";
        haveCardBean.recommendNum = "";
        haveCardBean.issuingAuthority = bean.getModel().getCidissueaddress();
        haveCardBean.myemail2 = bean.getModel().getCemail();
        haveCardBean.phoneServicePWD = "";
        haveCardBean.regin = unitInfoBean.countySelectCode;
        //以下可不填
        haveCardBean.spid = "";
        haveCardBean.spname = "";
        haveCardBean.sptxtnm = "";
    }

    /**
     * 汉字转拼音
     *
     * @param strCN
     * @return
     */
    private String hanZiToPinyin(String strCN) {
        String strEN = httpRequest.sendPost(URL_HANZI_TO_PINYIN, "{\"name\":\"" + strCN + "\"}:", cookieString);
        if (DEBUG) {
            System.out.println("汉字转拼音:" + strCN + " - " + strEN);
        }
        JSONObject obj = JSON.parseObject(strEN);
        if (obj.getString("code").equals("0")) {
            //成功
            strEN = obj.getString("name");
        } else {
            strEN = "";
        }
        return strEN;
    }

    /**
     * 发送手机验证码
     *
     * @return 是否成功
     */
    private String sendPhoneVerifyCode(String phoneNum) {
        String jsonData = "{\"phone\":\"" + phoneNum + "\"}";
        String result = httpRequest.sendPost(URL_SEND_PHONE_CODE, jsonData, cookieString);
        if (DEBUG) {
            System.out.println("发送手机验证码请求响应结果:\n     " + result);
        }
        return result;
    }

    /**
     * 流程
     * 1.发送短信验证码
     * 2.检测新老客户,保存返回的数据
     *   a。老客户
     *     1.填充新信用卡信息
     *     2.提交验证
     *   b.新客户
     *     1.填写住宅地址、联系方式
     *     2.公司信息及办卡方式
     *     3.直系亲属，紧急联系人资料
     *     ---到此资料填写完毕---
     *     4.新卡提交验证
     */

    /**
     * 检测新老客户 第一步
     * post请求返回信息"message","code"一定有，如果code是-1，取message错误信息：
     * {"message":"error-系统维护中","code":-1}
     * {"isRegularCustomer":"false","message":"phoneNum-请输入正确的信息","code":-1}
     * {"isRegularCustomer":"false","message":"tipMessage-本产品主卡仅限女性申请，女性主卡客户可为男性直系亲属申请附属卡！","code":-1}
     */
    private String checkIsOldCustomer() {
        String baseJson = JSON.toJSONString(baseInfoBean);
        String result = httpRequest.sendPost(URL_BASE_INFO_1, baseJson, cookieString);
        if (DEBUG) {
            System.out.println("检测新老用户 Step 1:" + result);
        }
        return result;
    }

    /**
     * a.老用户
     */
    private String submitNewCardInfoforOldUser() {
        String newCardJson = JSON.toJSONString(haveCardBean);
        String result = httpRequest.sendPost(URL_HAVECARD_APPLY, newCardJson, cookieString);
        if (DEBUG) {
            System.out.println("老用户提交新卡校验 :" + result);
        }
        return result;
    }

    /**
     * b.新用户 第一步 填写住宅地址、联系方式
     */
    private String submitHouseInfo() {
        String houseJson = JSON.toJSONString(houseInfoBean);
        String result = httpRequest.sendPost(URL_HOUSE_INFO_2, houseJson, cookieString);
        if (DEBUG) {
            System.out.println("提交联系方式,住宅信息 Step 2:" + result);
        }
        return result;
    }

    /**
     * b.新用户 第二步 公司信息及办卡方式
     */
    private String submitUnitInfo() {
        String unitJson = JSON.toJSONString(unitInfoBean);
        String result = httpRequest.sendPost(URL_UNIT_INFO_3, unitJson, cookieString);
        if (DEBUG) {
            System.out.println("公司信息及办卡方式 Step 3:" + result);
        }
        return result;
    }

    /**
     * b.新用户 第三步 直系亲属,紧急联系人资料
     */
    private String submitContactInfo() {
        String contactJson = JSON.toJSONString(contactInfoBean);
        String result = httpRequest.sendPost(URL_CONTRACT_INFO_4, contactJson, cookieString);
        if (DEBUG) {
            System.out.println("直系亲属,紧急联系人资料 Step 4:" + result);
        }
        return result;
    }

    /**
     * b.新用户 第四步 新卡提交
     * 本次post不需要data
     */
    private String insertCard() {
        String result = httpRequest.sendPost(URL_SUBMIT_APPLY_5, "", cookieString);
        if (DEBUG) {
            System.out.println("新用户最后一步校验 Step 5:" + result);
        }
        return result;
    }


    /**
     * ******** Bean内部类
     */
    //个人基本信息类
    static class ZhongXinBaseInfoBean {
        public String cardType = "";//卡类型,"魔力银联金卡"
        public String yearMoney = "";//年费,默认为空""
        public String zhName;//中文名
        public String enName;//中文拼音(用空格隔开，大写，例:无敌 - WU DI)
        public String idCard;//身份证号
        public String issuingAuthority;//发证机关
        public String startTime;//证件有效时间(始) 格式:"2016/02/18"
        public String endTime;//证件有效时间(止) 格式:"2016/03/18"
        public String phone;//手机号码(必填)
        public String verifyCode;//手机动态码(必填)
        public String isRecommend;//是否他人推荐("1":否，"0":是,虽然很别扭，但是页面确实是这个1、0的关系)
        public String recommendNum;//推荐号,不推荐则该字段为空
    }

    //住宅联系地址类
    static class ZhongXinHouseInfoBean {
        public String spname;//会员名(可不填)
        public String spid;//会员号(可不填)
        public String sptxtnm;//会员号(可不填)
        public String provinceCode;//省、直辖市code
        public String cityCode;//地级市、区code
        public String areaCode;//县、县级市code
        public String provenceSelectName;//省、直辖市名
        public String citySelectName;//地级市、区名
        public String countySelectName;//县、县级市名
        public String address;//住宅地址
        public String housePostCode;//住宅邮编
        public String houseTelArea;//区号，可不填
        public String houseTel;//住宅电话，可不填
        public String email;//电子邮件，必填
    }
    //联系人信息类
    static class ZhongXinContactInfoBean {
        public String relativesName;//直系联系人姓名
        public String familyRelation;//直系联系人关系
        public String familyPhone;//直系亲属手机号
        public String addressTelArea;//直系亲属区号
        public String addressTel;//直系亲属电话
        public String emergencyContact;//紧急联系人姓名
        public String contactPhone;//紧急联系人手机号
        public String contactRelation;//紧急联系人关系
        public String contactTelAea;//紧急联系人区号
        public String contactTel;//紧急联系人电话
    }
    //公司信息类
    class ZhongXinUnitInfoBean {
        public String unitName;//单位名称，必填
        public String unitNature;//单位性质
        public String postNature;//岗位性质
        public String provenceSelectCode;//省、直辖市code
        public String citySelectCode;//地级市、区code
        public String countySelectCode;//县、县级市code
        public String provenceSelectName;//省、直辖市名
        public String citySelectName;//地级市、区名
        public String countySelectName;//县、县级市名
        public String unitAddress;//单位地址
        public String unitPostCode;//单位地址邮编
        public String unitTelArea;//单位区号
        public String unitTel;//单位电话
        public String wayToCard;//办卡方式
        public String regin;//公司区域
        public String latticePoint;//公司网点
        public String ebankWayYZ;//办卡方式代码
        public String ebankWayXC;//办卡方式代码
    }

    //老用户新卡提交验证信息
    class ZhongXinHaveCardInfoBean {
        public String cardType;//卡类型
        public String yearMoney;//年费类型标准
        public String zhName;//中文名
        public String enName;//中文拼音
        public String idCard;//身份证号
        public String issuingAuthority;//发证机关
        public String startTime;//有效时间(始)
        public String endTime;//有效时间(止)
        public String isRecommend;//是否他人推荐,("1":否，"0":是,虽然很别扭，但是页面确实是这个1、0的关系)
        public String recommendNum;//推荐号，不推荐，则为空
        public String spname;//会员号，可不填
        public String spid;//会员号，可不填
        public String sptxtnm;//会员号可不填
        public String myemail2;//邮件地址
        public String phoneServicePWD;//电话服务密码
        public String wayToCard;//办卡方式
        public String regin;//公司区域
        public String latticePoint;//公司网点
        public String ebankWayYZ;//办卡方式代码
        public String ebankWayXC;//办卡方式代码
        public String haveGift;//公司礼品
    }

    public static MemCachedClient getMemCachedClient() {
        MemCachedClient client = new MemCachedClient();
        String[] addr = {"192.168.1.232:11211", "192.168.1.232:11211"};
        Integer[] weights = {10, 0};
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(addr);
        pool.setWeights(weights);
        pool.setInitConn(5);
        pool.setMinConn(5);
        pool.setMaxConn(200);
        pool.setMaxIdle(1000 * 30 * 30);
        pool.setMaintSleep(30);
        pool.setNagle(false);
        pool.setSocketTO(30);
        pool.setSocketConnectTO(0);
        pool.initialize();
        return client;
    }

}
