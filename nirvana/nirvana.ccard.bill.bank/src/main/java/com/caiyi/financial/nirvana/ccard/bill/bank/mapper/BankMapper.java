package com.caiyi.financial.nirvana.ccard.bill.bank.mapper;

import com.caiyi.financial.nirvana.ccard.bill.bean.BankPoint;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.dto.*;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by terry on 2016/5/25.
 */
public interface BankMapper extends BaseDao {
    final static String EXCHANGE_FATE_US_CHINA = "6.3317";//美元兑人民币

    List<Map<String, Object>> select(Channel channelBean);

    BankBillDto getUserBankBill(String card4Num, Integer bankId, String cuserId);

    BankBillDto getUserBankBillById(String billId);

    BankBillDto getUserBankBillByBillId(String billId, String uid);

    /*SELECT t_bill.* FROM tb_bank_bill t_bill,tb_bank t_bank WHERE t_bill.ibankid = t_bank.ibankid AND t_bill.cuserid=#{0,jdbcType=VARCHAR} AND ISDEL=0*/
    List<BankBillDto> getUserBankBillByUser(String userId);

    List<BankBillDto> getUserCardInfo(Channel bean);

    @Select("select * from tb_import_task where itype=#{itype,jdbcType=INTEGER} and cuserid=#{cuserid,jdbcType=VARCHAR} and ibankid=#{ibankid,jdbcType=INTEGER} and isend=#{isend,jdbcType=INTEGER} and caccountname=#{caccountname,jdbcType=VARCHAR}")
    ImportTaskDto queryTaskByUser(@Param("itype") int itype, @Param("cuserid") String cuserid, @Param("ibankid") int ibankid, @Param("isend") int isend, @Param("caccountname") String caccountname);


    @Select("select seq_import_task.nextval itaskid from dual")
    Integer querySeqIdFormTask();


    @Insert("insert into tb_import_task(ITASKID,CUSERID,IBANKID,ITYPE,ISTATE,CDESC,CURLPARAMS,CACCOUNTNAME,ISEND,ISAUTO) values(#{itaskid,jdbcType=INTEGER}," +
            "#{cuserid,jdbcType=VARCHAR},#{ibankid,jdbcType=INTEGER},#{itype,jdbcType=INTEGER},#{istate,jdbcType=INTEGER},#{cdesc,jdbcType=VARCHAR},#{curlparams,jdbcType=VARCHAR}," +
            "#{caccountname,jdbcType=VARCHAR},#{isend,jdbcType=INTEGER},#{isauto,jdbcType=INTEGER})")
    int createTask(ImportTaskDto taskDto);

    @Select("select seq_repayment_order.nextval from dual")
    Integer getSeqRepayOrderNextVal();

    int createRepaymentOrder(RepaymentOrderDto rod);

    @Select("select * from TB_REPAYMENT_ORDER where CPARTNER_ID=#{cpartner_id,jdbcType=VARCHAR}")
    RepaymentOrderDto getRepaymentOrderByPartnerId(String partnerId);

    int updateRepayOrder(RepaymentOrderDto rod);

    @Select("select seq_consume_task.nextval itaskid from dual")
    Integer querySeqIdFromConsumeTask();

    @Select("select * from tb_consume_task where itype=#{itype,jdbcType=INTEGER} and cuserid=#{cuserid,jdbcType=VARCHAR} and ibankid=#{ibankid,jdbcType=INTEGER} and isend=#{isend,jdbcType=INTEGER} and caccountname=#{caccountname,jdbcType=VARCHAR}")
    ConsumeTaskDto queryConsumeTaskByUser(@Param("itype") String itype, @Param("cuserid") String cuserid, @Param("ibankid") String ibankid, @Param("isend") String isend, @Param("caccountname") String caccountname);

    @Insert("insert into tb_consume_task(ITASKID,CUSERID,IBANKID,ITYPE,ISTATE,CDESC,CURLPARAMS,CACCOUNTNAME,ISEND) values(#{itaskid,jdbcType=INTEGER}," +
            "#{cuserid,jdbcType=VARCHAR},#{ibankid,jdbcType=INTEGER},#{itype,jdbcType=INTEGER},#{istate,jdbcType=INTEGER},#{cdesc,jdbcType=VARCHAR},#{curlparams,jdbcType=VARCHAR}," +
            "#{caccountname,jdbcType=VARCHAR},#{isend,jdbcType=INTEGER})")
    int createConsumeTask(ConsumeTaskDto taskDto);

    @Update("update tb_consume_task set isend=#{isend,jdbcType=INTEGER},istate=#{istate,jdbcType=INTEGER},cdesc=#{cdesc,jdbcType=VARCHAR} where itaskid=#{itaskid,jdbcType=INTEGER}")
    int updateConsumeTask(ConsumeTaskDto taskDto);

    @Select("select * from tb_bill_month where ibillid=#{ibillid,jdbcType=INTEGER} and isbill=1 order by cmonth desc")
    List<BillMonthDto> getBillMonthByBillId(String ibillid);

    @Select("select * from tb_bill_month where ibillid=#{ibillid,jdbcType=INTEGER} order by cmonth desc")
    List<BillMonthDto> getAllBillMonthByBillId(String ibillid);

    @Select("SELECT * FROM TB_BANK WHERE IBANKID=#{bankId,jdbcType=INTEGER}")
    BankDto getBankById(String bankId);

    @Select("select * from tb_bill_detail where imonthid=#{imonthid,jdbcType=INTEGER} order by ctradedate desc")
    List<BillDetailDto> getLatestBillDetailByMonthId(String imonthid);

    @Select("select * from(select * from tb_bill_month where ibillid=#{ibillid,jdbcType=INTEGER} order by cmonth desc) where rownum between 1 and 6")
    List<BillMonthDto> getAllBillMonths(String ibillid);

    @Select("select * from tb_bill_detail where imonthid=#{imonthid,jdbcType=INTEGER} order by ctradedate desc")
    List<BillDetailDto> getBillDetailsByMonthId(String imonthid);

    @Select("select ccosttypename from tb_bank_costtype where icostid = #{icostid,jdbcType=INTEGER}")
    String getCostTypeName(String icostid);

    @Update("update tb_bank_bill t set t.isdel=1 where t.ibillid=#{billId,jdbcType=INTEGER}")
    int deleteBankBill(String billId);

    //    @Update("update tb_bank_bill tbb set tbb.irepayment = #{1,jdbcType=INTEGER} where tbb.ibillid = #{0,jdbcType=INTEGER}")
    @Update("update tb_bill_month set IREPAYMENT=#{1,jdbcType=VARCHAR} where cmonth=(select max(b.cmonth) " +
            "from tb_bill_month b where b.isbill =1 and b.ibillid=#{0,jdbcType=VARCHAR}) and ibillid=#{0,jdbcType=VARCHAR}")
    int changeRepaymentStatus(String billId, String repaymentStatus);

    @Select("select sum(decode(icurrency,1,abs(" + EXCHANGE_FATE_US_CHINA + " * imoney),abs(imoney))) totalconsume from tb_bill_detail where imonthid=#{0,jdbcType=INTEGER} and itype=0")
    String calcTotalConsume(String imonthid);

    @Select("select sum(decode(icurrency,1,abs(" + EXCHANGE_FATE_US_CHINA + " * imoney),abs(imoney))) typeBill ,max(icosttype) costId,ccosttypename from tb_bill_detail td left join tb_bank_costtype tc on td.icosttype = tc.icostid where imonthid=#{0,jdbcType=INTEGER} group by ccosttypename order by typeBill desc")
    List<Map<String, Object>> queryConsumeByCostType(String imonthid);

    @Select("select * from tb_bank_bill where ibillid=#{billId,jdbcType=INTEGER} and cuserid=#{cuserId,jdbcType=VARCHAR} and iswebormail=1")
    BankBillDto queryBillByUserIdBillId(Channel bean);

    @Select("select * from tb_import_task where itype=#{0,jdbcType=INTEGER} and cuserid=#{1,jdbcType=VARCHAR} and ibankid=#{2,jdbcType=INTEGER} and (isend=#{3,jdbcType=INTEGER} or isend=#{4,jdbcType=INTEGER}) and caccountname=#{5,jdbcType=VARCHAR}")
    ImportTaskDto queryTaskByUser2(String type, String cuserId, String bankId, int isSend, int isSend2, String accountname);

    @Select("select * from tb_import_task where itaskid=#{0,jdbcType=INTEGER}")
    ImportTaskDto queryTaskById(String taskid);

    @Select("select * from tb_import_task where itype=#{0,jdbcType=INTEGER} and cuserid=#{1,jdbcType=VARCHAR}  and isend in (#{2,jdbcType=INTEGER},#{3,jdbcType=INTEGER}) and caccountname=#{4,jdbcType=VARCHAR}")
    ImportTaskDto queryTaskByUser3(String type, String cuserId, int isend, int isend2, String accountname);

    @Select("select t.ibankid,t1.cbankname,t.icheapnum,t.ipeoplenum,t.cardaddr from tb_handle_credit t left join tb_bank t1 on t.ibankid=t1.ibankid where t.cardaddr is not null order by t.ipeoplenum desc")
    List<HashMap<String, Object>> queryCreditHandle();

    @Select("select count(*) as num from tb_bank_point t where t.ibankid=#{0,jdbcType=INTEGER} and t.icityid=#{1,jdbcType=INTEGER} and t.itype=1 and t.istatus=1")
    int sumBankPoint(String bankId, String cityId);

    @Select("select t.clng \"clng\",t.clat \"clat\" from tb_area t where t.iareaid={0,jdbcType=VARCHAR}")
    List<Map<String, Object>> queryLatLngByAreaId(String cityId);

    @Select("select distinct * from (select t.cnetpointname \"cnetpointname\", t.caddr \"caddr\", t.cphone \"cphone\",t.clng \"clng\"," +
            "t.clat \"clat\", trunc(getdistance(t.clat,t.clng,#{0,jdbcType=VARCHAR},#{1,jdbcType=VARCHAR})) as  \"distance\" from tb_bank_point t where t.ibankid=#{2,jdbcType=VARCHAR} " +
            "and t.icityid=#{3,jdbcType=VARCHAR} and t.itype=1 and t.istatus=1 order by \"distance\" asc ) ")
    List<HashMap<String, Object>> queryBankPoint(String clat, String clng, String bankId, String cityId);



    @Select("select count(1) nums,cdesc,ibankid from tb_import_task where itype <=1 and istate!=4 and cadddate>=to_date(#{0,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') and cadddate<=to_date(#{1,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') group by cdesc,ibankid  order by ibankid")
    List<HashMap<String, Object>> queryBankErrorByDesc(String sd, String ed);

    @Select("select count(1) nums,cdesc,ibankid from tb_consume_task where itype <=1 and istate!=1 and cadddate>=to_date(#{0,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') and cadddate<=to_date(#{1,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') group by cdesc,ibankid  order by ibankid")
    List<HashMap<String,Object>> queryBankErrorByDescConsume(String sd,String ed);

    @Select("select count(1) nums,ibankid from tb_import_task where itype <=1 and istate!=4 and cadddate>=to_date(#{0,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') and cadddate<=to_date(#{1,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') group by ibankid  order by ibankid")
    List<HashMap<String, Object>> queryBankErrorByBankid(String sd, String ed);

    @Select("select count(1) nums,ibankid from tb_consume_task where itype <=1 and istate!=1 and cadddate>=to_date(#{0,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') and cadddate<=to_date(#{1,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') group by ibankid  order by ibankid")
    List<HashMap<String,Object>> queryBankErrorByBankidConsume(String sd,String ed);

    @Select("select count(1) nums,ibankid from tb_import_task where itype <=1 and istate=4 and cadddate>=to_date(#{0,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') and cadddate<=to_date(#{1,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') group by ibankid  order by ibankid")
    List<HashMap<String, Object>> queryBankSuccessByBankid(String sd, String ed);

    @Select("select count(1) nums,ibankid from tb_consume_task where itype <=1 and istate=1 and cadddate>=to_date(#{0,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') and cadddate<=to_date(#{1,jdbcType=VARCHAR},'yyyy-mm-dd hh24:mi:ss') group by ibankid  order by ibankid")
    List<HashMap<String,Object>> queryBankSuccessByBankidConsume(String sd,String ed);


    @Insert("insert into tb_billcount_day (CCOUNTDAY, IGFS, IGFE, IZXS, IZXE, IGDS, IGDE, INYS, INYE, IHQS, IHQE, IPAS, IPAE, IHXS, IHXE, IPFS, IPFE, IXYS, IXYE," +
            " IMSS, IMSE, IJSS, IJSE, IGSS, IGSE, IZGS, IZGE, IJTS, IJTE, ISHS, ISHE, IZSS, IZSE, IQQS, IQQE, IWY126S, IWY126E, IWY163S, IWY163E) " +
            "values (#{ccountday,jdbcType=VARCHAR}, #{igfs,jdbcType=INTEGER}, #{igfe,jdbcType=INTEGER}, #{izxs,jdbcType=INTEGER}, #{izxe,jdbcType=INTEGER}, " +
            "#{igds,jdbcType=INTEGER}, #{igde,jdbcType=INTEGER}, #{inys,jdbcType=INTEGER}, #{inye,jdbcType=INTEGER}, #{ihqs,jdbcType=INTEGER}, #{ihqe,jdbcType=INTEGER}, " +
            "#{ipas,jdbcType=INTEGER}, #{ipae,jdbcType=INTEGER}, #{ihxs,jdbcType=INTEGER}, #{ihxe,jdbcType=INTEGER}, #{ipfs,jdbcType=INTEGER}, #{ipfe,jdbcType=INTEGER}, " +
            "#{ixys,jdbcType=INTEGER}, #{ixye,jdbcType=INTEGER}, #{imss,jdbcType=INTEGER}, #{imse,jdbcType=INTEGER}, #{ijss,jdbcType=INTEGER}, #{ijse,jdbcType=INTEGER}, " +
            "#{igss,jdbcType=INTEGER}, #{igse,jdbcType=INTEGER}, #{izgs,jdbcType=INTEGER}, #{izge,jdbcType=INTEGER}, #{ijts,jdbcType=INTEGER}, #{ijte,jdbcType=INTEGER}, " +
            "#{ishs,jdbcType=INTEGER}, #{ishe,jdbcType=INTEGER}, #{izss,jdbcType=INTEGER}, #{izse,jdbcType=INTEGER}, #{iqqs,jdbcType=INTEGER}, #{iqqe,jdbcType=INTEGER}, " +
            "#{iwy126s,jdbcType=INTEGER}, #{iwy126e,jdbcType=INTEGER}, #{iwy163s,jdbcType=INTEGER}, #{iwy163e,jdbcType=INTEGER})")
    int saveBillCountDay(BillCountDayDto dto);

    @Insert("insert into tb_billdetailcount_day (CCOUNTDAY,IBANKID,CDES,IFAILCOUNT,ITYPE)values(#{ccountday,jdbcType=VARCHAR},#{ibankid,jdbcType=INTEGER}" +
            ",#{cdesc,jdbcType=VARCHAR},#{ifailcount,jdbcType=INTEGER},#{itype,jdbcType=INTEGER})")
    int saveBillDetailCountDay(BillDetailCountDayDto dto);

    @Delete("delete from tb_billdetailcount_day where ccountday=#{0,jdbcType=VARCHAR}")
    int deleteBillCountDetail(String ccountday);

    @Delete("delete from tb_billcount_day where ccountday=#{0,jdbcType=VARCHAR}")
    int deleteBillCount(String ccountday);


    //根据银行id,查询一段时间内未做更新的网银账户总数量
    @Select("select count(*) from tb_bank_bill where iswebormail=0 and iskeep=0 and ibankid=#{0,jdbcType=INTEGER} " +
            "and ((cupdate is not null and sysdate-cupdate>=#{1,jdbcType=INTEGER}) " +
            "or (cupdate is null and sysdate-cadddate>=#{1,jdbcType=INTEGER}))" +
            "and icard4num not in(select caccountname from tb_import_task where ibankid=#{0,jdbcType=INTEGER} and itype=1 and istate=3 and isauto=1 and sysdate-cadddate<=1)")
    long queryCountNoUpdateTimer(String ibankid, String time);

    //根据银行id,分页查询一段时间内未做更新操作的网银账户卡信息
    @Select(" select * from (select A.*, rownum RN from (select * from tb_bank_bill where iswebormail=0 and iskeep=0" +
            " and ibankid=#{0,jdbcType=INTEGER} and ((cupdate is not null and sysdate-cupdate>=#{1,jdbcType=INTEGER})" +
            " or (cupdate is null and sysdate-cadddate>=#{1,jdbcType=INTEGER}))" +
            "and icard4num not in(select caccountname from tb_import_task where ibankid=#{0,jdbcType=INTEGER} and itype=1 and istate=3 and isauto=1 and sysdate-cadddate<=1)" +
            ") A " +
            " where rownum <=#{3,jdbcType=INTEGER}) where RN >=#{2,jdbcType=INTEGER}")
    List<BankBillDto> queryBillNoUpdateTimer(String ibankid, String time, int start, int end);

    //查询当前正在执行的任务数量
    @Select("select count(itaskid) from tb_import_task where isend=0")
    int queryTaskNumOfRun();

    @Select("select * from (select  a.irepayment from tb_bill_month  a where a.isbill =1 and a.ibillid=#{0,jdbcType=INTEGER} order by a.cmonth desc) t where rownum=1")
    Integer queryIrepaymentOfMaxMonthByBillid(int billid);

    //添加BankPoint到数据库中
    int addBankPointList(@Param("bankPoints") List<BankPoint> bankPoints);

    //查询城市列表
    List<AreaDto> queryCity();

    //2017/2/20 Linxingyu 修改
    //查询指定银行
    List<CreditHandleDto> queryCreditHandle2(String bankId);

    //查询所有银行
    List<CreditHandleDto> queryCreditHandle3();

    //查询开卡进度地址
    List<CreditHandleDto> queryProgress();

    //查询用户关注银行
    List<CreditHandleDto> queryUserCollectionBank(String cuserId);

    //查询用户为关注银行
    List<CreditHandleDto> queryUserOtherBank(String cuserId);

    //按日期查询账单导入
    @Select("select * from TB_BILLCOUNT_DAY where CCOUNTDAY = #{ccountday,jdbcType=VARCHAR}")
    BillCountDayDto queryBillCountDay(@Param("ccountday")String ccountday);
}

