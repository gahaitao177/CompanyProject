package com.caiyi.financial.nirvana.ccard.bill.mail.mapper;

import com.caiyi.financial.nirvana.ccard.bill.bean.MailBill;
import com.caiyi.financial.nirvana.ccard.bill.dto.ImportTaskDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.MailBillDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by lichuanshun on 16/5/9.
 *  邮箱导入相关
 */
public interface MailMapper extends BaseDao{
    /**
     *
     * @param mailBillBean
     * @return
     */
//    @Select("select * from tb_user tu where tu.cuserid =#{cuserId,jdbcType=VARCHAR}")
    List<Map<String,Object>> select(MailBill mailBillBean);
    // 根据billid 获取账单信息
//    @Select("select icreditid,ioutsideid,ibillid,iskeep,cexpiredate from tb_bank_bill where ibillid=#{0,jdbcType=INTEGER} and cuserid=#{1,jdbcType=VARCHAR} and iswebormail=1")
    MailBillDto getMailBillByBillId(String billId, String cuserId);

    @Select("select * from tb_import_task where itype=#{itype,jdbcType=INTEGER} and cuserid=#{cuserid,jdbcType=VARCHAR} and ibankid=#{ibankid,jdbcType=INTEGER} and (isend=0 or isend=2) and caccountname=#{caccountname,jdbcType=VARCHAR}")
    ImportTaskDto queryTaskByUser(@Param("itype")int itype, @Param("cuserid") String cuserid, @Param("ibankid")int ibankid, @Param("caccountname") String caccountname);


    @Select("select seq_import_task.nextval itaskid from dual")
    Integer querySeqIdFormTask();

    int updateOnlyDate(String billId, String cuserId);

    @Insert("insert into tb_import_task(ITASKID,CUSERID,IBANKID,ITYPE,ISTATE,CDESC,CURLPARAMS,CACCOUNTNAME,ISEND) values(#{itaskid,jdbcType=INTEGER}," +
            "#{cuserid,jdbcType=VARCHAR},#{ibankid,jdbcType=INTEGER},#{itype,jdbcType=INTEGER},#{istate,jdbcType=INTEGER},#{cdesc,jdbcType=VARCHAR},#{curlparams,jdbcType=VARCHAR}," +
            "#{caccountname,jdbcType=VARCHAR},#{isend,jdbcType=INTEGER})")
    int createTask(ImportTaskDto taskDto);

}
