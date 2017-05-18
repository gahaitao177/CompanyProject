package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.Card;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.CardProgressDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.ChannelDao;
import com.caiyi.financial.nirvana.ccard.ccardinfo.mapper.ManualCardMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/7/4.
 */
@Service
public class ManualCardService extends AbstractService {

    @Autowired
    ManualCardMapper manualCardMapper;

    private final static String MD5_SMS_KEY = "6a37207e0fcd9237207e0fcd";

    /**
     * 卡申请发送验证码
     */
    public Map<String, String> cardApplySendYZM(Card card) {
        Map<String, String> resultMap = new HashMap<>();
        List<Map<String, Object>> bankState = manualCardMapper.queryBankState(card.getPhonenum());
        int count = 0;
        for (Map<String, Object> map : bankState) {
            if (map.get("ibankid") != null) {
                String success = map.get("ibankid") == null ? "" : map.get("ibankid").toString();
                if (map.get("ibankid").toString().equals(card.getBankid()) && "0".equals(success)) {
                    resultMap.put("busiErrCode", 1000 + "");
                    resultMap.put("busiErrDesc", "您已申请过该行信用卡，30天内不能再次申请!");
                    return resultMap;
                }
                if (!"0".equals(success)) {
                    count++;
                }
            }
        }
        if (count > 2) {
            resultMap.put("busiErrCode", 1001 + "");
            resultMap.put("busiErrDesc", "您暂时无法预约，建议您去银行柜台");
            return resultMap;
        }
        resultMap = sendYZM(card, resultMap);
        return resultMap;
    }

    /**
     * 发送验证码
     *
     * @param card
     * @param resultMap
     * @return
     */
    private Map<String, String> sendYZM(Card card, Map<String, String> resultMap) {
        StringBuffer str = new StringBuffer();
        str.append(card.getPhonenum());
        str.append(card.getTimestamp());
        str.append(MD5_SMS_KEY);
        String SeKey = null;
        try {
            SeKey = MD5Util.compute(str.toString());
        } catch (Exception e) {
            logger.error("验证码发送异常", e);
            e.printStackTrace();
        }
        logger.info("本地加--" + SeKey);
        logger.info("远程加密--" + card.getKey());
        if ((!card.getKey().equals(SeKey))) {
            resultMap.put("busiErrCode", 1001 + "");
            resultMap.put("busiErrDesc", "参数错误!");
            return resultMap;
        }
        // 如果手机号可用,发送验证码
        String yzm = CheckUtil.randomNum();

        Map<String, Object> mapPara = new HashMap<>();
        mapPara.put("mobileNo", card.getPhonenum());
        mapPara.put("yzm", yzm);
        mapPara.put("yzmType", 3 + "");
        mapPara.put("ipAddr", card.getIpAddr());

        logger.info("手机号： " + card.getMobileNo() + "    验证码：" + card.getYzm() + "IP地址" + card.getIpAddr());
        try {
            manualCardMapper.updateRegisterSendYZM(mapPara);
            resultMap.put("busiErrCode", mapPara.get("busiErrCode").toString());
            resultMap.put("busiErrDesc", mapPara.get("busiErrDesc").toString());
            logger.info("手机:" + card.getMobileNo() + "发送成功");
        } catch (Exception e) {
            resultMap.put("busiErrCode", 1001 + "");
            resultMap.put("busiErrDesc", "程序异常");
            logger.info("手机:" + card.getMobileNo() + "发送失败" + card.getBusiErrDesc());
        }
        return resultMap;
    }

    /**
     * 校验预约办卡短信验证码
     *
     * @param card
     */
    public Map<String, String> checkYZM(Card card) {
        Map<String, String> rMap = new HashMap<>();
        logger.info("进度查询验证码，手机号=" + card.getPhonenum() + ",yzm=" + card.getYzm());
        Map<String, String> map = new HashMap<>();
        map.put("mobileNo", card.getPhonenum());
        map.put("yzm", card.getYzm());
        map.put("yzmType", String.valueOf(3));
        try {
            manualCardMapper.registerCheckYZM(map);
            rMap.put("busiErrCode", map.get("busiErrCode"));
            rMap.put("busiErrDesc", map.get("busiErrDesc"));
            logger.info("手机号:" + card.getPhonenum() + ",验证结果:code=" + map.get("busiErrCode") + ",desc=" + map.get("busiErrDesc"));
        } catch (Exception e) {
            rMap.put("busiErrCode", String.valueOf(-1));
            rMap.put("busiErrDesc", "验证码验证异常");
            logger.info("手机号:{},异常:{}", card.getPhonenum(), e.getMessage());
        }
        return rMap;
    }

    /**
     * 提交预约人工办卡,可一次提交多个银行，bean.getBankid()中间用逗号隔开
     */
    public Map<String, String> cardAppliedInfo(Card card) {
        Map<String, String> rMap = new HashMap<>();
        Integer count;
        try {
            count = manualCardMapper.queryMessage(card.getPhonenum());
        } catch (Exception e) {
            rMap.put("busiErrCode", 0 + "");
            rMap.put("busiErrDesc", "程序异常");
            logger.info("手机号:" + card.getPhonenum() + ",查询异常:" + e.toString());
            return rMap;
        }
        if (count != null && count == 0) {
            rMap.put("busiErrCode", 0 + "");
            rMap.put("busiErrDesc", "10分钟内没有发送过验证码");
            logger.info("手机号:" + card.getPhonenum() + ",10分钟内没有发送过验证码");
            return rMap;
        }
        String[] bankIds = card.getBankid().split(",");

        if (bankIds.length == 1) {
            //每个手机号每个银行每月最多申请2次
            List<Map<String, Object>> bankState = manualCardMapper.queryBankState(card.getPhonenum());
            count = 0;
            for (Map<String, Object> map : bankState) {
                String bankIdDB = map.get("IBANKID") == null ? "" : map.get("IBANKID").toString();
                String isuccess = map.get("ISUCCESS") == null ? "" : map.get("ISUCCESS").toString();
                if (bankIdDB.equals(card.getBankid()) && "0".equals(isuccess)) {
                    rMap.put("busiErrCode", 1000 + "");
                    rMap.put("busiErrDesc", "您已申请过该行信用卡，30天内不能再次申请!");
                    return rMap;
                }
                if (!"0".equals(isuccess)) {
                    count++;
                }
            }
            if (count > 2) {
                rMap.put("busiErrCode", 1001 + "");
                rMap.put("busiErrDesc", "您暂时无法预约，建议您去银行柜台");
                return rMap;
            }
        }
        int ret = 0;
        for (String bankId : bankIds) {
            card.setBankid(bankId);
            try {
                if (1 == manualCardMapper.saveCardAppliedInfo(card)) {
                    ret++;
                }
            } catch (Exception e) {
                rMap.put("busiErrCode", 1000 + "");
                rMap.put("busiErrDesc", "程序异常");
                return rMap;
            }
        }

        if ("1".equals(card.getIsuccess())) {//资质不符合
            rMap.put("busiErrCode", -11 + "");
            rMap.put("busiErrDesc", "卡申请失败了");
            return rMap;
        }
        if ("2".equals(card.getIsuccess())) {//城市不匹配
            rMap.put("busiErrCode", -12 + "");
            rMap.put("busiErrDesc", "预约上门城市不是您当前所在地，请重新选择");
            return rMap;
        }

        String errorMsg = "";
        if (ret != bankIds.length) {
            errorMsg = "," + (bankIds.length - ret) + "家预约失败";
        }
        rMap.put("busiErrCode", 1 + "");
        rMap.put("busiErrDesc", "成功预约" + ret + "家银行" + errorMsg);
        return rMap;
    }

    /**
     * 查询用户收藏的银行
     *
     * @param card
     * @return
     */
    public List<Map<String, String>> queryUserBank(Card card) {
        try {
            return manualCardMapper.queryUserBank(card.getCitycode(), card.getPhonenum());
        } catch (Exception e) {
            logger.info("手机号:" + card.getPhonenum() + ",程序异常:" + e.toString());
            return null;
        }
    }

    /**
     * 办卡进度的验证码
     *
     * @param card
     * @return
     */
    public Map<String, String> applyProgressYzm(Card card) {
        Map<String, String> resultMap = new HashMap<>();
        sendYZM(card, resultMap);
        return resultMap;
    }

    /**
     * 查询办卡进度
     *
     * @param phonenum
     * @return
     */
    public List<Map<String, String>> queryProgressOfCard(String phonenum) {
        List<CardProgressDto> queryResult = null;
        List<Map<String, String>> returnResult = null;
        try {
            queryResult = manualCardMapper.queryProgressOfCard(phonenum);
        } catch (Exception e) {
            returnResult = null;
            logger.info("手机号=" + phonenum + ",程序异常:" + e.toString());
        }
        if (queryResult != null && queryResult.size() > 0) {
            returnResult = new ArrayList<>();
            for (CardProgressDto tmp : queryResult) {
                String iapplyid = tmp.getIapplyid();
                String bankname = tmp.getCbankname();
                String applyStatus = tmp.getApplyStatus();
                String isuccess = tmp.getIsuccess();
//                String orderStatus = tmp.getOrderStatus();
                String cadddate = tmp.getCadddate();
                String cphone = tmp.getCphone();
                String ststus = "";
                if ("0".equals(applyStatus)) {
                    ststus = "预约成功，等待信贷经理联系";
                } else if ("1".equals(applyStatus)) {//已经出售
                    ststus = "信贷经理已接单 " + cphone;
                } else {
                    if ("0".equals(isuccess)) {
                        ststus = "资料不符，暂时无法申办此银行信用卡";
                    } else if ("1".equals(isuccess)) {
                        ststus = "预约失败，暂时无法申办此银行信用卡";
                    } else if ("2".equals(isuccess)) {
                        ststus = "预约失败，您所在的城市暂时无法申请";
                    }
                }
                Map<String, String> data = new HashMap<>();
                data.put("iapplyid", iapplyid);
                data.put("bankame", bankname);
                data.put("status", ststus);
                data.put("date", cadddate);
                returnResult.add(data);
            }
        }
        return returnResult;
    }

    /**
     * 查询办卡进度详情
     *
     * @param iapplyid
     * @return
     */
    public Map<String, String> queryDetailProgressOfCard(String iapplyid) {
        CardProgressDto queryResult = null;
        Map<String, String> returnResult = null;
        try {
            queryResult = manualCardMapper.queryDetailProgressOfCard(iapplyid);
        } catch (Exception e) {
            returnResult = null;
            logger.info("申卡iapplyid=" + iapplyid + ",程序异常:" + e.toString());
        }
        if (queryResult != null) {
            returnResult = new HashMap<>();
            String applydate = queryResult.getCadddate();
            String applystatus = queryResult.getApplyStatus();
//            String isuccess = queryResult.getIsuccess();
            String buydate = queryResult.getBuyDate();
            String istatus = queryResult.getOrderStatus();
            String cloanname = queryResult.getCloanname();
            String cphone = queryResult.getCphone();

            String applystatusName = "";
            if ("1".equals(applystatus) || "0".equals(applystatus)) {
                applystatusName = "预约成功，等待信贷经理接单";
            } else {
                applystatusName = "预约失败，暂时无法申办此银行信用卡";
            }

            String statusName = "";
            if ("1".equals(applystatus)) {//已出售
                if ("1".equals(istatus)) {
                    statusName = "信贷经理已接单，等待联系";
                } else if ("2".equals(istatus)) {
                    statusName = "已联系客户";
                } else if ("3".equals(istatus)) {
                    statusName = "资料已提交银行";
                } else if ("4".equals(istatus)) {
                    statusName = "资料不全未提交银行";
                }
                if (StringUtils.isEmpty(statusName) && !StringUtils.isEmpty(cphone)) {
                    statusName = "已联系客户";
                }
            }

            if (!StringUtils.isEmpty(cloanname)) {
                cloanname = cloanname.substring(0, 1) + "经理";
            }
            returnResult.put("applydate", applydate);
            returnResult.put("applystatus", applystatusName);
            returnResult.put("buydate", buydate);
            returnResult.put("cloanname", cloanname);
            returnResult.put("cphone", cphone);
            returnResult.put("istatus", statusName);
        }
        return returnResult;
    }

    /**
     * 更新申卡的数量
     *
     * @param cardid
     * @return
     */
    public Map<String, String> updateCardApplyCounts(String cardid) {
        Map<String, String> map = new HashMap<>();
        int count;
        try {
            count = manualCardMapper.updateCardApplyTask(cardid);
            logger.info("更新申卡任务成功，更新条数" + count);
        } catch (Exception e) {
            count = -1;
            logger.info("更新申卡任务异常，" + e.toString());
        }
        map.put("count", count + "");
        return map;
    }

    /**
     * 查询渠道的内容
     *
     * @param channelId
     * @return
     */
    public List<ChannelDao> queryChannelContend(String channelId) {
        List<ChannelDao> channelDaos = new ArrayList<>();
        List<ChannelDao> channelDaosTmp;
        ChannelDao channelDao = new ChannelDao();
        channelDaosTmp = manualCardMapper.queryChannels(channelId);
        if (channelDaosTmp != null) {
            for (ChannelDao map : channelDaosTmp) {
                String channelid = map.getId();
                String channelName = map.getName();
                String contentNumber = map.getContentNumber();
                channelDaos.clear();
                ChannelDao channelDao2 = new ChannelDao();
                channelDao2.setId(channelId);
                channelDao2.setName(channelName);
                channelDao2.setContents(contentNumber);
                List<Map<String, String>> mapList = manualCardMapper.queryChannelContend(channelid);
                if (mapList == null || mapList.size() == 0) {
                    channelDao2.setDesc("该渠道暂无上架内容");
                } else {
                    channelDao2.setDesc("该渠道上架" + contentNumber + "条内容");
                    channelDao2.setData(mapList);
                }
                channelDaos.add(channelDao2);
            }
        } else {
            channelDao.setBusiErrCode(0 + "");
            channelDao.setBusiErrDesc("没有获得有效的渠道内容");
            channelDaos.add(channelDao);
        }
        return channelDaos;
    }
}
