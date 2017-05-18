package com.caiyi.financial.nirvana.ccard.investigation.mapper;

import com.caiyi.financial.nirvana.ccard.investigation.dto.*;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Linxingyu on 2017/3/16
 */
public interface YouyuCreditMapper extends BaseDao {

    //查询算分需要的所有信息
    CreditScoreNewDto queryCreditInfoByUserId(String cuserId);

    //查询征信明细
    List<CreditReportDetailsDto> queryCreditReportDetails(int icrid);

    List<CreditScoreNewDto> queryBillsByCondition(@Param("cuserId") String cuserId, @Param("name") String name);

    List<BillMonthInfoDto> queryMonthInfos(Integer billId);

    List<QuotaReportDto> queryLatelyBillNew(Integer card);

    Map<String,Object> queryEducationInfo(String cuserId);

}
