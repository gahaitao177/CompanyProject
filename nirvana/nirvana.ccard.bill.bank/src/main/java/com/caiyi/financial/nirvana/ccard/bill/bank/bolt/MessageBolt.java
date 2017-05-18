package com.caiyi.financial.nirvana.ccard.bill.bank.bolt;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.bill.bank.service.MessageService;
import com.caiyi.financial.nirvana.ccard.bill.dto.MessageDto;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/9/18.
 */
@Bolt(boltId = "message", parallelismHint = 1, numTasks = 1)
public class MessageBolt extends BaseBolt {

    @Autowired
    MessageService messageService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }
    @BoltController
    public JSONObject getMessageList(String cuserId){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("code",0);
        if(StringUtils.isEmpty(cuserId)){
            jsonObject.put("desc","用户id不能为空");
            return jsonObject;
        }
        List<MessageDto> list= messageService.getMessageList(cuserId);
        if(list!=null&&list.size()>0){
            jsonObject.put("code",1);
            jsonObject.put("desc","查询成功");
            jsonObject.put("data",list);
        }else if (list==null){
            jsonObject.put("code",0);
            jsonObject.put("desc","查询异常");
        }else {
            jsonObject.put("code",0);
            jsonObject.put("desc","没有查到有效数据");
        }
        return  jsonObject;
    }

    /**
     * 查询近1一个月是否存在没有推送的消息
     * @param cuserId
     * @return
     */
    @BoltController
    public JSONObject getValidMessageCount(String cuserId){
        return  messageService.getValidMessageCount(cuserId,-1);
    }

    /**
     * 查询userId
     * @param partnerId
     * @return
     */
    @BoltController
    public  JSONObject getUserIdByPartnerId(String partnerId){
        JSONObject jsonOjb=new JSONObject();
        String  userid=messageService.getUserIdByPartnerId(partnerId);
        if(StringUtils.isNotEmpty(userid)){
            jsonOjb.put("code",1);
            jsonOjb.put("desc","查询成功");
            jsonOjb.put("data",userid);
        }else {
            jsonOjb.put("code",0);
            jsonOjb.put("desc","查询失败");
        }
        return jsonOjb;
    };
}
