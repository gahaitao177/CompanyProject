package com.caiyi.financial.nirvana.core.util;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by heshaohua on 2016/5/26.
 */
public class XmlTool {

    /**
     * 获取根节点
     * @param doc
     * @return
     */
    public static Element getRootElement(Document doc) {
        if (Objects.isNull(doc)) {
            return null;
        }
        return doc.getRootElement();
    }

    /**
     * 获取节点eleName下的文本值
     * @param eleName
     * @return
     */
    public static String getElementValue(Element eleName) {
        return getElementValue(eleName, null);
    }
    /**
     * 获取节点eleName下的文本值,若eleName不存在则返回默认值defaultValue
     * @param eleName
     * @param defaultValue
     * @return
     */
    public static String getElementValue(Element eleName, String defaultValue) {
        if (Objects.isNull(eleName)) {
            return defaultValue == null ? "" : defaultValue;
        } else {
            return eleName.getTextTrim();
        }
    }

     /**
     * 通过String获取文本值
     * @param eleName
     * @param parentElement
     * @return
     */
    public static String getElementValue(String eleName, Element parentElement) {
        if (Objects.isNull(parentElement)) {
            return null;
        } else {
            Element element = parentElement.element(eleName);
            if (!Objects.isNull(element)) {
                return element.getTextTrim();
            } else {
                try {
                    throw new Exception("找不到节点" + eleName);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     * 获取属性值
     * @param attrName
     * @return
     */
    public static String getAttributeValue(Attribute attrName) {
        return getAttributeValue(attrName, null);
    }

    /**
     * 获取属性值否则默认
     * @param attrName
     * @param defaultValue
     * @return
     */
    public static String getAttributeValue(Attribute attrName, String defaultValue){
        if (Objects.isNull(attrName)) {
            return defaultValue == null ? "" : defaultValue;
        } else {
            return attrName.getValue();
        }
    }

    /**
     * 获取属性名称
     * @param attrName
     * @return
     */
    public static String getAttributeName(Attribute attrName){
        return getAttributeName(attrName, null);
    }

    /**
     * 获取属性名称否则默认
     * @param attrName
     * @param defaultValue
     * @return
     */
    public static String getAttributeName(Attribute attrName, String defaultValue){
        if (Objects.isNull(attrName)) {
            return defaultValue == null ? "" : defaultValue;
        } else {
            return attrName.getName();
        }
    }


    /**
     * 根据名称获取元素属性
     * @param attrname
     * @param element
     * @return
     */
    public static Attribute getElementAttribute(String attrname, Element element){
        if (Objects.isNull(element)) {
            return null;
        }else {
            return element.attribute(attrname);
        }
    }

    /**
     * 获取元素上的属性List
     * @param eles
     * @return
     */
    public static List<Attribute> getAttributesByElement(Element eles){
        List<Attribute> attributeList = new ArrayList<Attribute>();
        if(Objects.isNull(eles)){
            return null;
        }else{
            if(eles.attributeCount() > -1){
                Iterator iter = eles.attributeIterator();
                while(iter.hasNext()){
                    attributeList.add((Attribute) iter.next());
                }
                return attributeList;
            }else{
                try {
                    throw new Exception("元素不包含属性" + eles);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     * 读取文件document
     * @param file
     * @return
     */
    public static Document read(File file) {
        return read(file, null);
    }

    /**
     * 通过文件生成document
     * @param file
     * @param charset
     * @return
     */
    public static Document read(File file, String charset) {
        if (Objects.isNull(file)) {
            return null;
        }

        SAXReader reader = new SAXReader();
        if (!Objects.isNull(charset)) {
            reader.setEncoding(charset);
        }

        Document document = null;
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 通过字符串生成document
     * @param xml
     * @param charset
     * @return
     */
    public static Document read(String xml, String charset){
        if (Objects.isNull(xml)) {
            return null;
        }

        SAXReader reader = new SAXReader();
        if (!Objects.isNull(charset)) {
            reader.setEncoding(charset);
        }
        Document document = null;
        try{
            InputSource in = new InputSource(new StringReader(xml));
            if (!Objects.isNull(charset)) {
                in.setEncoding(charset);
            }
            document = reader.read(in);
        }catch (DocumentException e){
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 通过Url生成document
     * @param url
     * @param charset
     * @return
     */
    public static Document read(URL url, String charset) {
        if (Objects.isNull(url)) {
            return null;
        }
        SAXReader reader = new SAXReader();
        if (!Objects.isNull(charset)) {
            reader.setEncoding(charset);
        }
        Document document = null;
        try {
            document = reader.read(url);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }


    /**
     *
     * @param body
     * @param path
     * @return
     */
    public static Document findCDATA(Document body, String path) {
        return XmlTool.stringToXml(XmlTool.getElementValue(path,
                body.getRootElement()));
    }

    /**
     * 文档树转换为字符串
     * @param doc
     * @param charset
     * @return
     */
    public static String xmltoString(Document doc, String charset) {
        if (Objects.isNull(doc)) {
            return "";
        }
        if (Objects.isNull(charset)) {
            return doc.asXML();
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(charset);
        StringWriter strWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(strWriter, format);
        try {
            xmlWriter.write(doc);
            xmlWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strWriter.toString();
    }

    /**
     * 持久化Document
     * @param doc
     * @param file
     * @param charset
     * @throws Exception
     */
    public static void xmltoFile(Document doc, File file, String charset) throws Exception {
        if (Objects.isNull(doc)) {
            throw new NullPointerException("doc cant not null");
        }
        if (Objects.isNull(charset)) {
            throw new NullPointerException("charset cant not null");
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(charset);
        FileOutputStream os = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(os, charset);
        XMLWriter xmlWriter = new XMLWriter(osw, format);
        try {
            xmlWriter.write(doc);
            xmlWriter.close();
            if (osw != null) {
                osw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 持久化Document
     * @param doc
     * @param filePath
     * @param charset
     * @throws Exception
     */
    public static void xmltoFile(Document doc, String filePath, String charset)
            throws Exception {
        xmltoFile(doc, new File(filePath), charset);
    }

    /**
     * String2Doument
     * @param text
     * @return
     */
    public static Document stringToXml(String text) {
        try {
            return DocumentHelper.parseText(text);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * createAttrXml
     * @param var0
     * @param var1
     * @return
     */
    public static String createAttrXml(String var0, String var1) {
        StringBuffer var2 = new StringBuffer("");
        var2.append(xmlEncode(var0));
        var2.append("=\"");
        var2.append(xmlEncode(var1));
        var2.append("\" ");
        return new String(var2);
    }

    /**
     * 代替toRawXmlString方法
     * @param data
     * @param ele
     * @return
     */
    public static String toRawXmlString(List<HashMap<String, Object>> data, String ele) {
        StringBuilder sb = new StringBuilder("");
        for(Map<String,Object> map : data){
            sb.append("<" + ele);
            for(String key : map.keySet()){
                sb.append(".")
                   .append(xmlEncode(key.toLowerCase()))
                   .append("=\"")
                   .append(xmlEncode(map.get(key).toString()))
                   .append("\"");
//                sb.append(" " + xmlEncode(key.toLowerCase()) + "=\"" + xmlEncode(map.get(key).toString()) + "\"");
            }
            sb.append(" />");
        }
        return sb.toString();
    }

    /**
     * XML encode
     * 替换掉特殊字符
     * @param var0
     * @return
     */
    private static String xmlEncode(String var0) {
        if(var0 == null) {
            return "";
        } else {
            String var1 = var0.replaceAll("&", "&amp;");
            var1 = var1.replaceAll("<", "&lt;");
            var1 = var1.replaceAll(">", "&gt;");
            var1 = var1.replaceAll("\"", "&quot;");
            return var1;
        }
    }

    public static Document createDocument() {
        return DocumentHelper.createDocument();
    }

    public static void main(String[] args) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<students name=\"zhangsan\" age=\"20\" xingbie=\"nv\">\n" +
                "    <hello name=\"lisi\">hello Text1</hello>\n" +
                "    <hello name=\"lisi2\">hello Text2</hello>\n" +
                "    <hello name=\"lisi3\">hello Text3</hello>\n" +
                "    <world name=\"wangwu\">world text1</world>\n" +
                "    <world name=\"wangwu2\">world text2</world>\n" +
                "    <world >world text3</world>\n" +
                "</students>";
        Document doc = stringToXml(xml);
        Element ele = getRootElement(doc);
        System.out.println(getElementValue("students", ele));

        //QName

        Iterator inte = ele.attributeIterator();
        while(inte.hasNext()){
            Attribute attr = (Attribute)inte.next();
            System.out.println(attr.getValue());
            System.out.println(attr.getName());
        }

        Attribute att = ele.attribute(0);
        org.dom4j.QName nn = att.getQName();

        //ele.attribute();
        Attribute att2 = ele.attribute("age");

        System.out.println("1---"+  att);
        System.out.println("2---"+nn);
        System.out.println("3---"+att2);
        System.out.println("4---"+ele.attributeValue(nn));
        System.out.println("5---"+ele.attributeValue("name"));
        //ele.attributeValue()
        //ele.attribute();
        //ele
    }

}
