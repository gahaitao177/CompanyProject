package com.caiyi.financial.nirvana.discount.core.client;


import com.caiyi.financial.nirvana.core.client.LocalDrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by wenshiliang on 2016/4/22.
 */
public class LocalDrpcClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDrpcClientTest.class);

    public static void main(String[] args) {
//         StormUtil.DrpcConfig.newInstance();

//        LOGGER.info("-------------------------info");
//        LOGGER.debug("-------------------------debug");
//        LOGGER.error("-------------------------error");
//        LOGGER.warn("-------------------------warn");
//        System.out.println("-----------------"+LOGGER.isDebugEnabled());
//        System.out.println(LOGGER.getClass());
////        if(true){
////            return;
////        }
        LocalDrpcClient client = new LocalDrpcClient();
////        IDrpcClient client = new RemoteDrpcClient("192.168.1.207",3772,"demo_drpc");
////        IDrpcClient client = new RemoteDrpcClient("192.168.1.207",3772,"demo_drpc");
//        DrpcRequest request = new DrpcRequest();
////        request.setBolt("demo");
////        request.setData("");
////        request.setMethod("select2");
//
////        request.setBolt("demo");
////        request.setData("{\"t1\":\"测试添加\",\"t2\":\"测试添加t2\",\"clientwritetime\":\"t2\"}");
////        request.setMethod("add");
//
//        request.setBolt("demo");
//        request.setData("{\"dbwritetime\":\"测试添加\",\"t2\":\"测试添加t2\",\"clientwritetime\":\"t2\"}");
//        request.setMethod("add2");
//
//        Object object = client.execute(request);
//        System.out.println("-1111------------------------------" + object);
//        client.close();
    }

}