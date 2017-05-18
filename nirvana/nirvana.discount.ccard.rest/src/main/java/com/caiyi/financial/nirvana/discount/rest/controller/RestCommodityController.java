package com.caiyi.financial.nirvana.discount.rest.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.discount.ccard.bean.Commodity;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
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
import java.text.DecimalFormat;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * Created by lizhijie on 2016/5/31.
 */
@RestController
@RequestMapping("/credit")
public class RestCommodityController {
    private static Logger LOGGER = LoggerFactory.getLogger(RestCommodityController.class);

    @Resource(name = Constant.HSK_CCARD)
    IDrpcClient client;

    @RequestMapping("/jfindex.go")
    @SetUserDataRequired
    public  void queryPointsByUser(Commodity commodity, HttpServletResponse response, HttpServletRequest request) {
//        String cuserId = request.getParameter("cuserId");
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        String str = client.execute(new DrpcRequest("commodity","queryPointsByUser",commodity));

        JSONObject jsonObject = parseObject(str);
        if (jsonObject.get("commodity")!=null||jsonObject.get("commodityBank")!=null) {
            resp.addAttribute("code","1");
            resp.addAttribute("desc","积分查询成功");
            if (jsonObject.get("commodity") != null) {
                Element myaccount = new DOMElement("myaccount");
                JSONArray pointList = JSON.parseArray(jsonObject.get("commodity").toString());
                double count=0D;

                for (int i = 0; i < pointList.size(); i++) {
                    if(pointList.getJSONObject(i).getString("points")!=null)
                    count+= pointList.getJSONObject(i).getDouble("points");
                    myaccount.add(XmlUtils.jsonParseXml(pointList.getJSONObject(i), "bankitem"));
                }
                myaccount.addAttribute("totalpoints",new DecimalFormat("######0.00").format(count));
                resp.add(myaccount);
            }
            if (jsonObject.get("commodityBank") != null) {
                JSONArray pointBankList = JSON.parseArray(jsonObject.get("commodityBank").toString());
                if(pointBankList.size()==0){
                    resp.addAttribute("code","0");
                    resp.addAttribute("desc","没有获得有效积分信息");
                }else {
                    Element fonta = new DOMElement("fonta");
                    for (int j = 0; j < pointBankList.size(); j++) {
                        Object bankPoint0 = pointBankList.get(j);
                        JSONObject bankPoint= JSON.parseObject(bankPoint0.toString());
                        Element fontaitem = new DOMElement("fontaitem");
                        if (bankPoint.get("exchangetypeitem") != null) {
                            JSONArray children = bankPoint.getJSONArray("exchangetypeitem");
                            fontaitem.setContent(XmlUtils.jsonParseXml(children, "exchangetypeitem"));
                        }
                        bankPoint.remove("exchangetypeitem");
                        for (String key : bankPoint.keySet()) {
                            fontaitem.addAttribute(key, bankPoint.getString(key));
                        }
                        fonta.add(fontaitem);

                    }
                    resp.add(fonta);
                    dom.setRootElement(resp);
                }
            }
            dom.setRootElement(resp);
        }else{
            resp.addAttribute("code","0");
            resp.addAttribute("desc","积分查询失败");
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("jfbanks.go")
    public  void banks(HttpServletRequest request,HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        String str = client.execute(new DrpcRequest("commodity","queryPointsAndBanks"));

        JSONArray bankPointList = JSONArray.parseArray(str);
        if (bankPointList!=null) {
            resp.addAttribute("code","1");
            resp.addAttribute("desc","积分查询成功");
            int curbid=-1;
            Element bank= new DOMElement("bankitem");
            for (int i = 0; i < bankPointList.size(); i++) {
                JSONObject bankPoint=bankPointList.getJSONObject(i);
                int ibankid = bankPoint.getInteger("ibankid");
                int icateid = bankPoint.getInteger("icateid");
                String cbankname = bankPoint.getString("cbankName");
                String ccategory = bankPoint.getString("ccategory");
                if(curbid!=ibankid) {
                    bank = new DOMElement("bankitem");
                    bank.addAttribute("ibankid", ibankid + "");
                    bank.addAttribute("cbankname", cbankname);
                    resp.add(bank);
                }else {
                    Element bankitem = new DOMElement("classificationitem");
                    bankitem.addAttribute("ibankid", ibankid + "");
                    bankitem.addAttribute("icateid", icateid + "");
                    bankitem.addAttribute("ccategory", ccategory);
                    bank.add(bankitem);
                }
                curbid = ibankid;
            }
            dom.setRootElement(resp);
        }else{
            resp.addAttribute("code","0");
            resp.addAttribute("desc","积分查询失败");
        }
        XmlUtils.writeXml(dom,response);
    }

    @SetUserDataRequired
    @RequestMapping("jfcomms.go")
    public  void queryPointsList(Commodity commodity,HttpServletResponse response,HttpServletRequest request){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        String cuserId=request.getParameter("cuserId");
        commodity.setCuserId(cuserId);
        if(null==commodity.getPs()){
            commodity.setPs(10);
        }
        if(null==commodity.getPn()){
            commodity.setPn(1);
        }
        if(StringUtils.isEmpty(commodity.getCcategory())){
            commodity.setCcategory(null);
        }
        String str = client.execute(new DrpcRequest("commodity","queryPointsList",commodity));
        JSONObject jsonObject=JSONObject.parseObject(str);
        if (jsonObject.getJSONArray("data")!=null) {
            Element count=new DOMElement("count");
            count.addAttribute("tp",jsonObject.getString("tp"));
            count.addAttribute("rc",jsonObject.getString("rc"));
            count.addAttribute("pn",jsonObject.getString("pn"));
            count.addAttribute("ps",jsonObject.getString("ps"));
            resp.add(count);
            JSONArray pointBankList=jsonObject.getJSONArray("data");
            if(pointBankList.size()==0){
                resp.addAttribute("code","0");
                resp.addAttribute("desc","没有获得有效积分信息");
            }else {
                resp.addAttribute("code", "1");
                resp.addAttribute("desc", "积分查询成功");
                for (int j = 0; j < pointBankList.size(); j++) {
                    Object bankPoint0 = pointBankList.get(j);
                    JSONObject bankPoint= JSON.parseObject(bankPoint0.toString());
                    Element fontaitem = new DOMElement("fontaitem");
                    if (bankPoint.get("exchangetypeitem") != null) {
                        JSONArray children = bankPoint.getJSONArray("exchangetypeitem");
                        fontaitem.setContent(XmlUtils.jsonParseXml(children, "exchangetypeitem"));
                    }
                    bankPoint.remove("exchangetypeitem");
                    for (String key : bankPoint.keySet()) {
                        fontaitem.addAttribute(key, bankPoint.getString(key));
                    }
                    resp.add(fontaitem);
                }
                dom.setRootElement(resp);
            }
        }else{
            resp.addAttribute("code","0");
            resp.addAttribute("desc","积分查询失败");
        }
        dom.setRootElement(resp);
        XmlUtils.writeXml(dom,response);
    }

    @SetUserDataRequired
    @RequestMapping("jfdetail.go")
    public  void queryCommodityDetail(HttpServletRequest request,HttpServletResponse response){
        String icommid=request.getParameter("icommid");
        String str = client.execute(new DrpcRequest("commodity","queryCommodityDetail",icommid));
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        JSONObject bankPointDetail = JSONObject.parseObject(str);
        if (bankPointDetail!=null&&bankPointDetail.size()>0) {
            resp.addAttribute("code","1");
            resp.addAttribute("desc","积分查询成功");
            Element fontadetail = new DOMElement("fontadetail");
            JSONObject detailURL=JSON.parseObject(bankPointDetail.get("detailpic")==null?"":bankPointDetail.getString("detailpic"));
            for (String url:detailURL.keySet()){
                Element detailpic = new DOMElement("detailpic");
                detailpic.addAttribute("src",detailURL.getString(url));
                fontadetail.add(detailpic);
            }
            bankPointDetail.remove("detailpic");
            for (String attr:bankPointDetail.keySet()){
                fontadetail.addAttribute(attr,bankPointDetail.getString(attr));
            }
            resp.add(fontadetail);

        }else{
            resp.addAttribute("code","0");
            resp.addAttribute("desc","没有查询到该积分相关信息");
        }
        dom.setRootElement(resp);
        XmlUtils.writeXml(dom,response);
    }
}
