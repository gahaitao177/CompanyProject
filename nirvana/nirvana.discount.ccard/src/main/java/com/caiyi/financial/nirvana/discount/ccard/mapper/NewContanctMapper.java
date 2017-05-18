package com.caiyi.financial.nirvana.discount.ccard.mapper;

import com.caiyi.financial.nirvana.discount.ccard.dto.NewContanctDto;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/10/25.
 */
public interface NewContanctMapper {

    //获得热门话题的列表
    @Select("select t.ctitle \"title\",t.cthemeurl \"accessurl\" from tb_theme t " +
            "where t.isdel=0 order by t.iorder asc")
    List<Map<String,Object>> getThemeList();
    //获得卡神攻略最近15条信息
    @Select("select * from (select * from tb_contact tc where tc.itype=0 and tc.iactive =0 and tc.ipublished =1 order by tc.dcreatedtime desc) t where rownum<=15")
    List<NewContanctDto> getContanctList();
}
