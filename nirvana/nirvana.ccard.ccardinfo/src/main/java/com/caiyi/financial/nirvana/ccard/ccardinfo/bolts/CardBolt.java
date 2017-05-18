package com.caiyi.financial.nirvana.ccard.ccardinfo.bolts;


import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.CardDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.service.CardService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


/**
 * Created by terry on 2016/5/25.
 */
@Bolt(boltId = "card", parallelismHint = 1, numTasks = 1)
public class CardBolt extends BaseBolt {
    @Autowired
    CardService cardService;

    @Override
    protected void _prepare(Map map, TopologyContext topologyContext) {
//        cardService = getBean(CardService.class);
//        manualCardService=getBean(ManualCardService.class);
    }
    @BoltController
    public Map<String,List<Map<String,Object>>> queryCardIndex(Card card){
        logger.info("---------------Cardbolt  queryCardIndex----");
        return cardService.queryCardIndex(card);
    }

    @BoltController
    public JSONObject queryCardIndex2(Card card){
        logger.info("---------------Cardbolt  queryCardIndex2----");
        return cardService.queryCardIndex2(card);
    }

    @BoltController
    public Map<String,List<Map<String,Object>>> queryFilterCondition(Card card){
        logger.info("---------------Cardbolt  queryFilterCondition----");
        return cardService.queryFilterCondition(card);
    }

    @BoltController
    public JSONObject queryFilterCondition2(Card card){
        logger.info("---------------Cardbolt  queryFilterCondition----");
        return cardService.queryFilterCondition2(card);
    }

    @BoltController
    public List<CardDto> queryFilterCard(Card card){
        logger.info("---------------Cardbolt  queryFilterCard----");
        return  cardService.queryFilterCard(card);
    }

    @BoltController
    public List<CardDto> queryFilterCard2(Card card){
        logger.info("---------------Cardbolt  queryFilterCard----");
        return cardService.queryFilterCard(card);
    }

    @BoltController
    public  Map<String,List<Map<String,Object>>> queryCardDetail(Card card){
        logger.info("---------------Cardbolt  queryCardDetail----");
        return  cardService.queryCardDetail(card);
    }

    @BoltController
    public JSONObject queryCardDetail2(Card card){
        logger.info("---------------Cardbolt  queryCardDetail2----");
        return cardService.queryCardDetail2(card);

    }
    /**
     *
     * @param card
     * @return
     */
    @BoltController
    public List<CardDto> queryCardForTotalSearch(Card card){
        logger.info("---------------Cardbolt  queryCardForTotalSearch----");
        return  cardService.queryCardForTotalSearch(card);
    }
}
