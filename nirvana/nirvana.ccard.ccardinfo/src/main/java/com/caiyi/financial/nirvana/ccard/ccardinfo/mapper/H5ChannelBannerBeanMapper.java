package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;


import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelBannerBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface H5ChannelBannerBeanMapper {

    @Select("select t1.ibannerid,t1.ctitle,t1.cpicurl,t1.curl from tb_h5channel_banner t1 " +
            "inner join tb_h5channel_banner_channel t2 on t1.ibannerid = t2.ibannerid " +
            "where t2.ichannelid=#{ichannelid,jdbcType=DECIMAL} and istatus=1 " +
            "order by t1.iorder desc")
    List<H5ChannelBannerBean> selectList(@Param("ichannelid") Long ichannelid);

}