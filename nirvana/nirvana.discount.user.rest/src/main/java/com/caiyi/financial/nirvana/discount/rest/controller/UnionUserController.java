//package com.caiyi.financial.nirvana.discount.rest.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
//import com.caiyi.financial.nirvana.core.client.IDrpcClient;
//import com.caiyi.financial.nirvana.core.constant.Constant;
//import com.caiyi.financial.nirvana.core.util.CheckUtil;
//import com.caiyi.financial.nirvana.core.util.MD5Util;
//import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
//import com.caiyi.financial.nirvana.discount.token.UniqueStrCreator;
//import com.caiyi.financial.nirvana.discount.user.bean.User;
//import com.caiyi.financial.nirvana.discount.utils.BankUtil;
//import com.caiyi.financial.nirvana.discount.utils.UserCenter;
//import com.caiyi.financial.nirvana.discount.utils.WebUtil;
//import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
//import com.caiyi.user.domain.base.LoginInfo;
//import com.caiyi.user.domain.enums.UserCodeDict;
//import com.caiyi.user.domain.response.SimpleRsp;
//import com.caiyi.user.domain.response.UserInfoRsp;
//import com.caiyi.user.domain.response.UserLoginRsp;
//import com.caiyi.user.domain.response.UserRegisterRsp;
//import com.security.utils.UserErrCode;
//import com.util.string.StringUtil;
//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;
//import org.dom4j.dom.DOMElement;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
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
// * Created by liuweiguo on 2016/8/10.
// */
////@RestController
////@RequestMapping("/user")
//public class UnionUserController {
//    private static Logger logger = LoggerFactory.getLogger(UnionUserController.class);
//    private final static String MD5_SMS_KEY = "6a37207e0fcd92";
//
//    public static Map<String, String> loginhezuo = new HashMap<String, String>();
//
//    static {
//        loginhezuo.put("130313001", "A9FK25RHT487ULMI");//Android
//        loginhezuo.put("130313002", "A9FK25RHT487ULMI");//IOS
//    }
//
//    @Autowired
//    UserCenter userCenter;
//
//    @Resource(name = Constant.HSK_USER)
//    IDrpcClient client;
//
//    /**
//     * 测试setUserData
//     *
//     * @param user
//     * @param request
//     * @param response
//     * @throws UnsupportedEncodingException
//     */
//    @SetUserDataRequired
//    @RequestMapping("/setUserTest.go")
//    public User setUserTest(User user, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
//        logger.info("==========================================");
//        System.out.println(request.getParameter("cuserId"));
//        System.out.println(request.getParameter("busiXml"));
//        logger.info("cuserId=" + user.getCuserId());
//        logger.info("==========================================");
//        return user;
//    }
//
//    /**
//     * 新版登录
//     *
//     * @param user
//     * @param request
//     * @param response
//     * @throws UnsupportedEncodingException
//     */
//    @RequestMapping("/login.go")
//    public void union_login(User user, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
//        logger.info("新版登录：uid={},cuserId={}.", user.getCuserId(), user.getCuserId());
//        if (StringUtil.isEmpty(user.getSignMsg())
//                || StringUtil.isEmpty(user.getSignType())
//                || StringUtil.isEmpty(user.getMerchantacctId())
//                || StringUtil.isEmpty(user.getUid())
//                || StringUtil.isEmpty(user.getPwd())
//                ) {
//            XmlUtils.writeXml("1000", "非法的登录参数", response);
//            return;
//        } else {
//            user.setIpAddr(WebUtil.getRealIp(request).trim());
//            String merchantacctid = user.getMerchantacctId();
//            String signtype = user.getSignType();
//            String signmsg = user.getSignMsg();
//            String userNickName = user.getUid();
//            System.out.println("接收参数uid=" + userNickName);
//            //昵称登录的情况
//            if ("130313002".equals(user.getMerchantacctId())) {//ios需要转码
//                userNickName = URLDecoder.decode(userNickName, "utf-8");
//            } else {
//                userNickName = new String(userNickName.getBytes("UTF-8"), "UTF-8");
//            }
//            user.setUid(userNickName);
//            if (signtype.equals("1")) {//MD5
//                if (loginhezuo.containsKey(merchantacctid)) {
//                    // 生成加密签名串
//                    // 请务必按照如下顺序和规则组成加密串！
//                    String signMsgVal = "";
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "signType", signtype);
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "merchantacctId", merchantacctid);
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "uid", user.getUid());
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "pwd", user.getPwd());
//                    signMsgVal = WebUtil.appendParam(signMsgVal, "key", loginhezuo.get(merchantacctid));
//                    System.out.println("惠刷卡--signMsgVal=" + signMsgVal);
//                    String serverSignMsg = BankUtil.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();
//                    System.out.println("惠刷卡--serverSignMsg=" + serverSignMsg);
//
//                    if (serverSignMsg.equals(signmsg)) {//验签成功
//                        System.out.println("用户【" + userNickName + "】,惠刷卡验签成功,bean pwd:" + user.getPwd() + ",bean getPwd9188:" + user.getPwd9188());
//
//                        //登录到用户中心
//                        UserLoginRsp userLoginRsp = userCenter.login(user);
//                        String code = userLoginRsp.getResponseCode();//登录结果码
//                        String desc = userLoginRsp.getResponseMessage();//登录结果说明
//                        LoginInfo info = userLoginRsp.getLoginInfo();
//
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("code", code);
//                        jsonObject.put("desc", desc);
//
//                        // 处理登录结果
//                        //登录失败
//                        if (!UserCenter.SUCCESSCODE.equals(code)) {//登录成功
//                            XmlUtils.writeXml(code, desc, response);
//                            return;
//                        } else {//登录成功
//                            jsonObject.put("code", 1);
//
//                            jsonObject.put("appId", UniqueStrCreator.createUniqueString("lc"));
//                            jsonObject.put("accessToken", info.getToken());
//                            jsonObject.put("cuserid", info.getUserId());
//                            user.setCuserId(info.getUserId());
//                            //查询用户信息
//                            user.setAccessToken(info.getToken());
//                            UserInfoRsp userInfoRsp = userCenter.getUserByToken(user);
//                            transformUser(userInfoRsp.getUser(), user);
//                            //保存用户信息到本地
//                            String result = client.execute(new DrpcRequest("user", "persist9188User", user));
//
//                            //保存token
//                            //由用户中心统一保存
//
//                        }
//                        XmlUtils.writeXml(XmlUtils.jsonParseXml(jsonObject, "Resp").asXML(), response);
//                        return;
//                    } else {
//                        System.out.println("签名失效：");
//                        System.out.println("服务端签名结果为：" + serverSignMsg + "");
//                        System.out.println("客户端签名结果为：" + signmsg + "");
//                        System.out.println("signtype=" + signtype + "");
//                        System.out.println("merchantacctid=" + merchantacctid + "");
//                        System.out.println("uid=" + user.getUid() + "");
//                        System.out.println("pwd=" + user.getPwd() + "");
//                        System.out.println("pwd9188=" + user.getPwd9188() + "");
//
//                        XmlUtils.writeXml("1001", "签名失败", response);
//                    }
//                } else {
//                    System.out.println("错误的 merchantacctid=" + merchantacctid);
//                }
//            } else {
//                System.out.println("错误的signtype=" + signtype);
//            }
//        }
//    }
//
//
//    /**
//     * 手机号注册资格检测，并发送验证码
//     *
//     * @param user
//     * @param request
//     * @param response
//     */
//    @RequestMapping("/registerchk.go")
//    public void union_registerchk(User user, HttpServletRequest request, HttpServletResponse response) {
//        user.setIpAddr(WebUtil.getRealIp(request).trim());
//        String mobileno = user.getUid();
//        user.setMobileNo(mobileno);
//        logger.info("请求发送验证码:mobileNo=" + mobileno + ",ipAddr=" + user.getIpAddr());
//        //如果没能取到手机号码,提示用户重新尝试
//        if (StringUtil.isEmpty(mobileno) || !CheckUtil.isMobilephone(mobileno)) {
//            XmlUtils.writeXml(1000, "手机号码格式错误", response);
//            return;
//        }
//        if (CheckUtil.isNullString(user.getTimeStamp()) || CheckUtil.isNullString(user.getKey())) {
//            XmlUtils.writeXml(1001, "版本过低，请升级客户端版本!", response);
//            return;
//        }
//        try {
//            if (!CheckUtil.isNullString(user.getTimeStamp()) && !CheckUtil.isNullString(user.getKey())) {
//                StringBuffer str = new StringBuffer();
//                str.append(mobileno);
//                str.append(user.getTimeStamp());
//                str.append(MD5_SMS_KEY);
//                String SeKey = MD5Util.compute(str.toString());
//                logger.debug("远程签名=" + user.getKey() + "，本地签名=" + SeKey);
//                if ((!user.getKey().equals(SeKey))) {
//                    XmlUtils.writeXml(1001, "参数错误!", response);
//                    return;
//                }
//            }
//
//            //调用9188接口检测手机号码
//            SimpleRsp simpleRsp = userCenter.isMobilenoRegister(user);
//            String code = simpleRsp.getResponseCode();
//            String desc = simpleRsp.getResponseMessage();
//            String result = simpleRsp.getResult();
//
//            if (UserCenter.SUCCESSCODE.equals(code)) {//调用成功
//                if (!"true".equals(result) && !"quickLogin".equals(user.getActionName())) {//已经注册过，直接返回
//                    XmlUtils.writeXml(1001, "这个手机号已经注册啦,请直接登录!", response);
//                    return;
//                }
//                if ("quickLogin".equals(user.getActionName())) {//注册发送验证码||快速登录发送验证码
//                    user.setYzmType(UserCenter.DK_YZM);
//                } else {
//                    user.setYzmType(UserCenter.HSK_REG);
//                }
//                String yzm = CheckUtil.randomNum();
//                user.setYzm(yzm);
//                SimpleRsp res = userCenter.sendSms(user);
//                if (UserCenter.SUCCESSCODE.equals(res.getResponseCode())) {
//                    XmlUtils.writeXml(1, "验证码发送成功", response);
//                } else {
//                    XmlUtils.writeXml(res.getResponseCode(), res.getResponseMessage(), response);
//                }
//                return;
//            } else {//调用接口异常返回
//                XmlUtils.writeXml(code, desc, response);
//            }
//        } catch (Exception e) {
//            XmlUtils.writeXml(1099, "对不起,请重试.", response);
//            logger.info("检测手机号注册资格出错:" + mobileno, e);
//        }
//    }
//
//    /**
//     * 注册,明文密码
//     *
//     * @param user
//     * @param request
//     * @param response
//     */
//    @RequestMapping("/register.go")
//    public void union_register(User user, HttpServletRequest request, HttpServletResponse response) {
//
//        if (StringUtil.isEmpty(user.getPwd())) {//生成随机密码
//            String pwd = CheckUtil.randomNum();
//            user.setPwd(pwd);
//            user.setPwd9188(pwd);
//        }
//        if (CheckUtil.isNullString(user.getUid())) {
//            XmlUtils.writeXml(1000, "用户名不能为空", response);
//            return;
//        } else {
//            if (!CheckUtil.isMobilephone(user.getUid())) {
//                XmlUtils.writeXml(1000, "手机号码格式错误", response);
//                return;
//            }
//        }
//        if (CheckUtil.isNullString(user.getPwd())) {
//            XmlUtils.writeXml(1000, "密码不能为空", response);
//            return;
//        }
//        if (CheckUtil.isNullString(user.getYzm())) {
//            XmlUtils.writeXml(1000, "验证码不能为空", response);
//            return;
//        }
//        logger.info("注册参数:uid={}", user.getUid());
//        try {
//            String appid = "appId";
//            String accesstoken = "";
//            user.setMobileNo(user.getUid());
//
//            if ("dk".equals(user.getComeFrom())) {//快速注册登录
//                user.setYzmType(User.DK_YZM);
//                //快速登录，注册成功，将密码发送给用户
//                UserLoginRsp loginRsp = userCenter.registerAndLogin(user);
//                LoginInfo info = loginRsp.getLoginInfo();
//                accesstoken = info.getToken();
//            } else {//普通注册
//                //检验验证码
//                user.setYzmType(UserCenter.HSK_REG);
//                SimpleRsp res = userCenter.checkSms(user);
//                if (!UserCenter.SUCCESSCODE.equals(res.getResponseCode())) {
//                    XmlUtils.writeXml(res.getResponseCode(), res.getResponseMessage(), response);
//                    return;
//                }
//
//                //新用户注册
//                UserRegisterRsp registerRsp = userCenter.register(user);
//                if (!UserCenter.SUCCESSCODE.equals(registerRsp.getResponseCode())) {
//                    XmlUtils.writeXml(registerRsp.getResponseCode(), registerRsp.getResponseMessage(), response);
//                    return;
//                }
//            }
//
//            //使用9188token，不再自己生成
//            user.setAppId(appid);
//            user.setAccessToken(accesstoken);
//
//            //保存用户信息到本地数据库
//            String userid = "";
//            user.setCuserId(userid);
//
//            String pwd9188 = MD5Util.compute(user.getPwd() + User.MD5_KEY_9188);//9188加密的密码
//            String pwd = MD5Util.compute(user.getPwd() + User.MD5_KEY);//惠刷卡加密的密码
//            user.setPwd9188(pwd9188);
//            user.setPwd(pwd);
//
////            XmlUtils.writeXml(1,"注册成功！",response);
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("code", 1);
//            jsonObject.put("desc", "注册成功");
//
//            jsonObject.put("appId", appid);
//            jsonObject.put("accessToken", accesstoken);
//
//            XmlUtils.writeXml(XmlUtils.jsonParseXml(jsonObject, "Resp").asXML(), response);
//        } catch (Exception e) {
//            user.setBusiErrCode(UserErrCode.ERR_EXCEPTION);
//            user.setBusiErrDesc(UserErrCode.getErrDesc(user.getBusiErrCode()));
//            logger.error("UserBeanStub::register ", e);
//        }
//    }
//
//    /**
//     * 修改用户信息(登录状态 修改密码)
//     *
//     * @param user
//     * @param request
//     * @param response
//     */
//    @SetUserDataRequired
//    @RequestMapping("/modifyUserInfo.go")
//    public void union_modifyUserPwd(User user, HttpServletRequest request, HttpServletResponse response) {
//        try {
//            logger.info("用户CuserId=" + user.getCuserId());
//            logger.info("flag---111:" + user.getModifFlag() + ";userid:" + user.getCuserId());
//            // 登录状态 修改密码
//            if ("0".equals(user.getModifFlag())) {
//                if (CheckUtil.isNullString(user.getOldPwd()) || CheckUtil.isNullString(user.getNewPwd())) {
//                    XmlUtils.writeXml(1001, "请填写原密码和新密码", response);
//                    return;
//                }
//
//                //查询用户名
//                JSONObject json = client.execute(new DrpcRequest("user", "queryUserAccount", user), JSONObject.class);
//
//                String cusername = "";
//                if (json.containsKey("cusername")) {
//                    cusername = json.getString("cusername");
//                } else {
//                    XmlUtils.writeXml(9009, "没有该用户", response);
//                }
//
//                //调用9188修改密码接口
//                user.setCusername(cusername);
//                SimpleRsp res = userCenter.changeLoginPassword(user);
//                String result = res.getResult();
//                String code = res.getResponseCode();
//                String desc = res.getResponseMessage();
//
//                if (UserCenter.SUCCESSCODE.equals(code) && "0".equals(result)) {//修改密码成功
//                    XmlUtils.writeXml(1, "密码修改成功", response);
//
//                    //更新本地密码
//                    String pwd = MD5Util.compute(user.getNewPwd() + User.MD5_KEY);
//                    String pwd9188 = MD5Util.compute(user.getNewPwd() + User.MD5_KEY_9188);
//                    user.setPwd(pwd);
//                    user.setPwd9188(pwd9188);
//                    client.execute(new DrpcRequest("user", "updateUserPwd", user));
//                    return;
//                } else {
//                    UserCodeDict dict = UserCodeDict.getByCode(result);
//                    XmlUtils.writeXml(result, dict.getDesc(), response);
//                    return;
//                }
//            } else {
//                XmlUtils.writeXml(1009, "参数异常", response);
//                return;
//            }
//        } catch (Exception e) {
//            XmlUtils.writeXml(-1, "fail", response);
//            logger.info(e.getMessage(), e);
//        }
//    }
//
//    /**
//     * 发送忘记密码验证码，重置密码 (未登录)
//     * <p>
//     * <b>调用参数：</b>
//     * 发送验证码：<br>
//     * appVersion=231<br>
//     * source=5000<br>
//     * mtype=1<br>
//     * uuid=86e18ade-0d60-4537-91e8-8a1f23e95304<br>
//     * key=a781b601828be50f109b400fabc5bbaf<br>
//     * iclient=0<br>
//     * timeStamp=1468570261746<br>
//     * actionName=sendYzm<br>
//     * mobileNo=18703643669<br>
//     * 重置密码：<br>
//     * newPwd=888888<br>
//     * actionName=reSetPwd<br>
//     * yzm=123123<br>
//     * mobileNo=18703643669
//     * </p>
//     *
//     * @param user
//     * @param request
//     * @param response
//     */
//    @RequestMapping("/resetPwdNotLogin.go")
//    public void union_resetPwdNotLogin(User user, HttpServletRequest request, HttpServletResponse response) {
//        user.setIpAddr(WebUtil.getRealIp(request).trim());
//        if (CheckUtil.isNullString(user.getMobileNo())) {
//            XmlUtils.writeXml(1000, "请填写手机号", response);
//            return;
//        }
//        if (!CheckUtil.isMobilephone(user.getMobileNo())) {
//            XmlUtils.writeXml(1001, "手机号格式错误", response);
//            return;
//        }
//        try {
//            int num = 1;
//            // update by lcs 20150906 end
//            logger.info("actionName:" + user.getActionName());
//            if ("sendYzm".equals(user.getActionName())) {
//
//                // add by lcs 20150924
//                if (CheckUtil.isNullString(user.getTimeStamp()) || CheckUtil.isNullString(user.getKey())) {
//                    XmlUtils.writeXml(1001, "版本过低，请升级客户端版本!", response);
//                    return;
//                }
//                //检测是否注册过
//                SimpleRsp simpleRsp = userCenter.isMobilenoRegister(user);
//                if (null != simpleRsp) {
//                    String code = simpleRsp.getResponseCode();
//                    String desc = simpleRsp.getResponseMessage();
//                    String result = simpleRsp.getResult();
//                    if (UserCenter.SUCCESSCODE.equals(code)) {//请求成功
//                        if ("true".equals(result)) {
//                            XmlUtils.writeXml(-1, "该手机号没有注册过", response);
//                            return;
//                        }
//                    }
//                } else {
//                    XmlUtils.writeXml(-1, "无法获取服务器数据,请稍候再试!", response);
//                    return;
//                }
//
//                // 发送验证码
//                String yzm = CheckUtil.randomNum();
//                user.setYzm(yzm);
//                user.setYzmType(UserCenter.HSK_FORGET_PWD);
//                SimpleRsp res = userCenter.sendSms(user);
//                if (UserCenter.SUCCESSCODE.equals(res.getResponseCode())) {
//                    XmlUtils.writeXml(1, "验证码发送成功", response);
//                } else {
//                    XmlUtils.writeXml(-1, res.getResponseMessage(), response);
//                }
//            } else if ("reSetPwd".equals(user.getActionName())) {
//                if (CheckUtil.isNullString(user.getYzm())) {
//                    XmlUtils.writeXml(1001, "验证码不能为空", response);
//                    return;
//                }
//                if (CheckUtil.isNullString(user.getNewPwd())) {
//                    XmlUtils.writeXml(1001, "新密码不能为空", response);
//                    return;
//                }
//                user.setPwd(user.getNewPwd());
//                // 检验 验证码
//                user.setYzmType(UserCenter.HSK_FORGET_PWD);
//                SimpleRsp rsp = userCenter.checkSms(user);
//                if (!UserCenter.SUCCESSCODE.equals(rsp.getResponseCode())) {
//                    XmlUtils.writeXml(rsp.getResponseCode(), rsp.getResponseMessage(), response);
//                    return;
//                }
//
//                //如遇到错误，请删除本地仓库中的目录 .m2/repository/com/caiyi/user/user-api-client
//                SimpleRsp res = userCenter.resetPwd(user);
//                String result = res.getResult();
//                String code = res.getResponseCode();
//                String desc = res.getResponseMessage();
//
//                if (UserCenter.SUCCESSCODE.equals(code)) {
//                    //密码加密
//                    String pwd = MD5Util.compute(user.getNewPwd() + User.MD5_KEY);
//                    String pwd9188 = MD5Util.compute(user.getNewPwd() + User.MD5_KEY_9188);
//
//                    user.setPwd(pwd);
//                    user.setPwd9188(pwd9188);
//                    //更新本地密码
//                    client.execute(new DrpcRequest("user", "updateUserPwd", user));
//
//                    XmlUtils.writeXml(1, "重设密码成功", response);
//                    return;
//                } else {
//                    XmlUtils.writeXml(code, desc, response);
//                }
//            } else {
//                XmlUtils.writeXml(1004, "参数异常", response);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            XmlUtils.writeXml("1099", "系统有误，请稍后重新操作", response);
//        }
//    }
//
//    @SetUserDataRequired
//    @RequestMapping("/queryUserAccount.go")
//    public void queryUserAccount1(User user, HttpServletRequest request, HttpServletResponse response) {
//
//        //查询收藏信息 适用于1.3.1之后版本
//
//
//        //获取账号信息
//        logger.info("**********************查询个人信息********************");
//        logger.info("接收参数getCuserId=" + user.getCuserId());
//
//        JSONObject result = client.execute(new DrpcRequest("user", "queryUserAccount9188", user), JSONObject.class);
//        if(result.containsKey("code")){
//            XmlUtils.writeXml(result.getString("code"),result.getString("desc"),response);
//        }else{
//            Document dom = DocumentHelper.createDocument();
//            Element resp = new DOMElement("Resp");
//            dom.setRootElement(resp);
//            resp.addAttribute("code", "1");
//            resp.addAttribute("desc", "查询成功");
//
//            resp.add(XmlUtils.jsonParseXml(result, "info"));
//
//            XmlUtils.writeXml(dom.asXML(), response);
//        }
//    }
//
//    /**
//     * 转换用户对象
//     *
//     * @param user
//     * @param userInfoRspUser
//     */
//    private void transformUser(com.caiyi.user.domain.base.User userInfoRspUser, User user) {
//        userInfoRspUser.getRealName();//真是姓名
//        userInfoRspUser.getAgentId();
//
//        user.setMobileNo(userInfoRspUser.getMobileNo());//手机号
//        user.setIpAddr(userInfoRspUser.getRegIp());//注册ip
//        user.setImobbind(userInfoRspUser.getMobBind());//绑定手机号
//        user.setCuserId(userInfoRspUser.getUserId());//cuserId
//        user.setSource(userInfoRspUser.getSource());//渠道值
//
//        user.setRealname(userInfoRspUser.getRealName());//真实姓名
//        user.setIdcard(userInfoRspUser.getIdCard());//身份证号
//        user.setCusername(userInfoRspUser.getNickId());//9188用户名
//    }
//}
