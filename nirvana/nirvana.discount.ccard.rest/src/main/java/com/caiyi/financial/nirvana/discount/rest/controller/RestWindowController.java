package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.ccard.bean.Card;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Window;
import com.caiyi.financial.nirvana.discount.utils.JsonUtil;
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

/**
 * Created by lizhijie on 2016/8/16.
 */
@RestController
@RequestMapping("/credit")
public class RestWindowController {

    private static Logger logger = LoggerFactory.getLogger(RestWindowController.class);
    @Resource(name = Constant.HSK_CCARD)
    IDrpcClient client;

    @Resource(name = Constant.HSK_CCARD_MATERIAL)
    IDrpcClient client_material;

    @RequestMapping("/appStart.go")
    public  void startPage(HttpServletRequest request, HttpServletResponse response){
        Document dom= DocumentHelper.createDocument();
        Element resp=new DOMElement("Resp");
        dom.setRootElement(resp);

        String packagename=request.getParameter("packagename");
        String appVersion=request.getParameter("appVersion");
        String source=request.getParameter("source");
        String iclient = request.getParameter("iclient");

        Window window=new Window();
        if(StringUtils.isNumeric(iclient)){
            window.setIclient(Integer.parseInt(iclient));
        }

        window.setPackagename(packagename);

        if(StringUtils.isNumeric(source)) {
            window.setSource(Integer.parseInt(source));
        }else{
            window.setSource(5000);
        }
        window.setAppVersion(appVersion);
        String result=client.execute(new DrpcRequest("window","startPage",window));

        JSONObject root = JSONObject.parseObject(result);
        if(root!=null&&root.size()>0){
            if(root.getJSONObject("loadimg")!=null){
                Element loadimgE=new DOMElement("loadimg");
                JSONObject loading =root.getJSONObject("loadimg");
                loadimgE.addAttribute("version",loading.getString("cversion"));
                loadimgE.addAttribute("url",loading.getString("cpicurl"));
                resp.add(loadimgE);
            }
            if(root.getJSONObject("findbanner")!=null){
                Element banner=new DOMElement("banner");
                String version=root.getJSONObject("findbanner").getString("version");
                banner.addAttribute("version",version);
                resp.add(banner);
                JSONArray bannerrowList=root.getJSONObject("findbanner").getJSONArray("bannerrowList");
                for(int i=0;i<bannerrowList.size();i++){
                    JSONObject bannerrow=bannerrowList.getJSONObject(i);
                    Element bannerrowE=new DOMElement("bannerrow");
                    bannerrowE.addAttribute("type",bannerrow.getString("itype"));
                    bannerrowE.addAttribute("picurl",bannerrow.getString("cpicurl"));
                    bannerrowE.addAttribute("data",bannerrow.getString("cdata"));
                    bannerrowE.addAttribute("title",bannerrow.getString("ctitle"));
                    resp.add(bannerrowE);
                }
            }
            if(root.getJSONObject("loan")!=null){
                Element loanE=new DOMElement("Loan");
                JSONObject loading =root.getJSONObject("loan");
                JsonUtil.jsonToElement(loanE,loading);
                resp.add(loanE);
            }
            if(root.getJSONObject("newBanner")!=null){
                Element banner=new DOMElement("newbanner");
                String version=root.getJSONObject("newBanner").getString("newbanner");
                banner.addAttribute("version",version);
                resp.add(banner);
                JSONArray bannerrowList=root.getJSONObject("newBanner").getJSONArray("newbannerrow");
                for(int i=0;i<bannerrowList.size();i++){
                    JSONObject bannerrow=bannerrowList.getJSONObject(i);
                    Element bannerrowE=new DOMElement("newbannerrow");
                    bannerrowE.addAttribute("type",bannerrow.getString("itype"));
                    bannerrowE.addAttribute("picurl",bannerrow.getString("cpicurl"));
                    bannerrowE.addAttribute("data",bannerrow.getString("cdata"));
                    bannerrowE.addAttribute("title",bannerrow.getString("ctitle"));
                    resp.add(bannerrowE);
                }
            }
            if(root.getJSONObject("appBankPage")!=null&&root.getJSONObject("appBankPage").size()>0){
                Element loanE=new DOMElement("AppBankPage");
                JSONObject loading =root.getJSONObject("appBankPage");
                JsonUtil.jsonToElement(loanE,loading);
                resp.add(loanE);
            }
            if(root.getJSONObject("loanSwitch")!=null){
                Element loanE=new DOMElement("loanProgressSwitch");
                JSONObject loading =root.getJSONObject("loanSwitch");
                JsonUtil.jsonToElement(loanE,loading);
                resp.add(loanE);
            }
            // add by lcs 20161103 美洽客服
            Element loanE = new DOMElement("MQGID");
            if (root.getJSONObject("gid") != null) {
                JSONObject loading = root.getJSONObject("gid");
                JsonUtil.jsonToElement(loanE, loading);
            } else {
                loanE.addAttribute("gid", "");
            }
            if (root.getJSONObject("serviceTel") != null) {
                JSONObject loading = root.getJSONObject("serviceTel");
                JsonUtil.jsonToElement(loanE, loading);
            } else {
                loanE.addAttribute("serviceTel", "");
            }
            resp.add(loanE);
            // add by lcs 20161103 贷款显示
            Element DKSwitchE = new DOMElement("DKSwitch");
            if (root.getJSONObject("closeFlag") != null) {
                JSONObject loading = root.getJSONObject("closeFlag");
                JsonUtil.jsonToElement(DKSwitchE, loading);
            } else {
                DKSwitchE.addAttribute("closeFlag", "0");
            }
            resp.add(DKSwitchE);

            // add by lcs 20170105 增加默认配置 start
            Element DISPLAYLOAN = new DOMElement("DISPLAYLOAN");
            DISPLAYLOAN.addAttribute("displayLoan","1");
            resp.add(DISPLAYLOAN);
            // add by lcs 20170105 增加默认配置 end

            resp.addAttribute("code",root.getString("code"));
            resp.addAttribute("desc",root.getString("desc"));
        }else {
            resp.addAttribute("code","0");
            resp.addAttribute("desc","程序异常");
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/totalSearchByType.go")
    public String totalSearchByType(HttpServletRequest request,HttpServletResponse response) {
        JSONObject rt = new JSONObject();
        String icityid = request.getParameter("icityid");
        String source = request.getParameter("source");
        String pn = request.getParameter("pn");
        String ps = request.getParameter("ps");
        String query = request.getParameter("query");
        String clng = request.getParameter("clng");
        String clat = request.getParameter("clat");
        String searchtype = request.getParameter("searchtype");
        Cheap cheap = new Cheap();
        if (StringUtils.isNumeric(icityid)) {
            cheap.setIcityid(Long.parseLong(icityid));
        } else {
            rt.put("code", "0");
            rt.put("desc", "城市id不正确");
            return rt.toJSONString();
        }
        if (StringUtils.isNumeric(source)) {
            cheap.setSource(Integer.parseInt(source));
        }
        if (StringUtils.isNumeric(pn)) {
            cheap.setPn(Integer.parseInt(pn));
        } else {
            cheap.setPn(10);//默认的页面大小为10
        }
        if (StringUtils.isNumeric(ps)) {
            cheap.setPs(Integer.parseInt(ps));
        } else {
            cheap.setPs(1);//默认为第一页
        }
        cheap.setQuery(query);
        cheap.setSearchtype(searchtype);
        try {
            cheap.setClat(Double.parseDouble(clat));
            cheap.setClng(Double.parseDouble(clng));
        } catch (Exception e) {
            cheap.setClat(31.231706);  //默认设为上海
            cheap.setClng(121.472644);
        }
        if ("1".equals(searchtype)) {
            MaterialBean materialBean = new MaterialBean();
            materialBean.setSource(cheap.getSource());
            materialBean.setPn(cheap.getPn());
            materialBean.setPs(cheap.getPs());
            materialBean.getModel().setIcityid(icityid);
            materialBean.setCname(cheap.getQuery());
            String materialResult = client_material.execute(new DrpcRequest("material", "filterCard", materialBean));
            JSONObject jsonObject = JSONObject.parseObject(materialResult);
            if (jsonObject != null && jsonObject.get("cards")!=null) {
                JSONObject dataJson = new JSONObject();
                dataJson.put("contents", jsonObject.get("cards"));
                dataJson.put("type", 1);
                dataJson.put("isMore", 0);
                int count = jsonObject.getIntValue("cardstotal");
                if (count > 2) {
                    dataJson.put("isMore", 1);
                }
                rt.put("data", dataJson);
                rt.put("desc", "请求数据成功");
                rt.put("code", "1");
                dataJson.put("pn", jsonObject.getString("pageNum"));
                dataJson.put("tp", jsonObject.getString("totalPage"));
                dataJson.put("ps", jsonObject.getString("pageSize"));
                dataJson.put("rc", count);

            }else if(jsonObject != null && jsonObject.get("cards")== null){
                rt.put("code", "0");
                rt.put("desc", "没有获得有效值");
            }
            else {
                rt.put("code", "-1");
                rt.put("desc", "程序异常");
            }
            return rt.toJSONString();
        } else if ("2".equals(searchtype) || "3".equals(searchtype)) {
            String cheapResult = client.execute(new DrpcRequest("window", "totalSearchByType", cheap));
            return cheapResult;
        } else {
            rt.put("code", "0");
            rt.put("desc", "参数错误");
            return rt.toJSONString();
        }
    }
}
