package com.caiyi.financial.nirvana.core.util;

import org.junit.Test;

/**
 * Created by wenshiliang on 2016/7/21.
 */
public class ZKConfigTest {

    @Test
    public void testGet() throws Exception {
        System.out.println(ZKConfig.get("/zktest/test5",101234));
    }

    @Test
    public void testGet1() throws Exception {

    }
}