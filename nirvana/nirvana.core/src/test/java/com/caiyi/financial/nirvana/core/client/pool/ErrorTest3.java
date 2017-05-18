package com.caiyi.financial.nirvana.core.client.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/8/23.
 */
public class ErrorTest3 {
    public static void main(String[] args) throws Exception {
//        GenericObjectPool<DRPCClient> pool = PoolProvider.getPool();


        RemotePooledObjectDrpcClientImpl drpcClient = new RemotePooledObjectDrpcClientImpl();
        drpcClient.setPool(PoolProvider.getPool2());

        Map<String,Boolean> drpcServiceMap = new HashMap<>();

//        drpcServiceMap.put("hskCcard",true);
        drpcServiceMap.put("hskUser",true);
//        drpcServiceMap.put("hskCcardInfo",true);

//        Set<String> set =  drpcServiceMap.keySet();
        while(true){
            for (String func : drpcServiceMap.keySet()){
                try {
                    String str = drpcClient.execute(func,"{\"bolt\":\"null\",\"data\":{\"ichannelid\":1},\"method\":\"index\"}");
                    System.out.println(str);
                }catch (Exception e){
                    e.printStackTrace();
                }
                TimeUnit.SECONDS.sleep(3);
            }
        }
//        drpcClient.execute()

//        URI


        //storm:hskCcardInfo,request:{"bolt":"h5ChannelBolt","data":{"ichannelid":1},"method":"index"}
    }
}
