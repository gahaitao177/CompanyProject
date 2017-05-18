package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/9/19.
 * 公用的用户中心未能上线，暂时使用老版的9188用户中心
 * 等到新用户中心上线，废弃使用...
 */
@RestController
@RequestMapping("/user")
public class RestOldUserController {
    private static Logger logger = LoggerFactory.getLogger(RestOldUserController.class);
    @Resource(name = Constant.HSK_USER)
    private IDrpcClient client;


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




//    @RequestMapping("/registerchk.go")
//    public void registerchk9188(User bean, HttpServletRequest request, HttpServletResponse response) {
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        String mobileno = bean.getUid();
//        bean.setMobileNo(mobileno);
//        logger.info("请求发送验证码mobileNo=" + mobileno + "ipAddr=" + bean.getIpAddr());
//        //如果没能取到手机号码,提示用户重新尝试
//        if (StringUtil.isEmpty(mobileno) || !CheckUtil.isMobilephone(mobileno)) {
//            XmlUtils.writeXml(1000, "手机号码格式错误", response);
//            return;
//        }
//        if (CheckUtil.isNullString(bean.getTimeStamp()) || CheckUtil.isNullString(bean.getKey())) {
//            XmlUtils.writeXml(1001, "版本过低，请升级客户端版本!", response);
//            return;
//        }
//
//        /**
//         * 9188校验是否允许注册
//         */
//        BoltResult result = Api9188Util.registerchk(bean.transformSource(), bean.getMobileNo());
//        if (!"0".equals(result.getCode())) {
//            if ("quickLogin".equals(bean.getActionName()) && "1001".equals(result.getCode())) {
//                logger.info("用户【" + bean.getUid() + "】快速登录...");
//            } else {
//                XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//                return;
//            }
//        }
//        /**
//         * 惠刷卡校验是否允许注册
//         * 非快登情况
//         */
//        if (!"quickLogin".equals(bean.getActionName())) {
//            result = client.execute(new DrpcRequest("user", "query_user_byPhone", bean), BoltResult.class);
//            if (!result.isSuccess()) {
//                //已经存在 or 其他错误
//                XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//            }
//        }
//        String yzm = CheckUtil.randomNum();
//        bean.setYzm(yzm);
//        if ("dk".equals(bean.getComeFrom())) {
//            bean.setYzmType(User.DK_YZM);
//        } else {
//            bean.setYzmType(User.YZM_TYPE);
//        }
//        logger.info("手机号：【" + bean.getMobileNo() + "】，验证码：【" + bean.getYzm() + "】，IP地址：【" + bean.getIpAddr() + "】");
//
////        sendSms
//
//        result = client.execute(new DrpcRequest("user", "sendSms", bean), BoltResult.class);
//        XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//    }


//    @RequestMapping("/register.go")
//    public void register9188(User bean, HttpServletRequest request, HttpServletResponse response) {
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        /**
//         * 1 校验参数
//         * 2 检查验证码
//         * 3 快登情况
//         * 4 调用9188注册
//         * 5 检测是否快登注册，快登注册，发送密码
//         * 6 使用9188token
//         * 7 保存用户信息到惠刷卡数据库
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
//
//        bean.setMobileNo(bean.getUid());
//        // 验证 验证码
//        if ("dk".equals(bean.getComeFrom())) {
//            bean.setYzmType(User.DK_YZM);
//        } else {
//            bean.setYzmType(User.YZM_TYPE);
//        }
//        result = client.execute(new DrpcRequest("user", "checkSms", bean), BoltResult.class);
//        if(!result.isSuccess()){
//            XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//            return;
//        }
//
//        if ("dk".equals(bean.getComeFrom())) {
//            result = Api9188Util.registerchk(bean.transformSource(), bean.getMobileNo());
//            if("1003".equals(result.getCode())){
//                //快登 已经注册
//                result = client.execute(new DrpcRequest("user", "queryUserByPhone", bean), BoltResult.class);
//                if(result.isSuccess()){
//                    Element element =  XmlUtils.jsonParseXml((JSONObject) result.getData(),"Resp");
//                    element.addAttribute("code",result.getCode());
//                    element.addAttribute("desc","快速登录成功");
//                    XmlUtils.writeXml(element,response);
//                }else{
//                    XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//                }
//            }
//
//        }else{
//
//            result = Api9188Util.register(bean.getPwd9188(),bean.transformSource(),bean.getMobileNo(),bean.getIpAddr(),1);
//            if(!"0".equals(result.getCode())){
//                XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//                return;
//            }
//            User user = (User) result.getData();
//            String appId = user.getAppId();
//            String accessToken = user.getAccessToken();
//            result = Api9188Util.getUserInfo(appId,accessToken,bean.transformSource());
//            if(!"0".equals(result.getCode())){
//                XmlUtils.writeXml(result.getCode(), result.getDesc(), response);
//                return;
//            }
//            user = (User) result.getData();
//            bean.setYzm(bean.getPwd());
//            bean.setYzmType(User.DK_PWD);
//            bean.setCnickname(user.getCnickname());
//            bean.setImobbind(user.getImobbind());
//            bean.setRealname(user.getRealname());
//            bean.setIdcard(user.getIdcard());
//
//            result = client.execute(new DrpcRequest("user", "saveUserInfo9188", bean), BoltResult.class);
//            Element element = new DOMElement("Resp");
//            element.addAttribute("code",result.getCode());
//            element.addAttribute("desc",result.getDesc());
//            element.addAttribute("appId",appId);
//            element.addAttribute("accessToken",accessToken);
//            XmlUtils.writeXml(element,response);
//        }
//    }

//    @RequestMapping("/modifyUserInfo.go")
//    public void modifyUserInfo9188(User bean,HttpServletRequest request,HttpServletResponse response){
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        User user = client.execute(new DrpcRequest("user", "alterUserInfo9188", bean), User.class);
//        XmlUtils.writeXml(user.getBusiErrCode(),user.getBusiErrDesc(),response);
//    }


//    @RequestMapping("/resetPwdNotLogin.go")
//    public void resetPwdNotLogin9188(User bean,HttpServletRequest request,HttpServletResponse response){
//        bean.setIpAddr(WebUtil.getRealIp(request));
//        User user = client.execute(new DrpcRequest("user", "resetPwdNotLogin9188", bean), User.class);
//        XmlUtils.writeXml(user.getBusiErrCode(),user.getBusiErrDesc(),response);
//    }

    //@Deprecated
    //@SetUserDataRequired
    //@RequestMapping("/queryUserAccount.go")
    //public void queryUserAccount1(HttpServletRequest request, HttpServletResponse response,User user) {
    //
    //    //查询收藏信息 适用于1.3.1之后版本
    //
    //    //获取账号信息
    //    logger.info("**********************查询个人信息********************");
    //    logger.info("接收参数getCuserId=" + user.getCuserId());
    //    if(StringUtils.isEmpty(user.getCuserId())){
    //        user.setCuserId(request.getParameter("cuserId"));
    //    }
    //    BoltResult result = client.execute(new DrpcRequest("user", "queryUserAccount9188", user), BoltResult.class);
    //    if(BoltResult.SUCCESS.equals(result.getCode())){
    //        Element resp = new DOMElement("Resp");
    //        resp.addAttribute("code", "1");
    //        resp.addAttribute("desc", "查询成功");
    //        resp.add(XmlUtils.jsonParseXml((JSONObject) JSON.toJSON(result.getData()), "info"));
    //        XmlUtils.writeXml(resp, response);
    //
    //    }else{
    //        XmlUtils.writeXml(result.getCode(),result.getDesc(),response);
    //    }
    //}

}
