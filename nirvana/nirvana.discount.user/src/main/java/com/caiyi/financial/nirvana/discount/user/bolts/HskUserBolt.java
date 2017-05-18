package com.caiyi.financial.nirvana.discount.user.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.discount.user.bean.HskUserBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.bean.WeChatBean;
import com.caiyi.financial.nirvana.discount.user.bean.weChatUnionid;
import com.caiyi.financial.nirvana.discount.user.dto.HskUserDto;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import com.caiyi.financial.nirvana.discount.user.service.HskUserService;
import com.caiyi.financial.nirvana.discount.user.service.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lizhijie on 2017/2/8.
 */
@Bolt(boltId = "hskUser", parallelismHint = 1, numTasks = 1)
public class HskUserBolt extends BaseBolt {

    @Autowired
    private HskUserService userService;
    @Autowired
    private WeChatService weChatService;

    /**
     * 保存注册信息
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @BoltController
    public BoltResult saveUserInfo9188(User bean) throws Exception {
        String pwd9188 = MD5Util.compute(bean.getPwd() + User.MD5_KEY_9188);//9188加密的密码
        bean.setPwd9188(pwd9188);
        bean.setPwd(MD5Util.compute(bean.getPwd() + User.MD5_KEY));
        int exist = userService.queryUser(bean);
        if (exist > 0) {
            return new BoltResult(BoltResult.SUCCESS, "注册信息已存在!");
        }
        int count = userService.saveUserInfo(bean);
        if (count > 0) {
            return new BoltResult(BoltResult.SUCCESS, "保存注册信息成功!");
        } else {
            return new BoltResult(BoltResult.ERROR, "保存注册信息失败!");
        }
    }

    /**
     * 保存注册用户来源
     *
     * @param user
     * @return
     */
    @BoltController
    public BoltResult saveUserSource(User user) {
        int count = userService.saveUserSource(user);
        if (count > 0) {
            return new BoltResult("1", "保存注册用户来源成功");
        } else {
            return new BoltResult("0", "保存注册用户来源失败");
        }
    }

    /**
     * 旧版修改昵称(针对旧版user)
     *
     * @param bean
     * @return
     */
    @BoltController
    public BoltResult chgNickName(User bean) {
        BoltResult boltResult;
        if (StringUtils.isEmpty(bean.getUid())) {
            boltResult = new BoltResult();
            boltResult.setDesc("昵称不能为空");
            boltResult.setCode(String.valueOf(0));
            return boltResult;
        }
        return userService.chgNickName(bean);
    }

    /**
     * 查询微信unionId对象
     *
     * @param unionId
     * @return
     */
    @BoltController
    public weChatUnionid queryWeChatUnionId(String unionId) {
        return weChatService.queryWeChatUnionId(unionId);
    }

    /**
     * 惠刷卡微信用户注册
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @BoltController
    public HskUserDto weChatRegister(WeChatBean bean) throws Exception {
        return weChatService.weChatRegister(bean);
    }

    /**
     * 根据accessToken、appId查询token信息
     *
     * @param hskUserBean
     * @return
     */
    @BoltController
    public TokenDto queryToken(HskUserBean hskUserBean) {
        return userService.queryToken(hskUserBean);
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
     * 用户绑定头像地址
     *
     * @param hskUserBean
     * @return
     */
    @BoltController
    public BoltResult bindIcon(HskUserBean hskUserBean) {
        BoltResult boltResult = new BoltResult("1", "上传成功");
        logger.info("hskUserBean icon:{}", hskUserBean.getIcon());
        try {
            int count = userService.bindIcon(hskUserBean);
            if (count != 1) {
                boltResult.setCode("1000");
                boltResult.setDesc("用户上传头像失败");
                logger.info("用户上传头像失败");
                return boltResult;
            }
        } catch (Exception e) {
            boltResult.setCode("-1");
            boltResult.setDesc("绑定头像发生异常");
            logger.info(e.getMessage(), e);
        }
        return boltResult;
    }

    /**
     * 新版修改昵称(针对新版HskUserBean)
     *
     * @param hskUserBean
     * @return
     */
    @BoltController
    public BoltResult chgNickNameNew(HskUserBean hskUserBean) {
        BoltResult boltResult;
        if (CheckUtil.isNullString(hskUserBean.getUid())) {
            return new BoltResult("0", "昵称不能为空");
        }
        return userService.chgNickNameNew(hskUserBean);
    }


    /**
     * 保存注册信息
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @BoltController
    public BoltResult saveUserInfo9188New(User bean) throws Exception {
        BoltResult result = new BoltResult("1", "保存注册信息成功!");
        //9188加密的密码
        String pwd9188 = MD5Util.compute(bean.getPwd() + User.MD5_KEY_9188);
        bean.setPwd9188(pwd9188);
        bean.setPwd(MD5Util.compute(bean.getPwd() + User.MD5_KEY));
        int exist = userService.queryUser(bean);
        if (exist > 0) {
            logger.info("saveUserInfo9188New:注册信息已存在");
            result.setCode("1");
            result.setDesc("注册信息已存在!");
            return result;
        }
        //没有查询到用户信息，添加新的注册信息
        int count = userService.saveUserInfo(bean);
        if (count > 0) {
            //注册信息保存成功
            logger.info("saveUserInfo9188New:保存注册信息成功");
            // 保存用户来源
            int sourceCount = userService.saveUserSource(bean);
            if (sourceCount > 0) {
                logger.info("saveUserInfo9188New:保存用户来源成功");
            } else {
                logger.info("saveUserInfo9188New:保存用户来源失败");
            }
        } else {
            logger.info("saveUserInfo9188New:保存注册信息失败");
            result.setCode("-1");
            result.setDesc("保存注册信息失败!");
        }
        return result;
    }


}
