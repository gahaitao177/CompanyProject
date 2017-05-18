package com.caiyi.financial.nirvana.ccard.investigation.bolt;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReferenceDto;
import com.caiyi.financial.nirvana.ccard.investigation.service.InvestigationService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.storm.task.TopologyContext;

import java.util.Map;

/**
 * Created by terry on 2016/10/27.
 */
@Bolt(boltId = "registerBolt", parallelismHint = 1, numTasks = 1)
public class RegisterBolt extends BaseBolt{

    private InvestigationService investigationService;


    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        investigationService = getBean(InvestigationService.class);
        logger.info("---------------------investRegisterBolt _prepare");
    }

    @BoltController
    public JSONObject saveLoginAccount(Channel bean) {
        JSONObject jsonObj = new JSONObject();
        CreditReferenceDto dto=investigationService.queryCreditRefDto(bean);
        if (dto!=null){
            bean.setClientType("50");
        }else{
            bean.setClientType("0");
        }
        int rt=0;
        CreditAccountDto caBean=new CreditAccountDto();
        caBean.setCuserId(bean.getCuserId());
        caBean.setCstatus(bean.getClientType());
        caBean.setCloginname(bean.getLoginname());
        caBean.setCloginpwd(bean.getPassword());
        CreditAccountDto cto=investigationService.queryZhengxinAccountByCuserId(bean);
        if (cto!=null){
            rt=investigationService.updateAccount(caBean);
        }else{
            rt=investigationService.saveZhengXinAccount(caBean);
        }
        if (rt ==1) {
            jsonObj.put("code","1");
            jsonObj.put("desc", "保存成功");
            logger.info("用户[" + bean.getCuserId() + "] 登录名[" + bean.getLoginname() + "] 保存成功");
        } else {
            logger.info("用户[" + bean.getCuserId() + "] 登录名[" + bean.getLoginname() + "] 保存失败");
            jsonObj.put("code", "0");
            jsonObj.put("desc", "保存失败");
        }
        return jsonObj;
    }

}
