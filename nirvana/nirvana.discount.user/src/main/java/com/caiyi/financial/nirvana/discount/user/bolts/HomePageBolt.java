package com.caiyi.financial.nirvana.discount.user.bolts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.user.bean.*;
import com.caiyi.financial.nirvana.discount.user.service.HomePageService;
import com.caiyi.financial.nirvana.discount.user.service.UserService;
import com.github.pagehelper.Page;
import com.util.string.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by dengh on 2016/8/1.
 */
@Bolt(boltId = "HomePageBolt", parallelismHint = 1, numTasks = 1)
public class HomePageBolt extends BaseBolt {
    @Autowired
    private UserService userService;

    @Autowired
    private HomePageService homePageService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }

    @BoltController
    public BoltResult homePage(HomePageBean bean){
       JSONObject json= homePageService.homePage(bean);
        if(json!=null) {
            return new BoltResult(json.getString("code"),json.getString("desc"), json.get("data"));
        }else {
            return new BoltResult(BoltResult.ERROR, "查询异常",null);
        }
    }

    @BoltController
    public JSONObject enjoyCheap(HomePageBean bean) {
        bean.setBusiJSON("");
        JSONObject  jsonObject = new JSONObject();
        try {

            String icityid = bean.getIcityid();
            String hskcityid = bean.getHskcityid();
            String ibankids = bean.getIbankids();
            logger.info("惠刷卡城市id"+icityid+"---"+hskcityid+"银行id:"+ibankids);
            JSONObject data = new JSONObject();
            /*
            信用卡优惠推荐商户
            三个板块
            美食：最多4个，没有隐藏，上新进美食列表
            娱乐：最多2个，没有隐藏，热搜进电影列表
            商旅：最多2个，没有隐藏，全部进商旅列表
             */
            StringBuilder sqlwhere = new StringBuilder();
            if(!StringUtil.isEmpty(ibankids)){
                String[] ibankidArr = ibankids.split(",");
                if(ibankidArr.length>0){
                    sqlwhere.append("and c.cbankid in (");
                    for (String s : ibankidArr) {
                        sqlwhere.append(s).append(",");
                    }
                    sqlwhere.deleteCharAt(sqlwhere.length()-1);
                    sqlwhere.append(" )");
                }
                logger.info("优惠查询sqlwhere:"+sqlwhere);
            }
            HashMap<String, String> maps = new HashMap<String, String>();
            maps.put("sqlwhere", sqlwhere.toString());
            List<RecommendShopBean>jrs= userService.query_recommend_shop(bean.getIcityid(),sqlwhere.toString());
            if (jrs != null && jrs.size() > 0) {

                List<JSONObject> list0 = new ArrayList<>();
                List<JSONObject> list1 = new ArrayList<>();
                for (int i = 0; i <jrs.size(); i ++) {
                    JSONObject obj = new JSONObject();
                    String ibusid = jrs.get(i).getIbusid().toString();
                    String clogo = jrs.get(i).getClogo();
                    String cbusname = jrs.get(i).getCbusname();
                    int iorder = jrs.get(i).getIorder();
                    String type = jrs.get(i).getType();
                    obj.put("shopid",ibusid);
                    obj.put("shopname",cbusname);
                    obj.put("shoplogo",clogo);
                    obj.put("iorder",iorder);
                    obj.put("type",type);
                    if(0==iorder){
                        list0.add(obj);
                    }else{
                        list1.add(obj);
                    }
                }

                JSONArray cate = new JSONArray();
                JSONArray entertainment = new JSONArray();
                JSONArray trade = new JSONArray();
                int cateSize = 4;
                int entertainmentSize = 2;
                int tradeSize = 2;

                cateSize  = radomMerchant(list1,"1",cate,cateSize,icityid);
                cateSize  = radomMerchant(list0,"1",cate,cateSize,icityid);

                entertainmentSize  = radomMerchant(list1,"2",entertainment,entertainmentSize,icityid);
                entertainmentSize  = radomMerchant(list0,"2",entertainment,entertainmentSize,icityid);

                tradeSize  = radomMerchant(list1,"3",trade,tradeSize,icityid);
                tradeSize  = radomMerchant(list0,"3",trade,tradeSize,icityid);

                data.put("cate",cate);
                data.put("entertainment",entertainment);
                data.put("trade",trade);
            }

            /*
            优惠券全国
             */
            //  jrs =  JdbcSqlMapping.executeQuery("query_index_shop", bean, null, conn);
            List<ShopIndexBean> shopIndexBeen =userService.query_index_shop();
            if (shopIndexBeen != null ) {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < shopIndexBeen.size(); i ++) {
                    JSONObject obj = new JSONObject();
                    obj.put("shopid",shopIndexBeen.get(i).getShopid());
                    obj.put("shopname",shopIndexBeen.get(i).getShopname());
                    obj.put("shoplogo",shopIndexBeen.get(i).getShopid());//新版优惠劵取列表logo
                    obj.put("shopslogan",shopIndexBeen.get(i).getShopslogan());
                    jsonArray.add(obj);
                    if(jsonArray.size()==3){
                        break;
                    }
                }
                data.put("saleShop",jsonArray);

            }
            /*
            超市根据城市
             */
            data.put("market",getMarket(icityid));
            bean.setBusiErrCode(1);

            jsonObject.put("code", bean.getBusiErrCode());
            jsonObject.put("desc", bean.getBusiErrDesc());
            jsonObject.put("data", data);

            return  jsonObject;



        }catch (Exception e){
            e.printStackTrace();
            logger.error("享优惠查询失败",e);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询失败");
            jsonObject.put("code", bean.getBusiErrCode());
            jsonObject.put("desc", bean.getBusiErrDesc());


        }

        return  jsonObject;
    }
    Random random = new Random();
    private int radomMerchant(List<JSONObject> list, String s, List resultList, int cateSize,String icityid) {
        List<JSONObject> subList = new ArrayList<>();
        for(JSONObject obj : list){
            String type = obj.getString("type");
            if(s.equals(type)){
                subList.add(obj);
            }
        }
        while(subList.size()!=0 && cateSize!=0){
            int i = random.nextInt(subList.size());
            JSONObject obj = subList.get(i);
            subList.remove(i);
            JSONArray activity = getActivity(obj.getString("shopid"),icityid);
            if(activity.size()!=0){
                obj.put("activity",activity);
                resultList.add(obj);
                cateSize--;
            }

        }
        return cateSize;
    }
    private JSONArray getActivity(String ibussinessid,String icityid){
        JSONArray array = new JSONArray();
        List<CheapTitleBean> cheapTitleBeen = userService.query_cheap_ctitle(ibussinessid,icityid);
        Set<String> set = new HashSet<>();
        if (cheapTitleBeen != null && cheapTitleBeen.size() > 0) {
            for (int i = 0; i <cheapTitleBeen.size(); i ++){
                String ctitle = cheapTitleBeen.get(i).getCtitle();
                if(!set.contains(ctitle)){
                    JSONObject obj = new JSONObject();
                    obj.put("cptype",cheapTitleBeen.get(i).getCptype());
                    obj.put("ctitle",ctitle);
                    array.add(obj);
                    set.add(ctitle);
                }

            }
        }
        return array;
    }

    private JSONArray getMarket(String icityid){
        JSONArray array = new JSONArray();


        List<MarketBean> jrs = userService.query_market(icityid);
        Set<String> imarketids = new HashSet<>(3);
        if (jrs != null && jrs.size() > 0) {
            for(int i = 0; i < jrs.size(); i++){

                String imarketid = jrs.get(i).getImarketid().toString();
                if(!imarketids.contains(imarketid)){
                    JSONObject obj = new JSONObject();
                    imarketids.add(imarketid);
                    obj.put("shopid",imarketid);
                    obj.put("shopname",jrs.get(i).getCname());
                    obj.put("shoplogo",jrs.get(i).getClogolist());//新版超市图标取列表logo
                    obj.put("shopslogan",jrs.get(i).getCtitle());
                    array.add(obj);

                }
                if(imarketids.size()==3){
                    break;
                }
            }
            jrs.clear();
        }
        logger.info("超市"+array.toJSONString());
        return array;
    }
    @BoltController
    public JSONObject specialPreferential(HomePageBean bean){
        JSONObject result = new JSONObject();
        result.put("code",0);
        result.put("desc","查询失败");
        JSONObject data = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            String adcode = bean.getAdcode();
            String hskcityid = bean.getHskcityid();
            adcode = homePageService.getAdcode(adcode,hskcityid);
            if (StringUtil.isEmpty(adcode)) {
                result.put("code",0);
                result.put("desc","查询失败");
                return  result;
            }
//            logger.info(bean.getIbankids());
//            logger.info(adcode);
            String[] bankids=null;
            if(StringUtils.isNotEmpty(bean.getIbankids())){
                bankids=bean.getIbankids().split(",");
            }
            bean.setPageNum(bean.getPn());
            bean.setPageSize(bean.getPs());
            bean.setAdcode(adcode);
            bean.setBankids(Arrays.asList(bankids));
            Page<SpecialPreferentialBean> jrs =  userService.qurey_special_preferential(bean);
            result.put("data",jrs);
            result.put("pages",jrs.getPages());
            result.put("code",1);
            result.put("desc","查询成功");
            return  result;

        }catch (Exception e){
            e.printStackTrace();
            logger.error("享优惠查询失败",e);
            result.put("code",0);
            result.put("desc","查询异常");
        }
        return  result;
    }


    @BoltController
    public BoltResult serviceBanner(HomePageBean bean){
        bean.setHomePageType("SERVICE_BANNER");
        JSONArray array = homePageService.selectHomePage(bean);
        return new BoltResult(BoltResult.SUCCESS,"查询成功",array);
    }
    @BoltController
    public BoltResult   linesPromotion(HomePageBean bean){
        bean.setHomePageType("LINES_PROMOTION");
        bean.setPageNum(1);
        bean.setPageSize(1);
        JSONArray array = homePageService.selectHomePage(bean);
        return new BoltResult(BoltResult.SUCCESS,"查询成功",array);
    }

}
