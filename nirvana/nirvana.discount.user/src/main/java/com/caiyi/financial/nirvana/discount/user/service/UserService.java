package com.caiyi.financial.nirvana.discount.user.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.user.bean.*;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import com.caiyi.financial.nirvana.discount.user.exception.UserException;
import com.caiyi.financial.nirvana.discount.user.mapper.*;
import com.caiyi.financial.nirvana.discount.user.util.BankConst;
import com.danga.MemCached.MemCachedClient;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.util.http.HttpUtil;
import com.util.string.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by heshaohua on 2016/5/20.
 */
@Service
public class UserService extends AbstractService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    LeancloudMapper leancloudMapper;
    @Autowired
    CooperationMapper cooperationMapper;
    @Autowired
    AreaMapper areaMapper;
    @Autowired
    BankBillMapper bankBillMapper;
    @Autowired
    BankJfCommodityMapper bankJfCommodityMapper;
    @Autowired
    UserBankMapper userBankMapper;
    @Autowired
    MarketMapper marketMapper;
    @Autowired
    RecommendShopMapper recommendShopMapper;
    @Autowired
    SpecialPreferentialMapper specialPreferentialMapper;
    @Autowired
    MemCachedClient memCachedClient;

    private static int[] DefBank = new int[]{21, 13, 1, 2, 10, 7};//未关注为导卡的用户显示积分的银行

    static String apiHost = "";

    static {

        apiHost = SystemConfig.get("apiHost");
    }

    /**
     * 9188获取个人信息接口
     */
    private String GETUSERINFO = "/user/getuserbasicinfo.go";

    public String MD5_KEY = "http://www.huishuaka.com/";
    public String MD5_KEY_9188 = "http://www.9188.com/";


    /**
     * 9188修改密码接口
     */
    private final static String MODIFY_PWD = apiHost + "/user/modify.go";

    /**
     * 9188重置密码接口
     */
    private final static String RESET_PWD = apiHost + "/user/usergetpwdyz.go";


    /**
     * 查询用户类型
     *
     * @param user
     * @return
     */
    public int query_userType(User user) {
        UserDto resultDto = new UserDto();

        User user1 = new User();
        if (CheckUtil.isMobilephone(user.getUid())) {//手机号登录
            user1.setMobileNo(user.getUid());
        } else {//用户名登录
            user1.setCusername(user.getUid());
        }

        try {
            //type 0惠刷卡老用户 1惠刷卡新注册用户 2惠刷卡和9188都注册过用户
            int type = userMapper.query_user_type(user1);

            if (StringUtil.isEmpty("" + type)) {
                return -1;
            } else {
                return type;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 登录接口
     *
     * @param user
     * @return
     */
    public UserDto userLogin(User user) {
        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("uid", user.getUid());
        parms.put("pwd", user.getPwd());

        userMapper.user_login(parms);
        System.out.println(parms.get("cuserId"));
        System.out.println(parms.get("busiErrCode"));
        System.out.println(parms.get("busiErrDesc"));

        UserDto dto = new UserDto();
        dto.setCuserId((String) parms.get("cuserId"));
        dto.setBusiErrCode((String) parms.get("busiErrCode"));
        dto.setBusiErrDesc((String) parms.get("busiErrDesc"));
        return dto;
    }

    /**
     * 查询用户密码
     *
     * @param user
     * @return
     */
    public UserDto query_userPwd9188(User user) {
        UserDto dto = new UserDto();
        try {
            String pwd9188 = userMapper.query_user_pwd9188(user.getUid(), user.getPwd());
            dto.setPwd9188(pwd9188);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    /**
     * 9188用户第一次登的时候保存信息到系统库
     *
     * @param user
     * @return
     */
    public UserDto save9188User(User user) {
        //不使用9188账户体系，而且该方法明显存在问题
        throw new RuntimeException("不应该存在的方法");
//        UserDto userDto = null;
//        try {
//            String pwd = "";
//            String pwd9188 = "";
//
//            userDto = userMapper.query_user_byId(user.getCuserId(), null);
//            if (userDto != null) {
//                pwd = userDto.getPwd();
//                pwd9188 = userDto.getPwd9188();
//                if (!StringUtil.isEmpty(pwd) || !StringUtil.isEmpty(pwd9188)) {//存在的话，对比密码是否修改过
//                    boolean isModify = false;
//                    if (!StringUtil.isEmpty(user.getPwd()) && StringUtil.isEmpty(pwd)) {//检查pwd是否修改过
//                        isModify = true;
//                        if (!isModify) {
//                            if (!pwd.equals(user.getPwd())) {
//                                isModify = true;
//                            }
//                        }
//                    }
//
//                    if (!isModify && !StringUtil.isEmpty(user.getPwd9188()) && StringUtil.isEmpty(pwd9188)) {//检查pwd9188是否修改过
//                        isModify = true;
//                        if (!isModify) {
//                            if (!pwd9188.equals(user.getPwd9188())) {
//                                isModify = true;
//                            }
//                        }
//                    }
//
//                    if (isModify) {
//                        int z = userMapper.update_pwd(user.getPwd(), user.getPwd9188(), user.getCuserId());
//                    }
//                }
//                userDto.setBusiErrCode("1");
//                userDto.setBusiErrDesc("success");
//                return userDto;
//            }
//
//            if (user.getSource() == null) {
//                user.setSource(0);
//            }
//            //保存该用户
//            Map<String, String> info = getUserInfo(user.getAppId(), user.getAccessToken(), user.getSource());
//
//            //如果是在惠刷卡和9188都注册过的用户，账户数据合并之前，先用惠刷卡的cuserid
//            String mobileNo = info.get("mobileno");
//            if (!StringUtil.isEmpty(mobileNo)) {
//                userDto = userMapper.query_user_byId(null, mobileNo);
//                if (userDto != null) {
//                    String cuserId = userDto.getCuserId();
//                    pwd = userDto.getPwd();
//                    pwd9188 = userDto.getPwd9188();
//                    if (!StringUtil.isEmpty(cuserId)) {
//                        int ret = userMapper.update_userType(info.get("username"), user.getPwd9188(), mobileNo);
//                        if (ret == 1) {
//                            logger.info("更新用户类型成功，改用户为惠刷卡和9188两边都有的用户！");
//                        }
//
//                        userDto.setCuserId(cuserId);
//                        userDto.setPwd(pwd);
//                        userDto.setPwd9188(pwd9188);
//
//                        userDto.setBusiErrCode("1");
//                        userDto.setBusiErrDesc("更新用户类型成功，改用户为惠刷卡和9188两边都有的用户！");
//                        return userDto;
//                    }
//                }
//            }
//
//            //产生一个昵称
//            String time = String.valueOf((new Date()).getTime());
//            StringBuffer str = new StringBuffer();
//            str.append(user.getCuserId());
//            str.append(info.get("mobileno"));
//            str.append(user.getPwd9188());
//            str.append(time);
//            String cnickid = MD5Util.compute(str + MD5_KEY).substring(12, 20);
//            //sql = "select SEQ_CUSERID.nextVal num from dual";
//            //jrs = jcn.executeQuery(sql);
//            int num = userMapper.query_user_nextVal();
//            cnickid = cnickid + String.valueOf(num);
//
//            if (!StringUtil.isEmpty(info.get("source"))) {
//                user.setSource(Integer.parseInt(info.get("source")));
//            }
//
//            User user1 = new User();
//            user1.setUid(cnickid);
//            user1.setPwd(user.getPwd());
//            user1.setPwd9188(user.getPwd9188());
//            user1.setCusername(info.get("username"));
//            user1.setMobileNo(info.get("mobileno"));
//            user1.setCreateTime(DateUtil.getDate(DateUtil.getCurrentTime(), "yyyy-mm-dd hh:mm:ss"));
//            user1.setIpAddr(user.getIpAddr());
//            user1.setImobbind(Integer.parseInt(info.get("mobbind")));
//            user1.setCuserId(user.getCuserId());
//            user1.setSource(Integer.parseInt(info.get("source")));
//            user1.setRealname(info.get("realname"));
//            user1.setIdcard(info.get("idcard"));
//            user1.setUserType(2);
//
//            int ret = userMapper.insert_user_record(user1);
//
//            if (ret == 1) {
//                userDto.setBusiErrCode("1");
//                userDto.setBusiErrDesc("保存9188用户成功");
//                //user.setBusiErrCode(1);
//                //user.setBusiErrDesc("保存9188用户成功");
//                logger.info("保存9188用户成功！");
//            } else {
//                userDto.setBusiErrCode("-1");
//                userDto.setBusiErrDesc("保存9188用户失败");
//                //user.setBusiErrCode(-1);
//                //user.setBusiErrDesc("保存9188用户失败");
//                logger.info("保存9188用户失败！");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return userDto;
    }

    /**
     * 9188获取用户个人信息
     *
     * @param appId
     * @param accessToken
     * @param source
     * @return
     * @throws Exception
     */
    private Map<String, String> getUserInfo(String appId, String accessToken, int source) throws Exception {
        GETUSERINFO = SystemConfig.get("apiHost") + GETUSERINFO;
        Map<String, String> result = new HashMap<String, String>();
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("logintype", "1");
        params.put("appid", appId);
        params.put("accesstoken", accessToken);
        params.put("source", transformSource(source) + "");
        logger.info("获取用户信息，调用接口=" + GETUSERINFO);
        HttpUtil http = new HttpUtil(GETUSERINFO, "utf-8", params, null);

        Element rootElement = XmlTool.getRootElement(XmlTool.read(http.getResponseString(), "UTF-8"));
        String code = XmlTool.getAttributeValue(XmlTool.getElementAttribute("code", rootElement));
        if (!"0".equals(code)) {
            logger.info("从9188查询用户信息失败 code = " + code);
        }
        Element element = rootElement.element("row");
        result.put("userid", XmlTool.getAttributeValue(XmlTool.getElementAttribute("userid", element)));
        result.put("username", XmlTool.getAttributeValue(XmlTool.getElementAttribute("nickid", element)));
        result.put("source", XmlTool.getAttributeValue(XmlTool.getElementAttribute("source", element)));
        result.put("mobileno", XmlTool.getAttributeValue(XmlTool.getElementAttribute("mobileno", element)));
        result.put("mobbind", XmlTool.getAttributeValue(XmlTool.getElementAttribute("mobbind", element)));
        result.put("realname", XmlTool.getAttributeValue(XmlTool.getElementAttribute("realname", element)));
        result.put("idcard", XmlTool.getAttributeValue(XmlTool.getElementAttribute("idcard", element)));
        return result;
    }


    /**
     * 老版souce值转换为新版source值
     *
     * @param source
     * @return
     * @author lwg 2015-11-24
     */
    public int transformSource(int source) {
        if (source >= 1000 && source < 2000) {
            source += 4000;
        } else if (source == 1 || source == 0) {
            source = 5000;
        } else if (source == 2001) {
            source = 6000;
        }
        return source;
    }

    /**
     * 修改昵称
     *
     * @param bean 修改的用户信息
     */
    public void chgNickName(User bean) {
        logger.info("修改用户昵称" + bean.getUid());
        int num = userMapper.query_user_cnickid(bean.getUid());
        if (num > 0) {
            bean.setBusiErrCode(1000);
            bean.setBusiErrDesc("该昵称已被占用");
        } else {
            int ret = userMapper.update_user_cnickid(new Date(), bean.getUid(), bean.getCuserId());
            if (ret != 1) {
                logger.info("修改昵称失败：uid=" + bean.getCuserId());

            } else {
                logger.info("修改昵称成功：uid=" + bean.getCuserId());
            }
        }
    }

    /**
     * 更新用户密码
     *
     * @param user
     * @return
     */
    public int updateUserPwd(User user) {
        return userMapper.updateUserPwd(user.getPwd(), user.getPwd9188(), user.getCuserId());
    }

    /**
     * 新版，保存用户信息，根据cuserId去重
     *
     * @param user
     * @return
     */
    @Deprecated
    public UserDto persist9188User(User user) {
        //不使用9188账户体系，而且该方法明显存在问题
        throw new RuntimeException("不应该存在的方法");
//        UserDto userDto = null;
//        try {
//            String pwd = "";
//            String pwd9188 = "";
//            userDto = userMapper.query_user_byId(user.getCuserId(), null);
//            if (userDto != null) {//如果本系统库有该用户，检查并更新用户
//                pwd = userDto.getPwd();
//                pwd9188 = userDto.getPwd9188();
//                if (!StringUtil.isEmpty(pwd) || !StringUtil.isEmpty(pwd9188)) {//存在的话，对比密码是否修改过
//                    boolean isModify = false;
//                    if (StringUtil.isNotEmpty(user.getPwd()) && !user.getPwd().equals(pwd)) {//检查pwd是否修改过
//                        isModify = true;
//                    }
//                    if (!isModify && StringUtil.isNotEmpty(user.getPwd9188()) && !user.getPwd9188().equals(pwd9188))
// {//检查pwd9188是否修改过
//                        isModify = true;
//                    }
//                    if (isModify) {
//                        int z = userMapper.update_pwd(user.getPwd(), user.getPwd9188(), user.getCuserId());
//                    }
//                }
//                if (StringUtil.isNotEmpty(user.getMobileNo()) && !user.getMobileNo().equals(userDto.getCphone())) {//检查手机号是否换帮
//                    int ret = userMapper.updateMobileNo(user.getMobileNo(), user.getCuserId());
//                }
//                userDto.setBusiErrCode("1");
//                userDto.setBusiErrDesc("success");
//                return userDto;
//            }
//            if (user.getSource() == null) {
//                user.setSource(0);
//            }
//            //保存该用户
//            //如果是在惠刷卡和9188都注册过的用户，账户数据合并之前，先用惠刷卡的cuserid
//            /*String mobileNo = user.getMobileNo();
//            if (StringUtil.isNotEmpty(mobileNo)){
//                userDto = userMapper.query_user_byId(null,mobileNo);
//                if(userDto != null){
//                    String cuserId = userDto.getCuserId();
//                    pwd = userDto.getPwd();
//                    pwd9188 = userDto.getPwd9188();
//                    if(!StringUtil.isEmpty(cuserId)){
//                        int ret =  userMapper.update_userType(user.getCusername(), user.getPwd9188(), mobileNo);
//                        if (ret==1){
//                            logger.info("更新用户类型成功，改用户为惠刷卡和9188两边都有的用户！");
//                        }
//                        userDto.setCuserId(cuserId);
//                        userDto.setPwd(pwd);
//                        userDto.setPwd9188(pwd9188);
//
//                        userDto.setBusiErrCode("1");
//                        userDto.setBusiErrDesc("更新用户类型成功，改用户为惠刷卡和9188两边都有的用户！");
//                        return userDto;
//                    }
//                }
//            }*/
//
//            //产生一个昵称
//            String time = String.valueOf((new Date()).getTime());
//            StringBuffer str = new StringBuffer();
//            str.append(user.getCuserId());
//            str.append(user.getMobileNo());
//            str.append(user.getPwd9188());
//            str.append(time);
//            String cnickid = MD5Util.compute(str + MD5_KEY).substring(12, 20);
//            int num = userMapper.query_user_nextVal();
//            cnickid = cnickid + String.valueOf(num);
//
//            User user1 = new User();
//            user1.setUid(cnickid);
//            user1.setPwd(user.getPwd());
//            user1.setPwd9188(user.getPwd9188());
//            user1.setCuserId(user.getCuserId());
//            user1.setCusername(user.getCusername());
//            user1.setMobileNo(user.getMobileNo());
//            user1.setCreateTime(DateUtil.getDate(DateUtil.getCurrentDateTime(), "yyyy-mm-dd hh:mm:ss"));
//            user1.setIpAddr(user.getIpAddr());
//            user1.setImobbind(user.getImobbind());
//            user1.setSource(user.getSource());
//            user1.setRealname(user.getRealname());
//            user1.setIdcard(user.getIdcard());
//            user1.setUserType(2);//0:惠刷卡老用户，1:惠刷卡新注册用户,2:9188用户,3:惠刷卡和9188都注册过的用户
//
//            int ret = userMapper.insert_user_record(user1);
//
//            if (ret == 1) {
//                userDto.setBusiErrCode("1");
//                userDto.setBusiErrDesc("保存9188用户成功");
//                logger.info("保存9188用户成功！");
//            } else {
//                userDto.setBusiErrCode("-1");
//                userDto.setBusiErrDesc("保存9188用户失败");
//                logger.info("保存9188用户失败！");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return userDto;
    }

    /**
     * 判断（微信）用户是否绑定过手机号
     *
     * @param bean
     **/
    public String IsBound(User bean) {

        String phone = userMapper.query_user_phone(bean.getCuserId());
        return phone;
    }

    /**
     * 为（微信）用户绑定手机号
     *
     * @param bean
     **/

    public void mobileBinding(User bean) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("mobileNo", bean.getMobileNo());
            map.put("yzm", bean.getYzm());
            map.put("yzmType", bean.getYzmType());
            userMapper.registerCheckYZM(map);
            bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
            bean.setBusiErrDesc(map.get("busiErrDesc"));

            if (bean.getBusiErrCode() == 1) {
                logger.info("手机号:" + bean.getMobileNo() + "验证成功");
            } else {
                logger.info("手机号:" + bean.getMobileNo() + "验证失败：" + bean.getBusiErrDesc());
                return;
            }
            int ret = userMapper.update_user_imobbind(bean.getMobileNo(), bean.getCuserId());
            bean.setBusiErrCode(ret);
            bean.setBusiErrDesc(ret == 1 ? "绑定成功" : "绑定失败");
        } catch (Exception e) {
            e.printStackTrace();
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("绑定异常");
        }

    }

    /***
     * 银行卡关注
     **/
    public void bankFocus(User bean) {
        try {
            String bankIds = bean.getBankId();
            userMapper.delete_user_bank(bean.getCuserId());
            if (!StringUtil.isEmpty(bankIds) && !bankIds.contains("#")) {
                Map<String, String> map = new HashMap<>();
                Map<String, String> mapRes = new HashMap<>(); // 返回值
                map.put("cuserId", bean.getCuserId());
                map.put("bankId", bean.getBankId());
                userMapper.bankBind(map);

                bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
                bean.setBusiErrDesc(map.get("busiErrDesc"));
                if (bean.getBusiErrCode() == 1) {
                    logger.info("银行卡:" + bankIds + " 绑定成功");
                } else {
                    logger.info("银行卡:" + bankIds + " 绑定失败：" + bean.getBusiErrDesc());
                }
            } else if (!StringUtil.isEmpty(bankIds)) {
                // 绑定多个银行卡（用#隔开）
                String[] banksArr = bankIds.split("#");
                for (String strBkId : banksArr) {
                    if (!StringUtil.isEmpty(strBkId)) {
                        bean.setBankId(strBkId);
                        Map<String, String> map = new HashMap<>();
                        map.put("cuserId", bean.getCuserId());
                        map.put("bankId", bean.getBankId());
                        userMapper.bankBind(map);
                        bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
                        bean.setBusiErrDesc(map.get("busiErrDesc"));
                        if (bean.getBusiErrCode() == 1) {
                            logger.info("银行卡:" + bankIds + " 绑定成功");
                        } else {
                            logger.info("银行卡:" + bankIds + " 绑定失败：" + bean.getBusiErrDesc());
                        }

                    }
                }
            }
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("关注成功");

        } catch (Exception e) {

            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("绑定失败");

        }

    }


    public int query_user_byPhone(String phone) {
        return userMapper.query_user_byPhone(phone);
    }

    public UserDto queryUserByPhone(String cphone) {
        return userMapper.queryUserByPhone(cphone);
    }


    /**
     * 忘记密码发送验证码(登录状态)
     */
    public void sendForgetPwdYzm(User bean) {
        try {
            String cmobileNo = userMapper.query_user_phone(bean.getCuserId());
            if (cmobileNo != null) {
                bean.setMobileNo(cmobileNo);
                String yzm = CheckUtil.randomNum();
                bean.setYzm(yzm);
                bean.setYzmType("1");
                Map<String, String> map = new HashMap<>();
                map.put("mobileNo", bean.getMobileNo());
                map.put("yzm", bean.getYzm());
                map.put("yzmType", bean.getYzmType());
                map.put("ipAddr", bean.getIpAddr());
                userMapper.Send_PWD_YZM(map);
                bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
                bean.setBusiErrDesc(map.get("busiErrDesc"));
                if (bean.getBusiErrCode() == 1) {
                    logger.info("手机号:" + bean.getMobileNo() + "发送成功");
                } else {
                    logger.info("手机号:" + bean.getMobileNo() + "发送 失败：" + bean.getBusiErrDesc());
                }

            } else {
                bean.setBusiErrCode(1001);
                bean.setBusiErrDesc("用户信息有误");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            bean.setBusiErrCode(1099);
            bean.setBusiErrDesc("系统有误，请稍后重新操作");
        }


    }

    /**
     * 新版，登录状态用户修改密码
     *
     * @param bean
     */
    public void alterUserInfo9188(User bean) {

        try {
            logger.info("用户CuserId=" + bean.getCuserId());
            String updateId = "u_update_" + bean.getModifFlag();
            logger.info("flag---111:" + bean.getModifFlag() + ";userid:" + bean.getCuserId());
            bean.setBusiErrDesc("密码修改成功");
            bean.setBusiErrCode(1);
            if ("0".equals(bean.getModifFlag())) {
                if (CheckUtil.isNullString(bean.getOldPwd()) || CheckUtil.isNullString(bean.getNewPwd())) {
                    bean.setBusiErrCode(1001);
                    bean.setBusiErrDesc("请填写原密码和新密码");
                    return;
                }
//****************************中间版本，老用户修改惠刷卡密码--start******************************
                User bean0 = new User();
                bean0.setCuserId(bean.getCuserId());
                int userType = userMapper.query_user_type(bean0);
                if (3 == userType) {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("系统升级中，请到9188客户端或网页(www.9188.com)修改密码");
                    return;
                }
                if (0 == userType) {
                    logger.info("惠刷卡老用户，准备修改本地密码。。。");
                    User bean1 = new User();
                    bean1.setCuserId(bean.getCuserId());
                    bean1.setOldPwd(MD5Util.compute(bean.getOldPwd() + MD5_KEY));
                    String newPwd = bean.getNewPwd();
                    bean1.setNewPwd(MD5Util.compute(newPwd + MD5_KEY));
                    bean1.setPwd9188(MD5Util.compute(newPwd + MD5_KEY_9188));
                    int rs = 0;
                    if ("0".equals(bean.getModifFlag())) {
                        rs = userMapper.update_user_pwd0(bean1.getNewPwd(), bean1.getPwd9188(), bean1.getCuserId(), bean1.getOldPwd());
                    } else {
                        rs = userMapper.update_user_pwd1(bean1.getNewPwd(), bean1.getCuserId());
                    }

                    logger.info("修改密码结果:" + rs);
                    if (rs != 1) {
                        if ("0".equals(bean.getModifFlag())) {
                            bean.setBusiErrCode(1002);
                            bean.setBusiErrDesc("旧密码错误");
                            return;
                        }
                    } else {
                        bean.setBusiErrCode(1);
                        bean.setBusiErrDesc("密码修改成功");
                        return;
                    }
                }
//****************************中间版本，老用户修改惠刷卡密码--end*****************************

                //调用9188修改密码接口
                logger.info("惠刷卡新用户，准备连接到9188修改密码。。。");
                HashMap<String, String> params = new HashMap<>();
                params.put("flag", "2");
                params.put("newValue", bean.getNewPwd());
                params.put("upwd", bean.getOldPwd());
                params.put("logintype", "1");
                params.put("appid", bean.getAppId());
                params.put("accesstoken", bean.getAccessToken());
                params.put("source", transformSource(bean.getSource()) + "");
                logger.info("修改密码，调用接口=" + MODIFY_PWD);
                HttpUtil http = new HttpUtil(MODIFY_PWD, "utf-8", params, null);
                Document xmlWaper = XmlTool.read(http.getResponseString(), "UTF-8");
                Element root = XmlTool.getRootElement(xmlWaper);
                String code = XmlTool.getAttributeValue(XmlTool.getElementAttribute("code", root));
                String desc = XmlTool.getAttributeValue(XmlTool.getElementAttribute("desc", root));
                logger.info("action=alterUserInfo9188,9188返回码--code=" + code + ",desc=" + desc);
                if ("0".equals(code)) {//修改密码成功
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("密码修改成功");
                    //更新本地密码
                    String pwd = MD5Util.compute(bean.getNewPwd() + MD5_KEY);
                    String pwd9188 = MD5Util.compute(bean.getNewPwd() + MD5_KEY_9188);
//                    String sql = "update tb_user t set t.cpassword=?, t.cpwd9188=? where t.cuserid=?";
//                    jcn.executeUpdate(sql,new Object[]{pwd, pwd9188, bean.getCuserId()});
                    userMapper.update_usr_pwd2(pwd, pwd9188, bean.getCuserId());
                    return;
                } else {
                    bean.setBusiErrCode(Integer.parseInt(code));
                    bean.setBusiErrDesc(desc);
                    return;
                }

            } else {
                bean.setBusiErrCode(1099);
                bean.setBusiErrDesc("参数异常");
                return;
            }

        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("fail");
            logger.info(e.getMessage(), e);
        }

    }

    /**
     * 忘记密码发送验证码(未登录状态)
     */
    public void resetPwdNotLogin9188(User bean) {
        try {
            int num = 1;

            if ("sendYzm".equals(bean.getActionName())) {

                // add by lcs 20150924
                if (CheckUtil.isNullString(bean.getTimeStamp()) || CheckUtil.isNullString(bean.getKey())) {
                    bean.setBusiErrCode(1001);
                    bean.setBusiErrDesc("版本过低，请升级客户端版本!");
                    return;
                }
                // add by lcs 20150724 end
                String yzm = CheckUtil.randomNum();
                bean.setYzm(yzm);
                bean.setYzmType("1");
                Map<String, String> map = new HashMap<>();
                map.put("mobileNo", bean.getMobileNo());
                map.put("yzm", bean.getYzm());
                map.put("yzmType", bean.getYzmType());
                map.put("ipAddr", bean.getIpAddr());
                userMapper.Send_PWD_YZM(map);
                bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
                bean.setBusiErrDesc(map.get("busiErrDesc"));
                if (bean.getBusiErrCode() == 1) {
                    logger.info("手机号:" + bean.getMobileNo() + "发送成功");
                } else {
                    logger.info("手机号:" + bean.getMobileNo() + "发送 失败：" + bean.getBusiErrDesc());
                }
            } else if ("reSetPwd".equals(bean.getActionName())) {

                if (CheckUtil.isNullString(bean.getYzm())) {
                    bean.setBusiErrCode(1001);
                    bean.setBusiErrDesc("验证码不能为空");
                    return;
                }
                if (CheckUtil.isNullString(bean.getNewPwd())) {
                    bean.setBusiErrCode(1002);
                    bean.setBusiErrDesc("新密码不能为空");
                    return;
                }
                // 验证 验证码
                bean.setYzmType("1");
                Map<String, String> map = new HashMap<>();
                map.put("mobileNo", bean.getMobileNo());
                map.put("yzm", bean.getYzm());
                map.put("yzmType", bean.getYzmType());

                userMapper.registerCheckYZM(map);
                bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
                bean.setBusiErrDesc(map.get("busiErrDesc"));
                if (bean.getBusiErrCode() == 1) {
                    logger.info("手机号:" + bean.getMobileNo() + "验证成功");
                } else {
                    logger.info("手机号:" + bean.getMobileNo() + "验证 失败：" + bean.getBusiErrDesc());
                    return;
                }
//*********************************中间版本start******************************************
                User bean1 = new User();
                bean1.setMobileNo(bean.getMobileNo());
                int userType = userMapper.query_user_type(bean1);
                if (3 == userType) {
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("系统升级中，请到9188客户端或网页(www.9188.com)重置密码");
                    return;
                }
                if (0 == userType) {
                    //老用户用惠刷卡重置密码
                    num = 0;
                    num = userMapper.query_user_byPhone(bean.getMobileNo());
                    if (num == 1) {
                        String newValue = MD5Util.compute(bean.getNewPwd() + MD5_KEY);
                        String newValue9188 = MD5Util.compute(bean.getNewPwd() + MD5_KEY_9188);

                        int row = userMapper.update_user_pwdbyPhone(newValue, newValue9188, bean.getMobileNo());
                        if (row == 1) {
                            bean.setBusiErrCode(1);
                            bean.setBusiErrDesc("重设密码成功");
                            return;
                        } else {
                            bean.setBusiErrCode(1099);
                            bean.setBusiErrDesc("系统异常，请稍后重新操作");
                            return;
                        }
                    } else {
                        logger.info("您的手机号还没有注册不能使用找回密码功能");
                        bean.setBusiErrCode(1003);
                        bean.setBusiErrDesc("您的手机号还没有注册不能使用找回密码功能");
                    }
                }
//********************************中间版本end******************************************
                //调用9188接口重置密码
                HashMap<String, String> params = new HashMap<>();
                params.put("source", transformSource(bean.getSource()) + "");
                params.put("mobileNo", bean.getMobileNo());
                params.put("pwd", bean.getNewPwd());
                params.put("source", transformSource(bean.getSource()) + "");
                logger.info("重置密码，调用接口=" + RESET_PWD);
                HttpUtil http = new HttpUtil(RESET_PWD, "utf-8", params, null);
                Document xmlWaper = XmlTool.read(http.getResponseString(), "utf-8");
                Element root = xmlWaper.getRootElement();
                String code = XmlTool.getAttributeValue(XmlTool.getElementAttribute("code", root));    //root.getAttributeValue
                // ("code");
                String desc = XmlTool.getAttributeValue(XmlTool.getElementAttribute("desc", root)); //root.getAttributeValue("desc");
                logger.info("action=resetPwdNotLogin9188,9188返回码---code=" + code + ",desc=" + desc);

                if ("0".equals(code)) {
                    bean.setBusiErrCode(1);
                    bean.setBusiErrDesc("重设密码成功");

                    //如果9188用户直接在惠刷卡重置密码
                    int count = userMapper.query_user_countByPhone(bean.getMobileNo());
                    String pwd = MD5Util.compute(bean.getPwd() + MD5_KEY);
                    String pwd9188 = MD5Util.compute(bean.getNewPwd() + MD5_KEY_9188);
                    if (count < 1) {

                    } else {//更新本地密码

                        userMapper.update_user_pwdbyPhone(pwd, pwd9188, bean.getMobileNo());
                    }
                    return;
                } else {
                    bean.setBusiErrCode(Integer.parseInt(code));
                    bean.setBusiErrDesc(desc);
                }
            } else {
                bean.setBusiErrCode(1004);
                bean.setBusiErrDesc("参数异常");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            bean.setBusiErrCode(1099);
            bean.setBusiErrDesc("系统有误，请稍后重新操作");
        }

    }


    /**
     * 用户点赞
     **/
    public void userPraise(User bean) {
        logger.info("用户点赞");
        if (StringUtil.isEmpty(bean.getStoreId())) {
            bean.setBusiErrCode(1000);
            bean.setBusiErrDesc("门店ID不能为空");
            return;
        }
        try {

            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("点赞成功");
            Map<String, String> map = new HashMap<>();
            map.put("cuserId", bean.getCuserId());
            map.put("storeId", bean.getStoreId());
            userMapper.user_praise(map);
            bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
            bean.setBusiErrDesc(map.get("busiErrDesc"));
            System.out.println("getBusiErrCode:" + bean.getBusiErrCode());
            if (bean.getBusiErrCode() != 1) {
                bean.setBusiErrCode(1001);
                bean.setBusiErrDesc("点赞失败");
                logger.info("点赞失败:" + bean.getBusiErrDesc());
            }

        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("fail");
            logger.info(e.getMessage(), e);
        }

    }


    public Map<String, String> bankBind(String useid, String bankID) {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("cuserId", useid);
            map.put("bankId", useid);
            userMapper.bankBind(map);
            logger.info("busiErrCode :" + map.get("busiErrCode"));
            logger.info("busiErrDesc :" + map.get("busiErrDesc"));

        } catch (Exception e) {
            logger.info("绑定银行卡失败");
            logger.info(e.getMessage(), e);
            map.put("busiErrCode", "0");
            map.put("busiErrDesc", "绑定银行卡失败");

        }
        return map;

    }

    /***
     * 用户添加收藏
     ***/
    public Map<String, String> collectAdd(User bean) {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("cuserId", bean.getCuserId());
            map.put("storeId", bean.getStoreId());
            userMapper.user_collect_add(map);
        } catch (Exception e) {
            logger.info("用户添加收藏失败");
            logger.info(e.getMessage(), e);
            map.put("busiErrCode", "0");
            map.put("busiErrDesc", "用户添加收藏失败");
        }
        return map;
    }
    ///**
    // * 原来惠刷卡微信登录，现在用9188微信登录
    // *
    // * @param bean
    // */
    //@Transactional(rollbackFor = Exception.class)
    //public void weChatLogin(WeChatBean bean) throws Exception {
    //    logger.info("微信登录开始");
    //
    //    weChatUnionid unionid = userMapper.query_weChat_Union(bean.getUnionid());
    //
    //    if (unionid != null) {
    //        logger.info("微信已注册用户");
    //
    //        String cuserid = unionid.getCuserid();
    //        String pwd = unionid.getCpassword();
    //        bean.setCuserId(cuserid);
    //        bean.setPwd(pwd);
    //        bean.setBusiErrCode(1);
    //        bean.setBusiErrDesc("微信登录成功");
    //    } else {
    //        logger.info("微信未注册，添加账户");
    //        weChatRegister(bean);
    //    }


    //}

    ///**
    // * 原来惠刷卡微信注册，现在用9188微信注册
    // *
    // * @param bean
    // */

    //public void weChatRegister(WeChatBean bean) throws Exception {
    //    String nickName = "wx_" + bean.getNickname();
    //    bean.setNickname(getWxName(nickName));
    //    bean.setPwd("888888");
    //    // 验证
    //    /**--------------------------生成cuserid----------------------------------**/
    //    String pwd = MD5Util.compute(bean.getPwd() + MD5_KEY);
    //    bean.setPwd(pwd);
    //    String userId = bean.getUid();
    //    String userPassword = bean.getPwd();
    //    String time = String.valueOf((new Date()).getTime());
    //
    //    StringBuilder sb = new StringBuilder();
    //    sb.append(userId);
    //    sb.append(userPassword);
    //    sb.append(time);
    //
    //    int num = userMapper.query_dual_num();
    //    String cuserid = MD5Util.compute(sb.toString() + MD5_KEY).substring(12, 20) + String.valueOf(num);
    //    bean.setCuserId(cuserid);
    //    /**--------------------------生成cuserid----------------------------------**/
    //    int ret = userMapper.Insert_user_weChat(bean);
    //    if (ret == 1) {
    //        logger.info("wechat_register 成功" + "昵称：" + bean.getNickname());
    //    } else {
    //        logger.info("wechat_register 失败" + "昵称：" + bean.getNickname());
    //    }
    //    int retBind = userMapper.Insert_user_weChatBind(bean);
    //    if (retBind == 1) {
    //        logger.info("wechat_user_bind 成功" + "昵称：" + bean.getNickname());
    //    } else {
    //        logger.info("wechat_user_bind 失败" + "昵称：" + bean.getNickname());
    //    }
    //    if (ret == 1 && retBind == 1) {
    //        bean.setBusiErrCode(1);
    //        bean.setBusiErrDesc("微信登录成功");
    //    } else {
    //        bean.setBusiErrCode(0);
    //        bean.setBusiErrDesc("微信登录失败");
    //    }
    //
    //}

    // 获取未被注册的昵称
    private String getWxName(String name) {
        String newName = "";
        try {
            int num = 0;
            num = userMapper.query_user_nickidNum(name);
            if (0 == num) {
                newName = name;
            } else {
                String nameWx = name.split("_")[0] + "1_" + name.split("_")[1];
                newName = getWxName(nameWx);
            }

        } catch (Exception e) {
            System.out.println("getWxName---" + e);
        }
        return newName;
    }

    /**
     * 注册token入库.
     *
     * @param bean
     */
//    @Transactional(rollbackFor = Exception.class)
//    public int registerToken(User bean) {
//        TokenBean tokenBean = new TokenBean();
//        int expiresIn = 60 * 60 * 24 * 14;//二周
//        tokenBean.setAccesstoken(bean.getAccessToken());
//        tokenBean.setExpiresin(expiresIn);
//        tokenBean.setUserType(bean.getUserType());
//        tokenBean.setCuserId(bean.getCuserId());
//        tokenBean.setCpassword(bean.getPwd());
//        tokenBean.setAppid(bean.getAppId());
//        tokenBean.setParamjson(bean.getParamJson());
//        tokenBean.setPwd9188(bean.getPwd9188());
//        return registerToken(tokenBean);
//    }


//    public int registerToken(TokenBean tokenBean){
//        if(tokenBean.getExpiresin()==null || tokenBean.getExpiresin()<0 ){
//            int expiresIn = 60 * 60 * 24 * 14;//二周
//            tokenBean.setExpiresin(expiresIn);
//        }
//        return userMapper.Insert_token(tokenBean);
//    }
    public List<String> query_user_bankId(User bean) {
        logger.info("---查找收藏的关注银行---");
        List<String> bankJrs = new ArrayList();
        try {
            bankJrs = userMapper.query_user_bankId(bean.getCuserId());
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("---查找收藏的关注银行错误---");
        }

        return bankJrs;
    }

    /**
     * 查找商店Logo
     **/
    public List<StoreLogo> query_store_logo(User bean) {
        logger.info("---查找商店Logo---");
        List<StoreLogo> bankJrs = new ArrayList();
        try {
            bankJrs = userMapper.query_store_logo(bean.getCuserId());
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("---查找商店Logo错误---");
        }
        return bankJrs;
    }

    /***
     * 查找商店优惠
     ***/
    public List<StoreCheapBean> query_store_cheap(String storeId) {
        logger.info("---查找商店优惠---");
        List<StoreCheapBean> bankJrs = new ArrayList();
//        try {
        bankJrs = userMapper.query_store_cheap(storeId);
//        }catch (Exception e){
//            logger.info(e.getMessage());
//            logger.info("---查找商店优惠错误---");
//        }
        return bankJrs;
    }

    /***
     * 优惠商店
     ***/
    public List<MarketBean> query_market(User bean) {
        logger.info("---查找收藏超市---");
        List<MarketBean> bankJrs = new ArrayList();
        try {
            String cuserid = bean.getCuserId();
            bankJrs = userMapper.query_market(cuserid);
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("---查找收藏超市错误---");
        }
        return bankJrs;
    }

    /***
     * 查找商店 优惠券**query_user_Coupon
     */
    public List<CouponBean> query_user_Coupon(String cuserId, String markeiId) {
        logger.info("---查找商店优惠券---");
        List<CouponBean> bankJrs = new ArrayList();
        try {
            bankJrs = userMapper.query_user_Coupon(cuserId, "1", markeiId);
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.info("---查找商店优惠券错误---");
        }
        return bankJrs;
    }


    public void expiredCollection(User bean) {
        logger.info("删除过期收藏: ");
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("删除过期收藏成功");
        try {
            // 门店收藏过期
            List<Integer> collectJrs = userMapper.query_collect_id(bean.getCuserId());
            if (collectJrs != null && collectJrs.size() > 0) {
                for (int i = 0; i < collectJrs.size(); i++) {
                    String storeId = collectJrs.get(i).toString();
                    bean.setStoreId(storeId);

                    List<StoreCheapBean> cheapJrs = userMapper.query_store_cheap(storeId);
                    if (cheapJrs != null && cheapJrs.size() > 0) {
                        int cheapExpireCount = 0;
                        String expire = "0";

                        for (int j = 0; j < 0; j++) {
                            expire = "0";
                            String cheapExpire = cheapJrs.get(j).getIexpire().toString();
                            if ("1".equals(cheapExpire)) {
                                cheapExpireCount++;
                            }
                            if (cheapExpireCount == cheapJrs.size()) {
                                expire = "1";
                            }
                        }
                        if ("1".equals(expire)) {
                            System.out.println("過期ID--" + storeId);
                            int ret = userMapper.update_user_collection_overdue(bean.getCuserId(), storeId);
                            System.out.println("過期ret--" + ret);
                            logger.info("删除过期门店收藏成功：" + " ret: " + ret);
                            if (ret == 1) {
                                logger.info("删除过期门店收藏成功：" + " storeId: " + storeId);
                            } else {
                                logger.info("删除过期门店收藏失败：" + " storeId: " + storeId);
                            }
                        }
                    }
                }

            }
            // 删除优惠价收藏
            int ret = userMapper.update_collect_expire(bean.getCuserId());
            System.out.println("删除过期過期ret--" + ret);
            if (ret >= 1) {
                logger.info("删除过期优惠券收藏成功：" + " storeId: " + bean.getCuserId() + " 共" + ret + "条记录");
            } else {
                logger.info("删除过期优惠券收藏失败：" + " storeId: " + bean.getCuserId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("删除过期收藏失败");
        }
    }

    public List<Integer> query_collect_id(User bean) {

        return userMapper.query_collect_id(bean.getCuserId());
    }

    public Integer update_user_collection_overdue(String userId, String storeId) {
        return userMapper.update_user_collection_overdue(userId, storeId);
    }

    // 删除优惠收藏
    public Integer update_collect_expire(User bean) {
        return userMapper.update_collect_expire(bean.getCuserId());

    }


    /*
      * leancloud 用户绑定
 	 */
    public JSONObject leanCloudUserBind(User bean) {
        JSONObject result = new JSONObject();
        result.put("code", "1");
        result.put("desc", "绑定成功");
        logger.info("leanCloud 用户绑定:");
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("leanCloud 用户绑定");
        if (CheckUtil.isNullString(bean.getLeanCloudId())) {
            result.put("code", "1000");
            result.put("desc", "leanCloudId不能为空");
            return result;
        }
        String deviceType = bean.getDeviceType();
        logger.info("deviceType--" + deviceType);
        logger.info("getLeanCloudId--" + bean.getLeanCloudId());
        logger.info("getCuserId--" + bean.getCuserId());
        logger.info("bankid--" + bean.getBankId());
        logger.info("cityid--" + bean.getCityId());
        if (!"android".equals(deviceType) && !"ios".equals(deviceType)) {
            result.put("code", "1001");
            result.put("desc", "非法的参数");
            return result;
        }
        // 如果没有cuserid
        if (CheckUtil.isNullString(bean.getCuserId())) {
            bean.setCuserId("0");
        }
        if (CheckUtil.isNullString(bean.getCityId())) {
            bean.setCityId("0");
        }
        try {
            LeancloudBean leancloudBean = new LeancloudBean();
            leancloudBean.setCuserid(bean.getCuserId());
            leancloudBean.setCleancloudid(bean.getLeanCloudId());
            leancloudBean.setIdevicetype(bean.getDeviceType());
            leancloudBean.setCbankids(bean.getBankId());
            leancloudBean.setIcityid(Integer.valueOf(bean.getCityId()));

            int counts = leancloudMapper.query_leancloud_numBycloudid(leancloudBean);
            int updateRet = 0;
            if (counts == 0) {
                logger.info("leanCloud 用户插入: ");
                updateRet = leancloudMapper.insert_leancloud(leancloudBean);
            } else {
                logger.info("leanCloud 用户更新: ");
                updateRet = leancloudMapper.update_leancloud_byCloudid(leancloudBean);
            }
            if (updateRet > 0) {
                logger.info("leanCloud 用户绑定 成功: ");
            } else {
                logger.info("leanCloud 用户绑定 失败: ");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", "-1");
            result.put("desc", "操作异常");
        }
        return result;
    }

    /*
     * cooperationUserBind 用户绑定
     */
    public void cooperationUserBind(User bean) {
        logger.info("cooperation 用户绑定: ");
        bean.setBusiErrCode(1);
        bean.setBusiErrDesc("cooperation 用户绑定");

        logger.info("cooperationid:" + bean.getCooperationid());
        if (CheckUtil.isNullString(bean.getCooperationid())) {
            bean.setBusiErrCode(1000);
            bean.setBusiErrDesc("合作方标识 不能为空");
            return;
        }
        if (bean.getHztype() == 0) {
            bean.setBusiErrCode(1000);
            bean.setBusiErrDesc("非法参数");
            return;
        }

        try {
            CooperationBean cooperationBean = new CooperationBean();
            cooperationBean.setCuserid(bean.getCuserId());
            cooperationBean.setItype(bean.getHztype());
            cooperationBean.setCcooperationid(bean.getCooperationid());
            cooperationBean.setCip(bean.getIpAddr());
            cooperationBean.setIclient(bean.getIclient());

            int counts = 0;
            List<Integer> states = cooperationMapper.query_cooperation_state(cooperationBean);
            if (states != null && states.size() > 0) {
                counts = 1;
            }
            if (counts == 0) {
                logger.info("cooperation 用户插入: ");
                int ret = cooperationMapper.Insert_cooperation(cooperationBean);
                if (ret == 1) {
                    logger.info("cooperation 用户插入 成功 ");
                } else {
                    logger.info("cooperation 用户插入 失败 ");
                }
            } else {
                logger.info("leanCloud 用户更新: ");
                int ret = cooperationMapper.update_cooperation_Bycuserid(cooperationBean);
                if (ret == 1) {
                    logger.info("cooperation 用户更新 成功 ");
                } else {
                    logger.info("cooperation 用户更新 失败 ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("leanCloud 用户绑定");
        }

    }


    /**
     * 根据用户名返回六个优惠
     *
     * @param cuserId
     * @return
     * @throws Exception
     */
    public String fetchComm(String cuserId, int len, String ibankids) throws Exception {
        CommodityBean bean = new CommodityBean();
        bean.setCuserId(cuserId);
        Set<Integer> ibanks = new TreeSet<Integer>();
        if (!StringUtil.isEmpty(ibankids)) {
            String[] arr = ibankids.split("#");
            for (String a : arr) {
                if (!StringUtil.isEmpty(a)) {
                    ibanks.add(Integer.parseInt(a));
                }
            }
        }
        Map<Integer, Double> ubankjf = userjf(bean);
        Map<Integer, List<String>> result = new TreeMap<>();
        logger.info("ubankjf:{}", ubankjf);
        if (ubankjf != null) {
            // 查询和用户积分相近的商品
            Set<Map.Entry<Integer, Double>> set = ubankjf.entrySet();
            for (Map.Entry<Integer, Double> s : set) {
                int key = s.getKey();
                double val = s.getValue();
                ibanks.add(key);
                if (val > 0) {// 小于他的从大到小排序
                    // 设置参数
                    bean.setCminscore(val + "");
                    bean.setIbankid(key + "");
//                    JdbcRecordSet jf_index = JdbcSqlMapping.executeQuery("query_bank_commodity_byjf", bean, null, len,
//                            1, jcn);
                    /**
                     * add by wsl in 2017年1月14日14:39:39 临时补丁，查询积分只查50条
                     */
                    PageHelper.startPage(1, 50);


                    List<BankJfCommodity> jf_index = bankJfCommodityMapper.query_bank_commodity_byjf(bean.getIbankid(), bean
                            .getCminscore());
                    if (jf_index != null) {
                        for (int i = 0; i < jf_index.size(); i++) {
                            String cminscore = jf_index.get(i).getCminscore().toString();
                            if (!StringUtil.isEmpty(cminscore)) {
                                int v = (int) Double.parseDouble(cminscore);
                                int x = (int) (val - v);
                                List<String> ljf = result.get(x);
                                if (ljf == null) {
                                    ljf = new ArrayList<String>();
                                }
                                ljf.add(commodity2str(jf_index.get(i), ubankjf));
                                result.put(x, ljf);
                            }
                        }

                    }

                }
            }
        } else {
            //  JdbcRecordSet jrs_ubanks = JdbcSqlMapping.executeQuery("query_jf_user_bank", bean, null, jcn);
            List<UserBank> userBanks = userBankMapper.query_userBank_cnickid(bean.getCuserId());

            if (userBanks != null) {
                for (int i = 0; i < userBanks.size(); i++) {
                    ibanks.add(userBanks.get(i).getIbankid());
                }
            }
        }

        List<String> lastResult = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> re : result.entrySet()) {
            lastResult.addAll(re.getValue());
        }
        int ihave = len - lastResult.size();
        if (ihave < 0) {
            //Collections.shuffle(lastResult);
        }
        if (ihave > 0) {
            Map<String, String> parmm = new HashMap<String, String>();
            String order = "  order by cminscore,to_number(regexp_replace(nvl(coriginprice,'0'), '[^0-9.]')),cmaxscore";
            String sqlwhere = "";
            if (ibanks.size() > 0) {
                sqlwhere += "and(1=0";
                for (Integer bk : ibanks) {
                    sqlwhere += " or ibankid=" + bk;
                }
                sqlwhere += ")";
            }
            parmm.put("order", order);

            if (sqlwhere.equals("")) {
                //没有导入卡，没有关注银行
                sqlwhere += "and (1=0";
                for (int i : DefBank) {
                    sqlwhere += " or icommid=(select icommid from (select * from  tb_bank_jf_commodity where istate=0 and ibankid="
                            + i + order + " ) where rownum=1 ) ";
                }
                sqlwhere += ")";
            }
            System.out.println("sqlwhere:" + sqlwhere);
            parmm.put("sqlwhere", sqlwhere);

            List<BankJfCommodity> c_index = bankJfCommodityMapper.query_index_commodity(sqlwhere);
            if (c_index != null) {
                for (int i = 0; i < c_index.size(); i++) {
                    lastResult.add(commodity2str(c_index.get(i), ubankjf));
                }
            }

        }

        StringBuilder xml = new StringBuilder();
        xml.append("<fonta>");
        for (int i = 0; i < lastResult.size() && i < 6; i++) {
            xml.append(lastResult.get(i));
        }
        xml.append("</fonta>");
        return xml.toString();
    }

    /**
     * 用户积分
     *
     * @param bean
     * @return
     * @throws Exception
     */
    private Map<Integer, Double> userjf(CommodityBean bean) throws Exception {
        //JdbcRecordSet jrs = JdbcSqlMapping.executeQuery("query_jf_user_point", bean, null, jcn);
        List<BankPointBean> bankPointBeanList = bankBillMapper.query_jf_user_point(bean.getCuserId());
        Map<Integer, Double> jfs = new HashMap<Integer, Double>();
        if (bankPointBeanList != null) {
            for (int i = 0; i < bankPointBeanList.size(); i++) {
                int ibankid = bankPointBeanList.get(i).getIbankid();
                Double ipoint = bankPointBeanList.get(i).getIpoint();
                String icard4num = bankPointBeanList.get(i).getIcard4num();
                if (icard4num != null) {
                    Double ot = Double.parseDouble(icard4num);
                    jfs.put(ibankid, ipoint);
                }
            }
        }
        return jfs;

    }

    private String commodity2str(BankJfCommodity jrs, Map<Integer, Double> ubankjf) {

        StringBuilder row = new StringBuilder();
        int ibankid = jrs.getIbankid();
        Double c = ubankjf.get(ibankid);// 现有积分
        String cminscore = jrs.getCminscore().toString();
        cminscore = StringUtil.isEmpty(cminscore) ? "0" : cminscore;
        Double price = Double.parseDouble(cminscore);// 需要积分
        Double required = 0d;
        if (c != null && price != null && c != 0 && price != 0 && price > c) {
            // 相差多少
            required = (price - c);
        }
        String cbankname = BankConst.Bank.get(ibankid);
        row.append("<fontaitem ");
        int icommid = jrs.getIcommid();
        row.append(XmlTool.createAttrXml("icommid", icommid + ""));
        String clistimg = jrs.getClistimg();
        row.append(XmlTool.createAttrXml("required", required + ""));
        row.append(XmlTool.createAttrXml("clistimg", clistimg));
        String exchangeurl = jrs.getCfetchurl();
        row.append(XmlTool.createAttrXml("cfetchurl", exchangeurl));
        String ctitle = jrs.getCtitle();
        row.append(XmlTool.createAttrXml("ctitle", ctitle));
        String cname = jrs.getCname();
        row.append(XmlTool.createAttrXml("cname", cname));
        row.append(XmlTool.createAttrXml("ibankid", ibankid + ""));
        row.append(XmlTool.createAttrXml("cbankname", cbankname));
        String coriginprice = jrs.getCoriginprice();// 市场价
        row.append(XmlTool.createAttrXml("coriginprice", coriginprice));
        row.append(" >");
        String ccash1 = jrs.getCcash1();
        String ccash2 = jrs.getCcash2();

        String[] ctype = ccash1.split("\\|");
        // 交换规则1
        for (String cty : ctype) {
            if (!StringUtil.isEmpty(cty)) {
                row.append("<exchangetypeitem ");
                row.append(XmlTool.createAttrXml("ccash", cty));
                row.append("/>");
            }
        }
        // 交换规则2
        if (!StringUtil.isEmpty(ccash2)) {
            // row.append("<exchangetypeitem ");
            // row.append(JXmlUtil.createAttrXml("ccash",
            // ccash2.split("\\|")[0]));
            // row.append("/>");
        }
        row.append("</fontaitem>");
        return row.toString();
    }

    public List<RecommendShopBean> query_recommend_shop(String icityid, String sqlwhere) {
        try {
            List<RecommendShopBean> recommendShopBeen = recommendShopMapper.query_recommend_shop(icityid, icityid, icityid, icityid,
                    sqlwhere);
            return recommendShopBeen;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("首页推荐商户列表查询失败", e);
        }
        return null;
    }

    public List<ShopIndexBean> query_index_shop() {
        try {
            List<ShopIndexBean> shopIndexBeen = recommendShopMapper.query_index_shop();
            return shopIndexBeen;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查询首页连锁店", e);
        }
        return null;

    }

    public List<CheapTitleBean> query_cheap_ctitle(String ibussinessid, String icityid) {
        try {
            List<CheapTitleBean> shopIndexBeen = userMapper.query_cheap_ctitle(ibussinessid, icityid);
            return shopIndexBeen;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商户优惠title", e);
        }
        return null;
    }

    public List<MarketBean> query_market(String icityid) {
        try {
            List<MarketBean> marketBeanList = marketMapper.query_market(icityid);
            return marketBeanList;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("商户优惠title", e);
        }
        return null;


    }


    public Integer qurey_special_preferential_num(String sqlwhere) {

        try {
            Integer res = specialPreferentialMapper.qurey_special_preferential_num(sqlwhere);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("特惠优惠错误", e);
        }
        return null;


    }

    public Page<SpecialPreferentialBean> qurey_special_preferential(HomePageBean bean) {

        Page<SpecialPreferentialBean> res = specialPreferentialMapper.qurey_special_preferential(bean);
        return res;

    }

    public Integer u_bind_icon(User bean) {
        try {
            Integer res = userMapper.u_bind_icon(bean);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传头像错误", e);
        }
        return 0;

    }

    public UserDto queryUserAccount(User user) {
        return userMapper.queryUserAccount(user.getCuserId(), user.getPwd(), user.getPwd9188());
    }


    public U_AccountBean u_account(User bean) {
        U_AccountBean user = userMapper.u_account(bean.getCuserId());
        if (user != null) {
            user.setTotal(user.getCoupons() + user.getStores());
        }
        return user;
    }

    /**
     * 发送短信
     * mobileNo 手机号
     * yzm 验证码
     * yzmType 验证码类型
     * ipAddr ip地址
     *
     * @param bean
     * @return
     */
    public User sendSms(User bean) {
        String cmobileNo = bean.getMobileNo();
        if (StringUtil.isEmpty(cmobileNo)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("手机号为空");
        } else if ("".equals(bean.getYzm())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("验证码为空");
        } else if ("".equals(bean.getYzmType())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("验证码类型为空");
        } else if (StringUtil.isEmpty(bean.getIpAddr())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("IP为空");
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("mobileNo", bean.getMobileNo());
            map.put("yzm", bean.getYzm());
            map.put("yzmType", bean.getYzmType());
            map.put("ipAddr", bean.getIpAddr());
            userMapper.Send_PWD_YZM(map);
            bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
            bean.setBusiErrDesc(map.get("busiErrDesc"));
            if (bean.getBusiErrCode() == 1) {
                logger.info("手机号:" + bean.getMobileNo() + "发送成功");
            } else {
                logger.info("手机号:" + bean.getMobileNo() + "发送 失败：" + bean.getBusiErrDesc());
            }
        }
        return bean;
    }

    /**
     * 校验验证码
     * mobileNo 手机号
     * yzm 验证码
     * yzmType 验证码类型
     *
     * @param bean
     * @return
     */
    public User checkSms(User bean) {
        String cmobileNo = bean.getMobileNo();
        if (StringUtil.isEmpty(cmobileNo)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("手机号为空");
        } else if ("".equals(bean.getYzm())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("验证码为空");
        } else if ("".equals(bean.getYzmType())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("验证码类型为空");
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("mobileNo", bean.getMobileNo());
            map.put("yzm", bean.getYzm());
            map.put("yzmType", bean.getYzmType());
            userMapper.registerCheckYZM(map);
            bean.setBusiErrCode(Integer.valueOf(map.get("busiErrCode")));
            bean.setBusiErrDesc(map.get("busiErrDesc"));

            if (bean.getBusiErrCode() == 1) {
                logger.info("手机号:" + bean.getMobileNo() + "验证成功");
            } else {
                logger.info("手机号:" + bean.getMobileNo() + "验证失败：" + bean.getBusiErrDesc());

            }
        }
        return bean;
    }

    @Transactional(rollbackFor = Exception.class)
    public int insert_user_record(User bean) {
        int count = userMapper.insert_user_record(bean);
        if ("dk".equals(bean.getComeFrom())) {
            //快登情况 未注册，发送密码到手机
            logger.info("手机号：【" + bean.getMobileNo() + "】，快速注册成功。。。密码：【" + bean.getPwd() + "】IP地址：【" + bean.getIpAddr() + "】");
            User user = sendSms(bean);
            if (user.getBusiErrCode() == 1) {
                throw new UserException("发送短信失败");
            }
        }
        return count;
    }

    /**
     * 根据cuserId查询用户信息
     *
     * @param cuserId
     * @return
     */
    public UserDto queryUserByCuserId(String cuserId) {
        return userMapper.queryUserByCuserId(cuserId);
    }

    /**
     * 根据accessToken、appId查询token信息
     *
     * @param user
     * @return
     */
    public TokenDto queryToken(User user) {
        return userMapper.queryToken(user);
    }

}
