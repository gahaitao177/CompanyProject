package com.caiyi.financial.nirvana.discount.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.utils.HttpClientUtil;
import com.caiyi.financial.nirvana.http.ParameterRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Created by lizhijie on 2016/8/18.
 */
@MVCComponent
public class TokenUtil2 {
    private static Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    private static final String BASE_URL = SystemConfig.get("user_http_url");

    // @Autowired
   // private UserCenter userCenter;
/*
    9001   token已注销
    9002   验证失败,token已过期
    9003   密码已修改,请重新登录
    9004   账户已禁用
    9005   未查到相关token记录
    9006   查询token信息出错
    9007   token解析异常
    9009   用户未登录
*/
    public void setUserData( BaseBean base){
        base.setCuserId("");
        base.setPwd("");
        base.setBusiErrCode(1);
        if (StringUtils.isEmpty(base.getAccessToken()) || StringUtils.isEmpty(base.getAppId())) {
            logger.info(" bean.getAccessToken() :"+ base.getAccessToken());
            logger.info(" bean.getAppId() :"+ base.getAppId());
            base.setBusiErrCode(0);
            base.setBusiErrDesc("验证失败");
            return ;
        }
//        BaseBean tokenInfo = getUserByToken(base);
        getHskUserByBaseBean(base);
//        if (tokenInfo.getBusiErrCode() == 1) {
//            base.setCuserId(tokenInfo.getCuserId());
//            base.setPwd(tokenInfo.getPwd());
//            base.setPwd9188(tokenInfo.getPwd9188());
//            base.setParamJson(tokenInfo.getParamJson());
//            base.setBusiErrCode(1);
//            base.setBusiErrDesc("success");
//            logger.info("request======userid:" + tokenInfo.getCuserId());
//        } else {
//            base.setBusiErrCode(tokenInfo.getBusiErrCode());
//            base.setBusiErrDesc(tokenInfo.getBusiErrDesc());
//        }

    }

    /**
     * 获取token数据
     * @param request
     * @param response
     * @param base
     */
    public void setUserData(ParameterRequestWrapper request, HttpServletResponse response, BaseBean base){
        String token = request.getParameter("accessToken");
        String appId = request.getParameter("appId");//不用了
        String iclient = request.getParameter("iclient");
        String source = request.getParameter("source");
        String imei = request.getParameter("imei");
        String appversion = request.getParameter("appversion");
        String appVersion = request.getParameter("appVersion");
        String ipAddr = getRealIp(request).trim();

        if (StringUtils.isEmpty(base.getAccessToken())) {
            base.setAccessToken(token);
        }
        if (null == base.getIclient()&&StringUtils.isNotEmpty(iclient)) {
            base.setIclient(Integer.parseInt(iclient));
        }
        if (null == base.getSource()&&StringUtils.isNotEmpty(source)) {
            base.setSource(Integer.parseInt(source));
        }
        if (StringUtils.isEmpty(base.getImei())&&StringUtils.isNotEmpty(imei)) {
            base.setImei(imei);
        }
        if (StringUtils.isEmpty(base.getAppversion())&&StringUtils.isNotEmpty(appversion)) {
            base.setAppversion(appversion);
        }
        if (StringUtils.isEmpty(base.getAppVersion())&&StringUtils.isNotEmpty(appVersion)) {
            base.setAppversion(appVersion);
        }
        if (StringUtils.isEmpty(base.getIpAddr())&&StringUtils.isNotEmpty(ipAddr)) {
            base.setAppversion(ipAddr);
        }

        base.setBusiErrCode(1);

        if (StringUtils.isEmpty(token)) {
            base.setBusiErrCode(0);
            base.setBusiErrDesc("token不能为空");
            return ;
        }
        getHskUserByBaseBean(base);
    }

/*    *//**
     *
     * @param baseBean
     * @return
     *//*
    public BaseBean getUserByToken(BaseBean baseBean){
        UserInfoRsp infoRsp = userCenter.getUserByToken(baseBean);

        String code = infoRsp.getResponseCode();
        String desc = infoRsp.getResponseMessage();
        com.caiyi.user.domain.base.User user = infoRsp.getUser();

        if (UserCenter.SUCCESSCODE.equals(code) && null != user) {//操作成功
            baseBean.setCuserId(user.getUserId());
            baseBean.setPwd("pwd");
            baseBean.setPwd9188(user.getPassword());
            baseBean.setBusiErrCode(1);
            baseBean.setBusiErrDesc("success");

        } else {
            baseBean.setBusiErrCode(9009);
            baseBean.setBusiErrDesc("用户未登录");
        }
        return baseBean;
    }*/

    /**
     * 获取IP
     * @param request
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (ip.indexOf("192.168") > -1 || ip.indexOf("127.0.0") > -1) {
            String xf = request.getHeader("X-Forwarded-For");
            if(xf != null){
                ip = xf.split(",")[0];
            }
        }
        return ip;
    }
    /**
     * 通过appid和token获得userDto
     * @return
     */
    public static void getHskUserByBaseBean(BaseBean baseBean){
        HashMap<String,String> params=new HashMap<>();
        String resetUrl = BASE_URL + "queryToken";
        params.put("accessToken",baseBean.getAccessToken());
        params.put("appId",baseBean.getAppId());
        params.put("cloginfrom","HSK");
        params.put("csource", String.valueOf(baseBean.getSource()));
        params.put("ipAddr",baseBean.getIpAddr());
        if(baseBean.getIclient()==-1){
            baseBean.setIclient(0);
        }
        params.put("mobileType",String.valueOf(baseBean.getIclient()));
        params.put("packageName",baseBean.getPackagename());
        String result= HttpClientUtil.callHttpPost_Map(resetUrl,params);
        logger.info("把token转化成userid的结果:"+result);
        JSONObject jsonObject= JSON.parseObject(result);
        if(jsonObject!=null){
            if("1".equals(jsonObject.getString("code"))){
                JSONObject data=jsonObject.getJSONObject("data");
                if(data!=null){
                    baseBean.setCuserId(data.getString("cuserId"));
                    baseBean.setCnickname(data.getString("cnickId"));
                }else {
                    baseBean.setBusiErrDesc("查询token信息出错");
                    baseBean.setBusiErrCode(9006);
                }
            }else {
                baseBean.setBusiErrDesc(jsonObject.getString("desc"));
                baseBean.setBusiErrCode(Integer.parseInt(jsonObject.getString("code")));
            }
        }else {
            baseBean.setBusiErrDesc("查询token信息出错");
            baseBean.setBusiErrCode(9006);
        }
    }
}
