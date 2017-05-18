package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.BankPointBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dengh on 2016/8/1.
 */
public interface BankBillMapper extends BaseDao {
    // 查询商品图片
    @Select("select icard4num ,ibankid, ipoint from tb_bank_bill where isdel=0 and cuserid=#{cuserid,jdbcType=VARCHAR}")
    List<BankPointBean> query_jf_user_point(@Param("cuserid")String cuserid);

}
