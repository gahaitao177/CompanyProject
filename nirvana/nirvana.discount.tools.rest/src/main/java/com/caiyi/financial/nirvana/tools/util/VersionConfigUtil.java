package com.caiyi.financial.nirvana.tools.util;

import com.caiyi.financial.nirvana.core.util.XmlTool;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.util.List;

/**
 * Created by dengh on 2016/8/9.
 */
public class VersionConfigUtil {
    public static VersionBean getVersionBeanForAndroid(String source){
        File file = new File(FilePathConstant.ANDROID_UPGRADE);
        Document doc  =  XmlTool.read(file,"utf-8");
        Element  root = XmlTool.getRootElement(doc);
        List<Element> elements = root.elements("app");
        int count = elements.size();
        VersionBean vb = new VersionBean();
        for(int i = 0; i < count; i++){
            Element element = elements.get(i);
            String channel   =  element.attributeValue("channel");
            if(channel.equals(source)){
                String anum   =  element.attributeValue("anum");
                String type   =  element.attributeValue("type");
                String url   =  element.attributeValue("url");
                String content   =  element.attributeValue("content");
                String pnum = element.attributeValue("pnum");
                String path = element.attributeValue("path");
                String link  = element.attributeValue("link");
                vb.setAnum(Integer.valueOf(anum));
                vb.setContent(content);
                vb.setType(type);
                vb.setUrl(url);
                vb.setPath(path);
                vb.setPnum(Integer.valueOf(pnum));
                vb.setLink(link);
                break;
            }
         }

        return vb;
    }

    public static VersionBean getVersionBeanForIOS(String source){
        File file = new File(FilePathConstant.IOS_UPGRADE);
        Document doc  =  XmlTool.read(file,"utf-8");
        Element  root = XmlTool.getRootElement(doc);
        List<Element> elements = root.elements("app");
        int count = elements.size();
        VersionBean vb = new VersionBean();
        for(int i = 0; i < count; i ++ ){
            Element element = elements.get(i);
            String channel   =  element.attributeValue("channel");
            if(channel.equals(source)){
                String anum   =  element.attributeValue("anum");
                String type   =  element.attributeValue("type");
                String url   =  element.attributeValue("url");
                String content   =  element.attributeValue("content");
                String pnum = element.attributeValue("pnum");
                String path = element.attributeValue("path");
                String link  = element.attributeValue("link");
                vb.setAnum(Integer.valueOf(anum.replace(".", "")));
                vb.setContent(content);
                vb.setType(type);
                vb.setUrl(url);
                vb.setPath(path);
                vb.setPnum(Integer.valueOf(pnum));
                vb.setAversion(anum);
                break;
            }
        }
        return vb;
    }
}
