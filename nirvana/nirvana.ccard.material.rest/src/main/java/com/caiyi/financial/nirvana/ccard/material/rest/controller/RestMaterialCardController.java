package com.caiyi.financial.nirvana.ccard.material.rest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.material.banks.guangda2.apply.GuangDaApplyUtil;
import com.caiyi.financial.nirvana.ccard.material.banks.minsheng.MinShengSubmit;
import com.caiyi.financial.nirvana.ccard.material.banks.pingan.PingAnApplyUtil;
import com.caiyi.financial.nirvana.ccard.material.banks.xingye.XingYeApply;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.materialCardImp.MaterialCardImp;
import com.caiyi.financial.nirvana.ccard.material.util.DataUtil;
import com.caiyi.financial.nirvana.ccard.material.util.bean.FillMaterialBean;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/26.
 */
@RestController
@RequestMapping("/credit")
public class RestMaterialCardController {

    private  static Logger log= LoggerFactory.getLogger(RestMaterialCardController.class);
    @Resource(name = Constant.HSK_CCARD_MATERIAL)
    IDrpcClient client;
    @Autowired
    MaterialCardImp cardImp;
    @Autowired
    MemCachedClient cc;

    @RequestMapping("/apply_bankVerifyCode.go")
    public  String  getBankVerifyCode(MaterialBean bean,HttpServletRequest request, HttpServletResponse response){
//        resp.clearContent();
        JSONObject jsonObject=new JSONObject();
        bean.setMediatype("json");
        if(StringUtils.isEmpty(bean.getIbankid())){
            jsonObject.put("code","0");
            jsonObject.put("desc","银行标识不能为空");
            return jsonObject.toJSONString();
        }
        Integer i= FillMaterialBean.getMaterialBean(bean,request);
        if(i!=1){
//            jsonObject.put("code",bean.getBusiErrCode());
//            jsonObject.put("desc",bean.getBusiErrDesc());
//            bean.toString();
            return bean.toString();
        }
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        HttpSession session = request.getSession();
        if (session.isNew()) {
            session.setMaxInactiveInterval(300);
        }
        MaterialModel model = bean.getModel();
        String cidcard = model.getCidcard();
        String cphone = model.getCphone();
        if (StringUtils.isEmpty(cidcard) || StringUtils.isEmpty(cphone)) {
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return jsonObject.toJSONString();
        }
        String keyPrefix = cidcard + cphone;
        try {
            ServletOutputStream localServletOutputStream = response.getOutputStream();
            BufferedImage localBufferedImage = null;
            //可以参考ChannelBeanImpl的getBankVerifyCode方法
            System.out.println("当前访问银行:" + bean.getIbankid());
            switch (bean.getIbankid()) {
                case "3"://光大
                    localBufferedImage = GuangDaApplyUtil.getImgCode(bean,cc);
                    break;
                case "7"://平安
                    localBufferedImage = PingAnApplyUtil.getApplyImgPre(bean,cc);
                    if(localBufferedImage==null){
                        jsonObject.put("code","0");
                        jsonObject.put("desc","没有获取到图片验证码");
                        return jsonObject.toJSONString() ;
                    }
                    break;
                case "11":// 民生by denghong
                    MinShengSubmit minshengSub = new MinShengSubmit();
                    localBufferedImage = minshengSub.getVerifyCode(bean,cc);
                    break;
                case "10"://兴业
                    XingYeApply xingye=new XingYeApply();
                    localBufferedImage = xingye.getCheckCode(bean,request,cc);
                    break;
            }
            if (localBufferedImage != null) {
                ImageIO.write(localBufferedImage, "PNG", localServletOutputStream);
                localServletOutputStream.flush();
                localServletOutputStream.close();
            } else {
                jsonObject.put("code","0");
                jsonObject.put("desc","无效银行ID");
                return  jsonObject.toJSONString();
            }
            jsonObject.put("code","1");
            jsonObject.put("desc","获得验证码成功");
            return jsonObject.toJSONString();
        }catch (Exception e){
            jsonObject.put("code","-1");
            jsonObject.put("desc","程序异常");
           return jsonObject.toJSONString();
        }
    }
    @RequestMapping("/apply_checkSms.go")
    public String apply_checkSms(MaterialBean bean,HttpServletRequest request,HttpServletResponse response){
        JSONObject object=new JSONObject();
        Integer i= FillMaterialBean.getMaterialBean(bean,request);
        if(i!=1){
            object.put("code","0");
            object.put("desc","参数错误");
            return object.toJSONString();
        }
       String result=client.execute(new DrpcRequest("materialCard","applyCreditConversion",bean));
        if(result!=null){
            bean=(MaterialBean)JSON.toJavaObject(JSON.parseObject(result),MaterialBean.class);
            if(bean.getBusiErrCode()!=1){
                return bean.toString();
            }else{
                Integer z= cardImp.checkBankMessage(bean,request,response,cc);
                if(z==1){
                    object.put("code","1");
                    object.put("desc","发送成功");
                    return  object.toJSONString();
                }else {
                    return bean.toString();
                }
            }
        }else {
            object.put("code","0");
            object.put("desc","程序异常");
            return  object.toJSONString();
        }
    }
    @RequestMapping("/apply_bankMessage.go")
    public String applyBankMessage(MaterialBean bean,HttpServletRequest request,HttpServletResponse response){
        JSONObject jsonObject=new JSONObject();
        //解密并把参数放在bean中
        Integer i= FillMaterialBean.getMaterialBean(bean,request);
        if(i!=1){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return jsonObject.toJSONString();
        }
        //获取请求地址的IP
        bean.setIpAddr(DataUtil.getRealIp(request));
       String result=client.execute(new DrpcRequest("materialCard","applyCreditConversion",bean));
        if(result!=null){
            if(!"500".equals(JSON.parseObject(result).get("code"))) {
                bean = (MaterialBean) JSON.toJavaObject(JSON.parseObject(result), MaterialBean.class);
                if (bean.getBusiErrCode() != 1) {
                    return bean.toString();
                } else {
                    Integer z = cardImp.getBankMessage(bean, request, response,cc);
                    if (z == 1) {
                        jsonObject.put("code", "1");
                        jsonObject.put("desc", "发送成功");
                        return jsonObject.toJSONString();
                    } else {
                        return bean.toString();
                    }
                }
            }else {
                jsonObject.put("code", "0");
                jsonObject.put("desc", "程序异常");
                return jsonObject.toJSONString();
            }
        }else {
            jsonObject.put("code","0");
            jsonObject.put("desc","程序异常");
            return  jsonObject.toJSONString();
        }
    }
    @RequestMapping("/apply_CreditCard.go")
    public  String apply_CreditCard(MaterialBean bean,HttpServletRequest request,HttpServletResponse response){
        JSONObject jsonObject=new JSONObject();
        bean.setBusiJSON("fail");
        bean.setMediatype("json");
        //解密并把参数放在bean中
        Integer i= FillMaterialBean.getMaterialBean(bean,request);
        if(i!=1){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return jsonObject.toJSONString();
        }
        i=cardImp.applyBankCreditCard(bean,request,response,cc);
        if(i==0){
            return  bean.toString();
        }
       String result=client.execute(new DrpcRequest("materialCard","applyCreditCard",bean));
        if(result!=null){
            if(!"500".equals(JSON.parseObject(result).get("code"))) {
                bean = (MaterialBean) JSON.toJavaObject(JSON.parseObject(result), MaterialBean.class);
                return bean.toString();
            }else {
                jsonObject.put("code", "0");
                jsonObject.put("desc", "程序异常");
                return jsonObject.toJSONString();
            }
        }else {
            jsonObject.put("code","0");
            jsonObject.put("desc","程序异常");
            return  jsonObject.toJSONString();
        }
    }
    @RequestMapping("/apply_tempData.go")
    public String saveOrUpdateMaterialDirty(MaterialBean bean,HttpServletRequest request,HttpServletResponse response){
        JSONObject jsonObject=new JSONObject();
        if(bean.getIclient()==null){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return jsonObject.toJSONString();
        }
//        bean.setIclient(Integer.parseInt(iclient));
        Integer i= FillMaterialBean.getMaterialBean(bean,request);
        if(i!=1){
            jsonObject.put("code",bean.getBusiErrCode());
            jsonObject.put("desc",bean.getBusiErrDesc());
            return jsonObject.toJSONString();
        }
      String  result=client.execute(new DrpcRequest("materialCard","saveOrUpdateMaterialDirty",bean));
        JSONObject json= JSON.parseObject(result);
        if(json==null){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return  jsonObject.toJSONString();
        }else{
           return  result;
        }
    }
    @RequestMapping("/spreadCount.go")
    public  String spreadCount(HttpServletRequest request,HttpServletResponse response){
        String ispreadid=request.getParameter("ispreadid");
        String ichannelid=request.getParameter("ichannelid");
        String ltype=request.getParameter("ltype");
        Map<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject();
        if(StringUtils.isEmpty(ichannelid)&&StringUtils.isEmpty(ispreadid)){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return jsonObject.toJSONString();
        }
        map.put("ispreadid",ispreadid);
        map.put("ichannelid",ichannelid);
        map.put("ltype",ltype);
        return client.execute(new DrpcRequest("materialCard","spreadCount",map));
    }
    @RequestMapping("/spreadCard.go")
    public  String spreadCard(HttpServletRequest request,HttpServletResponse response){
        String icityid=request.getParameter("icityid");
        String ichannelid=request.getParameter("ichannelid");
        String ibankid=request.getParameter("ibankid");
        Map<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject();
        if(StringUtils.isEmpty(ichannelid)){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return jsonObject.toJSONString();
        }
        if(StringUtils.isEmpty(ibankid)){
            jsonObject.put("code","0");
            jsonObject.put("desc","银行id错误");
            return jsonObject.toJSONString();
        }
        map.put("icityid",icityid);
        map.put("ichannelid",ichannelid);
        map.put("ibankid",ibankid);
        map.put("pn","2");
        map.put("ps","8");
        String result=client.execute(new DrpcRequest("materialCard","spreadCard",map));
        if(result==null||"".equals(result)){
            jsonObject.put("code","0");
            jsonObject.put("desc","参数错误");
            return jsonObject.toJSONString();
        }else{
            JSONObject object=JSONObject.parseObject(result);
            if(object!=null){
                jsonObject.put("cards", object.get("rows"));
                jsonObject.put("pn", object.get("pageNum"));
                jsonObject.put("tp", object.get("totalPage"));
                jsonObject.put("ps", object.get("pageSize"));
                jsonObject.put("cardstotal", object.get("records"));

                JSONObject object2=new JSONObject();
                object2.put("code","1");
                object2.put("desc","请求成功");
                object2.put("data",jsonObject.toJSONString());
             return object2.toJSONString();
            }else {
                jsonObject.put("code","0");
                jsonObject.put("desc","程序异常");
                return jsonObject.toJSONString();
            }
        }
    }
    @RequestMapping("/spreadBank.go")
    public  String spreadBank(HttpServletRequest request, HttpServletResponse response){
        String icityid=request.getParameter("icityid");
        String ichannelid=request.getParameter("ichannelid");
        Map<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject();
        if(StringUtils.isEmpty(ichannelid)){
            jsonObject.put("code","0");
            jsonObject.put("desc","没有参数错误");
            return jsonObject.toJSONString();
        }
        if(StringUtils.isEmpty(icityid)){
            jsonObject.put("code","0");
            jsonObject.put("desc","没有参数错误");
            return jsonObject.toJSONString();
        }
        map.put("icityid",icityid);
        map.put("ichannelid",ichannelid);
        map.put("pn","2");
        map.put("ps","8");
        String result=client.execute(new DrpcRequest("materialCard","spreadBank",map));
        if(result==null||"".equals(result)){
            jsonObject.put("code","0");
            jsonObject.put("desc","程序异常");
            return jsonObject.toJSONString();
        }else {
            jsonObject.put("code","1");
            jsonObject.put("desc","请求成功");
            jsonObject.put("data",result);
            return jsonObject.toJSONString();
        }
    }
}
