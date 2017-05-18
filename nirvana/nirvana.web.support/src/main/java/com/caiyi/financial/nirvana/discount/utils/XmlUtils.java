package com.caiyi.financial.nirvana.discount.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/5/6.
 */
public class XmlUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

    public static void writeXml(JSONObject jsonObject, HttpServletResponse response){
        writeXml(toOldXml(jsonObject),response);
    }

    public static void writeXml(Document document, HttpServletResponse response){
        String code = document.getRootElement().attributeValue("code");
        if(code!=null && !BoltResult.SUCCESS.equals(code)){
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        writeXml(document.asXML(),response);
    }

    /**
     * 该方法应当废弃， XmlUtils中不应该存在返回json的方法
     * @param jsonStr
     * @param response
     */
    @Deprecated
    public static void writeJson(String jsonStr, HttpServletResponse response){
        response.setContentType("application/json; charset=utf-8");
        try {
            response.getWriter().write(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法废弃，不允许直接返回xml字符串
     * @param xmlStr
     * @param response
     */
    @Deprecated
    public static void writeXml(String xmlStr, HttpServletResponse response){
        response.setContentType("text/xml;charset=UTF-8");
        try {
            response.getWriter().write(xmlStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeXml(String code, String desc, HttpServletResponse response){
        Element resp = new DOMElement("Resp");
        resp.addAttribute("code", code);
        resp.addAttribute("desc", desc);
        writeXml(resp, response);
    }

    public static void writeXml(Element element,HttpServletResponse response){
        Document dom = DocumentHelper.createDocument();
        dom.setRootElement(element);
        writeXml(dom,response);
    }

    public static void writeXml(int code, String desc, HttpServletResponse response){
        writeXml(String.valueOf(code),desc,response);
    }


    public static Document toOldXml(JSONObject jsonObject){
        String code = jsonObject.getString("code");
        String desc = jsonObject.getString("desc");
        Document dom = DocumentHelper.createDocument();

        if(code == null || BoltResult.SUCCESS.equals(code)){
            //成功
            Element resp = new DOMElement("Resp");
            dom.setRootElement(resp);
            resp.addAttribute("code","1");
            resp.addAttribute("desc",desc != null ? desc : "查询成功");
            //解析为count
            Element count = new DOMElement("count");
            count.addAttribute("tp",jsonObject.getString("totalPage"));
            count.addAttribute("rc",jsonObject.getString("records"));
            count.addAttribute("pn",jsonObject.getString("pageNum"));
            count.addAttribute("ps",jsonObject.getString("pageSize"));
            resp.add(count);

            //解析查询为row
            JSONArray array =  jsonObject.getJSONArray("rows");
            if (null != array) {
                for(int i = 0,size = array.size();i<size;i++){
                    resp.add(XmlUtils.jsonParseXml(array.getJSONObject(i),"row"));
                }
            }
        }else{
            //失败
            Element resp = new DOMElement("Resp");
            dom.setRootElement(resp);
            resp.addAttribute("code","0");
            resp.addAttribute("desc",desc != null ? desc : "执行失败");
        }
        return dom;
    }

    private static ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<DateFormat> sqlDateFormatThreadLocal = new ThreadLocal<>();

    /**
     * 将一个jsonObject转换为一个xml节点
     * @param json
     * @param elementName
     * @return
     */
    public static Element jsonParseXml(JSONObject json,String elementName){
        Element row = new DOMElement(elementName);

        for(Map.Entry<String, Object> rowEntry : json.entrySet()){
            String key = rowEntry.getKey();
            Object value = rowEntry.getValue();
            if(value!=null){
                if (value instanceof JSONArray){
                    List<Element> list = jsonParseXml((JSONArray) value, key);
                    for(Element element: list){
                        row.add(element);
                    }
                }else if(rowEntry.getValue() instanceof JSONObject){
                    Element element = jsonParseXml((JSONObject) value,key);
                    row.add(element);
                }else{
                    if(value instanceof Number || value instanceof String){
                        row.addAttribute(key, value.toString());
                    }else if (value instanceof java.sql.Date){
                        DateFormat df =  sqlDateFormatThreadLocal.get();
                        if(df==null){
                            df = new SimpleDateFormat("yyyy-MM-dd");
                            sqlDateFormatThreadLocal.set(df);
                        }
                        row.addAttribute(key, df.format(value));
                    }else if(value instanceof Date){
                        DateFormat df =  dateFormatThreadLocal.get();
                        if(df==null){
                            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dateFormatThreadLocal.set(df);
                        }
                        row.addAttribute(key, df.format(value));
                    }else{
                        throw new RuntimeException("暂不支持转换");
                    }
//                    row.addAttribute(key, value.toString());
                }
            }
        }
        return row;
    }

//    public static void main(String[] args) {
//    String key = "abc";
//        System.out.println(key.substring(0,1).toUpperCase()+key.substring(1));
//    }

    public static List<Element> jsonParseXml(JSONArray json,String elementName){
        List<Element> list = new ArrayList<Element>();
        for(int i=0,size = json.size();i<size;i++){
            JSONObject jsonObject = json.getJSONObject(i);
            Element e = jsonParseXml(jsonObject,elementName);

            list.add(e);
        }
        return list;
    }
}
