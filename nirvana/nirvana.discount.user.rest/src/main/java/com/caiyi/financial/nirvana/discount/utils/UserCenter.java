//package com.caiyi.financial.nirvana.discount.utils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.caiyi.financial.nirvana.core.bean.BaseBean;
//import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
//import com.caiyi.financial.nirvana.core.client.IDrpcClient;
//import com.caiyi.financial.nirvana.core.constant.Constant;
//import com.caiyi.financial.nirvana.discount.user.bean.User;
//import com.caiyi.user.api.UserInterface;
//import com.caiyi.user.domain.base.BaseRsp;
//import com.caiyi.user.domain.base.UserClientInfo;
//import com.caiyi.user.domain.enums.UserCodeDict;
//import com.caiyi.user.domain.request.LoginInfoReq;
//import com.caiyi.user.domain.request.UserRegisterReq;
//import com.caiyi.user.domain.response.SimpleRsp;
//import com.caiyi.user.domain.response.UserInfoRsp;
//import com.caiyi.user.domain.response.UserLoginRsp;
//import com.caiyi.user.domain.response.UserRegisterRsp;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.annotation.Resource;
//
//import static com.caiyi.financial.nirvana.discount.utils.WebUtil.transformSource;
//
///**
// * Created by liuweiguo on 2016/8/23.
// */
////@MVCComponent
//public class UserCenter {
//    private static Logger logger = LoggerFactory.getLogger(UserCenter.class);
//
//    public static String HSK_REG = "0"; // 惠刷卡注册发送验证码
//    public static String HSK_FORGET_PWD = "1"; // 惠刷卡忘记密码发送验证码
//    public static String DK_YZM = "4"; // 有鱼贷款注册/快速登录发送验证码
//    public static String DK_PWD = "5"; // 有鱼贷款注册成功发送密码
//
//    /**
//     * 用户中心正常请求返回code
//     */
//    public static final String SUCCESSCODE = "1";
//
//    /**
//     * 发送短信验证码提供者
//     */
//    public static final SmsProvider smsProvider = SmsProvider.hsk;
//
//    @Resource(name = Constant.HSK_USER)
//    IDrpcClient client;
//
//    /**
//     * 服务异常后，接口不报错，返回此code和desc
//     */
//    public static final String OUTER_ERROR_CODE = "500";
//    public static final String OUTER_ERROR_DESC = "请求失败，请稍后再试";
//
//    @Autowired
//    private UserInterface userService;
//
//    /**
//     * 发送短信验证码
//     * user.getMobileNo()
//     * bean.getYzm()
//     * bean.getYzmType()
//     * bean.getIpAddr()
//     * @param user
//     * @return
//     */
//    public SimpleRsp sendSms(User user) {
//        logger.info("手机号：【" + user.getMobileNo() + "】，验证码：【" + user.getYzm() + "】，IP地址：【" + user.getIpAddr()+ "】");
//        if (SmsProvider.hsk.equals(smsProvider)) {
//            //惠刷卡发送验证码
//            JSONObject result = client.execute(new DrpcRequest("user", "sendSms", user), JSONObject.class);
//            SimpleRsp simpleRsp = new SimpleRsp();
//            simpleRsp.setResponseCode(result.getString("code"));
//            simpleRsp.setResponseMessage(result.getString("desc"));
//            return simpleRsp;
//        } else if (SmsProvider.center.equals(smsProvider)) {
//            //中信发送验证码
//            return sendMsg_center(user.getMobileNo());
//        }
//        return null;
//    }
//
//    /**
//     * 检验手机验证码
//     * mobileNo
//     * yzm
//     * user.getYzmType()
//     * @param user
//     * @return
//     */
//    public SimpleRsp checkSms(User user) {
//        if (SmsProvider.hsk.equals(smsProvider)) {
//            //惠刷卡校验验证码
//            JSONObject result = client.execute(new DrpcRequest("user", "checkSms", user), JSONObject.class);
//            SimpleRsp simpleRsp = new SimpleRsp();
//            simpleRsp.setResponseCode(result.getString("code"));
//            simpleRsp.setResponseMessage(result.getString("desc"));
//            return simpleRsp;
//        } else if (SmsProvider.center.equals(smsProvider)) {
//            //中心校验验证码
//            return checkSmsCheckNum(user.getMobileNo(), user.getYzm());
//        }
//        return null;
//    }
//
//    /**
//     * 发送短信验证码
//     * @param mobileNo
//     * @return
//     */
//    private SimpleRsp sendMsg_center(String mobileNo) {
//        try {
//            SimpleRsp simpleRsp = userService.sendMsg(mobileNo);
//            printLog("sendMsg", simpleRsp);
//            return simpleRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("sendMsg",e);
//            return getErrorSimpleRsp();
//        }
//    }
//
//    /**
//     * 检验手机验证码
//     * @param mobileNo
//     * @param yzm
//     * @return
//     */
//    private SimpleRsp checkSmsCheckNum(String mobileNo, String yzm) {
//        try {
//            SimpleRsp simpleRsp = userService.checkSmsCheckNum(mobileNo, yzm);
//            printLog("checkSmsCheckNum", simpleRsp);
//            return simpleRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("checkSmsCheckNum", e);
//            return getErrorSimpleRsp();
//        }
//    }
//
//    /**
//     * 查询当前手机号是否可被注册，如果手机号已开通手机号登录，或已绑定都多账号则不能再被注册
//     * @param bean mobileno手机号注册时输入的新手机号
//     * @return result=true:可以注册，false:已经注册过，不能注册
//     */
//    public SimpleRsp isMobilenoRegister(BaseBean bean) {
//        try {
//            SimpleRsp simpleRsp = userService.isMobilenoRegister(bean.getMobileNo(), getUserClientInfo(bean));
//            printLog("isMobilenoRegister", simpleRsp);
//            return simpleRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("isMobilenoRegister", e);
//            return getErrorSimpleRsp();
//        }
//    }
//
//    /**
//     * 根据手机号获取用户名
//     * @param bean
//     * @return
//     */
//    public String getNickIdByMobileno(BaseBean bean){
//        try {
//            SimpleRsp simpleRsp = userService.getNickIdByMobileno(bean.getMobileNo(), getUserClientInfo(bean));
//            printLog("getNickIdByMobileno", simpleRsp);
//            return simpleRsp.getResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("getNickIdByMobileno", e);
//            return "";
//        }
//    }
//
//    /**
//     * 新用户注册
//     * @param bean
//     * @return
//     */
//    public UserRegisterRsp register(BaseBean bean) {
//        try {
//            //随机生成用户名
//            SimpleRsp simpleRsp = userService.randomUsername(getUserClientInfo(bean));
//            String userName = simpleRsp.getResult();
//
//            //新用户注册
//            UserRegisterReq userRegisterReq = new UserRegisterReq();
//            userRegisterReq.setNickId(userName);
//            userRegisterReq.setMobileNo(bean.getUid());
//            userRegisterReq.setPassword(bean.getPwd());//明文密码
//            userRegisterReq.setAgentId("");//可为空
//
//            UserRegisterRsp registerRsp = userService.register(userRegisterReq, getUserClientInfo(bean));
//            printLog("register",registerRsp);
//            return registerRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("register", e);
//            return getErrorUserRegisterRsp();
//        }
//    }
//
//    /**
//     * 快速登录
//     * @param user
//     * @return
//     */
//    public UserLoginRsp registerAndLogin(User user){
//        try {
//            UserLoginRsp loginRsp = userService.registerAndLogin(user.getUid(), user.getYzm(), getUserClientInfo(user));
//            printLog("registerAndLogin", loginRsp);
//            return loginRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("registerAndLogin", e);
//            return getErrorUserLoginRsp();
//        }
//    }
//
//    /**
//     * 用户登录
//     * @param bean
//     * @return
//     */
//    public UserLoginRsp login(BaseBean bean) {
//        try {
//            LoginInfoReq req = new LoginInfoReq();
//            req.setIdentification(bean.getUid());//用户名
//            req.setHskPwd(bean.getPwd());//惠刷卡签名的密码
//            req.setPassword(bean.getPwd9188());//9188签名的密码
//
//            UserLoginRsp userLoginRsp = userService.login(req, getUserClientInfo(bean));
//            printLog("login", userLoginRsp);
//            return userLoginRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("login", e);
//            return getErrorUserLoginRsp();
//        }
//    }
//
//    /**
//     * 使用令牌获取用户基本信息
//     * @param bean
//     * @return
//     */
//    public UserInfoRsp getUserByToken(BaseBean bean) {
//        try {
//            UserInfoRsp userInfo = userService.getUserByToken(bean.getAccessToken(), getUserClientInfo(bean));
//            printLog("getUserByToken",userInfo);
//            return userInfo;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("getUserByToken", e);
//            return getErrorUserInfoRsp();
//        }
//    }
//
//    /**
//     * 修改登录密码
//     * @param bean
//     * @return
//     */
//    public SimpleRsp changeLoginPassword(User bean) {
//        try {
//            SimpleRsp simpleRsp = userService.changeLoginPassword(bean.getCusername(), bean.getOldPwd(), bean.getNewPwd(), bean.getAccessToken(), getUserClientInfo(bean));
//            printLog("changeLoginPassword", simpleRsp);
//            return simpleRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("changeLoginPassword", e);
//            return getErrorSimpleRsp();
//        }
//    }
//
//    /**
//     * 忘记密码-修改密码
//     * @param bean
//     * @return
//     */
//    public SimpleRsp resetPwd(BaseBean bean) {
//        try {
//            String identification = getNickIdByMobileno(bean);
//            SimpleRsp simpleRsp = userService.resetPwd(identification, bean.getPwd(), getUserClientInfo(bean));
//            printLog("resetPwd", simpleRsp);
//            return simpleRsp;
//        } catch (Exception e) {
//            e.printStackTrace();
//            printException("resetPwd", e);
//            return getErrorSimpleRsp();
//        }
//    }
//
//    /**
//     * 生成调用9188用户中心参数
//     * @param user
//     * @return
//     */
//    public UserClientInfo getUserClientInfo(BaseBean user) {
//        if (null == user.getIclient()) {
//            user.setIclient(0);
//        }
//        if (null == user.getSource()) {
//            user.setSource(5000);
//        }
//        UserClientInfo clientInfo = new UserClientInfo();
//        clientInfo.setIpAddr(user.getIpAddr());
//        clientInfo.setMtype(user.getIclient()+1);//手机类型 0主站,1安卓,2iOS,3Windows Phone,4触屏
//        clientInfo.setSource(transformSource(user.getSource()));
//        clientInfo.setImei(user.getImei());
//        clientInfo.setAppVersion(user.getAppVersion());
//        return clientInfo;
//    }
//
//    private void printLog(String interfaceName, Object object) {
//        if (object instanceof SimpleRsp){
//            SimpleRsp simpleRsp = (SimpleRsp) object;
//            codeAdapter(simpleRsp);
//            printSimpleRsp(interfaceName, simpleRsp);
//        }else {
//            BaseRsp rsp = (BaseRsp) object;
//            codeAdapter(rsp);
//            logger.info("调用用户中心接口【" + interfaceName + "】，返回code=" + rsp.getResponseCode() + ",desc=" + rsp.getResponseMessage());
//        }
//    }
//
//    private void printSimpleRsp(String interfaceName, SimpleRsp simpleRsp){
//        UserCodeDict dict = UserCodeDict.getByCode(simpleRsp.getResult());
//        if (null != dict) {
//            logger.info("调用用户中心接口【{}】，返回code={},desc={},result={},resultDesc={}", interfaceName, simpleRsp.getResponseCode(), simpleRsp.getResponseMessage(), simpleRsp.getResult(), dict.getDesc());
//        } else {
//            logger.info("调用用户中心接口【{}】，返回code={},desc={},result={}", interfaceName, simpleRsp.getResponseCode(), simpleRsp.getResponseMessage(), simpleRsp.getResult());
//        }
//    }
//
//    private void printException(String interfaceName, Throwable e){
//        logger.error("调用用户中心接口【{}】出现异常:{},", interfaceName, e.getMessage(), e);
//    }
//
//    /**
//     * 将用户中心code转换一下
//     * @param baseRsp
//     */
//    private static void codeAdapter(BaseRsp baseRsp) {
//        if (null != baseRsp) {
//            if ("0".equals(baseRsp.getResponseCode())) {//调用成功
//                baseRsp.setResponseCode(SUCCESSCODE);
//            }else  if ("1".equals(baseRsp.getResponseCode())) {//调用失败
//                baseRsp.setResponseCode("0");
//            }
//        }
//    }
//
//    public SimpleRsp getErrorSimpleRsp() {
//        SimpleRsp simpleRsp = new SimpleRsp();
//        simpleRsp.setResponseCode(OUTER_ERROR_CODE);
//        simpleRsp.setResponseMessage(OUTER_ERROR_DESC);
//        return simpleRsp;
//    }
//
//    public UserInfoRsp getErrorUserInfoRsp(){
//        UserInfoRsp userInfoRsp = new UserInfoRsp();
//        userInfoRsp.setResponseCode(OUTER_ERROR_CODE);
//        userInfoRsp.setResponseMessage(OUTER_ERROR_DESC);
//        return userInfoRsp;
//    }
//
//    public UserLoginRsp getErrorUserLoginRsp(){
//        UserLoginRsp userLoginRsp = new UserLoginRsp();
//        userLoginRsp.setResponseCode(OUTER_ERROR_CODE);
//        userLoginRsp.setResponseMessage(OUTER_ERROR_DESC);
//        return userLoginRsp;
//    }
//
//    public UserRegisterRsp getErrorUserRegisterRsp(){
//        UserRegisterRsp userRegisterRsp = new UserRegisterRsp();
//        userRegisterRsp.setResponseCode(OUTER_ERROR_CODE);
//        userRegisterRsp.setResponseMessage(OUTER_ERROR_DESC);
//        return userRegisterRsp;
//    }
//
//    public static void main(String[] args) {
//
//    }
//}
