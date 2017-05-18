package com.caiyi.financial.nirvana.ccard.material.bolts;

import com.caiyi.financial.nirvana.ccard.material.bean.MaterialBean;
import com.caiyi.financial.nirvana.ccard.material.service.MaterialCardService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.task.TopologyContext;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/26.
 */

@Bolt(boltId = "materialCard", parallelismHint = 1, numTasks = 1)
public class MaterialCardBolt extends BaseBolt {

    MaterialCardService materialCardService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        materialCardService=getBean(MaterialCardService.class);
    }
    @BoltController
    public Map<String,String> saveOrUpdateMaterialDirty(MaterialBean bean){
        logger.info("---------MaterialCardBolt saveOrUpdateMaterialDirty----");
        return materialCardService.saveOrUpdateMaterialDirty(bean);
    }
    @BoltController
    public List<Map<String,Object>> queryO2OBank(String cityId){
        logger.info("---------MaterialCardBolt queryO2OBank------");
        return  materialCardService.queryO2OBank(cityId);
    }
    @BoltController
    public List<Map<String,Object>> queryBusiness(String adcode){
        logger.info("---------MaterialCardBolt queryBusiness----");
        return  materialCardService.queryBusiness(adcode);
    }
    @BoltController
    public Map<String,String> saveO2OMaterial(MaterialBean bean){
        logger.info("---------MaterialCardBolt saveO2OMaterial----");
        return materialCardService.saveO2OMaterial(bean);
    }
    @BoltController
    public List<Map<String,Object>> spreadBank(Map<String,String> para){
        logger.info("---------MaterialCardBolt spreadBank----");
        return materialCardService.spreadBank(para);
    }
    @BoltController
    public Map<String,String> spreadCount(Map<String,String> para){
        logger.info("---------MaterialCardBolt spreadCount----");
        return materialCardService.spreadCount(para);
    }
    @BoltController
    public  List<Map<String,Object>> spreadCard(Map<String,String> map){
        logger.info("---------MaterialCardBolt spreadCard----");
        return materialCardService.spreadCard(map);
    }
    @BoltController
    public MaterialBean applyCreditQueryConversion(MaterialBean bean){
        logger.info("---------MaterialCardBolt applyCreditQueryConversion----");
        return  materialCardService.applyCreditQueryConversion(bean);
    }
    @BoltController
    public  Integer updateApplyCreditLog(MaterialBean bean){
        logger.info("---------MaterialCardBolt updateApplyCreditLog----");
        return  materialCardService.updateApplyCreditLog(bean);
    }
    @BoltController
    public  MaterialBean  applyCreditConversion(MaterialBean bean){
        logger.info("---------MaterialCardBolt applyCreditConversion----");
        return  materialCardService.applyCreditConversion(bean);
    }
    @BoltController
    public Map<String,String> saveApplyCreditLog(MaterialBean bean){
        logger.info("---------MaterialCardBolt saveApplyCreditLog----");
        return  materialCardService.saveApplyCreditLog(bean);
    }
    @BoltController
    public Map<String,String> saveOrUpdateMaterial(MaterialBean bean){
        logger.info("---------MaterialCardBolt saveOrUpdateMaterial----");
       return materialCardService.saveOrUpdateMaterial(bean);
    }
    @BoltController
    public MaterialBean applyCreditCard(MaterialBean bean){
        logger.info("---------MaterialCardBolt applyCreditCard----");
        return  materialCardService.applyCreditCard(bean);
    }
    @BoltController
    public Map<String,Object> o2oApplyCity(Map<String,String> map){
        logger.info("---------MaterialCardBolt applyCreditCard----");
        return  materialCardService.o2oApplyCity(map);
    }
}
