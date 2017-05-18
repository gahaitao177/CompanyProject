package com.caiyi.financial.nirvana.bill.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mario on 2016/7/29 0029.
 */
@RestController
public class CardController {
    private static Logger logger = LoggerFactory.getLogger(CardController.class);
    @Resource(name = Constant.HSK_BILL_BANK)
    private IDrpcClient client;
    @Autowired
    MemCachedClient cc;

    /**
     * 开卡
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/credit/handleCredit.go")
    public void handleCredit(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            String res = client.execute(new DrpcRequest("billCard", "handleCredit", bean));
            bean = JSON.parseObject(res, Channel.class);
        } catch (Exception e) {
            logger.error("handleCredit 异常" + e.getMessage());
            e.printStackTrace();
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
            sb.append(bean.getBusiXml());
            sb.append("</Resp>");
            XmlUtils.writeXml(sb.toString(), response);
        }
    }

    /**
     * 银行网点
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/credit/bankPoint.go")
    public void bankPoint(Channel bean, HttpServletRequest request, HttpServletResponse response) {
        try {
            String res = client.execute(new DrpcRequest("billCard", "bankPoint", bean));
            bean = JSON.parseObject(res, Channel.class);
        } catch (Exception e) {
            logger.error("bankPoint 异常" + e.getMessage());
            e.printStackTrace();
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("请求失败");
        } finally {
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<Resp code=\"" + bean.getBusiErrCode() + "\" desc=\"" + bean.getBusiErrDesc() + "\">");
            sb.append(bean.getBusiXml());
            sb.append("</Resp>");
            XmlUtils.writeXml(sb.toString(), response);
        }
    }

    /**
     * 信用卡激活方式查询
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/credit/activation.go")
    public void activation(Channel bean, HttpServletRequest request, HttpServletResponse response){
        Document doc = null;
        try {
            JSONObject res = client.execute(new DrpcRequest("billCard", "activation",bean), JSONObject.class);
            doc = getDoc(res);
        } catch (Exception e) {
            logger.error("activation 异常" + e.getMessage());
        } finally {
            XmlUtils.writeXml(doc, response);
        }
    }

    /**
     * 办卡进度查询
     *
     * @param bean
     * @param request
     * @param response
     */
    @RequestMapping("/credit/getCardProgress.go")
    public void getCardProgress(Channel bean, HttpServletRequest request, HttpServletResponse response){
        Document doc = null;
        try {
            JSONObject res = client.execute(new DrpcRequest("billCard", "getCardProgress", bean), JSONObject.class);
            doc = getDoc(res);
        } catch (Exception e) {
            logger.error("getCardProgress 异常" + e.getMessage());
        } finally {
            XmlUtils.writeXml(doc, response);
        }
    }

    /**
     * 银行服务大厅
     */
    @SetUserDataRequired
    @RequestMapping("/notcontrol/credit/bankServiceIndex.go")
    public BoltResult bankServiceIndex(Channel channel) {
        long start = System.currentTimeMillis();
        BoltResult result = client.execute(new DrpcRequest("billCard", "bankServiceIndex", channel), BoltResult.class);
        logger.info("|银行服务大厅|bankServiceIndex|cuserId:" + channel.getCuserId() + "|result:" + JSON.toJSON(result));
        logger.info("银行服务大厅耗时：" + (System.currentTimeMillis() - start));
        return result;
    }

    /**
     * 重写XmlUtils里的Jsonobject转Document方法
     * @param jsonObject
     * @return
     */
    public Document getDoc (JSONObject jsonObject){
        String code = jsonObject.getString("code");
        String desc = jsonObject.getString("desc");
        Document dom = DocumentHelper.createDocument();

        if (code == null || BoltResult.SUCCESS.equals(code)) {
            //成功
            Element resp = new DOMElement("Resp");
            dom.setRootElement(resp);
            resp.addAttribute("code", "1");
            resp.addAttribute("desc", desc != null ? desc : "查询成功");
            //解析查询为row
            JSONArray array = jsonObject.getJSONArray("rows");
            if (null != array) {
                for (int i = 0, size = array.size(); i < size; i++) {
                    resp.add(XmlUtils.jsonParseXml(array.getJSONObject(i), "row"));
                }
            }
        } else {
            //失败
            Element resp = new DOMElement("Resp");
            dom.setRootElement(resp);
            resp.addAttribute("code", "0");
            resp.addAttribute("desc", desc != null ? desc : "执行失败");
        }
        return dom;
    }


}
