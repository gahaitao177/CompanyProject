package com.caiyi.financial.nirvana.discount.ccard.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.discount.ccard.dto.NewContanctDto;
import com.caiyi.financial.nirvana.discount.ccard.dto.ResultDtoS;
import com.caiyi.financial.nirvana.discount.ccard.mapper.NewContanctMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/10/25. 有鱼 新卡神服务
 */
@Service
public class NewContanctService  extends AbstractService{

    @Autowired
    private NewContanctMapper newContanctMapper;
    /**
     * 查询热门话题列表和卡神列表
     * @return
     */
    public ResultDtoS queryThemesAndConstancts(){
        ResultDtoS result=new ResultDtoS();
        logger.info("进入有鱼 卡神新接口");
        List<Map<String,Object>> themes=newContanctMapper.getThemeList();
        JSONObject json=new JSONObject();
        if(themes!=null){
            json.put("themes",themes);
            logger.info("获得热门话题,条数为{}",themes.size());
        }else {
            logger.info("获得热门话题为空");
        }
        List<NewContanctDto> contanctDtoList=newContanctMapper.getContanctList();
        if(contanctDtoList!=null){
            json.put("contancts",contanctDtoList);
        }else {
            logger.info("获得卡神资讯为空");
        }
        if(themes==null&&contanctDtoList==null){
            result.setCode(ResultDtoS.FAIL);
            result.setDesc("没有查询到有效值");
        }else {
            result.setCode(ResultDtoS.SUCCESS);
            result.setDesc("查询成功");
            result.setData(json);
            logger.info("获得卡神信息成功");
        }
        return  result;
    }
}
