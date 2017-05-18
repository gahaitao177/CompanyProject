package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.caiyi.financial.nirvana.ccard.bill.bank.service.BillService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by Linxingyu on 2016/12/15.
 * 账单流水、消费类型接口
 */
@Bolt(boltId = "BillConsumeBolt", parallelismHint = 1, numTasks = 1)
public class BillConsumeBolt extends BaseBolt {

    @Autowired
    private BillService billService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        logger.info("---------------------billConsume _prepare");
    }

    /**
     * 账单流水
     *
     * @return
     */
    @BoltController
    public BoltResult queryBillStream(Channel channel) {
        logger.info("------BillConsumeBolt-----queryBillStream");
        return billService.queryBillStream(channel);
    }

    /**
     * 消费类型分析
     */
    @BoltController
    public BoltResult queryConsumeType(Channel channel) {
        logger.info("------BillConsumeBolt-----queryConsumeType");
            return billService.queryConsumeType(channel);
    }
}
