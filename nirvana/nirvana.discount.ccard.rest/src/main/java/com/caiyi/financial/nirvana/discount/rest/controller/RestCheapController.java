package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Store;
import com.caiyi.financial.nirvana.discount.ccard.bean.Window;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by heshaohua on 2016/5/3.
 * update by lcs 20160804 add method totalSearch
 */
@RestController
@RequestMapping("/credit")
public class RestCheapController {
    private static Logger logger = LoggerFactory.getLogger(RestCheapController.class);

    @Resource(name = Constant.HSK_CCARD)
    IDrpcClient client;
    @Resource(name = Constant.HSK_CCARD_INFO)
    IDrpcClient card_info_client;
    @Resource(name = Constant.HSK_CCARD_MATERIAL)
    IDrpcClient material_client;

    /**
     * 地区级连
     * @param cheap
     * @return
     */
    @RequestMapping("/area.go")
    public void query_area(Cheap cheap, HttpServletResponse response) throws IOException{
        String result = client.execute(new DrpcRequest("cheap", "query_area", cheap));
        System.out.println("result="+result);

        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        JSONArray jsonArray = null;
        try{
            jsonArray=JSONObject.parseArray(result);
            if(jsonArray!=null&&jsonArray.size()>0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    resp.add(XmlUtils.jsonParseXml(jsonObject, "parent"));
                }
                resp.addAttribute("code", "1");
                resp.addAttribute("desc", "获取地区信息成功");
                logger.info("地区信息不分页获得成功,地区号={}", cheap.getIpareaid());
            }else {
                resp.addAttribute("code", "1");
                resp.addAttribute("desc", "没有符合条件的信息");
                logger.info("地区信息不分页获得成功,地区号={}", cheap.getIpareaid());
            }
        }catch(JSONException e){//抛错说明JSON字符不是数组或根本就不是JSON
            try {
                JSONObject object=JSONObject.parseObject(result);
                jsonArray=JSONObject.parseArray(object.getString("rows"));
                for(int i=0; i< jsonArray.size(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    resp.add(XmlUtils.jsonParseXml(jsonObject, "parent"));
                }
                resp.addAttribute("code", "1");
                resp.addAttribute("desc", "获取地区信息成功");
                logger.info("地区信息分页获得成功,地区号={}",cheap.getIpareaid());
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                resp.addAttribute("code", "-200");
                resp.addAttribute("desc", "非法操作");
                logger.error("query_area 非法的JSON字符串");
            }
        }
        dom.setRootElement(resp);
        XmlUtils.writeXml(dom, response);
    }

    /**
     * 门店列表
     * @param store
     * @param response
     * @throws IOException
     */
    @RequestMapping("/storelist.go")
    public void list_store(Store store, HttpServletResponse response) throws IOException {
        logger.debug("pageNum===="+store.getPageNum());
        logger.debug("pageSize===="+store.getPageSize());
        logger.debug("ibusinessid===="+store.getIbusinessid());
        logger.debug("icityid===="+store.getIcityid());
        String str = client.execute(new DrpcRequest("cheap","storeList",store));

        JSONObject jsonObject = JSONObject.parseObject(str);
        XmlUtils.writeXml(jsonObject,response);
    }


    public static void main(String[] args) {
       // String s = "{}";
        String str = "[{'adcode':'510100','child':[{'adcode':'310101','careaname':'淮海路','citycode':'021','clat':'31.220645','clng':'121.470498','iareaid':'1010000','iareatype':'3','icount':'64','ipareaid':'10100','iroot':'0'}], 'careaname':'成都市','child':[{'adcode':'310101','careaname':'淮海路','citycode':'021','clat':'31.220645','clng':'121.470498','iareaid':'1010000','iareatype':'3','icount':'64','ipareaid':'10100','iroot':'0'}],'citycode':'028','clat':'30.659462','clng':'104.065735','iareaid':'113','iareatype':'1','icount':'0','ipareaid':'0','iroot':'1'},{'adcode':'210200','careaname':'大连市','citycode':'0411','clat':'38.91459','clng':'121.618622','iareaid':'110','iareatype':'1','icount':'0','ipareaid':'0','iroot':'1'},{'adcode':'330200','careaname':'宁波市','citycode':'0574','clat':'29.868388','clng':'121.549792','iareaid':'111','iareatype':'1','icount':'0','ipareaid':'0','iroot':'1'}]";
        JSONArray jsonArray = JSONObject.parseArray(str);

        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        resp.addAttribute("code", "1");
        resp.addAttribute("desc", "");
/**
        for(int i=0; i< jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray jsonChildArray = jsonObject.getJSONArray("areaDtoList");

            resp.add(XmlUtils.jsonParseXml(jsonObject, "parent"));
            if(jsonChildArray != null){
                for(int j=0; j<jsonChildArray.size(); j++){
                    resp.add(XmlUtils.jsonParseXml(jsonChildArray.getJSONObject(j), "child"));
                }
            }
        }
 **/



        for(int i=0; i< jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            resp.add(XmlUtils.jsonParseXml(jsonObject, "parent"));
         }
        dom.setRootElement(resp);
            System.out.println(dom.asXML());


        /**for(Map.Entry<String, Object> entry : jsonObject.entrySet()){
            String key = entry.getKey();
            System.out.println(key);
            System.out.println(entry);
            String jsonkey = JSON.toJSONString(key, true);
            System.out.println(jsonkey);

            JSONArray keyjson = JSONArray.parseArray(jsonkey) ;
            System.out.println(" keyjson.size():"+ keyjson.size());

            JSONArray jsonArray = (JSONArray) entry.getValue();
            for(int i = 0,size = jsonArray.size();i<size;i++){
                rows.add(XmlUtils.jsonParseXml(jsonArray.getJSONObject(i),"parent"));
            }
        }**/



    }


    /**
     * 优惠详情接口
     * @param cheap
     * @param response
     * @return
     */
    @SetUserDataRequired
    @RequestMapping("/cheap.go")
    public void cheap(Cheap cheap, HttpServletResponse response){
        Element resp = new DOMElement("Resp");
        Document dom = DocumentHelper.createDocument();
        dom.setRootElement(resp);
        String str = client.execute(new DrpcRequest("cheap","cheap",cheap));
        JSONObject jsonObject = JSONObject.parseObject(str);
        String code = jsonObject.getString("code");

        if(StringUtils.isEmpty(code)){
            resp.addAttribute("code","1");
            resp.addAttribute("desc","请求成功");

            Element rows = XmlUtils.jsonParseXml(jsonObject,"rows");
            resp.add(rows);
            dom.setRootElement(resp);

        }else{
            //存在code就为失败
            String desc = jsonObject.getString("desc");
            resp.addAttribute("code","0");
            resp.addAttribute("desc",desc != null ? desc : "执行失败");
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/qwindowpage.go")
    public void qpage(Window window, HttpServletResponse response){
        String str=client.execute(new DrpcRequest("cheap","qpage",window) );
        JSONObject json= JSONObject.parseObject(str);

        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        Element pageinfo = new DOMElement("pageinfo");
        pageinfo.addAttribute("tp",json.getString("tp"));
        pageinfo.addAttribute("rc",json.getString("rc"));
        pageinfo.addAttribute("pn",json.getString("pn"));
        pageinfo.addAttribute("ps",json.getString("ps"));
        resp.add(pageinfo);
        JSONArray topicList= (JSONArray) json.get("topicList");
        if(topicList!=null&&topicList.size()>0){
            resp.addAttribute("code","1");
            resp.addAttribute("desc","获得橱窗信息成功");
//            resp.setText(json.get("busiXml").toString());
            for(Object  t:topicList){
                JSONObject topic= JSONObject.parseObject(t.toString());
                resp.add(XmlUtils.jsonParseXml(topic,"rows"));
            }
            JSONArray topicBusiList= (JSONArray) json.get("topicBussiDtoList");
            for(Object  t2:topicBusiList){
                JSONObject topicBuss= JSONObject.parseObject(t2.toString());
                resp.add(XmlUtils.jsonParseXml(topicBuss,"row"));
            }
        }else{
            String desc = json.getString("desc");

            resp.addAttribute("code","0");
            resp.addAttribute("desc",desc != null ? desc : "没有获得有效数据");
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/topicClick.go")
    public void topicClick(Window window,HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        //topicId=204 测试id
        String str=client.execute(new DrpcRequest("cheap","topicClick",window.getTopicId()) );
        JSONObject json= JSONObject.parseObject(str);
        if(json.get("count")!=null&&"1".equals(json.get("count"))){
            dom.setRootElement(resp);
            resp.addAttribute("code","1");
            resp.addAttribute("desc","更新点击量成功");
        }else{
            dom.setRootElement(resp);
            resp.addAttribute("code","0");
            resp.addAttribute("desc","更新点击量失败");
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/qtopicInfo.go")
    public void getTopicInfo(Window window,HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        window=new Window();
        window.setTopicId("230");
        window.setPn(1);
        window.setPs(10);
        String str=client.execute(new DrpcRequest("window","getTopicInfo",window) );
        JSONObject json= JSONObject.parseObject(str);
        if(json.get("code")!=null&&"1".equals(json.get("code"))){
            dom.setRootElement(resp);
            resp.addAttribute("code","1");
            resp.addAttribute("desc","获取专题关联商家成功");
            resp.setText(json.get("busiXml").toString());
//            resp.addAttribute("data",str);
        }else{
            dom.setRootElement(resp);
            resp.addAttribute("code","0");
            resp.addAttribute("desc","获取专题关联商家失败");
            resp.addAttribute("data",str);
        }
        XmlUtils.writeXml(dom,response);
    }

    @RequestMapping("/keyworks.go")
    public void query_searchKeyWorks(HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        String str=client.execute(new DrpcRequest("cheap","query_searchKeyWorlds") );
        JSONObject json= JSONObject.parseObject(str);
        dom.setRootElement(resp);
        if(json.get("code")!=null&&"1".equals(json.get("code"))){
            resp.addAttribute("code","1");
            resp.addAttribute("desc","专题明细分发获得成功");
            resp.setText(json.get("busiXml").toString());
        }else{
            resp.addAttribute("code","0");
            resp.addAttribute("desc","专题明细获得失败");
        }
        XmlUtils.writeXml(dom,response);
    }

    @SetUserDataRequired
    @RequestMapping("/keys.go")
    public void query_search(Cheap cheap,HttpServletResponse response){
//        query=星巴克&icityid=101&pn=2&ps=10&clat=31.22342&clng=121.53454
//        cheap=new Cheap();
//        cheap.setQuery("星巴克");
//        cheap.setCityid(101);
//        cheap.setPn(2);
//        cheap.setPs(10);
//        cheap.setClng(121.53454);
//        cheap.setClat(31.22342);
        Element resp = new DOMElement("Resp");
        Document dom = DocumentHelper.createDocument();
        dom.setRootElement(resp);
        String cuserid=cheap.getCuserId();
        if(StringUtils.isEmpty(cuserid)){
            resp.addAttribute("code","0");
            resp.addAttribute("desc","未登录");
            XmlUtils.writeXml(dom,response);
            return;
        }
        String re=client.execute(new DrpcRequest("cheap","query_result",cheap));

        resp.addAttribute("code","1");
        resp.addAttribute("desc","获得橱窗信息成功");
        JSONObject result=JSONObject.parseObject(re);
        int count = Integer.parseInt(result.get("rc").toString());

        if("1".equals(result.get("busiErrCode"))) {
            if (result.get("data")!=null) {
                JSONArray list=JSONObject.parseArray(result.get("data").toString());
                Element rows = new DOMElement("rows");
                rows.addAttribute("rc",result.get("rc").toString());
                rows.addAttribute("tp",result.get("tp")+"");
                rows.addAttribute("pn",result.get("pn")+"");
                rows.addAttribute("ps",result.get("ps")+"");
                rows.addAttribute("ccount", result.get("ccount")+"");
                for (Object o : list) {
                    rows.add(XmlUtils.jsonParseXml(JSONObject.parseObject(o.toString()), "row"));
                }
                resp.add(rows);
            }

        }else{
            resp.addAttribute("code","0");
            resp.addAttribute("desc","未获得优惠");
        }
        XmlUtils.writeXml(dom,response);
    }

    /**
     *
     * @param cheap
     * @param response
     * add by lcs 20160805  全局搜索
     */
    @RequestMapping("/totalSearch.go")
    public String totalSearch(Cheap cheap,HttpServletResponse response){
        JSONObject result=new JSONObject();
        String query = cheap.getQuery();
        logger.info("query:" + query + ",cityid:" + cheap.getCityid());
        cheap.setPs(2);
        cheap.setPn(1);
        JSONArray rt=new JSONArray();
        MaterialBean materialBean = new MaterialBean();
        materialBean.setSource(cheap.getSource());
        materialBean.setPn(cheap.getPn());
        materialBean.setPs(cheap.getPs());
        materialBean.getModel().setIcityid(String.valueOf(cheap.getIcityid()));
        materialBean.setCname(cheap.getQuery());
        String materialResult = material_client.execute(new DrpcRequest("material", "filterCard", materialBean));
        JSONObject jsonObject = JSONObject.parseObject(materialResult);
        if (jsonObject != null && jsonObject.getJSONArray("cards")!=null) {
            JSONObject dataJson = new JSONObject();
            dataJson.put("contents", jsonObject.get("cards"));
            dataJson.put("type", 1);
            dataJson.put("isMore", 0);
            int count = jsonObject.getIntValue("cardstotal");
            if (count > 2) {
                dataJson.put("isMore", 1);
            }
            if("1".equals(cheap.getSearchtype())) {
                dataJson.put("pn", jsonObject.getString("pageNum"));
                dataJson.put("tp", jsonObject.getString("totalPage"));
                dataJson.put("ps", jsonObject.getString("pageSize"));
                dataJson.put("rc", count);
            }
            rt.add(dataJson);
        }
        String cheapStr=client.execute(new DrpcRequest("window","totalSearch",cheap));
        JSONObject json=JSON.parseObject(cheapStr);
        if("1".equals(json.getString("code"))&&json.getJSONArray("data")!=null){
            logger.info("size:"+rt.size());
            if(rt.size()>0) {
                json.getJSONArray("data").add(rt.getJSONObject(0));
            }
            result.put("code","1");
            result.put("desc","查询成功");
            result.put("data",json.getJSONArray("data"));
        }else {
            if(rt.size()==0){
                result.put("code","0");
                result.put("desc","查询失败");
            }else {
                result.put("code","1");
                result.put("desc","查询成功");
                result.put("data",rt);
            }
        }
        return result.toJSONString();
    }
   /* public String totalSearch(Cheap cheap,HttpServletResponse response){
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", "1");
        resultJson.put("desc", "查询成功");
        String query = cheap.getQuery();
        logger.info("query:" + query + ",cityid:" + cheap.getCityid());
        JSONArray jsonArray = new JSONArray();

        MaterialBean material = new MaterialBean();
        material.setCname(query);
        material.getModel().setIcityid(String.valueOf(cheap.getCityid()));
        material.setPs(2);
        material.setPn(1);
        JSONObject cardJson = new JSONObject();
        cardJson.put("type","1");
        cardJson.put("isMore","0");
        cardJson.put("contents", new JSONArray());
        String materialRes =  material_client.execute(new DrpcRequest("material","filterCard",material));
        logger.info("totalSearch materialRes:"  +JSON.toJSONString(materialRes));
        JSONObject mJSon = JSONObject.parseObject(materialRes);
        if (mJSon != null &&!"null".equals(mJSon) && mJSon.containsKey("cards")){
            cardJson.put("contents", mJSon.getJSONArray("cards"));
            int rs = mJSon.getInteger("cardstotal");
            if (rs > 2){
                cardJson.put("isMore","1");
            }
        }
        jsonArray.add(cardJson);
        // 查询卡神攻略
        Contanct contanct = new Contanct();
        contanct.setTitle(query);
        contanct.setPs(2);
        contanct.setPn(1);
        String contactRe = client.execute(new DrpcRequest("contanct","queryForTotalsearch",contanct));
        JSONObject contactresult = JSONObject.parseObject(contactRe);
        logger.info("totalSearch queryForTotalsearch:"  +JSON.toJSONString(contactresult));
        JSONObject contachJson = new JSONObject();
        contachJson.put("type","3");
        contachJson.put("isMore","0");
        if (contactresult != null && contactresult.containsKey("rows")){
            contachJson.put("contents",contactresult.getJSONArray("rows"));
            int rs = contactresult.getInteger("records");
            if (rs > 2){
                contachJson.put("isMore","1");
            }
        }else {
            contachJson.put("contents",new JSONArray());
        }
        jsonArray.add(contachJson);
        // 查询优惠信息
        cheap.setPs(2);
        cheap.setPn(1);
        if (cheap.getClng() == null){
            cheap.setClng(121.53454);
        }
        if (cheap.getClat() == null){
            cheap.setClat(31.22342);
        }
        cheap.setQuery(query);
        String cheapRe =client.execute(new DrpcRequest("cheap","query_result",cheap));
        JSONObject cheapresult = JSONObject.parseObject(cheapRe);
        logger.info("totalSearch query_result:"  +JSON.toJSONString(cheapresult));
        JSONObject cheapJson = new JSONObject();
        cheapJson.put("type","2");
        cheapJson.put("isMore","1");
        if (cheapresult != null && cheapresult.containsKey("data")){
            cheapJson.put("contents",cheapresult.getJSONArray("data"));
        }else {
            cheapJson.put("contents", new JSONArray());
        }
        jsonArray.add(cheapJson);
        resultJson.put("data",jsonArray);
        return resultJson.toString();
    }*/
    @RequestMapping("/dauStatistics.go")
    public String saveUserStatistics(HttpServletResponse response, HttpServletRequest request){
        String uid=request.getParameter("uid");
        String info1=request.getParameter("info1");
        String type=request.getParameter("type");
        JSONObject jsonObject=new JSONObject();
        if(StringUtils.isEmpty(uid)&&StringUtils.isEmpty(info1)){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数不能为空");
            return  jsonObject.toJSONString();
        }
        Map<String,String> para=new HashMap<>();
        para.put("uid",uid);
        para.put("info1",info1);
        if("ios".equals(type)){
            para.put("type","2");
        }else {
            para.put("type","1");
        }
        String result=client.execute(new DrpcRequest("window","saveUserStatistics",para));
        return  result;
    }
}
