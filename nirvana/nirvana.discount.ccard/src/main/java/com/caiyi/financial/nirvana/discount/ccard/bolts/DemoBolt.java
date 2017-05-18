package com.caiyi.financial.nirvana.discount.ccard.bolts;

import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.annotation.BoltParam;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.ccard.bean.Demo;
import com.caiyi.financial.nirvana.discount.ccard.service.DemoService;
import org.apache.storm.task.TopologyContext;

import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/4/22.
 */
public class DemoBolt extends BaseBolt {

    public DemoService demoService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        demoService = getBean(DemoService.class);
    }

    @BoltController
    public List<Map<String,Object>> select1(){
        logger.info("---------------------进入DemoBolt select1");
        return demoService.select();
    }
    @BoltController
    public List<Demo> select2(){
        logger.info("---------------------进入DemoBolt select2");
        return demoService.select2();
    }
    @BoltController
    public List<Demo> select3(){
        logger.info("---------------------进入DemoBolt select3");
        return demoService.select3();
    }
    @BoltController
    public List<Map<String,Object>> select4(){
        logger.info("---------------------进入DemoBolt select4");
        return demoService.select4();
    }
    @BoltController
    public int  add(@BoltParam("t1")String t1,@BoltParam("t2")String t2)throws Exception{
        logger.info("---------------------进入DemoBolt add" + ";t1：" + t1 + ";t2:" + t2);
        return demoService.addTest(t1, t2);
    }

    @BoltController
    public int add2(Demo demo)throws Exception{
        logger.info("---------------------进入DemoBolt add2" + ";" + demo);
        return demoService.addTest(demo.getClientwritetime(),demo.getDbwritetime());
    }

}
