package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelCardUseBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface H5ChannelCardUseBeanMapper {

    @Select("select t1.iuseid,t1.ichannelid,t1.iorder,t1.picurl,t1.cusename,t1.csubtitle from tb_h5channel_card_use t1 " +
            "inner join tb_bank_use t2 on t1.iuseid=t2.iuseid " +
            "where ishot=1 and istatus=1 and ichannelid=#{ichannelid,jdbcType=DECIMAL} order by t1.iorder desc")
    List<H5ChannelCardUseBean> selectList(@Param("ichannelid") Long ichannelid);
}