package com.youyu.demo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.youyu.demo.api.Provider;
import org.apache.http.impl.cookie.DateUtils;

import java.util.Date;

/**
 * Created by User on 2017/5/15.
 */
public class ProviderImpl implements Provider {
    public String build(String str) throws Exception {
		System.out.println("called");
        return Thread.currentThread().getName() + ":" + str + ": " + DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }
}
