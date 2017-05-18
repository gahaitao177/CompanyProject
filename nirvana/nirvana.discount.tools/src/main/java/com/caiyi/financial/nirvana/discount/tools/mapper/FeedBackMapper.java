package com.caiyi.financial.nirvana.discount.tools.mapper;
import com.caiyi.financial.nirvana.discount.tools.bean.FeedBackBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * Created by dengh on 2016/8/10.
 */
public interface FeedBackMapper {
    @Insert("insert into tb_user_feedback  " +
            "   (ifeedbackid,itype,istoreid,iwrongtype,cname,caddress,ibankid,ctitle,cdetail,cpicurl,icityid)\n" +
            "   values " +
            "    (seq_feed_back.nextval," +
            "#{type ,jdbcType=VARCHAR}," +
            "#{storeId ,jdbcType=VARCHAR}," +
            "#{wrongType ,jdbcType=VARCHAR}," +
            "#{name ,jdbcType=VARCHAR}," +
            "#{address ,jdbcType=VARCHAR}," +
            "#{bankId ,jdbcType=VARCHAR}," +
            "#{title ,jdbcType=VARCHAR}," +
            "#{detail ,jdbcType=VARCHAR}," +
            "#{picUrl ,jdbcType=VARCHAR}," +
            "#{cityId ,jdbcType=VARCHAR})")
    Integer u_wrong_submit(FeedBackBean bean);
    @Select("select count(1) as num from tb_custom_service tcs where trunc(tcs.cadddate) = trunc(sysdate) and " +
            "tcs.icustomserviceid = #{icustomserviceid  ,jdbcType=VARCHAR}")
    Integer query_custom_service_Num(@Param("icustomserviceid")String icustomserviceid);
    // 客服咨询统计更新
    @Update("update tb_custom_service tcs set tcs.icounts = tcs.icounts + 1,tcs.cupdate = sysdate where trunc(tcs.cadddate) = trunc(sysdate) " +
            "and tcs.icustomserviceid = #{icustomserviceid  ,jdbcType=VARCHAR} ")
    Integer update_custom_service(@Param("icustomserviceid")String icustomserviceid);
    //客服咨询统计插入
    @Insert("insert into tb_custom_service tcs " +
            "(icustomserviceid) values (#{icustomserviceid  ,jdbcType=VARCHAR})")
    Integer insert_custom_service(@Param("icustomserviceid")String icustomserviceid);

    //isoidfa 是否存在
    @Select("select count(1) \"num\" from tb_ios_idfa tii where tii.cidfa = #{cidfa,jdbcType=VARCHAR}")
    Map<String,Object> queryIsExists(@Param("cidfa")String cidfa);
    //保存cidfa信息
    @Insert("insert into tb_ios_idfa tii (cidfa,isource) values (#{cidfa,jdbcType=VARCHAR},#{isource,jdbcType=VARCHAR})")
    Integer saveIdfa(@Param("cidfa")String cidfa,@Param("isource")Integer source);

}
