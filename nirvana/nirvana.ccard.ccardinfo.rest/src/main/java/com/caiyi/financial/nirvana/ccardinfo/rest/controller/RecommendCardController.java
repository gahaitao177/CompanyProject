package com.caiyi.financial.nirvana.ccardinfo.rest.controller;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.RecommendCardBean;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.sun.org.apache.xpath.internal.operations.String;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by lizhijie on 2017/1/11.
 */
@RestController
public class RecommendCardController {

    @Resource(name = Constant.HSK_CCARD_INFO)
    private IDrpcClient drpcClient;

    @RequestMapping("/notcontrol/card/queryRecommendCards.go")
    public BoltResult queryRecommendCards(RecommendCardBean bean) {
//        if(bean.getPs()==null){
//            bean.setPs(10);
//        }
//        if(bean.getPageNum()==null){
//            bean.setPn(1);
//        }
        BoltResult result = drpcClient.execute(new DrpcRequest("recommendCard","queryRecommendCards",bean),BoltResult.class);
        if(result==null){
            result=new BoltResult();
            result.setDesc("程序异常");
            result.setCode(BoltResult.ERROR);
        }
        return result;
    }
    @RequestMapping("/notcontrol/card/queryRecommendCardDetail.go")
    public  BoltResult queryRecommendCardDetail(String cardId){
        BoltResult result = drpcClient.execute(new DrpcRequest("recommendCard","queryRecommendCardDetail",cardId),BoltResult.class);
        if(result==null){
            result=new BoltResult();
            result.setDesc("程序异常");
            result.setCode(BoltResult.ERROR);
        }
        return result;
    }
    @RequestMapping("/notcontrol/card/updateClickCount.go")
    public BoltResult updateClickCount(RecommendCardBean bean){
        BoltResult result = new BoltResult();
        if(bean.getCardId()<0){
            result.setCode(BoltResult.ERROR);
            result.setDesc("参数错误");
        }else {
            result = drpcClient.execute(new DrpcRequest("recommendCard", "updateClickCount",bean), BoltResult.class);
            if(result==null){
                result=new BoltResult();
                result.setDesc("程序异常");
                result.setCode(BoltResult.ERROR);
            }
        }
        return result;
    }
}
