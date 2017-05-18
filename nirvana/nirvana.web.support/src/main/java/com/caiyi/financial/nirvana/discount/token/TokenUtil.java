package com.caiyi.financial.nirvana.discount.token;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuweiguo on 2016/8/18.
 */
//@MVCComponent
public class TokenUtil {
    //private static Logger logger = LoggerFactory.getLogger(TokenUtil.class);
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
/**
    public void setUserData( BaseBean base){
        base.setCuserId("");
        base.setPwd("");
        base.setBusiErrCode(1);
        if (StringUtil.isEmpty(base.getAccessToken()) || StringUtil.isEmpty(base.getAppId())) {
            logger.info(" bean.getAccessToken() :"+ base.getAccessToken());
            logger.info(" bean.getAppId() :"+ base.getAppId());
            base.setBusiErrCode(0);
            base.setBusiErrDesc("验证失败");
            return ;
        }
        BaseBean tokenInfo = getUserByToken(base);
        if (tokenInfo.getBusiErrCode() == 1) {
            base.setCuserId(tokenInfo.getCuserId());
            base.setPwd(tokenInfo.getPwd());
            base.setPwd9188(tokenInfo.getPwd9188());
            base.setParamJson(tokenInfo.getParamJson());
            base.setBusiErrCode(1);
            base.setBusiErrDesc("success");
            logger.info("request======userid:" + tokenInfo.getCuserId());
        } else {
            base.setBusiErrCode(tokenInfo.getBusiErrCode());
            base.setBusiErrDesc(tokenInfo.getBusiErrDesc());
        }

    }

    /**
     * 获取token数据
     * @param request
     * @param response
     * @param base
     */
/**
    public void setUserData(ParameterRequestWrapper request, HttpServletResponse response, BaseBean base){
        String token = request.getParameter("accessToken");
        String appId = request.getParameter("appId");//不用了
        String iclient = request.getParameter("iclient");
        String source = request.getParameter("source");
        String imei = request.getParameter("imei");
        String appversion = request.getParameter("appversion");
        String appVersion = request.getParameter("appVersion");
        String ipAddr = getRealIp(request).trim();

        if (StringUtil.isEmpty(base.getAccessToken())) {
            base.setAccessToken(token);
        }
        if (null == base.getIclient()&&StringUtil.isNotEmpty(iclient)) {
            base.setIclient(Integer.parseInt(iclient));
        }
        if (null == base.getSource()&&StringUtil.isNotEmpty(source)) {
            base.setSource(Integer.parseInt(source));
        }
        if (StringUtil.isEmpty(base.getImei())&&StringUtil.isNotEmpty(imei)) {
            base.setImei(imei);
        }
        if (StringUtil.isEmpty(base.getAppversion())&&StringUtil.isNotEmpty(appversion)) {
            base.setAppversion(appversion);
        }
        if (StringUtil.isEmpty(base.getAppVersion())&&StringUtil.isNotEmpty(appVersion)) {
            base.setAppversion(appVersion);
        }
        if (StringUtil.isEmpty(base.getIpAddr())&&StringUtil.isNotEmpty(ipAddr)) {
            base.setAppversion(ipAddr);
        }

        base.setBusiErrCode(1);

        if (StringUtil.isEmpty(token)) {
            System.out.println(" bean.getAccessToken() :"+ token);
            System.out.println(" bean.getAppId() :"+ appId);
            return ;
        }

        BaseBean tokenInfo = getUserByToken(base);
        if (tokenInfo.getBusiErrCode() == 1) {

            base.setIpAddr(ipAddr);

            base.setCuserId(tokenInfo.getCuserId());
            base.setPwd(tokenInfo.getPwd());
            base.setPwd9188(tokenInfo.getPwd9188());
            base.setParamJson(tokenInfo.getParamJson());
            base.setBusiErrCode(1);
            base.setBusiErrDesc("success");

            logger.info("request======userid:" + tokenInfo.getCuserId());
            request.setParameter("cuserId",tokenInfo.getCuserId());
            request.setParameter("pwd",tokenInfo.getPwd());
            request.setParameter("pwd9188",tokenInfo.getPwd9188());
            request.setParameter("busiErrCode","1");
            request.setParameter("busiErrDesc","success");
        } else {
            base.setBusiErrCode(tokenInfo.getBusiErrCode());
            base.setBusiErrDesc(tokenInfo.getBusiErrDesc());
            request.setParameter("busiErrCode",tokenInfo.getBusiErrCode()+"");
            request.setParameter("busiErrDesc",tokenInfo.getBusiErrDesc());
        }
    }
**/

    /**
     *
     * @param baseBean
     * @return
     */
    /**
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
    }**/

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

}
