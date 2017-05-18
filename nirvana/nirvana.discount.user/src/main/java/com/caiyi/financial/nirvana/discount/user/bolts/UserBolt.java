package com.caiyi.financial.nirvana.discount.user.bolts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.annotation.BoltParam;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.user.bean.U_AccountBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import com.caiyi.financial.nirvana.discount.user.exception.UserException;
import com.caiyi.financial.nirvana.discount.user.service.TokenService;
import com.caiyi.financial.nirvana.discount.user.service.UserService;
import com.util.string.StringUtil;
import org.apache.storm.task.TopologyContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by heshaohua on 2016/5/20.
 */
@Bolt(boltId = "user", parallelismHint = 1, numTasks = 1)
public class UserBolt extends BaseBolt {
    private String YZM_TYPE = "0"; // 惠刷卡注册发送验证码
    private String DK_YZM = "4"; // 有鱼贷款注册/快速登录发送验证码
    private String DK_PWD = "5"; // 有鱼贷款注册成功发送密码

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;


    @Override
    protected void _prepare(Map map, TopologyContext topologyContext) {
        userService = getBean(UserService.class);
    }


    /**
     * 查询用户信息
     *
     * @param user
     */
    @BoltController
    public JSONObject queryUserAccount(User user) {
        JSONObject json = new JSONObject();
        UserDto userDto = userService.queryUserAccount(user);
        if (null == userDto) {
            json.put("code", "1000");
            json.put("cphone", "未查询到用户信息");
            return json;
        }
        json.put("cnickid", userDto.getCnickid());
        json.put("cphone", userDto.getCphone());
        json.put("ctinyurl", userDto.getCtinyurl());
        json.put("cusername", userDto.getCusername());
        json.put("username", userDto.getUsername());
        json.put("banks", userDto.getBanks());
        json.put("stores", userDto.getStores());
        json.put("cards", userDto.getCards());
        json.put("coupons", userDto.getCoupons());
        json.put("total", userDto.getTotal());
        return json;
    }

    /**
     * @param user
     * @return
     */
    @BoltController
    public TokenDto query_userToken(User user) throws ParseException {

        try {
            return tokenService.queryToken(user);
        } catch (UserException e) {
            if (!e.getCode().equals("9001")) {//不为9001异常时候，注销token
                tokenService.logoutToken(e.getBoltResult().toJsonString(), user.getAccessToken(), user.getAppId());
            }
            throw e;
        }
    }

    @BoltController
    public int query_userType(User user) {
        UserDto userDto = null;
        int type = -1;
        try {
            type = userService.query_userType(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    @BoltController
    public UserDto quick_login(User user) {
        UserDto userDto = null;
        try {
            userDto = userService.userLogin(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDto;
    }

    @BoltController
    public UserDto query_userPwd9188(User user) {
        logger.info("---------------------UserBolt query_userPwd9188");
        UserDto userDto = null;
        try {
            userDto = userService.query_userPwd9188(user);
        } catch (Exception e) {
            e.getMessage();
        }

        return userDto;
    }

    @BoltController
    public UserDto save_9188User(User user) {
        logger.info("---------------------UserBolt save_9188User");
        UserDto userDto = null;
        try {
            userDto = userService.save9188User(user);
        } catch (Exception e) {
            e.getMessage();
        }
        return userDto;
    }

    /***修改昵称
     * @param bean 需要修改的用户
     * 测试url http://localhost:9090/rest/user/chgNickName.go?userType=1&uid=%E6%B5%8B%E8%AF%95dh&cuserId=84d45b82c303
     * ***/
    @BoltController
    public User chgNickName(User bean) {

        try {
            String userNickName = bean.getUid();
            if (StringUtil.isEmpty(userNickName)) {
                bean.setBusiErrCode(1001);
                bean.setBusiErrDesc("昵称不能为空!");
                return bean;
            }
            bean.setUid(userNickName);
            System.out.println("昵称s3:" + userNickName);
            userService.chgNickName(bean);

        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("fail");
            logger.info(e.getMessage(), e);
        }
        return bean;
    }

    /**
     * 更新本地密码
     *
     * @param user
     * @return
     */
    @BoltController
    public int updateUserPwd(User user) {
        return userService.updateUserPwd(user);
    }

    /**
     * 新版保存用户信息
     *
     * @param user
     * @return
     */
    @BoltController
    public UserDto persist9188User(User user) {
        logger.info("-----------UserBolt persist9188User----------");
        UserDto userDto = null;
        try {
            userDto = userService.persist9188User(user);
        } catch (Exception e) {
            e.getMessage();
        }
        return userDto;
    }

    /**
     * 判断（微信）用户是否绑定过手机
     * by DH 2016/7/15
     * 测试url  http://localhost:9090/rest/user/isBound.go?userType=1&uid=%E6%B5%8B%E8%AF%95dh&cuserId=84d45b82c303
     */
    @BoltController
    public User isBound(User bean) {

        try {
            String phone = userService.IsBound(bean);
            if (phone == null) {
                bean.setBusiErrCode(-1);
                bean.setBusiErrDesc("没有该用户");
                return bean;
            }
            if (StringUtil.isEmpty(phone)) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("没有绑定手机号");
            } else {
                bean.setBusiErrCode(1);
                bean.setBusiErrDesc("已经绑定手机号");
            }
        } catch (Exception e) {
            e.printStackTrace();
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("查询异常");
        }
        return bean;
    }

    /**
     * 为(微信)用户绑定手机号（登陆状态下绑定手机号）
     *
     * @param bean
     */
    @BoltController
    public User mobileBinding(User bean) {

        if (!CheckUtil.isMobilephone(bean.getMobileNo())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("手机号错误");
            return bean;
        }
        if (StringUtil.isEmpty(bean.getYzm())) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("请输入验证码");
            return bean;
        }
        userService.mobileBinding(bean);

        return bean;
    }

    /**
     * 关注银行
     * 需要检测是否登陆
     * by dh 2016/7/16
     **/
    @BoltController
    public User bankFocus(User bean) {
        userService.bankFocus(bean);
        return bean;
    }


    /**
     * 发送忘记密码验证码 (已登录)
     * by dh
     **/
    @BoltController
    public User sendForgetPwdYzm(User bean) {

        userService.sendForgetPwdYzm(bean);
        return bean;
    }

    /**
     * 新版，登录状态用户修改密码
     *
     * @param bean
     */
    @BoltController
    public User alterUserInfo9188(User bean) {
        userService.alterUserInfo9188(bean);
        return bean;
    }

    /**
     * 忘记密码发送验证码(未登录状态)
     **/
    @BoltController
    public User resetPwdNotLogin9188(User bean) {
        if (CheckUtil.isNullString(bean.getMobileNo())) {
            bean.setBusiErrCode(1000);
            bean.setBusiErrDesc("请填写手机号");
            return bean;
        }
        if (!CheckUtil.isMobilephone(bean.getMobileNo())) {
            bean.setBusiErrCode(1001);
            bean.setBusiErrDesc("手机号格式错误");
            return bean;
        }
        userService.resetPwdNotLogin9188(bean);
        return bean;

    }

    /**
     * 用户点赞
     */
    @BoltController
    public User userPraise(User bean) {
        userService.userPraise(bean);
        return bean;
    }


    /**
     * 注册token入库.
     *
     * @param bean
     */
    @BoltController
    public BoltResult registerToken(TokenDto bean) {
        tokenService.saveToken(bean);
        return new BoltResult(BoltResult.SUCCESS, "");
    }


    @BoltController
    public User expiredCollection(User bean) {

        userService.expiredCollection(bean);
        return bean;
    }

    @BoltController
    public JSONObject leanCloudUserBind(User bean) {
        return userService.leanCloudUserBind(bean);
    }

    @BoltController
    public User cooperationUserBind(User bean) {
        userService.cooperationUserBind(bean);
        return bean;
    }

    @BoltController
    public User bindIcon(User bean) {
        try {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("上传成功");
            logger.info("user icon:", bean.getIcon());
            int ret = userService.u_bind_icon(bean);
            if (ret != 1 || bean.getBusiErrCode() != 1) {
                logger.info("用户上传头像失败:" + bean.getBusiErrDesc());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("fail");
            logger.info(e.getMessage(), e);
        }
        return bean;
    }

    @BoltController
    public BoltResult queryUserAccount9188(User bean) {
        U_AccountBean uAccountBean = userService.u_account(bean);
        BoltResult boltResult = new BoltResult();
        if (uAccountBean != null) {
            boltResult.setCode(BoltResult.SUCCESS);
            boltResult.setDesc("查询成功");
            boltResult.setData(uAccountBean);
        } else {
            boltResult.setCode(BoltResult.ERROR);
            boltResult.setDesc("查询失败");
        }
        return boltResult;
    }


    /**
     * 根据用户名返回六个优惠
     *
     * @param cuserid
     * @return
     * @throws Exception
     */
    @BoltController
    public JSONObject queryComm(@BoltParam("cuserid") String cuserid) {
        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            String xmlStr = userService.fetchComm(cuserid, 6, null);
            Document xmlWaper1 = XmlTool.read(xmlStr, "UTF-8");
            Element rootElement1 = XmlTool.getRootElement(xmlWaper1);
            List<Element> xmlNodeList = rootElement1.elements("fontaitem");

            for (int i = 0, size = xmlNodeList.size(); i < size; i++) {
                Element xmw = xmlNodeList.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cname", xmw.attributeValue("cname"));
                List<Element> lists = xmw.elements();
                String cash = lists.get(0).attributeValue("ccash");
                jsonObject.put("icommid", xmw.attributeValue("icommid"));
                jsonObject.put("ccash", cash);
                jsonArray.add(jsonObject);
            }
            result.put("code", 1);
            result.put("desc", "查询成功");
            result.put("data", jsonArray);
            return result;

        } catch (Exception e) {
            //bean.setBusiErrCode(-1);
            //bean.setBusiErrDesc("fail");
            logger.info(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 发送验证码
     *
     * @param user
     * @return
     */
    @BoltController
    public JSONObject sendSms(User user) {
        JSONObject json = new JSONObject();
        User bean = userService.sendSms(user);
        json.put("code", bean.getBusiErrCode());
        json.put("desc", bean.getBusiErrDesc());
        return json;
    }

    /**
     * 校验验证码
     *
     * @param user
     * @return
     */
    @BoltController
    public JSONObject checkSms(User user) {
        JSONObject json = new JSONObject();
        User bean = userService.checkSms(user);
        json.put("code", bean.getBusiErrCode());
        json.put("desc", bean.getBusiErrDesc());
        return json;
    }

    @BoltController
    public BoltResult query_user_byPhone(User user) {
        int count = userService.query_user_byPhone(user.getMobileNo());
        if (count > 0) {
            return new BoltResult("1001", "这个手机号已经注册啦,请直接登录!");
        } else {
            return new BoltResult(BoltResult.SUCCESS, "");
        }
    }

    @BoltController
    public BoltResult queryUserByPhone(User user) {
        UserDto dto = userService.queryUserByPhone(user.getMobileNo());
        if (dto != null) {
            return new BoltResult(BoltResult.SUCCESS, "", dto);
        } else {
            return new BoltResult("1001", "这个手机号已经注册啦,请直接登录!");
        }
    }

    @BoltController
    public BoltResult saveUserInfo9188(User bean) throws Exception {
        //保存用户信息，如果是dk，发送密码短信给用户
        String pwd9188 = MD5Util.compute(bean.getPwd() + User.MD5_KEY_9188);//9188加密的密码
        String pwd = MD5Util.compute(bean.getPwd() + User.MD5_KEY);//惠刷卡加密的密码
        int count = userService.insert_user_record(bean);
        if (count > 0) {
            return new BoltResult(BoltResult.SUCCESS, "注册成功!");
        } else {
            return new BoltResult(BoltResult.SUCCESS, "");
        }
    }


    /**
     * 根据cuserId查询用户信息
     *
     * @param cuserId
     * @return
     */
    @BoltController
    public UserDto queryUserByCuserId(String cuserId) {
        return userService.queryUserByCuserId(cuserId);
    }

    /**
     * 根据accessToken、appId查询token信息
     *
     * @param user
     * @return
     */
    @BoltController
    public TokenDto queryToken(User user) {
        return userService.queryToken(user);
    }

}
