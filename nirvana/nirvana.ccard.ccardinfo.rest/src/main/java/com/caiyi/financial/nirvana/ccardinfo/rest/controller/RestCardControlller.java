package com.caiyi.financial.nirvana.ccardinfo.rest.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.utils.JsonUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lizhijie on 2016/6/20.
 */
@RestController
@RequestMapping("/credit")
public class RestCardControlller {
    private static Logger log = LoggerFactory.getLogger(RestCardControlller.class);
    @Resource(name = Constant.HSK_CCARD_INFO)
    IDrpcClient client;

    @SetUserDataRequired
    @RequestMapping("/qCardIndex.go")
    public void queryCardIndex(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("cuserId");
        log.info("用户" + userId + ",访问了qCardIndex.go");
        String bankId = request.getParameter("ibankids");
        String cityId = request.getParameter("cityid");
        String source = request.getParameter("source");
        Document dom = DocumentHelper.createDocument();
        Element res = new DOMElement("Resp");
        dom.setRootElement(res);
        Card card = new Card();
        //判断客户端，如果iclien为空，根据source判断
        if ("-1".equals(request.getParameter("iclient"))) {
            try {
                if (Integer.parseInt(source) >= 6000) {
                    card.setIclient(1);
                } else {
                    card.setIclient(0);
                }
            } catch (Exception e) {
                res.addAttribute("code", "0");
                res.addAttribute("desc", "参数错误");
                XmlUtils.writeXml(dom, response);
            }
        }
        card.setCuserId(userId);
        card.setSource(Integer.parseInt(source));
        card.setIbankids(bankId);
        card.setCityid(cityId);
        String indexResult = client.execute(new DrpcRequest("card", "queryCardIndex", card));
        JSONObject json = JSONObject.parseObject(indexResult);
        Map<String, String> map = new HashMap<>();
        if (null != json.get("allnormal") && json.getJSONArray("allnormal").size() > 0) {
            JSONArray allnormal = json.getJSONArray("allnormal");
            map.put("ISNORMAL", "open");
            JsonUtil.jsonToElement(allnormal, res, "allnormal", map);
            map.clear();
        }
        if (null != json.get("BankList") && json.getJSONArray("BankList").size() > 0) {
            Element BankList = null;
            JSONArray bankList = json.getJSONArray("BankList");
            map.put("IBANKID", "BankId");
            map.put("CBANKNAME", "BankName");
            BankList = JsonUtil.jsonToElement(bankList, "BankList", "BankItem", map);
            res.add(BankList);
        }
        if (null != json.get("ArticalList") && json.getJSONArray("ArticalList").size() > 0) {
            Element artical = null;
            Element Artical = new DOMElement("Artical");
            JSONArray articalList = json.getJSONArray("ArticalList");
            map.put("CTITLE", "ArticalName");
            map.put("CSUBTITLE", "ArticalDesc");
            map.put("CPICURL", "ArticalPic");
            map.put("CACCESSULR", "ArticalUrl");
            map.put("IITEMID", "ArticalId");
            artical = JsonUtil.jsonToElement(articalList, "ArticalList", "ArticalItem", map);
            Artical = JsonUtil.jsonToElement(articalList, Artical, map, true);
            res.add(artical);
            res.add(Artical);
            map.clear();
        }
        if (null != json.get("CardList") && json.getJSONArray("CardList").size() > 0) {
            Element CardList = null;
            JSONArray cardList = json.getJSONArray("CardList");
            map.put("ICARDID", "CardId");
            map.put("CCARDIMG", "CardPic");
            map.put("CCARDNAME", "CardTitle");
            map.put("CCARDGAIN", "CardDesc");
            map.put("IAPPLICATIONNUM", "CardNums");
            map.put("CTAG", "CardTags");
            CardList = JsonUtil.jsonToElement(cardList, "CardList", "CardItem", map);
            res.add(CardList);
            map.clear();
        }
        if (null != json.get("CollectCardList") && json.getJSONArray("CollectCardList").size() > 0) {
            Element collectCard = null;
            JSONArray collectCardList = json.getJSONArray("CollectCardList");
            map.put("ICARDID", "CardId");
            map.put("CCARDIMG", "CardPic");
            map.put("CCARDNAME", "CardTitle");
            map.put("CCARDGAIN", "CardDesc");
            map.put("IAPPLICATIONNUM", "CardNums");
            collectCard = JsonUtil.jsonToElement(collectCardList, "CollectCardList", "CollectCardItem", map);
            res.add(collectCard);
            map.clear();
        }
        if (null != json.get("wechatMsg") && json.getJSONArray("wechatMsg").size() > 0) {
            JSONArray wechatMsg = json.getJSONArray("wechatMsg");
            map.put("CBANKNAME", "bankname");
            map.put("IPRAISE", "ipraise");
            map.put("ICOLLECT", "icollect");

            JsonUtil.jsonToElement(wechatMsg, res, "wechatMsg", map);
            map.clear();
        }
        res.addAttribute("code", "1");
        res.addAttribute("desc", "办卡首页查询成功");
        dom.setRootElement(res);
        XmlUtils.writeXml(dom, response);
    }


    /**
     * 查询办卡首页（修改为json格式）
     * Updated by shaoqinghua in 20170111
     *
     * @param card
     * @return
     */
    @SetUserDataRequired
    @RequestMapping("/qCardIndex2.go")
    public BoltResult queryCardIndex2(Card card) {
        BoltResult boltResult = new BoltResult("1", "success");
        JSONObject data = new JSONObject();
        log.info("用户：" + card.getCuserId() + ",访问了qCardIndex.go");
        //判断客户端，如果iclient为空，则判断source
        if ("-1".equals(card.getIclient())) {
            try {

                if (card.getSource() >= 6000) {
                    card.setIclient(1);
                } else {
                    card.setIclient(0);
                }
            } catch (Exception e) {
                boltResult.setCode("0");
                boltResult.setData("参数错误");
                return boltResult;
            }
        }

        String result = client.execute(new DrpcRequest("card", "queryCardIndex2", card));
        JSONObject resultJson = JSONObject.parseObject(result);

        JSONArray allnormal = resultJson.getJSONArray("allnormal");
        if (null != resultJson.get("allnormal") && allnormal.size() > 0) {
            JSONObject temp = new JSONObject();
            temp.put("ISNORMAL", "open");
            data.put("allnormal", temp);
        }

        //获取银行列表
        JSONArray bankList = resultJson.getJSONArray("BankList");
        if (null != resultJson.get("BankList") && bankList.size() > 0) {
            JSONArray tempBankList = new JSONArray();
            for (int i = 0; i < bankList.size(); i++) {
                Map<String, Object> temp = new HashMap<>();
                JSONObject bank = bankList.getJSONObject(i);
                temp.put("BankName", bank.get("CBANKNAME") == null ? "" : bank.get("CBANKNAME"));
                temp.put("BankId", bank.get("IBANKID") == null ? "" : bank.get("IBANKID"));
                tempBankList.add(temp);
            }
            data.put("BankList", tempBankList);
        }

        //获取卡神攻略列表
        JSONArray articalList = resultJson.getJSONArray("ArticalList");
        if (null != resultJson.get("ArticalList") && articalList.size() > 0) {
            JSONArray tempArticalList = new JSONArray();
            for (int i = 0; i < articalList.size(); i++) {
                Map<String, Object> temp = new HashMap<>();
                JSONObject artical = articalList.getJSONObject(i);
                temp.put("ArticalName", artical.get("CTITLE") == null ? "" : artical.get("CTITLE"));
                temp.put("ArticalDesc", artical.get("CSUBTITLE") == null ? "" : artical.get("CSUBTITLE"));
                temp.put("ArticalPic", artical.get("CPICURL") == null ? "" : artical.get("CPICURL"));
                temp.put("ArticalUrl", artical.get("CACCESSULR") == null ? "" : artical.get("CACCESSULR"));
                temp.put("ArticalId", artical.get("IITEMID") == null ? "" : artical.get("IITEMID"));
                tempArticalList.add(temp);
            }
            data.put("ArticalList", tempArticalList);
            data.put("Artical", tempArticalList.getJSONObject(0));
        }
        //获取微信文章列表
        JSONArray wechatMsg = resultJson.getJSONArray("wechatMsg");
        if (null != resultJson.get("wechatMsg") && wechatMsg.size() > 0) {
            data.put("wechatMsg", wechatMsg);
        }
        if (boltResult == null) {
            boltResult.setCode("0");
            boltResult.setDesc("办卡首页查询失败");
        } else {
            boltResult.setCode("1");
            boltResult.setDesc("办卡首页查询成功");
            boltResult.setData(data);
        }
        return boltResult;
    }

    /**
     * 筛选条件
     *
     * @param card
     * @param response update by lcs 20161114
     */
    @RequestMapping("/qFilterCondition.go")
    public void queryFilterCondition(Card card, HttpServletResponse response) {
        if (card.getSource() == null) {
            card.setSource(0);
        }
        if (CheckUtil.isNullString(card.getCityid())) {
            card.setCityid("101");
        }
        int source = card.getSource();
        String cityId = card.getCityid();
        int iclient = card.getIclient();

        log.info("条件：" + cityId + ",访问了qFilterCondition.go start" + source + "," + cityId);

        if (-1 == iclient) {
            if (source >= 6000) {
                card.setIclient(1);
            } else {
                card.setIclient(0);
            }
        }
        Document dom = DocumentHelper.createDocument();
        Element res = new DOMElement("Resp");
        dom.setRootElement(res);

        String cardResult = client.execute(new DrpcRequest("card", "queryFilterCondition", card));
        JSONObject json = JSONObject.parseObject(cardResult);
        Map<String, String> map = new HashMap<>();
        if (null != json.get("BankList") && json.getJSONArray("BankList").size() > 0) {
            JSONArray bankList = json.getJSONArray("BankList");
            Element bankElement = new DOMElement("BankList");
            Element bankItemElement = new DOMElement("BankItem");
            bankItemElement.addAttribute("BankId", "-1");
            bankItemElement.addAttribute("BankName", "全部银行");
            bankElement.add(bankItemElement);
            map.put("IBANKID", "BankId");
            map.put("CBANKNAME", "BankName");
            map.put("HOTDEAL", "HotDeal");
            bankElement = JsonUtil.jsonToElement(bankList, bankElement, "BankItem", map);
            res.add(bankElement);
            log.info("条件：" + cityId + ",访问了qFilterCondition.go,添加BankList");
        }
        if (null != json.get("CardLevel") && json.getJSONArray("CardLevel").size() > 0) {
            JSONArray cardLevel = json.getJSONArray("CardLevel");
            Element CardLevel = new DOMElement("CardLevel");
            Element levelElement = new DOMElement("Level");
            levelElement.addAttribute("Level", "-1");
            levelElement.addAttribute("LevelName", "全部等级");
            CardLevel.add(levelElement);
            CardLevel = JsonUtil.jsonToElement(cardLevel, CardLevel, "Level", null);
            res.add(CardLevel);
            log.info("条件：" + cityId + ",访问了qFilterCondition.go,添加CardLevel");
        }
        if (null != json.get("CardType") && json.getJSONArray("CardType").size() > 0) {
            JSONArray cardType = json.getJSONArray("CardType");
            Element cardTypeE = new DOMElement("CardType");
            Element typeElement = new DOMElement("Type");
            typeElement.addAttribute("iuseid", "-1");
            typeElement.addAttribute("cusename", "全部主题");
            cardTypeE.add(typeElement);
            cardTypeE = JsonUtil.jsonToElement(cardType, cardTypeE, "Type", null);
            res.add(cardTypeE);
            log.info("条件：" + cityId + ",访问了qFilterCondition.go,添加CardType");
        }
        res.addAttribute("code", "1");
        res.addAttribute("desc", "卡列表筛选成功");
        log.info("条件：" + cityId + ",访问了qFilterCondition.go,成功");
        dom.setRootElement(res);
        XmlUtils.writeXml(dom, response);
    }

    /**
     * 卡筛选条件(修改为json格式)
     * Updated by shaoqinghua in 20170111
     *
     * @param card
     * @return
     */
    @RequestMapping("/qFilterCondition2.go")
    public BoltResult queryFilterCondition2(Card card) {
        BoltResult boltResult = new BoltResult("1", "success");
        JSONObject data = new JSONObject();
        if (null == card.getSource()) {
            card.setSource(0);
        }
        if (CheckUtil.isNullString(card.getCityid())) {
            card.setCityid("101");
        }

        int source = card.getSource();
        String cityId = card.getCityid();
        int iclient = card.getIclient();
        log.info("条件：" + cityId + "访问了qFilterCondition.go,Source:" + source + ",iclient:" + iclient);
        //判断客户端，如果iclient为空，则判断source
        if ("-1".equals(iclient)) {
            try {
                if (source >= 6000) {
                    card.setIclient(1);
                } else {
                    card.setIclient(0);
                }
            } catch (Exception e) {
                boltResult.setCode("0");
                boltResult.setData("参数错误");
                return boltResult;
            }
        }

        String result = client.execute(new DrpcRequest("card", "queryFilterCondition2", card));
        JSONObject resultJson = JSONObject.parseObject(result);
        JSONArray bankList = resultJson.getJSONArray("BankList");
        Map<String, Object> map = new HashMap<>();
        if (null != resultJson.get("BankList") && bankList.size() > 0) {
            JSONObject temp = new JSONObject();
            temp.put("BankId", "-1");
            temp.put("BankName", "全部银行");
            bankList.add(temp);
            data.put("BankList", bankList);
            log.info("条件：" + cityId + ",访问了qFilterCondition.go,添加BankList");
        }

        JSONArray cardLevel = resultJson.getJSONArray("CardLevel");
        if (null != resultJson.get("CardLevel") && cardLevel.size() > 0) {
            JSONObject temp = new JSONObject();
            temp.put("Level", "-1");
            temp.put("LevelName", "全部等级");
            cardLevel.add(temp);
            data.put("CardLevel", cardLevel);
            log.info("条件：" + cityId + ",访问了qFilterCondition.go,添加CardLevel");
        }

        JSONArray cardType = resultJson.getJSONArray("CardType");
        if (null != resultJson.get("CardType") && cardType.size() > 0) {
            JSONObject temp = new JSONObject();
            temp.put("iuseid", "-1");
            temp.put("cusename", "全部主题");
            cardType.add(temp);
            data.put("CardType", cardType);
            log.info("条件：" + cityId + ",访问了qFilterCondition2.go,添加CardType");
        }
        if(boltResult == null) {
            boltResult.setCode("0");
            boltResult.setDesc("卡列表筛选失败");
        } else {
            boltResult.setCode("1");
            boltResult.setDesc("卡列表筛选成功");
            boltResult.setData(data);
        }
        return boltResult;
    }


    /**
     * @param response
     * @param request
     */
    @RequestMapping("/qCardFilter.go")
    public void queryFilterCard(HttpServletResponse response, HttpServletRequest request) {
        String cityid = request.getParameter("cityid");
        String cardlevel = request.getParameter("cardlevel");
        String forward = request.getParameter("forward");
        String useid = request.getParameter("useid");
        String bankid = request.getParameter("bankid");
        String ps = request.getParameter("ps");
        String pn = request.getParameter("pn");

        if (StringUtils.isEmpty(cityid)) {
            cityid = "101";
        }
        if (StringUtils.isEmpty(ps)) {
            ps = "10";
        }
        if (StringUtils.isEmpty(pn)) {
            pn = "1";
        }
        Card card = new Card();
        card.setCityid(cityid);
        card.setPs(Integer.parseInt(ps));
        card.setPn(Integer.parseInt(pn));
        card.setCardlevel(cardlevel);
        card.setUseid(useid);
        card.setBankid(bankid);
        String cardResult = client.execute(new DrpcRequest("card", "queryFilterCard", card));

        JSONObject jsonObject = JSONObject.parseObject(cardResult);
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        Element page = new DOMElement("pageinfo");
        page.addAttribute("rc", jsonObject.get("records").toString());
        page.addAttribute("tp", jsonObject.get("totalPage").toString());
        page.addAttribute("ps", jsonObject.get("pageSize").toString());
        page.addAttribute("pn", jsonObject.get("pageNum").toString());
        resp.add(page);
        if (null != jsonObject.get("rows")) {
            JSONArray rows = jsonObject.getJSONArray("rows");
            Element cardListElement = new DOMElement("CardList");
            for (int i = 0; i < rows.size(); i++) {
                Element cardItemElement = new DOMElement("CardItem");
                JSONObject item = rows.getJSONObject(i);
                cardItemElement.addAttribute("CardId", item.getString("ICARDID"));
                cardItemElement.addAttribute("CardPic", item.getString("CCARDIMG"));
                cardItemElement.addAttribute("CardTitle", item.getString("CCARDNAME"));
                cardItemElement.addAttribute("CardDesc", item.getString("CCARDGAIN"));
                cardItemElement.addAttribute("CardNums", item.getString("IAPPLICATIONNUM"));
                if ("new".equals(forward)) {
                    cardItemElement.addAttribute("CardTags", item.getString("CTAG"));
                }
                cardListElement.add(cardItemElement);
            }
            resp.add(cardListElement);
            resp.addAttribute("code", "1");
            resp.addAttribute("desc", "筛选成功");
        } else {
            resp.addAttribute("code", "0");
            resp.addAttribute("desc", "筛选失败");
        }
        XmlUtils.writeXml(dom, response);
    }

    /**
     * 卡筛选(修改为json格式)
     * Updated by shaoqinghua in 20170111
     *
     * @param card
     * @return
     */
    @RequestMapping("/qCardFilter2.go")
    public BoltResult queryFilterCard(Card card) {
        BoltResult boltResult = new BoltResult();
        JSONObject data = new JSONObject();
        String cityid = card.getCityid();
        String forward = card.getForward();
        Integer ps = card.getPs();
        Integer pn = card.getPn();

        if (StringUtils.isEmpty(cityid)) {
            card.setCityid("101");
        }
        if (ps == null) {
            card.setPs(10);
        }
        if (pn == null) {
            card.setPn(1);
        }

        String result = client.execute(new DrpcRequest("card", "queryFilterCard2", card));
        JSONObject resultJson = JSONObject.parseObject(result);
        Map<String, Object> pageinfo = new HashMap<>();
        pageinfo.put("rc", resultJson.get("records").toString());
        pageinfo.put("tp", resultJson.get("totalPage").toString());
        pageinfo.put("ps", resultJson.get("pageSize").toString());
        pageinfo.put("pn", resultJson.get("pageNum").toString());
        data.put("pageinfo", pageinfo);

        JSONArray rows = resultJson.getJSONArray("rows");
        List<Map<String, Object>> cardItem = new ArrayList<>();
        if (null != resultJson.get("rows") && rows.size() > 0) {
            for (int i = 0; i < rows.size(); i++) {
                JSONObject item = rows.getJSONObject(i);
                Map<String, Object> temp = new HashMap<>();
                temp.put("CardId", item.get("ICARDID").toString());
                temp.put("CardPic", item.get("CCARDIMG").toString());
                temp.put("CardTitle", item.get("CCARDNAME").toString());
                temp.put("CardDesc", item.get("CCARDGAIN").toString());
                temp.put("CardNums", item.get("IAPPLICATIONNUM").toString());
                if ("new".equals(forward)) {
                    item.put("CardTags", item.get("CTAG").toString());
                }
                cardItem.add(temp);
            }
            data.put("CardList", cardItem);
            boltResult.setCode("1");
            boltResult.setDesc("筛选成功");
            boltResult.setData(data);
        } else {
            boltResult.setCode("0");
            boltResult.setDesc("筛选失败");
        }
        return boltResult;
    }

    @RequestMapping("/qCardDetail.go")
    @SetUserDataRequired
    public void cardDetail(Card card, HttpServletResponse response) {
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        //判断客户端，如果iclien为空，根据source判断
        Integer source = card.getSource();
        if (null != card.getIclient() && card.getIclient() == -1) {
            if (source != null && source >= 6000) {
                card.setIclient(1);
            } else {
                card.setIclient(0);
            }
        } else if (card.getIclient() == null) {
            card.setIclient(0);
        }
        String resultDetail = client.execute(new DrpcRequest("card", "queryCardDetail", card));
        log.info("cardDetail:" + resultDetail);
        JSONObject jsonReuslt = JSONObject.parseObject(resultDetail);
        if (jsonReuslt.get("CardInfo") != null && jsonReuslt.getJSONArray("CardInfo").size() > 0) {
            JSONObject cardInfo = jsonReuslt.getJSONArray("CardInfo").getJSONObject(0);
            Element elementCard = new DOMElement("CardInfo");
            for (String key : cardInfo.keySet()) {
                elementCard.addAttribute(key, cardInfo.get(key) == null ? "" : cardInfo.get(key).toString());
            }
            resp.add(elementCard);
        }
        if (jsonReuslt.get("Recommend") != null && jsonReuslt.getJSONArray("Recommend").size() > 0) {
            JSONArray recommend = jsonReuslt.getJSONArray("Recommend");
            JsonUtil.jsonToElement(recommend, resp, "Recommend", null);
        }
        if (resp.elements().size() > 0) {
            resp.addAttribute("desc", "获得卡详情成功");
            resp.addAttribute("code", "1");
        } else {
            resp.addAttribute("desc", "获得卡详情失败");
            resp.addAttribute("code", "0");
        }
        XmlUtils.writeXml(dom, response);
    }


    /**
     * 查询卡片详情(修改为json格式)
     * Updated by shaoqinghua in 20170111
     *
     * @param card
     * @return
     */
    @RequestMapping("/qCardDetail2.go")
    @SetUserDataRequired
    public BoltResult cardDetail(Card card) {
        BoltResult boltResult = new BoltResult();
        JSONObject data = new JSONObject();
        //判断客户端，根据iclient判断，如果iclient为空，则根据source判断
        Integer iclient = card.getIclient();
        Integer source = card.getSource();
        if (null != iclient && iclient == -1) {
            if (source != null && source >= 6000) {
                card.setIclient(1);
            } else {
                card.setIclient(0);
            }
        } else if (null == iclient) {
            card.setIclient(0);
        }

        String cardDetail = client.execute(new DrpcRequest("card", "queryCardDetail2", card));
        log.info("cardDetail:" + cardDetail);
        JSONObject detailJson = JSONObject.parseObject(cardDetail);
        //添加卡片信息
        if (detailJson.get("CardInfo") != null && detailJson.getJSONArray("CardInfo").size() > 0) {
            JSONObject cardInfo = detailJson.getJSONArray("CardInfo").getJSONObject(0);
            Map<String, Object> map = new HashMap<>();
            for (String key : cardInfo.keySet()) {
                map.put(key, cardInfo.get(key) == null ? "" : cardInfo.get(key));
            }
            data.put("CardInfo", map);
        }
        //添加推荐
        JSONArray recommend = detailJson.getJSONArray("Recommend");
        if (detailJson.get("Recommend") != null && recommend.size() > 0) {
            data.put("recommend", recommend);
        }

        if (data.size() > 0) {
            boltResult.setCode("1");
            boltResult.setDesc("获得卡详情成功");
            boltResult.setData(data);
        } else {
            boltResult.setCode("0");
            boltResult.setDesc("获得卡详情失败");
            boltResult.setData(null);
        }
        return boltResult;
    }
}
