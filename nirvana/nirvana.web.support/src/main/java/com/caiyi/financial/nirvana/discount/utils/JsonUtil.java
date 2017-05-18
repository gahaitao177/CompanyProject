package com.caiyi.financial.nirvana.discount.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

import java.util.Map;

/**
 * Created by lizhijie on 2016/6/20.
 */
public class JsonUtil {
    public static  Element jsonToElement(JSONArray jsonArray,Element root, String childStr,Map<String,String> map){
        for (int j=0;j<jsonArray.size();j++) {
            JSONObject json=jsonArray.getJSONObject(j);
            Element child = new DOMElement(childStr);
            for (String key : json.keySet()) {
                if(map==null){
                    child.addAttribute(key, json.getString(key));
                }else {
                    child.addAttribute(map.get(key) == null ? key : map.get(key), json.getString(key));
                }
            }
            root.add(child);
        }
        return root;
    }
    public static  Element jsonToElement(JSONArray jsonArray,String rootStr ,String childStr, Map<String,String> map){
        Element root=new DOMElement(rootStr);
        if (jsonArray!=null&&jsonArray.size()>0){
            for (int i=0;i<jsonArray.size();i++){
                JSONObject json = jsonArray.getJSONObject(i);
                if(childStr!=null) {
                    Element child = new DOMElement(childStr);
                    if (map == null) {
                        for (String key : json.keySet()) {
                            child.addAttribute(key, json.getString(key) == null ? "" : json.getString(key));
                        }
                    } else {
                        for (String key : map.keySet()) {
                            child.addAttribute(map.get(key), json.getString(key) == null ? "" : json.getString(key));
                        }
                    }
                    root.add(child);
                }
            }
        }
        return root;
    }
    public static  Element jsonToElement(JSONArray jsonArray, Element element, Map<String,String> map,boolean first){
        if (jsonArray!=null&&jsonArray.size()>0){
            for (int i=0;i<jsonArray.size()&&first;i++){
                JSONObject json= jsonArray.getJSONObject(i);
                for (String key : json.keySet()) {
                    element.addAttribute(map.get(key), json.getString(key) == null ? "":json.getString(key));
                }
                return element;
            }
        }
        return element;
    }
    public static Element mapToElement(Element element, Map<String,String> map){
        for (String key:map.keySet()){
            element.addAttribute(key,map.get(key));
        }
        return element;
    }
    public static void jsonToElement(Element element,JSONObject json){
        for (String key:json.keySet()){
            element.addAttribute(key,json.get(key)==null?"":json.get(key).toString());
        }
        return ;
    }

}
