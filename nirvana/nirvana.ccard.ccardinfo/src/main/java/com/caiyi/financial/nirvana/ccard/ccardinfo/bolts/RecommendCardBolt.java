package com.caiyi.financial.nirvana.ccard.ccardinfo.bolts;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.RecommendCardBean;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.RecommendCardDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.service.RecommendCardService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.github.pagehelper.Page;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by lizhijie on 2017/1/11. 推荐卡
 */
@Bolt(boltId = "recommendCard")
public class RecommendCardBolt extends BaseBolt {
    @Autowired
    private RecommendCardService cardService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }
    @BoltController
    public BoltResult queryRecommendCards(RecommendCardBean bean){
        BoltResult result=new BoltResult();
        JSONObject data=new JSONObject();
        data.put("pageSize",bean.getPs());
        data.put("pageNum",bean.getPn());
        if (CheckUtil.isNullString(bean.getKey())){
            bean.setKey("1");
        }
        Page<RecommendCardDto> cardList= cardService.queryRecommendCards(bean);
        if(cardList!=null){
            result.setDesc("查询推荐卡成功");
            result.setCode(BoltResult.SUCCESS);
            data.put("list",cardList.getResult());
            data.put("total",cardList.getTotal());
            data.put("pages",cardList.getPages());
            result.setData(data);
        }else {
            result.setDesc("沒有查詢到推荐卡");
            result.setCode(BoltResult.SUCCESS);
            data.put("total",0);
            data.put("pages",0);
            result.setData(data);
        }
        return result;
    }
    @BoltController
    public  BoltResult queryRecommendCardDetail(String cardId){
        BoltResult result=new BoltResult();
        RecommendCardDto recommendCard=cardService.queryRecommendCardDetail(cardId);
        if(recommendCard!=null){
            result.setCode(BoltResult.SUCCESS);
            result.setDesc("获得卡详情");
            result.setData(recommendCard);
        }else {
            result.setCode(BoltResult.SUCCESS);
            result.setDesc("没有找到此卡信息");
        }
        return result;
    }
    @BoltController
    public  BoltResult updateClickCount(RecommendCardBean bean){
        BoltResult result=new BoltResult();
        if(bean.getCardId()>0){
            int count=cardService.updateClickCount(bean.getCardId());
            if(count>0){
                result.setCode(BoltResult.SUCCESS);
                result.setDesc("更新成功");
            }else {
                result.setCode(BoltResult.ERROR);
                result.setDesc("更新失败");
            }
        }else {
            result.setCode(BoltResult.ERROR);
            result.setDesc("参数错误");
        }
        return  result;
    }
}
