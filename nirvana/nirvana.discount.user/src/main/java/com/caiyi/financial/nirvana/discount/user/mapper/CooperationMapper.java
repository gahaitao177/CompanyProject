package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.CooperationBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by dengh on 2016/7/29.
 */
public interface CooperationMapper extends BaseDao {
    @Select("select tcb.istate from tb_cooperation_bind tcb where" +
            " tcb.cuserid =#{cuserid, jdbcType=VARCHAR}  and" +
            " tcb.itype = #{itype, jdbcType=VARCHAR}  ")
    List<Integer> query_cooperation_state(CooperationBean bean);
    /***保存合作ID*/
    @Insert(" insert into tb_cooperation_bind tcb " +
            "(cuserid,ccooperationid,itype,cip,iclient)" +
            "    values(" +
            "#{cuserid, jdbcType=VARCHAR}," +
            "#{ccooperationid, jdbcType=VARCHAR}," +
            "#{itype, jdbcType=VARCHAR}," +
            "#{cip, jdbcType=VARCHAR}," +
            "#{iclient, jdbcType=VARCHAR})")
    Integer Insert_cooperation(CooperationBean bean);
    //更新
    @Update("update tb_cooperation_bind tcu " +
            "set tcu.ccooperationid = #{ccooperationid, jdbcType=VARCHAR}," +
            " tcu.ilogincount = tcu.ilogincount + 1,tcu.cupdate = sysdate," +
            "tcu.cip =#{cip, jdbcType=VARCHAR}," +
            "tcu.iclient = #{iclient, jdbcType=VARCHAR} " +
            " where tcu.cuserid = #{cuserid, jdbcType=VARCHAR}" +
            " and tcu.itype = #{itype, jdbcType=VARCHAR}")
    Integer update_cooperation_Bycuserid(CooperationBean bean);



}
