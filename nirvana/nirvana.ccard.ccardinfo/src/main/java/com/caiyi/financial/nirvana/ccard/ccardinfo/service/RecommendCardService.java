package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.RecommendCardBean;
import com.caiyi.financial.nirvana.ccard.ccardinfo.dto.RecommendCardDto;
import com.caiyi.financial.nirvana.ccard.ccardinfo.mapper.RecommendCardMapper;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lizhijie on 2017/1/11.
 */
@Service
public class RecommendCardService extends AbstractService {

    @Autowired
    RecommendCardMapper recommendCardMapper;

    /**
     * 通过 城市id 获得 推荐卡
     * @param bean
     * @return
     */
    public Page<RecommendCardDto> queryRecommendCards(RecommendCardBean bean){
        return recommendCardMapper.getRecommendCardsByCityCode(bean);
    }

    /**
     * 通过 cardId 卡id 获得卡详情
     * @param cardId
     * @return
     */
    public RecommendCardDto queryRecommendCardDetail(String cardId){
        return recommendCardMapper.getRecommendCardDetailById(cardId);
    }

    /**
     * 更新点击量
     * @param cardId
     * @return
     */
    public  int updateClickCount(int cardId){
        return recommendCardMapper.updatePicClickCount(cardId);
    }
}
