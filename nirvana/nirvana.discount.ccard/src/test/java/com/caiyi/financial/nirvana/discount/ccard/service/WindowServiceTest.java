package com.caiyi.financial.nirvana.discount.ccard.service;

import com.caiyi.financial.nirvana.discount.ccard.bean.Window;
import com.caiyi.financial.nirvana.discount.ccard.core.TestSupport;
import org.junit.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by wenshiliang on 2016/12/9.
 */

public class WindowServiceTest extends TestSupport{

    @Autowired
    WindowService windowService;

    @Test
    public void startpage() throws Exception {
        Window bean = new Window();
        bean.setAppversion("240");
        bean.setPackagename("com.huishuaka.credit");
        bean.setSource(5000);
        windowService.startpage(bean);

    }

}