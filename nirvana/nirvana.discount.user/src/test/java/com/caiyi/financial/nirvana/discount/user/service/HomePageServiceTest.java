package com.caiyi.financial.nirvana.discount.user.service;

import com.alibaba.fastjson.JSONArray;
import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.discount.user.bean.HomePageBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wenshiliang on 2016/8/31.
 */
public class HomePageServiceTest extends TestSupport {


    @Autowired
    private HomePageService homePageService;

    @Test
    public void testServiceBanner() throws Exception {
        HomePageBean bean = new HomePageBean();
        bean.setAdcode("150100");

        bean.setHomePageType("LINES_PROMOTION");
        bean.setPageNum(1);
        bean.setPageSize(1);
        JSONArray array =  homePageService.selectHomePage(bean);
        logger.info(array.toJSONString());

    }
}