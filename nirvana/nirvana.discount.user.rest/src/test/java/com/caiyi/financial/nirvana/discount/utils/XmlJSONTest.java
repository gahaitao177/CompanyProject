package com.caiyi.financial.nirvana.discount.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/11/8.
 */
public class XmlJSONTest {

    @Test
    public void testJson2XML() throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("name","tom");
        map.put("age",12);
        map.put("sex","男");
        String str = "WeChatBean";
//        String xmlString = XmlJSON.json2XML(JSONObject.toJSONString(map),str);
//        Document xml = XmlTool.read(xmlString, "utf-8");
//        Element root = XmlTool.getRootElement(xml);
//        System.out.println(root);
//        System.out.println(xmlString);
    }
}