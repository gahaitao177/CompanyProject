package com.caiyi.financial.nirvana.discount.tools.mapper;

import com.caiyi.financial.nirvana.discount.tools.dto.TbHomePageDto;
import com.datastax.driver.mapping.annotations.Param;

import java.util.List;

/**
 * Created by pc on 2017/3/9.
 */
public interface TbHomePageMapper  {
    /**
     * 根据城市码和tbhomepage的type 查询（banner）
     * @param adcode
     * @param type
     * @return
     */
    List<TbHomePageDto> selectHomePage( String adcode,String type);

}
