package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.TestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lizhijie on 2016/11/18.
 */
public class CardServiceTest extends TestSupport {
    @Autowired
    CardService cardService;
    @Autowired
    ManualCardService manualCardService;
    /**
     * 测试 queryCardIndex 服务
     * @throws Exception
     */
//    @Test
//    public  void  queryCardIndex() throws  Exception{
//        Card card=new Card();
//        card.setIbankids("1,2,3,4,10");
//        card.setCityid("101,102");
//        System.out.println("queryCardIndex:"+cardService.queryCardIndex(card));
////        Assert.assertArrayEquals();
//    }

    /**
     * 查询办卡进度
     * @throws Exception
     */
//    @Test
//    public  void  queryCardProgress() throws  Exception{
//        System.out.println("queryCardIndex:"+manualCardService.queryProgressOfCard("13765669410"));
//    }
//    /**
//     * 查询办卡进度详情
//     * @throws Exception
//     */
//    @Test
//    public  void  queryCardProgressDetail() throws  Exception{
//        System.out.println("queryCardIndex:"+manualCardService.queryDetailProgressOfCard("1411")); //1411   //1424
//    }

    /**
     * 查询渠道内容
     * @throws Exception
     */
    @Test
    public  void  queryChannelContend() throws  Exception{
        System.out.println("queryChannelContend:"+ JSON.toJSON(manualCardService.queryChannelContend("1")));
    }
}
