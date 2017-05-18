package com.caiyi.financial.nirvana.discount.ccard.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.ccard.dto.ResultDtoS;
import com.caiyi.financial.nirvana.discount.ccard.service.NewContanctService;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by lizhijie on 2016/10/25.
 */
@Bolt(boltId = "newContanct", parallelismHint = 2, numTasks = 4)
public class NewContanctBolt extends BaseBolt {
    @Autowired
    private NewContanctService newContanctService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }
    @BoltController
    public ResultDtoS queryThemesAndConstancts(){
        logger.info("请求接口 bolt=newContanct  方法=queryThemesAndConstancts");
        return  newContanctService.queryThemesAndConstancts();
    }
}
