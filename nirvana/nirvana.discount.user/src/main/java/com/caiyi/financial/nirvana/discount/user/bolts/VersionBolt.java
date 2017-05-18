package com.caiyi.financial.nirvana.discount.user.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.user.bean.VersionBean;
import com.caiyi.financial.nirvana.discount.user.service.VersionService;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by wenshiliang on 2016/9/12.
 */
@Bolt(boltId = "VersionBolt", parallelismHint = 1, numTasks = 1)
public class VersionBolt extends BaseBolt {

    @Autowired
    private VersionService versionService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }

    @BoltController
    public BoltResult queryVersion(VersionBean bean){
        return new BoltResult(BoltResult.SUCCESS,"查询成功",versionService.queryVersion(bean));
    }
}
