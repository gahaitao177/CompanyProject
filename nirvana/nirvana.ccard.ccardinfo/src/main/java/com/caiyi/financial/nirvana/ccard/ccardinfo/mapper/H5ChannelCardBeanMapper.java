package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;


import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelCardBean;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface H5ChannelCardBeanMapper {
    @Select(" select * from tb_h5channel_card where istatus=1 and ishot=1 and ichannelid=#{ichannelid,jdbcType=DECIMAL} order by iorder desc,iclicknum desc")
    List<H5ChannelCardBean> selectListHit(@Param("ichannelid") Long ichannelid);

    @Select("select * from tb_h5channel_card where istatus=1 and iuseids like '%,'||#{iuseid,jdbcType=VARCHAR}||',%' and ichannelid=#{ichannelid,jdbcType=DECIMAL} order by iorder desc,iclicknum desc")
    Page<H5ChannelCardBean> selectListByUse(H5ChannelCardBean bean);


    @Update("update tb_h5channel_card set iclicknum=iclicknum+1,irealclicknum=irealclicknum+1 where icardid=#{icardid,jdbcType=DECIMAL} and ichannelid=#{ichannelid,jdbcType=DECIMAL}")
    int clickCard(H5ChannelCardBean bean);

    @Update("update tb_h5channel_bank set iclicknum=iclicknum+1 where ibankid=(select ibankid from tb_h5channel_card where ichannelid=#{ichannelid,jdbcType=DECIMAL} and icardid=#{icardid,jdbcType=DECIMAL}) and ichannelid=#{ichannelid,jdbcType=DECIMAL}")
    int clickBank(H5ChannelCardBean bean);
}