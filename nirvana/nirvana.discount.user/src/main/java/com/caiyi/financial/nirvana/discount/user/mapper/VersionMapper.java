package com.caiyi.financial.nirvana.discount.user.mapper;

import com.caiyi.financial.nirvana.discount.user.bean.VersionBean;
import org.apache.ibatis.annotations.Select;

/**
 * Created by wenshiliang on 2016/9/12.
 */
public interface VersionMapper {

    @Select("select * from tb_version where " +
            "iclient=#{iclient,jdbcType=INTEGER } " +
//            "and appMgr=#{appMgr,jdbcType=INTEGER } " +
            "and source=#{source,jdbcType=INTEGER } " +
            "and packagename=#{packagename,jdbcType=VARCHAR } " +
            "and to_number(replace(appVersion,'.'))>to_number(replace(#{appVersion,jdbcType=VARCHAR},'.')) and del=0 and open=1")
    VersionBean queryVersion(VersionBean bean);
}
