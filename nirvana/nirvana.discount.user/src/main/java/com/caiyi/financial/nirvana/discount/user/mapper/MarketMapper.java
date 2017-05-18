package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.MarketBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dengh on 2016/8/3.
 */
public interface MarketMapper extends BaseDao {

    @Select("select t1.imarketid,cname,clogo,t1.clogolist,ctitle from tb_market t1  " +
            "inner join tb_market_cheap t2 on t1.imarketid = t2.imarketid " +
            "inner join tb_market_cheap_city t3 on t2.icheapid = t3.icheapid  " +
            "where " +
            "t2.istate = 1 and t1.istate = 1 and t1.itype=2 " +
            "and (t3.icityid=#{icityid, jdbcType=VARCHAR} or t3.icityid=-1) " +
            " order by dbms_random.value()")
    List<MarketBean>query_market(@Param("icityid")String icityid);
}
