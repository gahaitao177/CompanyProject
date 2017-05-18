package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;

import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface H5ChannelBeanMapper {
    @Select("select * from tb_h5channel where istatus=1 and ichannelid=#{ichannelid,jdbcType=DECIMAL}")
    H5ChannelBean selectOne(@Param("ichannelid") Long ichannelid);

    @Update("update tb_h5channel set  iclicknum=iclicknum+1 where ichannelid=#{ichannelid,jdbcType=DECIMAL}")
    int iclickChannel(@Param("ichannelid") Long ichannelid);
}
