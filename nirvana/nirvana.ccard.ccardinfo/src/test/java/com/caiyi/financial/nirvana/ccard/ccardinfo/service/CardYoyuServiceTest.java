package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.alibaba.fastjson.JSON;
import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by lichuanshun on 16/12/23.
 */
public class CardYoyuServiceTest extends TestSupport {
    @Autowired
    CardYoyuService cardService;

    @Test
    public void queryNewsCollect() throws Exception {
        Card card = new Card();
        card.setCuserId("cc4a0768139");
        card.setNewsType("xyk");
        card.setPn(1);
        card.setPs(10);
        System.out.println(JSON.toJSONString(cardService.queryNewsCollect(card)));

    }

    @Test
    public void getNews() throws Exception {
        Card card = new Card();
        card.setCuserId("cc4a0768139");
        card.setNewsType("xyk");
        card.setPn(1);
        card.setPs(10);
        System.out.println(JSON.toJSONString(cardService.queryNewsPage(card)));

    }

    @Test
    public void newsCollect() throws Exception {
        Card card = new Card();
        card.setCuserId("cc4a0768139");
        card.setNewsId("80006");
        card.setFunc("add");
        System.out.println(JSON.toJSONString(cardService.newsCollect(card)));
    }
    @Test
    public void newApplyIndex() throws Exception {
        Card card = new Card();
        card.setCuserId("testlcs");
        card.setNewsId("80008,2222");
        card.setHskcityid("101");
        System.out.println(JSON.toJSONString(cardService.cardApplyIndex(card)));
    }

    @Test
    public void delNewsCollect() throws Exception {
        Card card = new Card();
        card.setCuserId("cc4a0768139");
        card.setNewsId("80006,80007");
//        card.setFunc("add");
        System.out.println(JSON.toJSONString(cardService.newsCollect(card)));
    }

}