package com.caiyi.financial.nirvana.ccard.material.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.util.DataUtil;
import com.caiyi.financial.nirvana.ccard.material.util.bean.FillMaterialBean;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.user.bean.HomePageBean;
import com.caiyi.financial.nirvana.discount.utils.WebUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/13.
 */
@RestController
@RequestMapping("/credit")
public class RestMaterialController {
    private static Logger logger = LoggerFactory.getLogger(RestMaterialController.class);

    @Resource(name = Constant.HSK_CCARD_MATERIAL)
    IDrpcClient client;

    @Resource(name = Constant.HSK_USER)
    IDrpcClient user_client;

    @RequestMapping("/apply_credit_cards.go")
    public String filterCard(MaterialBean bean, HttpServletRequest request, HttpServletResponse response) {
        logger.info("start apply_credit_cards>>>>>>");
        MaterialModel materialModel = new MaterialModel();
        materialModel.initMaterialModel(bean.getData());
        bean.setModel(materialModel);
        if (bean.getIclient() == null || bean.getIclient() == -1) {
            if (bean.getSource() >= 6000) {
                bean.setIclient(1);
            } else {
                bean.setIclient(0);
            }
        }
        //获取优惠信息
        if (bean.getPn() == null){
            bean.setPn(1);
        }
        if (bean.getPs() == null){
            bean.setPs(10);
        }
        int pn = bean.getPn();
        int ltype = bean.getLtype();
        String ibankid = materialModel.getIbankid();
        if ("-1".equals(ibankid)) {
            ibankid = null;
        }
        JSONObject benefit = null;
        if (pn == 1 && ltype == 0) {
            if (!StringUtils.isEmpty(ibankid)) {
                HomePageBean user = new HomePageBean();
                user.setHskcityid(bean.getHskcityid());
                user.setIpAddr(WebUtil.getRealIp(request).trim());
                user.setPn(bean.getPn());
                user.setPs(bean.getPs());
                user.setIbankids(ibankid);
                String urt = user_client.execute(new DrpcRequest("HomePageBolt", "specialPreferential", user));
                JSONObject jsonObject = JSONObject.parseObject(urt);
                if (jsonObject != null && jsonObject.size() > 0) {
                    int code = jsonObject.getIntValue("code");
                    if (code == 1) {//查询成功
                        JSONArray rows = jsonObject.getJSONArray("data");
                        String bankidStr=","+ibankid+",";
                        if (rows != null && rows.size() > 0) {//取第一个优惠
                            benefit = rows.getJSONObject(0);
                            if(rows.size()>1){
                                for(int i=0;i<rows.size();i++){
                                    JSONObject obj=rows.getJSONObject(i);
                                    if(bankidStr.equals(obj.getString("ibankid"))){
                                        benefit= obj;
                                        benefit.remove("cadddate");
                                        benefit.remove("cadduser");
                                        benefit.remove("citycodes");
                                        benefit.remove("is_hidden");
                                        benefit.remove("is_del");
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        logger.info("HomePageBolt.specialPreferential 调用失败");
                    }
                }
            }
        }
        String rt = client.execute(new DrpcRequest("material", "filterCard", bean));
        JSONObject jsonObject = JSONObject.parseObject(rt);
        JSONObject result = new JSONObject();
        if (jsonObject != null && jsonObject.size() > 0) {
            JSONObject dataJson = new JSONObject();
            dataJson.put("cards", jsonObject.get("cards"));
            dataJson.put("pn", jsonObject.getString("pageNum"));
            dataJson.put("tp", jsonObject.getString("totalPage"));
            dataJson.put("ps", jsonObject.getString("pageSize"));
            dataJson.put("cardstotal", jsonObject.getString("cardstotal"));
            dataJson.put("score", jsonObject.getString("score"));
            dataJson.put("score_tag", jsonObject.getString("score_tag"));
            if (benefit != null) {
                dataJson.put("specialPreferential", benefit);
            }
            result.put("data", dataJson);
            result.put("desc", "请求数据成功");
            result.put("code", "1");
        } else if (jsonObject != null) {
            result.put("data", null);
            result.put("desc", "没有请求到数据");
            result.put("code", "1");
        } else {
            result.put("desc", "请求出错");
            result.put("code", "0");
        }

        return result.toJSONString();
    }

    /**
     * 信用卡附加字段
     */
    @RequestMapping("/apply_credit_field_p.go")
    public void field_p(HttpServletRequest request, HttpServletResponse response) {
        Element resp = new DOMElement("Resp");
        Document dom = DocumentHelper.createDocument();
        String result = client.execute(new DrpcRequest("material", "field_p"));
        dom.setRootElement(resp);
        JSONArray jsonArray = JSONArray.parseArray(result);
        if (jsonArray != null && jsonArray.size() > 0) {
            Element banks = new DOMElement("banks");
            for (Object item : jsonArray) {
                JSONObject bankitem = JSONObject.parseObject(item.toString());
                Element bankitemE = new DOMElement("bankitem");
                for (String key : bankitem.keySet()) {
                    if (!"cxml".equals(key)) {
                        bankitemE.addAttribute(key, bankitem.get(key).toString());
                    } else if ("cxml".equals(key) && bankitem.get(key) != null) {
                        String contend = "<code>" + bankitem.get(key).toString() + "</code>";
                        Document doc = XmlTool.stringToXml(contend);
                        List<Element> list = doc.getRootElement().elements();
                        bankitemE.setContent(list);
                    }
                }
                banks.add(bankitemE);
            }
//            JsonUtil.jsonToElement(jsonArray,banks,"bankitem",null);
            resp.add(banks);
            resp.addAttribute("code", "1");
            resp.addAttribute("desc", "查询成功");
        } else if (jsonArray != null) {
            resp.addAttribute("code", "1");
            resp.addAttribute("desc", "没有获得有效数据");
        } else {
            resp.addAttribute("code", "0");
            resp.addAttribute("desc", "请求出错");
        }
        XmlUtils.writeXml(dom, response);
    }

    @RequestMapping("/apply_credit_area.go")
    public String queryCreditArea(HttpServletResponse response, HttpServletRequest request) {
//        resp.clearContent();
        JSONObject res = new JSONObject();
        String city = request.getParameter("icityid");
        String ibankid = request.getParameter("ibankid");
        String ltype = request.getParameter("ltype");
        MaterialBean bean = new MaterialBean();

        if (StringUtils.isEmpty(city)) {
            city = "100000";
        }
        bean.setIbankid(ibankid);
        if (StringUtils.isEmpty(ltype)) {
            ltype = "0";
        }
        try {
            bean.setItype(Integer.parseInt(ltype));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(bean.getLtype() + "") || !"11".equals(ibankid)) {
            bean.setLtype(0);
        }
        bean.setIcityid(city);
        String result = client.execute(new DrpcRequest("material", "queryCreditArea", bean));
        JSONArray jsonArray = JSONArray.parseArray(result);
        if (jsonArray != null && jsonArray.size() > 0) {
            res.put("data", jsonArray);
            res.put("desc", "查询数据成功");
            res.put("code", "1");
        } else if (jsonArray != null) {
            res.put("code", "1");
            res.put("desc", "没有获得有效数据");
        } else {
            res.put("code", "0");
            res.put("desc", "请求出错");
        }
        return res.toJSONString();
    }

    @RequestMapping("/apply_credit_action.go")
    public String saveMaterial(HttpServletRequest request, HttpServletResponse response) {
//        resp.clearContent();
        JSONObject jsonObject = new JSONObject();
        MaterialBean bean = new MaterialBean();
        int i = 0;
        try {
            bean.setIclient(Integer.parseInt(request.getParameter("iclient")));
            i = FillMaterialBean.getMaterialBean(bean, request);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("code", "-1");
            jsonObject.put("desc", "非法请求");
            return jsonObject.toJSONString();
        }
        if (i == 1) {
            MaterialModel materialModel = new MaterialModel();
            materialModel.initMaterialModel(bean.getData());
            String result = client.execute(new DrpcRequest("material", "saveMaterial", materialModel));
            JSONObject object = JSONObject.parseObject(result);
            if (object != null) {
                return object.toJSONString();
            } else {
                jsonObject.put("code", "-1");
                jsonObject.put("desc", "程序异常");
            }
        } else {
            jsonObject.put("code", "-1");
            jsonObject.put("desc", "解密失败");
        }
        return jsonObject.toJSONString();
    }

    /**
     * 信用卡申请列表
     *
     * @param request
     * @param response
     */
    @RequestMapping("/apply_credit_orders.go")
    public String queryCreditOrder(HttpServletRequest request, HttpServletResponse response) {
//        resp.clearContent();
        JSONObject object = new JSONObject();
        MaterialBean bean = new MaterialBean();
        String data = request.getParameter("data");

        JSONObject json = JSON.parseObject(data);
        Object cphone = json.get("userPhoneNum");
        if (cphone != null && StringUtils.isEmpty(cphone.toString())) {
            bean.setCphone(cphone.toString());
            DataUtil.dencrypt(bean, request, response);
            String result = client.execute(new DrpcRequest("material", "queryCreditOrder", bean.getCphone()));

            object.put("code", "1");
            object.put("data", result);
            object.put("desc", "查询申请卡列表成功");
        } else {
            object.put("code", "0");
            object.put("desc", "手机号不能为空");
        }
        return object.toJSONString();
    }

    /**
     *找回申卡资料发送验证码
     * @param response
     * @param request
     */
//    @RequestMapping("/find_material_yzm.go")
//    public  void sendMessage(HttpServletResponse response,HttpServletRequest request){
//        String ipAddr=DataUtil.getRealIp(request);
//        String mobileNo=request.getParameter("cphone");
//        if(!StringUtils.isEmpty(mobileNo)){
//            Map<String,String> map=new HashMap<>();
//            map.put("ipAddr",ipAddr);
//            map.put("mobileNo",mobileNo);
//            result=client.execute(new DrpcRequest("material","sendMessage",map));
//            JSONObject object=JSONObject.parseObject(result);
//            resp.addAttribute("code", object.get("code").toString());
//            resp.addAttribute("desc", object.get("desc").toString());
//        }else{
//            resp.addAttribute("code", "0");
//            resp.addAttribute("desc", "手机号不能为空");
//        }
//        XmlUtils.writeXml(dom,response);
//    }

    /**
     * 找回申卡资料
     * @param request
     * @param response
     */
//    @RequestMapping("/find_material.go")
//    public  void findMaterial(HttpServletRequest request,HttpServletResponse response){
//        String cphone=request.getParameter("cphone");
//        String onlyCheckSms=request.getParameter("onlyCheckSms");
//        String phoneauthcode=request.getParameter("phoneauthcode");
//        if(StringUtils.isEmpty(cphone)||StringUtils.isEmpty(phoneauthcode)){
//            resp.addAttribute("code", "0");
//            resp.addAttribute("desc", "手机号和验证码都不能空");
//            XmlUtils.writeXml(dom,response);
//            return;
//        }
//        Map<String,String> map=new HashMap<>();
//        map.put("yzm",phoneauthcode);
//        map.put("mobileNo",cphone);
//        map.put("onlyCheckSms",onlyCheckSms);
//        result=client.execute(new DrpcRequest("material","findMaterial",map));
//        if(result==null){
//            resp.addAttribute("code", "0");
//            resp.addAttribute("desc", "没有查到该手机号资料,手机号 "+cphone);
//        }else{
//            JSONArray objects = JSONArray.parseArray(result);
//            if(objects!=null){
//                resp.addAttribute("code", "1");
//                resp.addAttribute("desc", "找回资料,手机号 "+cphone);
//                resp.addAttribute("data",result);
//            }else {
//                resp.addAttribute("code", "0");
//                resp.addAttribute("desc", "没有查到该手机号有效资料,手机号 "+cphone);
//            }
//        }
//        XmlUtils.writeXml(dom,response);
//        return;
//    }

    /**
     * 删除信用卡申请列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/delete_apply_credit_log.go")
    public String deleteApplyCreditLog(HttpServletRequest request, HttpServletResponse response) {
        String cphone = request.getParameter("cphone");
        String iapplyid = request.getParameter("iapplyid");
        JSONObject json = new JSONObject();
        MaterialBean bean = new MaterialBean();
        bean.setCphone(cphone);
        Integer z = DataUtil.dencrypt(bean, request, response);
        if (z == 1) {
            Map<String, String> map = new HashMap<>();
            map.put("cphone", bean.getCphone());
            map.put("iapplyid", iapplyid);
            String result = client.execute(new DrpcRequest("material", "deleteApplyCreditLog", map));
            return result;
        } else {
            json.put("desc", "解密出错");
            json.put("code", "0");
        }
        return json.toJSONString();
    }

    @RequestMapping("/queryO2OBank.go")
    public String queryO2OBank(HttpServletRequest request, HttpServletResponse response) {
        String icityid = request.getParameter("icityid");
        JSONObject json = new JSONObject();
        if (StringUtils.isEmpty(icityid)) {
            json.put("code", "0");
            json.put("desc", "参数错误");
            json.put("data", "fail");
            return json.toJSONString();
        }
        String result = client.execute(new DrpcRequest("materialCard", "queryO2OBank", icityid));
        JSONArray resultJson = JSON.parseArray(result);
        if (resultJson != null) {
            json.put("code", "1");
            json.put("desc", "获得成功");
            json.put("data", resultJson.toJSONString());
        } else {
            json.put("code", "0");
            json.put("desc", "没有查询到数据");
        }
        return json.toJSONString();
    }

    @RequestMapping("/queryBiz.go")
    public String queryBusiness(HttpServletRequest request, HttpServletResponse response) {
        String idistrictid = request.getParameter("idistrictid");
        JSONObject json = new JSONObject();
        if (StringUtils.isEmpty(idistrictid)) {
            json.put("code", "0");
            json.put("desc", "参数错误");
            json.put("data", "fail");
            return json.toJSONString();
        }
        String result = client.execute(new DrpcRequest("materialCard", "queryBusiness", idistrictid));
        JSONArray resultJson = JSON.parseArray(result);
        if (resultJson != null) {
            json.put("code", "1");
            json.put("desc", "获得成功");
            json.put("data", resultJson.toJSONString());
        } else {
            json.put("code", "0");
            json.put("desc", "没有查询到数据");
        }
        return json.toJSONString();
    }

    @RequestMapping("/saveO2O.go")
    public String saveO2OMaterial(HttpServletRequest request, HttpServletResponse response) {
        String cbankids = request.getParameter("cbankids");
        String ipostaddr = request.getParameter("ipostaddr");
        String ibizid = request.getParameter("ibizid");
        String cphone = request.getParameter("cphone");
        JSONObject json = new JSONObject();
        if (StringUtils.isEmpty(cbankids) || StringUtils.isEmpty(ipostaddr) || StringUtils.isEmpty(ibizid)) {
            json.put("code", "0");
            json.put("desc", "参数错误");
            json.put("data", "fail");
            return json.toJSONString();
        }
        MaterialBean bean = new MaterialBean();
        Integer i = FillMaterialBean.getMaterialBean(bean, request);
        if (i != 1) {
            json.put("code", "0");
            json.put("desc", "参数错误");
            return json.toJSONString();
        }
        String result = client.execute(new DrpcRequest("materialCard", "saveO2OMaterial", bean));
        JSONObject resultJson = JSON.parseObject(result);
        if (resultJson != null) {
            return resultJson.toJSONString();
        } else {
            json.put("code", "0");
            json.put("desc", "没有查询到数据");
        }
        return json.toJSONString();
    }

}
