package com.caiyi.financial.nirvana.bill.bank.shanghai;

import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.bank.ShangHaiBank;
import com.caiyi.financial.nirvana.bill.bank.pufa.PuFaBankTest;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.bill.base.LoginContext;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import com.hsk.cardUtil.HttpClientHelper;
import com.hsk.cardUtil.HttpResult;
import org.apache.http.impl.client.BasicCookieStore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ljl on 2017/2/21.
 */
public class ShangHaiBankTest extends AbstractBankServiceTest{
    public static void main(String[] args) throws Exception {
        ShangHaiBankTest test = new ShangHaiBankTest();
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

    @Test
    public void testIndex() throws Exception {
        ShangHaiBank service = (ShangHaiBank) createHttpService();
        String url = "https://ebanks.bankofshanghai.com/pweb/login.do";

    }
    private Channel createChannel() {
        Channel bean = new Channel();
        bean.setDencryIdcard("6259532007456277");
        bean.setDencryBankPwd("yfw5438125");
        bean.setCuserId("test");
        bean.setBankId("19");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        return bean;
    }

    @Override
    protected AbstractHttpService createHttpService() {
        return new ShangHaiBank();
    }
}
