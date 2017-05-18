package com.caiyi.financial.nirvana.discount.user.service;

import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.CheapDto;
import com.caiyi.financial.nirvana.discount.user.dto.MarketCheapDto;
import com.caiyi.financial.nirvana.discount.user.mapper.CollectionMapper;
import com.caiyi.financial.nirvana.discount.user.mapper.UserBankMapper;
import com.caiyi.financial.nirvana.discount.user.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//import com.caiyi.financial.nirvana.discount.user.dto.CollectionCheapDto;

/**
 * Created by wenshiliang on 2016/9/5.
 * 收藏接口
 * 优化
 */
@Service
public class CollectionService extends AbstractService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserBankMapper userBankMapper;



    @Transactional(rollbackFor = Exception.class)
    public int cheapCollectDel(User user){
        String storeId = user.getStoreId();
        String typeStr = user.getCollectType();
        String cuserId = user.getCuserId();

        String[] storeIds = storeId.split(",");
        int type =0;
        if(typeStr!=null){
            type = Integer.parseInt(typeStr);
        }
        int size = 0;
        for(String id : storeIds){
            int count = collectionMapper.deleteUserCollection(cuserId,id,type);
            if(count>0){
                if(type==0){
                    collectionMapper.updateBusinessCollection(id);
                }else{
                    collectionMapper.updateMarketCheapCollection(id);
                }
                size++;
                logger.info("cuserId:"+ cuserId +"收藏删除成功"+id+"type:"+type);
            }else {
                logger.error("cuserId:"+ cuserId +"收藏删除失败"+id+"type:"+type);
            }
        }
        return size;
    }

    /**
     * 门店优惠
     * @param cuserId
     * @return
     */
    public List<CheapDto> selectCheapDto(String cuserId){
        return collectionMapper.selectCheapDto(cuserId);
    }

    /**
     * 优惠劵
     * @param cuserId
     * @return
     */
    public List<MarketCheapDto> selectMarketCheapDto(String cuserId){
        return collectionMapper.selectMarketCheapDto(cuserId);
    }

    /**
     * 关注银行
     * @param cuserId
     * @return
     */
    public List<String> queryUserbankId(String cuserId){
        return userMapper.query_user_bankId(cuserId);
    }


    public int saveCollection(User user){
        String userId = user.getCuserId();
        String ibankid = user.getBankId();//银行
        String storeId = user.getStoreId();//门店
        String coupon = user.getCoupon();//优惠劵


        int bankCount = 0;
        int storeCount = 0;
        int couponCount = 0;
        if(StringUtils.isNoneEmpty(ibankid)){
            bankCount = saveUserBank(userId,ibankid.split("#"));

        }
        if(StringUtils.isNoneEmpty(storeId)){
            storeCount = saveCollection(userId,0,storeId.split("#"));

        }
        if(StringUtils.isNoneEmpty(coupon)){
            couponCount = saveCollection(userId,1,coupon.split("#"));

        }
        logger.info("用户[{}],关注银行[{}],收藏门店[{}],收藏优惠劵[{}]",userId,bankCount,storeCount,couponCount);
        return bankCount+storeCount+couponCount;
    }

    /**
     *
     * 保存关注银行
     * @param userId
     * @param bankids
     * @return
     */
    public int saveUserBank(String userId,String... bankids){

        List<String> existBankids = userMapper.query_user_bankId(userId);
        
        int count = 0;
        for (String bankid : bankids) {
            if(!existBankids.contains(bankid)){
                count += userBankMapper.saveUserBank(userId,bankid);
                existBankids.add(bankid);
            }
        }
        return count;
    }


    /**
     * 收藏 门店  优惠劵同步
     * @param userId
     * @param type 0 门店  1 优惠劵
     * @param collectionids
     * @return
     */
    public int saveCollection(String userId,int type,String... collectionids){
        List<String> existCollectionids = collectionMapper.queryCollectionId(userId,type);
        int count = 0;
        for (String id:collectionids){
            if(!existCollectionids.contains(id)){
                count += collectionMapper.saveUserCollection(userId,id,type);
                existCollectionids.add(id);
            }
        }
        return count;
    }




}
