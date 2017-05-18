package com.caiyi.financial.nirvana.ccard.ccardinfo.bolts;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.service.CardYoyuService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by lichuanshun on 16/10/20.
 */
@Bolt(boltId = "cardYouyu", parallelismHint = 1, numTasks = 1)
public class CardYouyuBolt extends BaseBolt {
    @Autowired
    CardYoyuService cardService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }

    /**
     * 有鱼金融首页
     * @since 有鱼金融第一次迭代 20161209
     * @param card
     * @return
     */
    @BoltController
    public JSONObject youYuHomePage(Card card){
        return cardService.queryYoyuHomePage(card);
    }

    /**
     * 新版本首页
     * @since 惠刷卡第十四次迭代
     * @param card
     * @return
     */
    @BoltController
    public JSONObject newHomeIndex(Card card){
        return cardService.queryNewHomeIndex(card);
    }
    /**
     * 查询人气卡 十张
     * @since 惠刷卡第十四次迭代
     * @param card
     * @return
     */
    @BoltController
    public JSONObject queryTopTenCards(Card card){
        return cardService.queryTopTenCards(card);
    }

    /**
     * 查询人气卡一张
     * @since 惠刷卡第十四次迭代
     * @param hskCityId 城市ID
     * @author lcs
     * @return
     *
     */
    @BoltController
    public JSONObject queryTopOneCard(String hskCityId){
        return cardService.queryTopOneCard(hskCityId);
    }

    /**
     * 新版本办卡首页
     * @param card
     * @return
     */
    @BoltController
    public JSONObject cardApplyIndex(Card card){
        JSONObject result = new JSONObject();
        logger.info("---------------CardYouyuBolt  carApplyIndex----" +  card.getHskcityid());
        result.put("data",cardService.cardApplyIndex(card));
        result.put("desc","success");
        result.put("code","1");
        return result;
    }

    /**
     * 保存
     * @param card
     * @return
     */
    @BoltController
    public JSONObject saveBankCallBack(Card card){
        JSONObject result = new JSONObject();
        logger.info("---------------CardYouyuBolt  saveBankCallBack----");
        result.put("code",cardService.saveBankCallBack(card));
        result.put("desc","success");
        return result;
    }

    /**
     * 按照用户标记查询
     * @param card
     * @return
     */
    @BoltController
    public int queryCallBackInfo(Card card){
        logger.info("---------------CardYouyuBolt  queryCallBackInfo----");
        return cardService.queryCallBackInfo(card);
    }

    /**
     * 查询银行功能 开启的标志
     * @param bankId
     * @return
     */
    @BoltController
    public JSONObject queryBankFlagByBankId(String bankId){
        logger.info("---------------CardYouyuBolt  queryBankFlagByBankId---");
        return  cardService.queryBankFlagByBankId(bankId);
    }

    /**
     * 有鱼金融资讯收藏接口
     * @param card
     * @return
     */
    @BoltController
    public BoltResult newsCollection(Card card){
        logger.info("---------------CardYouyuBolt  newsCollection---");
        return cardService.newsCollect(card);
    }

    /**
     * 有鱼金融资讯——
     * @param card
     * @return
     */
    @BoltController
    public BoltResult queryNewsPage(Card card){
        logger.info("---------------CardYouyuBolt  newsCollection---");
        return cardService.queryNewsPage(card);
    }

    /**
     * 有鱼金融资讯——查询收藏列表
     * @param card
     * @return
     */
    @BoltController
    public BoltResult queryNewsCollect(Card card){
        logger.info("---------------CardYouyuBolt  newsCollection---");
        return cardService.queryNewsCollect(card);
    }
}
