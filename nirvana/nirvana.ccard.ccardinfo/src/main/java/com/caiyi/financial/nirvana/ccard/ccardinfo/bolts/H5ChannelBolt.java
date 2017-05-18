package com.caiyi.financial.nirvana.ccard.ccardinfo.bolts;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelBankBean;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelCardBean;
import com.caiyi.financial.nirvana.ccard.ccardinfo.service.H5ChannelService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.annotation.BoltParam;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.github.pagehelper.Page;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/6/20.
 */
@Bolt(boltId = "h5ChannelBolt", parallelismHint = 2, numTasks = 2)
public class H5ChannelBolt extends BaseBolt {

    @Autowired
    private H5ChannelService h5ChannelService;



    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
//        h5ChannelService = getBean(H5ChannelService.class);
    }

    @BoltController
    public BoltResult index(@BoltParam("ichannelid")Long ichannelid){
        return h5ChannelService.selectIndex(ichannelid);
    }

    @BoltController
    public List<Map<String,Object>> selectProgressUrl(@BoltParam("ichannelid")Long ichannelid){
        return h5ChannelService.selectProgressUrl(ichannelid);
    }

    @BoltController
    public Page selectCard(H5ChannelCardBean bean){
        return h5ChannelService.selectCard(bean);
    }



    @BoltController
    public BoltResult clickCard(H5ChannelCardBean bean){
        if(h5ChannelService.clickCard(bean)==1){
            return new BoltResult(BoltResult.SUCCESS,"");
        }else {
            return new BoltResult(BoltResult.ERROR,"点击量+!失败");
        }
    }

    @BoltController
    public BoltResult clickBank(H5ChannelBankBean bankBean){
        if(h5ChannelService.clickBank(bankBean)==1){
            return new BoltResult(BoltResult.SUCCESS,"");
        }else{
            return new BoltResult(BoltResult.ERROR,"点击量+!失败");
        }
    }
}
