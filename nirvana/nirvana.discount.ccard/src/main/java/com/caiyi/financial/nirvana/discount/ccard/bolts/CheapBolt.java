package com.caiyi.financial.nirvana.discount.ccard.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Store;
import com.caiyi.financial.nirvana.discount.ccard.dto.AreaDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.CheapDetailDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.ResultDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.WindowDto;
import com.caiyi.financial.nirvana.discount.ccard.service.CheapService;
import com.caiyi.financial.nirvana.discount.ccard.service.StoreService;
import org.apache.storm.task.TopologyContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heshaohua on 2016/5/3.
 */
@Bolt(boltId = "cheap", parallelismHint = 2, numTasks = 2)
public class CheapBolt extends BaseBolt {

    private CheapService cheapService;
    private StoreService storeService;

    @Override
    protected void _prepare(Map map, TopologyContext topologyContext) {
        cheapService = getBean(CheapService.class);
        storeService = getBean(StoreService.class);
    }

    @BoltController
    public List<AreaDto> query_area(Cheap cheap){
        logger.info("---------------------CheapBolt query_area");
        return cheapService.query_area(cheap);
    }

    @BoltController
    public List<Store> storeList(Store store){
        return storeService.storeList(store);
    }

    @BoltController
    public CheapDetailDto cheap(Cheap cheap){
        return storeService.cheap(cheap);
    }


    @BoltController
    public Map<String,String> topicClick(String topicId){
        logger.info("---------------------CheapBolt topicClick--");
        Map<String,String> map=new HashMap<String,String>();
        map.put("count",cheapService.topicClick(topicId)+"");
        return map;
    }

    @BoltController
    public WindowDto query_searchKeyWorlds(){
        logger.info("---------------------CheapBolt query_searchKeyWorlds----");
        return cheapService.query_searchKeyWorlds();
    }
    @BoltController
    public ResultDto query_result(Cheap cheap){
        logger.info("---------------------CheapBolt query_result----");
        return cheapService.query_result(cheap);
    }

}
