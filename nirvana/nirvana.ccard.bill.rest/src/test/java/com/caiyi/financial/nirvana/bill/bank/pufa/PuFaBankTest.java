package com.caiyi.financial.nirvana.bill.bank.pufa;

import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.bank.PuFaBank;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ljl on 2017/2/7.
 */
public class PuFaBankTest extends AbstractBankServiceTest{
    public static void main(String[] args) throws Exception {
        PuFaBankTest test = new PuFaBankTest();
        Channel bean = test.createChannel();
        test.testLogin(bean);
    }
    private void testYzm(Channel bean,MemCachedClient mcc){
        httpService.setYzm(bean,mcc);
        System.out.println("请输入验证码:");
        String yzm = readConsole();
        bean.setBankRand(yzm);
    }

    private void testLogin(Channel bean) throws Exception{
        MemCachedClient mcc = getMemCachedClient();
        testYzm(bean,mcc);
        httpService.login(bean,mcc);//调用加密接口,获取控件加密结果
        if (bean.getBusiErrCode()==2){
            testSms(bean,mcc);
        }else if (bean.getBusiErrCode()==3){

        }
    }

    private Channel createChannel() {
        Channel bean = new Channel();
        bean.setIdCardNo("341204198912052446");
        bean.setBankPwd("201666");
        bean.setCuserId("test");
        bean.setBankId("9");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        bean.setBankRand("2541212");
        return bean;
    }

    @Override
    protected AbstractHttpService createHttpService() {
        return new PuFaBank();
    }
}
