package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.caiyi.financial.nirvana.ccard.bill.bank.service.CardQuotaIncrService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.ForeheadRecord;
import com.caiyi.financial.nirvana.ccard.bill.dto.BankBillDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Linxingyu on 2017/2/6.
 * 提额
 */
@Bolt(boltId = "CardQuotaBolt", parallelismHint = 1, numTasks = 1)
public class CardQuotaBolt extends BaseBolt {

    @Autowired
    private CardQuotaIncrService cardService;

    /**
     * 提额首页
     *
     * @param channel
     * @return
     */
    @BoltController
    public BoltResult raiseQuotaIndex(Channel channel) {
        logger.info("|提额首页|raiseQuotaIndex");
        return cardService.raiseQuotaIndex(channel.getCuserId());
    }

    /**
     * 提额详情页
     *
     * @param channel
     * @return
     */
    @BoltController
    public BoltResult getRaiseQuotaDetail(Channel channel) {
        logger.info("|提额详情页|getRaiseQuotaDetail");
            return cardService.raiseQuotaDetail(Integer.valueOf(channel.getBillId()));
    }

    /**
     * 诊断报告页
     *
     * @param channel
     * @return
     */
    @BoltController
    public BoltResult getRaiseQuotaReport(Channel channel) {
        logger.info("|诊断报告页|getRaiseQuotaReport");
        return cardService.raiseQuotaReport(Integer.valueOf(channel.getBillId()));
    }

    /**
     * 提额历史记录
     *
     * @param foreheadRecord
     * @return
     */
    @BoltController
    public int saveForeheadRecord(ForeheadRecord foreheadRecord) {
        logger.info("——提额历史记录——saveForeheadRecord");
        return cardService.saveForeheadRecord(foreheadRecord);
    }

    /**
     * 提额成功更新bankbill表数据
     *
     * @param bankBillDto
     * @return
     */
    @BoltController
    public int updateByPrimaryKeySelective(BankBillDto bankBillDto) {
        logger.info("——提额成功更新bankbill表数据——saveForeheadRecord");
        return cardService.updateByPrimaryKeySelective(bankBillDto);
    }
}
