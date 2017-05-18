package com.caiyi.financial.nirvana.core.client.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.storm.thrift.TApplicationException;
import org.apache.storm.utils.DRPCClient;

/**
 * Created by wenshiliang on 2016/8/23.
 */
public class ErrorTest {
    public static void main(String[] args) throws Exception {
        GenericObjectPool<DRPCClient> pool = PoolProvider.getPool();


        DRPCClient client = pool.borrowObject();
        boolean flag = false;
        while (true) {
            try {
                String str = null;
                if (flag) {
                    flag = false;
                    str = client.execute("hskCcardInfo", "{\"bolt\":\"h5ChannelBolt\",\"data\":{\"ichannelid\":1},\"method\":\"index\"}");

                } else {
                    flag = true;
                    str = client.execute("hskLend", "{\"bolt\":\"null\",\"data\":{\"ichannelid\":1},\"method\":\"index\"}");

                }

                System.out.println(str);
                Thread.sleep(2000);
                System.out.println("-----------------------------------");
            } catch (Exception e) {
                e.printStackTrace();

                while (true) {
                    try {
                        Thread.sleep(2000);
                        String str = client.execute("hskLend", "{\"bolt\":\"null\",\"data\":{\"ichannelid\":1},\"method\":\"index\"}");
                        System.out.println(str);

                        return;
                    } catch (Exception e1) {
                        if(e1 instanceof TApplicationException){
                            System.out.println(11111);
                        }
                        e1.printStackTrace();
                    }
                }


            }

        }


        //storm:hskCcardInfo,request:{"bolt":"h5ChannelBolt","data":{"ichannelid":1},"method":"index"}
    }
}
