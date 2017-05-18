package com.caiyi.financial.nirvana.discount.core.client;


import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.LocalDrpcClient;
import com.caiyi.financial.nirvana.discount.ccard.bean.Commodity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by wenshiliang on 2016/4/22.
 */
public class LocalDrpcClientTest2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDrpcClientTest2.class);

    public static void main(String[] args) {
        LOGGER.info("-------------------------info");
        LOGGER.debug("-------------------------debug");
        LOGGER.error("-------------------------error");
        LOGGER.warn("-------------------------warn");
        System.out.println("-----------------"+LOGGER.isDebugEnabled());
        System.out.println(LOGGER.getClass());
//        if(true){
//            return;
//        }
        LocalDrpcClient client = new LocalDrpcClient();
//        IDrpcClient client = new RemoteDrpcClient("192.168.1.207",3772,"demo_drpc");
//        IDrpcClient client = new RemoteDrpcClient("192.168.1.207",3772,"demo_drpc");
        DrpcRequest request = new DrpcRequest();
//        request.setBolt("demo");
//        request.setData("");
//        request.setMethod("select2");

//        request.setBolt("demo");
//        request.setData("{\"t1\":\"测试添加\",\"t2\":\"测试添加t2\",\"clientwritetime\":\"t2\"}");
//        request.setMethod("add");

//        request.setBolt("demo");
//        request.setData("{\"dbwritetime\":\"测试添加\",\"t2\":\"测试添加t2\",\"clientwritetime\":\"t2\"}");
//        request.setMethod("add2");
//
//        Object object = client.execute(request);
//        System.out.println("-1111------------------------------" + object);
//        client.close();


        Commodity commodity=new Commodity();
        commodity.setCuserId("yrk6");
        commodity.setIbankid("1");
        String re=client.execute(new DrpcRequest("commodity","queryPointsAndBanks",""));
//        re.replaceAll("ibankid","bankid").replaceAll("cbankName","bankname")
//                .replaceAll("icard4num","cardnum").replaceAll("ipoint","points");
        client.close();
        System.out.println("re:"+re);
    }

}