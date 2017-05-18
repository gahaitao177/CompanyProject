package com.caiyi.financial.nirvana.discount.utils;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.junit.Test;

/**
 * Created by wenshiliang on 2016/8/25.
 */
public class XmlUtilsTest {

    @Test
    public void testJsonParseXml() throws Exception {
        String str = "{\"listMarketDto\":[{\"cadddate\":\"2015-10-20 17:47:57.0\",\"ceditdate\":\"2015-10-20 17:47:57.0\",\"cedituser\":\"susususu\",\"cenddate\":\"2015-10-23 00:00:00.0\",\"cstartdate\":\"2015-10-06 00:00:00.0\",\"ctitle\":\"惊喜价\",\"icheapid\":\"765\",\"images\":[{\"cimgurl\":\"http://hsk.gs.9188.com/imgs/business/2015/9/20/1-1e8100de-5efa-4247-a6f0-991da4a3.jpg\",\"icheapid\":\"765\",\"imgid\":\"1204\"},{\"cimgurl\":\"http://hsk.gs.9188.com/imgs/business/2015/9/20/2-3a721811-bfcf-46ad-87e2-2ed88332.jpg\",\"icheapid\":\"765\",\"imgid\":\"1207\"},{\"cimgurl\":\"http://hsk.gs.9188.com/imgs/business/2015/9/20/3-5b34a1c4-a992-412d-be69-200221e5.jpg\",\"icheapid\":\"765\",\"imgid\":\"1206\"},{\"cimgurl\":\"http://hsk.gs.9188.com/imgs/business/2015/9/20/4-1540ffd4-34dc-44a8-a2aa-3356cf81.jpg\",\"icheapid\":\"765\",\"imgid\":\"1205\"}],\"imarketid\":\"62\",\"istate\":\"1\"}],\"marketDto\":{\"cadddate\":\"2015-10-20 17:47:33.0\",\"ceditdate\":\"2016-10-20 17:47:33.0\",\"clogo\":\"http://hsk.gs.9188.com/imgs/business/2015/9/20/万宁-f0be660e-1321-4022-8937-eb123f96.jpg\",\"clogolist\":\"http://hsk.gs.9188.com/imgs/business/2015/9/20/wn_logo@2x-476a2d9e-95a1-4d2a-ad09-2a47acdc.png\",\"cname\":\"万宁\",\"imarketid\":\"62\",\"iorder\":\"0\",\"istate\":\"1\",\"itype\":\"2\"}}\n";
        JSONObject obj = JSONObject.parseObject(str);
        Element resp = XmlUtils.jsonParseXml(obj,"Resp");
        Document dom=new DOMDocument();
        dom.add(resp);

        System.out.println(dom.asXML());
    }

    @Test
    public void testElement(){
        Document dom = DocumentHelper.createDocument();
        Element resp = new DOMElement("Resp");
        dom.setRootElement(resp);
        resp.addAttribute("code", "111");
        resp.addAttribute("desc", "测试");
        dom.setRootElement(resp);
        System.out.println("-------------------");
        System.out.println("---\n"+dom.asXML());
        System.out.println("-------------------");
        System.out.println(resp.asXML());


    }
}