package com.caiyi.financial.nirvana.bill.bank;

import com.caiyi.financial.nirvana.bill.base.AbstractHttpService;
import com.caiyi.financial.nirvana.ccard.bill.bean.Channel;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ljl on 2016/12/16.
 */
public abstract class AbstractBankServiceTest {
    public Logger logger = LogManager.getLogger();
    public AbstractHttpService httpService;

    public AbstractBankServiceTest() {
        httpService = createHttpService();
    }

    protected abstract AbstractHttpService createHttpService();

    public int testSms(Channel bean,MemCachedClient mcc){
        httpService.getSms(bean,mcc);
        System.out.println("请输入短信验证码:");
        String sms = readConsole();
        bean.setBankRand(sms);
        return httpService.checkSms(bean,mcc);
    }

    public String readConsole(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static MemCachedClient getMemCachedClient() {
        MemCachedClient client = new MemCachedClient();
        String[] addr = {"192.168.1.232:11211", "192.168.1.232:11211"};
        Integer[] weights = {10, 0};
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(addr);
        pool.setWeights(weights);
        pool.setInitConn(5);
        pool.setMinConn(5);
        pool.setMaxConn(200);
        pool.setMaxIdle(1000 * 30 * 30);
        pool.setMaintSleep(30);
        pool.setNagle(false);
        pool.setSocketTO(30);
        pool.setSocketConnectTO(0);
        pool.initialize();
        return client;
    }
}
