package com.caiyi.financial.nirvana.ccard.investigation.bolt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.Channel;
import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditPrivilegeDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditScoreDto;
import com.caiyi.financial.nirvana.ccard.investigation.service.CreditLifeService;
import com.caiyi.financial.nirvana.ccard.investigation.service.CreditScoreService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by jianghao on 2016/11/29. 征信积分
 */
@Bolt(boltId = "creditScore", parallelismHint = 4, numTasks = 8)
public class CreditScoreBolt extends BaseBolt {
    @Autowired
    private CreditScoreService creditScoreService;

    @Autowired
    private LoginBolt loginBolt;

    @Autowired
    private CreditLifeService creditLifeService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
//        creditScoreService = getBean(CreditScoreService.class);
//        logger.info("---------------------CreditScoreBolt _prepare");
    }

    /**
     * 获取征信首页数据
     *
     * @param creditScoreBean
     * @return BoltResult
     */
    @BoltController
    public BoltResult CreditScoreIndex(CreditScoreBean creditScoreBean) {
        long start=System.currentTimeMillis();
        BoltResult boltResult = new BoltResult();
        if(StringUtils.isNotEmpty(creditScoreBean.getCuserId())) {
            Channel bean = new Channel();
            bean.setCuserId(creditScoreBean.getCuserId());
            List<Map<String,String>> maps=creditLifeService.getSwitch();
            String sb1="1";
            String zx1="1";
            String xyk1="1";
            String gjj1="1";
            logger.info("maps:"+ JSON.toJSON(maps));
            for(Map<String,String> map: maps){
                for (String key:map.keySet()){
                    if("name".equals((key))) {
                        if ("sb".equals(map.get(key))) {
                            sb1 = map.get("isOpen");
                        }
                        if ("zx".equals(map.get(key))) {
                            zx1 = map.get("isOpen");
                        }
                        if ("xyk".equals(map.get(key))) {
                            xyk1 = map.get("isOpen");
                        }
                        if ("gjj".equals(map.get(key))) {
                            gjj1 = map.get("isOpen");
                        }
                    }
                }
            }
            JSONObject json =  loginBolt.queryUserStatus(bean);
            if ("1".equals(zx1) && json.get("status") != null && json.get("status").toString().compareTo("50") >= 0) {
                creditScoreService.creditInvestigation(creditScoreBean, json.getString("status"));//更新征信
            }
            long end=System.currentTimeMillis();
            logger.info("征信耗时:"+(end-start));
            if("1".equals(xyk1)) {
                creditScoreService.creditCard(creditScoreBean);//更新信用卡数据
            }
            long xyk=System.currentTimeMillis();
            logger.info("信用卡耗时:"+(xyk-end));
            if("1".equals(gjj1)) {
                logger.info("执行公积金");
                creditScoreService.providentFund(creditScoreBean);//更新公积金
            }
            long gjj=System.currentTimeMillis();
            logger.info("公积金耗时:"+(gjj-xyk));
            if("1".equals(sb1)) {
                logger.info("执行社保");
                creditScoreService.socalInsurance(creditScoreBean);//更新社保
            }
            long sb=System.currentTimeMillis();
            logger.info("社保耗时:"+(sb-gjj));
            creditScoreService.calculateScoreAndUpdate(creditScoreBean);//保存积分和流水
            long flow=System.currentTimeMillis();
            logger.info("流水更新耗时:"+(flow-sb));
            JSONObject data = creditScoreService.queryCreditScoreIndex(creditScoreBean);
            if (data != null) {
                boltResult.setCode("1");
                boltResult.setDesc("查询征信首页数据成功");
                boltResult.setData(data);
            } else {
                boltResult.setCode("1");
                boltResult.setDesc("没有查出数据");
                logger.info("没有查出数据");
            }
        }else{
            boltResult.setCode("1");
            boltResult.setDesc("非法用户数据");
            logger.info("非法用户数据");
        }
        logger.info("bolt总耗时:"+(System.currentTimeMillis()-start));
        return boltResult;
    }



    /**
     * 信用生活首页
     * @return
     */
    @BoltController
    public  JSONObject queryCreditLife(CreditScoreBean scoreBean){
        return creditLifeService.queryCreditLife(scoreBean);
    }
    /**
     * 查询信用特权
     *
     * @param creditScoreBean
     * @return jsonObj
     */
    @BoltController
    public BoltResult creditPrivilege(CreditScoreBean creditScoreBean) {
        logger.info("creditPrivilege start : uid===" + creditScoreBean.getCuserId());
        CreditScoreDto creditScore = creditScoreService.queryCreditScore(creditScoreBean);
        BoltResult boltResult = new BoltResult();
        if(creditScore==null){
            boltResult.setCode("1");
            boltResult.setDesc("没有该用户数据");
        }else {
            double topRate = creditScoreService.queryTopRate(creditScore.getUserId());//获取战胜用户的比率
            topRate = 1-topRate;
            String strTopRate = topRate+"";
            Integer level = creditScoreService.queryLevelByScore(creditScore.getCreditScores());//获取用户的等级
//            String levelName = creditScoreService.queryLevelNameByScore(creditScore.getCreditScores());//获取用户的等级名称
            JSONObject result = creditScoreService.queryCreditPrivilege(level);
            JSONObject ObjData = new JSONObject();
            ObjData.put("creditScores", creditScore.getCreditScores());
            ObjData.put("topRate",strTopRate.substring(0,4) );
            ObjData.put("level", level);
            ObjData.put("levelName", creditScore.getLevelName());
            if (!"0".equals(result.get("code"))) {

                if(creditScore.getCreditScores()==0){
                    ObjData.put("privilegeCount", 0);
                }else{
                    List<CreditPrivilegeDto> creditPrivileges = (List<CreditPrivilegeDto>) result.get("creditPrivilegeDtos");
                    ObjData.put("privilegeCount", creditPrivileges.size());
                    ObjData.put("privilegeDetail", creditPrivileges);
                }

                boltResult.setCode("1");
                boltResult.setDesc("查询信用特权完成");
            } else {
                boltResult.setCode("1");
                boltResult.setDesc("没有信用特权数据");
            }
            boltResult.setData(ObjData);
        }
        return boltResult;
    }

    /**
     * 历史积分
     * @return
     */
    @BoltController
    public JSONObject getHistoryScores(CreditScoreBean bean){
        if(1==bean.getForceCaculate()){
            CreditScoreIndex(bean);
        }
        return creditScoreService.getHistoryScores(bean);
    }
}
