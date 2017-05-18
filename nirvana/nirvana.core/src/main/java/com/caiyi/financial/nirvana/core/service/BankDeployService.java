package com.caiyi.financial.nirvana.core.service;

/**
 * Created by ljl on 2016/8/25.
 * 读取银行登录信息接口
 */
public interface BankDeployService {

    //读取某个银行配置信息
    String readBankConfig(String bankid)throws Exception;

    //读取所有银行配置信息
    String readBankConfig()throws Exception;
}
