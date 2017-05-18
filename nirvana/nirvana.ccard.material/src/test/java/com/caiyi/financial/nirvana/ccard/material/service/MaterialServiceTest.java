package com.caiyi.financial.nirvana.ccard.material.service;

import com.caiyi.financial.nirvana.ccard.material.mapper.MaterialMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by lizhijie on 2016/7/19.
 */
public class MaterialServiceTest extends TestSupport {


    @Autowired
    MaterialMapper mapper;

    @Test
    public void testFilterCard() throws Exception {
        mapper.query_filterCard(null);
        System.out.println("-----------------------------------------");
    }
}