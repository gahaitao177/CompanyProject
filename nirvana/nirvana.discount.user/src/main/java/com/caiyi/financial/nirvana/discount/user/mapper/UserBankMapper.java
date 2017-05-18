package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.UserBank;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 关注银行
 * Created by dengh on 2016/8/1.
 */
public interface UserBankMapper extends BaseDao {
    @Select("select * from tb_user_bank where cnickid=#{cnickid,  jdbcType=VARCHAR}")
    List<UserBank> query_userBank_cnickid(@Param("cnickid")String cnickid);

    @Insert("insert into tb_user_bank(cnickid, ibankid) values (#{cuserId,jdbcType=VARCHAR}, #{ibankid,jdbcType=VARCHAR})")
    int saveUserBank(@Param("cuserId")String cuserId,@Param("ibankid")String ibankid);
}
