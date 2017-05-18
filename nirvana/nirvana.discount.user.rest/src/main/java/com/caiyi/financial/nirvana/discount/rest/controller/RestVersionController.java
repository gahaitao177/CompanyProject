package com.caiyi.financial.nirvana.discount.rest.controller;

import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.user.bean.VersionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by wenshiliang on 2016/9/12.
 */
@RestController
public class RestVersionController {
    private static Logger logger = LoggerFactory.getLogger(RestVersionController.class);

    @Resource(name = Constant.HSK_USER)
    private IDrpcClient client;


    @RequestMapping("/notcontrol/user/queryVersion.go")
    public String queryVersion(VersionBean bean){
//        bean.setAppMgr(1);
        return client.execute(new DrpcRequest("VersionBolt","queryVersion",bean));
    }
}
