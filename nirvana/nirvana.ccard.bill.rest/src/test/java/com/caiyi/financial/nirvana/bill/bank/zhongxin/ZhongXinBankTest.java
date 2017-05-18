package com.caiyi.financial.nirvana.bill.bank.zhongxin;

import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.bank.ZhongXinBank;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;

/**
 * Created by ljl on 2016/12/16.
 */
public class ZhongXinBankTest extends AbstractBankServiceTest{
    public static void main(String[] args) {
        Channel bean = createChannel();
        ZhongXinBankTest test = new ZhongXinBankTest();
        test.testLogin(bean);
    }

    private void testYzm(Channel bean,MemCachedClient mcc) {
        httpService.setYzm(bean, mcc);
        System.out.println("请输入验证码:");
        String yzm = readConsole();
        bean.setBankRand(yzm);
    }

    private void testLogin(Channel bean){
        MemCachedClient mmc = getMemCachedClient();
        httpService.login(bean,mmc);
        if (2==bean.getBusiErrCode()){//需要短信验证
            testSms(bean,mmc);
        } else if (3==bean.getBusiErrCode()){//需要图片验证码
            testYzm(bean,mmc);
            httpService.loginAfter(bean,mmc);
        }else if (1==bean.getBusiErrCode()){//登录成功
            System.out.println("登录成功,开始解析账单>>>>>>>>>>>>>>>>>>>>>>");
        }else{//登录失败
            System.out.println("失败原因>>>"+bean.getBusiErrDesc());
        }
    }

    private static Channel createChannel() {
        Channel bean = new Channel();
        bean.setIdCardNo("Ag8ana7olO5cQpWaj7LW/Q==");
        bean.setBankPwd("js+5bYdhwTlsBzkfajlB1w==");
        bean.setCuserId("test");
        bean.setBankId("1");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        return bean;
    }

    @Override
    protected AbstractHttpService createHttpService() {
        return new ZhongXinBank();
    }
}
