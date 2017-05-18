package com.caiyi.financial.nirvana.discount.tools.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.caiyi.financial.nirvana.discount.tools.bean.MarketBean;
import com.caiyi.financial.nirvana.discount.tools.dto.HomeMarketDto;
import com.caiyi.financial.nirvana.discount.tools.dto.MarketDto;
import com.caiyi.financial.nirvana.discount.tools.dto.UniteMarketDto;
import com.caiyi.financial.nirvana.discount.tools.mapper.MarketsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaohua on 2016/8/23.
 */
@Service
public class MarketService extends AbstractService {
    @Autowired
    MarketsMapper marketMapper;

    /**
     * 连锁店列表查询
     * @param bean
     */
    public List<MarketDto> getShopList(MarketBean bean){
        try{
            Integer cou = marketMapper.queryShopCount();

            logger.info("查询到连锁店数量num=" + cou);

            if(cou<1){
                return null;
            }
            return marketMapper.queryShops(bean.getCnum());
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getShopList",e);
            return null;
        }
    }

    /**
     * 商家优惠券列表查询
     * @param bean
     */
    public List<MarketDto> getCouponList(MarketBean bean){
        try{
            Integer cou = marketMapper.queryCouponCount();

            logger.info("查询到连锁店数量num=" + cou);

            if(cou<1){
                return null;
            }
            return marketMapper.queryCoupons(bean.getCuserId(), bean.getImarketid(), bean.getIcheapid());

        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getCouponList",e);
            return null;
        }
    }

    /**
     * 超市列表查询
     * @param bean
     */
    public List<MarketDto> getMarketList(MarketBean bean){
        try{
            List<MarketDto> marketDtoList = marketMapper.queryMarkets(bean.getIcityid() + "");
            logger.info("==========marketDtoList.size():"+marketDtoList.size());
            if(marketDtoList != null && marketDtoList.size() > 0){
                for (MarketDto marketDto : marketDtoList){
                    List<MarketDto> dtoList = null;
                    dtoList = marketMapper.queryMarketCheaps(bean.getIcityid()+"", marketDto.getImarketid(),"2");
                    if(dtoList != null && dtoList.size() > 0){
                        marketDto.setCtitle(dtoList.get(0).getCtitle());
                        marketDto.setCstartdate(dtoList.get(0).getCstartdate());
                        marketDto.setCendtdate(dtoList.get(0).getCenddate());
//                        for(MarketDto dto :  dtoList){
//                            marketDto.setCtitle(dto.getCtitle());
//                            marketDto.setCstartdate(dto.getCstartdate());
//                            marketDto.setCenddate(dto.getCenddate());
//                        }
                    }
                }
            }
            return marketDtoList;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getMarketList",e);
        }
        return null;
    }

    /**
     * 超市优惠查询
     * @param bean
     */
    public UniteMarketDto getMarketCheap(MarketBean bean){
        UniteMarketDto uniteMarketDto = null;
        try{
            MarketDto marketDto = marketMapper.queryMarketById(bean.getImarketid());

            List<MarketDto> marketDtoList = marketMapper.queryMarketCheaps(bean.getIcityid()+"",bean.getImarketid(),"3");
            if(marketDtoList != null && marketDtoList.size() > 0){
                for(MarketDto Dto : marketDtoList){
                    Dto.setCendtdate(Dto.getCenddate());
                    List<MarketDto> cheapImageList = marketMapper.queryMarketCheapImages(Dto.getIcheapid());
                    Dto.setImg(cheapImageList);
                }
            }
            if(marketDto != null){
                marketDto.setCendtdate(marketDto.getCenddate());
                uniteMarketDto = new UniteMarketDto(marketDto, marketDtoList);
            }
            return uniteMarketDto;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getMarketCheap",e);
        }
        return null;
    }


    /**
     * 查询连锁店条数
     * @param bean
     * @return
     */
    public Integer getHomeMarketNum(MarketBean bean){
        return marketMapper.queryShopCount();
    }

    /**
     * 超市总数
     * @param bean
     * @return
     */
    public Integer getHomeSuperMaketNum(MarketBean bean){
        return marketMapper.queryHomeMarketCount(bean.getIcityid()+"");
    }

    /**
     * 查询三个连锁店信息
     * @param bean
     * @return
     */
    public List<HomeMarketDto> getHomeMarketShop(MarketBean bean){
        List<HomeMarketDto> homeMarketDtoList = null;
        try{
            homeMarketDtoList = marketMapper.queryHomeMarket();
            return homeMarketDtoList;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getHomeMarketShop",e);
        }
        return null;
    }

    /**
     * 查询超市信息
     * @param bean
     * @return
     */
    public List<HomeMarketDto> getHomeSuperMarket(MarketBean bean){
        List<HomeMarketDto> homeMarketDtoList = null;
        HomeMarketDto homeMarketDto= null;
        try{

            //获取优惠最多的两个超市id
            List<String> marketIdList = marketMapper.queryHomeMarketId(bean.getIcityid() + "");

            if(marketIdList != null && marketIdList.size() > 0){
                int i = 0;
                for(String marketId : marketIdList){
                    homeMarketDto =  marketMapper.queryHomeMarket2(bean.getIcityid() + "", marketId);
                    if(homeMarketDtoList == null){
                        homeMarketDtoList = new ArrayList<HomeMarketDto>();
                        homeMarketDtoList.add(homeMarketDto);
                    }else{
                        if(!homeMarketDtoList.contains(homeMarketDto)){
                            homeMarketDtoList.add(homeMarketDto);
                        }
                    }
                    i++;
                    if( i == 2){
                        break;
                    }
                }
            }

            return homeMarketDtoList;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getHomeSuperMarket",e);
        }
        return null;
    }

    /**
     * 查询随机排序的商户
     * @param bean
     * @return
     */
    public List<HomeMarketDto> getHomeRecommendStore(MarketBean bean){
        List<HomeMarketDto> homeMarketDtoList = null;
        try{

            //未关注银行的商户
            return marketMapper.queryHomeMarketList(bean.getIcityid()+"");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getHomeSuperMarket",e);
        }
        return null;
    }

    /**
     * 查询置顶的商户
     * @param bean
     * @return
     */
    public List<HomeMarketDto> getHomeRecommendStoreZd(MarketBean bean){
        List<HomeMarketDto> homeMarketDtoList = null;
        try{
            //根据城市和关注银行筛选
            String[] ibankidArr = {};
            String ibankids = bean.getIbankid();
            if(!StringUtils.isEmpty(ibankids)){
                ibankidArr = ibankids.split("#");
            }

            //推荐商户列表 最多4个
            StringBuilder sqlwhere = new StringBuilder();
            for (int j = 0; j < ibankidArr.length; j++) {
                if (j==0){
                    sqlwhere.append("and (");
                }else{
                    sqlwhere.append(" or ");
                }
                sqlwhere.append("c.cbankid ="+ibankidArr[j]);
                if (j==ibankidArr.length-1){
                    sqlwhere.append(")");
                }
            }
            String ibas = sqlwhere.toString();

            //关注银行的商户
            homeMarketDtoList = marketMapper.queryHomeFollowMarketList(bean.getIcityid() + "", ibas);

            return homeMarketDtoList;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getHomeRecommendStoreZd",e);
        }
        return null;
    }


    /**
     * 查询头条信息
     * @param bean
     * @return
     */
    public List<HomeMarketDto> getHomeHeadNews(MarketBean bean){
        List<HomeMarketDto> homeMarketDtoList = null;
        try{

            return marketMapper.queryHomeNews();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MarketService异常--getHomeHeadNews",e);
        }
        return null;
    }
}
