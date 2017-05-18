package com.caiyi.financial.nirvana.bill.bank.guangda;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.bank.GuangDaBank;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.ccard.bill.bean.ResponseEntity;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by ljl on 2016/11/18.
 */
public class GuangDaBankTest extends AbstractBankServiceTest{

    public static void main(String[] args) throws Exception{
        GuangDaBankTest test = new GuangDaBankTest();
        Channel bean = test.createChannel();
        test.testLogin(bean);
    }

    private void testYzm(Channel bean,MemCachedClient mcc){
        httpService.setYzm(bean,mcc);
        System.out.println("请输入验证码:");
        String yzm = readConsole();
        bean.setBankRand(yzm);
    }

    private void checkSms(Channel bean, MemCachedClient mcc){
        testYzm(bean,mcc);
        httpService.checkSms(bean,mcc);
    }
    private void testLogin(Channel bean) throws Exception{
        MemCachedClient mcc = getMemCachedClient();
        //发送短信
        testSms(bean,mcc);

        checkSms(bean,mcc);
        System.out.println("result=="+bean.getBusiErrDesc());
        mcc.delete(bean.getCuserId() + bean.getBankId() + "guangda_cookieStore");
    }

    private Channel createChannel() {
        Channel bean = new Channel();
        bean.setIdCardNo("aC7FgBa+ogxevl5DnwuxzLu3JDsyL5+8cG2V3KeICOc=");
        bean.setBankPwd("Mx+YGtkClZBR6Si4dYbVMQ==");
        bean.setCuserId("test");
        bean.setBankId("3");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        return bean;
    }

    @Override
    protected AbstractHttpService createHttpService() {
        return new GuangDaBank();
    }
}
