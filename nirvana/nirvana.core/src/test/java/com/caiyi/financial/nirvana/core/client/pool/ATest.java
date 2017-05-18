package com.caiyi.financial.nirvana.core.client.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wenshiliang on 2016/8/17.
 */
public class ATest {
    static  AtomicInteger currentNum = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        for(int i = 0;i<10;i++){
            service.submit(new A());
        }
    }
}
class A implements Runnable{

    @Override
    public void run() {
        while(true){
            System.out.println(ATest.currentNum.incrementAndGet());
        }

    }
}