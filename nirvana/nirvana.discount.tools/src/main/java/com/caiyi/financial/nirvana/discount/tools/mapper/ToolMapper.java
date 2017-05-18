package com.caiyi.financial.nirvana.discount.tools.mapper;

import com.caiyi.financial.nirvana.discount.tools.bean.ToolVersionBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/8/9.
 */
public interface ToolMapper {
    @Select("select * from tb_apply_credit_material b where b.imaterialid=485")
    Map<String,Object> queryTest();
    //精品推荐
    List<Map<String,Object>> queryQualitySpread(@Param("typeid") String typeid);

    //获取用户的账本数据
    @Select("select bookId \"bookId\", bookName \"bookName\",bookColor \"bookColor\",updateTime \"updateTime\",budget \"budget\"," +
            "isDelete \"isDelete\",exparam01 \"exparam01\",exparam02 \"exparam02\" from tb_decoration_bill  where " +
            "cuserid =#{cuserid,jdbcType=VARCHAR}")
    List<Map<String,Object>> queryBooks(@Param("cuserid") String cuserid);
    // 获取用户的类别
    @Select("select categoryId \"categoryId\",subCategoryId \"subCategoryId\",name \"name\"," +
            "updateTime \"updateTime\",sDelete \"isDelete\",exparam01 \"exparam01\",exparam02 \"exparam02\" " +
            "from TB_DECORATION_CATEGORY  where cuserid =#{cuserid,jdbcType=VARCHAR}")
    List<Map<String,Object>> queryCategory(@Param("cuserid") String cuserid);
    // 获取记录
    @Select("select irecordId \"irecordId\", bookId \"bookId\",categoryId \"categoryId\",subCategoryId \"subCategoryId\"," +
            "updateTime \"updateTime\",expenditure \"expenditure\",remark \"remark\",isDelete \"isDelete\",exparam01 \"exparam01\"," +
            "exparam02 \"exparam02\",exparam03 \"exparam03\",exparam04 \"exparam04\", cuserid  \"cuserid\" from TB_DECORATION_RECORD" +
            "  where cuserid =#{cuserid,jdbcType=VARCHAR}")
    List<Map<String,Object>> queryRecord(@Param("cuserid") String cuserid);
    //查询账本数量
    @Select("select count(1)  \"num\" from tb_decoration_bill tdb where  tdb.cuserid = #{cuserid,jdbcType=VARCHAR} " +
            "and bookid = #{bookid,jdbcType=VARCHAR}")
    Integer queryBookCount(@Param("cuserid") String cuserid,@Param("bookid") String bookid);
    //更新账本信息
    Integer updateBookInfo(Map<String ,String> map);
    //保存账本信息
    Integer saveBookInfo(Map<String ,String> map);

    //查询记录数量
    @Select("select count(1) as num from tb_decoration_record tdr where tdr.cuserid =#{cuserid,jdbcType=VARCHAR} " +
            "and tdr.irecordid = #{irecordid,jdbcType=VARCHAR}")
    Integer queryRecordCount(@Param("cuserid") String cuserid,@Param("irecordid") String irecordid);
    //更新账本信息
    Integer updateRecordInfo(Map<String ,String> map);
    //保存账本信息
    Integer saveRecordInfo(Map<String ,String> map);

    //查询记录数量
    @Select("select count(1) as num from tb_decoration_category tdc where tdc.cuserid =#{cuserid,jdbcType=VARCHAR}" +
            " and tdc.subcategoryid=#{subcategoryid,jdbcType=VARCHAR}")
    Integer queryCategoryCount(@Param("cuserid") String cuserid,@Param("subcategoryid") String subcategoryid);
    //更新账本信息
    Integer updateCategoryInfo(Map<String ,String> map);
    //保存账本信息
    Integer saveCategoryInfo(Map<String ,String> map);
    @Select("select * from tb_tool_version where iisopen=1 and " +
            "ctoolid = #{ctoolid,jdbcType=VARCHAR } " +
            "and itype = #{itype,jdbcType=VARCHAR } " +
            " and instr(',' || csource || ',', ',' || #{csource,jdbcType=VARCHAR} || ',') >= 1 and " +
            "cversion>#{cversion,jdbcType=VARCHAR } ")
    List<ToolVersionBean> query_tool_version(ToolVersionBean bean);

}
