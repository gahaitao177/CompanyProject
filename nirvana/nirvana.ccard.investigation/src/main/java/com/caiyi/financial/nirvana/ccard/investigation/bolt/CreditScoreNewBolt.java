package com.caiyi.financial.nirvana.ccard.investigation.bolt;

import com.caiyi.financial.nirvana.ccard.investigation.bean.CreditScoreBean;
import com.caiyi.financial.nirvana.ccard.investigation.service.YouyuCreditService;
import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 查询征信分数新接口
 * Created by chenli on 2017/3/23.
 */
@Bolt(boltId = "newCreditScoreBolt" , parallelismHint = 1,numTasks = 1)
public class CreditScoreNewBolt extends BaseBolt {

    @Autowired
    private YouyuCreditService youyuCreditService;

    /**
     * 查询征信分数
     * @param creditScoreBean
     * @return 征信分数
     */
    @BoltController
    public BoltResult getFinalCreditScore(CreditScoreBean creditScoreBean){
        logger.info("征信分数计算开始：");
        BoltResult boltResult = new BoltResult();
        double newCreditScore =  youyuCreditService.creditScoreIndex(creditScoreBean);
        if(newCreditScore<0){
            boltResult.setCode("0");
            boltResult.setData("此用户占无分数");
            boltResult.setDesc("此用户暂无分数");
            return boltResult;
        }
        boltResult.setCode("1");
        boltResult.setData(newCreditScore);
        boltResult.setDesc("查询成功");
        return boltResult;
    }

}
