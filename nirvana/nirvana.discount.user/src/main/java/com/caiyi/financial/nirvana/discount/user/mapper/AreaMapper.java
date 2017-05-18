package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.AreaBean;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dengh on 2016/8/1.
 */
public interface AreaMapper extends BaseDao {
    @Select("select * from tb_area where iareatype=1 and " +
            "iareaid = #{iareaid, jdbcType=VARCHAR} ")
    List<AreaBean> qurey_area_adcode(AreaBean bean);

    @Select("select icityid from tb_district where" +
            "  clevel ='city' and icityid in (#{cityid0, jdbcType=VARCHAR},#{cityid1, jdbcType=VARCHAR}) order by icityid desc")
    Integer qurey_district(@Param("cityid0")String adcode0, @Param("cityid1")String cityid1 );


}
