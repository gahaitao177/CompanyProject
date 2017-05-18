package com.caiyi.financial.nirvana.discount.ccard.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.ccard.dto.BankCommodityDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.CommodityDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by A-0106 on 2016/5/31.
 */
public interface CommodityMapper extends BaseDao {
    @Select("select t1.ibankid,t1.icard4num,t1.ipoint,t2.cbankname  from tb_bank_bill t1," +
            "tb_bank t2 where t1.ibankid=t2.ibankid and  t1.isdel=0 and t1.cuserid=#{cuserid,jdbcType=VARCHAR}")
    List<CommodityDto> queryPointsByUserId(@Param("cuserid") String cuserid);

    @Select(" select t4.* from (select t3.*,t2.cbankname from tb_bank_jf_commodity t3 ,tb_bank t2   where  t3.ibankid= t2.ibankid " +
            "and t3.istate=0 and t3.ibankid= #{ibankid,jdbcType=VARCHAR} and t3.cminscore < " +
            "#{cminscore,jdbcType=VARCHAR}  order by t3.cminscore desc," +
            "to_number(regexp_replace(nvl(t3.coriginprice,'0'), '[^0-9.]')) asc,t3.cmaxscore ) t4 where rownum <= #{limit,jdbcType=INTEGER} ")
    List<BankCommodityDto> queryBankCommodity(@Param("ibankid") String  ibankid,@Param("cminscore") String cminscore,@Param("limit") Integer limit);

    //&lt;
    @Select("select ibankid from tb_user_bank where cnickid=#{userId,jdbcType=VARCHAR}")
    List<String> queryBankIdByUser(@Param("userId") String userId);

    List<BankCommodityDto> query_index_commodity(Map<String,String> map);

    @Select("select t1.*,cbankname from (select ibankid,icateid,ccategory from" +
            " TB_BANK_JF_CATEGORY) t1,tb_bank t2 where t1.ibankid = t2.ibankid(+) " +
            "order by t2.iorder,t2.ibankid")
    List<CommodityDto> queryPointsAndBanks();
//    @Param("ibankid") String  ibankid,@Param("ccategory") String ccategory,
//    @Param("start") Integer start,@Param("size") Integer size
    List<BankCommodityDto> query_jf_commodity(Map<String,String> map);

//    @Select("select * from TB_BANK_JF_COMMODITY where icommid=#{icommid,jdbcType=VARCHAR}")
    @Select("select jf.*,b.cbankname from TB_BANK_JF_COMMODITY jf,tb_bank b  " +
            "where jf.ibankid=b.ibankid and  icommid=#{icommid,jdbcType=VARCHAR}")
    BankCommodityDto queryCommodityDetail(@Param("icommid") String icommid);

    int count_jf_commodity(@Param("ibankid") String  ibankid,@Param("ccategory") String ccategory);


    @Select("select cpicurl from TB_BANK_JF_COMMODITY_IMG where icommid=#{icommid,jdbcType=VARCHAR}")
    List<String> queryCommodityImgUrl(@Param("icommid") String icommid);
}