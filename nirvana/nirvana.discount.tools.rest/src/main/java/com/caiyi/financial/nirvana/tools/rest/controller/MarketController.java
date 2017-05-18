package com.caiyi.financial.nirvana.tools.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.tools.bean.MarketBean;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heshaohua on 2016/8/24.
 */

@RestController
public class MarketController {
    private  static Logger log= LoggerFactory.getLogger(MarketController.class);
    @Resource(name = Constant.HSK_TOOL)
    IDrpcClient client;

    @RequestMapping("/market/getShopList.go")
    public void getShopList(MarketBean bean, HttpServletRequest request, HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");

        dom.setRootElement(resp);
        try {
            String result = client.execute(new DrpcRequest("MarketBolt", "getShopList", bean));
            System.out.println(result);

            if(result != null && !result.equals("null")){
                JSONArray jsonArray = JSONObject.parseArray(result);
                resp.addAttribute("code", "1");
                resp.addAttribute("desc", "查询连锁店列表");

                for(int i=0; i< jsonArray.size(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    resp.add(XmlUtils.jsonParseXml(jsonObject, "row"));
                }
            }else{
                resp.addAttribute("code", "-1");
                resp.addAttribute("desc", "不存在的数据");
            }

            dom.setRootElement(resp);

           // [{"clogo":"http://www.juanlaoda.com/ImageUpload/2013310191729944.jpg","clogolist":"http://www.juanlaoda.com/ImageUpload/2013310191729944.jpg","cname":"肯德基","imarketid":"37","num":"3"},{"clogo":"http://www.juanlaoda.com/ImageUpload/2013211194034.jpg","clogolist":"http://www.juanlaoda.com/ImageUpload/2013211194034.jpg","cname":"汉堡王","imarketid":"7","num":"3"},{"clogo":"http://www.juanlaoda.com/ImageUpload/2013310204751560.jpg","clogolist":"http://www.juanlaoda.com/ImageUpload/2013310204751560.jpg","cname":"德克士","imarketid":"9","num":"2"},{"clogo":"http://www.juanlaoda.com/ImageUpload/201321117512.jpg","clogolist":"http://www.juanlaoda.com/ImageUpload/201321117512.jpg","cname":"吉野家","imarketid":"6","num":"1"},{"clogo":"http://www.juanlaoda.com/ImageUpload/2013310205634659.jpg","clogolist":"http://www.juanlaoda.com/ImageUpload/2013310205634659.jpg","cname":"真功夫","imarketid":"5","num":"0"},{"clogo":"http://www.juanlaoda.com/ImageUpload/2013211194232.jpg","clogolist":"g","cname":"永和大王","imarketid":"8","num":"0"},{"clogo":"http://www.juanlaoda.com/ImageUpload/2013310204533518.jpg","cname":"麦当劳","imarketid":"4","num":"0"}]
        } catch (JSONException e){//抛错说明JSON字符不是数组或根本就不是JSON
            try {
                resp.addAttribute("code", "-2");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                resp.addAttribute("code", "-200");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            }
        }finally {
            XmlUtils.writeXml(dom.asXML(), response);
        }
    }

    @RequestMapping("/market/getCouponList.go")
    public void getCouponList(MarketBean bean, HttpServletRequest request, HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");

        dom.setRootElement(resp);
        try {
            String result = client.execute(new DrpcRequest("MarketBolt", "getCouponList", bean));
            System.out.println(result);

            if(result != null && !result.equals("null")){
                JSONArray jsonArray =JSONObject.parseArray(result);
                resp.addAttribute("code", "1");
                resp.addAttribute("desc", "查询优惠券列表");

                for(int i=0; i< jsonArray.size(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    resp.add(XmlUtils.jsonParseXml(jsonObject, "row"));
                }
            }else{
                resp.addAttribute("code", "-1");
                resp.addAttribute("desc", "不存在的数据");
            }

            dom.setRootElement(resp);
        } catch (JSONException e){//抛错说明JSON字符不是数组或根本就不是JSON
            try {
                resp.addAttribute("code", "-2");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                resp.addAttribute("code", "-200");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            }
        }finally{
            XmlUtils.writeXml(dom.asXML(), response);
        }
    }

    @RequestMapping("/market/getMarketList.go")
    public void getMarketList(MarketBean bean, HttpServletRequest request, HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        Element market = new DOMElement("Market");

        dom.setRootElement(resp);
        resp.add(market);
        try {
            String result = client.execute(new DrpcRequest("MarketBolt", "getMarketList", bean));

            if(result != null && !result.equals("null")){
                JSONArray jsonArray = JSONObject.parseArray(result);
                resp.addAttribute("code", "1");
                resp.addAttribute("desc", "查询成功");

                for(int i=0; i< jsonArray.size(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    market.add(XmlUtils.jsonParseXml(jsonObject, "MarketItem"));
                }
            }else{
                resp.addAttribute("code", "-1");
                resp.addAttribute("desc", "不存在的数据");
            }
            dom.setRootElement(resp);

            //[{"cadddate":"2015-10-20 16:53:06.0","ceditdate":"2016-10-20 17:47:33.0","cenddate":"2015-10-23 00:00:00.0","clogo":"http://hsk.gs.9188.com/imgs/business/2015/9/20/家乐福-49f6c1cc-7756-4300-942d-91cb5e78.jpg","clogolist":"http://hsk.gs.9188.com/imgs/business/2015/9/20/jlf_logo@3x-75b12f5f-8f0c-437e-91f6-6fa1c0f7.png","cname":"家乐福","cstartdate":"2015-10-05 00:00:00.0","ctitle":"葡萄酒节 低至5折","imarketid":"58","iorder":"0","istate":"1","itype":"2"},{"cadddate":"2015-10-21 15:40:31.0","ceditdate":"2015-10-21 15:40:31.0","cenddate":"2015-11-30 00:00:00.0","clogo":"http://hsk.gs.9188.com/imgs/business/2015/9/21/苏果-8bb5e439-02f8-45ec-b89f-79d9d2d6.jpg","clogolist":"http://hsk.gs.9188.com/imgs/business/2015/9/21/jlf_logo@2x-24baa227-ae95-453d-a4bd-cd4662e9.png","cname":"乐购","cstartdate":"2015-11-04 00:00:00.0","ctitle":"212","imarketid":"65","iorder":"0","istate":"1","itype":"2"},{"cadddate":"2015-10-20 17:01:20.0","ceditdate":"2015-10-20 17:01:20.0","cenddate":"2015-10-21 00:00:00.0","clogo":"http://hsk.gs.9188.com/imgs/business/2015/9/20/麦德龙-741b7832-16a0-4eac-8684-b5f8a2f8.jpg","clogolist":"http://hsk.gs.9188.com/imgs/business/2015/9/20/mfl_logo@2x-bda79070-dfde-490e-8438-d4eb4470.png","cname":"麦德龙","cstartdate":"2015-10-08 00:00:00.0","ctitle":"11111111","imarketid":"59","iorder":"0","istate":"1","itype":"2"},{"cadddate":"2015-10-20 17:20:47.0","ceditdate":"2016-10-20 17:47:33.0","cenddate":"2015-10-29 00:00:00.0","clogo":"http://hsk.gs.9188.com/imgs/business/2015/9/20/苏果-79d98b9d-8649-4848-bdd6-645e0f80.jpg","clogolist":"http://hsk.gs.9188.com/imgs/business/2015/9/20/sg_logo@2x-21daed68-aa61-4861-b80b-6f5b57a5.png","cname":"苏果","cstartdate":"2015-10-06 00:00:00.0","ctitle":"风暴商品","imarketid":"60","iorder":"0","istate":"1","itype":"2"},{"cadddate":"2015-10-20 17:36:55.0","ceditdate":"2015-10-20 17:36:55.0","cenddate":"2015-11-23 00:00:00.0","clogo":"http://hsk.gs.9188.com/imgs/business/2015/9/20/屈臣氏-4e4112f2-921d-4e89-9bd4-8bb523ca.jpg","clogolist":"http://hsk.gs.9188.com/imgs/business/2015/9/20/qcs_logo@2x-0f4a13a9-088d-4323-b9b8-a10c350a.png","cname":"屈臣氏","cstartdate":"2015-10-05 00:00:00.0","ctitle":"小图","imarketid":"61","iorder":"0","istate":"1","itype":"2"}]
        } catch (JSONException e){//抛错说明JSON字符不是数组或根本就不是JSON
            try {
                resp.addAttribute("code", "-2");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                resp.addAttribute("code", "-200");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            }
        }finally{
            XmlUtils.writeXml(dom.asXML(), response);
        }
    }

    @RequestMapping("/market/getCheapList.go")
    public void getMarketCheap(MarketBean bean, HttpServletRequest request, HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        try {
            String result = client.execute(new DrpcRequest("MarketBolt", "getMarketCheap", bean));

            if(result != null && !result.equals("null")){
                JSONObject jsonObject = JSONObject.parseObject(result);
                Element respEle = XmlUtils.jsonParseXml(jsonObject, "Resp");
                respEle.addAttribute("code", "1");
                respEle.addAttribute("desc", "查询成功");
                dom.setRootElement(respEle);
            }else{
                resp.addAttribute("code", "-1");
                resp.addAttribute("desc", "不存在的数据");
                dom.setRootElement(resp);
            }
        } catch (JSONException e){//抛错说明JSON字符不是数组或根本就不是JSON
            try {
                resp.addAttribute("code", "-2");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                resp.addAttribute("code", "-200");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            }
        }finally{
            XmlUtils.writeXml(dom.asXML().replaceAll("marketItem","MarketItem").replaceAll("<cheap","<Cheap").replaceAll("cheap>","Cheap>"), response);
        }
    }



    @RequestMapping("/market/indexShopAndMarketList.go")
    public void getHomeShopAndMarketList(MarketBean bean, HttpServletRequest request, HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        try {
            Map<String,Object> map = new HashMap<>();
            map.put("cuserid", bean.getCuserId());
            String result = client.execute(new DrpcRequest("user", "queryComm", map));
            System.out.println(result);

            JSONObject jObject = JSONObject.parseObject(result);
            JSONArray ss =  jObject.getJSONArray("data");
            if(ss!=null) {
                for (int i = 0; i < ss.size(); i++) {
                    JSONObject object = ss.getJSONObject(i);
                    resp.add(XmlUtils.jsonParseXml(object, "PointsItem"));
                }
            }

            String result2 = client.execute(new DrpcRequest("MarketBolt", "getHomeShopAndMarketList", bean));
            System.out.println(result2);

            Element hotNews = new DOMElement("HotNews");
            Element market = new DOMElement("Market");
            Element saleShop  = new DOMElement("SaleShop");
            Element recommendShop = new DOMElement("RecommendShop");

            JSONObject jsonObject = JSONObject.parseObject(result2);

            saleShop.addAttribute("Count", jsonObject.getString("saleShopCount"));
            market.addAttribute("Count", jsonObject.getString("marketCount"));

            JSONArray hotNew =  jsonObject.getJSONArray("hotNews");
            if(hotNew != null && hotNew.size() > 0){
                for(int i=0; i< hotNew.size(); i++){
                    JSONObject object = hotNew.getJSONObject(i);
                    hotNews.add(XmlUtils.jsonParseXml(object, "NewsItem"));
                }
            }
            resp.add(hotNews);

            JSONArray markets =  jsonObject.getJSONArray("market");
            if(markets != null && markets.size() > 0){
                for(int i=0; i< markets.size(); i++){
                    JSONObject object = markets.getJSONObject(i);
                    market.add(XmlUtils.jsonParseXml(object, "MarketItem"));
                }
            }
            resp.add(market);

            JSONArray saleShops =  jsonObject.getJSONArray("saleShop");
            if(saleShops != null && saleShops.size() > 0){
                for(int i=0; i< saleShops.size(); i++){
                    JSONObject object = saleShops.getJSONObject(i);
                    saleShop.add(XmlUtils.jsonParseXml(object, "ShopItem"));
                }
            }
            resp.add(saleShop);

            JSONArray recommendShops =  jsonObject.getJSONArray("RecommendShop");
            if(recommendShops != null && recommendShops.size() > 0){
                for(int i=0; i< recommendShops.size(); i++){
                    JSONObject object = recommendShops.getJSONObject(i);
                    recommendShop.add(XmlUtils.jsonParseXml(object, "RCShopItem"));
                }
            }
            resp.add(recommendShop);

            dom.setRootElement(resp);
        } catch (JSONException e){//抛错说明JSON字符不是数组或根本就不是JSON
            try {
                resp.addAttribute("code", "-2");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                resp.addAttribute("code", "-200");
                resp.addAttribute("desc", "非法操作");
                dom.setRootElement(resp);
            }
        }finally{
            XmlUtils.writeXml(dom.asXML(), response);
        }
    }
}
