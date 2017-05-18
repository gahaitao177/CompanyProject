package com.caiyi.financial.nirvana.core.client.pool;

import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/8/25.
 */
public class T4 {
    public static void main(String[] args) {
        String str ="{\"bolt\":\"user\",\"data\":{\"accessToken\":\"84bb6a2f62704f6c96bc3fd3b4174029\",\"appId\":\"lcOMF2K0D1WF60E82U30V42000Y7L1PX0\",\"appVersion\":\"275\",\"bankId\":\"\",\"busiErrCode\":1,\"busiErrDesc\":\"success\",\"cityId\":\"\",\"cnickname\":\"\",\"collectType\":\"0\",\"coupon\":\"\",\"cuserId\":\"38c96d877fb84f2f8f1aef9cd3bac3ba\",\"cusername\":\"\",\"deviceType\":\"android\",\"endNum\":0,\"iclient\":0,\"imgYzm\":\"\",\"key\":\"\",\"leanCloudId\":\"\",\"modifFlag\":\"99\",\"newPwd\":\"\",\"oldPwd\":\"\",\"pwd\":\"pwd\",\"pwd9188\":\"75bdbf385afa692e9929a83f3df03f28\",\"rc\":0,\"source\":5000,\"startNum\":0,\"timeStamp\":\"\",\"tp\":0,\"yzm\":\"\",\"yzmType\":\"\"},\"method\":\"qCollectInfo\"}";
//        storm:hskUser,request:{"bolt":"user","data":{"accessToken":"787538c147bc4c2f938188397ced5863","appId":"lc201608ANCZQ2D4042UBT7M2UTLX6494","appVersion":"275","bankId":"1#2#3#4#5#7#8#9#10#11#13#14#15#16#18#19#21#41#42","busiErrCode":1,"busiErrDesc":"success","cityId":"101","cnickname":"","collectType":"0","coupon":"","cuserId":"6b8c7043223","cusername":"","deviceType":"android","endNum":0,"iclient":0,"imgYzm":"","key":"","leanCloudId":"3cc616914f132c3c3c30df4089a5134b","modifFlag":"99","newPwd":"","oldPwd":"","pwd":"pwd","pwd9188":"3fc2231aeb1500097d21ff8b14caab11","rc":0,"source":5000,"startNum":0,"timeStamp":"","tp":0,"yzm":"","yzmType":""},"method":"leanCloudUserBind"}

        //storm:hskUser,request:{"bolt":"user","data":{"accessToken":"84bb6a2f62704f6c96bc3fd3b4174029","appId":"lcOMF2K0D1WF60E82U30V42000Y7L1PX0","appVersion":"275","bankId":"","busiErrCode":1,"busiErrDesc":"success","cityId":"","cnickname":"","collectType":"0","coupon":"","cuserId":"38c96d877fb84f2f8f1aef9cd3bac3ba","cusername":"","deviceType":"android","endNum":0,"iclient":0,"imgYzm":"","key":"","leanCloudId":"","modifFlag":"99","newPwd":"","oldPwd":"","pwd":"pwd","pwd9188":"75bdbf385afa692e9929a83f3df03f28","rc":0,"source":5000,"startNum":0,"timeStamp":"","tp":0,"yzm":"","yzmType":""},"method":"qCollectInfo"}

        RemotePooledObjectDrpcClientImpl drpcClient = new RemotePooledObjectDrpcClientImpl();
        drpcClient.setPool(PoolProvider.getPool2());



        String result = drpcClient.execute("hskUser",str);
        System.out.println(result);

    }

    @Test
    public void test1(){
        ApplicationContext context = new ClassPathXmlApplicationContext("drpcclient-test1.xml");
        IDrpcClient client =  context.getBean(com.caiyi.financial.nirvana.core.client.pool.RemotePooledObjectDrpcClientImpl.class);
       while(true){


           try {
               String str = client.execute("drpc_heartbeat",new DrpcRequest());
               System.out.println(str);
               TimeUnit.SECONDS.sleep(10);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

    }
}
