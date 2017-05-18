package com.caiyi.financial.nirvana.core.ip;

import com.caiyi.financial.nirvana.core.util.SpringFactory;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wenshiliang on 2016/9/2.
 */
public class T {
    public static void main(String[] args) throws UnknownHostException {
       String addressIp = InetAddress.getLocalHost().getHostAddress();

        System.out.println(addressIp);
    }

    @Test
    public void test1(){
        String ip = SpringFactory.getAddressIp();
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(ip+":"+jvmName);
    }
}
