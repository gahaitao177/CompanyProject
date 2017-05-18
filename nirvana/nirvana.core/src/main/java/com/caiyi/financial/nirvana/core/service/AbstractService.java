package com.caiyi.financial.nirvana.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenshiliang on 2016/4/21.
 * 抽象service
 */
public abstract class AbstractService {
    protected Logger logger = null;

    public AbstractService() {
        logger = LoggerFactory.getLogger(getClass());
    }
}
