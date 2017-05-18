package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.HskUserBean;
import com.caiyi.financial.nirvana.discount.user.bean.User;
import com.caiyi.financial.nirvana.discount.user.dto.TokenDto;
import com.caiyi.financial.nirvana.discount.user.dto.UserDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by lizhijie on 2017/2/8.
 */
public interface HskUserMapper extends BaseDao {

    int insertUserInfo(User user);

    int insertUserSource(User user);

    @Select(" select count(1) from tb_user tu where tu.cuserid=#{cuserId,jdbcType=VARCHAR}")
    int queryUserById(@Param("cuserId") String cuserId);

    TokenDto queryToken(HskUserBean hskUserBean);

    UserDto queryUserByCuserId(@Param("cuserId") String cuserId);

    int bindIcon(HskUserBean hskUserBean);
}
