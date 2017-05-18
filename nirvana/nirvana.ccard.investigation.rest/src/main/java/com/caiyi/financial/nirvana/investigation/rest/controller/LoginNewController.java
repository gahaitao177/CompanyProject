package com.caiyi.financial.nirvana.investigation.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.discount.utils.CaiyiEncryptIOS;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.caiyi.financial.nirvana.investigation.base.BaseLoginController;
import com.caiyi.financial.nirvana.investigation.util.InvestigationHelper;
import com.hsk.cardUtil.HpClientUtil;
import com.util.string.StringUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Controller
//@RequestMapping("/credit")
public class LoginNewController extends BaseLoginController{
    /**
     * 获取验证码
     *
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxVerifyCodeNew.go")
//    @RequestMapping("/zxVerifyCodeNew.go")
    public void getVerifyCode(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String base64="";
        String decodeBase64="";
        Map<String,String> map=new HashMap<>();
        logger.info("cuserId====================="+bean.getCuserId());
        getBase64Img(bean, request, response);
        if (bean.getBusiErrCode()==1){
            //加载图片成功，自动解析图片
            base64=bean.getSign();
            decodeBase64=InvestigationHelper.decodeYzm(base64, "31", "1");
            bean.setCode(decodeBase64);
            map.put("code", String.valueOf(bean.getBusiErrCode()));
            map.put("desc", bean.getBusiErrDesc());
            map.put("base64img", bean.getSign());
            map.put("decodeBase64img", bean.getCode());
        }else{
            //加载图片失败
            map.put("code", "0");
            map.put("desc", bean.getBusiErrDesc());
        }
        XmlUtils.writeJson(JSONObject.toJSON(map).toString(), response);
    }

    /**
     * 征信登录
     *
     * @param bean
     * @param request
     */
    @Override
    @RequestMapping("/control/investigation/zxLoginNew.go")
//    @RequestMapping("/zxLoginNew.go")
    public void investLogin(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investLogin(bean, request, response);
        responseJson(bean, response);

    }

    @RequestMapping("/control/investigation/zxAutologon.go")
//    @RequestMapping("/zxAutologon.go")
    public void investAutologon(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String base64="";
        String decodeBase64="";
        Map<String,String> map=new HashMap<>();
        bean.setFrom("Autologon");
        try {
            if ("1".equals(bean.getIskeep())){
                Object lObject=memCachedClient.get(bean.getCuserId() + "zhenxinLoginname");
                Object pObject=memCachedClient.get(bean.getCuserId() + "zhenxinPwdword");
                if (lObject==null||pObject==null){
                    map.put("code", "0");
                    map.put("desc", "登录页面失效，请返回登录页面登录");
                }else{
                    String loginname= (String) lObject;
                    String pwd= (String) pObject;

                    if ("1".equals(bean.getClient())) {
                        bean.setLoginname(CaiyiEncryptIOS.encryptStr(loginname));
                        bean.setPassword(CaiyiEncryptIOS.encryptStr(pwd));
                    } else {
                        bean.setLoginname(CaiyiEncrypt.encryptStr(loginname));
                        bean.setPassword(CaiyiEncrypt.encryptStr(pwd));
                    }
                    super.investLogin(bean, request, response);
                    logger.info("Autologon["+bean.getCuserId()+"] code["+bean.getBusiErrCode()+"] desc["+bean.getBusiErrDesc()+"]");
                    if (bean.getBusiErrCode()==0){
                        if ("验证码输入错误,请重新输入".equals(bean.getBusiErrDesc())||bean.getBusiErrDesc().contains("验证码")){
                            bean.setType("0");
                            getBase64Img(bean, request, response);
                            if (bean.getBusiErrCode()==1){
                                base64=bean.getSign();
                                decodeBase64=InvestigationHelper.decodeYzm(base64, "31", "1");
                                bean.setCode(decodeBase64);
                                map.put("code", "2");
                                map.put("desc",bean.getBusiErrDesc());
                                map.put("base64img", bean.getSign());
                                map.put("decodeBase64img", bean.getCode());
                                memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", loginname, 1000 * 60 * 50);
                                memCachedClient.set(bean.getCuserId() + "zhenxinPwdword", pwd, 1000 * 60 * 50);
                            }else{
                                //加载图片失败
                                map.put("code", "0");
                                map.put("desc", bean.getBusiErrDesc());
                            }
                        }else{
                            map.put("code", "0");
                            map.put("desc", bean.getBusiErrDesc());
                        }
                    }else{
                        //登录成功
                        map.put("code", "1");
                        map.put("desc", bean.getBusiErrDesc());
                        sendAgain(bean, request);
                    }

                }
            }else{
                String jsonRes =client.execute(new DrpcRequest("investLogin", "queryUserStatus", bean));
                JSONObject loginjs=JSON.parseObject(jsonRes);
                String code=loginjs.getString("code");
                if ("0".equals(code)){
                    map.put("code", "0");
                    map.put("desc", loginjs.getString("cdesc"));
                    XmlUtils.writeJson(JSONObject.toJSON(map).toString(), response);
                    return;
                }
                String loginname=loginjs.getString("loginname");
                String pwd=loginjs.getString("loginpwd");
                memCachedClient.set(bean.getCuserId() + "zhenxinLoginname", loginname, 1000 * 60 * 50);
                memCachedClient.set(bean.getCuserId() + "zhenxinPwdword", pwd, 1000 * 60 * 50);
                if ("1".equals(bean.getClient())) {
                    bean.setLoginname(CaiyiEncryptIOS.encryptStr(loginname));
                    bean.setPassword(CaiyiEncryptIOS.encryptStr(pwd));
                } else {
                    bean.setLoginname(CaiyiEncrypt.encryptStr(loginname));
                    bean.setPassword(CaiyiEncrypt.encryptStr(pwd));
                    bean.setClient("0");
                }

                for (int i = 1; i <=res_picNums; i++) {
                    map.clear();
                    bean.setType("0");
                    getBase64Img(bean, request, response);
                    if (bean.getBusiErrCode()==1){
                        //加载图片成功，自动解析图片
                        base64=bean.getSign();
                        decodeBase64=InvestigationHelper.decodeYzm(base64, "31", "1");
                        bean.setCode(decodeBase64);
                        logger.info("decodeBase64" + decodeBase64);
                        super.investLogin(bean, request, response);
                        logger.info("bean.getBusiErrCode()=" + bean.getBusiErrCode());
                        logger.info("bean.getBusiErrDesc()="+bean.getBusiErrDesc());

                        if (bean.getBusiErrCode()==0){
                            if (bean.getBusiErrDesc().contains("验证码")){
                                if (i==res_picNums){
                                    getBase64Img(bean, request, response);
                                    if (bean.getBusiErrCode()==1){
                                        base64=bean.getSign();
                                        decodeBase64=InvestigationHelper.decodeYzm(base64, "31", "1");
                                        bean.setCode(decodeBase64);
                                        map.put("code", "2");
                                        map.put("desc", bean.getBusiErrDesc());
                                        map.put("base64img", bean.getSign());
                                        map.put("decodeBase64img", bean.getCode());
                                    }else{
                                        //加载图片失败
                                        map.put("code", "0");
                                        map.put("desc", bean.getBusiErrDesc());
                                    }
                                }
                            }else{
                                map.put("code", "0");
                                map.put("desc", bean.getBusiErrDesc());
                                break;
                            }
                        }else{
                            //登录成功
                            map.put("code", "1");
                            map.put("desc", bean.getBusiErrDesc());
                            sendAgain(bean,request);
                            break;
                        }
                    }else{
                        //加载图片失败
                        map.put("code", "0");
                        map.put("desc", bean.getBusiErrDesc());
                    }
                }

            }
        }catch (Exception e){
            logger.error(bean.getCuserId()+" investAutologon 异常",e);
            map.put("code", "0");
            map.put("desc", "登录失败");
        }
        XmlUtils.writeJson(JSONObject.toJSON(map).toString(), response);
    }

    private boolean sendAgain(Channel bean,HttpServletRequest request){
        if (StringUtil.isEmpty(bean.getTaskId())||"-1".equals(bean.getTaskId())){
            return false;
        }
        String jsonRes =client.execute(new DrpcRequest("investLogin", "queryUserStatus", bean));
        JSONObject loginjs=JSON.parseObject(jsonRes);
        String code=loginjs.getString("code");
        if ("1".equals(code)){
            String status=loginjs.getString("status");
            if (status.equals("20")||status.equals("30")||status.equals("60")||status.equals("70")){

                memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
                Object cookieObj = memCachedClient.get(bean.getCuserId() + "zhenxinLoginCookie");
                Object sendAgainflag = memCachedClient.get(bean.getCuserId() + bean.getLoginname() +"sendAgain");
                if (sendAgainflag!=null&&"1".equals(String.valueOf(sendAgainflag))){
                    logger.info(bean.getCuserId()+" sendAgain fail TaskId["+bean.getTaskId()+"] 近一小时内已发送过["+String.valueOf(sendAgainflag)+"]");
                    return false;
                }
                if (cookieObj == null) {
                    return false;
                }
                CookieStore cookieStore = (CookieStore) cookieObj;
                CloseableHttpClient httpClient = HpClientUtil.getHttpsClient();
                HttpContext localContext = new BasicHttpContext();
                RequestConfig requestConfig = InvestigationHelper.getRequestConfig();
                Map<String, String> requestHeaderMap = InvestigationHelper.getHeaderMap();
                Map<String, String> parames = new HashMap<String, String>();
                localContext.setAttribute("http.cookie-store", cookieStore);
                String userIp = InvestigationHelper.getRealIp(request).trim();
                if (!CheckUtil.isNullString(userIp)) {
                    requestHeaderMap.put("X-Forwarded-For", userIp);
                }
                String url = "https://ipcrs.pbccrc.org.cn/reportAction.do?num="+Math.random();
                String errcontent = "";
                requestHeaderMap.put("Referer", "https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport");
                parames.put("method","sendAgain");
                parames.put("reportformat",bean.getTaskId());
                errcontent = HpClientUtil.httpPost(url, requestHeaderMap, parames, httpClient, localContext, "gbk", requestConfig);
                if (errcontent.contains("success")){
                    memCachedClient.set(bean.getCuserId() + "zhenxinLoginCookie",cookieObj);
                    memCachedClient.set(bean.getCuserId() + bean.getLoginname() +"sendAgain", "1", 1000 * 60 * 60);
                    logger.info(bean.getCuserId()+" sendAgain TaskId["+bean.getTaskId()+"] success 重新发送身份证验证码成功"+errcontent);
                    return true;
                }else {
                    logger.info(bean.getCuserId()+" sendAgain fail TaskId["+bean.getTaskId()+"] 重新发送身份证验证码失败"+errcontent);
                    return false;
                }
            }else{
                logger.info(bean.getCuserId()+" sendAgain fail 重新发送身份证验证码失败 不符合发送条件status="+status);
            }
        }
        return false;
    }



    /**
     * 征信登出
     * @param bean
     * @param request
     * @param response
     */
    @Override
    @RequestMapping("/control/investigation/zxLoginOutNew.go")
//    @RequestMapping("/zxLoginOutNew.go")
    public void investLoginOut(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            super.investLoginOut(bean,request,response);
            responseJson(bean, response);
        }catch (Exception e){
            logger.error("investLoginOut 异常", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求异常");
            responseJson(bean, response);
        }
    }






    @RequestMapping("/control/investigation/queryUserStatus.go")
//    @RequestMapping("/queryUserStatus.go")
    public void queryUserStatus(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            String jsonRes =client.execute(new DrpcRequest("investLogin", "queryUserStatus", bean));
            XmlUtils.writeJson(jsonRes, response);
        }catch (Exception e){
            logger.error("queryUserStatus 异常",e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求异常");
            responseJson(bean, response);
        }

    }

    @RequestMapping("/control/investigation/updateUserStatus.go")
//    @RequestMapping("/updateUserStatus.go")
    public void updateUserStatus(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            if (CheckUtil.isNullString(bean.getReadStatus())){
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("更新失败");
                return;
            }
            String jsonRes =client.execute(new DrpcRequest("investLogin", "updateStatus", bean));
            logger.info(bean.getCuserId()+" jsonRes="+jsonRes);
            if ("1".equals(jsonRes)){
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("更新成功");
            }else{
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("更新失败");
            }
        }catch (Exception e){
            logger.error("queryUserStatus 异常", e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求异常");
        }finally {
            responseJson(bean, response);
        }
    }
}
