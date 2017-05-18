package com.caiyi.financial.nirvana.ccard.ccardinfo.service;

import com.caiyi.financial.nirvana.TestSupport;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wenshiliang on 2016/6/20.
 */
public class H5ChannelServiceTest extends TestSupport {

    @Autowired
    private H5ChannelService h5ChannelService;

    @Test
    public void testSelectIndex() throws Exception {
        BoltResult result = h5ChannelService.selectIndex(1l);

        System.out.println(result.toJsonString());
    }

    @Test
    public void testSelectCard() throws Exception {
    }

    @Test
    public void testClickCard() throws Exception {
//        H5ChannelCardBean bean = new H5ChannelCardBean();
//        bean.setIcardid(3509l);
//        bean.set
//        int count = h5ChannelService.clickCard(bean);
    }
}