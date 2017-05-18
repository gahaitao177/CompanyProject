package com.caiyi.financial.nirvana.discount.ccard.bolts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.ccard.bean.Commodity;
import com.caiyi.financial.nirvana.discount.ccard.dto.CommodityDto;
import com.caiyi.financial.nirvana.discount.ccard.service.CommodityService;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.TopologyContext;

import java.util.List;
import java.util.Map;

/**
 * Created by A-0106 on 2016/5/31.
 */
@Bolt(boltId = "commodity", parallelismHint = 2, numTasks = 4)
public class CommodityBolt extends BaseBolt {

    private CommodityService commodityService;
    @Override
    protected void _prepare(Map map, TopologyContext topologyContext) {
        commodityService=getBean(CommodityService.class);
    }
    @BoltController
    public JSONObject queryPointsByUser(Commodity bean){
        List<CommodityDto> commodityDtoList=commodityService.queryPointsByUser(bean.getCuserId());
        JSONArray list=new JSONArray();
        if(commodityDtoList!=null){
            for(CommodityDto commodityDto:commodityDtoList){
                JSONObject object=new JSONObject();
                if(StringUtils.isNotEmpty(commodityDto.getCbankName())){
                    object.put("bankname",commodityDto.getCbankName());
                }
                if(commodityDto.getIbankid()!=null){
                    object.put("bankid",commodityDto.getIbankid());
                }
                if(StringUtils.isNotEmpty(commodityDto.getIcard4num())){
                    object.put("cardnum",commodityDto.getIcard4num());
                }
                if(StringUtils.isNotEmpty(commodityDto.getIpoint())){
                    object.put("points",commodityDto.getIpoint());
                }
                list.add(object);
            }
        }
        JSONObject map=new JSONObject();
        map.put("commodity",list);
        map.put("commodityBank", commodityService.fetchComm(bean.getCuserId(),6,bean.getIbankid(),commodityDtoList));
        return map;
    }
    @BoltController
    public List<CommodityDto> queryPointsAndBanks(){
        return  commodityService.queryPointsAndBanks();
    }
    @BoltController
    public Map<String,Object> queryPointsList(Commodity commodity){
        logger.info("------------CommodityBolt queryPointsList----");
        return commodityService.queryPointsList(commodity);
    }
    @BoltController
    public com.alibaba.fastjson.JSONObject queryCommodityDetail(String icommid){
        logger.info("------------CommodityBolt queryCommodityDetail--");
        return  commodityService.queryCommodityDetail(icommid);
    }

}
