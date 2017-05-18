package com.caiyi.financial.nirvana.bill.bank.nongye;

import com.caiyi.common.security.CaiyiEncrypt;
import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.bank.NongYeBank;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;

/**
 * Created by ljl on 2017/1/20.
 */
public class NongYeBankTest extends AbstractBankServiceTest{
    public static void main(String[] args) throws Exception{
        NongYeBankTest test = new NongYeBankTest();
        Channel bean = test.createChannel();
        test.testLogin(bean);
    }

    private void testYzm(Channel bean,MemCachedClient mcc) {
        httpService.setYzm(bean, mcc);
        System.out.println("请输入验证码:");
        String yzm = readConsole();
        bean.setBankRand(yzm);
    }

    private void testLogin(Channel bean) throws Exception{
        MemCachedClient mcc = getMemCachedClient();
       /* testYzm(bean,mcc);*/
        httpService.login(bean,mcc);//调用加密接口,获取控件加密结果*/
       /* testSms(bean,mcc);
        if (bean.getBusiErrCode()==2){
            testSms(bean,mcc);
        }*/
    }

    private Channel createChannel() {
        Channel bean = new Channel();
        bean.setDencryIdcard("volcano85");
        bean.setDencryBankPwd("9188cp123");
        bean.setCuserId("test");
        bean.setBankId("4");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        return bean;
    }

    @Override
    protected AbstractHttpService createHttpService() {
        return new NongYeBank();
    }
}
