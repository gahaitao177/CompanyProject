package com.caiyi.financial.nirvana.ccard.investigation.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.ccard.investigation.constants.Constant;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditCardDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.CreditInvestigationDto;
import com.caiyi.financial.nirvana.ccard.investigation.dto.ProvidentFundDto;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.CalculateScoreMapper;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.CreditScoreMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lizhijie on 2016/12/19.
 */
@Service
public class CreditScoreCalculateService extends AbstractService {

    @Autowired
    CreditScoreMapper mapper;

    @Autowired
    CalculateScoreMapper calculateScoreMapper;

    /**
     * 征信表计算积分 包括两个，high:唯一得分和low:不唯一得分
     *
     * @param newBean
     * @return mapZX
     */
    public Map<String, Integer> calculateByZX(CreditInvestigationDto newBean) {
        Map<String, Integer> mapZX = new HashMap<>();
        int high = 0;
        int low = 0;
        if (newBean.getIsOverStay() == 1) {
            high += Constant.CREDIT_INVESTIGATION_NO_OVERSTAY_BIG;
            low += Constant.CREDIT_INVESTIGATION_NO_OVERSTAY_SMALL;
        }else if(newBean.getIsOverStay() == 0){
            high -= Constant.CREDIT_INVESTIGATION_NO_OVERSTAY_BIG;
            low -= Constant.CREDIT_INVESTIGATION_NO_OVERSTAY_SMALL;
        }
        if (newBean.getIsCard() == 1) {
            high += Constant.CREDIT_INVESTIGATION_HAVE_CARD_BIG;
            low += Constant.CREDIT_INVESTIGATION_HAVE_CARD_SMALL;
        }
        if (newBean.getIsLoan() == 1) {
            high += Constant.CREDIT_INVESTIGATION_HAVE_LOAN_BIG;
            low += Constant.CREDIT_INVESTIGATION_HAVE_LOAN_SMALL;
        }
        mapZX.put("high", high);
        mapZX.put("low", low);
        return mapZX;
    }

    /**
     * 根据信用卡的额度 来计算积分的大值和小值
     *
     * @param creditCard
     * @return
     */
    public Map<String, Integer> calculateCreditCard(CreditCardDto creditCard) {
        Map<String, Integer> mapCarditCard = new HashMap<>();
        int high = 0;
        int low = 0;
        if (creditCard.getTotalNum() < 10000) {
            high += Constant.CREDIT_CARD_TOTAL_0TO1_BIG;
            low += Constant.CREDIT_CARD_TOTAL_0TO1_SMALL;
        } else if (creditCard.getTotalNum() < 50000) {
            high += Constant.CREDIT_CARD_TOTAL_1TO5_BIG;
            low += Constant.CREDIT_CARD_TOTAL_1TO5_SMALL;
        } else if (creditCard.getTotalNum() < 100000) {
            high += Constant.CREDIT_CARD_TOTAL_5TO10_BIG;
            low += Constant.CREDIT_CARD_TOTAL_5TO10_SMALL;
        } else {
            high += Constant.CREDIT_CARD_TOTAL_OVER10_BIG;
            low += Constant.CREDIT_CARD_TOTAL_OVER10_SMALL;
        }
        mapCarditCard.put("high", high);
        mapCarditCard.put("low", low);
        return mapCarditCard;
    }

    /**
     * 计算公积金的积分 high值和low值
     *
     * @return
     */
    public Map<String, Integer> calculateProvidentFund(ProvidentFundDto fundBean) {
        Map<String, Integer> mapFund = new HashMap<>();
        int high = 0;
        int low = 0;
        if ("正常".equals(fundBean.getStatus())) {
            high += Constant.GJJ_STATUS_NORMAL_BIG;
            low += Constant.GJJ_STATUS_NORMAL_SMALL;
        }
        if (fundBean.getMounthTotal() < 500) {
            high += Constant.GJJ_MONTH_TOTAL500_LOW_BIG;
            low += Constant.GJJ_MONTH_TOTAL500_LOW_SMALL;
        } else if (fundBean.getMounthTotal() < 1000) {
            high += Constant.GJJ_MONTH_TOTAL1000_LOW_BIG;
            low += Constant.GJJ_MONTH_TOTAL1000_LOW_SMALL;
        } else {
            high += Constant.GJJ_MONTH_TOTAL1000_UP_BIG;
            low += Constant.GJJ_MONTH_TOTAL1000_UP_SMALL;
        }
        if (fundBean.getMounthNum() < 6) {
            high += Constant.GJJ_MONTHS6_LOW_BIG;
            low += Constant.GJJ_MONTHS6_LOW_SMALL;
        } else {
            high += Constant.GJJ_MONTHS6_UP_BIG;
            low += Constant.GJJ_MONTHS6_UP_SMALL;
        }
        mapFund.put("high", high);
        mapFund.put("low", low);
        return mapFund;
    }
    /**
     * 计算学信的积分
     *
     * @return
     */
    public int calculateChsiAccount(int educationLevel) {
        int xxScore = 0;
        if (educationLevel==1) {
            xxScore += Constant.XX_LEVEL_COLLEGE;
        } else if (educationLevel==2) {
            xxScore += Constant.XX_LEVEL_UNDERGRADUATE;
        } else if(educationLevel==3){
            xxScore += Constant.XX_LEVEL_MASTER;
        }else if(educationLevel==4){
            xxScore += Constant.XX_LEVEL_DOCTOR;
        }
        return xxScore;
    }
    /**
     * 通过积分bean 获得最大的积分
     *
     * @param scoreBean
     * @return maxScore
     */
    public Integer getMaxScoresByCreditScoreBean(CreditScoreBean scoreBean) {
        int maxScore = 0;
        boolean flag = false;
        ProvidentFundDto fundBean;
        CreditInvestigationDto zxBean;
        int fundScore = 0;  //以公积金为主题
        int cardScore = 0;  //以卡为主题
        int zxScore = 0;     //征信为主题
        int xxScore = 0;//学信积分（不区分主题）
        if (scoreBean.getGjjId() != 0) {
            flag = true;
            fundBean = calculateScoreMapper.getGjjBeanById(scoreBean.getGjjId());
            Map<String, Integer> map = new HashMap<>();
            if (fundBean != null) {
                JSONObject jsonFund = new JSONObject();
                JSONObject child = new JSONObject();
                jsonFund.put("公积金", child);
                child.put("状态", fundBean.getStatus());
                child.put("月缴额度", fundBean.getMounthTotal());
                child.put("缴纳月数", fundBean.getMounthNum());
                scoreBean.setFlowDesc(jsonFund.toJSONString());
                map = calculateProvidentFund(fundBean);
                if (fundBean.getAddMonth() != 0 && fundBean.getAddMonth() / 10 > 0) {
                    fundScore += fundBean.getAddMonth() / 10;
                    cardScore += fundBean.getAddMonth() / 10;
                    zxScore += fundBean.getAddMonth() / 10;
                }
            }
            if (map.get("high") != null) {
                fundScore += map.get("high");
                cardScore += map.get("low");
                zxScore += map.get("low");
            }

        }
        if (scoreBean.getZxId() != 0) {
            if (!flag) {
                flag = true;
            }
            zxBean = calculateScoreMapper.getCreditInvestigetionBeanById(scoreBean.getZxId());
            Map<String, Integer> zxMap = new HashMap<>();
            if (zxBean != null) {

                JSONObject jsonZX = new JSONObject();
                JSONObject child = new JSONObject();
                jsonZX.put("征信", child);
                child.put("无逾期", zxBean.getIsOverStay());
                child.put("有信用卡", zxBean.getIsCard());
                child.put("有贷款", zxBean.getIsLoan());
                if (StringUtils.isNotEmpty(scoreBean.getFlowDesc())) {
                    String tmp = scoreBean.getFlowDesc() + "," + jsonZX.toString();
                    scoreBean.setFlowDesc(tmp);
                } else {
                    scoreBean.setFlowDesc(jsonZX.toJSONString());
                }
                zxMap = calculateByZX(zxBean);
                if (zxBean.getAddMonth() != 0 && zxBean.getAddMonth() / 10 > 0) {
                    fundScore += zxBean.getAddMonth() / 10;
                    cardScore += zxBean.getAddMonth() / 10;
                    zxScore += zxBean.getAddMonth() / 10;
                }
            }
            if (zxMap.get("high") != null) {
                zxScore += zxMap.get("high");
                fundScore += zxMap.get("low");
                cardScore += zxMap.get("low");
            }
        }
        if (scoreBean.getXykId() != 0) {
            if (!flag) {
                flag = true;
            }
            if (scoreBean.getXykId() != 0) {
                CreditCardDto cardDto = calculateScoreMapper.getCreditCard(String.valueOf(scoreBean.getXykId()));
                if (cardDto != null) {
                    JSONObject jsonCard = new JSONObject();
                    JSONObject child = new JSONObject();
                    jsonCard.put("信用卡", child);
                    child.put("信用卡额度", cardDto.getTotalNum());
                    if (StringUtils.isNotEmpty(scoreBean.getFlowDesc())) {
                        String tmp = scoreBean.getFlowDesc() + "," + jsonCard.toString();
                        scoreBean.setFlowDesc(tmp);
                    } else {
                        scoreBean.setFlowDesc(jsonCard.toJSONString());
                    }
                    Map<String, Integer> cardMap = calculateCreditCard(cardDto);
                    if (cardMap.get("high") != null) {
                        cardScore += cardMap.get("high");
                        fundScore += cardMap.get("low");
                        zxScore += cardMap.get("low");
                    }
                }
            }
        }
        if (scoreBean.getSbId() != 0) {
            JSONObject jsonSB = new JSONObject();
            JSONObject child = new JSONObject();
            jsonSB.put("社保", child);
            child.put("有社保", 1);
            if (StringUtils.isNotEmpty(scoreBean.getFlowDesc())) {
                String tmp = scoreBean.getFlowDesc() + "," + jsonSB.toString();
                scoreBean.setFlowDesc(tmp);
            } else {
                scoreBean.setFlowDesc(jsonSB.toJSONString());
            }
            if (!flag) {
                flag = true;
            }
        }


        Integer educationLevel = mapper.queryEducationLevel(scoreBean.getCuserId());
        if(educationLevel!=null) {
            xxScore = calculateChsiAccount(educationLevel);
            if (!flag) {
                flag = true;
            }
        }

        maxScore = cardScore;
        if (maxScore < fundScore) {
            maxScore = fundScore;
        }
        if (maxScore < zxScore) {
            maxScore = zxScore;
        }
        if (flag) {
            maxScore = maxScore + Constant.ZX_BASE_NUM0 + Constant.ZX_BASE_NUM1 + xxScore;
        } else {
            return 0;
        }
        return maxScore;
    }
}
