package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.discount.user.dto.CheapDto;
import com.caiyi.financial.nirvana.discount.user.dto.MarketCheapDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by wenshiliang on 2016/9/5.
 * 收藏
 *
 */
public interface CollectionMapper {


    /**
     * 查询收藏的门店优惠
     * @param cuserId
     * @return
     */
    List<CheapDto> selectCheapDto(@Param("cuserId") String cuserId);

    /**
     * 查询收藏的优惠劵
     * @param cuserId
     * @return
     */
    List<MarketCheapDto> selectMarketCheapDto(@Param("cuserId")String cuserId);



    @Select("select istoreid from tb_user_collection where cuserid=#{cuserId,jdbcType=VARCHAR} and itype=#{itype,jdbcType=INTEGER}")
    List<String> queryCollectionId(@Param("cuserId")String cuserId,@Param("itype")int itype);


    @Insert("insert into tb_user_collection tuc (cuserid,istoreid,itype) values (#{cuserid,jdbcType=VARCHAR}, #{istoreid,jdbcType=VARCHAR} ,#{itype,jdbcType=INTEGER})")
    Integer saveUserCollection(@Param("cuserid") String cuserid, @Param("istoreid") String istoreid , @Param("itype") int itype);

    /**
     * 删除优惠
     * @param cuserid
     * @param istoreid
     * @param itype
     * @return
     */
    @Delete("delete from tb_user_collection t where t.cuserid =#{cuserid,jdbcType=VARCHAR} and t.istoreid =#{istoreid,jdbcType=VARCHAR } and t.itype =#{itype,jdbcType=INTEGER } ")
    int deleteUserCollection(@Param("cuserid") String cuserid, @Param("istoreid")String istoreid ,@Param("itype")int itype );

    /**更新超市优惠收藏数**/
    @Update("update tb_market_cheap tmc set tmc.isavetimes = tmc.isavetimes - 1 where tmc.icheapid =#{icheapid,jdbcType=VARCHAR }")
    int updateMarketCheapCollection(@Param("icheapid")String icheapid);

    /**更新商户表的收藏数***/
    @Update("update tb_business tb set tb.isavetimes = tb.isavetimes -1 where tb.ibusinessid in (select ts.ibussinessid from tb_store ts where ts.istoreid = #{istoreid,jdbcType=VARCHAR })")
    int updateBusinessCollection(@Param("istoreid")String istoreid);
}