package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.user.bean.HomePage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dengh on 2016/8/1.
 */
public interface HomePageMapper extends BaseDao {
    @Select("select * from tb_home_page where is_hidden=0 and is_del=0 and ( city_code = 'all' or city_code" +
            " like #{fuzzy, jdbcType=VARCHAR} ) order by type,iorder desc")
    List<HomePage> qurey_homePage_fuzzy(@Param("fuzzy")String city_code);


    @Select("select * from tb_home_page where is_hidden=0 and is_del=0 and ( city_code = 'all' or city_code\n" +
            "            like #{cityCode, jdbcType=VARCHAR} ) \n" +
            "            and type='SERVICE_BANNER'\n" +
            "            order by type,iorder desc")
    List<HomePage> queryHomePageByServiceBanner(@Param("cityCode")String cityCode);


    @Select("select * from tb_home_page where is_hidden=0 and is_del=0 and ( city_code = 'all' or city_code\n" +
            "            like #{cityCode, jdbcType=VARCHAR} ) \n" +
            "            and type=#{ctype, jdbcType=VARCHAR} " +
            "            order by type,iorder desc")
    List<HomePage> queryHomePage(@Param("cityCode")String cityCode,@Param("ctype")String ctype);

}
