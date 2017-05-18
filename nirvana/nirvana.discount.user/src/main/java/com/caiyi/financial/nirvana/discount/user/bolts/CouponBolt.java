package com.caiyi.financial.nirvana.discount.user.bolts;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.annotation.BoltController;
import com.caiyi.financial.nirvana.core.service.BaseBolt;
import com.caiyi.financial.nirvana.discount.user.bean.*;
import com.caiyi.financial.nirvana.discount.user.service.UserService;
import org.apache.storm.task.TopologyContext;

import java.util.List;
import java.util.Map;
/**
 * Created by dengh on 2016/8/26.
 *
 */
@Bolt(boltId = "CouponBolt", parallelismHint = 2, numTasks = 2)
public class CouponBolt  extends BaseBolt{
    private UserService userService;
    @Override
    protected void _prepare(Map stormConf, TopologyContext context) {
        userService = getBean(UserService.class);
    }

    /**查找关注银行**/
    @BoltController
    public  List<String> focusBanks(User bean){
        String retXml = "";
        List<String> bankJrs= userService.query_user_bankId(bean);
        logger.info("查询关注银行数据结束--" + retXml);
        return  bankJrs;
    }
    /**查找商店Logo***/
    @BoltController
    public List<StoreLogo> storelogo(User bean){
        List<StoreLogo> collectJrs = userService.query_store_logo(bean);
        logger.info("查找商店Logo");
        return collectJrs;

    }
    /***查找商店优惠****/
    @BoltController
    public  List<StoreCheapBean>  storeCheap(Map<String,String>storeId){


        List<StoreCheapBean>  storeCheaps =  userService.query_store_cheap(storeId.get("storeId"));
        return  storeCheaps;
    }
    /****查找优惠超市**/
    @BoltController
    public  List<MarketBean>  queryMarket(User bean){
        List<MarketBean> marketBeanList = userService.query_market(bean);
        return marketBeanList;
    }
    /***查找超市优惠券****/
    @BoltController
    public List<CouponBean>  userCoupon(Map<String,String> map){

        List<CouponBean> couponBeanList = userService.query_user_Coupon(map.get("cuserId"),map.get("markeiId"));
        return couponBeanList;
    }
    /****绑定银行***/
    @BoltController
    public  Map<String,String> bankBind(User bean){
        Map<String,String> res = userService.bankBind(bean.getCuserId(), bean.getBankId());
        return  res;
    }
    /***用户添加收藏****/
    @BoltController
    public  Map<String,String> collectAdd(User bean){
        Map<String,String> res = userService.collectAdd(bean);
        return  res;
    }
    /****门店过期收藏***/
    @BoltController
    public List<Integer> query_collect_id(User bean){
        List <Integer> res = userService.query_collect_id(bean);
        return  res;
    }
    @BoltController
    public  Integer  collectionOverdue(Map<String,String> map){
        return userService.update_user_collection_overdue(map.get("cuserId"),map.get("storeId"));
    }
    @BoltController
    public  Integer collectExpire(User bean){
        return  userService.update_collect_expire(bean);

    }








}
