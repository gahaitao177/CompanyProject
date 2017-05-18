package com.caiyi.financial.nirvana.ccard.investigation.mapper;

import com.caiyi.financial.nirvana.ccard.investigation.bean.*;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditCardDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditInvestigationDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditPrivilegeDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditScoreDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by jianghao on 2016/11/29. 征信积分
 */
public interface CreditScoreMapper extends BaseDao {
    //查询征信积分表中数据
    CreditScoreDto queryCreditScore(CreditScoreBean bean);

    //查询信用卡表中信用卡标题
    @Select("SELECT CCC_TITLE FROM TB_ZX_CREDIT_CARD WHERE ICC_ID = #{xykId,jdbcType=INTEGER} AND " +
            "ROWNUM = 1")
    String queryCreditCardTitle(String xykId);

    //查询征信表中征信状态
    @Select("SELECT CCI_STATUS FROM TB_ZX_CREDIT_INVESTIGATION WHERE ICI_ID = #{ZxId,jdbcType=INTEGER}  ")
    String queryZxStatus(Integer ZxId);

    //查询月次积分号
    @Select("SELECT IMS_ID FROM TB_ZX_MONTH_SCORE WHERE ICS_ID = #{creditId,jdbcType=INTEGER} AND " +
            "CMS_MONTH = TO_CHAR(SYSDATE, 'YYYYMM' )")
    String queryMonthScoreId(CreditScoreBean bean);

    //插入月次积分
    int insertMonthScore(CreditScoreBean bean);
    //更新月次积分
    int updateMonthScore(CreditScoreBean bean);
    //更新流水
    int insertCreditScoreFlow(CreditScoreBean bean);

    //查询用户积分排名
//    @Select("select * from (select cs.ics_id  creditId, rank() over (order by cs.ics_score desc) rankNum ," +
//            " cs.ccs_userid userId ,cs.ics_score creditScores ,cs.ici_id zxId,cs.icc_id xykId ,cs.igjj_id gjjId, " +
//            " cs.isi_id sbId from tb_zx_credit_score  cs where cs.ccs_status=1) tmp " +
//            " where tmp.userId=#{userId,jdbcType=VARCHAR}")
    @Select("select (select count(*) from credit.tb_zx_credit_score where ics_score>=t.ics_score) as rankNum ," +
            " t.ics_id creditId ,t.ccs_userid userId ,t.ics_score creditScores ,t.ici_id zxId,t.icc_id xykId ,t.igjj_id gjjId," +
            " t.isi_id sbId  from credit.tb_zx_credit_score t  where t.ccs_userid=#{userId,jdbcType=VARCHAR}")
    CreditScoreDto queryUserRank(@Param("userId") String userId);

    //查询积分对应的月份列表 通过积分表里的id
    @Select("select * from (" +
            "select ms.ims_id monthId,ms.ics_id creditId ,ms.cms_month month ,ms.ims_score creditScores " +
            "from tb_zx_month_score ms where ms.ics_id=#{creditId,jdbcType=INTEGER} order by ms.cms_month asc" +
            ") where rownum<=6")
    List<CreditScoreDto> queryMonthScores(@Param("creditId") int creditId);

     //查询总的人数
    @Select("select count(1) from tb_zx_credit_score where ccs_status=1")
    double queryUserCount();

    //查询等级特权数据
    List<CreditPrivilegeDto> queryCreditPrivilege(int levelCode);

    //查询tb_credit_reference表中征信数据
    CreditInvestigationDto queryCreditInvestigation(CreditScoreBean bean);

    //插入tb_zx_credit_investigation征信表
    int insertCreditInvestigation(CreditInvestigationBean bean);

    //更新tb_zx_credit_investigation征信表
    int updateCreditInvestigation(CreditInvestigationBean bean);

    //查询信用卡数据
    List<CreditCardDto> queryBankBill(CreditScoreBean bean);
    //插入信用卡表
    int insertCreditCard(CreditCardBean creditCardBean);

    //更新信用卡表
    int updateCreditCard(CreditCardBean creditCardBean);

    //插入征信表
    Integer insertProvidentFund(ProvidentFundBean providentFundBean);
    //更新征信表
    Integer updateProvidentFund(ProvidentFundBean providentFundBean);


//    //查询公积金表的序列当前序列号
//    @Select("SELECT  SEQ_ZX_GJJ_ID.NEXTVAL FROM DUAL")
//    Integer queryGjjSeq2();

    //查询公积金表中最后更新月
    @Select("SELECT  CGJJ_LAST_MONTH FROM TB_ZX_GJJ  WHERE IGJJ_ID = #{gjjId,jdbcType=VARCHAR }")
    String queryLastUpdateMonth(CreditScoreDto creditScoreDto);
    //查询公积金表中title
    @Select("SELECT  CGJJ_TITLE FROM TB_ZX_GJJ  WHERE IGJJ_ID = #{gjjId,jdbcType=VARCHAR }")
    String queryGjjTitle(int gjjId);
    //查询学信表中最高学历
    @Select("SELECT  NVL(ICA_EDUCATION_LEVEL,-1) FROM TB_XX_CREDIT_ACCOUNT  WHERE CCA_USERID = #{userId,jdbcType=INTEGER }")
    Integer queryEducationLevel(String userId);
    //插入征信表
    Integer insertSocalInsurance(SocialInsuranceBean socialInsuranceBean);
    //更新征信表
    Integer updateSocalInsurance(SocialInsuranceBean socialInsuranceBean);

//
//    @Select("SELECT  SEQ_ZX_SOCIAL_INSURANCE_ID.NEXTVAL FROM DUAL")
//    Integer querySbSeq2();
    //更新tb_zx_Credit_Score征信积分表数据
    int updateCreditScore(CreditScoreBean bean);
    //更新tb_zx_Credit_Score征信积分表信用卡数据
    int insertCreditScore(CreditScoreBean bean);

}
