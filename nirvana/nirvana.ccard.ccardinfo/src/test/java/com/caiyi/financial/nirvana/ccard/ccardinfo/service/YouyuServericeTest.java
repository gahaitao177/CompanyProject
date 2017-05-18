package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lichuanshun on 16/12/8.
 */
public class YouyuServericeTest extends TestSupport {
    @Autowired
    CardYoyuService cardYoyuService;

    @Test
    public void queryTopCardsTest(){
        Card card = new Card();
        card.setHskcityid("101");
        System.out.println("---------------");
        System.out.println(cardYoyuService.queryTopTenCards(card));
    }

    @Test
    public void queryNewHomeIndexTest(){
        Card card = new Card();
        card.setHskcityid("101");
        card.setAdcode("150100");
        System.out.println("---------------");
        System.out.println(cardYoyuService.queryNewHomeIndex(card));
    }

    @Test
    public void isCollectionTest(){
        Card card = new Card();
        card.setCuserId("cc4a0768139");
        card.setNewsId("80006");
        card.setFunc("add");
        BoltResult result = cardYoyuService.newsCollect(card);
        System.out.println(JSONObject.toJSON(result));
    }

    @Test
    public void getNews() {
        Card card = new Card();
        card.setCuserId("cc4a0768139");
        card.setNewsType("xyk");
        BoltResult result = cardYoyuService.queryNewsPage(card);
        System.out.println(JSONObject.toJSON(result));
    }
}
