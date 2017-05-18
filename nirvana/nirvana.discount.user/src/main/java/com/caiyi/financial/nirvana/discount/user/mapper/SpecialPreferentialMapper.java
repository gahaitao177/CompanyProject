package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.HomePageBean;
import com.caiyi.financial.nirvana.discount.user.bean.SpecialPreferentialBean;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by dengh on 2016/8/5.
 */
public interface SpecialPreferentialMapper extends BaseDao {
    @Select("select count(1) num from tb_special_preferential  ${sqlwhere}")
    Integer qurey_special_preferential_num(@Param("sqlwhere") String sqlwhere);
//    @Select(" select * from tb_special_preferential  ${sqlwhere}   order by cadddate desc ")
//    List<SpecialPreferentialBean>  qurey_special_preferential(@Param("adcode") String adcode,@Param("bankids") List<String> bankids);
    Page<SpecialPreferentialBean> qurey_special_preferential(HomePageBean bean);

}
