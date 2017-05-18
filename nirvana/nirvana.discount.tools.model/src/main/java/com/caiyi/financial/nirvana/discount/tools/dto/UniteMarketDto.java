package com.caiyi.financial.nirvana.discount.tools.dto;

import java.util.List;

/**
 * Created by heshaohua on 2016/8/25.
 */
public class UniteMarketDto {
    public UniteMarketDto(){};
    public UniteMarketDto(MarketDto marketDto, List<MarketDto> listMarketDto){
        this.MarketItem = marketDto;
        this.Cheap = listMarketDto;
    };

    private MarketDto MarketItem;
    private List<MarketDto> Cheap;

    public MarketDto getMarketItem() {
        return MarketItem;
    }

    public void setMarketItem(MarketDto marketItem) {
        MarketItem = marketItem;
    }

    public List<MarketDto> getCheap() {
        return Cheap;
    }

    public void setCheap(List<MarketDto> cheap) {
        Cheap = cheap;
    }
}
