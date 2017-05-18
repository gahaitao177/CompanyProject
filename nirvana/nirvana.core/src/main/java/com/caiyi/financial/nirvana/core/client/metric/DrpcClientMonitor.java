package com.caiyi.financial.nirvana.core.client.metric;

import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.codahale.metrics.MetricRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2017/3/8.
 */
@Aspect
public class DrpcClientMonitor {


    private MetricRegistry metricRegistry;


    public DrpcClientMonitor() {
        this.metricRegistry = new MetricRegistry();
        TimeReporter.forRegistry(metricRegistry).name("drpc-client-execute").everyTimeToRemove(true).build().start
                (60, TimeUnit.SECONDS);
    }

//    @Pointcut("execution(* com.caiyi.financial.nirvana.core.client.IDrpcClient.execute(..))")
//    public void executePointcut() {
//
//    }

     @Pointcut("execution(* com.caiyi.financial.nirvana.core.client.IDrpcClient.execute(..))")
     public void executePointcut() {

     }

    @Pointcut("execution(* com.caiyi.financial.nirvana.core.client.IDrpcClient.execute(..))")
    public void executePointcut2(){

     }


    @AfterThrowing("executePointcut()")
    public void execute(JoinPoint jp){
//        jp.getTarget()
        Object[] args = jp.getArgs(); // 获得参数列表
        StringBuilder builder = new StringBuilder(32);



        String drpcService;
        DrpcRequest drpcRequest;


        if(args.length==1){
            drpcService = ((IDrpcClient)jp.getTarget()).getDrpcService();
            drpcRequest = (DrpcRequest)args[0];

        }else if(args.length==2){
            if(args[0] instanceof DrpcRequest){
                drpcRequest = (DrpcRequest) args[0];
                drpcService = ((IDrpcClient) jp.getTarget()).getDrpcService();
            }else {
                drpcService = (String) args[0];
                drpcRequest = (DrpcRequest) args[1];
            }
        }else {
            drpcService = (String) args[0];
            drpcRequest = (DrpcRequest) args[1];
        }


        builder.append("drpc.execute.error.").append(drpcService).append(".").append(drpcRequest.getBolt()).append(".").append(drpcRequest.getMethod());
        String name = builder.toString();
        metricRegistry.counter(name).inc();
//        System.out.println("--------------------"+ metricRegistry.getCounters().get(name).getCount());

    }

//    @Before("executePointcut2()")
//    public void before(){
//        System.out.println("11111111111111");
//    }




}
