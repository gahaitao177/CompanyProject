package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.RankCard;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.YouYuNewsDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.mapper.CardMapper;
import com.caiyi.financial.nirvana.ccard.ccardinfo.mapper.CardYoyuMapper;
import com.caiyi.financial.nirvana.ccard.ccardinfo.mapper.YouyuNewsMapper;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lichuanshun on 16/10/20.
 */
@Service
public class CardYoyuService extends AbstractService {
    @Autowired
    CardYoyuMapper cardYoyuMapper;
    @Autowired
    CardMapper cardMapper;
    @Autowired
    YouyuNewsMapper youyuNewsMapper;
    // 打开指定卡片
    private static final String OPEN_ONE_CARD_TYPE = "8";
    // 打开链接
    private static final String OPEN_LINK_TYPE = "0";


    /**
     * 有鱼金融首页
     * @since 有鱼金融第一次迭代 20161209
     * @param card
     * @return
     */
    public JSONObject queryYoyuHomePage(Card card){
        JSONObject homePage = new JSONObject();
        homePage.put("code","1");
        homePage.put("desc","success");
        JSONObject data = new JSONObject();
        data.put("banner",getBannerInfos(card.getAdcode(),"YOUYUBANNER"));
        data.put("hotCard",queryTopOneCard(card.getHskcityid()));
        homePage.put("data", data);
        return homePage;
    }

    /**
     * 人气卡片
     * @param card
     * @return
     */
    public JSONObject queryNewHomeIndex(Card card){
        JSONObject indexJson = new JSONObject();

        // 快速入口4个
        JSONArray quickArr = new JSONArray();
        // 人气榜
        JSONObject topOne = new JSONObject();

        // 福利场
        JSONArray welfareArr = new JSONArray();
        try {
            String uid = card.getCuserId();
            String adcode = card.getAdcode();
            quickArr = getBannerInfos(adcode,"QUICK14");
            topOne = queryTopOneCard(card.getHskcityid());

            // 福利场
            List<Map<String,String>> welfares = null;

            if(!CheckUtil.isNullString(uid)){
                List<Integer> bankIds = cardYoyuMapper.queryBankIdByUserId(uid);
                //用户登录并关注了银行
                if (bankIds != null && bankIds.size() > 0){
                    welfares = cardYoyuMapper.queryWelfare2(adcode,bankIds);
                }  else {
                    welfares = cardYoyuMapper.queryWelfare(adcode);
                    logger.info("----------------welfares:"+welfares);
                }
            } else {
                //用户未登录或用户登录但没有关注的银行
                welfares = cardYoyuMapper.queryWelfare(adcode);
                logger.info("----------------welfares:"+welfares);
            }

            if (welfares != null && welfares.size() >0){
                for (Map<String,String> welfareMap : welfares){
                    JSONObject tempWel = new JSONObject();
                    tempWel.put("title",welfareMap.get("TITLE"));
                    tempWel.put("content",welfareMap.get("CONTENT"));
                    tempWel.put("picUrl",welfareMap.get("PICURL"));
                    tempWel.put("webUrl",welfareMap.get("WEBURL"));
                    welfareArr.add(tempWel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        indexJson.put("quick", quickArr);
        indexJson.put("topCard", topOne);
        indexJson.put("welfare", welfareArr);
        return indexJson;
    }


    private JSONArray getBannerInfos(String adcode,String type){
        JSONArray banners = new JSONArray();
        try {
            List<Map<String, Object>> quicks = cardYoyuMapper.queryQuickInfo("%" + adcode + "%",type);
            if (quicks != null && quicks.size() > 0){
                for (int index =0; index < quicks.size();index ++ ){
                    Map<String, Object> tempQuickMap = quicks.get(index);
                    JSONObject tempQuick = new JSONObject();
                    tempQuick.put("actionType", tempQuickMap.get("ACTION_TYPE"));
                    tempQuick.put("param01", tempQuickMap.get("PARAM01"));
                    tempQuick.put("param02", tempQuickMap.get("PARAM02"));
                    tempQuick.put("picUrl", tempQuickMap.get("PIC_URL"));
                    tempQuick.put("subTitle", tempQuickMap.get("SUB_TITLE"));
                    tempQuick.put("title", tempQuickMap.get("TITLE"));
                    banners.add(tempQuick);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("getBannerInfos", e);
        }
        return banners;
    }
    /**
     * 根据城市id获取单独一张卡
     * @param hskCityId
     * @return
     */
    public JSONObject queryTopOneCard(String hskCityId){
        // 人气榜
        JSONObject topOne = new JSONObject();
        List<RankCard> cards = cardYoyuMapper.queryTopOneCard(hskCityId);
        if (cards != null && cards.size() > 0){
            RankCard rankCardTemp = cards.get(0);
            topOne = JSONObject.parseObject(JSON.toJSONString(rankCardTemp));
            // add by lcs 20170221 start
            if (CheckUtil.isNullString(rankCardTemp.getCardUrl())){
                topOne.put("actionType", OPEN_ONE_CARD_TYPE);
                topOne.put("param01", rankCardTemp.getCardId());
            } else {
                topOne.put("actionType", OPEN_LINK_TYPE);
                topOne.put("param01", rankCardTemp.getCardUrl());
            }
            topOne.put("param02", "0");
            topOne.put("subTitle", rankCardTemp.getReason2());
            topOne.put("title", rankCardTemp.getReason1());
            // add by lcs 20170221 end
        }
        return topOne;
    }
    /**
     * 人气卡片
     * @param card
     * @return
     */
    public JSONObject queryTopTenCards(Card card){
        JSONObject topCards = new JSONObject();
        topCards.put("code","1");
        topCards.put("desc","成功");
        try {
            List<RankCard> cards = cardYoyuMapper.queryTopTenCards(card.getHskcityid());
            if(cards!=null&&cards.size()>0){
                topCards.put("code","1");
                topCards.put("desc","成功");
                topCards.put("data",cards);
            }else{
                topCards.put("code","0");
                topCards.put("desc","查询失败");
            }
        } catch (Exception e) {
            logger.error("topTenCards error", e);
            topCards.put("code","0");
            topCards.put("desc","查询失败");
        }
        return topCards;

    }


    /**
     *
     * @param card
     */
    public JSONObject cardApplyIndex(Card card){
        JSONObject indexJson = new JSONObject();
        try {
            // 银行列表
            if (CheckUtil.isNullString(card.getIcooperation())){
                card.setIcooperation("1");
            }
            List<Map<String, Object>> cityBankList = cardMapper.quryCardCity(card.getHskcityid(), card.getIcooperation(),"0");
            JSONArray bankArr = new JSONArray();
            if (cityBankList != null && cityBankList.size() > 0) {
                for (Map<String, Object> bank: cityBankList){
                    JSONObject tempBank = new JSONObject();
                    String bankId = bank.get("IBANKID").toString();
                    tempBank.put("bankId",bankId);
                    tempBank.put("bankName",bank.get("CBANKNAME"));
                    tempBank.put("bankIcon",getBankIconById(bankId));
                    bankArr.add(tempBank);
                }
            }
            indexJson.put("bankList",bankArr);
            JSONObject operation = new JSONObject();
            List<Map<String, Object>> quicks = cardYoyuMapper.queryQuickInfo("%" + card.getAdcode() + "%","QUICK13");
            if (quicks != null && quicks.size() > 0){
                Map<String, Object> tempQuick = quicks.get(0);
                operation.put("actionType", tempQuick.get("ACTION_TYPE"));
                operation.put("param01", tempQuick.get("PARAM01"));
                operation.put("param02", tempQuick.get("PARAM02"));
                operation.put("picUrl", tempQuick.get("PIC_URL"));
                operation.put("subTitle", tempQuick.get("SUB_TITLE"));
                operation.put("title", tempQuick.get("TITLE"));
            } else {
                operation.put("actionType", "0");
                operation.put("param01", "0");
                operation.put("param02", "0");
                operation.put("picUrl", "0");
                operation.put("subTitle", "0");
                operation.put("title", "0");
            }
            operation.put("type", "QUICK");
            indexJson.put("operation",operation);
            // 推荐卡片
            JSONArray subjectCards = new JSONArray();
            List<Map<String,String>> configInfos = cardYoyuMapper.queryCardRecommendConfig();
            JSONArray recommonedCardArr = new JSONArray();
            if (configInfos != null && configInfos.size() > 0) {
                for (Map<String, String> config : configInfos) {
                    //t.ctitle,t.csubtitle,t.cpicurl,t.icardid
                    String picUrl = config.get("CPICURL");
                    String ctitle = config.get("CTITLE");
                    String csubtitle = config.get("CSUBTITLE");
                    String icardid = config.get("ICARDID");
                    logger.info("config" + config);
                    icardid = "," + icardid + ",";
                    List<Map<String, String>> cardList = cardYoyuMapper.queryApplyCards(card.getHskcityid(), icardid);
                    JSONArray cardListTempArr = new JSONArray();
                    for (Map<String, String> cardTemp : cardList) {
                        //tbc.icardid,tbc.ccardname,tbc.ccardimg
                        String icardidTemp = String.valueOf(cardTemp.get("ICARDID"));
                        String ccardnameTemp = cardTemp.get("CCARDNAME");
                        String ccardimgTemp = cardTemp.get("CCARDIMG");
                        JSONObject cardTempOf = new JSONObject();
                        cardTempOf.put("icardid", icardidTemp);
                        cardTempOf.put("ccardname", ccardnameTemp);
                        cardTempOf.put("ccardimg", ccardimgTemp);
                        cardListTempArr.add(cardTempOf);
                    }
                    //
                    if (cardListTempArr != null && cardListTempArr.size() > 0) {
                        JSONObject sunbjectCardTemp = new JSONObject();
                        sunbjectCardTemp.put("picUrl", picUrl);
                        sunbjectCardTemp.put("cardList", cardListTempArr);
                        recommonedCardArr.add(sunbjectCardTemp);
                    }
                }
            }
            indexJson.put("subjectCards",recommonedCardArr);
        } catch (Exception e) {
            logger.error("CardYoyuService:cardApplyIndex:" , e);
            indexJson = new JSONObject();
        }
        return indexJson;
    }
    /**
     * 保存回调信息
     * @param card
     * @return
     */
    public int saveBankCallBack(Card card){
        try {
            Map<String,String> map = new  HashMap<>();
            map.put("bankid",card.getBankid());
            map.put("adid",card.getAdid());
            map.put("orderid",card.getOrderid());
            map.put("time",card.getTimestamp());
            map.put("state",card.getIstatus());
            logger.info("saveBankCallBack" + map);
            return cardYoyuMapper.saveBankCallBackInfo(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询回调信息
     * @param card
     * @return
     */
    public int queryCallBackInfo(Card card){
        try {
            Map<String,String> map = new  HashMap<>();
            map.put("orderid",card.getOrderid());
            map.put("bankid",card.getBankid());
            logger.info("queryCallBackInfo" + map);
            Map<String,Object> result = cardYoyuMapper.queryCallBackInfo(map);
            logger.info("queryCallBackInfo" + result);
            return result == null?0:Integer.valueOf(result.get("NUMS").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public JSONObject queryBankFlagByBankId(String bankId){
        JSONObject resutl= new JSONObject();
        Map<String,Object> map=null;
        try {
            map= cardYoyuMapper.getBankFlagByBankId(bankId);
        }catch (Exception e){
            resutl.put("code",-1);
            resutl.put("desc","程序异常");
            return resutl;
        }
        if (map!=null){
            resutl.put("code",1);
            resutl.put("desc","查询成功");
            resutl.put("data",map);
        }else {
            resutl.put("code",0);
            resutl.put("desc","没有查询到该银行");
        }
        return resutl;
    }

    /**
     * 根据银行id 获取图标
     * @param bankId
     * @return
     */
    private String getBankIconById(String bankId){
        String bankIcon = "";
        try {
            List<Map<String,Object>> icons = cardYoyuMapper.getBankIconById(bankId);
            if (icons != null && icons.size() > 0){
                Object icon = icons.get(0).get("CBANKICON");
                if (icon != null){
                    bankIcon = icon.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bankIcon;
    }

    /**
     * 有鱼金融资讯
     */
    public BoltResult queryNewsPage(Card card){
        BoltResult result = new BoltResult("1","success");
        if (card.getPn() == null){
            card.setPn(1);
        }
        if (card.getPs() == null){
            card.setPs(10);
        }
        //获取资讯操作
        Page<YouYuNewsDto> listNews = youyuNewsMapper.queryYouYuNews(card);
        JSONObject data = new JSONObject();
        data.put("newsList",listNews);
        data.put("pn",String.valueOf(listNews.getPageNum()));
        data.put("ps",String.valueOf(listNews.getPageSize()));
        data.put("tp",String.valueOf(listNews.getPages()));
        data.put("rc",String.valueOf(listNews.getTotal()));
        result.setData(data);
        return result;
    }

    /**
     * 资讯收藏接口
     * @param card
     * @return
     */
    public BoltResult newsCollect(Card card) {
        BoltResult result = new BoltResult("0","收藏失败");
        String newsid = card.getNewsId();
        String cuserid = card.getCuserId();
        logger.info(cuserid+ "," + newsid );
        if ("add".equals(card.getFunc())){
            int count = youyuNewsMapper.checkNewsCollect(cuserid, newsid);
            if (count == 0){
                int res = youyuNewsMapper.newsCollect(cuserid, newsid);
                logger.info("res:" + res);
                if (res == 1){
                    result.setCode("1");
                    result.setDesc("收藏成功");
                }
            }else {
                result.setCode("1");
                result.setDesc("收藏成功");
            }
        } else {
            result = new BoltResult("1","删除收藏成功");
            if (!CheckUtil.isNullString(newsid)){
                newsid = "," + newsid + ",";
                int res = youyuNewsMapper.delNewsCollect(cuserid, newsid);
                logger.info("res:" + res);
                }
        }
        return result;
    }

    /**
     * 资讯收藏接口
     * @param card
     * @return
     */
    public BoltResult queryNewsCollect(Card card) {
        if (card.getPn() == null){
            card.setPn(1);
        }
        if (card.getPs() == null){
            card.setPs(10);
        }
        BoltResult result = new BoltResult("1","查询成功");
        Page<YouYuNewsDto> listNews = youyuNewsMapper.queryNewsCollect(card);
        JSONObject data = new JSONObject();
        logger.info("getPages:" + listNews.getPages());
        data.put("newsList",listNews);
        data.put("pn",String.valueOf(listNews.getPageNum()));
        data.put("ps",String.valueOf(listNews.getPageSize()));
        data.put("tp",String.valueOf(listNews.getPages()));
        data.put("rc",String.valueOf(listNews.getTotal()));
        result.setData(data);
        return result;
    }

}

