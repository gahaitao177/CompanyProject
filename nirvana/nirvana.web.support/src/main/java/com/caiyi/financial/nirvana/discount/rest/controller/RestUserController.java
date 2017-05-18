package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.user.bean.FeedBackBean;
import com.caiyi.financial.nirvana.discount.user.bean.HomePageBean;
import com.caiyi.financial.nirvana.discount.user.bean.SpecialPreferentialBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import com.caiyi.financial.nirvana.discount.utils.LoginUtil;
import com.caiyi.financial.nirvana.discount.utils.WebUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import com.util.string.StringUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;

/**
 * 用户登陆，注册，修改个人信息 等相关接口
 * Created by heshaohua on 2016/5/19.
 */

@RestController
public class RestUserController {
    private static Logger logger = LoggerFactory.getLogger(RestUserController.class);
    public static final String SMS_ERROR = "smserrorprefix";
    private static String PIC_PATH = "imgs/user_icon/";
    public static final String PATH = "/opt/export/www/";
    private static final String SEVER_ADDRESS = "http://web.img.huishuaka.com";

    @Resource(name = Constant.HSK_USER)
    IDrpcClient client;

    @Autowired
    private LoginUtil loginUtil;
    @Autowired
    private MemCachedClient cc;

    private final static String MD5_SMS_KEY = "6a37207e0fcd92";
    public static final String ENCODING = "UTF-8";
    public static Map<String, String> loginhezuo = new HashMap<String, String>();
    /**
     * 惠刷卡编号和9188编号映射
     */
    public static Map<String, String> merchantacctidMap = new HashMap<String, String>();

    static {
        loginhezuo.put("130313001", "A9FK25RHT487ULMI");//Android
        loginhezuo.put("130313002", "A9FK25RHT487ULMI");//IOS

        merchantacctidMap.put("130313001", "130313002");
        merchantacctidMap.put("130313002", "130313003");
    }

    /**
     * 修改用户昵称
     *
     * @param user
     * @param request
     * @param response
     */
    @RequestMapping("/user/chgNickName.go")
    public void chgNickName(User user, HttpServletRequest request, HttpServletResponse response) {
        user.setIpAddr(WebUtil.getRealIp(request).trim());
        String method = request.getMethod();
        logger.info("修改用户昵称:cuserId:{},uid:{},method:{}", user.getCuserId(), user.getUid(), method);
        //String userNickName = user.getUid();
        //if (!CheckUtil.isNullString(method) && "get".equals(method.toLowerCase())) {
        //    if (2 == (user.getUserType())) {
        ////      userNickName = new String(userNickName.getBytes("ISO-8859-1"), "UTF-8");
        //        user.setUid(userNickName);
        //    }
        //}
        BoltResult result = client.execute(new DrpcRequest("hskUser", "chgNickName", user), BoltResult.class);
        logger.info("修改昵称结果:code:{},desc:{}", result.getCode(), result.getDesc());
        XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
    }

//    @RequestMapping("/login.go")
    /*public void quickLogin(User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String apiHost = SystemConfig.get("apiHost");
        if (StringUtils.isEmpty(user.getSignMsg()) || StringUtils.isEmpty(user.getSignType())
                || StringUtils.isEmpty(user.getMerchantacctId())
                || StringUtils.isEmpty(user.getUid())
                || StringUtils.isEmpty(user.getPwd())){

            XmlUtils.writeXml("1000", "非法的登录参数", response);
            return ;
        }else{
            String merchantacctid = user.getMerchantacctId();
            String signtype = user.getSignType();
            String signmsg = user.getSignMsg();
            String userNickName = user.getUid();

            //昵称登录的情况
            if("130313002".equals(user.getMerchantacctId())){//ios需要转码
                userNickName = URLDecoder.decode(userNickName, "utf-8");
            }else{
                userNickName = new String(userNickName.getBytes("UTF-8"),ENCODING);
            }
            user.setUid(userNickName);
            if (signtype.equals("1")){//MD5
                if (loginhezuo.containsKey(merchantacctid)){
                    // 生成加密签名串
                    // 请务必按照如下顺序和规则组成加密串！
                    String signMsgVal = "";
                    signMsgVal = WebUtil.appendParam(signMsgVal, "signType", signtype);
                    signMsgVal = WebUtil.appendParam(signMsgVal, "merchantacctId", merchantacctid);
                    signMsgVal = WebUtil.appendParam(signMsgVal, "uid", user.getUid());
                    signMsgVal = WebUtil.appendParam(signMsgVal, "pwd", user.getPwd());
                    signMsgVal = WebUtil.appendParam(signMsgVal, "key", loginhezuo.get(merchantacctid));
                    logger.info("惠刷卡--signMsgVal=" + signMsgVal);
                    String serverSignMsg = BankUtil.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();
                    logger.info("惠刷卡--serverSignMsg=" + serverSignMsg);

                    if (serverSignMsg.equals(signmsg)){//验签成功
                        logger.info("用户【" + userNickName + "】,惠刷卡验签成功,bean pwd:" + user.getPwd() + ",bean getPwd9188:" + user
                        .getPwd9188());
/*//**********************************中间版本：老惠刷卡用户登录到惠刷卡start******************************************
     //检查是否为老惠刷卡用户
     //int ret1 = RemoteBeanCallUtil.RemoteBeanCall(bean, context, USER_GROUP, "isOldUser");

     String result = client.execute(new DrpcRequest("user", "query_userType", user));

     int userType = -1;
     if(!StringUtils.isEmpty(result)){
     if(result.length() < 5){
     userType = Integer.parseInt(result);
     }
     }
     // add by lcs 20160922
     user.setUserType(userType);
     if(userType == -1){
     XmlUtils.writeXml("1034", "用户名或密码错误", response);
     return ;
     }
     if ((0==userType||3==userType)) {//惠刷卡老用户或者两边都有的用户在此登录
     logger.info("惠刷卡老用户【" + userNickName + "】,用户类型=" + userType + ",正在登录到惠刷卡。。。");
     User user1 = new User();
     user1.setUid(user.getUid());
     user1.setPwd(user.getPwd());
     user1.setIpAddr(WebUtil.getRealIp(request).trim());
     String rs = client.execute(new DrpcRequest("user", "quick_login", user1));
     logger.info("rs======" + rs);
     if(rs != null){
     JSONObject object = JSONObject.parseObject(rs);
     user1.setCuserId(object.getString("cuserId"));
     user1.setBusiErrCode(Integer.parseInt(object.getString("busiErrCode")));
     user1.setBusiErrDesc(object.getString("busiErrDesc"));
     }else{
     logger.info("用户【" + userNickName + "】登录失败");
     //                                // add by lcs 20160922
     //                                XmlUtils.writeXml("0", "登录到惠刷卡失败", response);
     return;
     }
     if (user1.getBusiErrCode() == 1) {
     user.setBusiErrCode(1);
     user.setCuserId(user1.getCuserId());
     user.setPwd(user1.getPwd());
     // update by lcs 20160922
     //                                XmlUtils.writeXml("1", "登录到惠刷卡成功", response);
     loginUtil.createToken(user,response);
     logger.info("loginUtil.createToken end");
     return ;
     } else if (3==userType){
     logger.info("用户【" + userNickName + "】登录到惠刷卡失败，该用户在惠刷卡，9188都有注册，现在准备登录到9188。。。");
     }else{
     user.setBusiErrCode(-1005);
     user.setBusiErrDesc(user1.getBusiErrDesc());
     XmlUtils.writeXml("-1005", user1.getBusiErrDesc(), response);
     return ;
     }
     }

     logger.info("惠刷卡新用户【" + userNickName + "】，用户类型=" + userType + ",正在登录到9188。。。");
     //如果为在老版客户端注册的新用户，去本地库查询其登录密码
     if(StringUtils.isEmpty(user.getPwd9188())){
     String rs = client.execute(new DrpcRequest("user", "query_userPwd9188", user));
     logger.info("rs======"+rs);
     if(rs != null){
     JSONObject object = JSONObject.parseObject(rs);
     user.setPwd9188(object.getString("pwd9188"));
     }else{
     logger.info("用户【"+ userNickName + "】登录失败");
     return;
     }
     //int ret = RemoteBeanCallUtil.RemoteBeanCall(user, context, USER_GROUP, "queryPwdMaping");
     if (StringUtils.isEmpty(user.getPwd9188())){
     user.setBusiErrCode(1011);
     user.setBusiErrDesc("密码错误");
     logger.info("没有查询到密码映射");
     XmlUtils.writeXml("1011", "密码错误", response);
     return ;
     }else{
     logger.info("未查询到密码映射");
     }
     }

     String merchantacctid9188 = merchantacctidMap.get(merchantacctid);//惠刷卡编号和9188编号映射
     signMsgVal = "";
     // 生成9188加密签名串
     // 请务必按照如下顺序和规则组成加密串！
     signMsgVal = WebUtil.appendParam(signMsgVal, "signtype", signtype);
     signMsgVal = WebUtil.appendParam(signMsgVal, "merchantacctid", merchantacctid9188);
     signMsgVal = WebUtil.appendParam(signMsgVal, "uid", user.getUid());
     signMsgVal = WebUtil.appendParam(signMsgVal, "pwd", user.getPwd9188());//9188加密的密码
     signMsgVal = WebUtil.appendParam(signMsgVal, "newpwd", user.getPwd());//惠刷卡加密的密码
     signMsgVal = WebUtil.appendParam(signMsgVal, "key", loginhezuo.get(merchantacctid));
     serverSignMsg = BankUtil.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();
     //登录到9188
     Map<String, String> mapParams = new HashMap<String, String>();
     mapParams.put("uid", user.getUid());
     mapParams.put("pwd", user.getPwd9188());//用彩票加密串加密
     mapParams.put("newpwd", user.getPwd());//用惠刷卡加密串加密
     mapParams.put("merchantacctid", merchantacctid9188);
     mapParams.put("signtype", signtype);
     mapParams.put("signmsg", serverSignMsg);
     mapParams.put("logintype", "1");//token登录
     mapParams.put("source", transformSource(user.getSource() == null ? 0:user.getSource()) + "");

     String resu = HttpClientUtil.callHttpPost_Map(apiHost + "/user/mlogin.go", mapParams);
     logger.info(apiHost +"9188--resu:" + resu);
     Element rootEle = XmlTool.getRootElement(XmlTool.read(resu, "UTF-8"));
     String code = XmlTool.getAttributeValue(XmlTool.getElementAttribute("code", rootEle));
     String desc = XmlTool.getAttributeValue(XmlTool.getElementAttribute("desc", rootEle));
     String cuserid = XmlTool.getAttributeValue(XmlTool.getElementAttribute("userid", rootEle));
     String appid = XmlTool.getAttributeValue(XmlTool.getElementAttribute("appid", rootEle));
     String accesstoken = XmlTool.getAttributeValue(XmlTool.getElementAttribute("accesstoken", rootEle));
     logger.info("9188返回码--code=" + code + ",desc=" + desc + ",cuserid=" + cuserid);

     //9188那边账户禁用返回的是code=1,desc=账户已禁用，做个转换
     if ("账户已禁用".equals(desc)){
     user.setBusiErrCode(1012);
     user.setBusiErrDesc(desc);
     XmlUtils.writeXml("1012", desc, response);
     return ;
     }

     //返回9188失败代码
     if(!"0".equals(code)){
     user.setBusiErrCode(Integer.parseInt(code));
     user.setBusiErrDesc(desc);
     XmlUtils.writeXml(code, desc, response);
     return ;
     }

     //9188用户第一次登录，保存信息到本地
     User user1 = new User();
     user1.setAppId(appid);
     user1.setAccessToken(accesstoken);
     user1.setCuserId(cuserid);
     user1.setPwd9188(user.getPwd9188());//9188用户，保存9188加密的密码
     user1.setPwd(user.getPwd());//保存惠刷卡加密的密码

     String ret = client.execute(new DrpcRequest("user", "save_9188User", user1));
     if(ret != null){
     logger.info("save_9188User:" + ret);
     JSONObject object = JSONObject.parseObject(ret);
     Object userId = object.get("cuserId");
     Object pwd = object.get("pwd");
     Object pwd9188 = object.get("pwd9188");
     Object ccode = object.get("busiErrCode");
     Object cdesc = object.get("busiErrDesc");

     //如果是在惠刷卡和9188都注册过的用户，账户数据合并之前，先用惠刷卡的cuserid
     user.setCuserId(userId != null ? (String) userId : "");
     user.setPwd(pwd != null ? (String) pwd : "");
     user.setPwd9188(pwd9188 != null ? (String) pwd9188 : "");
     user1.setBusiErrCode(ccode != null ? (Integer) Integer.parseInt(ccode == null ? "" : ccode.toString())  : -1);
     }else{
     logger.info("用户【" + userNickName + "】登录失败");
     return;
     }
     if (user1.getBusiErrCode() == 1) {
     logger.info("保存9188用户成功");
     }
     user.setAppId(appid);//使用9188token不再自己生成
     user.setAccessToken(accesstoken);

     user.setBusiErrCode(1);
     user.setBusiErrDesc("登录成功");

     logger.info("登录到9188成功");
     // add by lcs 20161014 start
     String saveTk = client.execute(new DrpcRequest("user", "registerToken", user1));
     logger.info("saveTk:" +saveTk);
     Document dom = DocumentHelper.createDocument();
     Element Resp = new DOMElement("Resp");
     Resp.addAttribute("code","1");
     Resp.addAttribute("desc","登录成功");
     Resp.addAttribute("appId",appid);
     Resp.addAttribute("accessToken",accesstoken);
     dom.setRootElement(Resp);
     XmlUtils.writeXml(dom,response);
     //                        XmlUtils.writeXml("1", "登录成功", response);
     // add by lcs 20161014 end
     return ;
     }else{
     logger.info("签名失效：");
     logger.info("服务端签名结果为：" + serverSignMsg + "");
     logger.info("客户端签名结果为：" + signmsg + "");
     logger.info("signtype=" + signtype + "");
     logger.info("merchantacctid=" + merchantacctid + "");
     logger.info("uid=" + user.getUid() + "");
     logger.info("pwd=" + user.getPwd() + "");

     user.setBusiErrCode(1001);
     user.setBusiErrDesc("签名失败");
     XmlUtils.writeXml("1001", "签名失败", response);
     return ;
     }
     }else{
     logger.info("错误的 merchantacctid=" + merchantacctid);
     }
     }else{
     logger.info("错误的signtype=" + signtype);
     }
     }
     }*/

    /**
     * 发送短信的次数
     * 这里使用里 用 MemCachedClient 替代  CacheClient
     **/
    @SetUserDataRequired
    @RequestMapping("/user/smsnum.go")
    public int getSmsNum(User bean, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String num = String.valueOf(cc.get(SMS_ERROR + bean.getMobileNo()));
        if (null == num || "".equals(num) || "null".equals(num))
            bean.setBusiErrCode(0);
        else
            bean.setBusiErrCode(Integer.parseInt(num));
        return 1;
    }

    /**
     * 判断（微信）用户 是否绑定过手机号
     * by dh 2016/7/15
     ***/
    @SetUserDataRequired
    @RequestMapping("/user/isBound.go")
    public void isBound(User user, HttpServletRequest request, HttpServletResponse response) {
        // 启动bolt
        String result = client.execute(new DrpcRequest("user", "isBound", user));
        user = JSONObject.parseObject(result, User.class);
        XmlUtils.writeXml(user.getBusiErrCode(), user.getBusiErrDesc(), response);
    }

    /***
     * 微信用户登录状态下绑定手机号
     **/
    @SetUserDataRequired
//    @RequestMapping("/mobileBinding.go")
    public void mobileBinding(User user, HttpServletRequest request, HttpServletResponse response) {
        String result = client.execute(new DrpcRequest("user", "mobileBinding", user));
        user = JSONObject.parseObject(result, User.class);
        XmlUtils.writeXml(user.getBusiErrCode(), user.getBusiErrDesc(), response);
    }


    /**
     * 发送忘记密码验证码 (已登录)
     **/
//    @RequestMapping("/sendForgetPwdYzm.go")
    /*@Deprecated
    public void sendForgetPwdYzm(User user, HttpServletRequest request, HttpServletResponse response) {
        //   user.setAppId(WebUtil.getRealIp(request));
        user.setIpAddr(WebUtil.getRealIp(request).trim());
        String result = client.execute(new DrpcRequest("user", "sendForgetPwdYzm", user));
        XmlUtils.writeJson(result,response);
    }*/


    /***
     * 点赞
     ***/
    @RequestMapping("/user/doPraise.go")
    public void userPraise(User user, HttpServletRequest request, HttpServletResponse response) {
        String result = client.execute(new DrpcRequest("user", "userPraise", user));
        user = JSONObject.parseObject(result, User.class);
        XmlUtils.writeXml(user.getBusiErrCode(), user.getBusiErrDesc(), response);
    }


    public String encode(String var0) {
        if (var0 == null) {
            return "";
        } else {
            String var1 = StringUtil.replaceString(var0, "&", "&amp;");
            var1 = StringUtil.replaceString(var1, "<", "&lt;");
            var1 = StringUtil.replaceString(var1, ">", "&gt;");
            var1 = StringUtil.replaceString(var1, "\"", "&quot;");
            return var1;
        }
    }

    /***
     * 收藏过期
     **/
//    @SetUserDataRequired
//    @RequestMapping("/expiredCollect.go")
  /*  public void expiredCollect(User user, HttpServletRequest request, HttpServletResponse response) {
        String result = client.execute(new DrpcRequest("user", "expiredCollection", user));
        XmlUtils.writeXml(result,response);
    }*/

    /***
     * leancloud用户绑定
     **/
    @SetUserDataRequired
    @RequestMapping("/user/leanCloudUserBind.go")
    public void leanCloudUserBind(User user, HttpServletResponse response) {
        String result = client.execute(new DrpcRequest("user", "leanCloudUserBind", user));
        JSONObject json = JSONObject.parseObject(result);
        XmlUtils.writeXml(json.getString("code"), json.getString("desc"), response);
    }

    @RequestMapping("/user/cooperationUserBind.go")
    public void cooperationUserBind(User user, HttpServletRequest request, HttpServletResponse response) {
        user.setIpAddr(WebUtil.getRealIp(request).trim());
        String result = client.execute(new DrpcRequest("user", "cooperationUserBind", user));
        User bean = JSONObject.parseObject(result, User.class);
        XmlUtils.writeXml(bean.getBusiErrCode(), bean.getBusiErrDesc(), response);
    }

    @SetUserDataRequired
    @RequestMapping("/user/homePage.go")
    public String homePage(HomePageBean user, HttpServletRequest request, HttpServletResponse response) {
        user.setIpAddr(WebUtil.getRealIp(request).trim());
        String result = client.execute(new DrpcRequest("HomePageBolt", "homePage", user));
        logger.info("homePage:{}", result);
        return result;
    }

    @SetUserDataRequired
    @RequestMapping("/user/enjoyCheap.go")
    public void enjoyCheap(HomePageBean user, HttpServletRequest request, HttpServletResponse response) {
        user.setIpAddr(WebUtil.getRealIp(request).trim());
        String result = client.execute(new DrpcRequest("HomePageBolt", "enjoyCheap", user));
        XmlUtils.writeXml(result, response);
    }

    @SetUserDataRequired
    @RequestMapping("/user/specialPreferential.go")
    public String specialPreferential(HomePageBean user, HttpServletRequest request, HttpServletResponse response) {
        user.setIpAddr(WebUtil.getRealIp(request).trim());
        String resultBolt = client.execute(new DrpcRequest("HomePageBolt", "specialPreferential", user));
        logger.info("specialPreferential  result={}", resultBolt);
        JSONObject jsonObject = JSON.parseObject(resultBolt);
        JSONObject result = new JSONObject();
        JSONArray datas = new JSONArray();
        if (jsonObject != null && jsonObject.get("data") != null) {
            List<SpecialPreferentialBean> list = JSONArray.parseArray(jsonObject.getString("data"), SpecialPreferentialBean.class);
            for (SpecialPreferentialBean spb : list) {
                JSONObject obj = new JSONObject();
                obj.put("ctitle", spb.getCtitle());
                obj.put("cpicurl", spb.getCpicurl());
                obj.put("curl", spb.getCurl());
                obj.put("ibankid", spb.getIbankid());
                // add by lcs 20161028 start
                obj.put("ccontent", spb.getCcontent());
                // add by lcs 20161028 end
                obj.put("ipraisenum", spb.getIpraisenum());
                datas.add(obj);
            }
            result.put("tp", jsonObject.get("pages"));
        }
        JSONObject tmp = new JSONObject();
        tmp.put("row", datas);
        result.put("code", 1);
        result.put("desc", "查询成功");
        result.put("data", tmp);
        return result.toJSONString();
    }

    /**
     * 修改头像,返回xml(检测是否登陆)
     * update by lcs 20160921
     *
     * @param bean
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/user/uploadIcon.go")
    public void uploadIcon(User bean, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String enctype = request.getHeader("content-type");
        PIC_PATH = "imgs/user_icon/";
        if (enctype != null && enctype.indexOf("multipart/form-data") > -1) {
            try {
                this.handleMultipartRequest(request, response, bean, 1);
                //根据accessToken,appId查询cuserId
                logger.info("根据accessToken,appId查询cuserId");
                TokenDto tokenDto = client.execute(new DrpcRequest("user", "queryToken", bean), TokenDto.class);
                String cuserId = "";
                if (tokenDto != null) {
                    cuserId = tokenDto.getCuserId();
                }
                logger.info("cuserId:{}", cuserId);
                bean.setCuserId(cuserId);
                //根据cuserId查询用户信息
                logger.info("根据cuserId查询用户信息,cuserId:{}", cuserId);
                UserDto userDto = client.execute(new DrpcRequest("user", "queryUserByCuserId", cuserId), UserDto.class);
                logger.info("根据cuserId查询的用户:user:{}", userDto);
                if (userDto != null) {
                    bean.setPwd(userDto.getPwd());
                    bean.setPwd9188(userDto.getPwd9188());
                }
                String rc = client.execute(new DrpcRequest("user", "bindIcon", bean));
                User resultBean = JSON.parseObject(rc, User.class);
                if (resultBean.getBusiErrCode() == 1) {
                    resultBean.setBusiErrCode(1);
                    resultBean.setBusiErrDesc("");
                    logger.info("上传头像成功");
                    XmlUtils.writeXml("1", "上传头像成功", response);
                } else {
                    resultBean.setBusiErrCode(-1000);
                    resultBean.setBusiErrDesc("上传头像失败");
                    logger.info("上传头像失败");
                    XmlUtils.writeXml("-1000", "上传头像失败", response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                bean.setBusiErrCode(-1000);
                bean.setBusiErrDesc("上传失败");
                XmlUtils.writeXml("-1000", "上传头像失败", response);
            }
        }
    }


    /**
     * 以年份+月为单位保存用户上传头像的目录(最大文件数为32000)
     *
     * @return
     */
    private String getUploadDir() {
        Calendar cal = Calendar.getInstance();
        // 获取年份
        int year = cal.get(Calendar.YEAR);
        // 获取月份
        int month = cal.get(Calendar.MONTH) + 1;
        // 获取日期
        int day = cal.get(Calendar.DATE);
        StringBuilder dir = new StringBuilder();
        dir.append(year);
        dir.append("/");
        dir.append(String.valueOf(year) + String.valueOf(month < 10 ? ("0" + month) : ("" + month)));
        dir.append("/");
        dir.append(String.valueOf(year) + String.valueOf(month < 10 ? ("0" + month) : ("" + month)) + String.valueOf(day < 10 ? ("0"
                + day) : ("" + day)));
        return dir.toString();
    }

    /**
     * 处理文件上传请求
     *
     * @param request
     * @param bean
     * @param type
     * @throws Exception
     */
    private void handleMultipartRequest(HttpServletRequest request, HttpServletResponse response, User bean, int type) throws
            Exception {
        logger.info("开始处理图片文件请求");
        String uploadPath = PIC_PATH + getUploadDir();
        request.setCharacterEncoding(ENCODING);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        String photoPath = PATH + uploadPath;
        File uploadFile = new File(photoPath);
        logger.info("photoPath:" + photoPath);
        logger.info("photoPathexists:" + uploadFile.exists());
        if (!uploadFile.exists()) {
            boolean cret = uploadFile.mkdirs();
            logger.info("头像上传目录创建结果:{}", cret);
        }
        //设置临时存放目录位置
        factory.setRepository(new File(photoPath));
        //设置内存的缓存大小
        factory.setSizeThreshold(5242880);
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileItemFactory(factory);
        List<FileItem> items = upload.parseRequest(request);
        Method[] methods = User.class.getMethods();
        if (2 == type) {
            methods = FeedBackBean.class.getMethods();
        }
        String classtype;
        for (FileItem item : items) {
            String fieldName = item.getFieldName(); //accessToken
            String firstChar = fieldName.substring(0, 1).toUpperCase(); //A
            fieldName = firstChar + fieldName.substring(1); // AccessToken
            if (item.isFormField()) {
                //普通参数设置到bean中
                String setter = "set" + fieldName; //setAccessToken
                Object arg = item.getString("UTF-8");
                for (Method method : methods) {
                    if (setter.equalsIgnoreCase(method.getName())) {
                        //获取方法的形参类型
                        classtype = method.getParameterTypes()[0].getName();
                        if ("int".equals(classtype)) {
                            arg = Integer.parseInt(item.getString());
                        }
                        method.invoke(bean, arg);
                        break;
                    }
                }
            } else {
                //二进制文件参数处理
                List<String> exts = new ArrayList<String>();
                exts.add(".jpg");
                exts.add(".JPG");
                exts.add(".PNG");
                exts.add(".png");
                String tmpName = item.getName();
                String ext = tmpName.substring(tmpName.lastIndexOf("."));
                if (!exts.contains(ext)) {
                    logger.info("不支持的图片格式，只能上传.jpg或.png格式图片");
                    throw new Exception("只能上传.jpg或.png格式图片");
                }
                Random random = new Random();
                BigInteger big = new BigInteger(64, random);
                String imgName = big.toString() + ext;
                logger.info("上传图片的文件名:imgName:{}", imgName);
                File originalImg = new File(photoPath, imgName);
                OutputStream outStream = new FileOutputStream(originalImg);
                InputStream inStream = item.getInputStream();
                byte[] buffer = new byte[4096];
                int size = 0;
                while ((size = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, size);
                }
                inStream.close();
                outStream.close();
                inStream = null;
                outStream = null;
                //判断图片是否存在
                StringBuilder builder = new StringBuilder();
                // add by lcs 20150604 start
                builder.append(SEVER_ADDRESS);
                // add by lcs 20150604 end
                builder.append("/");
                builder.append(uploadPath);
                builder.append("/");
                builder.append(imgName);
                bean.setIcon(builder.toString());
                logger.info("cuserId:" + bean.getCuserId() + " 图片地址：" + builder.toString());
            }
        }
    }
}
