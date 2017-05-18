package com.caiyi.financial.nirvana.discount.tools.mapper;

import com.caiyi.financial.nirvana.discount.tools.dto.CalculateParamDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by lizhijie on 2016/8/10.
 */
public interface FinancialMapper {
    //查询金融产品列表
    @Select("select * from tb_financial_products order by ipriority asc ")
    List<Map<String,Object>> queryFinancailProducts();
    //更新金融产品列表
    Integer updateProduct(Map<String,String> map);

    //通过类型来查询利率
    @Select("select itypeid \"itypeid\",ccityname \"ccityname\",crate \"crate\",cvalue \"cvalue\" from " +
            "TB_CALCULATE_PARAM where istate!=4 and itype=#{itype,jdbcType=INTEGER} order by to_number( itypeid)")
    List<CalculateParamDto> queryRateByType(@Param("itype") Integer itype);

    @Select("select itype \"itype\",cversion \"cversion\" from tb_app_startpage where itype=3 or itype=4")
        List<Map<String,Object>> queryCalversion();


}
