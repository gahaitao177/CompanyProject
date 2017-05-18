package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.BankJfCommodity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dengh on 2016/8/1.
 */
public interface BankJfCommodityMapper  extends BaseDao {
    //query_bank_commodity_byjf
    @Select("select * from tb_bank_jf_commodity   where istate=0 and " +
            "ibankid= #{ibankid, jdbcType=VARCHAR} and" +
            " cminscore < #{cminscore, jdbcType=VARCHAR}" +
            " order by cminscore desc,to_number(regexp_replace(nvl(coriginprice,'0'), '[^0-9.]')) asc,cmaxscore ")
    List<BankJfCommodity>query_bank_commodity_byjf(@Param("ibankid")String ibankid,@Param("cminscore")String cminscore);

    @Select("select * from TB_BANK_JF_COMMODITY where 1=1 and istate=0  ${sqlwhere}")
    List<BankJfCommodity> query_index_commodity(@Param("sqlwhere")String sqlwhere);

}
