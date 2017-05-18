package com.caiyi.financial.nirvana.discount.ccard.service;

import com.caiyi.financial.nirvana.discount.ccard.bean.Commodity;
import com.caiyi.financial.nirvana.discount.ccard.core.TestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lizhijie on 2017/1/13.
 */
public class CommodityServiceTest extends TestSupport {
    @Autowired
    CommodityService commodityService;

    /**
     * 积分列表
     */
    @Test
    public void queryPointsList(){
        Commodity commodity =new Commodity();
        commodity.setIbankid("13");
        System.out.println("list:"+commodityService.queryPointsList(commodity));
    }
}
