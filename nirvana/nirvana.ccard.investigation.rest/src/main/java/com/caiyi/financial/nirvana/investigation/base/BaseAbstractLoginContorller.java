package com.caiyi.financial.nirvana.investigation.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by terry on 2017/1/16.
 */
public abstract class BaseAbstractLoginContorller extends BaseController{


    public void investOldLogin(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investLogin(bean, request, response);
    }
    /**
     * 征信登录
     * @param bean
     * @param request
     */
    @Override
    public void investLogin(Channel bean, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.investLogin(bean, request, response);
        if (bean.getBusiErrCode()!=0){
            bean.setBusiErrCode(1);
            bean.setClientType("10");
            bean.setLastLoginDate(new Date());
            String rcode =client.execute(new DrpcRequest("investLogin", "updateAccount", bean));
            JSONObject rjson= JSON.parseObject(rcode);
            int rt=rjson.getInteger("rt");
            String status=rjson.getString("status");
            if (rt==1){
                if ("50".equals(status)){
                    bean.setBusiErrCode(2);
                }
                bean.setBusiErrDesc("登录成功");
            }else{
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("保存账号信息失败");
            }
            logger.info(" investLogin rcode-----"+rt);
        }
    }
}
