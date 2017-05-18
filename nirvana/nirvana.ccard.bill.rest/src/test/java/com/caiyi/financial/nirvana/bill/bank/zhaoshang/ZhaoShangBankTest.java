package com.caiyi.financial.nirvana.bill.bank.zhaoshang;

import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.bank.ShangHaiBank;
import com.caiyi.financial.nirvana.bill.bank.ZhaoShangBank;
import com.caiyi.financial.nirvana.bill.bank.shanghai.ShangHaiBankTest;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import org.junit.Test;

/**
 * Created by ZhouPingHua
 * User：User
 * Date：2017/3/28.
 * Time: 11:00
 */
public class ZhaoShangBankTest extends AbstractBankServiceTest{
    public static void main(String[] args) throws Exception {
        ZhaoShangBankTest test = new ZhaoShangBankTest();
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
            testYzm(bean,mcc);
        }
    }

    private Channel createChannel() {
        Channel bean = new Channel();
        bean.setDencryIdcard("362502199106172615");
        bean.setDencryBankPwd("199166");

        bean.setCuserId("test");
        bean.setBankId("21");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        return bean;
    }
    @Override
    protected AbstractHttpService createHttpService() {
        return new ZhaoShangBank();
    }
}
