package com.caiyi.financial.nirvana.discount.rest.controller;


import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.user.bean.WeChatBean;
import com.caiyi.financial.nirvana.discount.utils.WeChatUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by dengh on 2016/7/22.
 */
@RestController
@RequestMapping("/user")
public class RestWechatUserController {
//    private final static Logger LOGGGER = LoggerFactory.getLogger(RestWechatUserController.class);
//    private static String SEVER_ADDRESS = "http://www.huishuaka.com";
//    private static String PIC_PATH = "/opt/export/www/imgs/user_icon/";
//    private static String VISIT_PATH = "/imgs/user_icon/";


    // 微信公众号appid
    private static String WECHAT_APPID = "wx0b7fc2bdc4c1820e";
    // 微信公众号AppSecret
    private static String WECHAT_APPSECRET = "c418138a9685d46dff5aadeef7ffa4db";


    @Resource(name = Constant.HSK_USER)
    IDrpcClient client;

 /*   @Autowired
    private MemCachedClient cc;


    @Resource
    private LoginUtil loginUtil;*/


    /**
     * 简单doget方法
     * todo  有时间梳理下httpclient使用，将其封装到统一的地方
     * @param url
     * @return
     */
    protected String doGet(String url){
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
        }finally {
            closeHttp(httpClient, response);
            return reuslt;
        }
    }

    private void closeHttp(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        if(response!=null){
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

    private CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient;
        httpClient = HttpClients.createDefault();
        return httpClient;
    }

    /** 获取access_token */
   /* private JSONObject getAccessToken(String appid, String secret, String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret=" + secret + "&code=" + code
                + "&grant_type=authorization_code";

        String respStr =  doGet(url);
        JSONObject dataJson = JSONObject.parseObject(respStr);
        return dataJson;

    }*/
   /* private  byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        inStream.close();
        // 把outStream里的数据写入内存
        return outStream.toByteArray();
    }*/
    // 以年份+月为单位保存用户上传头像的目录(最大文件数为32000)
  /*  private  String getUploadDir() {
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
    }*/

//    /** 刷新access_token */
//    private  JSONObject getUserInfo(String access_token, String openId) {
//        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openId;
//        String respStr =  doGet(url);
//        JSONObject dataJson = JSONObject.parseObject(respStr);
//        return dataJson;
//    }
//
//    private  String saveHeadImg(String headImgurl) {
//        String retStr = "";
//        CloseableHttpClient httpClient = null;
//        CloseableHttpResponse response = null;
//        HttpGet get = new HttpGet(headImgurl);
//        try {
//            httpClient = getHttpClient();
//            response = httpClient.execute(get);
//            InputStream inputStream = response.getEntity().getContent();
//            String dirNew = getUploadDir();
//            String uploadPath = PIC_PATH + dirNew;
//            File uploadFile = new File(uploadPath);
//            if (!uploadFile.exists()) {
//                uploadFile.mkdirs();
//            }
//            Random random = new Random();
//            BigInteger big = new BigInteger(64, random);
//            String imgName = big.toString() ;
//
//            OutputStream os = new FileOutputStream(new File(uploadFile, imgName));
//            IOUtils.copy(inputStream,os);
//            retStr =SEVER_ADDRESS + VISIT_PATH + dirNew + imgName;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            closeHttp(httpClient, response);
//            return retStr;
//        }
//    }





//    private void wrapWeChatBean(WeChatBean bean){
//        JSONObject tokenJson = getAccessToken(bean.getAppId(), bean.getSecret(), bean.getCode());
//        if (tokenJson == null || tokenJson.containsKey("errcode")) {
//            throw new WechatUserException("1001","获取微信accessToken失败");
//        }
//        String accessToken = tokenJson.getString("access_token");
//        if (CheckUtil.isNullString(accessToken)){
//            throw new WechatUserException("1001","获取微信accessToken失败");
//        }
//        String openId = tokenJson.getString("openid");
//        JSONObject userInfoJson =  getUserInfo(accessToken, openId);
//
//        System.out.println("userInfoJson--" + userInfoJson);
//        if (userInfoJson == null || userInfoJson.containsKey("errcode")) {
//            throw new WechatUserException("1001","获取微信用户个人信息失败");
//        }
//        String unionid = userInfoJson.getString("unionid");
//        if (CheckUtil.isNullString(unionid)){
//            throw new WechatUserException("1001","获取微信用户个人信息失败");
//        }
//        bean.setUnionid(unionid);
//        bean.setNickname(userInfoJson.getString("nickname"));
//        String headImgUrl = userInfoJson.getString("headimgurl");
//        if (!CheckUtil.isNullString(headImgUrl)){
//            bean.setIcon(saveHeadImg(headImgUrl));
//        }
//        // add by lcs 20150911
//        bean.setWxNickName(bean.getNickname());
//        bean.setOpenid(userInfoJson.getString("openid"));
//        bean.setSex(userInfoJson.getInteger("sex"));
//        bean.setProvince(userInfoJson.getString("province"));
//        bean.setCity(userInfoJson.getString("city"));
//        bean.setCountry(userInfoJson.getString("country"));
//        bean.setHeadimgurl(userInfoJson.getString("headimgurl"));
//        bean.setPrivilege(userInfoJson.getString("privilege"));
//    }




//    @RequestMapping("/wechatLogin.go")
//    public void weChatLogin(WeChatBean bean, HttpServletRequest request, HttpServletResponse response){
//        if (CheckUtil.isNullString(bean.getCode()) || CheckUtil.isNullString(bean.getAppId()) ){//|| CheckUtil.isNullString(bean.getSecret())
//            throw new WechatUserException("1000","非法参数");
//        }
//        if(StringUtils.isEmpty(bean.getSecret())){
//            bean.setSecret("046786df610b4daba8db7983072c702e");
//        }
//        //根据appid secret code 从微信api获得用户信息并赋值给bean
//        wrapWeChatBean(bean);
//        //设置ip
//        bean.setIpAddr(WebUtil.getRealIp(request));
//
//        //入库
//        HskUserDto resultHsk = client.execute(new DrpcRequest("hskUser", "weChatLogin", bean),HskUserDto.class);
//
//        Element resp = new DOMElement("Resp");
//        resp.addAttribute("code",resultHsk.getCode());
//        resp.addAttribute("desc",resultHsk.getDesc());
//        resp.addAttribute("appId",resultHsk.getAppid());
//        resp.addAttribute("accessToken",resultHsk.getAccessToken());
//        XmlUtils.writeXml(resp,response);
//
//        //判断成功失败，将 weChatBean 转为 userbean 注册登录
////        User user = new User();
////        user.setCuserId(bean.getCuserId());
////        user.setPwd(bean.getPwd());
////        user.setSource(bean.getSource());
////        user.setUserType(bean.getUserType());
////        user.setParamJson(bean.getParamJson());
////        loginUtil.createToken(user,response);
//    }

    @RequestMapping("/weChatShare.go")
    public int weChatShare(WeChatBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception{
        createPageShareParam(request,response);
        return 1;
    }

    private void createPageShareParam(HttpServletRequest request,HttpServletResponse response){
        String xml = null;
        boolean success = true;
        try {
            String url = request.getParameter("shareUrl");
            if (null == url){
                xml = "<Resp code=\"-1\" desc=\"参数错误\" />";
                return;
            }
            xml = getShareXml(url);
        } catch (Exception e) {
            System.out.println("创建微信分享参数异常:" + e.getMessage());
            success = false;
        } finally {
            if (!success){
                xml = "<Resp code=\"-1\" desc=\"系统异常\" />";
            }
            System.out.println("xml:" + xml);
            XmlUtils.writeXml(xml,response);
        }
    }

    /**
     * 获取分享链接xml
     * @param shareUrl
     * @return
     */
    private  String getShareXml(String shareUrl){
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String jsapi_ticket = getJsApiToken();
        String noncestr = "wxshare";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("jsapi_ticket", jsapi_ticket);
        paramMap.put("noncestr", noncestr);
        paramMap.put("timestamp", timestamp);
        paramMap.put("url", shareUrl);
        Set<String> keys = paramMap.keySet();
        Object[] keyStr =  keys.toArray();
        Arrays.sort(keyStr);
        String str = "";
        for (Object key:keyStr){
            str += (key + "=" + paramMap.get(key) + "&");
        }
        str = str.substring(0, str.length() - 1);
        String sign = WeChatUtil.SHA1(str);
        String xml = "<Resp code=\"0\"" + "><row appId=\"" + WECHAT_APPID+ "\" timestamp=\"" + timestamp + "\" nonceStr=\"" + noncestr + "\" signature=\"" + sign + "\"></row></Resp>";
        return xml;
    }

    private  String getJsApiToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        String respStr =  doGet(url);
        JSONObject dataJson = JSONObject.parseObject(respStr);
        return dataJson.getString("ticket");
    }

//    /** 获取access_token */
//    private String getAccessToken() {
//        String token = null;
//
//        token = (String)cc.get("SHARE_TOKEN");
//
//        if (token != null ){
//            System.out.println("token---" + token);
//        } else {
//            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
//                    + WECHAT_APPID + "&secret=" + WECHAT_APPSECRET;
//            String respStr =  doGet(url);
//            JSONObject dataJson = JSONObject.parseObject(respStr);
//            token = dataJson.getString("access_token");
//        }
//        return token;
//    }





}
