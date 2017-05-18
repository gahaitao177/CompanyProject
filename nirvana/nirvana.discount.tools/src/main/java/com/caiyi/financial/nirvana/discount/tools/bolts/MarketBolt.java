package com.caiyi.financial.nirvana.discount.tools.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.tools.bean.MarketBean;
import com.caiyi.financial.nirvana.discount.tools.dto.HomeMarketDto;
import com.caiyi.financial.nirvana.discount.tools.dto.HomeRootDto;
import com.caiyi.financial.nirvana.discount.tools.dto.MarketDto;
import com.caiyi.financial.nirvana.discount.tools.dto.UniteMarketDto;
import com.caiyi.financial.nirvana.discount.tools.service.MarketService;
import org.apache.storm.task.TopologyContext;

import java.util.List;
import java.util.Map;

/**
 * Created by heshaohua on 2016/8/24.
 */
@Bolt(boltId = "MarketBolt", parallelismHint = 1, numTasks = 1)
public class MarketBolt extends BaseBolt {

    MarketService marketService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        marketService = getBean(MarketService.class);
    }

    @BoltController
    public List<MarketDto> getShopList(MarketBean bean){
        logger.info("---------------------MarketBolt getShopList");
        try {
            List<MarketDto> marketDtoList = marketService.getShopList(bean);
            return marketDtoList;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketBolt异常--getShopList",e);
            return null;
        }
    }

    @BoltController
    public List<MarketDto> getCouponList(MarketBean bean){
        logger.info("---------------------MarketBolt getCouponList");
        try {
            List<MarketDto> marketDtoList = marketService.getCouponList(bean);
            return marketDtoList;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketBolt异常--getCouponList",e);
            return null;
        }
    }

    @BoltController
    public List<MarketDto> getMarketList(MarketBean bean){
        logger.info("---------------------MarketBolt getMarketList");
        try {
            List<MarketDto> marketDtoList = marketService.getMarketList(bean);
            return marketDtoList;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketBolt异常--getMarketList",e);
            return null;
        }
    }

    @BoltController
    public UniteMarketDto getMarketCheap(MarketBean bean){
        logger.info("---------------------MarketBolt getMarketCheap");
        try {
            UniteMarketDto uniteMarketDto = marketService.getMarketCheap(bean);
            return uniteMarketDto;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketBolt异常--getMarketCheap",e);
            return null;
        }
    }

    @BoltController
    public HomeRootDto getHomeShopAndMarketList(MarketBean bean){
        logger.info("---------------------MarketBolt getIndexShopAndMarketList");

        //连锁店总数
        int shopNum  = marketService.getHomeMarketNum(bean);
        //超市总数
        int superMaketNum = marketService.getHomeSuperMaketNum(bean);

        //查询三个连锁店信息
        List<HomeMarketDto> homeMarketDtoList = marketService.getHomeMarketShop(bean);
        logger.info("--------------homeMarketDtoList---"+homeMarketDtoList.size());
        //查询超市信息
        List<HomeMarketDto> homeSuperMarketDtoList = marketService.getHomeSuperMarket(bean);
        logger.info("--------------homeSuperMarketDtoList---"+homeSuperMarketDtoList.size());
        //查询随机排序的商户
        List<HomeMarketDto> homeRecommendStoreDtoList = marketService.getHomeRecommendStore(bean);
        logger.info("--------------homeRecommendStoreDtoList---"+homeRecommendStoreDtoList.size());
        //查询置顶排序商户
        List<HomeMarketDto> homeRecommendStoreZdDtoList = marketService.getHomeRecommendStoreZd(bean);
        logger.info("--------------homeRecommendStoreZdDtoList---" + homeRecommendStoreZdDtoList.size());
        //查询头条信息
        List<HomeMarketDto> homeHeadNewsList = marketService.getHomeHeadNews(bean);
        logger.info("--------------homeHeadNewsList---" + homeHeadNewsList.size());

        if (homeRecommendStoreZdDtoList != null && homeRecommendStoreZdDtoList.size() > 0) {
            homeRecommendStoreDtoList.addAll(homeRecommendStoreZdDtoList);
        }

        HomeRootDto homeRootDto = new HomeRootDto();

        homeRootDto.setSaleShopCount(shopNum+"");
        homeRootDto.setMarketCount(superMaketNum + "");
        homeRootDto.setSaleShop(homeMarketDtoList);
        homeRootDto.setMarket(homeSuperMarketDtoList);
        homeRootDto.setRecommendShop(homeRecommendStoreDtoList);
        homeRootDto.setHotNews(homeHeadNewsList);


        logger.info("--------------homeMarketDtoList---"+homeMarketDtoList.size());
        return homeRootDto;
    }
}
