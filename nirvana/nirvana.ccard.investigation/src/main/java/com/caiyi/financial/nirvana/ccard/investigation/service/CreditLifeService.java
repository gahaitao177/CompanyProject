package com.caiyi.financial.nirvana.ccard.investigation.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.ccard.investigation.bean.ProvidentFundBean;
import com.caiyi.financial.nirvana.ccard.investigation.bean.SocialInsuranceBean;
import com.caiyi.financial.nirvana.ccard.investigation.dto.BannerDto;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.CreditLifeMapper;
import com.caiyi.financial.nirvana.ccard.investigation.mapper.CreditScoreMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import com.hsk.common.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/12/8.
 */
@Service
public class CreditLifeService extends AbstractService {

    @Autowired
    private CreditLifeMapper creditLifeMapper;
    @Autowired
    CreditScoreMapper mapper;

    /**
     * 信用生活首页
     *
     * @return
     */
    public JSONObject queryCreditLife(CreditScoreBean scoreBean) {
        long start = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        //查询banners的列表
        List<BannerDto> banners = creditLifeMapper.queryBanners();
        jsonObject.put("banners", banners);

//        CreditScoreDto creditIdBean = mapper.queryUserRank(scoreBean.getCuserId());
//        Integer level = scoreService.queryLevelByScore(creditIdBean.getCreditScores());
        if (scoreBean.getAdcode() != null && scoreBean.getAdcode().length() == 6) {
            scoreBean.setAdcode(scoreBean.getAdcode().substring(0, 4) + "00");
        }
        List<BannerDto> privilegeDtos = creditLifeMapper.queryPrivileges(scoreBean.getAdcode());
        jsonObject.put("privileges", privilegeDtos);
        logger.info("特权和banner消耗时间:" + (System.currentTimeMillis() - start));
        return jsonObject;
    }

    /**
     * 公积金登录
     *
     * @param scoreBean
     * @return
     */
    public ProvidentFundBean getGjjBean(CreditScoreBean scoreBean) {
        ProvidentFundBean fundBean = new ProvidentFundBean();
        //请求公积金结果信息
        String url = SystemConfig.get("zxURL.gjj_url");
        logger.info("公积金地址:{}",url);
        Map<String, String> param = new HashMap<>();
        param.put("releaseVersion", "1.9.0");
//        param.put("from",String.valueOf(scoreBean.getSource()));
        param.put("from", "hsk");
        param.put("appId", scoreBean.getAppId());
        param.put("accessToken", scoreBean.getAccessToken());
        String result = null;
        try {
            result = HttpClientUtil.callHttpPost_Map(url, param);
        } catch (Exception e) {
            logger.error("请求公积金接口出错", e);
            return null;
        }
        logger.info("公积金内容:{}", result);
        JSONObject contendJson = JSONObject.parseObject(result);
        if (contendJson != null) {
            JSONObject data = contendJson.getJSONObject("results");
            if (data != null && data.getJSONArray("list") != null) {
                JSONObject gjjContend = data.getJSONArray("list").getJSONObject(0);
                if (gjjContend != null) {
                    String cpay = gjjContend.getString("cpay");
                    String cstate = gjjContend.getString("cstate");
                    String balance = gjjContend.getString("cbalance");
                    fundBean.setStatus(cstate);
                    if (StringUtils.isNotEmpty(balance)) {
                        fundBean.setTitle("总金额:" + String.format(balance, "%.2f"));
                    }
                    if (StringUtils.isNumeric(cpay)) {
                        fundBean.setMounthTotal(Double.parseDouble(cpay));
                    }
                    logger.info("月缴纳额度:{}", cpay);
                    logger.info("公积金缴纳状态:{}", cstate);
                    JSONArray records = gjjContend.getJSONArray("records");
                    int month = 0;
                    String lastUpdateMonth = "";
                    for (int i = 0; i < records.size(); i++) {
                        JSONArray json0 = records.getJSONObject(i).getJSONArray("record");
                        for (int j = 0; j < json0.size(); j++) {
                            JSONObject tem = json0.getJSONObject(j);
                            if (j == 0 && StringUtils.isEmpty(lastUpdateMonth)) {
                                lastUpdateMonth = tem.getString("item1");
                            }
                            String item2 = tem.getString("item2");
                            if (StringUtils.isNotEmpty(item2) && item2.contains("汇缴") && item2.contains("公积金")) {
                                month++;
                            }
                        }
                    }
                    fundBean.setMounthNum(month);
                    fundBean.setLastUpdateMonth(lastUpdateMonth);
                    logger.info("month 缴纳月数:{}", month);
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return fundBean;
    }

    /**
     * 获得社保数据
     *
     * @param scoreBean
     * @return
     */
    public SocialInsuranceBean getSbBean(CreditScoreBean scoreBean) {
        SocialInsuranceBean bean = new SocialInsuranceBean();
        String url = SystemConfig.get("zxURL.sb_url");
        logger.info("社保地址:{}",url);
        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("accessToken", scoreBean.getAccessToken());
        mapParams.put("appId", scoreBean.getAppId());
        mapParams.put("businessType", "1");
        mapParams.put("source", String.valueOf(scoreBean.getSource()));
        mapParams.put("releaseVersion", "1.9.6");
        String result = HttpClientUtil.callHttpPost_Map(url, mapParams);
        logger.info("请求的社保结果:{}", result);
        JSONObject json = JSONObject.parseObject(result);
        if (json != null && json.get("code") != null) {
            if (1 == json.getInteger("code")) {
                bean.setTitle("综合社保");
                return bean;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 开关标识
     *
     * @return
     */
    public List<Map<String, String>> getSwitch() {
        return creditLifeMapper.querySwitchs();
    }
}
