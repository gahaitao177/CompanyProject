package com.caiyi.financial.nirvana.discount.rest.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.util.JsonUtil;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.CheapDto;
import com.caiyi.financial.nirvana.discount.user.dto.CollectionDto;
import com.caiyi.financial.nirvana.discount.user.dto.MarketCheapDto;
import com.caiyi.financial.nirvana.discount.utils.XmlUtils;
import com.danga.MemCached.MemCachedClient;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 重构收藏 。收藏修改添加，用户信息。 RestCouponController的方法重构
 * <p/>
 * 用户信息接口放于此
 * Created by wenshiliang on 2016/9/7.
 */
@RestController
public class RestUserInfoController {
    private static Logger logger = LoggerFactory.getLogger(RestUserInfoController.class);
    @Resource(name = Constant.HSK_USER)
    IDrpcClient client;
    @Autowired
    private MemCachedClient cc;


    private ExecutorService executorService = Executors.newCachedThreadPool();


    /**
     * 个人中心数据同步 重构
     * @param user
     * @param response
     * Created by wenshiliang on 2016/9/7.
     */
    @RequestMapping("/user/dataMerge.go")
    public void dataMerge(User user,HttpServletResponse response){
        BoltResult result = client.execute(new DrpcRequest("CollectionBolt","saveCollection",user),BoltResult.class);
        Element resp = null;
        if(result.isSuccess()){
            //同步完成，查询
            CollectionDto collectionDto = qCollectInfo(user);
            resp = XmlUtils.jsonParseXml((JSONObject) JSONObject.toJSON(collectionDto), "Resp");
        }else{
            resp = new DOMElement("Resp");
        }
        resp.addAttribute("code", result.getCode());
        resp.addAttribute("desc", result.getDesc());
        Document dom = new DOMDocument();
        dom.add(resp);
        XmlUtils.writeXml(dom, response);
    }

    /**
     * 收藏接口 收藏
     * @param user
     * @param response
     */
    @RequestMapping("/user/cheapCollectAdd.go")
    public void cheapCollectAdd(User user,HttpServletResponse response){
        String type = user.getCollectType();
        String storeId = user.getStoreId();
        String cuserId = user.getCuserId();
        Map<String,Object> map = new HashMap<>();
        map.put("cuserId",cuserId);
        if("0".equals(type)){
            //门店
            map.put("storeId",storeId);
        }else if ("1".equals(type)){
            //优惠劵
            map.put("coupon",storeId);
        }else{
            XmlUtils.writeXml("-1","不存在的类型",response);
            return;
        }

        BoltResult result = client.execute(new DrpcRequest("CollectionBolt","saveCollection",map),BoltResult.class);
        XmlUtils.writeXml(result.getCode(),"收藏成功",response);

    }








    private CollectionDto qCollectInfo(User user){
        long start = System.currentTimeMillis();
        long time = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        Future<String> cheapDtoFuture =  executorService.submit(()-> client.execute(new DrpcRequest("CollectionBolt", "selectCheapDto", user)));
        Future<String> marketCheapDtoFuture =  executorService.submit(()-> client.execute(new DrpcRequest("CollectionBolt", "selectMarketCheapDto", user)));
        Future<String> bankFuture =  executorService.submit(()-> client.execute(new DrpcRequest("CollectionBolt", "queryUserbankId", user)));
//        String cheapDtoStr =
//        String marketCheapDtoStr = client.execute(new DrpcRequest("CollectionBolt", "selectMarketCheapDto", user));
//        String bankStr = client.execute(new DrpcRequest("CollectionBolt", "queryUserbankId", user));
        CollectionDto collectionDto = new CollectionDto();
        CollectionDto.Count collectionDtoCount = new CollectionDto.Count();
        collectionDto.setCount(collectionDtoCount);
        int expire = 0;
        try {
            List<Integer> idList = JsonUtil.parseArray(bankFuture.get(time,timeUnit), Integer.class);
            List<CollectionDto.Bank> bankList = new ArrayList<>();
            idList.forEach(bankid -> {
                CollectionDto.Bank bank = new CollectionDto.Bank();
                bank.setIbankid(bankid);
            });
            collectionDto.setBank(bankList);
            collectionDtoCount.setBanks(bankList.size());
        } catch (Exception e) {
            logger.error("关注银行查询失败", e);
        }
        try {
            List<CheapDto> cheapDtoList = JsonUtil.parseArray(cheapDtoFuture.get(time,timeUnit), CheapDto.class);
            Collections.sort(cheapDtoList, (c1, c2) -> {
                if (c1.getIstoreid() < c2.getIstoreid()) {
                    return 1;
                }
                return -1;
            });

            List<CollectionDto.CheapDto> collectionCheapDtoList = new ArrayList<>();
            Long istoreid = null;
            StringBuilder bankids = null;
            StringBuilder cheaptitle = null;
            StringBuilder ccheaptype = null;
            String iexpire = "0";
            String cname = null;
            String clogo = null;
            CollectionDto.CheapDto collectionCheapDto = null;

            for (CheapDto cheapDto : cheapDtoList) {
                if (!"1".equals(cheapDto.getIexpire())) {
                    if (cheapDto.getIstoreid().equals(istoreid)) {
                        if (bankids.indexOf(cheapDto.getIshortname()) < 0) {
                            bankids.append(" ").append(cheapDto.getIshortname());
                        }
                        cheaptitle.append("@").append(cheapDto.getIshortname()).append("：").append(cheapDto.getCtitle());
                        if (ccheaptype.indexOf(cheapDto.getCptype()) < 0) {
                            ccheaptype.append("_").append(cheapDto.getCptype());
                        }
                    } else {
                        if (istoreid != null) {
                            collectionCheapDto = new CollectionDto.CheapDto();
                            collectionCheapDto.setIstoreid(istoreid);
                            collectionCheapDto.setCname(cname);
                            collectionCheapDto.setBankids(bankids.toString());
                            collectionCheapDto.setCheaptitle(cheaptitle.toString());
                            collectionCheapDto.setCcheaptype(ccheaptype.toString());
                            collectionCheapDto.setIexpire(iexpire);
                            collectionCheapDto.setClogo(clogo);
                            collectionCheapDtoList.add(collectionCheapDto);
                        }
                        istoreid = cheapDto.getIstoreid();
                        cheapDto.getClogo();
                        bankids = new StringBuilder(cheapDto.getIshortname());
                        cheaptitle = new StringBuilder().append(cheapDto.getIshortname()).append("：").append(cheapDto.getCtitle());
                        //交行：满158赠柠檬茶@兴业：9.5折
                        ccheaptype = new StringBuilder(cheapDto.getCptype());
                        cname = cheapDto.getCstorename();
                        clogo = cheapDto.getClogo();
                    }
                }
            }
            collectionDto.setRow(collectionCheapDtoList);
            collectionDtoCount.setStores(collectionCheapDtoList.size());
        } catch (Exception e) {
            logger.error("门店优惠查询失败", e);
        }

        try {
            List<MarketCheapDto> marketCheapDtoList = JsonUtil.parseArray(marketCheapDtoFuture.get(time,timeUnit), MarketCheapDto.class);
            Collections.sort(marketCheapDtoList, (c1, c2) -> {
                if (c1.getImarketid() < c2.getImarketid()) {
                    return 1;
                } else if (c1.getImarketid() > c2.getImarketid()) {
                    return -1;
                } else if (c1.getCadddate().getTime() < c2.getCadddate().getTime()) {
                    return 1;
                }
                return -1;
            });
//            marketCheapDtoList.forEach(m -> {
//                logger.info(m.getImarketid() + "---" + m.getCadddate());
//            });
            collectionDtoCount.setCoupons(marketCheapDtoList.size());

            List<CollectionDto.MarketDto> marketDtoList = new ArrayList<>();
            Long marketId = null;
            CollectionDto.MarketDto marketDto = null;
            List<MarketCheapDto> sonList = null;
            int count = 0;
            for (MarketCheapDto marketCheapDto : marketCheapDtoList) {
                if (marketCheapDto.getCenddate().getTime() < start) {
                    expire++;
                    marketCheapDto.setIexpire(1);
                }
                if (marketId != null && marketCheapDto.getImarketid().equals(marketId)) {
                    count++;
                    sonList.add(marketCheapDto);
                } else {
                    if (marketDto != null) {
                        marketDto.setCount(count);
                    }
                    marketId = marketCheapDto.getImarketid();
                    marketDto = new CollectionDto.MarketDto();
                    marketDto.setId(marketId);
                    marketDto.setClogo(marketCheapDto.getClogo());
                    marketDto.setCname(marketCheapDto.getCname());
                    count = 1;
                    sonList = new ArrayList<>();
                    sonList.add(marketCheapDto);
                    marketDto.setCheap(sonList);
                    marketDtoList.add(marketDto);
                }
                marketCheapDto.setClogo(null);
            }
            Collections.sort(marketDtoList,(m1,m2)->{
                long time1 = m1.getCheap().get(0).getCadddate().getTime();
                long time2 = m2.getCheap().get(0).getCadddate().getTime();
                if(time1>time2){
                    return -1;
                }else{
                    return 1;
                }
            });
            collectionDto.setCoupon(marketDtoList);
        } catch (Exception e) {
            logger.error("优惠劵查询失败", e);
        }
        collectionDtoCount.setExpire(expire);//过期总数

        return collectionDto;
    }

    /**
     * 重构收藏接口
     * 仔细查看老接口。分为三块，关注银行，收藏门店优惠，收藏优惠劵，统计。
     * 优化：分别请求，然后在controller聚合组织返回数据
     *
     * @param user
     * @param response
     */
    @RequestMapping("/user/qCollectInfo.go")
    public void qCollectInfo(User user, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        CollectionDto collectionDto = qCollectInfo(user);
        Element resp = XmlUtils.jsonParseXml((JSONObject) JSONObject.toJSON(collectionDto), "Resp");
        resp.addAttribute("code", "1");
        resp.addAttribute("desc", "查询成功");
        Document dom = new DOMDocument();
        dom.add(resp);
        XmlUtils.writeXml(dom, response);
        long end = System.currentTimeMillis();
        logger.info("qCollectInfo查询共用时：{}", end - start);
    }

    @RequestMapping("/user/cheapCollectDel.go")
    public void cheapCollectDel(User user, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(user.getStoreId())){
            XmlUtils.writeXml(-1,"请选择收藏",response);
            return;
        }
        BoltResult result = client.execute(new DrpcRequest("CollectionBolt","cheapCollectDel",user),BoltResult.class);
        XmlUtils.writeXml(result.getCode(),result.getDesc(),response);
    }


}
