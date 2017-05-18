package com.caiyi.financial.nirvana.ccard.investigation.mapper;

import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReferenceDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReportDetailsDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by terry on 2016/5/25.
 */
public interface InvestigationMapper extends BaseDao {

    @Select("select * from tb_credit_reference where cuserid=#{cuserId,jdbcType=VARCHAR} and rownum < 2")
    CreditReferenceDto queryCreditRefDtoById(String cuserId);

    @Update("update tb_credit_reference set isreapply=1 where cloginname=#{loginname,jdbcType=VARCHAR} and cuserid=#{cuserId,jdbcType=VARCHAR}")
    void updateReApplyStatus(Channel bean);

    @Select("select * from tb_credit_reference where cuserid=#{cuserId,jdbcType=VARCHAR} and cloginname=#{loginname,jdbcType=VARCHAR}")
    CreditReferenceDto queryCreditRefDto(Channel bean);

    @Delete("delete tb_cr_reportdetails where icrid=#{icid,jdbcType=INTEGER}")
    int deleteReportDetail(long icid);

    @Select("select SEQ_CREDIT_REFERENCE.Nextval from dual")
    long selectSeqCreditReference();

    @Insert("INSERT INTO TB_CR_REPORTDETAILS( ICRID, CDETAILS, ITYPE)" +
            "VALUES( #{icrid,jdbcType=INTEGER}, #{cdetails,jdbcType=VARCHAR}, #{itype,jdbcType=INTEGER})")
    int insertCreditReportDetail(CreditReportDetailsDto dto);

    @Update("UPDATE TB_CREDIT_REFERENCE SET CREPORTNO= #{creportno,jdbcType=VARCHAR}," +
            "CREPORTDATE= #{creportdate,jdbcType=TIMESTAMP},ITOTALDEFAULT= #{itotaldefault,jdbcType=INTEGER},CREALNAME= #{crealname,jdbcType=VARCHAR}," +
            "CMARSTATUS= #{cmarstatus,jdbcType=VARCHAR},CIDTYPE= #{cidtype,jdbcType=VARCHAR},CIDCARD= #{cidcard,jdbcType=VARCHAR}," +
            "ICREDITCC= #{icreditcc,jdbcType=INTEGER},ICREDITCU= #{icreditcu,jdbcType=INTEGER},ICREDITCO= #{icreditco,jdbcType=INTEGER}," +
            "ICREDITCSO= #{icreditcso,jdbcType=INTEGER},ICREDITCG= #{icreditcg,jdbcType=INTEGER},ILOANC= #{iloanc,jdbcType=INTEGER}," +
            "ILOANU= #{iloanu,jdbcType=INTEGER},ILOANO= #{iloano,jdbcType=INTEGER},ILOANSO= #{iloanso,jdbcType=INTEGER}," +
            "ILOANG= #{iloang,jdbcType=INTEGER},IOT= #{iot,jdbcType=INTEGER},ICJ= #{icj,jdbcType=INTEGER}," +
            "ICE= #{ice,jdbcType=INTEGER},IAP= #{iap,jdbcType=INTEGER},ITA= #{ita,jdbcType=INTEGER}," +
            "IMI= #{imi,jdbcType=INTEGER},IPI= #{ipi,jdbcType=INTEGER},ISOBTAIN= #{isobtain,jdbcType=INTEGER}," +
            "ISAPPLY= #{isapply,jdbcType=INTEGER},ISREAPPLY= #{isreapply,jdbcType=INTEGER} WHERE ICRID= #{icrid,jdbcType=INTEGER}")
    int updateCreditReference(CreditReferenceDto creditReferenceDto);

    @Insert("INSERT INTO TB_CREDIT_REFERENCE( ICRID, CLOGINNAME, CUSERID, CREPORTNO, CREPORTDATE, ITOTALDEFAULT, CREALNAME," +
            " CMARSTATUS, CIDTYPE, CIDCARD, ICREDITCC, ICREDITCU, ICREDITCO," +
            " ICREDITCSO, ICREDITCG, ILOANC, ILOANU, ILOANO, ILOANSO," +
            " ILOANG, IOT, ICJ, ICE, IAP, ITA," +
            " IMI, IPI, ISOBTAIN, CADDDATE, ISAPPLY, CLASTAPPLYDATE," +
            " ISREAPPLY, ITYPE)" +
            "VALUES( #{icrid,jdbcType=INTEGER}, #{cloginname,jdbcType=VARCHAR}, #{cuserid,jdbcType=VARCHAR}, #{creportno,jdbcType=VARCHAR}, #{creportdate,jdbcType=TIMESTAMP}, #{itotaldefault,jdbcType=INTEGER}," +
            " #{crealname,jdbcType=VARCHAR}, #{cmarstatus,jdbcType=VARCHAR}, #{cidtype,jdbcType=VARCHAR}, #{cidcard,jdbcType=VARCHAR}, #{icreditcc,jdbcType=INTEGER}," +
            " #{icreditcu,jdbcType=INTEGER}, #{icreditco,jdbcType=INTEGER}, #{icreditcso,jdbcType=INTEGER}, #{icreditcg,jdbcType=INTEGER}, #{iloanc,jdbcType=INTEGER}," +
            " #{iloanu,jdbcType=INTEGER}, #{iloano,jdbcType=INTEGER}, #{iloanso,jdbcType=INTEGER}, #{iloang,jdbcType=INTEGER}, #{iot,jdbcType=INTEGER}," +
            " #{icj,jdbcType=INTEGER}, #{ice,jdbcType=INTEGER}, #{iap,jdbcType=INTEGER}, #{ita,jdbcType=INTEGER}, #{imi,jdbcType=INTEGER}," +
            " #{ipi,jdbcType=INTEGER}, #{isobtain,jdbcType=INTEGER}, SYSDATE, #{isapply,jdbcType=INTEGER}, #{clastapplydate,jdbcType=TIMESTAMP}," +
            " #{isreapply,jdbcType=INTEGER}, #{itype,jdbcType=INTEGER})")
    int insertCreditReference(CreditReferenceDto creditReferenceDto);

    @Select("select * from TB_CREDIT_REFERENCE where cuserid=#{cuserId,jdbcType=VARCHAR} and isobtain=1 order by cadddate desc")
    List<CreditReferenceDto> queryUserReference(Channel bean);

    @Select("select * from TB_CREDIT_REFERENCE where cuserid=#{cuserId,jdbcType=VARCHAR} and cloginname=#{loginname,jdbcType=VARCHAR} and isobtain=1 order by cadddate desc")
    List<CreditReferenceDto> queryUserReferenceByLoginname(Channel bean);


    @Select("select * from tb_cr_reportdetails where icrid=#{icrid,jdbcType=INTEGER} order by itype")
    List<CreditReportDetailsDto> queryCreditReportDetailById(long icrid);


    @Insert("INSERT INTO TB_ZX_ACCOUNT(CUSERID,CLOGINNAME,CLOGINPWD,CSTATUS)VALUES(#{cuserId,jdbcType=VARCHAR},#{cloginname,jdbcType=VARCHAR}," +
            "#{cloginpwd,jdbcType=VARCHAR},#{cstatus,jdbcType=VARCHAR})")
    int saveZhengXinAccount(CreditAccountDto bean);

    int updateAccount(CreditAccountDto bean);

    @Select("select * from tb_zx_account where cuserid=#{cuserId,jdbcType=VARCHAR}")
    CreditAccountDto queryZhengxinAccountByCuserId(Channel bean);


    @Select("select * from tb_zx_account where cuserid=#{cuserId,jdbcType=VARCHAR} and cloginname=#{loginname,jdbcType=VARCHAR}")
    CreditAccountDto queryZhengxinAccountByCuserIdAndLoginname(Channel bean);

    @Update("update tb_zx_account set capplydate=null,cvprotdate=null where cuserid=#{cuserId,jdbcType=VARCHAR}")
    int resettingDate(String cuserId);


}

