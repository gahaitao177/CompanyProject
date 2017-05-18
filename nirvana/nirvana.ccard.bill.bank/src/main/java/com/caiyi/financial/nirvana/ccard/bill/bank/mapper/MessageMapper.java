package com.caiyi.financial.nirvana.ccard.bill.bank.mapper;

import com.caiyi.financial.nirvana.ccard.bill.bean.Message;
import com.caiyi.financial.nirvana.ccard.bill.dto.MessageDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by lizhijie on 2016/9/18.
 */
public interface MessageMapper extends BaseDao {
    //保存消息
    @Insert("insert into tb_user_message (imsgid,ctitle,csubtitle,cimgurl,cdesc1,cdesc2,cdesc3," +
            "ctarget,itype,caddtime,cuserid,iflag) values(SEQ_MESSAGEID.NEXTVAL,#{title,jdbcType=VARCHAR}," +
            "#{subtitle,jdbcType=VARCHAR},#{imgurl,jdbcType=VARCHAR},#{desc1,jdbcType=VARCHAR}," +
            "#{desc2,jdbcType=VARCHAR},#{desc3,jdbcType=VARCHAR},#{target,jdbcType=VARCHAR}," +
            "#{type,jdbcType=INTEGER},sysdate,#{userId,jdbcType=VARCHAR},1)")
    Integer saveMessage(Message message);
    //num 为负数时 表示过去num个月，为正表示未来num个月
    @Select("select ctitle,csubtitle,cimgurl,cdesc1,cdesc2,cdesc3,ctarget,itype,caddtime ," +
            "case  when (caddtime-trunc(sysdate-1))<0 then to_char(caddtime,'yyyy-mm-dd hh24:mi')\n" +  //昨天之前
            "      when (caddtime-trunc(sysdate-1))*24< 24 and (caddtime-trunc(sysdate-1))>0 then '昨天'||to_char(caddtime,'hh24:mi')\n" +//昨天
            "      when (caddtime -trunc(sysdate))*24 > 1 and (sysdate-caddtime)*24>1 then  to_char(caddtime,'hh24:mi')\n" + //今天 又不超过1小时
            "      else floor((sysdate-caddtime)*24 *60)|| '分钟前' \n" + //一小时之内
            "end \"convertTime\" from" +
            " tb_user_message  where cuserid=#{cuserid,jdbcType=VARCHAR} and " +
            "caddtime  between add_months(sysdate,#{num,jdbcType=INTEGER}) and  sysdate and isdel=0  order by caddtime asc")
    List<MessageDto> queryMessageList(@Param("cuserid") String cuserid,@Param("num") Integer  num);
    //通过PartnerId 查询userid
    @Select("select b.cuser_id from tb_repayment_order b where b.cpartner_id=#{partnerId,jdbcType=VARCHAR}  and rownum=1")
    String queryUserIdByPartnerId(@Param("partnerId") String partnerId);
    //num 为负数时 表示过去num个月，为正表示未来num个月 num个月 是否存在未读消息
    @Select("select count(1)  from tb_user_message  where cuserid=#{cuserid,jdbcType=VARCHAR} and " +
            "caddtime  between add_months(sysdate,#{num,jdbcType=INTEGER}) and  sysdate and iflag=1 and isdel=0")
    Integer queryValidMessageCount(@Param("cuserid") String cuserid,@Param("num") Integer  num);
    //num 为负数时 表示过去num个月，为正表示未来num个月 设置为已读
    @Update("update tb_user_message set iflag=0 where  caddtime  between add_months(sysdate,#{num,jdbcType=INTEGER})" +
            " and  sysdate and iflag=1 and cuserid=#{cuserid,jdbcType=VARCHAR} and isdel=0")
    Integer updateValidMessageList(@Param("cuserid") String cuserid,@Param("num") Integer  num);
    @Update("update tb_user_message tm set tm.isdel=1 where tm.ctarget=#{billId,jdbcType=VARCHAR} " +
            " and tm.cuserid=#{cuserid,jdbcType=VARCHAR} and isdel=0")
    Integer deleteMessageByBillId(@Param("cuserid") String cuserid,@Param("billId") String billId);
}
