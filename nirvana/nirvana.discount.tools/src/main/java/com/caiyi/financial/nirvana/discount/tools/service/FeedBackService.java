package com.caiyi.financial.nirvana.discount.tools.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.discount.tools.bean.FeedBackBean;
import com.caiyi.financial.nirvana.discount.tools.bean.IdfaBean;
import com.caiyi.financial.nirvana.discount.tools.mapper.FeedBackMapper;
import com.caiyi.financial.nirvana.discount.tools.mapper.IdfaMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.trident.testing.IFeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dengh on 2016/8/10.
 */
@Service
public class FeedBackService extends AbstractService {
    @Autowired
    FeedBackMapper feedBackMapper;
    @Autowired
    IdfaMapper idfaMapper;
   public Integer u_wrong_submit(FeedBackBean bean){
       try{
         Integer res = feedBackMapper.u_wrong_submit(bean);
           return  res;
       }catch (Exception e){
           e.printStackTrace();
           logger.error("用户纠错__插入错误",e);
       }
        return 0;
    }

    public Integer query_custom_service_Num(String icustomserviceid){
        try{
            Integer res = feedBackMapper.query_custom_service_Num(icustomserviceid);
            return  res;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("计算客服数目",e);
        }
        return 0;

    }

    public Integer update_custom_service(String icustomserviceid){
        try{
            Integer res = feedBackMapper.update_custom_service(icustomserviceid);
            return  res;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("更新客服数目",e);
        }
        return 0;

    }

    public Integer insert_custom_service(String icustomserviceid){
        try{
            Integer res = feedBackMapper.insert_custom_service(icustomserviceid);
            return  res;
        }catch (Exception e){
            e.printStackTrace();
            logger.error("插入客服数目",e);
        }
        return 0;

    }
    /**
     * 记录ios客户端idfa值
     * @param bean
     * @return
     */
    public JSONObject checkIosUserExist(FeedBackBean bean){
        String idfa = bean.getIdfa();
        JSONObject result = new JSONObject();
        if(StringUtils.isEmpty(idfa)){
            result.put("code","0");
            result.put("desc","参数不能为空");
            return  result;
        }
        JSONObject jsonObject = new JSONObject();
        String[] idfas=idfa.split(",");

        for (String id: idfas){
            if (checkIdfaExist(id)){
                jsonObject.put(id,"1");
            }else {
                jsonObject.put(id,"0");
            }
        }
        return jsonObject;
    }

    /**
     *
     * @param bean
     * @return
     */
    public JSONObject iosIdfaSave(FeedBackBean bean){
        JSONObject result = new JSONObject();
        String idfa = bean.getIdfa();
        logger.info("idfa:" + idfa + ",source:" + bean.getSource());
        if(CheckUtil.isNullString(idfa)){
            result.put("code","0");
            result.put("desc","参数不能为空");
            return result;
        }
        //
        try {
            if (checkIdfaExist(idfa)){
                result.put("code","1");
                result.put("desc","idfa已存在");
            } else {
                int savaC = feedBackMapper.saveIdfa(idfa,bean.getSource());
                if (savaC == 1){
                    result.put("code","1");
                    result.put("desc","保存成功");
                    result.put("data",checkIsFromAd(idfa));
                } else {
                    result.put("code","0");
                    result.put("desc","保存失败");
                }
            }
        } catch (Exception e) {
            logger.error("iosIdfaSave:" + idfa + "," +  bean.getSource(),e);
            result.put("code","0");
            result.put("desc","保存失败");
        }
        return result;
    }

    /**
     *  判定idfa是否来自广告商
     * @param idfa
     * @return
     */
    private String checkIsFromAd(String idfa){
        String callBackUrl = "";
        try {
            List<IdfaBean> queryResult = idfaMapper.queryIdfaByIdfa(idfa);
            if (queryResult != null && queryResult.size() > 0){
                IdfaBean idfaBean = queryResult.get(0);
                long timeNow = System.currentTimeMillis();
                String fbTimeStamp = idfaBean.getTimestamp();
                long timeStamp = Long.valueOf(fbTimeStamp);
                if (fbTimeStamp.length() == 11){
                    timeStamp = timeStamp * 100;
                }
                if (fbTimeStamp.length() == 10){
                    timeStamp = timeStamp * 1000;
                }
                logger.info(idfa + "hours:" + timeNow + "," +timeStamp);
                long days = (timeNow - timeStamp)/1000/60/60;
                if (days < 24){
                    callBackUrl = idfaBean.getCallback();
                }
                logger.info(idfa + "hours:" + days);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("checkIsFromAd:" +  idfa);
        }

        return callBackUrl;
    }
    /**
     *
     * @param idfa
     * @return
     */
    private boolean checkIdfaExist(String idfa){
        boolean isExist = false;
        try {
            if (CheckUtil.isNullString(idfa)){
                return false;
            }
            Map<String,Object> result = feedBackMapper.queryIsExists(idfa);
            logger.info("checkIdfaExist:" + idfa + ":" + result);
            int count = Integer.valueOf(result.get("num").toString());
            if (count > 0){
                isExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("checkIdfaExist error:",e);
        }
        return isExist;
    }
}
