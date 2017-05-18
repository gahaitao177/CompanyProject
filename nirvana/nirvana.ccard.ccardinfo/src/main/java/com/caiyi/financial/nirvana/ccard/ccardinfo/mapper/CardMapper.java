package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.BankCardDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.CardDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


/**
 * Created by lizhijie on 2016/6/20.
 */
public interface CardMapper extends BaseDao {

    //资料是否正常
    @Select("select nvl(t.isnormal,0) isnormal from tb_apply_credit_material_p t where t.ibankid=0")
    List<Map<String,Object>> queryMaterial();
    //查询城市支持银行
    List<Map<String,Object>> quryCardCity(@Param("cityid") String cityid,@Param("icooperation") String icooperation, @Param("ifFilterIos") String ifFilterIos);
    //查询卡神条数
    List<Map<String,Object>> queryArticleCount();
    //查询城市支持银行卡
    List<Map<String,Object>> queryCityCardInfo(@Param("cityid") String cityid, @Param("ifFilterIos") String ifFilterIos);
    //查询用户关注卡片
    List<Map<String,Object>> queryCardFocusInfo(@Param("cuserid") String cuserid);
    //查询微信文章
    List<Map<String,Object>> queryWechatMsg(@Param("bankids") String[] bankids,@Param("citys") String[] citys);
    //查询是否是收藏
    //@Select("select * from tb_user_collection where itype=9 and istoreid= " +
    //        "#{imsgid,jdbcType=VARCHAR} and cuserid= #{cuserid,jdbcType=VARCHAR}")
    int queryCollectScoll(@Param("imsgid") String imsgid,@Param("cuserid") String cuserid);
    //查询是否是点赞
    //@Select("select * from TB_USER_PRAISE where itype=9 and " +
    //        "istoreid= #{imsgid,jdbcType=VARCHAR} and cuserid=  #{cuserid,jdbcType=VARCHAR}")
    int querySpraise(@Param("imsgid") String imsgid,@Param("cuserid") String cuserid);

    List<Map<String,Object>> queryCityCardOld(@Param("cityid") String cityid,@Param("ifFilterIos") String ifFilterIos);

    @Select("select  decode(tbc.ccardlevel,'1','普通卡', '2','金卡/钛金卡','3','白金/钻石/无限卡',tbc.ccardlevel) " +
            "as \"ccardlevel\"  from tb_bank_card tbc  where tbc.ccardlevel is not null group by tbc.ccardlevel order by tbc.ccardlevel")
    List<Map<String,Object>> queryCardLevel();
    //查询卡的用途
    @Select("select tbu.iuseid  \"iuseid\", tbu.cusename  \"cusename\" from tb_bank_use tbu " +
            "where tbu.ishow = 1 order by tbu.iorder desc , tbu.iuseid")
    List<Map<String,Object>> queryCardType();

    List<CardDto> filterCardInfo(Map<String,String> map);

    @Select("select t1.*,t2.cusename,t2.iuseid from tb_bank_card t1 left join " +
            "(select icardid,wm_concat(cusename) cusename,wm_concat(iuseid) iuseid " +
            "from (select icardid,cusename,t1.iuseid from tb_card_use " +
            "t1,tb_bank_use t2 where t1.iuseid=t2.iuseid)group by icardid)t2 " +
            "on t1.icardid=t2.icardid where t1.icardid=#{cardid,jdbcType=VARCHAR}")
    List<BankCardDto> queryCardDetail(@Param("cardid") String cardid);

    //查询办卡指南
    @Select("select t2.ctitle,t1.caccessulr from tb_contact t1 " +
            "inner join tb_swipe_page t2 on t1.icontactid=t2.iitemid " +
            "where t1.iactive=0 and t1.ipublished= 1 and t2.itype=3 and t2.ibankid=#{bankId,jdbcType=VARCHAR} " +
            "order by t1.cpublishedtime desc ")
    Map<String,String> queryCardGuide(@Param("bankId") String bankId);


    //查询信用卡优惠数量
    @Select("select count(*) as count from tb_cheap t where t.cbankid =#{bankId,jdbcType=VARCHAR}")
    int queryCardCheapCount(@Param("bankId") String bankId);

    //积分商城数
    @Select("select count(*) as count from tb_bank_jf_commodity t where t.ibankid =#{bankId,jdbcType=VARCHAR}")
    int queryCommodityCount(@Param("bankId") String bankId);

    //该用户是否收藏
    @Select("select tu.cuserid from tb_user tu, tb_user_collection tuc where tu.cuserid = tuc.cuserid and " +
            "tu.cuserid = #{cuserId,jdbcType=VARCHAR}  and tuc.iexpire = 0 " +  //and (tu.cpassword =  or tu.cpwd9188=? )
            "and tuc.idel = 0  and tuc.itype = 2 and tuc.istoreid = #{cardid,jdbcType=VARCHAR}")
    List<String> queryUserIdByCardid(@Param("cuserId") String cuserId,@Param("cardid") String cardid);

    //根据城市的id查询银行的code
    @Select("select ccode from tb_loan_dictionary t where t.istatus = 1 " +
            "   and t.ipdicid = (select t1.idicid from tb_loan_dictionary t1\n" +
            "   right join (select t2.adcode from tb_area t2 where t2.iareaid = #{cityId,jdbcType=VARCHAR}) t3\n" +
            "   on t1.ccode = t3.adcode where t1.istatus = 1)")
    List<String> queryBandCodeByCityId(@Param("cityId") String cityId);
    //查询银行的办卡地址
//    @Select("select thc.cardaddr,thc.cardaddrios from tb_handle_credit thc where thc.ibankid = #{bankId,jdbcType=VARCHAR}")
//    Map<String,String> queryBankAddress(@Param("bankId") String bankId);
    //查询银行的办卡地址
    @Select("select thc.cardaddr \"cardaddr\",thc.cardaddrios \"cardaddrios\",thc.cpackagename \"cpackagename\" from tb_handle_credit thc where thc.ibankid = #{bankId,jdbcType=VARCHAR}")
    Map<String,String> queryBankAddress(@Param("bankId") String bankId);

    //该银行是否支持模拟办卡
    @Select("select nvl(isnormal,0) isnormal from TB_APPLY_CREDIT_MATERIAL_P where ibankid=#{bankId,jdbcType=VARCHAR}")
    Integer isNormalOfBank(@Param("bankId") String bankId);

    //该卡是否支持模拟办卡
    @Select("select nvl(cardnormal,0) cardnormal from TB_BANK_CARD where icardid=#{cardid,jdbcType=VARCHAR}")
    Integer isNormalOfCard(@Param("cardid") String cardid);

    //获得排除后的合作银行Id
    @Select("select ibankid from TB_BANK where icooperation>0 ${exsql} order by iorder desc")
    List<Integer> queryBankId(@Param("exsql") String exsql);

    //从合作银行每家选一张相同主题，相同等级的卡
    List<BankCardDto> queryThemeByCardId(Card card);

    /**
     *
     * @return
     * add by lcs 20160805 按关键字搜索 三条  卡片名、权益、标签
     */
    List<CardDto> queryCardForTotalSearch(Card card);


}
