package com.caiyi.financial.nirvana.ccard.ccardinfo.mapper;


import com.caiyi.financial.nirvana.ccard.ccardinfo.bean.H5ChannelBankBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface H5ChannelBankBeanMapper {

    @Select("select * from tb_h5channel_bank where istatus=1 and ichannelid=#{ichannelid,jdbcType=DECIMAL} order by iorder desc,iclicknum desc")
    List<H5ChannelBankBean> selectList(@Param("ichannelid") Long ichannelid);

    /**
     * 查询进度查询地址
     * @return
     */
    @Select("select t2.ibankid \"ibankid\",t2.cbankname \"bankName\",t1.cprogressaddr \"targetUrl\" from tb_handle_credit t1 " +
            "inner join tb_bank t2 on t1.ibankid=t2.ibankid ")
    List<Map<String,Object>> selectProgressUrl();

    @Update("update tb_h5channel_bank set iclicknum=iclicknum+1 where ibankid=#{ibankid,jdbcType=DECIMAL} and ichannelid=#{ichannelid,jdbcType=DECIMAL}")
    int clickBank(H5ChannelBankBean bean);
}