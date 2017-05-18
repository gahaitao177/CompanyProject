//package com.caiyi.financial.nirvana.discount.rest.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.caiyi.financial.nirvana.core.bean.BoltResult;
//import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
//import com.caiyi.financial.nirvana.core.client.IDrpcClient;
//import com.caiyi.financial.nirvana.core.constant.Constant;
//import com.caiyi.financial.nirvana.core.util.CheckUtil;
//import com.caiyi.financial.nirvana.core.util.MD5Util;
//import com.caiyi.financial.nirvana.core.util.StringUtils;
//import com.caiyi.financial.nirvana.core.util.SystemConfig;
//import com.caiyi.financial.nirvana.discount.user.bean.HskUser;
//import com.caiyi.financial.nirvana.discount.user.bean.User;
//import com.caiyi.financial.nirvana.discount.utils.*;
//import com.util.string.StringUtil;
//import org.dom4j.Element;
//import org.dom4j.dom.DOMElement;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by lizhijie on 2017/2/9.
// */
//@RestController
//public class RestHskUserController {
//    private static Logger logger = LoggerFactory.getLogger(RestHskUserController.class);
//    @Resource(name = Constant.HSK_USER)
//    private IDrpcClient client;
//    private static String baseUrl="http://192.168.1.51:10021/";
//
//    private final static String MD5_SMS_KEY = "6a37207e0fcd92";
//    public static final String ENCODING = "UTF-8";
//    public static Map<String, String> loginhezuo = new HashMap<String, String>();
//    /**
//     * 惠刷卡编号和9188编号映射
//     */
//    public static Map<String, String> merchantacctidMap = new HashMap<String, String>();
//
//    static {
//        loginhezuo.put("130313001", "A9FK25RHT487ULMI");//Android
//        loginhezuo.put("130313002", "A9FK25RHT487ULMI");//IOS
//
//        merchantacctidMap.put("130313001", "130313002");
//        merchantacctidMap.put("130313002", "130313003");
//        baseUrl= SystemConfig.get("user_http_url");
//    }
//
//    @RequestMapping("/user/registerchk.go")
//    public void registerchk9188(User bean, HttpServletRequest request, HttpServletResponse response) {
//        JSONObject jsonObject;
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        String mobileno = bean.getUid();
//        bean.setMobileNo(mobileno);
//        logger.info("请求发送验证码mobileNo=" + mobileno + "ipAddr=" + bean.getIpAddr());
//        //如果没能取到手机号码,提示用户重新尝试
//        if (StringUtil.isEmpty(mobileno) || !CheckUtil.isMobilephone(mobileno)) {
//            XmlUtils.writeXml(HskUserConstants.ERROR_FORMAT, "手机号码格式错误", response);
//            return;
//        }
//        if (CheckUtil.isNullString(bean.getTimeStamp()) || CheckUtil.isNullString(bean.getKey())) {
//            XmlUtils.writeXml(HskUserConstants.ERROR_PARAM, "版本过低，请升级客户端版本!", response);
//            return;
//        }
//        /**
//         * 惠刷卡校验是否允许注册
//         * 非快登情况
//         */
//        HashMap<String,String> params=new HashMap<>();
//        String ckeckIsExisUrl=baseUrl+"checkExistCphone";
//        params.put("cphone",mobileno);
//        String ckeckIsExis= HttpClientUtil.callHttpPost_Map(ckeckIsExisUrl,params);
//        jsonObject= JSON.parseObject(ckeckIsExis);
//        logger.info("ckeckIsExis:"+ckeckIsExis);
//        if(jsonObject!=null){
//            if("1".equals(jsonObject.getString("code"))){
//                if("true".equals(jsonObject.getString("data"))){
//                    logger.info("用户【"+bean.getUid()+"】已存在");
//                    if(!"quickLogin".equals(bean.getActionName())){
//                        XmlUtils.writeXml(HskUserConstants.ERROR_PARAM, "该手机号已存在,可以直接登录啦", response);
//                        return;
//                    }
//                }else {
//                    logger.info("用户【"+bean.getUid()+"】可以注册");
//                }
//            }else{
//                XmlUtils.writeXml(HskUserConstants.EXCEPTION, "程序异常", response);
//                return;
//            }
//        }else {
//            XmlUtils.writeXml(HskUserConstants.EXCEPTION, "程序异常", response);
//            return;
//        }
//        if(bean.getIclient()!=1||bean.getIclient()!=2){
//            bean.setIclient(3);
//        }
////       发送短信
//        if ("dk".equals(bean.getComeFrom())) {
//            bean.setYzmType(User.DK_YZM);
//        } else {
//            bean.setYzmType(User.YZM_TYPE);
//        }
//        String sendSmsResult = sendSms(bean);
//        jsonObject=JSON.parseObject(sendSmsResult);
//        if(jsonObject!=null) {
//            logger.info("手机号：【" + bean.getMobileNo() + "】，IP地址：【" + bean.getIpAddr() + "】");
//            if("1".equals(jsonObject.getString("code"))) {
//                XmlUtils.writeXml(jsonObject.getString("code"), jsonObject.getString("desc"), response);
//            }else {
//                XmlUtils.writeXml(HskUserConstants.ERROR_PARAM_DESC, jsonObject.getString("desc"), response);
//            }
//        }else {
//            XmlUtils.writeXml(-1, "程序异常", response);
//        }
//    }
//    public  String sendSms(User bean){
//        HashMap<String,String> params=new HashMap<>();
//        String sendSmsUrl=baseUrl+"sendSms";
//        if(StringUtils.isNotEmpty(bean.getUid())) {
//            params.put("cphone", bean.getUid());
//        }
//        if(StringUtils.isNotEmpty(bean.getMobileNo())){
//            params.put("cphone", bean.getMobileNo());
//        }
//        params.put("yzmType",bean.getYzmType());
//        params.put("csource",String.valueOf(bean.getSource()));
//        params.put("ipAddr",bean.getIpAddr());
//        params.put("mobileType",String.valueOf(bean.getIclient()));
//        params.put("channelType","0");
//        String result= HttpClientUtil.callHttpPost_Map(sendSmsUrl,params);
//        logger.info("短信发送结果:"+result);
//        return  result;
//    }
//
//    @RequestMapping("/user/register.go")
//    public void register9188(User bean, HttpServletRequest request, HttpServletResponse response) {
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        /**
//         * 1 校验参数
//         * 2 检查验证码
//         * 3 快登情况
//         * 4 调用9188注册
//         * 5 检测是否快登注册，快登注册，发送密码
//         * 6 保存用户信息到惠刷卡数据库
//         */
//        String mobileNo = bean.getUid();
//        bean.setMobileNo(mobileNo);
//        if (StringUtil.isEmpty(bean.getPwd())) {//生成随机密码
//            String pwd = CheckUtil.randomNum();
//            bean.setPwd(pwd);
//            bean.setPwd9188(pwd);
//        }
//        BoltResult result = null;
//        if (!CheckUtil.isMobilephone(mobileNo)) {
//            result = new BoltResult("1000","手机号码格式错误");
//        }else if (CheckUtil.isNullString(bean.getYzm())) {
//            result = new BoltResult("1000","验证码不能为空");
//        }
//        if(result != null){
//            XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//            return;
//        }
//
//        // 验证 验证码
//        if ("dk".equals(bean.getComeFrom())) {
//            bean.setYzmType(User.DK_YZM);
//        } else {
//            bean.setYzmType(User.YZM_TYPE);
//        }
//        JSONObject jsonObject;
//        String appId="";
//        String accessToken="";
//        //贷款 登录
//        if ("dk".equals(bean.getComeFrom())) {
//            String quickLoginResult=quickLogin(bean);
//            jsonObject=JSON.parseObject(quickLoginResult);
//            if(jsonObject!=null&&"1".equals(jsonObject.getString("code"))){
//                JSONObject data=jsonObject.getJSONObject("data");
//                bean.setCuserId(data.getString("cuserId"));
//                appId=data.getString("appId");
//                accessToken=data.getString("accessToken");
//                try {
//                    bean.setCreateTime(data.getDate("createTime"));
//                }catch (Exception e){
//                    logger.error("快速登录返回的时间格式 错误",e);
//                }
//            }else {
//                XmlUtils.writeXml(-1, "快速登录失败", response);
//                return;
//            }
//        }else{
//            HskUser user = userTransforHskUser(bean);
//            String registerResult= null;
//            try {
//                registerResult = register(user);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if(registerResult==null){
//                XmlUtils.writeXml(HskUserConstants.EXCEPTION, HskUserConstants.ERROR_PARAM_DESC, response);
//                return;
//            }else {
//                JSONObject data=JSON.parseObject(registerResult);
//                if(data!=null&&"1".equals(data.getString("code"))){
//                    JSONObject contend=data.getJSONObject("data");
//                    appId=contend.getString("appId");
//                    accessToken=contend.getString("accessToken");
////                    bean.setCreateTime(new Date());
//                    bean.setCuserId(contend.getString("cuserId"));
//                }else {
//                    XmlUtils.writeXml(data.getString("code"), data.getString("desc"), response);
//                    return;
//                }
//            }
//        }
//        bean.setYzmType(User.DK_PWD);
//        result = client.execute(new DrpcRequest("hskUser", "saveUserInfo9188", bean), BoltResult.class);
//        Element element = new DOMElement("Resp");
//        element.addAttribute("code",result.getCode());
//        element.addAttribute("desc",result.getDesc());
//        element.addAttribute("appId",appId);
//        element.addAttribute("accessToken",accessToken);
//        XmlUtils.writeXml(element,response);
//    }
//
//    /**
//     * 手机短信快速登录
//     * @param user
//     * @return
//     */
//    public String quickLogin(User user){
//        HashMap<String,String> params=new HashMap<>();
//        String quickLoginUrl=baseUrl+"fastLogin";
//        params.put("cphone",user.getUid());
//        params.put("yzmType",user.getYzmType());
//        params.put("csource",String.valueOf(user.getSource()));
//        params.put("ipAddr",user.getIpAddr());
//        params.put("mobileType",String.valueOf(user.getIclient()));
//        params.put("packageName",user.getPackagename());
//        String quickLoginResult= HttpClientUtil.callHttpPost_Map(quickLoginUrl,params);
//        logger.info("短信快速登录结果:"+quickLoginResult);
//        return  quickLoginResult;
//    }
//
//    /**
//     * 旧user  转成 HskUser
//     * @param user
//     * @return hskUser
//     */
//    public HskUser userTransforHskUser(User user){
//        HskUser hskUser=new HskUser();
//        if(user==null)
//            return  null;
//        if(StringUtils.isNotEmpty(user.getUid())) {
//            hskUser.setCphone(user.getUid());
//        }
//        if(StringUtils.isNotEmpty(user.getMobileNo())){
//            hskUser.setCphone(user.getMobileNo());
//        }
//        hskUser.setYzm(user.getYzm());
//        hskUser.setYzmType(user.getYzmType());
//        hskUser.setMobileType(user.getIclient());
//        hskUser.setCsource(user.getSource());
//        hskUser.setIpAddr(user.getIpAddr());
//        hskUser.setCuserId(user.getCuserId());
//        hskUser.setIloginfrom(0);
//        hskUser.setOldPassword(user.getOldPwd());
//        hskUser.setNewPassword(user.getNewPwd());
//        hskUser.setCpassword(user.getPwd());
//        hskUser.setCpassword9188(user.getPwd9188());
//        hskUser.setPackagename(user.getPackagename());
//        hskUser.setChannelType(0);
//        return  hskUser;
//    }
//
//    /**
//     *注册
//     * @param bean
//     * @return
//     * @throws Exception
//     */
//    public  String register (HskUser bean) throws Exception {
//        HashMap<String,String> params=new HashMap<>();
//        String sendSmsUrl=baseUrl+"register";
//        params.put("cphone",bean.getCphone());
//        params.put("yzm",bean.getYzm());
//        params.put("yzmType",bean.getYzmType());
//        params.put("csource",String.valueOf(bean.getCsource()));
//        params.put("ipAddr",bean.getIpAddr());
//        params.put("cbelongValue",HskUser.COME_FROM);
//        params.put("mobileType",String.valueOf(bean.getMobileType()));
//        params.put("cpassword", MD5Util.compute(bean.getCpassword()+HskUser.MD5_KEY_9188));
//        params.put("packageName",bean.getPackagename());
//        String result= HttpClientUtil.callHttpPost_Map(sendSmsUrl,params);
//        logger.info("注册结果:"+result);
//        return  result;
//    }
//    @RequestMapping("/user/login.go")
//    public void login(User bean, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
//        bean.setMobileNo(bean.getUid());
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        if (StringUtil.isEmpty(bean.getPwd())) {//生成随机密码
//            String pwd = CheckUtil.randomNum();
//            bean.setPwd(pwd);
//        }
//        BoltResult result = null;
//        if (!CheckUtil.isMobilephone(bean.getUid())) {
//            result = new BoltResult(String.valueOf(HskUserConstants.ERROR_FORMAT),"手机号码格式错误");
//        }
////        else if (CheckUtil.isNullString(bean.getYzm())) {
////            result = new BoltResult(String.valueOf(HskUserConstants.ERROR_FORMAT),"验证码不能为空");
////        }
//        if(result != null){
//            XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//            return;
//        }
//
//        JSONObject jsonObject;
//        //贷款 登录
//        if ("dk".equals(bean.getComeFrom())) {
//            String quickLoginResult=quickLogin(bean);
//            jsonObject=JSON.parseObject(quickLoginResult);
//            if(jsonObject!=null&&"1".equals(jsonObject.getString("code"))){
//                JSONObject data=jsonObject.getJSONObject("data");
//                bean.setCuserId(data.getString("cuserId"));
//                try {
//                    bean.setCreateTime(data.getDate("createTime"));
//                }catch (Exception e){
//                    logger.error("快速登录返回的时间格式 错误",e);
//                }
//            }else {
//                XmlUtils.writeXml(-1, "快速登录失败", response);
//                return;
//            }
//        }else {
//            String merchantacctid = bean.getMerchantacctId();
//            String signtype = bean.getSignType();
//            String signmsg = bean.getSignMsg();
//            String userNickName = bean.getUid();
//
//            //昵称登录的情况
//            if("130313002".equals(bean.getMerchantacctId())){//ios需要转码
//                userNickName = URLDecoder.decode(userNickName, "utf-8");
//            }else{
//                userNickName = new String(userNickName.getBytes("UTF-8"),ENCODING);
//            }
//            bean.setUid(userNickName);
//            if (signtype.equals("1")){//MD5
//                if (loginhezuo.containsKey(merchantacctid)){
//                    // 生成加密签名串
//                    // 请务必按照如下顺序和规则组成加密串！
//                    String signMsgVal = "";
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "signType", signtype);
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "merchantacctId", merchantacctid);
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "uid", bean.getUid());
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "pwd", bean.getPwd());
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "key", loginhezuo.get(merchantacctid));
//                    logger.info("惠刷卡--signMsgVal=" + signMsgVal);
//                    String serverSignMsg = BankUtil.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();
//                    logger.info("惠刷卡--serverSignMsg=" + serverSignMsg);
//
//                    if (serverSignMsg.equals(signmsg)){//验签成功
//                        logger.info("用户【" + userNickName + "】,惠刷卡验签成功,bean pwd:" + bean.getPwd() + ",bean getPwd9188:" + bean.getPwd9188());
//                        HskUser user = userTransforHskUser(bean);
//                        String loginResult = login(user);
//                        if (loginResult == null) {
//                            XmlUtils.writeXml(HskUserConstants.EXCEPTION, HskUserConstants.ERROR_PARAM_DESC, response);
//                            return;
//                        } else {
//                            Element element = new DOMElement("Resp");
//                            JSONObject jsonObj = JSON.parseObject(loginResult);
//                            JSONObject data = jsonObj.getJSONObject("data");
//                            System.out.println("结果:"+"1".equals(jsonObj.getString("code")));
//                            if (jsonObj != null && "1".equals(jsonObj.getString("code"))) {
//                                element.addAttribute("code","1");
//                                element.addAttribute("desc","登录成功");
//                                element.addAttribute("appId",data.getString("appId"));
//                                element.addAttribute("accessToken",data.getString("accessToken"));
//                                bean.setCuserId(data.getString("cuserId"));
//                                bean.setCnickname(data.getString("cnickId"));
//                                client.execute(new DrpcRequest("hskUser", "saveUserInfo9188", bean));
//                            }else {
//                                element.addAttribute("code","0");
//                                element.addAttribute("desc",data.getString("desc"));
//                            }
//                            XmlUtils.writeXml(element,response);
//                            return;
//                        }
//                    }else{
//                        logger.info("签名失效：");
//                        logger.info("服务端签名结果为：" + serverSignMsg + "");
//                        logger.info("客户端签名结果为：" + signmsg + "");
//                        logger.info("signtype=" + signtype + "");
//                        logger.info("merchantacctid=" + merchantacctid + "");
//                        logger.info("uid=" + bean.getUid() + "");
//                        logger.info("pwd=" + bean.getPwd() + "");
////                        bean.setBusiErrCode(1001);
////                        bean.setBusiErrDesc("签名失败");
//                        XmlUtils.writeXml("1001", "签名失败", response);
//                        return ;
//                    }
//                }else{
//                    logger.info("错误的 merchantacctid=" + merchantacctid);
//                }
//            }else{
//                logger.info("错误的signtype=" + signtype);
//            }
//        }
//    }
//    public  String login (HskUser bean){
//        HashMap<String,String> params=new HashMap<>();
//        String loginUrl=baseUrl+"login";
//        params.put("cuserId",bean.getCphone());
//        params.put("cpassword",bean.getCpassword9188());
//        params.put("csource",String.valueOf(bean.getCsource()));
//        params.put("cloginfrom",HskUser.COME_FROM);
//        params.put("ipAddr",bean.getIpAddr());
//        params.put("mobileType",String.valueOf(bean.getMobileType()));
//        params.put("packageName",bean.getPackagename());
//        String result= HttpClientUtil.callHttpPost_Map(loginUrl,params);
//        logger.info("登录结果:"+result);
//        return  result;
//    }
//    @RequestMapping("user/mobileBinding.go")
//    public void  bandingPhone(User bean, HttpServletRequest request, HttpServletResponse response){
//
//    }
//    @RequestMapping("user/modifyUserInfo.go")
//    public void  modifyPassword(User bean, HttpServletRequest request, HttpServletResponse response){
//        Element element = new DOMElement("Resp");
//        if(StringUtil.isEmpty(bean.getOldPwd())){
//            element.addAttribute("code","0");
//            element.addAttribute("desc","旧密码不能为空");
//        }
//        if (StringUtil.isEmpty(bean.getNewPwd())){
//            element.addAttribute("code","0");
//            element.addAttribute("desc","新密码不能为空");
//        }
//        if (StringUtil.isEmpty(bean.getModifFlag())){
//            element.addAttribute("code","0");
//            element.addAttribute("desc","参数错误");
//        }
//        if (StringUtil.isEmpty(bean.getOldPwd())||StringUtil.isEmpty(bean.getNewPwd())||
//                StringUtil.isEmpty(bean.getOldPwd())){
//            XmlUtils.writeXml(element,response);
//            return;
//        }
//        HskUser user=userTransforHskUser(bean);
//        if(user!=null){
//           String motifiedPassResult= motifyPassword(user);
//            JSONObject data=JSON.parseObject(motifiedPassResult);
//            if (data!=null){
//                element.addAttribute("code",data.getString("code"));
//                element.addAttribute("desc",data.getString("desc"));
//            }else {
//                element.addAttribute("code","0");
//                element.addAttribute("desc","参数错误");
//            }
//        }else {
//            element.addAttribute("code","0");
//            element.addAttribute("desc","参数错误");
//        }
//        XmlUtils.writeXml(element,response);
//    }
//
//    /**
//     * 修改密码 http 请求
//     * @param bean
//     * @return
//     */
//    private  String motifyPassword(HskUser bean){
//        HashMap<String,String> params=new HashMap<>();
//        String loginUrl=baseUrl+"updatePwd";
//        params.put("cuserId",bean.getCuserId());
//        params.put("cpassword",bean.getNewPassword());
//        params.put("oldPassword",bean.getOldPassword());
////        params.put("csource",String.valueOf(bean.getCsource()));
//        params.put("cloginfrom",HskUser.COME_FROM);
//        params.put("ipAddr",bean.getIpAddr());
//        params.put("mobileType",String.valueOf(bean.getMobileType()));
//        params.put("packageName",bean.getPackagename());
//        String result= HttpClientUtil.callHttpPost_Map(loginUrl,params);
//        logger.info("修改密码结果:"+result);
//        return  result;
//    }
//    @RequestMapping("/user/resetPwdNotLogin.go")
//    public void resetPwdNotLogin9188(User bean,HttpServletRequest request,HttpServletResponse response){
//        logger.info("actionName:"+ bean.getActionName());
//        if (CheckUtil.isNullString(bean.getMobileNo())){
//            XmlUtils.writeXml(HskUserConstants.ERROR_PARAM,"请填写手机号", response);
//            return ;
//        }
//        if (!CheckUtil.isMobilephone(bean.getMobileNo())){
//            XmlUtils.writeXml(HskUserConstants.ERROR_FORMAT, "手机号格式错误", response);
//            return ;
//        }
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        HskUser hskUser = userTransforHskUser(bean);
//        bean.setYzmType("1");
//        if("sendYzm".equals(bean.getActionName())) {
//            if (hskUser != null) {
//                String sendSms = sendSms(bean);
//                JSONObject jsonObject = JSON.parseObject(sendSms);
//                logger.info("手机号：【" + bean.getMobileNo() + "】 发送结果：" + sendSms);
//                if (jsonObject != null) {
//                    if ("1".equals(jsonObject.getString("code"))) {
//                        XmlUtils.writeXml(jsonObject.getString("code"), jsonObject.getString("desc"), response);
//                    } else {
//                        XmlUtils.writeXml(HskUserConstants.ERROR_PARAM, jsonObject.getString("desc"), response);
//                    }
//                    logger.info("手机号：【" + bean.getMobileNo() + "】，IP地址：【" + bean.getIpAddr() + "】");
//                } else {
//                    XmlUtils.writeXml(-1, "程序异常", response);
//                }
//            } else {
//                XmlUtils.writeXml(-1, "程序异常", response);
//            }
//        }else if("reSetPwd".equals(bean.getActionName())) {
//            resetPassword(bean,request,response);
//        }
//    }
//    public void  resetPassword(User bean, HttpServletRequest request, HttpServletResponse response){
////        bean.setIpAddr(WebUtil.getRealIp(request));
//
//        Element element = new DOMElement("Resp");
////        if(StringUtil.isEmpty(bean.getMobileNo())){
////            element.addAttribute("code","0");
////            element.addAttribute("desc","手机号不能为空");
////        }
//        if (StringUtil.isEmpty(bean.getNewPwd())){
//            element.addAttribute("code","0");
//            element.addAttribute("desc","新密码不能为空");
//        }
//        if (StringUtil.isEmpty(bean.getYzm())){
//            element.addAttribute("code","0");
//            element.addAttribute("desc","验证码不能为空");
//        }
////        if (!"resetPwd".equals(bean.getActionName())){
////            element.addAttribute("code","0");
////            element.addAttribute("desc","参数错误");
////        }
//        if (StringUtil.isEmpty(bean.getNewPwd())||StringUtil.isEmpty(bean.getYzm())){
//            XmlUtils.writeXml(element,response);
//            return;
//        }
//        HskUser user=userTransforHskUser(bean);
//        if(user!=null){
//           String result= resetPasswordHttp(user);
//            JSONObject data=JSON.parseObject(result);
//            if(data!=null){
//                element.addAttribute("code",data.getString("code"));
//                element.addAttribute("desc",data.getString("desc"));
//            }else{
//                element.addAttribute("code","0");
//                element.addAttribute("desc","程序异常");
//            }
//        }else {
//            element.addAttribute("code","0");
//            element.addAttribute("desc","程序异常");
//        }
//        XmlUtils.writeXml(element,response);
//    }
//    /**
//     * 重置密码
//     * @param bean
//     * @return
//     */
//    private  String resetPasswordHttp(HskUser bean){
//        HashMap<String,String> params=new HashMap<>();
//        String resetUrl=baseUrl+"resetPwd";
//        params.put("cphone",bean.getCphone());
//        params.put("cpassword",bean.getNewPassword());
//        params.put("yzm",bean.getYzm());
//        params.put("yzmType",String.valueOf(bean.getYzmType()));
//        params.put("cloginfrom",HskUser.COME_FROM);
//        params.put("ipAddr",bean.getIpAddr());
//        params.put("mobileType",String.valueOf(bean.getMobileType()));
//        params.put("packageName",bean.getPackagename());
//        String result= HttpClientUtil.callHttpPost_Map(resetUrl,params);
//        logger.info("重置密码结果:"+result);
//        return  result;
//    }
//}
