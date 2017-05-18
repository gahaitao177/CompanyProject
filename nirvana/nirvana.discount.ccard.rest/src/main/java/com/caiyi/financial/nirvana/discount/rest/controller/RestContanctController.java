package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.ccard.bean.Contanct;
import com.caiyi.financial.nirvana.discount.ccard.bean.Tool;
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
import java.io.IOException;

/**
 * Created by heshaohua on 2016/6/6.
 */
@RestController
@RequestMapping("/credit")
public class RestContanctController {
    private static Logger LOGGER = LoggerFactory.getLogger(RestContanctController.class);
    @Resource(name = Constant.HSK_CCARD)
    IDrpcClient client;

    /**
     * 微信文章列表 身边有料
     * @param contanct
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wechatMsgList.go")
    @SetUserDataRequired
    public void weChatList(Contanct contanct, HttpServletResponse response, HttpServletRequest request) throws IOException {
//        contanct.setCuserId((String)request.getAttribute("cuserId"));
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        if(StringUtils.isEmpty(contanct.getCuserId())){
            resp.addAttribute("code", "0");
            resp.addAttribute("desc", "未登录");
            LOGGER.info("未登录");
            XmlUtils.writeXml(dom,response);
            return;
        }
        String result = client.execute(new DrpcRequest("contanct", "weChatList_page", contanct));
        try{
            JSONObject jObject = JSONObject.parseObject(result);
            XmlUtils.writeXml(jObject, response);
        }catch(JSONException e){
            resp.addAttribute("code", "-200");
            resp.addAttribute("desc", "非法操作");
            LOGGER.error("wechatMsgList 非法的JSON字符串");
        }
        XmlUtils.writeXml(dom.asXML(), response);
    }

    /**
     * 微信文章点赞收藏
     * @param contanct
     * @param response
     * @throws IOException
     */
    @SetUserDataRequired
    @RequestMapping("/wechatMsg_action.go")
    public void weChatOperate(Contanct contanct, HttpServletResponse response, HttpServletRequest request) throws IOException {
//        contanct.setCuserId((String)request.getAttribute("cuserId"));
        if(StringUtils.isEmpty(contanct.getCuserId())){
            XmlUtils.writeXml("0","未登录", response);
            return;
        }
        String result = client.execute(new DrpcRequest("contanct", "weChatMsg_operate", contanct));
        System.out.println("result==============="+result);
        try{
            JSONObject jObject = JSONObject.parseObject(result);
            XmlUtils.writeXml(jObject.getString("busiErrCode"), jObject.getString("busiErrDesc"), response);
        }catch(JSONException e){
            LOGGER.error("query_area 非法的JSON字符串");
        }
    }

    /**
     * 微信文章收藏列表
     * @param contanct
     * @param response
     * @throws IOException
     */
    @RequestMapping("/coll_wechat_Msg_list.go")
    @SetUserDataRequired
    public void weChatCollList(Contanct contanct, HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(contanct.getCuserId())){
            XmlUtils.writeXml("0","未登录", response);
            return;
        }
        String result = client.execute(new DrpcRequest("contanct", "weChatMsg_operate", contanct));
        System.out.println("result==============="+result);

        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);

        JSONObject jObject = JSONObject.parseObject(result);
        try{
            XmlUtils.writeXml(jObject, response);
        }catch(JSONException e){
            try {
                JSONObject object = JSONObject.parseObject(result);

                resp.addAttribute("code", object.getString("code"));
                resp.addAttribute("desc", object.getString("desc"));
            } catch (JSONException e2) {// 抛错 说明JSON字符根本就不是JSON
                resp.addAttribute("code", "-200");
                resp.addAttribute("desc", "非法操作");
                LOGGER.error("query_area 非法的JSON字符串");
            }
        }
    }
    @RequestMapping("/pv.go")
    public void updateView(HttpServletRequest request,HttpServletResponse response){
        String contactId=request.getParameter("contactId");
        String result="";
        Document dom=DocumentHelper.createDocument();

        Element res=new DOMElement("Resp");
        dom.setRootElement(res);
        try {
             result = client.execute(new DrpcRequest("contanct", "updateView", contactId));
        }catch (Exception e){
            res.addAttribute("code","0");
            res.addAttribute("desc","访问异常");
        }
        JSONObject json=JSONObject.parseObject(result);
        res.addAttribute("code",json.getString("busiErrCode"));
        res.addAttribute("desc",json.getString("busiErrDesc"));
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/qstrategy.go")
    public void queryCategory(HttpServletResponse response,HttpServletRequest request){
        Contanct contanct=new Contanct();
        Document dom=DocumentHelper.createDocument();
        Element res=new DOMElement("Resp");
        dom.setRootElement(res);
        String category=request.getParameter("category");
        Integer ps=10;
        Integer pn=1;
        if(null==category||"".equals(category)){
            res.addAttribute("code","0");
            res.addAttribute("desc","种类不能空");
        }
        try{
            ps=Integer.parseInt(request.getParameter("ps"));
            pn=Integer.parseInt(request.getParameter("pn"));
        }catch (Exception e){
             ps=10;
             pn=1;
        }
        contanct.setPn(pn);
        contanct.setPs(ps);
        contanct.setCategory(category);
        String result="";
        try {
            result = client.execute(new DrpcRequest("contanct", "queryCategory", contanct));
        }catch (Exception e){
            res.addAttribute("code","0");
            res.addAttribute("desc","访问异常");
        }
        JSONObject json=JSONObject.parseObject(result);
        Element count=new DOMElement("count");
        count.addAttribute("tp",json.get("totalPage").toString());
        count.addAttribute("rc",json.get("records").toString());
        count.addAttribute("pn",json.get("pageNum").toString());
        count.addAttribute("ps",contanct.getPs().toString());
        Integer records=0;
        try{
            records= json.getInteger("records");
        }catch (Exception e){
            res.addAttribute("code","0");
            res.addAttribute("desc","查询失败");
        }
        if (records>=0){
            res.addAttribute("code","1");
            res.addAttribute("desc","查询成功");
        }
        res.add(count);
        JSONArray jsonList=json.getJSONArray("rows");
        Element nodes=new DOMElement("nodes");
        for(int i=0;i<jsonList.size();i++){
            JSONObject jsonObject=jsonList.getJSONObject(i);
            Element node=new DOMElement("node");
            node.addAttribute("contactId",jsonObject.get("ICONTACTID")==null?"":jsonObject.get("ICONTACTID").toString());
            node.addAttribute("title",jsonObject.get("CTITLE")==null?"":jsonObject.get("CTITLE").toString());
            node.addAttribute("content",jsonObject.get("CSUMMARY")==null?"":jsonObject.get("CSUMMARY").toString());
            node.addAttribute("url", jsonObject.get("CURL")==null?"":jsonObject.get("CURL").toString());
            node.addAttribute("author", jsonObject.get("CAUTHOR")==null?"":jsonObject.get("CAUTHOR").toString());
            node.addAttribute("type", jsonObject.get("ITYPE")==null?"":jsonObject.get("ITYPE").toString());
            node.addAttribute("picUrl", jsonObject.get("CPICURL")==null?"":jsonObject.get("CPICURL").toString());
            node.addAttribute("createdTime", jsonObject.get("DCREATEDTIME")==null?"":jsonObject.get("DCREATEDTIME").toString());
            node.addAttribute("published", jsonObject.get("IPUBLISHED")==null?"":jsonObject.get("IPUBLISHED").toString());
            node.addAttribute("origin", jsonObject.get("CORIGIN")==null?"":jsonObject.get("CORIGIN").toString());
            node.addAttribute("publishedTime", jsonObject.get("CPUBLISHEDTIME")==null?"":jsonObject.get("CPUBLISHEDTIME").toString());
            node.addAttribute("accessUrl", jsonObject.get("CACCESSULR")==null?"":jsonObject.get("CACCESSULR").toString());
            node.addAttribute("views", jsonObject.get("IVIEWS")==null?"":jsonObject.get("IVIEWS").toString());
            node.addAttribute("iconUrl", jsonObject.get("CICONURL")==null?"":jsonObject.get("CICONURL").toString());
            nodes.add(node);
        }
        res.add(nodes);
        dom.setRootElement(res);
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/tool_article.go")
    public  void queryToolArticle(HttpServletRequest request,HttpServletResponse response){
        Document dom=DocumentHelper.createDocument();
        Element res=new DOMElement("Resp");
        dom.setRootElement(res);
        String toolid=request.getParameter("toolid");
        String typeid=request.getParameter("typeid");
        String source=request.getParameter("source");
        String bversion=request.getParameter("bversion");
        Tool tool=new Tool();
        tool.setToolid(toolid);
        tool.setTypeid(typeid);
        tool.setCsource(source);
        tool.setBversion(bversion);
        if(toolid==null||"".equals(toolid)){
            res.addAttribute("code","0");
            res.addAttribute("desc","参数错误");
        }
        String result=client.execute(new DrpcRequest("contanct","queryToolArticle",tool));
        JSONObject json=JSONObject.parseObject(result);
        if (json.get("Type")!=null){
            Element typeElement=new DOMElement("Type");
            JSONArray typeList=json.getJSONArray("Type");
            for (int i=0;i<typeList.size();i++){
                Element typeItem=new DOMElement("TypeItem");
                JSONObject type=typeList.getJSONObject(i);
                typeItem.addAttribute("typeid",type.get("TYPEID")==null?"":type.getString("TYPEID"));
                typeItem.addAttribute("typename",type.get("TYPENAME")==null?"":type.getString("TYPENAME"));
                typeItem.addAttribute("typelogo",type.get("TYPELOGO")==null?"":type.getString("TYPELOGO"));
                typeElement.add(typeItem);
            }
            res.add(typeElement);
        }
        if (json.get("B")!=null){
            JSONArray versionList=json.getJSONArray("B");
            Element versionElement=new DOMElement("B");
            for (int i=0;i<versionList.size();i++){
                Element versionItem=new DOMElement("BItem");
                JSONObject type=versionList.getJSONObject(i);
                versionItem.addAttribute("bitemtitle",type.get("BITEMTITLE")==null?"":type.getString("BITEMTITLE"));
                versionItem.addAttribute("bitempic",type.get("BITEMPIC")==null?"":type.getString("BITEMPIC"));
                versionItem.addAttribute("bitemtarget",type.get("BITEMTARGET")==null?"":type.getString("BITEMTARGET"));
                versionElement.add(versionItem);
            }
            res.add(versionElement);
        }
        if (json.get("Article")!=null){
            JSONArray articleList=json.getJSONArray("Article");
            Element articleElement=new DOMElement("Article");
            for (int i=0;i<articleList.size();i++){
                Element articleItem=new DOMElement("BItem");
                JSONObject type=articleList.getJSONObject(i);
                articleItem.addAttribute("articletitle",type.get("ARTICLETITLE")==null?"":type.getString("ARTICLETITLE"));
                articleItem.addAttribute("articlesubtitle",type.get("ARTICLESUBTITLE")==null?"":type.getString("ARTICLESUBTITLE"));
                articleItem.addAttribute("articlepic",type.get("ARTICLEPIC")==null?"":type.getString("ARTICLEPIC"));
                articleItem.addAttribute("artitleurl",type.get("ARTITLEURL")==null?"":type.getString("ARTITLEURL"));
                articleElement.add(articleItem);
            }
            res.add(articleElement);
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/article_click.go")
    public void articleClick(HttpServletRequest request,HttpServletResponse response) {
        String articleid = request.getParameter("articleid");
        String result = "";
        Document dom = DocumentHelper.createDocument();
        Element res = new DOMElement("Resp");
        dom.setRootElement(res);
        if(articleid==null||"".equals(articleid)){
            res.addAttribute("code", "0");
            res.addAttribute("desc", "参数错误");
            XmlUtils.writeXml(dom, response);
            return;
        }
        try {
            result = client.execute(new DrpcRequest("contanct", "articleClickCount", articleid));
        } catch (Exception e) {
            res.addAttribute("code", "0");
            res.addAttribute("desc", "访问异常");
        }
        JSONObject json = JSONObject.parseObject(result);
        res.addAttribute("code", json.getString("code"));
        res.addAttribute("desc", json.getString("desc"));
        XmlUtils.writeXml(dom, response);
    }
    @RequestMapping("/contacts.go")
   public void  queryContacts(HttpServletResponse response,HttpServletRequest request){
       Document dom = DocumentHelper.createDocument();
       Element res = new DOMElement("Resp");
       dom.setRootElement(res);
       Contanct constant=new Contanct();
       String position=request.getParameter("position");
       String type=request.getParameter("type");
       String ps=request.getParameter("ps");
       String pn=request.getParameter("pn");
       if(StringUtils.isNumeric(type)){
           constant.setType(Integer.parseInt(type));
       }
        if(StringUtils.isNumeric(ps)){
            constant.setPs(Integer.parseInt(ps));
        }else {
            constant.setPs(1);
        }
        if(StringUtils.isNumeric(pn)){
            constant.setPn(Integer.parseInt(pn));
        }else {
            constant.setPn(25);
        }
       constant.setPosition(position);
       String rt=client.execute(new DrpcRequest("contanct","queryContacts",constant));
        JSONObject json= JSON.parseObject(rt);
       if(json!=null){
           Element count=new DOMElement("count");
           count.addAttribute("ps",json.getString("pageSize"));
           count.addAttribute("pn",json.getString("pageNum"));
           count.addAttribute("tp",json.getString("totalPage"));
           count.addAttribute("rc",json.getString("records"));
           if(json.getJSONArray("rows")!=null){
               JSONArray data=json.getJSONArray("rows");
               Element nodes=new DOMElement("nodes");
               JsonUtil.jsonToElement(data,nodes,"node",null);
               res.add(nodes);
               res.addAttribute("code","1");
               res.addAttribute("desc","查询成功");
           }else {
               res.addAttribute("code","0");
               res.addAttribute("desc","没有查询到有效信息");
           }
       }else{
           res.addAttribute("code","-1");
           res.addAttribute("desc","程序异常");
       }
        XmlUtils.writeXml(dom,response);
   }

}
