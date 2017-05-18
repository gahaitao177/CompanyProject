package com.caiyi.financial.nirvana.tools.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.tools.bean.CheapBean;
import com.caiyi.financial.nirvana.tools.util.VersionBean;
import com.caiyi.financial.nirvana.tools.util.VersionConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 *
 * Created by dengh on 2016/8/9.
 */
@RestController
@RequestMapping("/credit")
public class Cheapcontroller {
    private  static Logger log= LoggerFactory.getLogger(Cheapcontroller.class);
    @Resource(name = Constant.HSK_TOOL)
    IDrpcClient client;
    @RequestMapping("/androidUpgrade.go")
    public void startAndroid(CheapBean bean , HttpServletRequest request, HttpServletResponse response){
        StringBuilder sb = new StringBuilder();
        try {
            //读取升级信息
            appendAndroidUpgradeInfo(request,bean, sb);
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("ok");
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("fail");
            e.printStackTrace();
        }
        bean.setBusiXml(sb.toString());
//        JSONObject object = JSONObject.fromObject(bean);
//        String str=object.toString();
        sendData(JSONObject.toJSONString(bean), request,response);

    }
    @RequestMapping("/iosUpgrade.go")
    public void startIos(CheapBean bean, HttpServletRequest request,HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        try {
            //读取升级信息
            appendIosUpgradeInfo(request,bean, sb);
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("ok");
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("fail");
            e.printStackTrace();
        }
        bean.setBusiXml(sb.toString());
//        JSONObject object = JSONObject.fromObject(bean);
//        String str=object.toString();
        sendData(JSONObject.toJSONString(bean), request,response);

    }




    protected int sendData(String str, HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(str);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return 1;
    }
    private void appendAndroidUpgradeInfo(HttpServletRequest request, CheapBean bean, StringBuilder sb) {
        VersionBean vb = VersionConfigUtil.getVersionBeanForAndroid(bean.getSource()+"");

        int aversion;
        if(!StringUtils.isEmpty(bean.getAppVersion())){
            aversion = Integer.valueOf(bean.getAppVersion());
        }else{
            aversion = Integer.valueOf(bean.getAppversion());
        }
        if (vb.getAnum() > aversion) {
            sb.append("<app ");
            sb.append(XmlTool.createAttrXml("type", vb.getType()));
            sb.append(XmlTool.createAttrXml("content", vb.getContent()));
            sb.append(XmlTool.createAttrXml("url", vb.getUrl()));
            sb.append(XmlTool.createAttrXml("appversion", vb.getAnum() + ""));
            sb.append("/>");
        }
    }

    private void appendIosUpgradeInfo(HttpServletRequest request, CheapBean bean, StringBuilder sb) {


        VersionBean verbean = VersionConfigUtil.getVersionBeanForIOS(bean.getSource()+"");

        String[] arr1 = verbean.getAversion().split("\\."); // 服务器版本

        String[] arr2;
        if(!StringUtils.isEmpty(bean.getAppVersion())){
            arr2 = bean.getAppVersion().split("\\."); // 用户所传版本
        }else{
            arr2 = bean.getAppversion().split("\\."); // 用户所传版本
        }
        boolean u = false;
        for (int i = 0; i < arr1.length; i++) {
            if (i < arr2.length) {
                int result = Integer.valueOf(arr1[i]) - Integer.valueOf(arr2[i]);
                if (result > 0) {
                    u = true;
                    break;
                } else if (result == 0) {
                    continue;
                } else if (result < 0) {
                    u = false;
                    break;
                }
            }
        }

        if (u) {
            String url = verbean.getUrl();
            url = url.replace("^", "&");
            sb.append("<app ");
            sb.append(XmlTool.createAttrXml("isup", "1"));
            sb.append(XmlTool.createAttrXml("type", verbean.getType()));
            sb.append(XmlTool.createAttrXml("content", verbean.getContent()));
            sb.append(XmlTool.createAttrXml("url", url));
            sb.append(XmlTool.createAttrXml("aversion", verbean.getAversion()));
            sb.append("/>");
        } else {
            sb.append("<app ");
            sb.append(XmlTool.createAttrXml("isup", "0"));
            sb.append(XmlTool.createAttrXml("type", "0"));
            sb.append(XmlTool.createAttrXml("content", "惠刷卡升级啦;1、惠刷卡正式内测;2、发现bug给解决单身问题,如已婚给介绍外遇对象 ＾V＾"));
            sb.append(XmlTool.createAttrXml("url", "http://www.licaidi.com"));
            sb.append(XmlTool.createAttrXml("aversion", "1.0.0"));
            sb.append("/>");
        }
    }

}
