package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.RecommendCardBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lizhijie on 2017/1/11.
 */
public class RecommendCardTest extends TestSupport {
    @Autowired
    private  RecommendCardService cardService;

    @Test
    public  void queryRecommendCardsTest(){
        RecommendCardBean cardBean=new RecommendCardBean();
        cardBean.setAdcode("310100");
//        cardBean.setPs(2);
//        cardBean.setPn(2);
        System.out.println("推荐卡列表:"+ JSON.toJSON(cardService.queryRecommendCards(cardBean)));
    } 

    @Test
    public  void queryRecommendCardDetailTest(){
        System.out.println("推荐卡详情:"+ JSON.toJSON(cardService.queryRecommendCardDetail("2222")));
    }

    @Test
    public  void updateClickCountTest(){
        System.out.println("更新点击量:"+ JSON.toJSON(cardService.updateClickCount(3)));
    }
}
