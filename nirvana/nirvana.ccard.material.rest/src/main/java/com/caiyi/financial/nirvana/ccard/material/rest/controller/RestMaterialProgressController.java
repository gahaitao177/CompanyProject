package com.caiyi.financial.nirvana.ccard.material.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.materialCardImp.ProgressImp;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
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
 * Created by lizhijie on 2016/7/29.
 */
@RestController
@RequestMapping("/credit")
public class RestMaterialProgressController {
    private static Logger logger = LoggerFactory.getLogger(RestMaterialProgressController.class);

   @Autowired
    private ProgressImp progressImp;
    @Resource(name = Constant.HSK_CCARD_MATERIAL)
    IDrpcClient client;

    /**
     * 获得查询进度的图片验证码
     * @param request
     * @param response
     */
    @RequestMapping("/apply_credit_img_yzm.go")
    public void applyCreditIMGyzm(HttpServletRequest request, HttpServletResponse response){
        Element resp=new DOMElement("Resp");
        Document dom= DocumentHelper.createDocument();
        dom.setRootElement(resp);
        String bankid=request.getParameter("ibankid");
        String orderid=request.getParameter("orderid");
        String cname=request.getParameter("cname");
        String iapplyid=request.getParameter("iapplyid");
        String cphone=request.getParameter("cphone");
        String idcardid=request.getParameter("idcardid");
        MaterialBean bean=new MaterialBean();
        bean.setIbankid(bankid);
        bean.setOrderid(orderid);
        bean.setCphone(cphone);
        bean.setCname(cname);
        bean.setIdcardid(idcardid);
        bean.setIapplyid(iapplyid);
        try {
            progressImp.apply_credit_img_yzm(bean,request,response);
        } catch (Exception e) {
            logger.info("程序异常,{}",e.toString());
        }
        resp.addAttribute("code",bean.getBusiErrCode()+"");
        resp.addAttribute("desc",bean.getBusiErrDesc());
        XmlUtils.writeXml(dom,response);
    }

    /**
     * 信用卡进度查询获得手机验证码
     * @param request
     * @param response
     */
    @RequestMapping("/apply_credit_phone_yzm.go")
    public void applyCreditPhoneYZM (HttpServletRequest request, HttpServletResponse response){
        String bankid=request.getParameter("ibankid");
        String orderid=request.getParameter("orderid");
        String idcardid=request.getParameter("idcardid");
        String cphone=request.getParameter("cphone");
        String imgauthcode=request.getParameter("imgauthcode");
        MaterialBean bean=new MaterialBean();
        bean.setIbankid(bankid);
        bean.setOrderid(orderid);
        bean.setCphone(cphone);
        bean.setIdcardid(idcardid);
        bean.setImgauthcode(imgauthcode);
        Element resp=new DOMElement("Resp");
        Document dom= DocumentHelper.createDocument();
        dom.setRootElement(resp);
        try {
            progressImp.apply_credit_phone_yzm(bean,request,response);
        } catch (Exception e) {
            logger.info("程序异常,{}",e.toString());
            resp.addAttribute("code","0");
            resp.addAttribute("desc","程序异常");
            XmlUtils.writeXml(dom,response);
            return;
        }
        resp.addAttribute("code",bean.getBusiErrCode()+"");
        resp.addAttribute("desc",bean.getBusiErrDesc());
        resp.addAttribute("data",bean.getBusiJSON());
        XmlUtils.writeXml(dom,response);
        return;
    }

    /**
     * 协议接口
     * @param request
     * @param response
     */
    @RequestMapping("/apply_credit_agreement.go")
    public String  getBankAgreement(HttpServletRequest request, HttpServletResponse response){
        String bankId=request.getParameter("ibankid");
        MaterialBean bean=new MaterialBean();
        bean.setIbankid(bankId);
        bean.setMediatype("json");
        progressImp.getBankAgreement(bean,request,response);
        return bean.toString();
    }

    /**
     * 信用卡进度查询验证码
     * @param request
     * @param response
     */
    @RequestMapping("/apply_credit_query.go")
    public String applyCreditQuery(HttpServletRequest request, HttpServletResponse response){
//        String bankid=request.getParameter("ibankid");
        JSONObject zz=new JSONObject();
        zz.put("code","0");
        zz.put("desc","查询失败");
        String orderid=request.getParameter("orderid");
//        String cname=request.getParameter("cname");
        String iapplyid=request.getParameter("iapplyid");
        String cphone=request.getParameter("cphone");
//        String idcardid=request.getParameter("idcardid");
        String imgauthcode=request.getParameter("imgauthcode");
        String phoneauthcode=request.getParameter("phoneauthcode");
        MaterialBean bean=new MaterialBean();
//        bean.setIbankid(bankid);
        bean.setOrderid(orderid);
        bean.setMediatype("json");
        bean.setCphone(cphone);
//        bean.setCname(cname);
//        bean.setIdcardid(idcardid);
        bean.setIapplyid(iapplyid);
        bean.setImgauthcode(imgauthcode);
        bean.setPhoneauthcode(phoneauthcode);
        String result=client.execute(new DrpcRequest("materialCard","applyCreditQueryConversion",bean));
        if(result!=null){
            bean=(MaterialBean) JSON.toJavaObject(JSON.parseObject(result),MaterialBean.class);
            if(bean.getBusiErrCode()!=1){
//                resp.addAttribute("code",bean.getBusiErrCode()+"");
//                resp.addAttribute("desc",bean.getBusiErrDesc());
//                resp.addAttribute("data",bean.getBusiJSON());
//                XmlUtils.writeXml(dom,response);
                return bean.toString();
            }else{
                int query=progressImp.apply_credit_query(bean,request,response);
                if(query==1) {
                    result = client.execute(new DrpcRequest("materialCard", "updateApplyCreditLog", bean));
                    if (result != null) {
                        if (Integer.parseInt(result) > 0) {
                            zz.put("code", bean.getBusiErrCode() + "");
                            zz.put("desc", "查询成功");
//                        resp.addAttribute("code",bean.getBusiErrCode()+"");
//                        resp.addAttribute("desc","查询成功");
//                        XmlUtils.writeXml(dom,response);
                            return zz.toJSONString();
                        }
                    }
                }else{
                    return bean.toString();
                }
            }
        }
        return  zz.toJSONString();
    }
}
