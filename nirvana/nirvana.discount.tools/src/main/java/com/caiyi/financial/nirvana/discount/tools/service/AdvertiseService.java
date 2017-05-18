package com.caiyi.financial.nirvana.discount.tools.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.service.AbstractService;
import com.caiyi.financial.nirvana.core.util.CheckUtil;
import com.caiyi.financial.nirvana.core.util.MD5Util;
import com.caiyi.financial.nirvana.discount.tools.bean.IdfaBean;

import com.caiyi.financial.nirvana.discount.tools.dto.IosPackageInfoDto;
import com.caiyi.financial.nirvana.discount.tools.mapper.IdfaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhukai on 2016/12/5.
 */
@Service
public class AdvertiseService extends AbstractService {
    @Autowired
    IdfaMapper idfaMapper;

    public static final String TYPE_IOS_APPINFO = "PACKAGE_ICON";


    public JSONObject recordIdfa(IdfaBean idfaBean) {
        logger.info("Into>>>>>>>>>>>IdfaService");
        JSONObject result = new JSONObject();
        //判断数据库中是否存在相同的数据
        List<IdfaBean> idfaBeans = idfaMapper.queryIdfaByIdfa(idfaBean.getIdfa());
        if (idfaBeans == null || idfaBeans.size() == 0) {
            //不存在相同的数据，向数据库中添加。
            int i = idfaMapper.addIdfa(idfaBean);
            if (i == 1) {
                logger.info("IdfaService>>>>>>>>>>>更新成功");
                result.put("code", 0);
                result.put("result", "ok");
                return result;
            } else {
                logger.info("IdfaService>>>>>>>>>>>更新数据失败");
                result.put("code", 5);
                result.put("result", "更新数据失败");
                return result;
            }
        }
        logger.info("######################长度" + idfaBeans.size());
        //进行数据持久化
        result.put("code", 6);
        result.put("result", "idfa已存在");
        return result;
    }

    /**
     *  根据source值获取应用的 icon packagename downloadurl
     * @param source
     * @return
     */
    public BoltResult queryAppInfo(String source) {
        BoltResult boltResult = new BoltResult("1","success");
        List<IosPackageInfoDto> list = idfaMapper.queryIosChannelInfo(source,TYPE_IOS_APPINFO);
        if (list != null && list.size() >0){
            boltResult.setData(list.get(0));
        }
        return boltResult;
    }



}
