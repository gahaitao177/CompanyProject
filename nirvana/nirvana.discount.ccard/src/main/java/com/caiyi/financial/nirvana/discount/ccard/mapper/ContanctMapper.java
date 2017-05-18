package com.caiyi.financial.nirvana.discount.ccard.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.ccard.dto.ContanctDto;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by heshaohua on 2016/6/3.
 */
public interface ContanctMapper extends BaseDao {
    /**微信文章收藏列表**/
    List<ContanctDto> query_coll_wechatList_page(@Param("cuserid")String cuserid);

    /** 微信文章列表 **/
    List<ContanctDto> query_wechatList_page(@Param("whereSql")String whereSql, @Param("orderSql")String orderSql);

    /** 查询收藏数 **/
    @Select ("select count(*) from tb_user_collection where itype=9 and istoreid= #{istoreid,jdbcType=VARCHAR} and cuserid=#{cuserid,jdbcType=VARCHAR}")
    int query_userColl(@Param("istoreid")String istoreid, @Param("cuserid")String cuserid);

    /** 查询点赞数 **/
    @Select("select count(*) from TB_USER_PRAISE where itype=9 and istoreid=#{istoreid,jdbcType=VARCHAR} and cuserid=#{cuserid,jdbcType=VARCHAR}")
    int query_userSpraise(@Param("istoreid")String istoreid, @Param("cuserid")String cuserid);

    @Insert("insert into TB_USER_PRAISE(cuserid,istoreid,itype)values (#{cuserid,jdbcType=VARCHAR}, #{istoreid,jdbcType=VARCHAR},9)")
    int insert_userSpraise(@Param("cuserid")String cuserid, @Param("istoreid")String istoreid);

    @Delete("delete TB_USER_PRAISE where itype=9 and istoreid=#{istoreid,jdbcType=VARCHAR} and cuserid=#{cuserid,jdbcType=VARCHAR}")
    int del_userSpraise(@Param("istoreid")String istoreid, @Param("cuserid")String cuserid);

    @Update("update TB_BANK_WECHAT_MSG set icollectnum = icollectnum+1 where imsgid=#{imsgid,jdbcType=VARCHAR}")
    int update_userAcoll(@Param("imsgid")String imsgid);

    @Update("update TB_BANK_WECHAT_MSG set icollectnum = icollectnum-1 where imsgid=#{imsgid,jdbcType=VARCHAR} and icollectnum>0")
    int update_userDcoll(@Param("imsgid")String imsgid);

    @Insert("insert into tb_user_collection(cuserid,Istoreid,itype)values(#{cuserid,jdbcType=VARCHAR},#{istoreid,jdbcType=VARCHAR},9)")
    int insert_userIcoll(@Param("cuserid")String cuserid, @Param("istoreid")String istoreid);

    @Delete("delete tb_user_collection where itype=9 and istoreid=#{istoreid,jdbcType=VARCHAR} and cuserid=#{cuserid,jdbcType=VARCHAR}")
    int del_userColl(@Param("istoreid")String istoreid, @Param("cuserid")String cuserid);

    @Update("update TB_BANK_WECHAT_MSG set ipraisenum = ipraisenum+1 where imsgid=#{imsgid,jdbcType=VARCHAR}")
    int update_userAPraise(@Param("imsgid")String imsgid);

    @Update("update TB_BANK_WECHAT_MSG set ipraisenum = ipraisenum-1 where imsgid=#{imsgid,jdbcType=VARCHAR} and ipraisenum>0")
    int update_userDPraise(@Param("imsgid")String imsgid);

    //更新访问量
    @Update("update tb_contact t set t.iviews = t.iviews + 1,t.dmodifiedtime = sysdate where t.icontactid = #{contactId,jdbcType=VARCHAR}")
    int update_viewCount(@Param("contactId") String contactId);

    //根据分类查询 已发布信息
    @Select("select * from tb_contact t where t.ipublished = 1 and t.iactive = 0 and t.itype = 0 and " +
            "instr( ','||t.icategory|| ',', ','||#{category,jdbcType=VARCHAR}||',')>0 order by t.iorder desc,t.icontactid desc")
    List<Map<String,String>> queryCatetory(@Param("category") String category);

    //类型
    @Select("select a.itcid typeid,a.cname typename,a.clogourl typelogo from tb_tool_category a ," +
            "tb_tool b where a.isdel = 0 and a.itid=b.itid and b.ccode=#{toolid,jdbcType=VARCHAR} ")
    List<Map<String,String>> queryToolCategoryById(@Param("toolid") String  toolid );

    //推广
    @Select("select ctitle bitemtitle,ciconurl bitempic,ctarget bitemtarget from tb_tool_spread " +
            "where iversion > #{bversion,jdbcType=VARCHAR} and (ctoolid='0000' or ctoolid=#{toolid,jdbcType=VARCHAR})" +
            " and (csource like  '%'||#{source,jdbcType=VARCHAR}||'%' or  csource is null) ")
    List<Map<String,String>> queryToolSpread(Map<String,String> map);

    //根据toolid或者是typeid  查询文章
    List<Map<String,String>> queryToolArticle(@Param("toolid") String  toolid,@Param("typeid") String  typeid);

    //更新文章的点击数
    @Update("update tb_tool_article set ICLICKCOUNT=ICLICKCOUNT+1 where itaid= #{articleid,jdbcType=VARCHAR}")
    int articleClickCount(@Param("articleid") String articleid);

    List<Map<String,String>> queryForTotalsearch(@Param("query") String query);

    /**
     * 分页查询接口,根据bean的 type值查询数据,查询的是已经发布的数据(表TB_CONTACT的IPUBLISHE字段的值是1)
     * type值为0时查询的是卡神攻略数据 type值为1时查询的是信用卡百科数据 type值为2时查询的是每日时光数据
     * @return
     */
    Page<Map<String,Object>> queryContacts(Map<String,String> map);
}
