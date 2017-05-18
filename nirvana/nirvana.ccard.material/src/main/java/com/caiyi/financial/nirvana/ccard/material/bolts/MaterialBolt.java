package com.caiyi.financial.nirvana.ccard.material.bolts;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.bean.MaterialModel;
import com.caiyi.financial.nirvana.ccard.material.service.MaterialService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/13.
 */
@Bolt(boltId = "material", parallelismHint = 1, numTasks = 1)
public class MaterialBolt extends BaseBolt {
    @Autowired
    private MaterialService materialService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context)
    {
//        materialService=getBean(MaterialService.class);
    }
    @BoltController
    public Map<String,Object> filterCard(MaterialBean materialBean){
        logger.info("--------------MaterialBolt filterCard--------");
        return  materialService.filterCard(materialBean);
    }
    @BoltController
    public List<Map<String,Object>> field_p(){
        logger.info("--------------MaterialBolt filterCard--------");
        return  materialService.field_p();
    }
    @BoltController
    public List<Map<String,Object>>  queryCreditArea(MaterialBean bean){
        logger.info("--------------MaterialBolt queryCreditArea--------");
        return  materialService.queryCreditArea(bean);
    }
    @BoltController
    public Map<String,String> saveMaterial(MaterialModel model){
        logger.info("--------------MaterialBolt saveMaterial--------");
        return  materialService.saveMaterial(model);
    }
    @BoltController
    public  List<Map<String,Object>> queryCreditOrder(String phone){
        logger.info("----------MaterialBolt queryCreditOrder------");
        return  materialService.queryCreditOrder(phone);
    }
    @BoltController
    public Map<String,String> deleteApplyCreditLog(Map<String,String> map){
        logger.info("----------MaterialBolt queryCreditOrder------");
        return  materialService.deleteApplyCreditLog(map);
    }
    @BoltController
    public Map<String,String> sendMessage(Map<String,String> map){
        logger.info("----------MaterialBolt sendMessage------");
        return  materialService.sendMessage(map);
    }
    @BoltController
    public  List<Map<String,Object>> findMaterial(Map<String,String> map){
        logger.info("----------MaterialBolt findMaterial------");
        return  materialService.findMaterial(map);
    }

    /**
     *
     * @param materialBean
     * @return
     */
    @BoltController
    public Map<String,String> totalSearch(MaterialBean materialBean){
        System.out.println("--------------MaterialBolt totalSearch--------");
        return  materialService.totalSearch(materialBean);
    }
}
