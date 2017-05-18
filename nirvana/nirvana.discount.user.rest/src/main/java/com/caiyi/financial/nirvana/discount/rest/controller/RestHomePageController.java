package com.caiyi.financial.nirvana.discount.rest.controller;

import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.user.bean.HomePageBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by wenshiliang on 2016/8/31.
 */
@RestController
public class RestHomePageController {


    @Resource(name= Constant.HSK_USER)
    private IDrpcClient drpcClient;

    @RequestMapping("/notcontrol/user/serviceBanner.go")
    public String serviceBanner(HomePageBean bean){
        String str =  drpcClient.execute(new DrpcRequest("HomePageBolt","serviceBanner",bean));
        return str;
    }

    @RequestMapping("/notcontrol/user/linesPromotion.go")
    public String linesPromotion(HomePageBean bean){
        String str =  drpcClient.execute(new DrpcRequest("HomePageBolt","linesPromotion",bean));
        return str;
    }
}
