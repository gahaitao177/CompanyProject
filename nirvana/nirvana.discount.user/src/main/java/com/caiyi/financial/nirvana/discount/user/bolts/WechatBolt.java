package com.caiyi.financial.nirvana.discount.user.bolts;


import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.user.bean.WeChatBean;
import com.caiyi.financial.nirvana.discount.user.service.UserService;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by dengh on 2016/7/22.
 */

@Bolt(boltId = "WechatUser", parallelismHint = 1, numTasks = 1)
public class WechatBolt  extends BaseBolt{

    @Autowired
    private UserService userService;

    @Override
    protected void _prepare(Map map, TopologyContext topologyContext) {
    }
    ///**
    // * 原来惠刷卡微信登录，现在用9188微信登录
    // * @param bean
    // */
    //@BoltController
    //public WeChatBean weChatLogin(WeChatBean bean) throws Exception {
    //    userService.weChatLogin(bean);
    //    return bean;
    //}



}
