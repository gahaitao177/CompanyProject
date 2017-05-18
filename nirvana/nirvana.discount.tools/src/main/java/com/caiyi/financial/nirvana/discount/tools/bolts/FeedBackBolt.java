package com.caiyi.financial.nirvana.discount.tools.bolts;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.tools.bean.FeedBackBean;
import com.caiyi.financial.nirvana.discount.tools.service.FeedBackService;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.TopologyContext;

import java.util.Map;

/**
 * Created by dengh on 2016/8/10.
 */
@Bolt(boltId = "FeedBackBolt", parallelismHint = 1, numTasks = 1)
public class FeedBackBolt extends BaseBolt {

    FeedBackService feedBackService;
    private  String TYPE_SUBMIT_WRONG = "0";
    private  String TYPE_SUBMIT_CHEAP = "1";
    @Override
    protected void _prepare(Map stormConf, TopologyContext context)
    {
        feedBackService=getBean(FeedBackService.class);
    }
    @BoltController
    public FeedBackBean submitWrong(FeedBackBean bean){
        String istoreid = bean.getStoreId();
        logger.info("用户报错开始: istoreid  " +  istoreid);
        if(StringUtils.isEmpty(istoreid) ){
            return bean;
        }

        // 设置反馈类型
        bean.setType(TYPE_SUBMIT_WRONG);
        try {

            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("用户报错成功");
            Integer ret =feedBackService.u_wrong_submit(bean);

            if (ret != 1 || bean.getBusiErrCode() != 1) {
                bean.setBusiErrCode(0);
                bean.setBusiErrDesc("用户报错失败");
                logger.info("用户报错更新失败:" + bean.getStoreId());
            }
            logger.info("用户报错结束...门店: " + bean.getStoreId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  bean;
    }
    @BoltController
    public FeedBackBean custom_service(FeedBackBean bean) throws Exception {
        logger.info("客服咨询统计  " + bean.getQqAccountId() );

        // qq号为空 则返回  add by lcs 20160429
        if (CheckUtil.isNullString(bean.getQqAccountId())){
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("操作成功");
            return bean;
        }
        try {

            Integer num = 0;
            int ret = 0;
            num =feedBackService.query_custom_service_Num(bean.getQqAccountId());

            if(num > 0){
                logger.info("客服咨询统计更新：qq="+bean.getQqAccountId() );
                ret=  feedBackService.update_custom_service(bean.getQqAccountId());

            } else {
                logger.info("客服咨询统计插入：qq="+bean.getQqAccountId() );
                ret= feedBackService.insert_custom_service(bean.getQqAccountId());
            }

            if (ret != 1) {
                logger.info("客服咨询统计失败：qq="+bean.getQqAccountId() );
            }else {
                logger.info("客服咨询统计成功：qq="+bean.getQqAccountId());
            }
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }
    @BoltController
    public JSONObject checkIosUserExist(FeedBackBean bean){
        logger.info("------FeedBackBolt-----checkIosUserExist");
        return feedBackService.checkIosUserExist(bean);
    }
    @BoltController
    public JSONObject iosIdfaSave(FeedBackBean bean) {
        logger.info("------FeedBackBolt-----checkIosUserExist");
        return feedBackService.iosIdfaSave(bean);
    }








}
