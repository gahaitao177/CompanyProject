package com.caiyi.financial.nirvana.discount.ccard.mapper;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Window;
import com.caiyi.financial.nirvana.discount.ccard.dto.AreaDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.CheapDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.MarketDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.StrategyDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.TopicBussiDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.TopicDto;

/**
 * Created by heshaohua on 2016/5/3.
 */
public interface CheapMapper extends BaseDao {

    /**
     * 地区级联
     * @param ipareaid
     * @return
     */
    @Select("select iareaid,careaname,clat,clng,icount,iareatype,iroot,ipareaid,citycode,adcode from tb_area start with ipareaid=#{ipareaid,jdbcType=INTEGER} connect by prior iareaid= ipareaid")
    List<AreaDto> query_area(@Param("ipareaid") Integer ipareaid);

    List<AreaDto> query_area2(@Param("ipareaid") Integer ipareaid, @Param("bankid") Integer bankid);

    @Select("select iareaid,careaname,clat,clng,icount,iareatype,iroot,ipareaid,citycode,adcode from tb_area where ipareaid=#{ipareaid,jdbcType=INTEGER}")
    List<AreaDto> query_area3(@Param("ipareaid") Integer ipareaid);




    /**
     * 优惠
     * @param istoreid
     * @return
     */
    List<CheapDto> queryCheap(@Param("istoreid") String istoreid);

    /**
     * 刷吧页面查询
     * @return
     */
    List<TopicDto> queryShuaba();
    List<MarketDto> queryShuaba1();
    List<StrategyDto> queryShuaba2();
    
    /**
     * 主题
     * @return
     */
    @Select("select t.itopicid,'' as ctitle,t.csubtitle,t.cpicurl,t.iclickcount from tb_topic t " +
            "where t.iexpire = 1 and idelflag = 1")
    List<TopicDto> queryTopic();
    /**
     * 主题数量
     * @return int
     */
    Integer queryWindowCount(Window window);
    /**
     * 按距离排序主题
     * @return
     */
    List<TopicBussiDto> window_query_distance(Window window);

    /**
     * 通过Id查询主题
     * @return
     */
    @Select("select ttc.itopicid,ttc.ccontent,ttc.cpicurl,ttc.iclickcount," +
            "ttc.iareaid,ttc.ckeywords from tb_topic ttc where ttc.itopicid = #{topicId,jdbcType=VARCHAR}")
    TopicDto queryTopicById(@Param("topicId") String topicId);
    /**
     * 获取专题关联商家
     * @return
     */
    List<TopicBussiDto> topic_business_query(Window window);
    //更新点击数
    @Update("update tb_topic tp set tp.iclickcount = tp.iclickcount + 1 where tp.itopicid =  #{topicId,jdbcType=VARCHAR}")
    Integer u_topic_click(@Param("topicId") String topicId);

    //保存用户出错信息
    @Insert("insert into tb_statistics (istatisticsid,itype,cimei,cinfo) values (seq_statistics.nextval, " +
            "#{type,jdbcType=VARCHAR}, #{uid,jdbcType=VARCHAR}, #{info1,jdbcType=VARCHAR})")
   Integer saveUserStatistics(Map<String, String> map);

    @Select("select cversion \"cversion\",cpicurl \"cpicurl\" from tb_app_startpage where itype=1 " +
            "and istartpagetype=0")
    Map<String,Object> queryStartPage();
    //查询版本信息
    @Select("select cversion \"cversion\" from tb_app_startpage where itype=#{type,jdbcType=INTEGER} ")
    String queryVersion(@Param("type") Integer type);
    //查询banner
    @Select("select itype \"itype\",cpicurl \"cpicurl\",cdata \"cdata\",ctitle \"ctitle\" from " +
            "tb_banner where istate=1 and isnewbanner=#{type,jdbcType=INTEGER}")
    List<Map<String,Object>> queryBanner(@Param("type") Integer type);
    //查询银行包信息
    @Select("select mainswitch from tb_app_bank_page where cpackagename = #{packagename,jdbcType=VARCHAR} " +
            "and (csource like '%'||'${source}'||'%' or csource = 'all') and cappversion>#{appversion,jdbcType=VARCHAR}")
    List<Integer> queryAppBankPage(Map<String, String> map);
    //全局搜索卡神攻略
    Page<Map<String,Object>> totalsearch_contanct(Map<String, String> map);
    //查询专题列表
    @Select("  select s1.ctitle  \"title\", s1.cpicurl \" picurl\", s1.iitemid \" topicid\"\n" +
            "  from (select t.ctitle,t.cpicurl, t.iitemid  from tb_swipe_page t  where t.itype = 0 " +
            " and t.idel = 0 order by t.iorder desc) s1 where rownum <= 4")
    List<Map<String,Object>> queryTopics();

    // 优惠市集节点
    @Select("select * from ( select t1.ctitle  \"title\",t1.csubtitle \" subtitle\", t1.cpicurl   as \"picurl\", " +
            "t2.caccessulr as \"targeturl\"  from tb_swipe_page t1  inner join tb_contact t2   " +
            "on t1.iitemid = t2.icontactid  and t2.itype = 2 where t1.itype = 1  and t1.idel = 0 " +
            "order by t1.iorder desc )s1 where rownum <=3")
    List<Map<String,Object>> queryMarkets();
    // 专题节点
    @Select(" select * from ( select t1.ctitle as \"title\",t1.csubtitle as \"subtitle\", t1.cpicurl  as \"picurl\"," +
            " t2.caccessulr as \"targeturl\"  from tb_swipe_page t1 inner join tb_contact t2   on t1.iitemid = t2.icontactid  " +
            "and t2.itype = 0 where t1.itype = 2 and t1.idel = 0 order by t1.iorder desc )s1 where rownum =1")
    List<Map<String,Object>> queryStrategys();

    //美洽客服信息
    List<Map<String,Object>>queryMqkfInfo(@Param("appVersion") String appVersion);
}
