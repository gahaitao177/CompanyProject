package com.caiyi.financial.nirvana.ccard.investigation.mapper;

import com.caiyi.financial.nirvana.ccard.investigation.dto.BannerDto;
import com.caiyi.financial.nirvana.core.service.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/12/8. 信用生活
 */
public interface CreditLifeMapper extends BaseDao {

    @Select("select t.title,t.sub_title subTitle,t.param01 param01,t.param02 param02 ,t.pic_url picUrl " +
            " ,t.action_type actionType from tb_home_page t where t.type = 'YOUYUBANNER' and is_del =0 and is_hidden =0 order by t.iorder asc")
    List<BannerDto> queryBanners();

    List<BannerDto> queryPrivileges(@Param("cityId") String cityId);

    @Select("select ss.name \"name\" ,ss.is_open \"isOpen\" from tb_zx_sb_switch ss ")
    List<Map<String,String>> querySwitchs();

}
