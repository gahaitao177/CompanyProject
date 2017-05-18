package com.caiyi.nirvana.analyse;

import com.alibaba.fastjson.JSONObject;
import com.caipiao.memcache.CacheClient;
import com.caiyi.nirvana.analyse.common.HttpClientUtil;
import com.caiyi.nirvana.analyse.token.TokenBean;
import com.caiyi.nirvana.analyse.token.TokenGenerator;
import com.caiyi.nirvana.analyse.user.UserBean;
import com.caiyi.nirvana.analyse.util.Constants;
import com.caiyi.nirvana.analyse.util.DRPCSupport;
import com.caiyi.nirvana.analyse.util.UrlConfig;
import com.mina.rbc.util.CheckUtil;
import com.mina.rbc.util.StringUtil;
import com.mina.rbc.util.xml.JXmlWapper;
import com.rbc.frame.ServiceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


public abstract class BaseService implements DRPCSupport {

    public Logger logger = LogManager.getLogger(getClass());

    public BaseService() {
    }

    public static final String ENCODING = "UTF-8";
    //登录编号
    public static Map<String, String> loginhezuo = new HashMap<String, String>();


    static {
        loginhezuo.put("130313001", "A9FK25RHT487ULMI");//Android
        loginhezuo.put("130313002", "A9FK25RHT487ULMI");//IOS
        loginhezuo.put("130313003", "A9FK25RHT487ULMI");//H5
    }


    // 检查登录
    public int check_login(BaseBean bean, ServiceContext context,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        set_user_data(bean, context, request, response);
        if (CheckUtil.isNullString(bean.getUid()) || CheckUtil.isNullString(bean.getPwd())) {
            bean.setBusiErrCode(9009);
            bean.setBusiErrDesc("用户未登录");
            return 0;
        }
        return 1;
    }


    // set token信息
    public int set_user_data(BaseBean bean, ServiceContext context, HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        if (StringUtil.isEmpty(bean.getAccessToken()) || StringUtil.isEmpty(bean.getAppId())) {
            logger.info("token为空,用户未登陆");
            return 1;
        }
        String[] result = TokenGenerator.authToken(bean.getAccessToken(), bean.getAppId());
        for (int i = 0; i < result.length; i++) {
            logger.info(result[i]);
        }
        if ("1".equals(result[0])) {
            TokenBean token = null;
            CacheClient cc = CacheClient.getInstance();
            Object obj = cc.get(bean.getAppId());
            if (obj != null) {// 30分钟内使用过该token
                token = (TokenBean) obj;
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("token cache");
                bean.setUid(token.getUid());
                bean.setPwd(token.getPwd());
                bean.setCuserId(token.getCuserId());
                bean.setParamJson(token.getParamJson());
            } else {

                Map<String, String> mapParams = new HashMap<>();
                mapParams.put("accesstoken", bean.getAccessToken().replace("\\+", "%2B"));
                mapParams.put("appid", bean.getAppId());
                String responseStr = HttpClientUtil.callHttpPost_Map(UrlConfig.GETUSERTOKEN, mapParams);
                logger.info("url-->" + UrlConfig.GETUSERTOKEN + " ;para:" + mapParams.toString());
                logger.info("responseStr=" + responseStr);
                if (StringUtil.isEmpty(responseStr)) {
                    bean.setBusiErrCode(9000);
                    bean.setBusiErrDesc("网络异常,请稍后再试");
                    return 0;
                }
                JXmlWapper xml = JXmlWapper.parse(responseStr);
                int code = Integer.parseInt(xml.getXmlRoot().getAttributeValue("code"));
                if (code == 0) {// 彩票客户端 code = 0 代表访问成功 夺宝 code = 1 代表访问成功
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc(xml.getXmlRoot().getAttributeValue("desc"));
                    bean.setUid(xml.getXmlRoot().getChild("row").getAttributeValue("uid"));
                    bean.setPwd(xml.getXmlRoot().getChild("row").getAttributeValue("pwd"));
                    bean.setParamJson(xml.getXmlRoot().getChild("row").getAttributeValue("paramJson"));
                } else {
                    bean.setBusiErrCode(code);
                    bean.setBusiErrDesc(xml.getXmlRoot().getAttributeValue("desc"));
                    return 0;
                }
                UserBean bean1 = new UserBean();
                bean1.setUid(bean.getUid());
                bean1.setService("queryUserByUid");
                String rt = (String) doBussiness(Constants.TOPOLOGY_NAME, JSONObject.toJSONString(bean1)).get(RESULT);
                JSONObject json = JSONObject.parseObject(rt);
                if (json.containsKey("ccpuserid")) {
                    bean1.setCuserid(json.getString("cuserid"));
                    bean1.setBusiErrCode(1);
                }

                int rc = 0;
                if (rc == 0 && bean1.getBusiErrCode() == 1) {
                    bean.setCuserId(bean1.getCuserId());

                    token = new TokenBean();
                    token.setUid(bean.getUid());
                    token.setPwd(bean.getPwd());
                    token.setCuserId(bean1.getCuserId());

                    token.setAccessToken(bean.getAccessToken());
                    token.setAppId(bean.getAppId());
                    token.setParamJson(bean.getParamJson());

                    boolean flag = cc.set(bean.getAppId(), token, Constants.TIME_HALFHOUR); // 放入缓存中30分钟
                    logger.info("缓存token信息 =" + bean.getAppId() + "  结果:" + flag);
                } else {
                    logger.info("token登录失败:appid =" + bean.getAppId() + " desc:" + bean1.getBusiErrDesc());
                    bean.setBusiErrCode(9000);
                    bean.setBusiErrDesc("程序异常,登录失败");
                    return 0;
                }
            }
        } else {
            bean.setBusiErrCode(9007);
            bean.setBusiErrDesc(result[1]);
            return 0;
        }
        return 1;
    }

}
