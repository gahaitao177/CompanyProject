package com.caiyi.financial.nirvana.discount.user.service;

import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.user.bean.HskUserBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import com.caiyi.financial.nirvana.discount.user.mapper.HskUserMapper;
import com.caiyi.financial.nirvana.discount.user.mapper.UserMapper;
import com.util.http.HttpUtil;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by lizhijie on 2017/2/8.
 */
@Service
public class HskUserService extends AbstractService {

    @Autowired
    HskUserMapper userMapper;
    @Autowired
    UserMapper userMapperOld;

    private String GETUSERINFO = "user/getuserbasicinfo.go";

    /**
     * 查询用户信息
     *
     * @param bean
     * @return
     */
    public int queryUser(User bean) {
        return userMapper.queryUserById(bean.getCuserId());
    }

    /**
     * 保存惠刷卡注册用户信息
     *
     * @param bean
     * @return
     */
    public int saveUserInfo(User bean) {
        return userMapper.insertUserInfo(bean);
    }


    /**
     * 保存注册用户来源
     *
     * @param user
     * @return
     */
    public int saveUserSource(User user) {
        return userMapper.insertUserSource(user);
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
     * 9188获取用户个人信息
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public User getUserInfo(User bean) throws Exception {

        GETUSERINFO = SystemConfig.get("apiHost") + GETUSERINFO;
//        GETUSERINFO = "http://t2015.9188.com/" + GETUSERINFO;
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("logintype", "1");
        params.put("appid", bean.getAppId());
        params.put("accesstoken", bean.getAccessToken());
        params.put("source", transformSource(bean.getSource()) + "");
        logger.info("获取用户信息，调用接口=" + GETUSERINFO);
        HttpUtil http = new HttpUtil(GETUSERINFO, "utf-8", params, null);
        Element rootElement = XmlTool.getRootElement(XmlTool.read(http.getResponseString(), "UTF-8"));
        String code = XmlTool.getAttributeValue(XmlTool.getElementAttribute("code", rootElement));
        if (!"0".equals(code)) {
            logger.info("从9188查询用户信息失败 code = " + code);
        } else {
            Element element = rootElement.element("row");
            bean.setCuserId(XmlTool.getAttributeValue(XmlTool.getElementAttribute("userid", element)));
            bean.setCusername(XmlTool.getAttributeValue(XmlTool.getElementAttribute("nickid", element)));
            bean.setSource(Integer.parseInt(XmlTool.getAttributeValue(XmlTool.getElementAttribute("source", element))));
            bean.setMobileNo(XmlTool.getAttributeValue(XmlTool.getElementAttribute("mobileno", element)));
            bean.setImobbind(Integer.parseInt(XmlTool.getAttributeValue(XmlTool.getElementAttribute("mobbind", element))));
            bean.setRealname(XmlTool.getAttributeValue(XmlTool.getElementAttribute("realname", element)));
            bean.setIdcard(XmlTool.getAttributeValue(XmlTool.getElementAttribute("idcard", element)));
        }
        return bean;
    }

    /**
     * 旧版修改昵称(针对user)
     *
     * @param bean
     */
    public BoltResult chgNickName(User bean) {
        BoltResult boltResult = new BoltResult();
        logger.info("修改用户昵称为:{}", bean.getUid());
        int num = userMapperOld.query_user_cnickid(bean.getUid());
        if (num > 0) {
            boltResult.setCode(String.valueOf(1000));
            boltResult.setDesc("昵称已存在");
        } else {
            int ret = userMapperOld.update_user_cnickid(new Date(), bean.getUid(), bean.getCuserId());
            if (ret == 1) {
                boltResult.setCode(String.valueOf(1));
                boltResult.setDesc("修改昵称成功");
                logger.info("修改昵称成功:uid:{}", bean.getCuserId());
            } else {
                boltResult.setCode(String.valueOf(0));
                boltResult.setDesc("修改昵称失败");
                logger.info("修改昵称失败:uid:{}", bean.getCuserId());
            }
        }
        return boltResult;
    }

    /**
     * 根据accessToken、appId查询token信息
     *
     * @param hskUserBean
     * @return
     */
    public TokenDto queryToken(HskUserBean hskUserBean) {
        return userMapper.queryToken(hskUserBean);
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
     * 用户绑定头像
     *
     * @param hskUserBean
     * @return
     */
    public int bindIcon(HskUserBean hskUserBean) {
        try {
            int count = userMapper.bindIcon(hskUserBean);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传头像错误", e);
        }
        return 0;
    }

    /**
     * 新版修改昵称(针对HskUserBean)
     *
     * @param hskUserBean
     * @return
     */
    public BoltResult chgNickNameNew(HskUserBean hskUserBean) {
        BoltResult boltResult = new BoltResult();
        logger.info("修改用户昵称为:{}", hskUserBean.getUid());
        int num = userMapperOld.query_user_cnickid(hskUserBean.getUid());
        if (num > 0) {
            boltResult.setCode("1000");
            boltResult.setDesc("昵称已存在");
        } else {
            int ret = userMapperOld.update_user_cnickid(new Date(), hskUserBean.getUid(), hskUserBean.getCuserId());
            if (ret == 1) {
                boltResult.setCode("1");
                boltResult.setDesc("修改昵称成功");
                logger.info("修改昵称成功:uid:{}", hskUserBean.getCuserId());
            } else {
                boltResult.setCode("0");
                boltResult.setDesc("修改昵称失败");
                logger.info("修改昵称失败:uid:{}", hskUserBean.getCuserId());
            }
        }
        return boltResult;
    }
}
