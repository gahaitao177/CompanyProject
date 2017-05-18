package com.caiyi.financial.nirvana.ccard.bill.bank.mapper;

import com.caiyi.financial.nirvana.ccard.bill.bean.ForeheadRecord;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankApplyTypeDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.CardQuotaInfoDto;
import com.caiyi.financial.nirvana.ccard.bill.dto.QuotaReportDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * Created by Linxingyu on 2017/1/22.
 */
public interface CardQuotaMapper extends BaseDao {

    //查询提额详情需要的信息
    List<CardQuotaInfoDto> queryCardQuotaInfos(Integer billId);

    //查询卡的总额度
    Double queryTotalQuota(Integer billId);

    //查询额度信息
    Map<String, Object> queryQuotaInfo(Integer billId);

    //查询用户所有卡
    List<BankBillDto> queryUserCards(String cuserId);

    //根据billId查询用户卡信
    BankBillDto queryUserCard(Integer billId);

    //根据银行id查询申请方式信息
    BankApplyTypeDto queryBankInfoById(Integer bankId);

    //查询最近7个月的消费流水
    List<QuotaReportDto> queryLatelyBillNew(Integer billId);

    //信用卡提额记录插入
    int saveForeheadRecord(ForeheadRecord forRec);

    //提额成功后更新bankbill表额度及提额信息
    int updateByPrimaryKeySelective(BankBillDto bankBillDto);

    List<Integer> queryPromoteBankList();
}
