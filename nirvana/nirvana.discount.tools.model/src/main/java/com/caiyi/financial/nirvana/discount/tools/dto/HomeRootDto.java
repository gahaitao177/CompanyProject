package com.caiyi.financial.nirvana.discount.tools.dto;

import java.util.List;

/**
 * Created by heshaohua on 2016/9/18.
 */
public class HomeRootDto {

    private String saleShopCount;
    private String marketCount;

    //连锁店信息
    private List<HomeMarketDto> SaleShop;
    //超市信息
    private List<HomeMarketDto> Market;
    //关注信息
    private List<HomeMarketDto> RecommendShop;
    //消息
    private List<HomeMarketDto> HotNews;


    public String getSaleShopCount() {
        return saleShopCount;
    }

    public void setSaleShopCount(String saleShopCount) {
        this.saleShopCount = saleShopCount;
    }

    public String getMarketCount() {
        return marketCount;
    }

    public void setMarketCount(String marketCount) {
        this.marketCount = marketCount;
    }

    public List<HomeMarketDto> getSaleShop() {
        return SaleShop;
    }

    public void setSaleShop(List<HomeMarketDto> saleShop) {
        SaleShop = saleShop;
    }

    public List<HomeMarketDto> getMarket() {
        return Market;
    }

    public void setMarket(List<HomeMarketDto> market) {
        Market = market;
    }

    public List<HomeMarketDto> getRecommendShop() {
        return RecommendShop;
    }

    public void setRecommendShop(List<HomeMarketDto> recommendShop) {
        RecommendShop = recommendShop;
    }

    public List<HomeMarketDto> getHotNews() {
        return HotNews;
    }

    public void setHotNews(List<HomeMarketDto> hotNews) {
        HotNews = hotNews;
    }
}
