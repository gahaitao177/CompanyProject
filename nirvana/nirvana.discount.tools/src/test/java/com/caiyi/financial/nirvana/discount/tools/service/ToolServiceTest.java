package com.caiyi.financial.nirvana.discount.tools.service;

import com.caiyi.financial.nirvana.discount.tools.core.TestSupport;
import com.caiyi.financial.nirvana.discount.tools.dto.TbHomePageDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by pc on 2017/3/9.
 */
public class ToolServiceTest extends TestSupport{
    @Autowired
    private ToolService toolService ;

    @Test
    public void testSelectHomePage(){
        List<TbHomePageDto> list = toolService.selectHomePage("150100","BANNER");
        System.out.println(list);
    }



}
