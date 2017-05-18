package com.caiyi.financial.nirvana.discount.tools.mapper;

import com.caiyi.financial.nirvana.discount.tools.dto.HomeMarketDto;
import com.caiyi.financial.nirvana.discount.tools.dto.MarketDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by heshaohua on 2016/8/9.
 */
public interface MarketsMapper {
    @Select("select count(*) as num from tb_market where itype=1 and istate = 1")
    int queryShopCount();

    List<MarketDto> queryShops(String cnum);

    @Select("select count(*) as num from tb_market_cheap where istate=1 and cenddate>sysdate-1")
    int queryCouponCount();

    List<MarketDto> queryCoupons(@Param("userid")String userid,@Param("imarketid") String imarketid, @Param("icheapid")String icheapid);

    List<MarketDto> queryMarkets(@Param("icityid")String icityid);

    List<MarketDto> queryMarketCheaps(@Param("icityid")String icityid,@Param("imarketid")String imarketid, @Param("size")String size);

    @Select("select * from tb_market where imarketid =#{imarketid, jdbcType=VARCHAR} and itype=2")
    MarketDto queryMarketById(String imarketid);

    @Select("select * from tb_market_cheap_img where icheapid=#{icheapid, jdbcType=VARCHAR} order by to_number(nvl(regexp_replace(substr(cimgurl,INSTR(cimgurl, '/', -1, 1)+1,2),'[^0-9]'),'0'))")
    List<MarketDto> queryMarketCheapImages(@Param("icheapid")String icheapid);

    //首页三个连锁店列表查询
    @Select("select * from ( select t1.imarketid as ShopId, t1.cname as ShopName, t1.cslogan as ShopSlogan, t1.clogo as ShopPic,t1.clogolist clogolist, nvl(t2.mcount, 0) num from TB_MARKET t1 right join (select imarketid, count(icheapid) mcount from tb_market_cheap where cenddate > sysdate - 1 and istate = 1 group by imarketid order by mcount desc) t2 on t1.imarketid = t2.imarketid where itype = 1 and istate = 1 order by t1.iorder desc, t2.mcount desc ) where rownum <= 3")
    List<HomeMarketDto> queryHomeMarket();

    //查询超市总数
    @Select("select count(*) as count from (select * from tb_market where itype = 2 and istate = 1 order by iorder desc) t1, (select distinct t2.imarketid from (select icheapid from tb_market_cheap where istate = 1) t1, tb_market_cheap_city t2 where t1.icheapid = t2.icheapid and (icityid = #{icityid, jdbcType=VARCHAR} or icityid = -1)) t2 where t1.imarketid = t2.imarketid")
    int queryHomeMarketCount(@Param("icityid")String icityid);

    //查询两个超市id
    @Select("select t3.imarketid from (select t1.imarketid,t1.icheapid,t1.ceditdate from tb_market_cheap t1 left join tb_market t2 on t1.imarketid = t2.imarketid where t2.itype = 2 and t2.istate = 1 and t1.istate = 1) t3 left join tb_market_cheap_city t4 on t3.icheapid = t4.icheapid where (t4.icityid = #{icityid, jdbcType=VARCHAR} or t4.icityid=-1) order by t3.ceditdate desc")
    List<String> queryHomeMarketId(@Param("icityid")String icityid);

    //首页两个超市列表
    @Select("select t2.imarketid as MarketId, t2.cname as MarketName, t1.ctitle as MarketSlogan, t2.clogo as MarketPic from (select t.imarketid, t.ctitle from tb_market_cheap t left join tb_market_cheap_city t2 on t.icheapid = t2.icheapid where t.imarketid =#{imarketid, jdbcType=VARCHAR} and t.istate = 1 and (t2.icityid = #{icityid, jdbcType=VARCHAR} or t2.icityid = -1) order by t.ceditdate desc) t1 left join tb_market t2 on t1.imarketid = t2.imarketid where rownum <=1")
    HomeMarketDto queryHomeMarket2(@Param("icityid")String icityid, @Param("imarketid")String imarketid);

    //首页推荐商户列表
    List<HomeMarketDto> queryHomeMarketList(@Param("icityid")String icityid);
    //首页推荐关注的商户列表
    List<HomeMarketDto> queryHomeFollowMarketList(@Param("icityid")String icityid, @Param("ibankids")String ibankids);

    @Select("select t.ctitle as NewsTitle,t.ccontent as NewsDesc,t.icategory as NewsType,t.csummary as NewsValue from tb_contact t where t.itype=3 and t.iactive=0")
    List<HomeMarketDto> queryHomeNews();

}
