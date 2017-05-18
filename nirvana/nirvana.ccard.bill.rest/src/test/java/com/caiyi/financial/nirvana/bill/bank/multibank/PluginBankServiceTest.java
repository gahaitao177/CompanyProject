package com.caiyi.financial.nirvana.bill.bank.multibank;

import com.caiyi.financial.nirvana.bill.bank.AbstractBankServiceTest;
import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.caiyi.financial.nirvana.core.util.StringUtils;
import com.danga.MemCached.MemCachedClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by ljl on 2016/11/21.
 * 插件网银登陆服务接口测试类
 */
public class PluginBankServiceTest extends AbstractBankServiceTest {
    public static void main(String[] args) throws Exception {
        PluginBankService service = new PluginBankService();
        PluginBankServiceTest test = new PluginBankServiceTest();
        Channel bean = test.createBean();
        test.testLogin(bean,service);
    }

    private Channel createBean() {
/*
        农业银行4:z4VVQw/REmflW5um/Rbn7g==;uETP097C8Je90sT/2/CShg==
        浦发银行9:sodFAv6CHvpaHDANrflLJBQ68YqmtMy5fFY5qbosWOQ=;+i/vHuzTjeNK1nJk46yM/w==
        民生银行11:wuOaGSdXAROqIkU76blUpru3JDsyL5+8cG2V3KeICOc=;uETP097C8Je90sT/2/CShg==
        上海银行19:qrCdrD6v07L7KW+iRgVfobu3JDsyL5+8cG2V3KeICOc=;Sr4g0fmzabIKgiEavh/utg==
        华夏银行8:tuuIJnBT7CQNRAnI0UwwGfSRIbrowOxbm17jVyFk9BQ=;Sr4g0fmzabIKgiEavh/utg==
*/
        Channel bean = new Channel();
        bean.setIdCardNo("z4VVQw/REmflW5um/Rbn7g==");
        bean.setBankPwd("uETP097C8Je90sT/2/CShg==");
        bean.setCuserId("test");
        bean.setBankId("4");
        bean.setIskeep("0");
        bean.setClient("1");
        bean.setType("0");
        return bean;
    }

    public void testLogin(Channel bean, PluginBankService service) throws Exception {
        MemCachedClient mcc = getMemCachedClient();
        int ret = service.startTask(bean,mcc);
        if(ret==0){
            if (3==bean.getBusiErrCode()){//需要图片
                ret = sendYzm(bean,service);
                while(ret==0){
                    if (2==bean.getBusiErrCode()){
                        sendSms(bean,service);
                    }else if (3==bean.getBusiErrCode()||5==bean.getBusiErrCode()){
                        ret = sendYzm(bean,service);
                    }
                }
            }else if (2==bean.getBusiErrCode()){//需要短信
               sendSms(bean,service);
            }
        }
    }

    public int sendYzm(Channel bean, PluginBankService service) throws Exception {
        MemCachedClient mcc = getMemCachedClient();
        String base64Img = service.getYzmBase64(bean);
        if(!StringUtils.isEmpty(base64Img)) {
            System.out.println("请输入图片验证码:");
            String bankRand = readConsole();
            bean.setBankRand(bankRand);
            int ret = service.submitYzm(bean,mcc);
            return ret;
        }
        return 0;
    }

    public void sendSms(Channel bean, PluginBankService service) throws Exception {
        MemCachedClient mcc = getMemCachedClient();
        service.getSms(bean);
        if (1==bean.getBusiErrCode()){
            System.out.println("请输入短信验证码:");
            String optRand = readConsole();
            bean.setOptRand(optRand);
            service.checkSms(bean,mcc);
        }
    }

    @Override
    protected AbstractHttpService createHttpService() {
        return new PluginBankService();
    }

}
