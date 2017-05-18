package com.caiyi.financial.nirvana.discount.user.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.annotation.BoltParam;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.CheapDto;
import com.caiyi.financial.nirvana.discount.user.dto.MarketCheapDto;
import com.caiyi.financial.nirvana.discount.user.service.CollectionService;
import org.apache.storm.task.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/9/6.
 */
@Bolt(boltId = "CollectionBolt", parallelismHint = 5, numTasks = 5)
public class CollectionBolt extends BaseBolt{

    @Autowired
    private CollectionService collectionService;

    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {

    }

    /**
     * 门店优惠
     * @param cuserId
     * @return
     */
    @BoltController
    public List<CheapDto> selectCheapDto(@BoltParam("cuserId") String cuserId){
        return collectionService.selectCheapDto(cuserId);
    }

    /**
     * 优惠劵
     * @param cuserId
     * @return
     */
    @BoltController
    public List<MarketCheapDto> selectMarketCheapDto(@BoltParam("cuserId")String cuserId){
        return collectionService.selectMarketCheapDto(cuserId);
    }

    /**
     * 关注银行
     * @param cuserId
     * @return
     */
    @BoltController
    public List<String> queryUserbankId(@BoltParam("cuserId")String cuserId){
        return collectionService.queryUserbankId(cuserId);
    }


    /**
     * 收藏
     * @param user
     * @return
     */
    @BoltController
    public BoltResult saveCollection(User user){
        int count = collectionService.saveCollection(user);
        return new BoltResult(BoltResult.SUCCESS,"同步成功"+count);
    }

    /**
     * 收藏删除
     * @param user
     * @return
     */
    @BoltController
    public BoltResult cheapCollectDel(User user){
        int count = collectionService.cheapCollectDel(user);
        return new BoltResult(BoltResult.SUCCESS,"收藏删除成功："+count);
    }

}
