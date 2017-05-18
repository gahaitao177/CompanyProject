package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.intercept.SetUserDataRequired;
import com.caiyi.financial.nirvana.discount.utils.JsonUtil;
import com.caiyi.financial.nirvana.discount.utils.WebUtil;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lizhijie on 2016/7/4.
 */
@RequestMapping("/credit")
@RestController
public class RestManualCardController {

    private  static Logger log= LoggerFactory.getLogger(RestManualCardController.class);
    @Resource(name = Constant.HSK_CCARD_INFO)
    IDrpcClient client;

    Document dom=new DOMDocument();
    Element resp=new DOMElement("Resp");
    String result="";
    JSONObject json=null;

    public RestManualCardController(){
        dom.setRootElement(resp);
    }
    @RequestMapping("/uCardApplySendYzm.go")
    public  void cardApplySendYZM(HttpServletRequest request, HttpServletResponse response){
        resp.clearContent();
        log.info("cardApplySendYZM.go");
        String phonenum=request.getParameter("phonenum");
        String timestamp=request.getParameter("timestamp");
        String key=request.getParameter("key");
        String bankid=request.getParameter("bankid");
        Card card=new Card();
        card.setPhonenum(phonenum);
        card.setTimestamp(timestamp);
        card.setKey(key);
        card.setBankid(bankid);
        String ip= WebUtil.getRealIp(request);
        card.setIpAddr(ip);
        if (StringUtils.isEmpty(phonenum) || !CheckUtil.isMobilephone(phonenum)) {
            resp.addAttribute("code","1000");
            resp.addAttribute("desc","手机号码格式错误!");
            log.info("手机号码格式错误!"+"IP:"+ip);
            return;
        }
        if (!CheckUtil.isNullString(timestamp) && CheckUtil.isNullString(key)) {
            resp.addAttribute("code","1001");
            resp.addAttribute("desc","参数错误!");
            log.info("参数错误!"+"IP:"+ip);
            return;
        }
        if (StringUtils.isEmpty(bankid)||"undefined".equals(bankid)){
            resp.addAttribute("code","1001");
            resp.addAttribute("desc","没有获取到银行ID，请退出到主页后重新申请。");
            log.info("没有获取到银行ID，请退出到主页后重新申请。"+"IP:"+ip);
            return;
        }
        result=client.execute(new DrpcRequest("manualCard","cardApplySendYZM",card));

        json=JSONObject.parseObject(result);
        resp.addAttribute("code",json.get("busiErrCode").toString());
        resp.addAttribute("desc",json.get("busiErrDesc").toString());
        log.info("code:"+json.get("busiErrCode").toString()+",desc:"+json.get("busiErrDesc").toString()+",IP:"+ip);
        XmlUtils.writeXml(dom,response);

    }
    @RequestMapping("/uCheckYzm.go")
    public  void  checkYZM(HttpServletResponse response,HttpServletRequest request){
        resp.clearContent();
        String phonenum=request.getParameter("phonenum");
        String yzm=request.getParameter("yzm");
        if(CheckUtil.isNullString(phonenum)||!CheckUtil.isMobilephone(phonenum)){
            resp.addAttribute("code","1000");
            resp.addAttribute("desc","手机号码格式错误!");
            log.info("手机号:"+phonenum+",desc:手机号码格式错误!");
            XmlUtils.writeXml(dom,response);
        }
        if (CheckUtil.isNullString(yzm)){
            resp.addAttribute("code","1001");
            resp.addAttribute("desc","验证码不能为空");
            log.info("手机号:"+phonenum+",desc:验证码为空!");
            XmlUtils.writeXml(dom,response);
        }
        if("2978172".equals(yzm)){//测试关闭短信验证码
            resp.addAttribute("code","1");
            resp.addAttribute("desc","测试关闭短信验证码");
            XmlUtils.writeXml(dom,response);
        }
        Card card=new Card();
        card.setPhonenum(phonenum);
        card.setYzm(yzm);
        result=client.execute(new DrpcRequest("manualCard","checkYZM",card));
        json=JSONObject.parseObject(result);
        resp.addAttribute("code",json.get("busiErrCode").toString());
        resp.addAttribute("desc",json.get("busiErrDesc").toString());
        log.info("code:"+json.get("busiErrCode")+",desc:"+json.get("busiErrDesc"));
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/uCardApply.go")
    public  void cardAppliedInfo(HttpServletRequest request,HttpServletResponse response){
        resp.clearContent();
        Card card=getBeanByRequest(request);
        log.info("卡申请 Bankid="  + card.getBankid());
        log.info("卡申请 Cardid="  + card.getCardid());
        log.info("卡申请 用户所在城市="  + card.getPrivincecode() +"，用户选择城市="+card.getCitycode());
        card.setIstatus("0");
        card.setIsuccess("0");
        if(!checkData(card)){
            XmlUtils.writeXml(dom,response);
            return;
        }
        result=client.execute(new DrpcRequest("manualCard","cardAppliedInfo",card));
        json=JSONObject.parseObject(result);
        resp.addAttribute("code",json.get("busiErrCode").toString());
        resp.addAttribute("desc",json.get("busiErrDesc").toString());
        log.info("code:"+json.get("busiErrCode")+",desc:"+json.get("busiErrDesc"));
        XmlUtils.writeXml(dom,response);
    }
    private  Card getBeanByRequest(HttpServletRequest request){
        String profession=request.getParameter("profession");
        String use=request.getParameter("use");
        String overdue=request.getParameter("overdue");
        String socialpay=request.getParameter("socialpay");
        String workprove=request.getParameter("workprove");
        String otherbank=request.getParameter("otherbank");
        String advantage=request.getParameter("advantage");
        String name=request.getParameter("name");
        String phonenum=request.getParameter("phonenum");
        String idegree=request.getParameter("idegree");
        String cworkorg=request.getParameter("cworkorg");
        String workplace=request.getParameter("workplace");
        String age=request.getParameter("age");
        String cardid=request.getParameter("cardid");
        String bankid=request.getParameter("bankid");
        String privincecode=request.getParameter("privincecode");
        String citycode=request.getParameter("citycode");
        String countycode=request.getParameter("countycode");
        String cgroupcode=request.getParameter("cgroupcode");
        String lat=request.getParameter("lat");
        String lng=request.getParameter("lng");
        String gender=request.getParameter("gender");
        String isuccess=request.getParameter("isuccess");
        String istatus=request.getParameter("istatus");
        String ihouse=request.getParameter("ihouse");
        String icar=request.getParameter("icar");

        Card card=new Card();
        card.setProfession(profession);
        card.setUse(use);
        card.setOverdue(overdue);
        card.setSocialpay(socialpay);
        card.setWorkprove(workprove);
        card.setOtherbank(otherbank);
        card.setAdvantage(advantage);
        card.setName(name);
        card.setPhonenum(phonenum);
        card.setIdegree(idegree);
        card.setCworkorg(cworkorg);
        card.setWorkplace(workplace);
        card.setAge(age);
        card.setCardid(cardid);
        card.setBankid(bankid);
        card.setPrivincecode(privincecode);
        card.setCitycode(citycode);
        card.setCountycode(countycode);
        card.setCgroupcode(cgroupcode);
        card.setLat(lat);
        card.setLng(lng);
        card.setGender(gender);
        card.setIsuccess(isuccess);
        card.setIstatus(istatus);
        card.setIhouse(ihouse);
        card.setIcar(icar);

        return card;
    }
    private  Boolean checkData(Card card){
        if (StringUtils.isEmpty(card.getBankid())||"undefined".equals(card.getBankid())){
            resp.addAttribute("code","-1");
            resp.addAttribute("desc","没有获取到银行ID，请退出到主页后重新申请。");
            return false;
        }
        if (StringUtils.isEmpty(card.getPrivincecode())){
            resp.addAttribute("code","-1");
            resp.addAttribute("desc","没有获取到您的真实定位");
            return false;
        }
        if (StringUtils.isEmpty(card.getCitycode())){
            resp.addAttribute("code","-1");
            resp.addAttribute("desc","请选择城市");
            return false;
        }
        if (StringUtils.isEmpty(card.getCgroupcode())){
            resp.addAttribute("code","-1");
            resp.addAttribute("desc","请选择商圈");
            return false;
        }
        //精确判断城市参数：
        if (!isAdCode(card.getPrivincecode())){
            resp.addAttribute("code","-1");
            resp.addAttribute("desc","定位城市code有误");
            return false;
        }
        if (!isAdCode(card.getCitycode())){
            resp.addAttribute("code","-1");
            resp.addAttribute("desc","选择城市code有误");
            return false;
        }
        if (!isAdCode(card.getCgroupcode())){
            resp.addAttribute("code","-1");
            resp.addAttribute("desc","选择商圈code有误");
            return false;
        }
        String mobileNo = card.getPhonenum();
        log.info("lng "  + card.getLng());
        log.info("lat "  + card.getLat());
        if (StringUtils.isEmpty(mobileNo) || !CheckUtil.isMobilephone(mobileNo)) {
            resp.addAttribute("code","1000");
            resp.addAttribute("desc","手机号码格式错误!");
            return false;
        }
        return true;
    }
    private boolean isAdCode(String adcode) {
        if (StringUtils.isEmpty(adcode)){
            return false;
        }
        try {
            Integer.parseInt(adcode);
        }catch (Exception e){
            return false;
        }
        if (adcode.length()<6){
            return false;
        }
        return true;
    }
    /*
    * 查询用户可以申请的银行
    * **/
    @RequestMapping("/uApplyBank.go")
    public void queryUserBank(HttpServletResponse response,HttpServletRequest request){
        resp.clearContent();
       String citycode=request.getParameter("citycode");
       String phonenum=request.getParameter("phonenum");
        log.info("citycode="+citycode+",phonenum="+phonenum);
        Card card=new Card();
        card.setCitycode(citycode);
        card.setPhonenum(phonenum);
        if(CheckUtil.isNullString(phonenum)||CheckUtil.isNullString(citycode)){
            resp.addAttribute("code",-1+"");
            resp.addAttribute("desc","参数有误");
            log.info("参数有误");
            XmlUtils.writeXml(dom,response);
            return;
        }
        result=client.execute(new DrpcRequest("manualCard","queryUserBank",card));
        JSONArray bankList=JSONArray.parseArray(result);
        if(bankList!=null&&bankList.size()>0){
            log.info("获得可申请的银行数量"+bankList.size());
            JsonUtil.jsonToElement(bankList,resp,"row",null);
            resp.addAttribute("code",1+"");
            resp.addAttribute("desc","获得银行成功");
        }else{
            resp.addAttribute("code",1+"");
            resp.addAttribute("desc","没有获得可申请的银行");
            log.info("没有获得可申请的银行");
        }
        XmlUtils.writeXml(dom,response);
        return;
    }
    @RequestMapping("/applyProgressYzm.go")
    public  void applyProgressYzm(HttpServletRequest request, HttpServletResponse response){
        log.info("applyProgressYzm.go");
        resp.clearContent();
        String phonenum=request.getParameter("phonenum");
        String timestamp=request.getParameter("timestamp");
        String key=request.getParameter("key");
        Card card=new Card();
        card.setPhonenum(phonenum);
        card.setTimestamp(timestamp);
        card.setKey(key);
        String ip= WebUtil.getRealIp(request);
        card.setIpAddr(ip);
        if (StringUtils.isEmpty(phonenum) || !CheckUtil.isMobilephone(phonenum)) {
            resp.addAttribute("code","1000");
            resp.addAttribute("desc","手机号码格式错误!");
            log.info("手机号码格式错误!"+"IP:"+ip);
            return;
        }
        if (!CheckUtil.isNullString(timestamp) && CheckUtil.isNullString(key)) {
            resp.addAttribute("code","1001");
            resp.addAttribute("desc","参数错误!");
            log.info("参数错误!"+"IP:"+ip);
            return;
        }
        result=client.execute(new DrpcRequest("manualCard","applyProgressYzm",card));
        json=JSONObject.parseObject(result);
        resp.addAttribute("code",json.get("busiErrCode").toString());
        resp.addAttribute("desc",json.get("busiErrDesc").toString());
        log.info("code:"+json.get("busiErrCode").toString()+",desc:"+json.get("busiErrDesc").toString()+",IP:"+ip);
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/checkApplyProgressYzm.go")
    public  void  checkApplyProgressYzm(HttpServletResponse response,HttpServletRequest request){
        resp.clearContent();
        String phonenum=request.getParameter("phonenum");
        String yzm=request.getParameter("yzm");
        if(CheckUtil.isNullString(phonenum)||CheckUtil.isMobilephone(phonenum)){
            resp.addAttribute("code","1000");
            resp.addAttribute("desc","手机号码格式错误!");
            log.info("手机号:"+phonenum+",desc:手机号码格式错误!");
            XmlUtils.writeXml(dom,response);
            return;
        }
        if (CheckUtil.isNullString(yzm)){
            resp.addAttribute("code","1001");
            resp.addAttribute("desc","验证码不能为空");
            log.info("手机号:"+phonenum+",desc:验证码为空!");
            XmlUtils.writeXml(dom,response);
            return;
        }
        if("2978172".equals(yzm)){//测试关闭短信验证码
            resp.addAttribute("code","1");
            resp.addAttribute("desc","测试关闭短信验证码");
            XmlUtils.writeXml(dom,response);
            return;
        }
        Card card=new Card();
        card.setPhonenum(phonenum);
        card.setYzm(yzm);
        result=client.execute(new DrpcRequest("manualCard","checkYZM",card));
        json=JSONObject.parseObject(result);
        resp.addAttribute("code",json.get("busiErrCode").toString());
        resp.addAttribute("desc",json.get("busiErrDesc").toString());
        log.info("code:"+json.get("busiErrCode")+",desc:"+json.get("busiErrDesc"));
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/o2oProgress.go")
    public void queryProgressOfCard(HttpServletRequest request,HttpServletResponse response){
        resp.clearContent();
        String phonenum=request.getParameter("phonenum");
        if(CheckUtil.isNullString(phonenum)||!CheckUtil.isMobilephone(phonenum)){
            resp.addAttribute("code","1000");
            resp.addAttribute("desc","手机号码格式错误!");
            log.info("手机号:"+phonenum+",desc:手机号码格式错误!");
            XmlUtils.writeXml(dom,response);
            return;
        }
        result=client.execute(new DrpcRequest("manualCard","queryProgressOfCard",phonenum));
        JSONArray objects=JSONArray.parseArray(result);
        if(objects!=null&&objects.size()>0) {
            JsonUtil.jsonToElement(objects, resp, "row", null);
            resp.addAttribute("code",1+"");
            resp.addAttribute("desc","查询成功");
            log.info("手机号:" + phonenum+ ",查询成功" );
        }else {
            resp.addAttribute("code",0+"");
            resp.addAttribute("desc","查询失败");
            log.info("手机号:" + phonenum+ ",查询失败" );
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/o2oProgressDetail.go")
    public void queryDetailProgressOfCard(HttpServletRequest request,HttpServletResponse response){
        log.info("访问接口-----o2oProgressDetail");
        String iapplyid=request.getParameter("iapplyid");
        resp.clearContent();
        if(CheckUtil.isNullString(iapplyid)){
            resp.addAttribute("code","1000");
            resp.addAttribute("desc","参数错误");
            log.info("申请卡iapplyid=:"+iapplyid+"，参数错误!");
            XmlUtils.writeXml(dom,response);
            return;
        }
        result=client.execute(new DrpcRequest("manualCard","queryDetailProgressOfCard",iapplyid));
        JSONObject object =JSONObject.parseObject(result);

        if(object!=null) {
            Element row=new DOMElement("row");
            JsonUtil.jsonToElement(row,object);
            resp.add(row);
            resp.addAttribute("code",1+"");
            resp.addAttribute("desc","查询成功");
            log.info("申请卡信息iapplyid=" + iapplyid+ ",查询成功" );
        }else {
            resp.addAttribute("code",0+"");
            resp.addAttribute("desc","查询失败");
            log.info("申请卡信息iapplyid=" + iapplyid+ ",查询失败" );
        }
        XmlUtils.writeXml(dom,response);
    }
    @RequestMapping("/uCardApplyCounts.go")
    public  void updateCardApplyCounts(HttpServletResponse response,HttpServletRequest request){
        resp.clearContent();
        log.info("访问接口-----updateCardApplyCounts");
        String cardid=request.getParameter("cardid");
        if(CheckUtil.isNullString(cardid)){
            resp.addAttribute("code",1000+"");
            resp.addAttribute("desc","卡id不能为空");
            XmlUtils.writeXml(dom,response);
            return;
        }
        result=client.execute(new DrpcRequest("manualCard","updateCardApplyCounts",cardid));
        json=JSONObject.parseObject(result);
        if(json.get("count")!=null){
            String count=json.get("count").toString();
            if("0".equals(count)){
                resp.addAttribute("code",0+"");
                resp.addAttribute("desc","没有找到所有更新的卡号,cardid="+cardid);
                log.info("没有找到所有更新的卡号");
            }else if("1".equals(count)){
                resp.addAttribute("code",1+"");
                resp.addAttribute("desc","更新的卡号,cardid="+cardid);
                log.info("更新的卡号,cardid="+cardid);
            }else{
                resp.addAttribute("code",-1+"");
                resp.addAttribute("desc","更新异常,cardid="+cardid);
                log.info("更新异常,cardid="+cardid);
            }
        }else{
            resp.addAttribute("code",-1+"");
            resp.addAttribute("desc","更新异常,cardid="+cardid);
            log.info("更新异常,cardid="+cardid);
        }
        XmlUtils.writeXml(dom,response);
        return;
    }
    @SetUserDataRequired
    @RequestMapping("/channel_cards_info.go")
    public  void queryChannelContend(HttpServletRequest request,HttpServletResponse response){
        resp.clearContent();
        String cuserid=request.getParameter("cuserId");
        if(StringUtils.isEmpty(cuserid)){
            resp.addAttribute("code","0");
            resp.addAttribute("desc","未登录");
            XmlUtils.writeXml(dom,response);
            return;
        }
        String channelid=request.getParameter("ichannelid");
        result=client.execute(new DrpcRequest("manualCard","queryChannelContend",channelid));
        JSONArray array=JSONArray.parseArray(result);
        if(array!=null&&array.size()>0){
            for(int i=0;i<array.size();i++){
                JSONObject jsonObject=array.getJSONObject(i);
                Element channel=new DOMElement("Channel");
                for (String key:jsonObject.keySet()) {
                    if(!"data".equals(key)) {
                        if("busiErrCode".equals(key)){
                            channel.addAttribute("code",jsonObject.getString(key));
                        }else if("busiErrDesc".equals(key)){
                            channel.addAttribute("desc",jsonObject.getString(key));
                        }else{
                            channel.addAttribute(key,jsonObject.getString(key));
                        }
                    }else{
                        if(jsonObject.get("data")!=null){
                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            JsonUtil.jsonToElement(jsonArray,channel,"Card",null);
                        }
                    }
                }
                resp.addAttribute("code",1+"");
                resp.addAttribute("desc","获得渠道的内容成功");
                resp.add(channel);
                log.info("获得渠道的内容成功,ichannelid="+channelid);
            }
        }else{
            resp.addAttribute("code",0+"");
            resp.addAttribute("desc","没有获得渠道的内容,ichannelid="+channelid);
            log.info("没有获得渠道的内容,ichannelid="+channelid);
        }
        XmlUtils.writeXml(dom,response);
        return;
    }
}
