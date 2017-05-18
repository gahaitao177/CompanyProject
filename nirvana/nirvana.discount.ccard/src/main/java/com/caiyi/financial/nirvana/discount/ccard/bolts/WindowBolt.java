package com.caiyi.financial.nirvana.discount.ccard.bolts;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.ccard.bean.Cheap;
import com.caiyi.financial.nirvana.discount.ccard.bean.Window;
import com.caiyi.financial.nirvana.discount.ccard.dto.WindowDto;
import com.caiyi.financial.nirvana.discount.ccard.service.CheapService;
import com.caiyi.financial.nirvana.discount.ccard.service.WindowService;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by lizhijie on 2016/8/17.
 */
@Bolt(boltId = "window", parallelismHint = 2, numTasks = 4)
public class WindowBolt extends BaseBolt {
    @Autowired
    WindowService windowService;
    @Autowired
    CheapService cheapService;

    @Override
    protected void _prepare(Map map, TopologyContext topologyContext)
    {
    }
    @BoltController
    public JSONObject totalSearchByType(Cheap bean) {
        logger.info("------------WindowBolt totalSearchByType-----");
        return windowService.totalSearchByType(bean);
    }
    @BoltController
    public  JSONObject shuaba(){
        logger.info("------------WindowBolt shuaba-----");
        return windowService.shuaba();
    }
    @BoltController
    public  JSONObject totalSearch(Cheap bean) {
        logger.info("------------WindowBolt totalSearch-----");
        return windowService.totalSearch(bean);
    }



    @BoltController
    public  Map<String,String> saveUserStatistics( Map<String,String> para){
        logger.info("---------------------CheapBolt saveUserStatistics----");
        return  windowService.saveUserStatistics(para);
    }
    @BoltController
    public JSONObject startPage(Window bean) {
        logger.info("---------------------CheapBolt startpage---");
        return  windowService.startpage(bean);
    }


    @BoltController
    public WindowDto qpage(Window window){
        logger.info("---------------------CheapBolt qpage----");
        return cheapService.qpage(window);
    }
    @BoltController
    public Map<String,Object> getTopicInfo(Window window){
        logger.info("---------------------CheapBolt getTopicInfo----");
        return cheapService.getTopicInfo(window);
    }
}
