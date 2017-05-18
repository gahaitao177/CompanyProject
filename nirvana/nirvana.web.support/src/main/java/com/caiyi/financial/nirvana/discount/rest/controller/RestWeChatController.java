package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.user.bean.WeChatBean;
import com.caiyi.financial.nirvana.discount.user.bean.weChatUnionid;
import com.caiyi.financial.nirvana.discount.user.dto.HskUserDto;
import com.caiyi.financial.nirvana.discount.utils.HttpClientUtil;
import com.caiyi.financial.nirvana.discount.utils.WebUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by lizhijie on 2017/2/28.
 * 惠刷卡微信登录接口
 */
@RestController
public class RestWeChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestWeChatController.class);

    @Resource(name = Constant.HSK_USER)
    private IDrpcClient client;

    @Autowired
    private MemCachedClient cc;

    //基础服务器地址
    //private static final String BASE_URL = "http://192.168.1.51:10021";
    private static final String BASE_URL = SystemConfig.get("user_http_url");
    //获取微信网页授权access_token请求地址
    private static final String WECHAT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    //获取微信用户信息请求地址
    private static final String WECHAT_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    // 微信公众号AppId
    private static final String WECHAT_APPID = "wx0b7fc2bdc4c1820e";
    // 微信公众号AppSecret
    private static final String WECHAT_APPSECRET = "c418138a9685d46dff5aadeef7ffa4db";
    private static final String SEVER_ADDRESS = "http://www.huishuaka.com";
    private static final String PIC_PATH = "/opt/export/www/imgs/user_icon/";
    private static final String VISIT_PATH = "/imgs/user_icon/";

    /**
     * 微信登录新接口(返回JSON数据)
     *
     * @param weChatBean
     * @param request
     * @return
     */
    @RequestMapping("/notcontrol/user/wechatLoginNew.go")
    public BoltResult weChatLoginNew(WeChatBean weChatBean, HttpServletRequest request) {
        BoltResult boltResult = new BoltResult("1", "success");
        JSONObject data = new JSONObject();
        LOGGER.info("进入微信登录");
        if (CheckUtil.isNullString(weChatBean.getCode()) || CheckUtil.isNullString(weChatBean.getAppId())) {
            boltResult.setCode("1000");
            boltResult.setDesc("非法参数");
            return boltResult;
        }
        //设置AppSecret
        if (CheckUtil.isNullString(weChatBean.getSecret())) {
            weChatBean.setSecret("046786df610b4daba8db7983072c702e");
        }
        //使用code、appsecret、appid请求微信API获取用户信息添加到bean中
        BoltResult weChatResult = this.wrapWeChatBean(weChatBean);
        String weChatCode = weChatResult.getCode();
        String weChatDesc = weChatResult.getDesc();
        LOGGER.info("请求微信api获取用户信息结果:weChatCode:{},weChatDesc:{}", weChatCode, weChatDesc);
        if ("1001".equals(weChatCode)) {
            boltResult.setCode(weChatCode);
            boltResult.setDesc(weChatDesc);
            return boltResult;
        }
        //设置请求IP地址
        weChatBean.setIpAddr(WebUtil.getRealIp(request));
        LOGGER.info("开始微信登录");
        weChatUnionid unionId = client.execute(new DrpcRequest("hskUser", "queryWeChatUnionId", weChatBean.getUnionid()),
                weChatUnionid.class);
        LOGGER.info("根据unionid查询到的微信用户信息:weChatUnionid:{}", unionId);
        //根据unionId处理微信登录
        HskUserDto hskUserDto = this.processWeChatLoginByUnionId(weChatBean, unionId);
        String resultCode = hskUserDto.getCode();
        String resultDesc = hskUserDto.getDesc();
        LOGGER.info("code:{},desc:{}", resultCode, resultDesc);
        if (!CheckUtil.isNullString(resultCode)) {
            if ("1".equals(resultCode)) {
                data.put("appId", hskUserDto.getAppid());
                data.put("accessToken", hskUserDto.getAccessToken());
                boltResult.setData(data);
            }
            boltResult.setCode(resultCode);
            boltResult.setDesc(resultDesc);
        } else {
            boltResult.setCode("0");
            boltResult.setDesc("微信登录失败");
        }
        return boltResult;
    }

    /**
     * 请求微信接口获取信息封装到bean中
     *
     * @param bean
     * @return
     */
    private BoltResult wrapWeChatBean(WeChatBean bean) {
        BoltResult boltResult = new BoltResult("1", "success");
        //通过code换取网页授权access_token
        JSONObject tokenJson = this.getWeChatAccessToken(bean.getAppId(), bean.getSecret(), bean.getCode());
        LOGGER.info("获取的网页授权access_token信息:tokenJson:{}", tokenJson);
        if (tokenJson == null || tokenJson.containsKey("errcode")) {
            boltResult.setCode("1001");
            boltResult.setDesc("获取微信accessToken失败");
            return boltResult;
        }
        String accessToken = tokenJson.getString("access_token");
        LOGGER.info("access_token:{}", accessToken);
        if (CheckUtil.isNullString(accessToken)) {
            boltResult.setCode("1001");
            boltResult.setDesc("获取微信accessToken失败");
            return boltResult;
        }
        //用户唯一标识
        String openId = tokenJson.getString("openid");
        //获取微信用户信息
        JSONObject userInfoJson = this.getWeChatUserInfo(accessToken, openId);
        LOGGER.info("获取的微信用户信息:userInfoJson:{}", userInfoJson);
        if (userInfoJson == null || userInfoJson.containsKey("errcode")) {
            boltResult.setCode("1001");
            boltResult.setDesc("获取微信用户个人信息失败");
            return boltResult;
        }
        //获取用户信息授权后产生的unionid
        String unionid = userInfoJson.getString("unionid");
        LOGGER.info("unionid:{}", unionid);
        if (CheckUtil.isNullString(unionid)) {
            boltResult.setCode("1001");
            boltResult.setDesc("获取微信用户个人信息失败");
            return boltResult;
        }
        bean.setUnionid(unionid);
        bean.setNickname(userInfoJson.getString("nickname"));
        String headImgUrl = userInfoJson.getString("headimgurl");
        LOGGER.info("通过微信API获取的头像地址:headImgUrl:{}", headImgUrl);
        if (!CheckUtil.isNullString(headImgUrl)) {
            bean.setIcon(this.saveHeadImg(headImgUrl));
        }
        // add by lcs 20150911
        bean.setWxNickName(bean.getNickname());
        bean.setOpenid(userInfoJson.getString("openid"));
        bean.setSex(userInfoJson.getInteger("sex"));
        bean.setProvince(userInfoJson.getString("province"));
        bean.setCity(userInfoJson.getString("city"));
        bean.setCountry(userInfoJson.getString("country"));
        bean.setHeadimgurl(userInfoJson.getString("headimgurl"));
        bean.setPrivilege(userInfoJson.getString("privilege"));
        return boltResult;
    }

    /**
     * 根据unionId处理微信登录
     *
     * @param bean
     * @param unionId
     * @return
     */
    private HskUserDto processWeChatLoginByUnionId(WeChatBean bean, weChatUnionid unionId) {
        HskUserDto hskUserDto = new HskUserDto();
        if (unionId != null) {
            LOGGER.info("微信已注册用户");
            bean.setCuserId(unionId.getCuserid());
            bean.setPwd(unionId.getCpassword());
            bean.setNickname(unionId.getCnickid());
            //查询微信用户的token信息
            String result = this.queryWeChatToken(bean);
            LOGGER.info("查询微信用户的token信息:{}", result);
            if (!CheckUtil.isNullString(result)) {
                JSONObject tokenJson = JSON.parseObject(result);
                if ("1".equals(tokenJson.getString("code"))) {
                    //解析第三方登录结果获得accessToken和appId
                    this.getTokenAndAppId(hskUserDto, result);
                } else {
                    //注册惠刷卡微信登录用户
                    hskUserDto = client.execute(new DrpcRequest("hskUser", "weChatRegister", bean), HskUserDto.class);
                }
            }
        } else {
            LOGGER.info("微信未注册，添加账户");
            hskUserDto = client.execute(new DrpcRequest("hskUser", "weChatRegister", bean), HskUserDto.class);
        }
        return hskUserDto;
    }

    /**
     * 通过code换取网页授权access_token
     *
     * @param appid
     * @param secret
     * @param code
     * @return
     */
    private JSONObject getWeChatAccessToken(String appid, String secret, String code) {
        String url = WECHAT_ACCESS_TOKEN_URL + "?appid=" + appid + "&secret=" + secret + "&code=" +
                code + "&grant_type=authorization_code";
        String respStr = doGet(url);
        JSONObject dataJson = JSONObject.parseObject(respStr);
        return dataJson;

    }

    /**
     * 获取微信用户信息
     *
     * @param access_token
     * @param openId
     * @return
     */
    private JSONObject getWeChatUserInfo(String access_token, String openId) {
        String url = WECHAT_USER_INFO_URL + "?access_token=" + access_token + "&openid=" + openId;
        String respStr = doGet(url);
        JSONObject dataJson = JSONObject.parseObject(respStr);
        return dataJson;
    }

    /**
     * 查询token信息
     *
     * @param bean
     * @return
     */
    private String queryWeChatToken(WeChatBean bean) {
        HashMap<String, String> params = new HashMap<>();
        String resetUrl = BASE_URL + "createToken";
        LOGGER.info("9188查询token url:{}", resetUrl);
        params.put("cuserId", bean.getCuserId());
        params.put("cloginfrom", "HSK");
        params.put("cnickId", bean.getNickname());
        params.put("csource", String.valueOf(bean.getSource()));
        params.put("ipAddr", bean.getIpAddr());
        String mobileType;
        if ("ios".equals(bean.getDevType())) {
            mobileType = "2";
        } else {
            mobileType = "1";
        }
        params.put("mobileType", mobileType);
        params.put("packageName", bean.getAppPkgName());
        String result = HttpClientUtil.callHttpPost_Map(resetUrl, params);
        LOGGER.info(("查询token结果:" + result));
        return result;
    }

    /**
     * 解析第三方登录返回结果获得accessToken和appId
     *
     * @param result
     * @return
     */
    private void getTokenAndAppId(HskUserDto hskUserDto, String result) {
        if (hskUserDto == null) {
            hskUserDto = new HskUserDto();
        }
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject != null) {
            if ("1".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                hskUserDto.setCode("1");
                hskUserDto.setDesc("微信登录成功");
                if (data != null) {
                    hskUserDto.setAppid(data.getString("appId"));
                    hskUserDto.setAccessToken(data.getString("accessToken"));
                    LOGGER.info("appId:{},accessToken:{}", data.getString("appId"), data.getString("accessToken"));
                } else {
                    hskUserDto.setCode("-1");
                    hskUserDto.setDesc("程序异常");
                }
            } else {
                hskUserDto.setCode(jsonObject.getString("code"));
                if (CheckUtil.isNullString(jsonObject.getString("desc"))) {
                    hskUserDto.setDesc("token无效");
                } else {
                    hskUserDto.setDesc(jsonObject.getString("desc"));
                }
            }
        }
    }

    /**
     * 保存头像图片
     *
     * @param headImgurl
     * @return
     */
    private String saveHeadImg(String headImgurl) {
        String retStr = "";
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet(headImgurl);
        try {
            httpClient = getHttpClient();
            response = httpClient.execute(get);
            InputStream inputStream = response.getEntity().getContent();
            String dirNew = getUploadDir();
            String uploadPath = PIC_PATH + dirNew;
            LOGGER.info("uploadPath:" + uploadPath);
            File uploadFile = new File(uploadPath);
            if (!uploadFile.exists()) {
                uploadFile.mkdirs();
            }
            //生成随机文件名，并设置扩展名
            Random random = new Random();
            BigInteger big = new BigInteger(64, random);
            String imgName = big.toString() + ".png";
            LOGGER.info("imgName:" + imgName);

            OutputStream os = new FileOutputStream(new File(uploadFile, imgName));
            IOUtils.copy(inputStream, os);
            retStr = SEVER_ADDRESS + VISIT_PATH + dirNew + imgName;
            LOGGER.info("新头像图片地址:{}", retStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeHttp(httpClient, response);
            return retStr;
        }
    }

    // 以年份+月为单位保存用户上传头像的目录(最大文件数为32000)
    private String getUploadDir() {
        Calendar cal = Calendar.getInstance();
        // 获取年
        int year = cal.get(Calendar.YEAR);
        // 获取月
        int month = cal.get(Calendar.MONTH) + 1;
        // 获取日期
        int day = cal.get(Calendar.DATE);
        StringBuilder dir = new StringBuilder();
        dir.append(year);
        dir.append("/");
        dir.append(String.valueOf(year)
                + String.valueOf(month < 10 ? ("0" + month) : ("" + month)));
        dir.append("/");
        dir.append(String.valueOf(year)
                + String.valueOf(month < 10 ? ("0" + month) : ("" + month))
                + String.valueOf(day < 10 ? ("0" + day) : ("" + day)));
        dir.append("/");
        return dir.toString();
    }

    /**
     * 获取access_token
     */
    private String getAccessToken() {
        String token = null;

        token = (String) cc.get("SHARE_TOKEN");

        if (token != null) {
            System.out.println("token---" + token);
        } else {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                    + WECHAT_APPID + "&secret=" + WECHAT_APPSECRET;
            String respStr = doGet(url);
            JSONObject dataJson = JSONObject.parseObject(respStr);
            token = dataJson.getString("access_token");
        }
        return token;
    }

    /**
     * 简单doget方法
     * todo  有时间梳理下httpclient使用，将其封装到统一的地方
     *
     * @param url
     * @return
     */
    protected String doGet(String url) {
        HttpGet get = new HttpGet(url);
        String reuslt = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = getHttpClient();
            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            reuslt = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeHttp(httpClient, response);
            return reuslt;
        }
    }

    private CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient;
        httpClient = HttpClients.createDefault();
        return httpClient;
    }

    private void closeHttp(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 微信登录原接口(返回XML格式数据)
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/user/wechatLogin.go")
    public void weChatLogin(WeChatBean bean, HttpServletRequest request, HttpServletResponse response) {
        Element resp = new DOMElement("Resp");
        LOGGER.info("进入微信登录");
        if (CheckUtil.isNullString(bean.getCode()) || CheckUtil.isNullString(bean.getAppId())) {
            resp.addAttribute("code", "1000");
            resp.addAttribute("desc", "非法参数");
            XmlUtils.writeXml(resp, response);
            return;
        }
        if (StringUtils.isEmpty(bean.getSecret())) {
            bean.setSecret("046786df610b4daba8db7983072c702e");
        }
        String iclient = request.getParameter("iclient");
        if ("1".equals(iclient)) {
            bean.setDevType("ios");
        } else {
            bean.setDevType("android");
        }
        //处理前端传递的重复source值(5000,5000)
        String source = bean.getSource();
        if (source.contains(",")) {
            bean.setSource(source.split(",")[0]);
        }
        //使用code、appsecret、appid请求微信API获取用户信息添加到bean中
        BoltResult weChatResult = this.wrapWeChatBean(bean);
        String weChatCode = weChatResult.getCode();
        String weChatDesc = weChatResult.getDesc();
        LOGGER.info("请求微信api获取用户信息结果:weChatCode:{},weChatDesc:{}", weChatCode, weChatDesc);
        if ("1001".equals(weChatCode)) {
            resp.addAttribute("code", weChatCode);
            resp.addAttribute("desc", weChatDesc);
            XmlUtils.writeXml(resp, response);
            return;
        }
        //设置请求IP地址
        bean.setIpAddr(WebUtil.getRealIp(request));
        LOGGER.info("微信登录开始");

        String resultStr = client.execute(new DrpcRequest("hskUser", "queryWeChatUnionId", bean.getUnionid()));
        LOGGER.info("微信登录开始------" + resultStr);
        weChatUnionid unionId = null;

        if ((!CheckUtil.isNullString(resultStr)) && !"null".equals(resultStr) ){
             unionId = JSONObject.parseObject(resultStr,weChatUnionid.class);
        }
//        weChatUnionid unionId = client.execute(new DrpcRequest("hskUser", "queryWeChatUnionId", bean.getUnionid()),
//                weChatUnionid.class);
        LOGGER.info("根据unionid查询到的微信用户信息:weChatUnionid:{}", unionId);
        //根据unionId处理微信登录
        HskUserDto hskUserDto = this.processWeChatLoginByUnionId(bean, unionId);
        String resultCode = hskUserDto.getCode();
        String resultDesc = hskUserDto.getDesc();
        LOGGER.info("code:{},desc:{}", resultCode, resultDesc);
        if (!CheckUtil.isNullString(resultCode)) {
            resp.addAttribute("code", resultCode);
            resp.addAttribute("desc", resultDesc);
            if ("1".equals(resultCode)) {
                resp.addAttribute("appId", hskUserDto.getAppid());
                resp.addAttribute("accessToken", hskUserDto.getAccessToken());
            }
        } else {
            resp.addAttribute("code", "0");
            resp.addAttribute("desc", "微信登录失败");
        }
        XmlUtils.writeXml(resp, response);
    }
}
