package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.user.bean.FeedBackBean;
import com.caiyi.financial.nirvana.discount.user.bean.HskUser;
import com.caiyi.financial.nirvana.discount.user.bean.HskUserBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import com.caiyi.financial.nirvana.discount.utils.*;
import com.util.string.StringUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by lizhijie on 2017/2/9.
 * 惠刷卡用户相关接口
 */
@RestController
public class RestHskUserController {
    private static Logger logger = LoggerFactory.getLogger(RestHskUserController.class);

    @Resource(name = Constant.HSK_USER)
    private IDrpcClient client;

    //private static String baseUrl = "http://192.168.1.51:10021/";
    private static String baseUrl = SystemConfig.get("user_http_url");
    private final static String MD5_SMS_KEY = "6a37207e0fcd92";
    public static final String ENCODING = "UTF-8";
    //图片路径
    private static String PIC_PATH = "imgs/user_icon/";
    //根路径
    public static final String PATH = "/opt/export/www/";
    //服务器地址
    private static final String SEVER_ADDRESS = "http://web.img.huishuaka.com";
    public static Map<String, String> loginhezuo = new HashMap<String, String>();
    //惠刷卡编号和9188编号映射
    public static Map<String, String> merchantacctidMap = new HashMap<String, String>();
    //需要直接生成Token的用户列表(不使用短信验证码生成Token，防止短信验证码次数限制)
    public static Map<String, String> needTokenUserList = new HashMap<>();

    static {
        loginhezuo.put("130313001", "A9FK25RHT487ULMI");//Android
        loginhezuo.put("130313002", "A9FK25RHT487ULMI");//IOS

        merchantacctidMap.put("130313001", "130313002");
        merchantacctidMap.put("130313002", "130313003");
        baseUrl = SystemConfig.get("user_http_url");

        //添加需要直接生成Token的用户列表
        needTokenUserList.put("13641791729", "5d084252138109"); //钱飞
        needTokenUserList.put("15200000002", "cc4a0768139"); //IOS审核使用
        needTokenUserList.put("15209884435", "8e784db71214"); //辉杨
        needTokenUserList.put("15900544531", "a93152d2-f099-4af0-8f0c-4bd05392ee2f");
        needTokenUserList.put("15921119316", "e40ac911-96f0-446b-a44a-ba0a3e706bce"); //小慧
        needTokenUserList.put("18221395112", "a2492064243"); //小虎
        needTokenUserList.put("18336070473", "0a088328141050"); //占飞
        needTokenUserList.put("18351582086", "7cd0b024-03b1-40d5-93b0-ade26308448b"); //安琪
        needTokenUserList.put("18762321433", "5f0f01bd7a194a8b86bed41f8021abcd"); //朱国兵
        needTokenUserList.put("18767150596", "3a6feac3-c5ea-4eda-bf46-ee010ed7ebb5"); //夏航军
    }

    /**
     * 账号注册检查
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/user/registerchk.go")
    public void registerchk9188(User bean, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject;
        bean.setIpAddr(WebUtil.getRealIp(request));
        String mobileno = bean.getUid();
        bean.setMobileNo(mobileno);
        logger.info("请求发送验证码,mobileNo:{},ipAddr:{}", mobileno, bean.getIpAddr());
        //如果没能取到手机号码,提示用户重新尝试
        if (StringUtil.isEmpty(mobileno) || !CheckUtil.isMobilephone(mobileno)) {
            XmlUtils.writeXml(HskUserConstants.ERROR_FORMAT, "手机号码格式错误", response);
            return;
        }
        if (CheckUtil.isNullString(bean.getTimeStamp()) || CheckUtil.isNullString(bean.getKey())) {
            XmlUtils.writeXml(HskUserConstants.ERROR_PARAM, "版本过低，请升级客户端版本!", response);
            return;
        }
        /**
         * 惠刷卡校验是否允许注册
         * 非快登情况
         */
        HashMap<String, String> params = new HashMap<>();
        String checkIsExistUrl = baseUrl + "checkExistCphone";
        logger.info("9188检查手机号是否允许注册地址:checkIsExistUrl:{}", checkIsExistUrl);
        params.put("cphone", mobileno);
        String checkIsExist = HttpClientUtil.callHttpPost_Map(checkIsExistUrl, params);
        jsonObject = JSON.parseObject(checkIsExist);
        logger.info("checkIsExist:" + checkIsExist);
        if (jsonObject != null) {
            if ("1".equals(jsonObject.getString("code"))) {
                if ("true".equals(jsonObject.getString("data"))) {
                    logger.info("用户【" + bean.getUid() + "】已存在");
                    // update by lcs 20170330
                    if (!"quickLogin".equals(bean.getActionName()) && !"dk".equals(bean.getComeFrom())) {
                        XmlUtils.writeXml(HskUserConstants.ERROR_PARAM, "该手机号已存在,可以直接登录啦", response);
                        return;
                    }
                } else {
                    logger.info("用户【" + bean.getUid() + "】可以注册");
                }
            } else {
                XmlUtils.writeXml(HskUserConstants.EXCEPTION, "程序异常", response);
                return;
            }
        } else {
            XmlUtils.writeXml(HskUserConstants.EXCEPTION, "程序异常", response);
            return;
        }
        if (bean.getIclient() != 1 || bean.getIclient() != 2) {
            bean.setIclient(3);
        }
        //发送短信
        if ("dk".equals(bean.getComeFrom())) {
            bean.setYzmType(User.DK_YZM);
        } else {
            bean.setYzmType(User.YZM_TYPE);
        }
        String sendSmsResult = sendSms(bean);
        logger.info("sendSmsResult:{}", sendSmsResult);
        jsonObject = JSON.parseObject(sendSmsResult);
        if (jsonObject != null) {
            logger.info("手机号：【" + bean.getMobileNo() + "】，IP地址：【" + bean.getIpAddr() + "】");
            if ("1".equals(jsonObject.getString("code"))) {
                XmlUtils.writeXml(jsonObject.getString("code"), jsonObject.getString("desc"), response);
            } else {
                XmlUtils.writeXml(HskUserConstants.ERROR_PARAM_DESC, jsonObject.getString("desc"), response);
            }
        } else {
            XmlUtils.writeXml(-1, "程序异常", response);
        }
    }

    /**
     * 发送短信
     *
     * @param bean
     * @return
     */
    public String sendSms(User bean) {
        HashMap<String, String> params = new HashMap<>();
        String sendSmsUrl = baseUrl + "sendSms";
        logger.info("9188发送短信验证码地址:sendSmsUrl:{}", sendSmsUrl);
        if (StringUtils.isNotEmpty(bean.getUid())) {
            params.put("cphone", bean.getUid());
        }
        if (StringUtils.isNotEmpty(bean.getMobileNo())) {
            params.put("cphone", bean.getMobileNo());
        }
        params.put("yzmType", bean.getYzmType());
        params.put("csource", String.valueOf(bean.getSource()));
        params.put("ipAddr", bean.getIpAddr());
        params.put("mobileType", String.valueOf(bean.getIclient()));
        params.put("channelType", "0");
        String result = HttpClientUtil.callHttpPost_Map(sendSmsUrl, params);
        logger.info("短信发送结果:" + result);
        return result;
    }

    /**
     * 用户注册
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/user/register.go")
    public void register9188(User bean, HttpServletRequest request, HttpServletResponse response) {
        /**
         * 1 校验参数
         * 2 检查验证码
         * 3 快登情况
         * 4 调用9188注册
         * 5 检测是否快登注册，快登注册，发送密码
         * 6 保存用户信息到惠刷卡数据库
         */
        bean.setIpAddr(WebUtil.getRealIp(request));
        //设置手机号
        String mobileNo = bean.getUid();
        bean.setMobileNo(mobileNo);
        if (StringUtil.isEmpty(bean.getPwd())) {
            //生成随机密码
            String pwd = CheckUtil.randomNum();
            bean.setPwd(pwd);
            bean.setPwd9188(pwd);
        }
        //设置手机号绑定标识
        bean.setImobbind(1);
        BoltResult result = null;
        if (!CheckUtil.isMobilephone(mobileNo)) {
            result = new BoltResult("1000", "手机号码格式错误");
        } else if (CheckUtil.isNullString(bean.getYzm())) {
            result = new BoltResult("1000", "验证码不能为空");
        }
        if (result != null) {
            XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
            return;
        }

        // 验证 验证码
        if ("dk".equals(bean.getComeFrom())) {
            bean.setYzmType(User.DK_YZM);
        } else {
            bean.setYzmType(User.YZM_TYPE);
        }
        JSONObject jsonObject;
        String appId;
        String accessToken;
        //贷款 登录
        if ("dk".equals(bean.getComeFrom()) || "quickLogin".equals(bean.getActionName())) {
            String quickLoginResult = quickLogin(bean);
            logger.info("quickLoginResult:{}", quickLoginResult);
            jsonObject = JSON.parseObject(quickLoginResult);
            if (jsonObject != null && "1".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                bean.setCuserId(data.getString("cuserId"));
                appId = data.getString("appId");
                accessToken = data.getString("accessToken");
                try {
                    bean.setCreateTime(data.getDate("createTime"));
                } catch (Exception e) {
                    logger.error("快速登录返回的时间格式 错误", e);
                }
            } else {
                XmlUtils.writeXml(-1, "快速登录失败", response);
                return;
            }
        } else {
            HskUser user = userTransforHskUser(bean);
            String registerResult = null;
            try {
                registerResult = this.register(user);
                logger.info("registerResult:{}", registerResult);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (registerResult == null) {
                XmlUtils.writeXml(HskUserConstants.EXCEPTION, HskUserConstants.ERROR_PARAM_DESC, response);
                return;
            } else {
                JSONObject data = JSON.parseObject(registerResult);
                if (data != null && "1".equals(data.getString("code"))) {
                    JSONObject content = data.getJSONObject("data");
                    appId = content.getString("appId");
                    accessToken = content.getString("accessToken");
                    bean.setCuserId(content.getString("cuserId"));
                    bean.setAccessToken(accessToken);
                    bean.setAppId(appId);
                    if (StringUtils.isNotEmpty(content.getString("cnickId"))) {
                        bean.setCnickname(content.getString("cnickId"));
                    } else {
                        bean.setCnickname("HSK" + mobileNo);
                    }
                } else {
                    XmlUtils.writeXml(data.getString("code"), data.getString("desc"), response);
                    return;
                }
            }
        }
        bean.setYzmType(User.DK_PWD);
        result = client.execute(new DrpcRequest("hskUser", "saveUserInfo9188", bean), BoltResult.class);
        logger.info("code:{},desc:{}", result.getCode(), result.getDesc());

        //添加注册用户的来源记录
        if ("1".equals(result.getCode()) && "保存注册信息成功!".equals(result.getDesc())) {
            try {
                BoltResult sourceResult = client.execute(new DrpcRequest("hskUser", "saveUserSource", bean), BoltResult.class);
                if ("1".equals(sourceResult.getCode())) {
                    logger.info("注册用户来源保存成功,cuserId:{},appMgr:{}", bean.getCuserId(), bean.getAppMgr());
                } else {
                    logger.info("注册用户来源保存失败,cuserId:{},appMgr:{}", bean.getCuserId(), bean.getAppMgr());
                }
            } catch (Exception e) {
                logger.error("注册用户来源添加异常", e);
            }
        }

        Element element = new DOMElement("Resp");
        element.addAttribute("code", result.getCode());
        element.addAttribute("desc", result.getDesc());
        element.addAttribute("appId", appId);
        element.addAttribute("accessToken", accessToken);
        XmlUtils.writeXml(element, response);
    }

    /**
     * 手机短信快速登录
     *
     * @param user
     * @return
     */
    public String quickLogin(User user) {
        HashMap<String, String> params = new HashMap<>();
        String quickLoginUrl = baseUrl + "fastLogin";
        logger.info("9188短信快速登录地址:quickLoginUrl:{}", quickLoginUrl);
        params.put("cphone", user.getUid());
        params.put("yzm", user.getYzm());
        params.put("yzmType", user.getYzmType());
        params.put("csource", String.valueOf(user.getSource()));
        params.put("ipAddr", user.getIpAddr());
        params.put("cloginfrom", HskUser.COME_FROM);
        String mobileType;
        if ("1".equals(user.getIclient())) {
            mobileType = "2";
        } else {
            mobileType = "1";
        }
        params.put("mobileType", mobileType);
        params.put("packageName", user.getPackagename());
        String quickLoginResult = HttpClientUtil.callHttpPost_Map(quickLoginUrl, params);
        logger.info("短信快速登录结果:" + quickLoginResult);
        return quickLoginResult;
    }

    /**
     * 手机短信快速登录(针对新版公共参数的HskUserBean)
     *
     * @param user
     * @return
     */
    public String quickLoginNew(HskUserBean user) {
        HashMap<String, String> params = new HashMap<>();
        String quickLoginUrl = baseUrl + "fastLogin";
        logger.info("9188短信快速登录地址:quickLoginUrl:{}", quickLoginUrl);
        params.put("cphone", user.getMobileNo());
        params.put("yzm", user.getCaptcha());
        params.put("yzmType", user.getCaptchaType());
        params.put("csource", user.getSource());
        params.put("ipAddr", user.getIpAddr());
        params.put("cloginfrom", HskUser.COME_FROM);
        params.put("mobileType", this.getMobileType(user.getDevType()));
        params.put("packageName", user.getAppPkgName());
        String quickLoginResult = HttpClientUtil.callHttpPost_Map(quickLoginUrl, params);
        logger.info("短信快速登录结果:" + quickLoginResult);
        return quickLoginResult;
    }

    /**
     * 获取设备类型 android:1 ios:2 其它:3
     *
     * @return
     */
    private String getMobileType(String devType) {
        String mobileType;
        if ("android".equals(devType)) {
            mobileType = "1";
        } else if ("ios".equals(devType)) {
            mobileType = "2";
        } else {
            mobileType = "3";
        }
        return mobileType;
    }

    /**
     * 旧user  转成 HskUser
     *
     * @param user
     * @return hskUser
     */
    public HskUser userTransforHskUser(User user) {
        HskUser hskUser = new HskUser();
        if (user == null)
            return null;
        if (StringUtils.isNotEmpty(user.getUid())) {
            hskUser.setCphone(user.getUid());
        }
        if (StringUtils.isNotEmpty(user.getMobileNo())) {
            hskUser.setCphone(user.getMobileNo());
        }
        hskUser.setYzm(user.getYzm());
        hskUser.setYzmType(user.getYzmType());
        hskUser.setMobileType(user.getIclient() + 1);
        hskUser.setCsource(user.getSource());
        hskUser.setIpAddr(user.getIpAddr());
        hskUser.setCuserId(user.getCuserId());
        hskUser.setIloginfrom(0);
        hskUser.setOldPassword(user.getOldPwd());
        hskUser.setNewPassword(user.getNewPwd());
        hskUser.setCpassword(user.getPwd());
        hskUser.setCpassword9188(user.getPwd9188());
        hskUser.setPackagename(user.getPackagename());
        hskUser.setChannelType(0);
        return hskUser;
    }

    /**
     * 9188用户注册
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public String register(HskUser bean) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        String sendSmsUrl = baseUrl + "register";
        logger.info("9188用户注册地址:userRegister:{}", sendSmsUrl);
        params.put("cphone", bean.getCphone());
        params.put("yzm", bean.getYzm());
        params.put("yzmType", bean.getYzmType());
        params.put("csource", String.valueOf(bean.getCsource()));
        params.put("ipAddr", bean.getIpAddr());
        params.put("cbelongValue", HskUser.COME_FROM);
        params.put("mobileType", String.valueOf(bean.getMobileType()));
        params.put("cpassword", MD5Util.compute(bean.getCpassword() + HskUser.MD5_KEY_9188));
        params.put("packageName", bean.getPackagename());
        String result = HttpClientUtil.callHttpPost_Map(sendSmsUrl, params);
        logger.info("注册结果:" + result);
        return result;
    }

    /**
     * 用户登录
     *
     * @param bean
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/user/login.go")
    public void login(User bean, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        bean.setMobileNo(bean.getUid());
        bean.setIpAddr(WebUtil.getRealIp(request));
        if (StringUtil.isEmpty(bean.getPwd())) {
            //生成随机密码
            String pwd = CheckUtil.randomNum();
            bean.setPwd(pwd);
        }
        BoltResult result = null;
        if (!CheckUtil.isMobilephone(bean.getUid())) {
            logger.info("手机号码格式错误");
            result = new BoltResult(String.valueOf(HskUserConstants.ERROR_FORMAT), "手机号码格式错误");
        }
        if (result != null) {
            XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
            return;
        }

        JSONObject jsonObject;
        //贷款 登录
        if ("dk".equals(bean.getComeFrom())) {
            String quickLoginResult = quickLogin(bean);
            jsonObject = JSON.parseObject(quickLoginResult);
            if (jsonObject != null && "1".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                bean.setCuserId(data.getString("cuserId"));
                try {
                    bean.setCreateTime(data.getDate("createTime"));
                } catch (Exception e) {
                    logger.error("快速登录返回的时间格式 错误", e);
                }
            } else {
                XmlUtils.writeXml(-1, "快速登录失败", response);
                return;
            }
        } else {
            String merchantacctid = bean.getMerchantacctId();
            String signtype = bean.getSignType();
            String signmsg = bean.getSignMsg();
            String userNickName = bean.getUid();

            //昵称登录的情况
            if ("130313002".equals(bean.getMerchantacctId())) {//ios需要转码
                userNickName = URLDecoder.decode(userNickName, "utf-8");
            } else {
                userNickName = new String(userNickName.getBytes("UTF-8"), ENCODING);
            }
            bean.setUid(userNickName);
            if (signtype.equals("1")) {//MD5
                if (loginhezuo.containsKey(merchantacctid)) {
                    // 生成加密签名串
                    // 请务必按照如下顺序和规则组成加密串！
                    String signMsgVal = "";
                    signMsgVal = WebUtil.appendParam(signMsgVal, "signType", signtype);
                    signMsgVal = WebUtil.appendParam(signMsgVal, "merchantacctId", merchantacctid);
                    signMsgVal = WebUtil.appendParam(signMsgVal, "uid", bean.getUid());
                    signMsgVal = WebUtil.appendParam(signMsgVal, "pwd", bean.getPwd());
                    signMsgVal = WebUtil.appendParam(signMsgVal, "key", loginhezuo.get(merchantacctid));
                    logger.info("惠刷卡--signMsgVal=" + signMsgVal);
                    String serverSignMsg = BankUtil.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();
                    logger.info("惠刷卡--serverSignMsg=" + serverSignMsg);

                    if (serverSignMsg.equals(signmsg)) {//验签成功
                        logger.info("用户【" + userNickName + "】,惠刷卡验签成功,bean pwd:" + bean.getPwd() + ",bean getPwd9188:" + bean
                                .getPwd9188());
                        HskUser user = userTransforHskUser(bean);
                        //请求9188用户登录
                        String loginResult = this.login(user);
                        if (loginResult == null) {
                            XmlUtils.writeXml(HskUserConstants.EXCEPTION, HskUserConstants.ERROR_PARAM_DESC, response);
                            return;
                        } else {
                            Element element = new DOMElement("Resp");
                            JSONObject jsonObj = JSON.parseObject(loginResult);
                            JSONObject data = jsonObj.getJSONObject("data");
                            if (jsonObj != null && "1".equals(jsonObj.getString("code"))) {
                                element.addAttribute("code", "1");
                                element.addAttribute("desc", "登录成功");
                                element.addAttribute("appId", data.getString("appId"));
                                element.addAttribute("accessToken", data.getString("accessToken"));
                                bean.setCuserId(data.getString("cuserId"));
                                bean.setCnickname(data.getString("cnickId"));
                                client.execute(new DrpcRequest("hskUser", "saveUserInfo9188", bean));
                            } else {
                                element.addAttribute("code", "0");
                                element.addAttribute("desc", jsonObj.getString("desc"));
                            }
                            XmlUtils.writeXml(element, response);
                            return;
                        }
                    } else {
                        logger.info("签名失效：");
                        logger.info("服务端签名结果为：" + serverSignMsg + "");
                        logger.info("客户端签名结果为：" + signmsg + "");
                        logger.info("signtype=" + signtype + "");
                        logger.info("merchantacctid=" + merchantacctid + "");
                        logger.info("uid=" + bean.getUid() + "");
                        logger.info("pwd=" + bean.getPwd() + "");
                        XmlUtils.writeXml("1001", "签名失败", response);
                        return;
                    }
                } else {
                    logger.info("错误的 merchantacctid=" + merchantacctid);
                }
            } else {
                logger.info("错误的signtype=" + signtype);
            }
        }
    }

    /**
     * 9188用户登录
     *
     * @param bean
     * @return
     */
    public String login(HskUser bean) {
        HashMap<String, String> params = new HashMap<>();
        String loginUrl = baseUrl + "login";
        logger.info("9188用户登录地址:loginUrl:{}", loginUrl);
        params.put("cuserId", bean.getCphone());
        params.put("cpassword", bean.getCpassword9188());
        params.put("csource", String.valueOf(bean.getCsource()));
        params.put("cloginfrom", HskUser.COME_FROM);
        params.put("ipAddr", bean.getIpAddr());
        params.put("mobileType", String.valueOf(bean.getMobileType()));
        params.put("packageName", bean.getPackagename());
        String result = HttpClientUtil.callHttpPost_Map(loginUrl, params);
        logger.info("请求9188用户登录结果:" + result);
        return result;
    }

    @RequestMapping("/user/mobileBinding.go")
    public void bandingPhone(User bean, HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 登录状态修改密码
     *
     * @param bean
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/user/modifyUserInfo.go")
    public void modifyPassword(User bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Element element = new DOMElement("Resp");
        if (StringUtil.isEmpty(bean.getOldPwd())) {
            element.addAttribute("code", "0");
            element.addAttribute("desc", "旧密码不能为空");
        }
        if (StringUtil.isEmpty(bean.getNewPwd())) {
            element.addAttribute("code", "0");
            element.addAttribute("desc", "新密码不能为空");
        }
        if (StringUtil.isEmpty(bean.getModifFlag())) {
            element.addAttribute("code", "0");
            element.addAttribute("desc", "参数错误");
        }
        if (StringUtil.isEmpty(bean.getOldPwd()) || StringUtil.isEmpty(bean.getNewPwd()) ||
                StringUtil.isEmpty(bean.getOldPwd())) {
            XmlUtils.writeXml(element, response);
            return;
        }
        //设置请求IP地址
        bean.setIpAddr(WebUtil.getRealIp(request));
        HskUser user = userTransforHskUser(bean);
        if (user != null) {
            String motifiedPassResult = motifyPassword(user);
            JSONObject data = JSON.parseObject(motifiedPassResult);
            if (data != null) {
                String code = data.getString("code");
                if ("500".equals(code)) {
                    element.addAttribute("code", "1002");
                } else {
                    element.addAttribute("code", code);
                }
                element.addAttribute("desc", data.getString("desc"));
            } else {
                element.addAttribute("code", "0");
                element.addAttribute("desc", "参数错误");
            }
        } else {
            element.addAttribute("code", "0");
            element.addAttribute("desc", "参数错误");
        }
        XmlUtils.writeXml(element, response);
    }

    /**
     * 9188登录状态修改密码
     *
     * @param bean
     * @return
     */
    private String motifyPassword(HskUser bean) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        String loginUrl = baseUrl + "updatePwd";
        logger.info("9188登录状态修改密码地址:updatePwdUrl:{}", loginUrl);
        params.put("cuserId", bean.getCuserId());
        params.put("cpassword", (MD5Util.compute(bean.getNewPassword() + HskUser.MD5_KEY_9188)).toLowerCase());
        params.put("oldPassword", (MD5Util.compute(bean.getOldPassword() + HskUser.MD5_KEY_9188)).toLowerCase());
        params.put("csource", String.valueOf(bean.getCsource()));
        params.put("cloginfrom", HskUser.COME_FROM);
        params.put("ipAddr", bean.getIpAddr());
        params.put("mobileType", String.valueOf(bean.getMobileType()));
        params.put("packageName", bean.getPackagename());
        String result = HttpClientUtil.callHttpPost_Map(loginUrl, params);
        logger.info("修改密码结果:" + result);
        return result;
    }

    /**
     * 未登录重置密码(忘记密码)
     *
     * @param bean
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/user/resetPwdNotLogin.go")
    public void resetPwdNotLogin9188(User bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("actionName:" + bean.getActionName());
        if (CheckUtil.isNullString(bean.getMobileNo())) {
            XmlUtils.writeXml(HskUserConstants.ERROR_PARAM, "请填写手机号", response);
            return;
        }
        if (!CheckUtil.isMobilephone(bean.getMobileNo())) {
            XmlUtils.writeXml(HskUserConstants.ERROR_FORMAT, "手机号格式错误", response);
            return;
        }
        bean.setIpAddr(WebUtil.getRealIp(request));
        HskUser hskUser = userTransforHskUser(bean);
        bean.setYzmType("1");
        if ("sendYzm".equals(bean.getActionName())) {
            if (hskUser != null) {
                String sendSms = sendSms(bean);
                JSONObject jsonObject = JSON.parseObject(sendSms);
                logger.info("手机号：【" + bean.getMobileNo() + "】 发送结果：" + sendSms);
                if (jsonObject != null) {
                    if ("1".equals(jsonObject.getString("code"))) {
                        XmlUtils.writeXml(jsonObject.getString("code"), jsonObject.getString("desc"), response);
                    } else {
                        XmlUtils.writeXml(HskUserConstants.ERROR_PARAM, jsonObject.getString("desc"), response);
                    }
                    logger.info("手机号：【" + bean.getMobileNo() + "】，IP地址：【" + bean.getIpAddr() + "】");
                } else {
                    XmlUtils.writeXml(-1, "程序异常", response);
                }
            } else {
                XmlUtils.writeXml(-1, "程序异常", response);
            }
        } else if ("reSetPwd".equals(bean.getActionName())) {
            this.resetPassword(bean, request, response);
        }
    }

    /**
     * 处理重置密码操作
     *
     * @param bean
     * @param request
     * @param response
     * @throws Exception
     */
    public void resetPassword(User bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Element element = new DOMElement("Resp");
        if (StringUtil.isEmpty(bean.getNewPwd())) {
            element.addAttribute("code", "0");
            element.addAttribute("desc", "新密码不能为空");
        }
        if (StringUtil.isEmpty(bean.getYzm())) {
            element.addAttribute("code", "0");
            element.addAttribute("desc", "验证码不能为空");
        }
        if (StringUtil.isEmpty(bean.getNewPwd()) || StringUtil.isEmpty(bean.getYzm())) {
            XmlUtils.writeXml(element, response);
            return;
        }
        HskUser user = userTransforHskUser(bean);
        if (user != null) {
            //调用9188重置密码
            String result = resetPasswordHttp(user);
            JSONObject data = JSON.parseObject(result);
            if (data != null) {
                element.addAttribute("code", data.getString("code"));
                element.addAttribute("desc", data.getString("desc"));
            } else {
                element.addAttribute("code", "0");
                element.addAttribute("desc", "程序异常");
            }
        } else {
            element.addAttribute("code", "0");
            element.addAttribute("desc", "程序异常");
        }
        XmlUtils.writeXml(element, response);
    }

    /**
     * 9188重置密码
     *
     * @param bean
     * @return
     */
    private String resetPasswordHttp(HskUser bean) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        String resetUrl = baseUrl + "resetPwd";
        logger.info("9188重置密码地址:resetPwdUrl:{}", resetUrl);
        params.put("cphone", bean.getCphone());
        params.put("cpassword", (MD5Util.compute(bean.getNewPassword() + HskUser.MD5_KEY_9188)).toLowerCase());
        params.put("yzm", bean.getYzm());
        params.put("yzmType", String.valueOf(bean.getYzmType()));
        params.put("cloginfrom", HskUser.COME_FROM);
        params.put("csource", String.valueOf(bean.getCsource()));
        params.put("ipAddr", bean.getIpAddr());
        params.put("mobileType", String.valueOf(bean.getMobileType()));
        params.put("packageName", bean.getPackagename());
        String result = HttpClientUtil.callHttpPost_Map(resetUrl, params);
        logger.info("重置密码结果:" + result);
        return result;
    }

    /**
     * 查询用户账号信息
     *
     * @param user
     * @param request
     * @param response
     */
    @SetUserDataRequired
    @RequestMapping("/user/queryUserAccount.go")
    public void queryUserAccount(User user, HttpServletRequest request, HttpServletResponse response) {
        logger.info("查询用户账号信息");
        logger.info("接收参数:cuserId:{}", user.getCuserId());
        if (StringUtils.isEmpty(user.getCuserId())) {
            user.setCuserId(request.getParameter("cuserId"));
        }
        BoltResult result = client.execute(new DrpcRequest("user", "queryUserAccount9188", user), BoltResult.class);
        if (BoltResult.SUCCESS.equals(result.getCode())) {
            Element resp = new DOMElement("Resp");
            resp.addAttribute("code", "1");
            resp.addAttribute("desc", "查询成功");
            resp.add(XmlUtils.jsonParseXml((JSONObject) JSON.toJSON(result.getData()), "info"));
            XmlUtils.writeXml(resp, response);
        } else {
            XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
        }
    }

    /**
     * 短信快登 发短信
     *
     * @param bean
     * @param request
     * @return
     */
    @RequestMapping("/notcontrol/user/sendMessage.go")
    public BoltResult sendMessageByPhone(HskUserBean bean, HttpServletRequest request) {
        BoltResult boltResult = new BoltResult("1", "success");
        JSONObject jsonObject;
        bean.setSource(request.getHeader("source"));
        bean.setDevType(request.getHeader("devType"));
        bean.setIpAddr(WebUtil.getRealIp(request));
        String mobileNo = bean.getMobileNo();
        logger.info("短信快登发送短信验证码|sendMessageByPhone,mobileNo:{}", mobileNo);
        //如果没能取到手机号码,提示用户重新尝试
        if (StringUtil.isEmpty(mobileNo) || !CheckUtil.isMobilephone(mobileNo)) {
            logger.info("短信快登发送短信验证码|sendMessageByPhone:手机号码格式错误");
            boltResult.setCode(String.valueOf(HskUserConstants.ERROR_FORMAT));
            boltResult.setDesc("手机号码格式错误");
            return boltResult;
        }
        //判断正式环境还是测试环境
        logger.info("短信快登发送短信验证码|sendMessageByPhone:当前环境9188用户中心地址,baseUrl:{}", baseUrl);
        if (baseUrl.contains("account.youyuwo.com")) {
            //正式环境
            if (needTokenUserList.containsKey(mobileNo)) {
                boltResult.setCode("1");
                boltResult.setDesc("发送验证码成功");
                return boltResult;
            }
        }
        try {
            bean.setCaptchaType(HskUserBean.CAPTCHA_TYPE);
            jsonObject = JSON.parseObject(this.sendSmsNew(bean));
            if (jsonObject != null) {
                logger.info("短信快登发送短信验证码|sendMessageByPhone:请求9188用户中心结果:code:{},desc:{}", jsonObject.getString("code"), jsonObject
                        .getString("desc"));
                boltResult.setCode(jsonObject.getString("code"));
                boltResult.setDesc(jsonObject.getString("desc"));
            } else {
                logger.info("短信快登发送短信验证码|sendMessageByPhone:发送短信验证码失败");
                boltResult.setCode("0");
                boltResult.setDesc("发送短信验证码失败");
            }
        } catch (Exception e) {
            logger.info("短信快登发送短信验证码|sendMessageByPhone:发送短信验证码异常" + e.getMessage(), e);
            boltResult.setCode("0");
            boltResult.setDesc("发送短信验证码异常");
        }
        return boltResult;
    }

    /**
     * 发送短信(针对新版公共参数的HskUserBean)
     *
     * @param bean
     * @return
     */
    public String sendSmsNew(HskUserBean bean) {
        HashMap<String, String> params = new HashMap<>();
        String sendSmsUrl = baseUrl + "sendSms";
        logger.info("9188发送短信验证码请求地址:sendSmsUrl:{}", sendSmsUrl);
        if (StringUtils.isNotEmpty(bean.getMobileNo())) {
            params.put("cphone", bean.getMobileNo());
        }
        params.put("yzmType", bean.getCaptchaType());
        params.put("csource", bean.getSource());
        params.put("ipAddr", bean.getIpAddr());
        params.put("mobileType", this.getMobileType(bean.getDevType()));
        params.put("channelType", "0");
        String result = HttpClientUtil.callHttpPost_Map(sendSmsUrl, params);
        logger.info("短信发送结果:" + result);
        return result;
    }

    /**
     * 短信快登
     *
     * @param hskUserBean
     * @param request
     * @return
     */
    @RequestMapping("/notcontrol/user/quickLogin.go")
    public BoltResult quickLogin(HskUserBean hskUserBean, HttpServletRequest request) {
        BoltResult result = new BoltResult();
        JSONObject dataBack = new JSONObject();
        String appMgr = request.getHeader("appMgr");
        String hskCityId = request.getHeader("hskCityId");
        hskUserBean.setAppMgr(appMgr);
        hskUserBean.setHskCityId(hskCityId);
        hskUserBean.setSource(request.getHeader("source"));
        hskUserBean.setDevType(request.getHeader("devType"));
        hskUserBean.setAppPkgName(request.getHeader("appPkgName"));
        hskUserBean.setIpAddr(WebUtil.getRealIp(request));
        String mobileNo = hskUserBean.getMobileNo();
        logger.info("短信快登|quickLogin,mobileNo:{},ipAddr:{},appMgr:{},hskCityId:{}", mobileNo, hskUserBean.getIpAddr(), appMgr,
                hskCityId);
        //如果没能取到手机号码,提示用户重新尝试
        JSONObject jsonObject;
        if (StringUtil.isEmpty(mobileNo) || !CheckUtil.isMobilephone(mobileNo)) {
            logger.info("短信快登|quickLogin:手机号码格式错误");
            result.setCode(String.valueOf(HskUserConstants.ERROR_FORMAT));
            result.setDesc("手机号码格式错误");
            return result;
        }
        if (!StringUtils.isNumeric(hskUserBean.getCaptcha())) {
            logger.info("短信快登|quickLogin:验证码不能空或者非数字");
            result.setCode(String.valueOf(HskUserConstants.ERROR_FORMAT));
            result.setDesc("验证码不能空或者非数字");
            return result;
        }

        //判断正式环境还是测试环境
        logger.info("短信快登|quickLogin:当前环境9188用户中心地址,baseUrl:{}", baseUrl);
        if (baseUrl.contains("account.youyuwo.com")) {
            //正式环境，验证是否是开发测试账号，是则直接生成Token
            generateTokenDirect(mobileNo, hskUserBean, result);
            logger.info("短信快登|quickLogin:直接生成Token结果,code:{},desc:{}", result.getCode(), result.getDesc());
            if (result != null && "1".equals(result.getCode())) {
                return result;
            }
        }
        result = new BoltResult("1", "success");

        try {
            hskUserBean.setCaptchaType(HskUserBean.CAPTCHA_TYPE);
            //请求9188用户中心短信快登接口
            String quickLoginResult = this.quickLoginNew(hskUserBean);
            logger.info("短信快登|quickLogin:请求9188用户中心短信快登结果,quickLoginResult:{}", quickLoginResult);
            jsonObject = JSON.parseObject(quickLoginResult);
            if (jsonObject != null && "1".equals(jsonObject.getString("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                hskUserBean.setCuserId(data.getString("cuserId"));
                String appId = data.getString("appId");
                String accessToken = data.getString("accessToken");
                result.setCode(jsonObject.getString("code"));
                result.setDesc(jsonObject.getString("desc"));
                dataBack.put("appId", appId);
                dataBack.put("accessToken", accessToken);
                result.setData(dataBack);
                //组装User对象
                User user = new User();
                user.setUid(mobileNo);
                user.setMobileNo(mobileNo);
                user.setIpAddr(hskUserBean.getIpAddr());
                user.setCuserId(hskUserBean.getCuserId());
                user.setAppMgr(Integer.parseInt(appMgr));
                user.setHskcityid(hskCityId);
                user.setCidfa(hskUserBean.getCidfa());
                try {
                    BoltResult saveResult = client.execute(new DrpcRequest("hskUser", "saveUserInfo9188New", user), BoltResult
                            .class);
                    if (saveResult != null) {
                        logger.info("短信快登|quickLogin:保存注册信息结果,saveCode:{},saveDesc:{}", saveResult.getCode(), saveResult.getDesc
                                ());
                    }
                } catch (Exception e) {
                    logger.info("短信快登|quickLogin:保存注册信息异常" + e.getMessage(), e);
                }
            } else {
                logger.info("短信快登|quickLogin:快速登录失败");
                result.setCode("0");
                result.setDesc("快速登录失败");
            }
            return result;
        } catch (Exception e) {
            logger.info("短信快登|quickLogin:快速登录异常" + e.getMessage(), e);
            result.setCode("0");
            result.setDesc("快速登录异常");
        }
        return result;
    }

    /**
     * 为开发测试生成正式环境Token(不使用短信验证码生成Token，防止短信验证码次数限制)
     *
     * @param mobileNo
     * @return
     */
    public void generateTokenDirect(String mobileNo, HskUserBean hskUserBean, BoltResult result) {
        JSONObject data = new JSONObject();
        try {
            //判断是否包含在开发测试账号列表
            if (needTokenUserList.containsKey(mobileNo)) {
                //获取用户的用户id
                String cuserId = needTokenUserList.get(mobileNo);
                logger.info("短信快登|generateTokenDirect,cuserId:{}", cuserId);
                if (CheckUtil.isNullString(cuserId)) {
                    result.setCode("0");
                    result.setDesc("用户id为空");
                    return;
                }
                //请求9188用户中心创建Token
                String createTokenUrl = baseUrl + "/createToken";
                logger.info("短信快登|generateTokenDirect:9188用户中心创建Token地址,createTokenUrl:{}", createTokenUrl);
                //封装请求参数
                Map<String, String> params = new HashMap<>();
                params.put("cuserId", cuserId);
                params.put("cloginfrom", "HSK");
                params.put("csource", hskUserBean.getSource());
                params.put("ipAddr", hskUserBean.getIpAddr());
                params.put("mobileType", getMobileType(hskUserBean.getDevType()));
                params.put("packageName", hskUserBean.getAppPkgName());
                String resultStr = HttpClientUtil.callHttpPost_Map(createTokenUrl, params);
                logger.info("短信快登|generateTokenDirect:请求9188用户中心创建Token结果,resultStr:{}", resultStr);
                JSONObject resultJson = JSON.parseObject(resultStr);
                if (resultJson != null && "1".equals(resultJson.getString("code"))) {
                    JSONObject feedbackData = resultJson.getJSONObject("data");
                    String accessToken = feedbackData.getString("accessToken");
                    String appId = feedbackData.getString("appId");
                    data.put("accessToken", accessToken);
                    data.put("appId", appId);
                    result.setData(data);
                    result.setCode("1");
                    result.setDesc("直接创建Token成功");
                    return;
                } else {
                    result.setCode("0");
                    result.setDesc("请求9188用户中心直接创建Token失败");
                    return;
                }
            } else {
                result.setCode("0");
                result.setDesc("不是正式环境开发测试用户");
                return;
            }
        } catch (Exception e) {
            logger.info("短信快登|generateTokenDirect:直接生成Token信息异常" + e.getMessage(), e);
            result.setCode("0");
            result.setDesc("直接生成Token信息异常");
        }
    }

    /**
     * 新版头像上传(针对新版公共请求参数)
     * add by shangqinghua 20170406
     *
     * @param hskUserBean
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/control/user/uploadIconNew.go")
    public BoltResult uploadIconNew(HskUserBean hskUserBean, HttpServletRequest request, HttpServletResponse response) {
        BoltResult boltResult = new BoltResult("1", "上传头像成功");
        String enctype = request.getHeader("content-type");
        PIC_PATH = "imgs/user_icon/";
        if (enctype != null && enctype.indexOf("multipart/form-data") > -1) {
            try {
                //处理文件上传请求
                BoltResult handleResult = this.handleMultipartRequest(hskUserBean, request, response, 1);
                if (!"1".equals(handleResult.getCode())) {
                    //处理文件上传请求失败
                    boltResult.setCode("-1");
                    boltResult.setDesc(handleResult.getDesc());
                    logger.info("处理文件上传请求失败,code:{},desc:{}", handleResult.getCode(), handleResult.getDesc());
                    return boltResult;
                }
                //根据accessToken,appId查询cuserId
                logger.info("根据accessToken,appId查询cuserId");
                TokenDto tokenDto = client.execute(new DrpcRequest("hskUser", "queryToken", hskUserBean), TokenDto.class);
                String cuserId = "";
                if (tokenDto != null) {
                    cuserId = tokenDto.getCuserId();
                }
                logger.info("cuserId:{}", cuserId);
                hskUserBean.setCuserId(cuserId);
                //根据cuserId查询用户信息
                logger.info("根据cuserId查询用户信息,cuserId:{}", cuserId);
                UserDto userDto = client.execute(new DrpcRequest("hskUser", "queryUserByCuserId", cuserId), UserDto.class);
                logger.info("根据cuserId查询的用户:user:{}", userDto);
                if (userDto != null) {
                    hskUserBean.setPwd(userDto.getPwd());
                    hskUserBean.setPwd9188(userDto.getPwd9188());
                }
                String bindStr = client.execute(new DrpcRequest("hskUser", "bindIcon", hskUserBean));
                BoltResult bindResult = JSONObject.parseObject(bindStr, BoltResult.class);
                if ("1".equals(bindResult.getCode())) {
                    boltResult.setCode("1");
                    boltResult.setDesc("上传头像成功");
                    logger.info("上传头像成功");
                    return boltResult;
                } else {
                    boltResult.setCode("-1000");
                    boltResult.setDesc("上传头像失败");
                    logger.info("上传头像失败");
                    return boltResult;
                }
            } catch (Exception e) {
                e.printStackTrace();
                boltResult.setCode("-1000");
                boltResult.setDesc("上传失败");
                return boltResult;
            }
        }
        return null;
    }

    /**
     * 处理文件上传请求
     *
     * @param request
     * @param hskUserBean
     * @param type
     * @throws Exception
     */
    private BoltResult handleMultipartRequest(HskUserBean hskUserBean, HttpServletRequest request, HttpServletResponse response, int
            type)
            throws Exception {
        BoltResult result = new BoltResult("1", "上传图片文件成功");
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
            String fieldName = item.getFieldName(); //token
            String firstChar = fieldName.substring(0, 1).toUpperCase(); //T
            fieldName = firstChar + fieldName.substring(1); //Token
            if (item.isFormField()) {
                //普通参数设置到hskUserBean中
                String setter = "set" + fieldName; //setToken
                Object arg = item.getString("UTF-8");
                for (Method method : methods) {
                    if (setter.equalsIgnoreCase(method.getName())) {
                        //获取方法的形参类型
                        classtype = method.getParameterTypes()[0].getName();
                        if ("int".equals(classtype)) {
                            arg = Integer.parseInt(item.getString());
                        }
                        method.invoke(hskUserBean, arg);
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
                    result.setCode("-1");
                    result.setDesc("只能上传.jpg或.png格式图片");
                    logger.info("不支持的图片格式，只能上传.jpg或.png格式图片");
                    return result;
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
                builder.append(SEVER_ADDRESS);
                builder.append("/");
                builder.append(uploadPath);
                builder.append("/");
                builder.append(imgName);
                hskUserBean.setIcon(builder.toString());
                logger.info("cuserId:" + hskUserBean.getCuserId() + " 图片地址：" + builder.toString());
            }
        }
        return result;
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
     * 新版修改昵称(针对HskUserBean)
     * add by shangqinghua 20170406
     *
     * @param hskUserBean
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/control/user/chgNickNameNew.go")
    public BoltResult chgNickNameNew(HskUserBean hskUserBean, HttpServletRequest request, HttpServletResponse response) {
        BoltResult boltResult = new BoltResult("1", "昵称修改成功");
        String nickname = request.getParameter("nickname");
        logger.info("chgNickNameNew nickname:" + nickname);
        hskUserBean.setUid(nickname);
        hskUserBean.setIpAddr(WebUtil.getRealIp(request).trim());
        String method = request.getMethod();
        logger.info("修改用户昵称:cuserId:{},uid:{},method:{}", hskUserBean.getCuserId(), hskUserBean.getUid(), method);
        BoltResult result = client.execute(new DrpcRequest("hskUser", "chgNickNameNew", hskUserBean), BoltResult.class);
        logger.info("修改昵称结果:code:{},desc:{}", result.getCode(), result.getDesc());
        boltResult.setCode(result.getCode());
        boltResult.setDesc(result.getDesc());
        return boltResult;
    }

}
