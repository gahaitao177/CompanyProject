package com.caiyi.nirvana.analyse.test;

/**
 * Created by been on 2017/3/6.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        DrpcTestService service = new DrpcTestService();
        service.testDrpc(host);
    }

}
