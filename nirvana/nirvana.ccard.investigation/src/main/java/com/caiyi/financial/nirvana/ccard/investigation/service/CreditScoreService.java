package com.caiyi.financial.nirvana.ccard.investigation.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.*;
import com.caiyi.financial.nirvana.ccard.investigation.constants.Constant;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditCardDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditInvestigationDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditPrivilegeDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditScoreDto;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.CalculateScoreMapper;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.CreditScoreMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by jianghao on 2016/11/29. 征信积分
 */
@Service
public class CreditScoreService extends AbstractService {
    @Autowired
    CreditScoreMapper mapper;
    @Autowired
    private CalculateScoreMapper calculateScoreMapper;
    @Autowired
    private CreditScoreCalculateService scoreCalculateService;
    @Autowired
    private CreditLifeService creditLifeService;
    /**
     * 根据userId查询征信报告信息
     *
     * @param creditScoreBean
     * @return CreditScoreDto
     */
    public JSONObject queryCreditScoreIndex(CreditScoreBean creditScoreBean) {
        logger.info("进入查询首页 queryCreditScore ");
        JSONObject creditLevel = new JSONObject();
        JSONObject data = new JSONObject();
        creditLevel.put("level",Constant.SCORE_LEVELS);
        creditLevel.put("levelName",Constant.SCORE_LEVEL_NAMES);
        CreditScoreDto creditScoreDto = mapper.queryCreditScore(creditScoreBean);
        if(creditScoreDto!=null){
            data.put("creditLevel",creditLevel);
            data.put("creditScores",creditScoreDto.getCreditScores());
            data.put("creditComment",creditScoreDto.getLevelName());

            if(creditScoreDto.getCreditScores()==0){
                data.put("privilegeCount",0);
            }else{
                List<CreditPrivilegeDto> creditPrivilegeDtos = mapper.queryCreditPrivilege(creditScoreBean.getLevelCode());
                if (creditPrivilegeDtos == null) {
                    data.put("privilegeCount",0);
                } else {
                    data.put("privilegeCount",creditPrivilegeDtos.size());
                }

            }

            String zxStatus = mapper.queryZxStatus(creditScoreDto.getZxId());
            data.put("zxStatus",zxStatus);
            if(creditScoreDto.getCreditScores()==0||creditScoreDto.getZxId()==0){
                data.put("zxSubtitle","防逾期/查房贷/贷款特权");
            }else{
                data.put("zxSubtitle",creditScoreDto.getLevelName());
            }

            data.put("updateDate",creditScoreDto.getUpdateTime());
            if(creditScoreDto.getXykId()!=0){
                data.put("getXyk","true");
                String xykId =String.valueOf(creditScoreDto.getXykId());
                String xykSubtitle  = mapper.queryCreditCardTitle(xykId);
                data.put("xykSubtitle",xykSubtitle);
            }else{
                data.put("getXyk","false");
                data.put("xykSubtitle","账单分析/还款提醒");
            }
            if(creditScoreDto.getGjjId()!=0){
                String gjjSubtitle  = mapper.queryGjjTitle(creditScoreDto.getGjjId());
                data.put("gjjSubtitle",gjjSubtitle);
                data.put("getGjj","true");
            }else{
                data.put("getGjj","false");
                data.put("gjjSubtitle","账单明细/ 防止漏缴");
            }
            Integer educationLevel  = mapper.queryEducationLevel(creditScoreDto.getUserId());
            if(educationLevel!=null&&educationLevel!=-1){
                String xlSubtitle = "";
                if(educationLevel==1){
                    xlSubtitle = "专科";
                }else if(educationLevel==2){
                    xlSubtitle = "本科";
                }else if(educationLevel==3){
                    xlSubtitle = "硕士";
                }else if(educationLevel==4){
                    xlSubtitle = "博士";
                }else{
                    xlSubtitle = "其他";
                }
                data.put("xlSubtitle",xlSubtitle);
                data.put("getXl","true");
            }else{
                data.put("getXl","false");
                data.put("xlSubtitle","提高信用分");
            }

            String url_gjj= SystemConfig.get("zxURL.gjj_url_para");
            data.put("gjjUrl",url_gjj);
            if(creditScoreDto.getSbId()!=0){
                data.put("getSb","true");
            }else{
                data.put("getSb","false");
            }
            String url_sb= SystemConfig.get("zxURL.sb_url_para");
//            data.put("gjjUrl",url_gjj);
            data.put("sbSubtitle","账单明细/防止漏缴");
            data.put("sbUrl",url_sb);


        }else{
            data.put("desc","没有该用户数数据");
            data.put("code","1");
        }

        logger.info("查询完毕{}",data);
        return data;
    }
    public CreditScoreDto queryCreditScore(CreditScoreBean creditScoreBean) {
        return mapper.queryCreditScore(creditScoreBean);
    }
    /**
     * 更新征信数据
     *
     * @param creditScoreBean
     */
    public JSONObject creditInvestigation(CreditScoreBean creditScoreBean,String creditStatus) {
        logger.info("更新征信数据开始");
        JSONObject json = new JSONObject();
        try {
            CreditInvestigationBean creditInvestigationBean =new CreditInvestigationBean();
            StringBuffer title = new StringBuffer();
            //是否逾期 有无信用卡 有无贷款
            CreditInvestigationDto creditInvestigationDto = mapper.queryCreditInvestigation(creditScoreBean);
            logger.info("获取征信表数据",JSON.toJSON(creditInvestigationDto));
            if(creditInvestigationDto!=null){
                if(creditInvestigationDto.getIsOverStay()==1){
                    title.append("无逾期");
                    creditInvestigationBean.setIsOverStay(1);
                }else{
                    title.append("有逾期");
                    creditInvestigationBean.setIsOverStay(0);
                }
                if(creditInvestigationDto.getIsCard()==1){
                    title.append("/有信用卡");
                    creditInvestigationBean.setIsCard(1);
                }else{
                    creditInvestigationBean.setIsCard(0);
                }
                if(creditInvestigationDto.getIsLoan()==1){
                    title.append("/有贷款");
                    creditInvestigationBean.setIsLoan(1);
                }else{
                    creditInvestigationBean.setIsLoan(0);
                }
                creditInvestigationBean.setTitle(title.toString());
                creditInvestigationBean.setStatus(Integer.parseInt(creditStatus));
                //查询征信积分表中数据
                CreditScoreDto creditScoreDto = mapper.queryCreditScore(creditScoreBean);
                int isZx = 0;//返回更新征信表标志
                if(creditScoreDto!=null){
                    if(creditScoreDto.getZxId()==0){
                        logger.info("插入征信表数据开始，参数为{}",JSON.toJSON(creditInvestigationBean));
                        isZx = mapper.insertCreditInvestigation(creditInvestigationBean);
                        logger.info("插入征信表数据结束,插入数为{}",isZx);
                        creditScoreBean.setZxId(creditInvestigationBean.getCreditInvestigationId());
                    }else{
                        creditInvestigationBean.setCreditInvestigationId(creditScoreDto.getZxId());
                        logger.info("更新征信表数据开始");
                        isZx = mapper.updateCreditInvestigation(creditInvestigationBean);
                        logger.info("更新征信表数据结束,插入数为{}",isZx);
                        creditScoreBean.setZxId(creditScoreDto.getZxId());
                    }
                }else{
                    logger.info("插入征信表数据开始，参数为{}",JSON.toJSON(creditInvestigationBean));
                    isZx = mapper.insertCreditInvestigation(creditInvestigationBean);
                    logger.info("插入征信表数据结束,插入数为{}",isZx);
                    creditScoreBean.setZxId(creditInvestigationBean.getCreditInvestigationId());
                }
            }else{
                json.put("code","0");
                json.put("desc","查不到该用户数据");
            }


        } catch (Exception e) {
            logger.error("更新征信数据 异常", e);
            json.put("code","0");
            json.put("desc","更新征信数据异常");
        }
        return json;
    }

        /**
         * 公积金
         *
         * @param creditScoreBean
         */
        public JSONObject providentFund(CreditScoreBean creditScoreBean) {
            logger.info("更新公积金开始");
            JSONObject json = new JSONObject();
            try {
                long start=System.currentTimeMillis();
                ProvidentFundBean providentFundBean = creditLifeService.getGjjBean(creditScoreBean);
                logger.info("公积金http耗时:{}",(System.currentTimeMillis()-start));
                logger.info("获取公积金数据{}",JSON.toJSON(providentFundBean));
               if(providentFundBean!=null){
                   //查询征信积分表中数据
                   CreditScoreDto creditScoreDto = mapper.queryCreditScore(creditScoreBean);
                   int isGjj = 0;//返回更新公积金表标志
                   if(creditScoreDto!=null){

                       if(creditScoreDto.getGjjId()==0){
                           logger.info("插入公积金数据开始,参数为{}",JSON.toJSON(providentFundBean));
                           isGjj = mapper.insertProvidentFund(providentFundBean);
                           logger.info("插入公积金数据结束，插入数为{}",isGjj);
                           creditScoreBean.setGjjId(providentFundBean.getProvidentFundId());
                       }else{
                           String gjjId = mapper.queryLastUpdateMonth(creditScoreDto);
                           if(!providentFundBean.getLastUpdateMonth().equals(gjjId)){
                               providentFundBean.setProvidentFundId(creditScoreDto.getGjjId());
                               logger.info("更新公积金数据开始,参数为{}",JSON.toJSON(providentFundBean));
                               isGjj = mapper.updateProvidentFund(providentFundBean);
                               logger.info("更新公积金数据结束,更新数为{}",isGjj);
                           }
                           creditScoreBean.setGjjId(creditScoreDto.getGjjId());
                       }
                   }else{
                       logger.info("插入公积金数据开始,参数为{}",JSON.toJSON(providentFundBean));
                       isGjj = mapper.insertProvidentFund(providentFundBean);
                       logger.info("插入公积金数据结束，插入数为{}",isGjj);
                       creditScoreBean.setGjjId(providentFundBean.getProvidentFundId());
                   }
               }else{
                   json.put("code","1");
                   json.put("desc","查不到该用户公积金数据");
               }
            } catch (Exception e) {
                logger.error("更新公积金异常", e);
                json.put("code","0");
                json.put("desc","更新公积金异常");
            }
            return json;
        }
    /**
     * 更新社保数据
     *
     * @param creditScoreBean
     */
    public JSONObject socalInsurance(CreditScoreBean creditScoreBean) {
        JSONObject json = new JSONObject();
        try {
            SocialInsuranceBean socialInsuranceBean =  creditLifeService.getSbBean(creditScoreBean);
            logger.info("获取社保数据{}", JSON.toJSON(socialInsuranceBean));
            if (socialInsuranceBean != null) {
                //查询征信积分表中数据
                CreditScoreDto creditScoreDto = mapper.queryCreditScore(creditScoreBean);
                int isSb = 0;//返回更新社保表标志
                if (creditScoreDto != null) {
                    if (creditScoreDto.getSbId() == 0) {
                        logger.info("插入社保数据开始,参数为{}", JSON.toJSON(socialInsuranceBean));
                        isSb = mapper.insertSocalInsurance(socialInsuranceBean);
                        logger.info("插入社保据结束，插入数为{}", isSb);
                        creditScoreBean.setSbId(socialInsuranceBean.getSocialInsuranceId());
                    } else {
                        socialInsuranceBean.setSocialInsuranceId(creditScoreDto.getSbId());
                        logger.info("更新社保数据开始,参数为{}", JSON.toJSON(socialInsuranceBean));
                        isSb = mapper.updateSocalInsurance(socialInsuranceBean);
                        logger.info("更新社保数据结束,更新数为{}", isSb);
                        creditScoreBean.setSbId(creditScoreDto.getSbId());
                    }
                } else {
                    logger.info("插入社保数据开始,参数为{}", JSON.toJSON(socialInsuranceBean));
                    isSb = mapper.insertSocalInsurance(socialInsuranceBean);
                    logger.info("插入社保据结束，插入数为{}", isSb);
                    creditScoreBean.setSbId(socialInsuranceBean.getSocialInsuranceId());
                }
            } else {
                json.put("code", "1");
                json.put("desc", "查不到该用户社保数据");
            }
        }catch (Exception e) {
            logger.error("更新社保数据异常", e);
            json.put("code","0");
            json.put("desc","更新社保数据异常");
        }
        return json;
    }
    /**
     * 查询信用特权信息
     *
     * @param levelCode
     */
    public JSONObject queryCreditPrivilege(int levelCode ) {
        JSONObject json = new JSONObject();
        try{
            List<CreditPrivilegeDto> creditPrivilegeDtos = mapper.queryCreditPrivilege(levelCode);
            logger.info("size:"+creditPrivilegeDtos.size());
            if(creditPrivilegeDtos.size()==0){
                json.put("code","0");
                json.put("desc","queryCreditPrivilege没有查出数据");
            }else{
                json.put("creditPrivilegeDtos",creditPrivilegeDtos);
                json.put("code","1");
                json.put("desc","queryCreditPrivilege完成");
            }
        }catch (Exception e) {
            json.put("code","0");
            json.put("desc","queryCreditPrivilege异常");
        }
        return json;
    }

    //更新积分等级，月表和流水表
    private JSONObject updateMonthFlow(CreditScoreBean creditScoreBean) {

        JSONObject json = new JSONObject();
        try {
            CreditScoreDto creditScoreDto = mapper.queryCreditScore(creditScoreBean);
            creditScoreBean.setCreditId(creditScoreDto.getCreditId());
            creditScoreBean.setCreditScores(creditScoreDto.getCreditScores());
            String monthScoreId = mapper.queryMonthScoreId(creditScoreBean);//月次积分号
            if (monthScoreId == null) {
                logger.info("插入月表开始，参数为{}", JSON.toJSON(creditScoreBean));
                  mapper.insertMonthScore(creditScoreBean);
                logger.info("插入月表结束");
            } else {
                logger.info("更新月表开始，参数为{}", JSON.toJSON(creditScoreBean));
                 mapper.updateMonthScore(creditScoreBean);//更新月次积分表
                logger.info("更新月结束");
            }
            logger.info("插入流水开始，参数为{}", JSON.toJSON(creditScoreBean));
            mapper.insertCreditScoreFlow(creditScoreBean);//更新流水表
            logger.info("插入流水结束");
            json.put("code","1");
            json.put("desc","updateMonthFlow完成");
        } catch (Exception e) {
            logger.error("updateMonthFlow 异常", e);
            json.put("code","0");
            json.put("desc","updateMonthFlow异常");
        }
        return json;
    }

    /**
     * 查询用户的排名比例 0.25
     * @param userId
     * @return
     */
    public double queryTopRate(String userId){
        CreditScoreDto rankCount=mapper.queryUserRank(userId);
        double rankNum=0;
        double rate=0;//比例夸张100倍 取整数,如0.25 取25%
        double persons=mapper.queryUserCount();  //总人数
        if(rankCount!=null){
            rankNum=rankCount.getRankNum();
        }
        if (persons!=0){
            rate= rankNum/persons;
        }
        return rate;
    }
    //根据分数 查询用户所在的等级
    public int queryLevelByScore(Integer score){
        if(score<550){
            return Constant.SCORE_LEVEL_BAD;
        }else if(score<600){
            return Constant.SCORE_LEVEL_COMMON;
        }else if(score<650){
            return Constant.SCORE_LEVEL_GOOD;
        }else if(score<700){
            return Constant.SCORE_LEVEL_BETTER;
        }else{
            return Constant.SCORE_LEVEL_BEST;
        }
    }
    //根据分数 查询用户所在的等级名
    public String queryLevelNameByScore(Integer score){
        if(score<550){
            return Constant.SCORE_LEVEL_BAD_NAME;
        }else if(score<600){
            return Constant.SCORE_LEVEL_COMMON_NAME;
        }else if(score<650){
            return Constant.SCORE_LEVEL_GOOD_NAME;
        }else if(score<700){
            return Constant.SCORE_LEVEL_BETTER_NAME;
        }else{
            return Constant.SCORE_LEVEL_BEST_NAME;
        }
    }
    /**
     * 获得历史积分的列表
     * @param bean
     * @return
     */
    public JSONObject getHistoryScores(CreditScoreBean bean){
        JSONObject result=new JSONObject();
        double rate=queryTopRate(bean.getCuserId());
        String strTopRate = 1-rate+"";
        CreditScoreDto creditIdBean=mapper.queryUserRank(bean.getCuserId());

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MONTH,-5);
        List<CreditScoreDto> scoreDtos=mapper.queryMonthScores(creditIdBean.getCreditId());
        Map<Integer,Integer> monthScores=new LinkedHashMap<>();
        int score=0;
        String yearAndMonth="";
        boolean first=true;
        //把有积分的月份 添加到monthScores容器中 并补充其中缺少的月份对应的积分
        //如需要5-10 六个月数据 数据库只有7、9月份 会把5、6月份设置0, 8月和7月份的积分一致
        for(CreditScoreDto scoreDto :scoreDtos){
            yearAndMonth=calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1);
            if(calendar.get(Calendar.MONTH)<9){
                yearAndMonth=calendar.get(Calendar.YEAR)+"0"+(calendar.get(Calendar.MONTH)+1);
            }
            while(yearAndMonth.compareTo(scoreDto.getMonth())<0){
                monthScores.put(calendar.get(Calendar.MONTH)+1,score);
                calendar.add(Calendar.MONTH,1);
                yearAndMonth=calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1);
                if(calendar.get(Calendar.MONTH)<10){
                    yearAndMonth=calendar.get(Calendar.YEAR)+"0"+(calendar.get(Calendar.MONTH)+1);
                }
                first=false;
            }
            score=scoreDto.getCreditScores();
            if (yearAndMonth.compareTo(scoreDto.getMonth())>0&&first){
                monthScores.put(calendar.get(Calendar.MONTH)+1,score);
                continue;
            }
            monthScores.put(calendar.get(Calendar.MONTH)+1,score);
            calendar.add(Calendar.MONTH,1);
            first=false;
        }
        Calendar calendarNow=Calendar.getInstance();
        String yearAndMonthNow=calendarNow.get(Calendar.YEAR)+""+(calendarNow.get(Calendar.MONTH)+1);
        if(calendarNow.get(Calendar.MONTH)<9){
            yearAndMonthNow=calendarNow.get(Calendar.YEAR)+"0"+(calendarNow.get(Calendar.MONTH)+1);
        }
        yearAndMonth=calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1);
        if(calendar.get(Calendar.MONTH)<9){
            yearAndMonth=calendar.get(Calendar.YEAR)+"0"+(calendar.get(Calendar.MONTH)+1);
        }
        //如需要5-10 六个月数据 数据库只有7、9月份 会把9月和10月份的积分设为一致
        while (yearAndMonth.compareTo(yearAndMonthNow)<=0){
            monthScores.put(calendar.get(Calendar.MONTH)+1,score);
            calendar.add(Calendar.MONTH,1);
            yearAndMonth=calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1);
            if(calendar.get(Calendar.MONTH)<9){
                yearAndMonth=calendar.get(Calendar.YEAR)+"0"+(calendar.get(Calendar.MONTH)+1);
            }
        }
        StringBuilder monthSb=new StringBuilder();
        StringBuilder scoreSb=new StringBuilder();
        for (Integer key:monthScores.keySet()){
            monthSb.append(key);
            monthSb.append(",");
            scoreSb.append(monthScores.get(key));
            scoreSb.append(",");
//            logger.info("key={},value={}",key,monthScores.get(key));
        }
        if(monthSb.toString().endsWith(",")){
            monthSb.deleteCharAt(monthSb.length()-1);
        }
        if(scoreSb.toString().endsWith(",")){
            scoreSb.deleteCharAt(scoreSb.length()-1);
        }
        logger.info("monthSb={},scoreSb={}",monthSb.toString(),scoreSb.toString());
        result.put("score",creditIdBean.getCreditScores());
        if(StringUtils.isNotEmpty(strTopRate)) {
            if(strTopRate.length()>4) {
                result.put("rate", strTopRate.substring(0, 4));
            }else {
                result.put("rate", strTopRate);
            }
        }
        result.put("historyDate",monthSb.toString());
        result.put("historyScore",scoreSb.toString());
        result.put("tasks",getBillScores(creditIdBean));
        return  result;
    }

    /**
     * 账单添加分数
     * @return  billScore
     */
    public JSONArray getBillScores(CreditScoreDto creditIdBean){
        JSONArray billScore=new JSONArray();
        JSONObject zxScore=new JSONObject();
        zxScore.put("tType",0);
        zxScore.put("tName","征信报告");
        zxScore.put("tScore","100~200");

        JSONObject zdScore=new JSONObject();
        zdScore.put("tType",1);
        zdScore.put("tName","信用卡账单");
        zdScore.put("tScore","50~200");

        JSONObject gjjScore=new JSONObject();
        gjjScore.put("tType",2);
        gjjScore.put("tName","公积金账单");
        gjjScore.put("tScore","50~300");

        JSONObject sbScore=new JSONObject();
        sbScore.put("tType",3);
        sbScore.put("tName","社保账单");
        sbScore.put("tScore","0~50");
        if(creditIdBean.getZxId()==0){
            billScore.add(zxScore);
        }
        if(creditIdBean.getXykId()==0) {
            billScore.add(zdScore);
        }
        if(creditIdBean.getGjjId()==0) {
            billScore.add(gjjScore);
        }
        if(creditIdBean.getSbId()==0) {
            billScore.add(sbScore);
        }
        return billScore;
    }

    /**
     * 保存积分和更新流水
     * @param creditScoreBean
     */
    public void calculateScoreAndUpdate(CreditScoreBean creditScoreBean) {
        CreditScoreDto creditScoreDto = mapper.queryCreditScore(creditScoreBean);
        if (creditScoreDto != null) {
            creditScoreBean.setCreditId(creditScoreDto.getCreditId());
        }
        int creditScores = scoreCalculateService.getMaxScoresByCreditScoreBean(creditScoreBean);
        creditScoreBean.setCreditScores(creditScores);
        creditScoreBean.setLevelCode(queryLevelByScore(creditScores));
        creditScoreBean.setLevelName(queryLevelNameByScore(creditScores));



        logger.info("更新征信积分表开始，参数为{}", JSON.toJSON(creditScoreBean));
        int updateResult = -1;
        int countProject = 0;//统计拥有的项目数
        if (creditScoreBean.getZxId() != 0) {
            countProject++;
        }
        if (creditScoreBean.getXykId() != 0) {
            countProject++;
        }
        if (creditScoreBean.getGjjId() != 0) {
            countProject++;
        }
        if (creditScoreBean.getSbId() != 0) {
            countProject++;
        }
        creditScoreBean.setCountProject(countProject);
        if (creditScoreBean.getCreditId() == 0) {
            updateResult = mapper.insertCreditScore(creditScoreBean);
            //更新月表流水表
            updateMonthFlow(creditScoreBean);
        } else if (creditScoreBean.getXykId() != creditScoreDto.getXykId()||creditScoreBean.getGjjId() != creditScoreDto.getGjjId()||
                creditScoreBean.getSbId() != creditScoreDto.getSbId()||creditScoreBean.getZxId() != creditScoreDto.getZxId()||
                creditScoreBean.getCreditScores() != creditScoreDto.getCreditScores()) {

            updateResult = mapper.updateCreditScore(creditScoreBean);
            //更新月表流水表
            updateMonthFlow(creditScoreBean);
        }
        logger.info("更新征信积分表结束，更新数为{}", updateResult);

        if (updateResult == 1) {
            logger.info("更新社保完成,用戶={}", creditScoreBean.getCuserId());
        } else if(updateResult == 0){
            logger.info("更新社保失败,用戶={}", creditScoreBean.getCuserId());
        }
    }

    /**
     * 更新信用卡  更新代码
     *
     * @param creditScoreBean
     */
    public void creditCard(CreditScoreBean creditScoreBean) {
        logger.info("更新信用卡开始");
        CreditCardBean creditCardBean = new CreditCardBean();
        //查询征信积分表中数据
        CreditScoreDto creditScoreDto = mapper.queryCreditScore(creditScoreBean);
        int isXyk = 0;//返回信用卡标识
        try {
            List<CreditCardDto> creditCardDtos= mapper.queryBankBill(creditScoreBean);
            logger.info("获取bankbill表中数据{}",JSON.toJSON(creditCardDtos));
            if(creditCardDtos!=null&&creditCardDtos.size()>0) {
                double shouldPayment = 0;
                CreditCardDto maxCreditCardDto = creditCardDtos.get(0);//用来记录最大额度的卡信息
                for (CreditCardDto creditCardDto : creditCardDtos) {
                    if(creditCardDto.getShouldPayment() > 0) {
                        Integer repay = calculateScoreMapper.queryMaxMonthRepayStatus(creditCardDto.getBillId());
                        if (repay==null||repay!=1){
                            shouldPayment += creditCardDto.getShouldPayment();
                        }
                    }
                    if (maxCreditCardDto.getTotalNum() < creditCardDto.getTotalNum()) {
                        maxCreditCardDto = creditCardDto;
                    }
                }
                DecimalFormat df = new DecimalFormat("#0.00");
                if (maxCreditCardDto != null) {
                    creditCardBean.setAccount(maxCreditCardDto.getAccount());
                    creditCardBean.setTotalNum(maxCreditCardDto.getTotalNum());
                    creditCardBean.setTitle("应还总额：" + df.format(shouldPayment));
                    logger.info(creditScoreBean.getCuserId()+" 信用卡的个数:"+creditCardDtos.size());
                    creditCardBean.setCardNum(creditCardDtos.size());
                    logger.info("插入信用卡表开始，参数为{}", JSON.toJSON(creditCardBean));
                    if (creditScoreDto==null||creditScoreDto.getXykId()==0) {
                        isXyk = mapper.insertCreditCard(creditCardBean);
                        logger.info("插入信用卡表结束，插入数为{}", isXyk);
                        creditScoreBean.setXykId(creditCardBean.getCreditCardId());
                    } else {
                        creditCardBean.setCreditCardId(creditScoreDto.getXykId());
                        logger.info("插入信用卡表开始，参数为{}", JSON.toJSON(creditCardBean));
                        isXyk = mapper.updateCreditCard(creditCardBean);
                        logger.info("插入信用卡表结束，插入数为{}", isXyk);
                        creditScoreBean.setXykId(creditScoreDto.getXykId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("更新信用卡异常", e);
        }
    }
}
