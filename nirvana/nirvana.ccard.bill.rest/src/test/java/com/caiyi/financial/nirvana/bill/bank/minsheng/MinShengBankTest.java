package com.caiyi.financial.nirvana.bill.bank.minsheng;

import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.bank.MinShengBank;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;

/**
 * Created by ljl on 2017/2/6.
 */
public class MinShengBankTest extends AbstractBankServiceTest {
    public static void main(String[] args) throws Exception {
        MinShengBankTest test = new MinShengBankTest();
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
        testYzm(bean,mcc);
        httpService.login(bean,mcc);
        if (bean.getBusiErrCode()==2){
            testSms(bean,mcc);
        }else if (bean.getBusiErrCode()==3){

        }
    }

    private Channel createChannel() {
        Channel bean = new Channel();
        bean.setDencryIdcard("6225230017825977");
        bean.setDencryBankPwd("9188cp123");
        bean.setCuserId("test");
        bean.setBankId("11");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        return bean;
    }

    @Override
    protected AbstractHttpService createHttpService() {
        return new MinShengBank();
    }
}
