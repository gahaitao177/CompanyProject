package com.caiyi.financial.nirvana.core.zk.curator;

import com.caiyi.financial.nirvana.core.util.ZKConfig;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/10/9.
 * 测试curator实现的共享锁
 */
public class SharedLockTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedLockTest.class);


    @Test
    public void test1(){
        sharedLockTest();
    }

    @Test
    public void test2(){
        sharedLockTest();
    }

    @Test
    public void test3(){
        ExecutorService service = Executors.newCachedThreadPool();
        for(int i = 0;i<4;i++){
            service.submit(()->{
                sharedLockTest();
            });
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("程序结束");
    }
    InterProcessMutex sharedLock = new InterProcessMutex(ZKConfig.getClient(), "/test/test1");




    public void sharedLockTest(){

        LOGGER.info("等待获取锁");
        try {
            LOGGER.info("是否持有锁，{}",sharedLock.isAcquiredInThisProcess());
            sharedLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("获取完成锁输出");
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            sharedLock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("释放锁");
    }


    @Test
    public void test4(){
        InterProcessMutex sharedLock = new InterProcessMutex(ZKConfig.getClient(), "/test/test/t/t/t/t");
        try {
            LOGGER.info("11111111111");
            LOGGER.info("是否持有锁，{}",sharedLock.isAcquiredInThisProcess());
            sharedLock.acquire();
            LOGGER.info("22222222222");
            LOGGER.info("是否持有锁，{}",sharedLock.isAcquiredInThisProcess());
            sharedLock.acquire();
            LOGGER.info("3333333333333333");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
