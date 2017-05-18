package com.caiyi.financial.nirvana.discount.ccard.service;

import com.caiyi.financial.nirvana.core.util.JsonUtil;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.core.TestSupport;
import com.caiyi.financial.nirvana.discount.ccard.dto.CheapDetailDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wenshiliang on 2016/5/6.
 */
public class StoreServiceTest extends TestSupport {

    @Autowired
    private StoreService storeService;

    @Test

    public void testCheap() throws Exception {
        Cheap cheap = new Cheap();
//        cheap.setIstoreid("80286");
        cheap.setIcityid(101l);
        cheap.setIbusinessid("23001");

        CheapDetailDto dto = storeService.cheap(cheap);
        System.out.println("-------------------------------");
        System.out.println(JsonUtil.toJSONString(dto));
        System.out.println("-------------------------------");
    }
}