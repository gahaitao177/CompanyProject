package com.caiyi.financial.nirvana.discount.tools.bolts;


import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.tools.bean.IdfaBean;
import com.caiyi.financial.nirvana.discount.tools.service.AdvertiseService;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

/**
 * Created by zhukai on 2016/12/06.
 *
 */
@Bolt(boltId = "advertisement", parallelismHint = 1, numTasks = 1)
public class AdvertisementBolt extends BaseBolt {
    @Autowired
    AdvertiseService advertiseService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {}

    /**
     *
     * @param idfaBean
     * @return
     */
    @BoltController
    public JSONObject saveAdIdfa(IdfaBean idfaBean){
        logger.info("-------recordIdfa---------");
        return  advertiseService.recordIdfa(idfaBean);
    }


    @BoltController
    public BoltResult queryAppInfo(String source){
        logger.info("-------advertisement-------queryAppInfo---" + source);
        return advertiseService.queryAppInfo(source);
    }
}
