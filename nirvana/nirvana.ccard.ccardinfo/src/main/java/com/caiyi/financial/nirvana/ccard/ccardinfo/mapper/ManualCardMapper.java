package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.CardProgressDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.ChannelDao;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/4.
 */
public interface ManualCardMapper extends BaseDao {

    //查询手机号码是否申请过银行卡和发送状态
    @Select("select t.ibankid,t.isuccess from tb_card_apply t where t.cphonenum=" +
            "#{phonenum,jdbcType=VARCHAR} and t.cadddate>=sysdate-30")
    List<Map<String,Object>> queryBankState(@Param("phonenum") String phonenum) ;
    //发送短信验证码
    Map<String,Object> updateRegisterSendYZM(Map<String,Object> map);
    //校验预约办卡短信验证码
    Integer registerCheckYZM(Map<String,String> map);

    //查询十分钟之内是否发过短信
    @Select("select count(*) as count from tb_sms t where t.crecphone=#{phonenum,jdbcType=VARCHAR}" +
            " and t.cadddate > (sysdate - 10/(60*24))")
    Integer queryMessage(@Param("phonenum") String phonenum);

    //申卡信息插入
    @Insert("insert into tb_card_apply(iapplyid,cprofession,isuse,ioverdue,isocialpay," +
            " iworkprove,iotherbank,iadvantage,cname,cphonenum,idegree,cworkorg,cworkplace,iage," +
            " icardid,ibankid,iprivincecode,icitycode,icountycode,cgroupcode,clat,clng,igender,isuccess," +
            " istatus,ihouse,icar) values " +
            " (seq_applyid.nextval,#{profession,jdbcType=VARCHAR},#{use,jdbcType=VARCHAR},#{overdue,jdbcType=VARCHAR}," +
            "#{socialpay,jdbcType=VARCHAR},#{workprove,jdbcType=VARCHAR},#{otherbank,jdbcType=VARCHAR},#{advantage,jdbcType=VARCHAR}," +
            "#{name,jdbcType=VARCHAR},#{phonenum,jdbcType=VARCHAR},#{idegree,jdbcType=VARCHAR},#{cworkorg,jdbcType=VARCHAR}," +
            "#{workplace,jdbcType=VARCHAR},#{age,jdbcType=VARCHAR},#{cardid,jdbcType=VARCHAR},#{bankid,jdbcType=VARCHAR}," +
            "#{privincecode,jdbcType=VARCHAR},#{citycode,jdbcType=VARCHAR},#{countycode,jdbcType=VARCHAR},#{cgroupcode,jdbcType=VARCHAR}," +
            "#{lat,jdbcType=VARCHAR},#{lng,jdbcType=VARCHAR},#{gender,jdbcType=VARCHAR},#{isuccess,jdbcType=VARCHAR}," +
            "#{istatus,jdbcType=VARCHAR},#{ihouse,jdbcType=VARCHAR},#{icar,jdbcType=VARCHAR})")
   Integer saveCardAppliedInfo(Card card);

    //查询用户可申卡的银行
    @Select("select t2.ccode \"ccode\", t2.cname \"cname\" from tb_loan_dictionary t2 " +
            " where t2.ipdicid = ( " +
            " select t1.idicid from tb_loan_dictionary t1  where t1.itype = 1 " +
            " and t1.istatus = 1 and t1.ccode = #{code,jdbcType=VARCHAR})and t2.istatus = 1 " +
            " and t2.ccode not in ( " +
            " select distinct (t.ibankid)  from tb_card_apply t " +
            " where t.cphonenum =  #{phonenum,jdbcType=VARCHAR}  and t.cadddate >= sysdate - 30)")
    List<Map<String,String>> queryUserBank(@Param("code") String code,@Param("phonenum") String phonenum);

    //查询o2o办卡进度
    @Select("select t2.cbankname,t1.iapplyid,t1.istatus as applyStatus,t1.isuccess,t1.cadddate," +
            " t3.istatus as orderStatus,t4.cphone from tb_card_apply t1 left join tb_bank t2 " +
            "on t1.ibankid=t2.ibankid  left join tb_loan_order t3 on t1.iapplyid=t3.iapplyid \n" +
            "left join tb_loan_user t4 on t3.iloanid=t4.iloanid \n" +
            "where t1.cphonenum=#{phonenum,jdbcType=VARCHAR}")
    List<CardProgressDto> queryProgressOfCard(@Param("phonenum") String phonenum);

    //o2o进度详情
    @Select("select t1.cadddate ,t1.istatus as applyStatus,t1.isuccess, t2.cadddate as buyDate,\n" +
            "t2.istatus as orderStatus ,t3.cloanname,t3.cphone from tb_card_apply t1\n" +
            " left join tb_loan_order t2 on t1.iapplyid=t2.iapplyid \n" +
            " left join tb_loan_user t3 on t2.iloanid=t3.iloanid\n" +
            " where t1.iapplyid=#{applyid,jdbcType=VARCHAR}")
    CardProgressDto queryDetailProgressOfCard(@Param("applyid") String applyid);

    //卡申请任务更新
    @Update("update tb_bank_card tbc set tbc.iapplicationnum = tbc.iapplicationnum + 1 " +
            "where tbc.icardid = #{icardid,jdbcType=VARCHAR}")
    int updateCardApplyTask(@Param("icardid") String icardid);

    // 获取渠道内容接口
    @Select(" select c.icardid \"icardid\",c.ichannelid \"ichannelid\",c.iorder \"iorder\",c.cbankname \"cbankname\"," +
            "c.ctitle \"ctitle\",to_char(c.cshelf_time,'yyyy-mm-dd') \"cshelf_time\" \n" +
            " ,c.capply_url \"capply_url\",c.cimg_url \"cimg_url\",c.iclicks \"iclicks\",c.isaudit \"isaudit\" " +
            " from tb_channel_cards c where 1=1 and c.isaudit='1'\n" +
            " and ichannelid = #{ichannelid,jdbcType=VARCHAR} order by c.IORDER asc")
    List<Map<String,String>> queryChannelContend(@Param("ichannelid") String ichannelid);

    // 获取渠道
    List<ChannelDao> queryChannels(@Param("ichannelid") String ichannelid);
}
