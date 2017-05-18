package com.caiyi.nirvana.analyse.monitor.metric;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by rongkang on 2017-03-08.
 */
public class MetersTest {

    static final MetricRegistry metrics = new MetricRegistry();
    static int threshold_count = 10;//次数
    static int threshold_ctime = 5;//分钟
    public static void main(String args[]) {
        startReport();
        //metrics:事件总数，平均速率,包含1分钟，5分钟，15分钟的速率
        Meter requests = metrics.meter("requests");
        long count = 0;
        long start = System.currentTimeMillis();
        for(int i=1;i<=20;i++){
            //计数一次
            requests.mark();
//            try {
//                count = requests.getCount();
////                System.out.println(requests.getCount());
//                double minuteRate = requests.getOneMinuteRate();
//                System.out.println(minuteRate);//
//                int r = new Random().nextInt(20)*1000;
//                System.out.println("random:"+r);
//                if(minuteRate>threshold_count/(threshold_ctime*60)&&requests.getCount()>threshold_count){
//                    System.out.println("end");
//                    break;
//                }
//                System.out.println(count/((System.currentTimeMillis()-start)/1000D));
//                Thread.sleep(r);
//            } catch (InterruptedException e) {
//            }
//            if(i%1000==0){
//                try {
//                    Thread.sleep(1000);
//                }
//                catch(InterruptedException e) {}
//            }
        }
        System.out.println(System.currentTimeMillis()-start);
        wait5Seconds();
    }

    static void startReport() {
        //注册metrics,每个1秒打印metrics到控制台
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }

    static void wait5Seconds() {
        try {
            Thread.sleep(20*1000);
        }
        catch(InterruptedException e) {}
    }
}
