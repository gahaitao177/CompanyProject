package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.LeancloudBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by dengh on 2016/7/27.
 */
public interface LeancloudMapper extends BaseDao {
    @Select("select count(tlu.cuserid) as num from tb_leancloud_user tlu" +
            " where tlu.cleancloudid = #{cleancloudid ,jdbcType=VARCHAR} " +
            "and tlu.idevicetype = #{idevicetype ,jdbcType=VARCHAR} and tlu.cuserid = #{cuserid ,jdbcType=VARCHAR}")
    Integer query_leancloud_numBycloudid(LeancloudBean bean);
    @Insert("insert into tb_leancloud_user (cuserid, cleancloudid, idevicetype,cbankids, icityid) values" +
            " (#{cuserid ,jdbcType=VARCHAR}," +
            "#{cleancloudid ,jdbcType=VARCHAR}," +
            "#{idevicetype ,jdbcType=VARCHAR}," +
            "#{cbankids ,jdbcType=VARCHAR}," +
            "#{icityid ,jdbcType=VARCHAR})")
    Integer insert_leancloud(LeancloudBean bean);
    @Update("update tb_leancloud_user tlu set " +
            "cbankids =#{cbankids ,jdbcType=VARCHAR}, " +
            "icityid = #{icityid ,jdbcType=VARCHAR}, " +
            "cupdate =sysdate " +
            "where tlu.cleancloudid = #{cleancloudid ,jdbcType=VARCHAR} " +
            "and tlu.idevicetype = #{idevicetype ,jdbcType=VARCHAR} and tlu.cuserid = #{cuserid ,jdbcType=VARCHAR}")
    Integer update_leancloud_byCloudid(LeancloudBean bean);

    // deleted by lcs 20161115 废弃
//    @Select("select count(tlu.cuserid) as num from tb_leancloud_user tlu" +
//            " where tlu.cuserid = #{cuserid ,jdbcType=VARCHAR} " +
//            "and tlu.idevicetype = #{idevicetype ,jdbcType=VARCHAR}")
//    Integer query_leancloud_numBycuserid(LeancloudBean bean);
//    @Update("update tb_leancloud_user tlu set " +
//            "cbankids =#{cbankids ,jdbcType=VARCHAR}, " +
//            "icityid = #{icityid ,jdbcType=VARCHAR}, " +
//            "cupdate =sysdate " +
//            "where tlu.cuserid  = #{cuserid  ,jdbcType=VARCHAR} " +
//            "and tlu.idevicetype = #{idevicetype ,jdbcType=VARCHAR}")
//    Integer update_leancloud_byCuserid (LeancloudBean bean);






}
