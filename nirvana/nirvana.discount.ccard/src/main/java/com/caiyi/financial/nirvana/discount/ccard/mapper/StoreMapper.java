package com.caiyi.financial.nirvana.discount.ccard.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.ccard.bean.Store;
import com.caiyi.financial.nirvana.discount.ccard.dto.CheapDetailDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/5/5.
 */
public interface StoreMapper extends BaseDao {

    /**
     * 根据 商户  城市查询门店
     * @param store
     * @return
     */
    List<Store> storeList(Store store);


    /**
     * 查询商户在 城市 距离经纬度最近的门店id
     * @param ibussinessid
     * @param icityid
     * @param clat
     * @param clng
     * @return
     */
    String queryNearestStoreId(@Param("ibussinessid") String ibussinessid, @Param("icityid") Long icityid, @Param("clat") Double clat, @Param("clng") Double clng);

    /**
     * 查询城市的经纬度
     * @param icityid
     * @return
     */
    @Select("select t.clng \"clng\",t.clat \"clat\" from tb_area t where t.iareaid=#{icityid,jdbcType=DECIMAL}")
    Map<String,String> queryClngAndClat(@Param("icityid")Long icityid);

    @Select("select * from tb_store s,tb_business b where s.ibussinessid=b.ibusinessid and s.istoreid=#{istoreid,jdbcType=VARCHAR}")
    CheapDetailDto selectCheapDetail(@Param("istoreid")String istoreid);


    /**
     * 点赞数
     * @param cuserid
     * @param istoreid
     * @return
     */
    @Select("select count(1) as num from tb_user_praise where cuserid = #{cuserid,jdbcType=VARCHAR} and istoreid=#{istoreid,jdbcType=VARCHAR}")
    int praiseCount(@Param("cuserid")String cuserid,@Param("istoreid")String istoreid);

    /**
     * 收藏数
     * @param cuserid
     * @param istoreid
     * @return
     */
    @Select("select count(1) as num from tb_user_collection where cuserid =#{cuserid,jdbcType=VARCHAR} and istoreid=#{istoreid,jdbcType=VARCHAR} and idel=0 and itype = 0 and iexpire = 0")
    int collCount(@Param("cuserid")String cuserid,@Param("istoreid")String istoreid);


    /**
     * 查询当前城市商户的门店数量
     * @param ibussinessid
     * @param icityid
     * @return
     */
    @Select("select count(*) from tb_store where ibussinessid=#{ibussinessid,jdbcType=VARCHAR} and icityid=#{icityid,jdbcType=DECIMAL} and istate!=2 and camapid != 0")
    int storeCount(@Param("ibussinessid")String ibussinessid,@Param("icityid")Long icityid);


    /**
     * 商户浏览数+1
     * @param ibussinessid
     * @return
     */
    @Update("update tb_business set ipvtimes=ipvtimes+1 where ibusinessid=#{ibussinessid,jdbcType=VARCHAR}")
    int addBusinessIpvtimes(@Param("ibussinessid")String ibussinessid);



}