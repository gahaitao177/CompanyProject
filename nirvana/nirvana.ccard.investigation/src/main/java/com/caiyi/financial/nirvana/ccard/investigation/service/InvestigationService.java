package com.caiyi.financial.nirvana.ccard.investigation.service;

import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReferenceDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReportDetailsDto;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.InvestigationMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Mario on 2016/7/19 0019.
 */
@Service
public class InvestigationService extends AbstractService {

    @Autowired
    InvestigationMapper mapper;

    /**
     * 根据userId查询征信报告信息
     *
     * @param cuserId
     * @return
     */
    public CreditReferenceDto queryCreditRefDto(String cuserId) {
        try {
            return mapper.queryCreditRefDtoById(cuserId);
        } catch (Exception e) {
            logger.error("queryCreditRefDtoById 异常",e);
        }
        return null;
    }

    /**
     * 更新重新申请报告的标识
     *
     * @param bean
     */
    public void updateReApplyStatus(Channel bean) {
        try {
            mapper.updateReApplyStatus(bean);
        } catch (Exception e) {
            logger.error("updateReApplyStatus 异常", e);
        }
    }

    /**
     * 查询报告列表
     * select * from tb_credit_reference where cuserid=? and cloginname=?
     *
     * @param bean
     * @return
     */
    public CreditReferenceDto queryCreditRefDto(Channel bean) {
        try {
            return mapper.queryCreditRefDto(bean);
        } catch (Exception e) {
            logger.error("queryCreditRefDto 异常", e);
        }
        return null;
    }

    /**
     * 删除报告详细
     *
     * @param icid
     * @return
     */
    public int deleteReportDetail(long icid) {
        try {
            return mapper.deleteReportDetail(icid);
        } catch (Exception e) {
            logger.error("deleteReportDetail 异常", e);
        }
        return 0;
    }

    /**
     * 获取报告队列Next_Val
     *
     * @return
     */
    public long selectSeqCreditReference() {
        try {
            return mapper.selectSeqCreditReference();
        } catch (Exception e) {
            logger.error("selectSeqCreditReference 异常", e);
        }
        return 0;
    }

    /**
     * 插入新的TB_CR_REPORTDETAILS
     *
     * @param dto
     * @return
     */
    public int insertCreditReportDetail(CreditReportDetailsDto dto) {
        try {
            return mapper.insertCreditReportDetail(dto);
        } catch (Exception e) {
            logger.error("insertCreditReportDetail 异常", e);
        }
        return 0;
    }

    /**
     * 更新报告
     *
     * @param creditReferenceDto
     * @return
     */
    public int updateCreditReference(CreditReferenceDto creditReferenceDto) {
        try {
            return mapper.updateCreditReference(creditReferenceDto);
        } catch (Exception e) {
            logger.error("updateCreditReference 异常", e);
        }
        return 0;
    }

    /**
     * 插入一条新的报告
     *
     * @param creditReferenceDto
     * @return
     */
    public int insertCreditReference(CreditReferenceDto creditReferenceDto) {
        try {
            return mapper.insertCreditReference(creditReferenceDto);
        } catch (Exception e) {
            logger.error("insertCreditReference 异常", e);
        }
        return 0;
    }

    /**
     * 查询用户报告
     *
     * @param bean
     * @return
     */
    public List<CreditReferenceDto> queryUserReference(Channel bean) {
        try {
            return mapper.queryUserReference(bean);
        } catch (Exception e) {
            logger.error("queryUserReference 异常", e);
        }
        return null;
    }


    /**
     * 查询用户报告
     *
     * @param bean
     * @return
     */
    public List<CreditReferenceDto> queryUserReferenceByLoginname(Channel bean) {
        try {
            return mapper.queryUserReferenceByLoginname(bean);
        } catch (Exception e) {
            logger.error("queryUserReferenceByLoginname 异常", e);
        }
        return null;
    }


    /**
     * 根据Id查询报告detail
     *
     * @param icrid
     * @return
     */
    public List<CreditReportDetailsDto> queryCreditReportDetailById(long icrid) {
        try {
            return mapper.queryCreditReportDetailById(icrid);
        } catch (Exception e) {
            logger.error("queryCreditReportDetailById 异常", e);
        }
        return null;
    }


    /**
     * 存储用户征信账号
     * @param bean
     * @return
     */
    public int saveZhengXinAccount(CreditAccountDto bean){
        try {
            return mapper.saveZhengXinAccount(bean);
        }catch (Exception e){
            logger.error("saveZhengXinAccount 异常", e);
        }
        return 0;
    }

    /**
     * 更新征信用户状态
     * @param bean
     * @return
     */
    public int updateAccount(CreditAccountDto bean){
        try {
            return mapper.updateAccount(bean);
        }catch (Exception e){
            logger.error("updateAccount 异常", e);
        }
        return 0;

    }


    /**
     * 查询用户征信操作状态
     *
     * @param bean
     * @return
     */
    public CreditAccountDto queryZhengxinAccountByCuserId(Channel bean) {
        try {
            return mapper.queryZhengxinAccountByCuserId(bean);
        } catch (Exception e) {
            logger.error("queryZhengxinAccountByCuserId 异常", e);
        }
        return null;
    }


    public CreditAccountDto queryZhengxinAccountByCuserIdAndLoginname(Channel bean) {
        try {
            return mapper.queryZhengxinAccountByCuserIdAndLoginname(bean);
        } catch (Exception e) {
            logger.error("queryZhengxinAccountByCuserIdAndLoginname 异常", e);
        }
        return null;
    }


    /**
     * 更新征信用户状态
     * @param cuserId
     * @return int
     */
    public int resettingDate(String cuserId){
        try {
            return mapper.resettingDate(cuserId);
        }catch (Exception e){
            logger.error("resettingDate 异常", e);
        }
        return 0;

    }



}
