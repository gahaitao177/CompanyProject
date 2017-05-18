package com.caiyi.financial.nirvana.discount.utils;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.annotation.MVCComponent;
import com.caiyi.financial.nirvana.core.bean.BaseBean;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.token.TokenGenerator;
import com.caiyi.financial.nirvana.core.util.token.UniqueStrCreator;
import com.caiyi.financial.nirvana.discount.Constants;
import com.caiyi.financial.nirvana.discount.token.TokenBean;
import com.caiyi.financial.nirvana.discount.token.TokenUtil2;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.exception.UserException;
import com.caiyi.financial.nirvana.http.ParameterRequestWrapper;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by heshaohua on 2016/6/12.
 * <p>
 * <p>
 * com.caiyi.financial.nirvana.discount.token.TokenUtil
 */
@MVCComponent
public class LoginUtil {
    private final static Logger logger = LoggerFactory.getLogger(LoginUtil.class);
    @Autowired
    MemCachedClient memCachedClient;

    @Resource(name = Constant.HSK_USER)
    IDrpcClient client;

    /**
     * 旧版本token解析  websocket用
     *
     * @param base
     * @return add by lcs 20160921
     */
    public BaseBean getUserByToken(BaseBean base) {
        TokenDto dto = TokenDto.parseBaseBean(base);
        try {
            checkToken(base);
//            base = dto.makeBaseBean(base);
        } catch (UserException e) {
            base.setBusiErrCode(Integer.valueOf(e.getCode()));
            base.setBusiErrDesc(e.getMessage());
        } catch (Exception e) {
            base.setBusiErrCode(9007);
            base.setBusiErrDesc("token验证失败");
        }
        return base;
    }

    /**
     * 获取token数据
     *
     * @param request
     * @param response
     * @return
     */
    public void getUserData(ParameterRequestWrapper request, HttpServletResponse response, BaseBean base) {
        String token = request.getParameter("accessToken");
        String appId = request.getParameter("appId");
        String cuserId = request.getParameter("cuserId");
        String ipAddr = WebUtil.getRealIp(request).trim();
        base.setAccessToken(token);
        base.setAppId(appId);
        base.setIpAddr(ipAddr);
        base.setCuserId(cuserId);
        getUserByToken(base);
        request.setParameter("cuserId", base.getCuserId());
        request.setParameter("copenid", base.getCopenid());
//        request.setParameter("ilendid", base.getIlendid());
//        request.setParameter("isreal", base.getIsreal());
//        request.setParameter("pwd", base.getPwd());
//        request.setParameter("pwd9188", base.getPwd9188());
    }

    /**
     * 新版获取token数据(兼容旧版公共请求参数、header中新版公共请求参数)
     * add by shaoqinghua 20170406
     *
     * @param request
     * @param response
     * @param base
     */
    public void getUserDataNew(ParameterRequestWrapper request, HttpServletResponse response, BaseBean base) {
        String accessToken = request.getParameter("accessToken");
        String token = request.getHeader("token");
        String ipAddr = WebUtil.getRealIp(request).trim();
        if (StringUtils.isNotEmpty(accessToken)) {
            //旧版公共请求参数
            String appId = request.getParameter("appId");
            String cuserId = request.getParameter("cuserId");
            base.setAccessToken(accessToken);
            base.setAppId(appId);
            base.setIpAddr(ipAddr);
            base.setCuserId(cuserId);
            getUserByToken(base);
        } else if (!CheckUtil.isNullString(token)) {
            //新版公共请求参数在header中
            String appId = request.getHeader("appId");
            String cuserId = request.getParameter("cuserId");
            base.setAccessToken(token);
            base.setAppId(appId);
            base.setIpAddr(ipAddr);
            base.setCuserId(cuserId);
            getUserByToken(base);
        }
        request.setParameter("cuserId", base.getCuserId());
        request.setParameter("copenid", base.getCopenid());
    }

    public boolean getUserData(ParameterRequestWrapper request, HttpServletResponse response) {
        String token = request.getParameter("accessToken");
        String appId = request.getParameter("appId");
        String ipAddr = WebUtil.getRealIp(request).trim();
        BaseBean paramDto = new BaseBean();
        paramDto.setAccessToken(token);
        paramDto.setAppId(appId);
        paramDto.setIpAddr(ipAddr);

        TokenDto dto = checkToken(paramDto);

        request.setParameter("cuserId", dto.getCuserId());
//        request.setParameter("copenid", dto.getCopenid());
//        request.setParameter("isreal", dto.getIsreal());
//        request.setParameter("pwd", dto.getCpassword());
//        request.setParameter("pwd9188", dto.getCpwd9188());
        return true;
    }

    /**
     * 新版获取用户数据(兼容旧版公共请求参数、header中新版公共请求参数)
     * add by shaoqinghua 20170406
     *
     * @param request
     * @param response
     * @return
     */
    public boolean getUserDataNew(ParameterRequestWrapper request, HttpServletResponse response) {
        String accessToken = request.getParameter("accessToken");
        String token = request.getHeader("token");
        String ipAddr = WebUtil.getRealIp(request).trim();
        BaseBean paramDto = new BaseBean();
        if (StringUtils.isNotEmpty(accessToken)) {
            //旧版公共请求参数
            String appId = request.getParameter("appId");
            paramDto.setAccessToken(accessToken);
            paramDto.setAppId(appId);
            paramDto.setIpAddr(ipAddr);
            TokenDto dto = checkToken(paramDto);
            request.setParameter("cuserId", dto.getCuserId());
        } else if (!CheckUtil.isNullString(token)) {
            //新版公共请求参数在header中，设置为普通请求参数
            this.getHeaderParameter(request);
            token = request.getParameter("token");
            String appId = request.getParameter("appId");
            paramDto.setAccessToken(token);
            paramDto.setAppId(appId);
            paramDto.setIpAddr(ipAddr);
            TokenDto dto = checkToken(paramDto);
            request.setParameter("cuserId", dto.getCuserId());
        } else {
            throw new UserException("9009", "用户未登录");
        }
        return true;
    }

    /**
     * 获取header公共参数
     * add by shaoqinghua 20170406
     *
     * @param request
     */
    public void getHeaderParameter(ParameterRequestWrapper request) {
        request.setParameter("token", request.getHeader("token"));
        request.setParameter("appId", request.getHeader("appId"));
        request.setParameter("devType", request.getHeader("devType"));
        request.setParameter("source", request.getHeader("source"));
        request.setParameter("appPkgName", request.getHeader("appPkgName"));
        request.setParameter("appVersionName", request.getHeader("appVersionName"));
        request.setParameter("appMgr", request.getHeader("appMgr"));
        request.setParameter("appVersionCode", request.getHeader("appVersionCode"));
        request.setParameter("gps", request.getHeader("gps"));
        request.setParameter("adCode", request.getHeader("adCode"));
        request.setParameter("hskCityId", request.getHeader("hskCityId"));
        request.setParameter("cityCode", request.getHeader("cityCode"));
    }

    /**
     * 参数有用到(appid,accessToken.ipAddr,)
     *
     * @param dto
     * @return
     * @throws UserException
     */
    public TokenDto checkToken(BaseBean dto) throws UserException {
        String token = dto.getAccessToken();
        String appid = dto.getAppId();
        String ipAddr = dto.getIpAddr();
        TokenDto resultDto = null;

        if (StringUtils.isEmpty(appid) || StringUtils.isEmpty(token) || appid.length() < 10 || token.length() < 10) {
            throw new UserException("9009", "用户未登录");
        }
        String[] result = TokenGenerator.authToken(token, appid);
        logger.info("authToken" + result[0]);
        if ("1".equals(result[0])) {
            //直接從memcache獲得token。如果獲得失敗，從庫中獲得
            Object obj = memCachedClient.get(appid);
            try {
                String strToken = (String) obj;
                resultDto = JSONObject.parseObject(strToken, TokenDto.class);
            } catch (Exception e) {
                try {
                    TokenBean bean = (TokenBean) obj;
                    if (bean != null) {
                        resultDto = new TokenDto();
                        resultDto.setCuserId(bean.getCuserId());
                        resultDto.setCpassword(bean.getPwd());
                        resultDto.setPwd(bean.getPwd());
                        resultDto.setCpwd9188(bean.getPwd9188());
                        resultDto.setParamJson(bean.getParamJson());
                    }
                } catch (Exception e1) {
                    logger.error("", e1);
                }
            }
            //StringUtils.isNotEmpty(resultDto.getCuserId())
            if (resultDto == null || StringUtils.isNotEmpty(resultDto.getCuserId())) {
                //TODO
//                String str = client.execute(new DrpcRequest("user", "query_userToken", dto));
//                resultDto = JSONObject.parseObject(str,TokenDto.class);
//               20170220 lzj
                TokenUtil2.getHskUserByBaseBean(dto);
                if (dto.getCuserId() == null) {//如果cuserid不存在，證明失敗了
                    logger.info("checkToken|query_userToken error code:{},desc:{}", dto.getBusiErrCode(), dto.getBusiErrDesc());
                    throw new UserException(String.valueOf(dto.getBusiErrCode()), dto.getBusiErrDesc());
                }
                if (resultDto == null) {
                    resultDto = new TokenDto();
                }
                resultDto.setAppid(appid);

                resultDto.setAccessToken(token);
                resultDto.setCuserId(dto.getCuserId());
                memCachedClient.set(appid, JSONObject.toJSONString(resultDto), Constants.TIME_HALFHOUR);
            }
            return resultDto;
        } else {
            //验证失败
            throw new UserException("9007", result[1]);
        }
    }


    /**
     * 返回token登录信息
     * add by lcs 20160922
     */
    //TODO
    public void createToken(User bean, HttpServletResponse httpServletResponse) {
        String appId = UniqueStrCreator.createUniqueString("lc");
        String accessToken = TokenGenerator.createToken(appId); //生成token

        TokenDto dto = new TokenDto();

        dto.setAccessToken(accessToken);
        dto.setCuserId(bean.getCuserId());
        dto.setCpassword(bean.getPwd());
        dto.setPwd(bean.getPwd());
        dto.setAppid(appId);
        dto.setParamJson(bean.getParamJson());
        dto.setCpwd9188(bean.getPwd9188());
        dto.setIloginfrom(bean.getItype());

        BoltResult result = client.execute(new DrpcRequest("user", "registerToken", dto), BoltResult.class);


        String tokenStr = JSONObject.toJSONString(dto);
        if (BoltResult.SUCCESS.equals(result.getCode())) {
            //成功
            memCachedClient.set(appId, tokenStr, Constants.TIME_HALFHOUR);
        } else {
            //失败
            logger.info("token入库失败！" + result.getCode() + result.getDesc() + tokenStr);
        }
        Element resp = new DOMElement("Resp");
        resp.addAttribute("code", result.getCode());
        resp.addAttribute("desc", result.getDesc());
        resp.addAttribute("appId", appId);
        resp.addAttribute("accessToken", accessToken);
        XmlUtils.writeXml(resp, httpServletResponse);
    }
}
