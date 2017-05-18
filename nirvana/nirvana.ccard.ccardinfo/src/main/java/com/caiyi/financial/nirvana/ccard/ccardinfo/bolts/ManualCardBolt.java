package com.caiyi.financial.nirvana.ccard.ccardinfo.bolts;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.ChannelDao;
import com.caiyi.financial.nirvana.ccard.ccardinfo.service.ManualCardService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/11/21. 人工办卡bolt
 */
@Bolt(boltId = "manualCard",parallelismHint = 2,numTasks = 4)
public class ManualCardBolt extends BaseBolt {
    @Autowired
    ManualCardService manualCardService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }
    @BoltController
    public  Map<String,String> cardApplySendYZM(Card card){
        logger.info("---------------ManualCardBolt  cardApplySendYZM----");
        return  manualCardService.cardApplySendYZM(card);
    }
    @BoltController
    public  Map<String,String> checkYZM(Card card){
        logger.info("---------------ManualCardBolt  checkYZM----");
        return  manualCardService.checkYZM(card);
    }
    @BoltController
    public  Map<String,String> cardAppliedInfo(Card card){
        logger.info("---------------ManualCardBolt  cardAppliedInfo----");
        return  manualCardService.cardAppliedInfo(card);
    }
    @BoltController
    public List<Map<String,String>> queryUserBank(Card card){
        logger.info("---------------ManualCardBolt  queryUserBank----");
        return  manualCardService.queryUserBank(card);
    }

    @BoltController
    public  Map<String,String> applyProgressYzm(Card card){
        logger.info("---------------ManualCardBolt  applyProgressYzm----");
        return  manualCardService.applyProgressYzm(card);
    }

    @BoltController
    public List<Map<String,String>> queryProgressOfCard(String phonenum){
        logger.info("---------------ManualCardBolt  queryProgressOfCard----");
        return  manualCardService.queryProgressOfCard(phonenum);
    }
    @BoltController
    public Map<String,String> queryDetailProgressOfCard(String iapplyid){
        logger.info("---------------ManualCardBolt  queryDetailProgressOfCard----");
        return  manualCardService.queryDetailProgressOfCard(iapplyid);
    }
    @BoltController
    public Map<String,String> updateCardApplyCounts(String cardid){
        logger.info("---------------ManualCardBolt  updateCardApplyCounts----");
        return  manualCardService.updateCardApplyCounts(cardid);
    }
    @BoltController
    public List<ChannelDao> queryChannelContend(String channelId){
        logger.info("---------------ManualCardBolt  updateCardApplyCounts----");
        return  manualCardService.queryChannelContend(channelId);
    }
}
