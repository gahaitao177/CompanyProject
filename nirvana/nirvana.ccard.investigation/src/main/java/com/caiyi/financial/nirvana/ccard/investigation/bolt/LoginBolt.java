package com.caiyi.financial.nirvana.ccard.investigation.bolt;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditAccountDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditReferenceDto;
import com.caiyi.financial.nirvana.ccard.investigation.service.InvestigationService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import org.apache.storm.task.TopologyContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by Mario on 2016/7/19 0019.
 * 征信服务，与登录相关接口
 */
@Bolt(boltId = "investLogin", parallelismHint = 1, numTasks = 1)
public class LoginBolt extends BaseBolt {

    private InvestigationService investigationService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        investigationService = getBean(InvestigationService.class);
        logger.info("---------------------investLoginBolt _prepare");
    }

    /**
     * 检查征信报表是否已经存在
     *
     * @param bean
     * @return
     */
    @BoltController
    public JSONObject checkReportExists(Channel bean) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("code",bean.getBusiErrCode());
        jsonObj.put("desc",bean.getBusiErrDesc());
        CreditReferenceDto creditReferenceDto = investigationService.queryCreditRefDto(bean.getCuserId());
        if (creditReferenceDto != null) {
            logger.info("用户[" + bean.getCuserId() + "] 登录名[" + bean.getLoginname() + "] 已存在报告");
            jsonObj.put("code",2);
        } else {
            logger.info("用户[" + bean.getCuserId() + "] 登录名[" + bean.getLoginname() + "] 尚未申请报告");
        }
        return jsonObj;
    }


    /**
     * 更新征信用户状态
     * @param bean
     * @return
     */
    @BoltController
    public JSONObject updateAccount(Channel bean){
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("rt","0");
        try {
            CreditAccountDto caBean=new CreditAccountDto();
            caBean.setCuserId(bean.getCuserId());
            caBean.setCstatus(bean.getClientType());
            if (CheckUtil.isNullString(bean.getLoginname())){
                caBean.setCloginname(null);
                caBean.setCloginpwd(null);
            }else{
                caBean.setCloginname(bean.getLoginname());
                caBean.setCloginpwd(bean.getPassword());
            }
            caBean.setCapplydate(bean.getApplyDate());
            caBean.setClastlogindate(bean.getLastLoginDate());
            caBean.setCvprotdate(bean.getUpdate());
            //查询用户是否保存过账号
            CreditAccountDto cadto=investigationService.queryZhengxinAccountByCuserId(bean);
            //检查用户是否获取过报告
            CreditReferenceDto dto=investigationService.queryCreditRefDto(bean);
            if (cadto==null) {
                //没有保存过则保存
                if (dto != null) {
                    //已有报告标记
                    caBean.setCstatus("50");
                } else {
                    caBean.setCstatus("10");
                }
                int rt = investigationService.saveZhengXinAccount(caBean);
                if (rt == 1) {
                    logger.info("用户[" + bean.getCuserId() + "] 登录名[" + bean.getLoginname() + "] 保存成功");
                } else {
                    logger.info("用户[" + bean.getCuserId() + "] 登录名[" + bean.getLoginname() + "] 保存失败");
                }
                jsonObj.put("rt",rt);
                jsonObj.put("status",caBean.getCstatus());
            }else{
                if ("20".equals(bean.getClientType())||"10".equals(bean.getClientType())){
                    //检查用户是否是更新报告操作
                    if (dto!=null){
                        if ("10".equals(bean.getClientType())){
                            if (bean.getLoginname().equals(cadto.getCloginname())){
                                //登录用户名与库里保存的登录名一致
                                //如果该用户有申请过报告则不用更新征信操作状态
                                caBean.setCstatus(null);
                            }else{
                                //登录用户名与库里保存的登录名不一致，但是已有报告，并且库里保存的征信

                                caBean.setCstatus("50");
                                caBean.setCvprotdate(dto.getCadddate());
                                //重置时间 申请和报告时间
                                investigationService.resettingDate(bean.getCuserId());

                            }
                        }else{
                            caBean.setCstatus("60");
                        }
                        logger.info(" change cstatus loginname["+bean.getLoginname()+"] 60");
                    }else{
                        if (bean.getLoginname().equals(cadto.getCloginname())){
                            if (!"0".equals(cadto.getCstatus())&&!"10".equals(cadto.getCstatus())){
                                caBean.setCstatus(null);
                            }
                        }else{
                            investigationService.resettingDate(bean.getCuserId());
                        }
                    }
                }
                int rt=investigationService.updateAccount(caBean);
                logger.info(bean.getCuserId()+" cstatus loginname["+bean.getLoginname()+"] status[" + caBean.getCstatus() + "] rt["+rt+"] " +
                        " clientType["+bean.getClientType()+"]");
                jsonObj.put("rt",rt);
                jsonObj.put("status","-1");
            }
        }catch (Exception e){
            logger.error("updateAccount 异常",e);
        }
        return jsonObj;
    }


    /**
     * 更新征信用户状态
     * @param bean
     * @return
     */
    @BoltController
    public int updateStatus(Channel bean){
        try {
            int rt=0;
            if (!CheckUtil.isNullString(bean.getReadStatus())){
                CreditAccountDto cadto=investigationService.queryZhengxinAccountByCuserId(bean);
                if (cadto!=null){
                    if (Integer.valueOf(cadto.getCstatus())>Integer.valueOf(bean.getReadStatus())){
                        CreditAccountDto caBean=new CreditAccountDto();
                        caBean.setCuserId(bean.getCuserId());
                        caBean.setCstatus(bean.getReadStatus());
                        rt=investigationService.updateAccount(caBean);
                        logger.info(bean.getCuserId()+" updateStatus  status[" + caBean.getCstatus() + "] rt["+rt+"] " +
                                " clientType["+bean.getClientType()+"]");
                    }else{
                        logger.info(bean.getCuserId()+" updateStatus fail  getReadStatus[" + bean.getReadStatus() + "] cadto.status["+cadto.getCstatus()+"]");
                    }
                }
            }
            return rt;
        }catch (Exception e){
            logger.error("updateStatus 异常",e);
        }
        return 0;
    }



    /**
     * 查询征信用户状态
     * @param bean
     * @return
     */
    @BoltController
    public JSONObject queryUserStatus(Channel bean){
        JSONObject jsonObj = new JSONObject();
        CreditAccountDto cadto=investigationService.queryZhengxinAccountByCuserId(bean);
        if (cadto!=null){
            jsonObj.put("code","1");
            jsonObj.put("desc","查询成功");
            jsonObj.put("cuserid",cadto.getCuserId());
            jsonObj.put("loginname",cadto.getCloginname());

            if (cadto.getCstatus().equals("20")||cadto.getCstatus().equals("60")){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String applytime=sdf.format(cadto.getCapplydate());
                String nowtime=sdf.format(new Date());

                Calendar cd = Calendar.getInstance();
                cd.setTime(cadto.getCapplydate());
                cd.add(5, 7);
                int day7=Integer.valueOf(sdf.format(cd.getTime()));
                cd.add(5, 90);
                int day90=Integer.valueOf(sdf.format(cd.getTime()));


                if (applytime.equals(nowtime)){
                    //当天申请
                    if (cadto.getCstatus().equals("20")){
                        cadto.setCstatus("20");
                    }else{
                        cadto.setCstatus("60");
                    }
                }else if(Integer.valueOf(nowtime)<=day7){
                    //2-7天申请
                    if (cadto.getCstatus().equals("20")){
                        cadto.setCstatus("30");
                    }else{
                        cadto.setCstatus("70");
                    }
                }else{
                    //超过7天
                    if (cadto.getCstatus().equals("20")){
                        cadto.setCstatus("40");
                    }else{
                        if (Integer.valueOf(nowtime)>=day90){
                            cadto.setCstatus("90");
                        }else{
                            cadto.setCstatus("80");
                        }
                    }
                }
            }

            jsonObj.put("status",cadto.getCstatus());
            jsonObj.put("applydate",cadto.getCapplydate());
            jsonObj.put("lastlogindate",cadto.getClastlogindate());
            jsonObj.put("vprotdate",cadto.getCvprotdate());
            if ("Autologon".equals(bean.getFrom())){
                jsonObj.put("loginpwd",cadto.getCloginpwd());
            }
        }else{
            jsonObj.put("code","1");
            jsonObj.put("desc","没有账号记录");
            jsonObj.put("status","0");
        }
        return jsonObj;
    }




}
