package com.caiyi.financial.nirvana.discount.user.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.XmlTool;
import com.caiyi.financial.nirvana.discount.user.bean.AreaBean;
import com.caiyi.financial.nirvana.discount.user.bean.HomePage;
import com.caiyi.financial.nirvana.discount.user.bean.HomePageBean;
import com.caiyi.financial.nirvana.discount.user.exception.UserException;
import com.caiyi.financial.nirvana.discount.user.mapper.AreaMapper;
import com.caiyi.financial.nirvana.discount.user.mapper.HomePageMapper;
import com.github.pagehelper.PageHelper;
import com.util.string.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wenshiliang on 2016/8/31.
 */
@Service
public class HomePageService extends AbstractService {
    @Autowired
    HomePageMapper homePageMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private UserService userService;


    public JSONObject homePage(HomePageBean bean){
        JSONObject result=new JSONObject();
        try{
            JSONArray jsonArray = new JSONArray();
            String adcode = bean.getAdcode();
            String hskcityid = bean.getHskcityid();
            adcode = getAdcode(adcode,hskcityid);
            if(StringUtil.isEmpty(adcode)){
                result.put("code",0);
                result.put("desc","查询失败");
                return result;
            }
            /*
            BANNER:广告
            QUICK:动态快速入口
            PACT:产品运营
            SEM:广告推广
            CARDSHOW:信用卡推荐
            HOTMSG 头条
             */
            List<HomePage>jrs=  homePageMapper.qurey_homePage_fuzzy("%"+adcode+"%");
            int quick = 1;
            int pact = 3;
            int sem = 4;
            if(jrs!=null){
                for(int i = 0; i < jrs.size(); i ++){
                    boolean flag = true;
                    String type = jrs.get(i).getType();
                    if("PACT".equals(type)){
                        if(pact>0){
                            pact--;
                        }else{
                            flag = false;
                        }
                    }else if("QUICK".equals(type)){
                        if(quick>0){
                            quick--;
                        }else{
                            flag = false;
                        }
                    }else if("SEM".equals(type)){
                        if(sem>0){
                            sem--;
                        }else{
                            flag = false;
                        }
                    }
                    if(flag){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("type",type);
                        jsonObject.put("title",jrs.get(i).getTitle());
                        jsonObject.put("subTitle",jrs.get(i).getSub_title());
                        jsonObject.put("picUrl",jrs.get(i).getPic_url());
                        jsonObject.put("actionType",jrs.get(i).getAction_type());
                        jsonObject.put("param01",jrs.get(i).getParam01());
                        jsonObject.put("param02",jrs.get(i).getParam02());
                        jsonArray.add(jsonObject);
                    }

                }
            }
            String xmlStr = userService.fetchComm( bean.getCuserId(), 6,null);
            Document xmlWaper1  = XmlTool.read(xmlStr,"UTF-8");
            Element rootElement1  = XmlTool.getRootElement(xmlWaper1);
            List<Element> xmlNodeList = rootElement1.elements("fontaitem");

            for (int i =0,size=xmlNodeList.size();i<3 && i<size;i++){
                Element xmw = xmlNodeList.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type","POINT");
                jsonObject.put("title",xmw.attributeValue("cname"));
                List<Element> lists = xmw.elements();
                String cash = lists.get(0).attributeValue("ccash");
                jsonObject.put("subTitle",xmw.attributeValue("cbankname")+cash);
                jsonObject.put("picUrl",xmw.attributeValue("clistimg"));
                jsonObject.put("param01",xmw.attributeValue("icommid"));
                jsonObject.put("param02","");
                jsonObject.put("actionType","13");
                jsonArray.add(jsonObject);
            }
            result.put("code",1);
            result.put("desc","查询成功");
            result.put("data",jsonArray);
            return  result;
        }catch (Exception e){
            logger.error("首页接口查询失败",e);
            e.printStackTrace();
            result.put("code",0);
            result.put("desc","查询异常");
            result.put("data","");
        }
        return  result;
    }


    /**
     * 根据高德code  或者惠刷卡城市id获得 高德城市code
     * @param adcode
     * @param hskcityid
     * @return
     */
    public  String getAdcode(String adcode,String hskcityid){

        if (StringUtil.isEmpty(adcode)) {
            if (!StringUtil.isEmpty(hskcityid)) {
                AreaBean bean = new AreaBean();
                bean.setIareaid(Integer.valueOf(hskcityid));
                List<AreaBean>  beanList= areaMapper.qurey_area_adcode(bean);
                if(beanList !=null && beanList.size()>0){
                    adcode = beanList.get(0).getAdcode();
                }
            }
        } else {
            Integer adcodeNum    =  areaMapper.qurey_district(adcode, adcode.substring(0, adcode.length() - 2) + "00");
            adcode = String.valueOf(adcodeNum);
        }
        return adcode;
    }


    public JSONArray selectHomePage(HomePageBean bean){
        String adcode = bean.getAdcode();
        String hskcityid = bean.getHskcityid();
        adcode = getAdcode(adcode,hskcityid);
        if(StringUtil.isEmpty(adcode)){
            throw new UserException("查询失败");
        }
        JSONArray jsonArray = new JSONArray();
        if(bean.getPageNum()!=null && bean.getPageSize()!=null){
            PageHelper.startPage(bean.getPageNum(),bean.getPageSize());
        }

        adcode = "%"+adcode+"%";
        List<HomePage> list = homePageMapper.queryHomePage(adcode,bean.getHomePageType());

        if(list.size()>0){
            list.forEach(page -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type",page.getType());
                jsonObject.put("title",page.getTitle());
                jsonObject.put("subTitle",page.getSub_title());
                jsonObject.put("picUrl",page.getPic_url());
                jsonObject.put("actionType",page.getAction_type());
                jsonObject.put("param01",page.getParam01());
                jsonObject.put("param02",page.getParam02());
                jsonArray.add(jsonObject);
            });
        }
        return jsonArray;

    }


}
