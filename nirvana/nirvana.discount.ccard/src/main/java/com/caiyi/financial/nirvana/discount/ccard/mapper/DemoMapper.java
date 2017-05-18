package com.caiyi.financial.nirvana.discount.ccard.mapper;

import com.caiyi.financial.nirvana.core.service.BaseDao;
import com.caiyi.financial.nirvana.discount.ccard.bean.Demo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by wenshiliang on 2016/4/21.
 */
public interface DemoMapper extends BaseDao {
//    #{ipdicid,jdbcType=VARCHAR}
    @Insert("insert into bk_teset(dbwritetime, clientwritetime)values(#{t1,jdbcType=VARCHAR}, #{t2,jdbcType=VARCHAR})")
    int addTest(@Param("t1")String t1,@Param("t2")String t2);

    @Select("select * from bk_teset")
    List<Map<String,Object>> select();

    @Select("select * from bk_teset")
    List<Demo> select2();

    List<Demo> select3(Demo demo);

    List<Map<String,Object>> select4(Demo demo);
}
