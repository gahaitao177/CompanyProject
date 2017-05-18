package com.caiyi.financial.nirvana.discount.user.service;

import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.discount.user.bean.VersionBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wenshiliang on 2016/9/12.
 */
public class VersionServiceTest extends TestSupport{


    @Autowired
    VersionService versionService;

    @Test
    public void testQueryVersion() throws Exception {
        VersionBean bean = new VersionBean();
        bean.setIclient(0);
//        bean.setAppMgr(1);
        bean.setSource(5000);
        bean.setAppVersion("80");
        bean.setPackagename("com.huishuaka.credit.debug");
        VersionBean versionBean= versionService.queryVersion(bean);
        logger.info(versionBean.toJsonString());
    }
}