package com.caiyi.financial.nirvana.ccard.investigation.mapper;

import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditCardDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditInvestigationDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ProvidentFundDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by lizhijie on 2016/12/20. 计算积分
 */
public interface CalculateScoreMapper extends BaseDao {

    @Select("select tt.ici_id creditId,tt.cci_add_time addTime ,tt.ici_is_overstay isOverStay,tt.cci_title title," +
            "tt.cci_status status,tt.ici_is_card isCard,tt.ici_is_loan isLoan,tt.cci_update_time updateTime" +
            ",months_between(sysdate,tt.cci_add_time) addMonth  from  tb_zx_credit_investigation  tt where tt.ici_id=#{id,jdbcType=INTEGER}")
    CreditInvestigationDto getCreditInvestigetionBeanById(@Param("id") int id);

    @Select(" select t2.igjj_id providentFundId,t2.cgjj_title title,t2.cgjj_status status,t2.cgjj_url url," +
            " t2.cgjj_add_time addTime, t2.cgjj_update_time updateTime, t2.igjj_month_num mounthNum," +
            " t2.igjj_month_total mounthTotal ,months_between(sysdate,t2.cgjj_add_time) addMonth " +
            "from tb_zx_gjj t2 where t2.igjj_id=#{id,jdbcType=INTEGER}")
    ProvidentFundDto getGjjBeanById(@Param("id") int id);

    CreditCardDto getCreditCard(@Param("id") String id);

    @Select("select nvl(bm0.irepayment,-1) from tb_bill_month bm0 where bm0.cmonth= " +
            "(select max（bm.cmonth）from tb_bill_month bm where bm.ibillid=#{billid,jdbcType=INTEGER} and bm.isbill=1)" +
            "and bm0.ibillid=#{billid,jdbcType=INTEGER}")
    Integer queryMaxMonthRepayStatus(@Param("billid") int billid);

}
