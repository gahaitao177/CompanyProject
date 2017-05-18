package com.caiyi.financial.nirvana.discount.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.discount.user.bean.WeChatBean;
import com.caiyi.financial.nirvana.discount.user.bean.weChatUnionid;
import com.caiyi.financial.nirvana.discount.user.dto.HskUserDto;
import com.caiyi.financial.nirvana.discount.user.mapper.UserMapper;
import com.hsk.common.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static com.caiyi.financial.nirvana.discount.user.bean.User.MD5_KEY;
import static com.caiyi.financial.nirvana.discount.user.bean.User.MD5_KEY_9188;

/**
 * Created by lizhijie on 2017/2/15.
 */
@Service
public class WeChatService extends AbstractService {

    @Autowired
    UserMapper userMapper;

    //9188用户中心服务器地址
    //private static final String BASE_URL = "http://192.168.1.51:10021";
    private static final String BASE_URL = SystemConfig.get("user_http_url");

    /**
     * 查询微信unionId对象
     *
     * @param unionId
     * @return
     */
    public weChatUnionid queryWeChatUnionId(String unionId) {
        return userMapper.query_weChat_Union(unionId);
    }

    /**
     * 原来惠刷卡微信注册，现在用9188微信注册
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public HskUserDto weChatRegister(WeChatBean bean) throws Exception {
        logger.info("开始微信用户注册");
        String nickName = "wx_" + bean.getNickname();
        bean.setNickname(this.getWeChatName(nickName));
        bean.setPwd("888888");
        HskUserDto hskUserDto = new HskUserDto();
        /**--------------------------生成cuserid----------------------------------**/
        String pwd = MD5Util.compute(bean.getPwd() + MD5_KEY);
        bean.setPwd(pwd);
        hskUserDto.setPwd(pwd);
        Boolean flag = false;
        if (StringUtils.isNotEmpty(bean.getCuserId())) {
            flag = true;
        }
        //创建用户
        String createUserResult = this.createUser(bean);
        logger.info("createUserResult:{}", createUserResult);
        if (createUserResult != null) {
            //解析第三方登录的返回结果获得cuserId
            HskUserDto dto = this.getUserId(createUserResult);
            if (dto != null) {
                if ("1".equals(dto.getCode())) {
                    logger.info("创建用户成功获取cuserId:{}", dto.getCuserId());
                    bean.setCuserId(dto.getCuserId());
                }
                bean.setAppId(dto.getAppid());
                bean.setToken(dto.getAccessToken());
            } else {
                hskUserDto.setCode("0");
                hskUserDto.setDesc("程序异常");
                return hskUserDto;
            }
        }
        if (StringUtils.isEmpty(bean.getCuserId())) {
            hskUserDto.setCode("0");
            hskUserDto.setDesc("微信登录失败");
            return hskUserDto;
        }
        /**--------------------------生成cuserid----------------------------------**/
        //查询指定昵称数量
        int count = userMapper.query_user_cnickid(bean.getCnickname());
        if (count > 0) {
            bean.setNickname(bean.getCnickname() + Math.random() * 10);
        }
        //插入用户信息至TB_USER
        int ret = userMapper.Insert_user_weChat(bean);
        if (ret == 1) {
            logger.info("wechat_register:成功,昵称:{}", bean.getNickname());
        } else {
            logger.info("wechat_register:失败,昵称:{}", bean.getNickname());
        }
        int retBind;
        if (!flag) {
            //不存在，插入用户微信绑定信息到TB_WECHAT_USER
            retBind = userMapper.Insert_user_weChatBind(bean);
        } else {
            //存在，更新TB_WECHAT_USER中用户微信绑定信息
            retBind = userMapper.updateWechatUser(bean);
        }
        if (retBind == 1) {
            logger.info("wechat_user_bind:成功,昵称:{}", bean.getNickname());
        } else {
            logger.info("wechat_user_bind:失败,昵称:{}", bean.getNickname());
        }
        if (ret == 1 && retBind == 1) {
            hskUserDto.setCode("1");
            hskUserDto.setDesc("微信登录成功");
            hskUserDto.setAppid(bean.getAppId());
            hskUserDto.setAccessToken(bean.getToken());
        } else {
            hskUserDto.setCode("0");
            hskUserDto.setDesc("微信登录失败");
        }
        return hskUserDto;
    }

    /**
     * 获取未注册的微信账号昵称
     *
     * @param name
     * @return
     */
    private String getWeChatName(String name) {
        String newName = "";
        try {
            int num = 0;
            //查询指定昵称的用户个数
            num = userMapper.query_user_nickidNum(name);
            if (0 == num) {
                newName = name;
            } else {
                String nameWx = name.split("_")[0] + "1_" + name.split("_")[1];
                newName = getWeChatName(nameWx);
            }
        } catch (Exception e) {
            logger.info("getWeChatName:{}", e);
        }
        logger.info("wechatName:{}", newName);
        return newName;
    }

    /**
     * 创建用户
     *
     * @param bean
     * @return
     */
    public String createUser(WeChatBean bean) {
        HashMap<String, String> params = new HashMap<>();
        String createUserUrl = BASE_URL + "createUser";
        logger.info("9188创建用户地址:createUserUrl:{}", createUserUrl);
        try {
            params.put("cpassword", MD5Util.compute(bean.getPwd() + MD5_KEY_9188));
        } catch (Exception e) {
            logger.info("加密失败:{}", e);
        }
        params.put("cbelongValue", "HSK");
        params.put("csource", String.valueOf(bean.getSource()));
        params.put("ipAddr", bean.getIpAddr());
        String mobileType;
        if ("ios".equals(bean.getDevType())) {
            mobileType = "1";
        } else {
            mobileType = "2";
        }
        params.put("mobileType", mobileType);
        params.put("packageName", bean.getAppPkgName());
        String result = HttpClientUtil.callHttpPost_Map(createUserUrl, params);
        logger.info(("创建用户结果:" + result));
        return result;
    }

    /**
     * 解析第三方登录返回结果获得cuserId
     *
     * @param result
     * @return
     */
    private HskUserDto getUserId(String result) {
        HskUserDto dto = new HskUserDto();
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject != null && "1".equals(jsonObject.getString("code"))) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                String cuserId = data.getString("cuserId");
                logger.info("cuserId:{}", cuserId);
                dto.setCuserId(cuserId);
                dto.setAccessToken(data.getString("accessToken"));
                dto.setAppid(data.getString("appId"));
                logger.info("accessToken:{},appId:{}", data.getString("accessToken"), data.getString("appId"));
                dto.setCode("1");
                return dto;
            } else {
                return null;
            }
        } else {
            dto.setCode(jsonObject.getString("code"));
            dto.setDesc(jsonObject.getString("desc"));
            return dto;
        }
    }
}
