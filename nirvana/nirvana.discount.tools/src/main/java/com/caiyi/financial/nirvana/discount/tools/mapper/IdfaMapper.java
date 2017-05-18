package com.caiyi.financial.nirvana.discount.tools.mapper;

import com.caiyi.financial.nirvana.discount.tools.bean.IdfaBean;
import com.caiyi.financial.nirvana.discount.tools.dto.IosPackageInfoDto;

import java.util.List;

/**
 * Created by zhukai on 2016/12/5.
 */
public interface IdfaMapper {
    //向数据库中插入一条数据一条
    Integer addIdfa(IdfaBean idfaBean);

    //根据APPID查询信息
    List<IdfaBean> queryIdfaByIdfa(String CAI_IDFA);


    /**
     * 根据渠道值获取包名 下载地址 icon
     * @param source
     * @return
     */

    List<IosPackageInfoDto> queryIosChannelInfo(String source,String type);

}
