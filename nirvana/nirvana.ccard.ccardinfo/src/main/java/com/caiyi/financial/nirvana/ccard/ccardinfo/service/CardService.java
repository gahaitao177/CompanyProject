package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.BankCardDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.CardDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.mapper.CardMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by lizhijie on 2016/6/20.
 */
@Service
public class CardService extends AbstractService {
    @Autowired
    CardMapper cardMapper;

    public Map<String, List<Map<String, Object>>> queryCardIndex(Card card) {
        Map<String, List<Map<String, Object>>> resultList = new HashMap<>();

        List<Map<String, Object>> allnormal;
        allnormal = cardMapper.queryMaterial();
        if (allnormal != null) {
            resultList.put("allnormal", allnormal);
        }
        if (StringUtils.isEmpty(card.getIcooperation())) {
            card.setIcooperation("1");
        }
        List<Map<String, Object>> cityBankList;
        cityBankList = cardMapper.quryCardCity(card.getCityid(), card.getIcooperation(), ifFilterBankByIos(card));
        if (cityBankList != null) {
            resultList.put("BankList", cityBankList);
        }
        List<Map<String, Object>> articalList;
        articalList = cardMapper.queryArticleCount();
        if (articalList != null) {
            resultList.put("ArticalList", articalList);
        }
        List<Map<String, Object>> cardList;
        cardList = cardMapper.queryCityCardInfo(card.getCityid(), ifFilterBankByIos(card));
        if (cardList != null) {
            resultList.put("CardList", cardList);
        }
        String cuserid = card.getCuserId();
        List<Map<String, Object>> CollectCardList;
        CollectCardList = cardMapper.queryCardFocusInfo(cuserid);
        if (CollectCardList != null) {
            resultList.put("CollectCardList", CollectCardList);
        }
//        HashMap<String, String> maps = new HashMap<>();
//        String sqlwhere = "";
//        String ibanks = card.getIbankids();// 银行ids
//        if (!StringUtils.isEmpty(ibanks)) {
//            String[] ibs = ibanks.split(",");
//            sqlwhere = " and  t1.ibankid in ( ";
//            for (String i : ibs) {
//                sqlwhere += i + " ,";
//            }
//            if (sqlwhere.trim().endsWith(",")) {
//                sqlwhere = sqlwhere.substring(0, sqlwhere.length() - 1);
//            }
//            sqlwhere += " )";
//        }
//        String cityid = card.getCityid();
//        if (!StringUtils.isEmpty(cityid)) {
//            String[] ics = cityid.split(",");
//            sqlwhere += " and t1.icityid in  ( ";
//            for (String i : ics) {
//                sqlwhere += i + ",";
//            }
//            if (sqlwhere.trim().endsWith(",")) {
//                sqlwhere = sqlwhere.substring(0, sqlwhere.length() - 1);
//            }
//            sqlwhere += " )";
//        }
//        maps.put("order", "order by cpubdate desc, imsgid ");
//        maps.put("sqlwhere", sqlwhere);

        int ipraise = 0;
        int icollect = 0;
        List<Map<String, Object>> wechatMsgList;
        String[] bankidsP = null;
        if (StringUtils.isNotEmpty(card.getIbankids())) {
            bankidsP = card.getIbankids().split(",");
        }
        String[] citysP = null;
        if (StringUtils.isNotEmpty(card.getCityid())) {
            citysP = card.getCityid().split(",");
        }
        wechatMsgList = cardMapper.queryWechatMsg(bankidsP, citysP);
        if (wechatMsgList != null) {
            for (Map<String, Object> map : wechatMsgList) {
                if (StringUtils.isNotEmpty(cuserid) && map.get("imsgid") != null) {
                    int collection = cardMapper.queryCollectScoll(map.get("imsgid").toString(), cuserid);
                    if (collection > 0) {
                        icollect = 1;
                    }
                    int praise = cardMapper.querySpraise(map.get("imsgid").toString(), cuserid);
                    if (praise > 0) {
                        ipraise = 1;
                    }
                }
                map.put("ipraise", ipraise + "");
                map.put("icollect", icollect + "");
            }
            resultList.put("wechatMsg", wechatMsgList);
        }
        return resultList;
    }

    /**
     * 查询办卡首页（修改为json格式）
     * Updated by shaoqinghua in 20170111
     *
     * @param card
     * @return
     */
    public JSONObject queryCardIndex2(Card card) {
        JSONObject resultJson = new JSONObject();
        String cuserId = card.getCuserId();
        String ibankIds = card.getIbankids();
        String cityIds = card.getCityid();

        List<Map<String, Object>> allnormal;
        allnormal = cardMapper.queryMaterial();
        if (allnormal != null) {
            resultJson.put("allnormal", allnormal);
        }
        if (StringUtils.isEmpty(card.getIcooperation())) {
            card.setIcooperation("1");
        }
        //查询城市银行列表
        List<Map<String, Object>> cityBankList;
        cityBankList = cardMapper.quryCardCity(cityIds, card.getIcooperation(), ifFilterBankByIos(card));
        if (cityBankList != null) {
            resultJson.put("BankList", cityBankList);
        }
        //查询卡神攻略
        List<Map<String, Object>> articalList;
        articalList = cardMapper.queryArticleCount();
        if (articalList != null) {
            resultJson.put("ArticalList", articalList);
        }
        //查询卡片列表
        List<Map<String, Object>> cardList;
        cardList = cardMapper.queryCityCardInfo(cityIds, ifFilterBankByIos(card));
        if (cardList != null) {
            resultJson.put("CardList", cardList);
        }
        List<Map<String, Object>> CollectCardList;
        CollectCardList = cardMapper.queryCardFocusInfo(cuserId);
        if (CollectCardList != null) {
            resultJson.put("CollectCardList", CollectCardList);
        }

        int ipraise = 0;
        int icollect = 0;
        List<Map<String, Object>> wechatMsgList;
        String[] bankIdArray = null;
        String[] cityIdArray = null;
        if (!CheckUtil.isNullString(ibankIds)) {
            bankIdArray = ibankIds.split(",");
        }
        if (!CheckUtil.isNullString(cityIds)) {
            cityIdArray = cityIds.split(",");
        }

        //查询微信文章列表
        wechatMsgList = cardMapper.queryWechatMsg(bankIdArray, cityIdArray);
        if (wechatMsgList != null && wechatMsgList.size() > 0) {
            for (Map<String, Object> map : wechatMsgList) {
                if (!CheckUtil.isNullString(cuserId) && map.get("imsgid") != null) {
                    int collection = cardMapper.queryCollectScoll(map.get("imsgid").toString(), cuserId);
                    if (collection > 0) {
                        icollect = 1;
                    }
                    int praise = cardMapper.querySpraise(map.get("imsgid").toString(), cuserId);
                    if (praise > 0) {
                        ipraise = 1;
                    }
                }
                map.put("ipraise", ipraise + "");
                map.put("icollect", icollect + "");
            }
            resultJson.put("wechatMsg", wechatMsgList);
        }
        return resultJson;
    }


    /**
     * @return 不做筛选
     */
    private String ifFilterBankByIos(Card card) {
        String ifFilterIos = "1";
        logger.info("start iclient:" + card.getIclient() + ",version:" + card.getAppVersion() + ",ifFilterIos=" +
                ifFilterIos);
//        if (CheckUtil.isNullString(card.getAppVersion())){
//            return "1";
//        }
//        if (1 == card.getIclient() && !CheckUtil.isNullString(card.getAppVersion())) {
//            logger.info("111");
//            try {
//                if (Integer.valueOf(card.getAppVersion()) < 290) {
//                    logger.info("<<<");
//                    ifFilterIos = "1";
//                } else {
//                    logger.info(">>>===");
//                }
//            } catch (Exception e) {
//                logger.error("ifFilterBankByIos", e);
//            }
//        }
//        logger.info("ifFilterBankByIos end" + ifFilterIos);
        return ifFilterIos;
    }


    /**
     * @param card
     * @return 筛选过滤条件
     */
    public Map<String, List<Map<String, Object>>> queryFilterCondition(Card card) {
        Map<String, List<Map<String, Object>>> rList = new HashMap<>();

        List<Map<String, Object>> bankList;
        //如果是ios  并且版本号大于280
        String ifFilterIos = ifFilterBankByIos(card);
        if (StringUtils.isEmpty(card.getIcooperation())) {
            bankList = cardMapper.queryCityCardOld(card.getCityid(), ifFilterIos);
        } else {
            bankList = cardMapper.queryCityCardInfo(card.getCityid(), ifFilterIos);
        }
        logger.info("bankList:" + bankList.size());
        if (bankList != null) {
            rList.put("BankList", bankList);
        }
        List<Map<String, Object>> cardLevel;
        cardLevel = cardMapper.queryCardLevel();
        if (cardLevel != null) {
            rList.put("CardLevel", cardLevel);
        }

        List<Map<String, Object>> cardType;
        cardType = cardMapper.queryCardType();
        if (cardType != null) {
            rList.put("CardType", cardType);
        }
        return rList;
    }

    /**
     * 卡片筛选条件(修改为json格式)
     * Updated by shaoqinghua in 20170111
     *
     * @param card
     * @return
     */
    public JSONObject queryFilterCondition2(Card card) {
        JSONObject resultJson = new JSONObject();
        List<Map<String, Object>> bankList;
        String ifFilterIos = this.ifFilterBankByIos(card);
        //查询银行列表
        if (StringUtils.isEmpty(card.getIcooperation())) {
            bankList = cardMapper.queryCityCardOld(card.getCityid(), ifFilterIos);
        } else {
            bankList = cardMapper.queryCityCardInfo(card.getCityid(), ifFilterIos);
        }
        logger.info("bankList:" + bankList.size());
        if (bankList != null) {
            resultJson.put("BankList", bankList);
        }
        //查询卡等级
        List<Map<String, Object>> cardLevel;
        cardLevel = cardMapper.queryCardLevel();
        if (cardLevel != null) {
            resultJson.put("CardLevel", cardLevel);
        }
        //查询卡用途
        List<Map<String, Object>> cardType;
        cardType = cardMapper.queryCardType();
        if (cardType != null) {
            resultJson.put("CardType", cardType);
        }
        return resultJson;
    }

    public List<CardDto> queryFilterCard(Card card) {
        List<CardDto> listCard;
        int start = card.getPs() * (card.getPn() - 1);
//        int end=card.getPn()*card.getPs();

        Map<String, String> map = new HashMap<>();
        map.put("cityid", card.getCityid());
        map.put("cardlevel", card.getCardlevel());
        map.put("useid", card.getUseid());
        map.put("bankid", card.getBankid());
        for (String key : map.keySet()) {
            System.out.println("key:" + key + ",value:" + map.get(key));
        }
        PageHelper.offsetPage(start, card.getPs());
        listCard = cardMapper.filterCardInfo(map);

        return listCard;
    }

    String returnAllItem = "0";

    public Map<String, List<Map<String, Object>>> queryCardDetail(Card card) {
        Map<String, List<Map<String, Object>>> cardDetail = new HashMap<>();

        returnAllItem = "0";
        Integer source = card.getSource();
        if (source != null && source != 0) {
            returnAllItem = "1";
        }
        List<BankCardDto> detail = cardMapper.queryCardDetail(card.getCardid());
        List<Map<String, Object>> fillList = new ArrayList<>();
        String cardLevel = "";
        String cardUseId = "";
        if (detail.size() > 0) {
            BankCardDto map = detail.get(0);
          /*  cardUseId=map.get("IUSEID");
            cardLevel=map.get("CCARDLEVEL");*/
            cardUseId = map.getIuseid();
            cardLevel = map.getCcardlevel();
            Map<String, Object> tmpMap;
            tmpMap = fillMap(map, card, returnAllItem);
            fillList.add(tmpMap);
        }
        if (fillList.size() >= 0) {
            cardDetail.put("CardInfo", fillList);
        }
        //从合作银行每家选一张相同主题，相同等级的卡
        String exbanks = card.getExibankid();
        String exsql = "";
        //排除已有的银行
        if (!StringUtils.isEmpty(exbanks)) {
            String bs[] = exbanks.split(",");
            for (String i : bs) {
                exsql += (" and ibankid !=" + i);
            }
        }
        //TODO
        List<Integer> bankList = cardMapper.queryBankId(exsql);
        int index = 0;
        List<Map<String, Object>> recommendList = new ArrayList<>();
        for (int j = 0; j < bankList.size() && index < 3; j++) {
            Map<String, Object> theme = null;
            card.setBankid(String.valueOf(bankList.get(j)));
            card.setCardlevel(cardLevel);
            String[] cardUseIds = null;
            if (StringUtils.isNotEmpty(cardUseId)) {
                cardUseIds = cardUseId.split(",");
            }
            card.setUseids(cardUseIds);
            theme = fillBankThemeByBankId(card);
            if (theme != null && theme.size() > 0) {
                recommendList.add(theme);
                index++;
            }
        }
        if (recommendList.size() > 0) {
            for (int i = 0; i < recommendList.size(); i++) {
                if (recommendList.get(i).size() == 0) {
                    recommendList.remove(i);
                }
            }
            cardDetail.put("Recommend", recommendList);
        } else {
            cardDetail.put("Recommend", null);
        }
        return cardDetail;
    }


    /**
     * 查询卡片详情(修改为json格式)
     * Updated by shaoqinghua on 20170111
     *
     * @param card
     * @return
     */
    public JSONObject queryCardDetail2(Card card) {
        JSONObject cardDetail = new JSONObject();
        returnAllItem = "0";
        Integer source = card.getSource();
        if (source != null && source != 0) {
            returnAllItem = "1";
        }

        List<BankCardDto> detail = cardMapper.queryCardDetail(card.getCardid());
        List<Map<String, Object>> fillList = new ArrayList<>();
        String cardLevel = "";
        String cardUseId = "";
        if (detail.size() > 0) {
            BankCardDto map = detail.get(0);
            cardLevel = map.getCcardlevel();
            cardUseId = map.getIuseid();
            Map<String, Object> tmpMap;
            tmpMap = this.fillMap(map, card, returnAllItem);
            fillList.add(tmpMap);
        }

        if (fillList.size() >= 0) {
            cardDetail.put("CardInfo", fillList);
        }

        //从合作银行每家选择一张相同主题，相同等级的卡片
        String exbanks = card.getExibankid();
        //排除已有的银行
        String exsql = "";
        if (!StringUtils.isEmpty(exbanks)) {
            String[] banks = exbanks.split(",");
            for (String bank : banks) {
                exsql += (" and ibankid != " + bank);
            }
        }

        //查询排除之后合作银行的id列表
        List<Integer> bankList = cardMapper.queryBankId(exsql);
        int index = 0;
        List<Map<String, Object>> recommendList = new ArrayList<>();
        for (int j = 0; j < bankList.size() && index < 3; j++) {
            Map<String, Object> theme = null;
            card.setBankid(String.valueOf(bankList.get(j)));
            card.setCardlevel(cardLevel);
            String[] cardUseIds = null;
            if (StringUtils.isNotEmpty(cardUseId)) {
                cardUseIds = cardUseId.split(",");
            }
            card.setUseids(cardUseIds);
            theme = this.fillBankThemeByBankId(card);
            if (theme != null && theme.size() > 0) {
                recommendList.add(theme);
                index++;
            }
        }
        if (recommendList.size() > 0) {
            for (int i = 0; i < recommendList.size(); i++) {
                if (recommendList.get(i).size() == 0) {
                    recommendList.remove(i);
                }
            }
            cardDetail.put("Recommend", recommendList);
        } else {
            cardDetail.put("Recommend", null);
        }
        return cardDetail;
    }

    /**
     * @param map
     * @param card
     * @param returnAllItem
     * @return update by fix
     */
    private Map<String, Object> fillMap(BankCardDto map, Card card, String returnAllItem) {
        Map<String, Object> fillMap = new HashMap<>();
//        String bankId=map.get("IBANKID")!=null?map.get("IBANKID").toString():"";
//        fillMap.put("carduse",map.get("CTAG")!=null?map.get("CTAG").toString():"");

        String bankId = String.valueOf(map.getIbankid());
        fillMap.put("carduse", map.getCtag());
        int countCheap = cardMapper.queryCardCheapCount(bankId);
        fillMap.put("cheap", countCheap + "");
        int countCommodity = cardMapper.queryCommodityCount(bankId);
        fillMap.put("commodity", countCommodity + "");
        String isave = "0";
        List<String> ls = cardMapper.queryUserIdByCardid(card.getCuserId(), card.getCardid());
        if (ls.size() > 0) {
            isave = "1";
        }
        fillMap.put("isave", isave);
        String cardLevel = map.getCcardlevel();
        String cardLevelName = map.getCcardlevelname();
        String tmpCardLevelName = "";

        if ("1".equals(cardLevel)) {
            tmpCardLevelName = "普通卡";
        } else if ("2".equals(cardLevel)) {
            tmpCardLevelName = "金卡";
        } else if ("3".equals(cardLevel)) {
            tmpCardLevelName = "白金卡";
        }
        if (StringUtils.isEmpty(cardLevelName))
            cardLevelName = tmpCardLevelName;
        fillMap.put("ccardlevelname", cardLevelName);

        String jsonStr = map.getCintegral();
        if (!StringUtils.isEmpty(jsonStr)) {
//					{"rule":"每1元人民币积1分。","period":"积分永久有效，信用卡有效期到期以后，继续办卡，积分可以继续使用"}
            JSONObject json = JSONObject.parseObject(jsonStr);
            jsonStr = "积分规则:" + json.get("rule") + "|积分有效期:" + json.get("period");
        }

        // update by zhukai reson 201611014 start
        Map<String, String> Guide = null;
        Guide = cardMapper.queryCardGuide(bankId);
        // update by zhukai reson 201611014 end

        fillMap.put("integral", jsonStr);
        fillMap.put("icardid", map.getIcardid());
        fillMap.put("ibankid", bankId);
        fillMap.put("ccardname", map.getCcardname());
        fillMap.put("iapplicationnum", map.getIapplicationnum());
        fillMap.put("ccardimg", map.getCcardimg());

        fillMap.put("ccardgain", map.getCcardgain());
        //update by zhukai reson 201611014 start
        fillMap.put("guidtitle", Guide == null ? " " : Guide.get("CTITLE"));
        fillMap.put("guidurl", Guide == null ? " " : Guide.get("CACCESSULR"));
        // update by zhukai reson 201611014 end
        StringBuffer sb0 = new StringBuffer();

        fillBuffer(sb0, map);
        fillMap.put("carddesc", sb0.toString());

        StringBuffer sb1 = new StringBuffer();
        getPrivilege(map.getCprivilege(), sb1);
        fillMap.put("cprivilege", sb1.toString());

        StringBuffer sb2 = new StringBuffer();
        getService(map.getCcardservice(), sb2);
        fillMap.put("ccardservice", sb2.toString());
        String cityCode = "";
        String applyUrl = map.getCardaddr();
        String cardaddrios = "";
        if (StringUtils.isNotEmpty(map.getCardaddrios())) {
            cardaddrios = map.getCardaddrios();
        }
        if (StringUtils.isEmpty(card.getHskcityid())) {
            cityCode = card.getCitycode();
        } else {
            cityCode = card.getHskcityid();
        }
        card.getIclient();
        // update by lcs 20161020
//        applyUrl = getApplyUrl(applyUrl, cardaddrios, card.getIclient(), bankId, cityCode);
        applyUrl = getApplyUrl(applyUrl, cardaddrios, card.getIclient(), bankId, card.getPackagename());
//        applyUrl = getApplyUrl(applyUrl, bankId, cityCode);
        fillMap.put("applyUrl", applyUrl);
        if ("1".equals(returnAllItem)) {
            int isnormal = getCardIsnormal(bankId, card.getCardid());//判断该卡是否支持模拟办卡
            fillMap.put("isnormal", isnormal + "");
        }
        return fillMap;
    }

    private void getPrivilege(String cprivilege, StringBuffer sb) {
        try {
            JSONObject json = JSONObject.parseObject(cprivilege);
            Set<String> keys = json.keySet();
            JSONObject jo = null;
            Object o;
            for (String key : keys) {
                o = json.get(key);
                if (o instanceof JSONObject) {
                    jo = (JSONObject) o;
                    JSONArray titleJsa = jo.getJSONArray("title");
                    String title = (String) titleJsa.get(0);
                    if (CheckUtil.isNullString(title)) {
                        getPrivilege(jo.toString(), sb);
                    } else {
                        title = title.replace("[\"", "").replace("\"]", "");
                        if (!CheckUtil.isNullString(title)) {
                            if (sb.length() == 0 || sb.toString().endsWith("|")) {
                                sb.append(title);
                            } else {
                                sb.append("|" + title);
                            }
                        }
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillBuffer(StringBuffer sb0, BankCardDto map) {

        String feerule = map.getCyearfeerule();

        if (!StringUtils.isEmpty(feerule)) {//年费 todo
            sb0.append("年费:" + feerule);
        }

        if (StringUtils.isNotEmpty(map.getCusename())) {//卡用途
            //sb0.append("|卡种:"+ jsCard.get("cusename").replaceAll(",","、"));
        }
        if (StringUtils.isNotEmpty(map.getCcurrency())) {//币种
            sb0.append("|币种:" + map.getCcurrency());
        }
        if (StringUtils.isNotEmpty(map.getCmedium())) {//介质
            sb0.append("|介质:" + map.getCmedium());
        }
        if (StringUtils.isNotEmpty(map.getCcardfreenum())) {//介质
            sb0.append("|免息期:" + map.getCcardfreenum());
        }
        if (StringUtils.isNotEmpty(map.getCdailyinterest())) {
            sb0.append("|日息:" + map.getCdailyinterest());
        }
        if (StringUtils.isNotEmpty(map.getClatefeerate())) {
            sb0.append("|滞纳金比例:" + map.getClatefeerate());
        }
        if (StringUtils.isNotEmpty(map.getCcarshfee())) {
            sb0.append("|取现手续费率:" + map.getCcarshfee());
        }
        if (StringUtils.isNotEmpty(map.getCconvertrate())) {
            sb0.append("|外币兑换手续费:" + map.getCconvertrate());
        }
        if (StringUtils.isNotEmpty(map.getCcounterfee())) {
            sb0.append("|溢缴款取回手续费:" + map.getCcounterfee());
        }
        if (StringUtils.isNotEmpty(map.getCexcessrate())) {
            sb0.append("|超额费比例:" + map.getCexcessrate());
        }

        if (StringUtils.isNotEmpty(map.getCsplitcost())) {//todo
            sb0.append("|分期费率:" + map.getCsplitcost());
        }
    }

    private static void getService(String cprivilege, StringBuffer sb) {
        try {
            JSONObject json = JSONObject.parseObject(cprivilege);
            Set<String> keys = json.keySet();
            JSONObject jo = null;
            Object o;
            for (String key : keys) {
                o = json.get(key);
                if (o instanceof JSONObject) {
                    jo = (JSONObject) o;
                    String desc = jo.getString("desc");
                    if (CheckUtil.isNullString(desc) && !key.contains("使用指南")) {
                        getService(jo.toString(), sb);
                    } else {
                        desc = desc.replace("[\"", "").replace("\"]", "");
                        if (!CheckUtil.isNullString(desc)) {
                            if (sb.length() == 0 || sb.toString().endsWith("|")) {
                                sb.append(desc);
                            } else {
                                sb.append("|" + desc);
                            }
                        }
                    }
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取办卡链接
     *
     * @param applyUrl    某信用卡卡办卡地址
     * @param cardaddrios 某信用卡卡IOS端办卡地址
     * @param iclient     新版本设备类型 0 Android，1 iOS
     * @param bankId      银行ID
     * @param packagename 包名
     * @return created by lcs 20161019
     */
    public String getApplyUrl(String applyUrl, String cardaddrios, int iclient, String bankId, String packagename) {
        String cardApplyUrl = "";
        logger.info("request packageName:" + packagename + ",bankId:" + bankId);
        logger.info(" applyUrl:" + applyUrl + ",iclient:" + iclient);
        // 从开卡信息表中获取 相关信息
        Map<String, String> cardAddress = cardMapper.queryBankAddress(bankId);
        if (cardAddress == null) {
            return applyUrl;
        }
        //包名
        String pname = cardAddress.get("cpackagename");
        logger.info("例外 pname:" + pname);

        // 如果数据库表中设置了例外包名,包含此请求的包名 则直接返回空
//        if (!CheckUtil.isNullString(pname) && !CheckUtil.isNullString(packagename) && pname.contains(packagename)){
//            return cardApplyUrl;
//        }
        if (!CheckUtil.isNullString(pname) && !CheckUtil.isNullString(packagename)) {
            packagename = "," + packagename + ",";
            pname = "," + pname + ",";
            if (pname.contains(packagename)) {
                return cardApplyUrl;
            }
        }
        // 如果 cardaddrios不为空 并且是ios
        if (1 == iclient && !CheckUtil.isNullString(cardaddrios)) {
            return cardaddrios;
        }
        if (CheckUtil.isNullString(applyUrl)) {
            String addr = cardAddress.get("cardaddr");
            String addrios = cardAddress.get("cardaddrios");
            if (1 == iclient && !StringUtils.isEmpty(addrios)) {
                cardApplyUrl = addrios;
            } else {
                cardApplyUrl = addr;
            }
        } else {
            cardApplyUrl = applyUrl;
        }
        return cardApplyUrl;
    }

    /*
    *获取办卡链接
    * applyUrl 办卡地址
    * bankid 办卡银行
    * citycode 城市代码
    *
    * **/
//    private String getApplyUrl(String applyUrl, String cardaddrios, Integer iclient, String bankId,String citycode) {
//        boolean canyybk = false;
//        String sql = "";
//        //查询是否支持预约办卡
//        if (!StringUtil.isEmpty(bankId)&&!StringUtil.isEmpty(citycode)){
//        List<String> listCode=cardMapper.queryBandCodeByCityId(citycode);
//            // add by lcs 20160603 排除空指针
//          if (listCode.size()>0){
//               for (String code :listCode){
//                    if (bankId.equals(code)){
//                        canyybk = true;
//                        break;
//                    }
//                }
//            }
//        }
//
//        //如果某卡的办卡地址为空，去查询银行通用办卡地址
//        if(StringUtil.isEmpty(applyUrl)){
//           Map<String,String> cardAddress=cardMapper.queryBankAddress(bankId);
//            String addr = cardAddress.get("cardaddr");
//            String addrios = cardAddress.get("cardaddrios");
//            if (1 == iclient && !StringUtil.isEmpty(addrios)) {
//                applyUrl = addrios;
//            }else {
//                applyUrl = addr;
//            }
//        }
//        //新版的citycode不为空。
//        //信贷后台开关控制此办卡链接，现在不用了。
//		/*if (!StringUtil.isEmpty(citycode) && !canyybk && applyUrl.contains("huishuaka")){
//			applyUrl = "";
//		}*/
//        return applyUrl;
//    }
    /*
     *是否支持模拟办卡
      *  bankId 银行id
      *  cardid 卡id
     * */
    private int getCardIsnormal(String bankId, String cardid) {
        Integer bankIsnormal = cardMapper.isNormalOfBank(bankId);
        if (bankIsnormal == null || bankIsnormal != 1)
            return 0;
        int cardIsnormal = 0;
        cardIsnormal = cardMapper.isNormalOfCard(cardid);
        return cardIsnormal;
    }

    private Map<String, Object> fillBankThemeByBankId(Card card) {
        Map<String, Object> fillTheme = new HashMap<>();
//        Map<String,String> map=new HashMap<>();
//        map.put("bankid",String.valueOf(bankid));
//        map.put("cardlevel",level);
//        map.put("cardid",cardId);
//        StringBuffer sb=new StringBuffer();
//        if (cardUseIds!=null&&cardUseIds.length>0){
//            sb.append(" where  t2.iuseid in (");
//            for(String cardUserId:cardUseIds){
//                sb.append(cardUserId+",");
//            }
//            if (sb.toString().endsWith(","))
//                sb.deleteCharAt(sb.length()-1);
//            sb.append(")");
//        }
//        sb.append(" order by t1.ishot,t1.iapplicationnum desc");
//        map.put("sqlWhere",sb.toString());
        List<BankCardDto> bankThemeList = cardMapper.queryThemeByCardId(card);
        if (bankThemeList.size() > 0) {
            BankCardDto theme = bankThemeList.get(0);
            String privileges = theme.getCprivilege();
            fillTheme.put("Privilege", getTwoPrivileges(privileges));
            fillTheme.put("CardId", theme.getIcardid());
            fillTheme.put("CardPic", theme.getCcardimg());
            fillTheme.put("CardTitle", theme.getCcardname());
            fillTheme.put("CardNums", theme.getIapplicationnum());
            if ("1".equals(returnAllItem)) {
                fillTheme.put("ibankid", theme.getIbankid());
                fillTheme.put("ccardlevel", theme.getCcardlevel());
                fillTheme.put("ccardlevelname", theme.getCcardlevelname());
                fillTheme.put("cyearfee", theme.getCyearfee());
                fillTheme.put("ccurrency", theme.getCcurrency());
//                fillTheme.put("cardaddr", theme.get("CARDADDR")==null?"":theme.get("CARDADDR").toString());
                String applyUrl = theme.getCardaddr();
                fillTheme.put("cardaddr", getApplyUrl(applyUrl, "", card.getIclient(), String.valueOf(card.getBankid
                        ()), card.getPackagename()));
                fillTheme.put("isnormal", getCardIsnormal(String.valueOf(card.getBankid()), String.valueOf(theme
                        .getIcardid())));
            }
        }
        return fillTheme;
    }

    // 获取卡片的两种特权
    private String getTwoPrivileges(String args) {
        String privilages = "";
        StringBuffer sbpr = new StringBuffer();
        try {
            getPrivilege(args, sbpr);
            String privilege = sbpr.toString();
            logger.info("cprivilege" + privilege);
            if (!CheckUtil.isNullString(privilege)) {
                if (privilege.contains("|")) {
                    privilages = privilege.split("\\|")[0] + "|" + privilege.split("\\|")[1];
                } else {
                    privilages = privilege;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privilages;
    }

    public List<CardDto> queryCardForTotalSearch(Card card) {
        List<CardDto> listCard;
        PageHelper.startPage(card.getPn(), card.getPs());
        logger.info("cardName={}", card.getName());
        listCard = cardMapper.queryCardForTotalSearch(card);
        return listCard;
    }


}
