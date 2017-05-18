package com.caiyi.financial.nirvana.core.client.pool;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.exception.ClientException;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.storm.thrift.TException;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Created by wenshiliang
 */
public class RemotePooledObjectDrpcClientImpl implements IDrpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePooledObjectDrpcClientImpl.class);

    private GenericKeyedObjectPool<String, DRPCClient> pool;

    private String drpcService;

    private static final int RETRY_SIZE = 1;

    private static final int DORMANT_SIZE = 3;
    private static final int DORMANT_SECOND = 10;

    ThreadLocal<Integer> retryCount = new ThreadLocal<>();




    public GenericKeyedObjectPool<String, DRPCClient> getPool() {
        return pool;
    }

    public void setPool(GenericKeyedObjectPool<String, DRPCClient> pool) {
        this.pool = pool;
    }

    public String getDrpcService() {
        return drpcService;
    }

    public void setDrpcService(String drpcService) {
        this.drpcService = drpcService;
        if(!drpcServiceMap.containsKey(drpcService)){
            drpcServiceMap.put(drpcService,true);
        }
    }


    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private static Map<String, Boolean> drpcServiceMap;

    static {
        drpcServiceMap = new ConcurrentHashMap<>();

        Class c = Constant.class;
        Field[] fields = c.getDeclaredFields();
        for (int i = 0; fields != null && i < fields.length; i++) {
            // 成员变量描述符
            String modifier = Modifier.toString(fields[i].getModifiers());
            if (modifier != null && modifier.indexOf("final") > -1) {
                // 是常量 添加到列表中/
                try {
                    drpcServiceMap.put(fields[i].get(c).toString(), true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addDrpcService(String... drpcServices){
        for (String drpcService : drpcServices) {
            drpcServiceMap.put(drpcService,true);
        }
    }


    public RemotePooledObjectDrpcClientImpl() {
    }

    /**
     * 检测服务是否可用，不可用休眠 DORMANT_SECOND （10） 秒，重试 DORMANT_SIZE （3） 次，返回false
     *
     * @param drpcService
     * @return
     */
    public boolean checkDrpcService(String drpcService) {
        Boolean flag = drpcServiceMap.get(drpcService);
        if (flag == null) {
            throw new ClientException("服务不存在");
        }
        for (int i = 0; i < DORMANT_SIZE; i++) {
            if (flag) {
                return flag;
            } else {
                try {
                    TimeUnit.SECONDS.sleep(DORMANT_SECOND);
                    flag = drpcServiceMap.get(drpcService);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }

    /**
     * 另起线程重试服务，直到可用
     *
     * @param drpcService
     */
    public void retryAvailable(String drpcService) {
        synchronized (drpcServiceMap){
            if (drpcServiceMap.get(drpcService)) {
                drpcServiceMap.put(drpcService, false);
                executorService.submit(() -> {
                    DRPCClient client = null;
                    long size = 0;
                    try {

                        while (true) {
                            try {
                                client = pool.borrowObject(drpcService);
                                size++;
                                TimeUnit.SECONDS.sleep(10);
                                client.execute(drpcService, "{\"bolt\":\"null\"}");
                                drpcServiceMap.put(drpcService, true);
                                LOGGER.info("[{}]服务请求正常,size[{}]", drpcService,size);
                                break;
                            } catch (TException e) {
                                LOGGER.warn("重试[{}]服务中,size[{}]；Exception：{}", drpcService,size, e.getMessage());
                            }finally {
                                pool.invalidateObject(drpcService,client);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("drpc重试服务异常", e);
                        drpcServiceMap.put(drpcService, true);
                    }
                });
            }
        }
    }


    @Override
    public String execute(DrpcRequest drpcRequest) {
        return execute(drpcService, drpcRequest);
    }

    @Override
    public <T> T execute(DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcRequest), clazz);
    }

    @Override
    public String execute(String drpcService, DrpcRequest drpcRequest) {
        return execute(drpcService, drpcRequest.toRequest());
    }

    public String execute(String drpcService, String drpcRequest) {
        DRPCClient client = null;
        boolean isDestroy = false;
        if (!checkDrpcService(drpcService)) {
            throw new ClientException(BoltResult.Error_405, "服务暂不可用，请稍后再试!!");
        }
        try {
            long start = System.currentTimeMillis();
            LOGGER.info("storm:{},request:{}", drpcService, drpcRequest);
            client = pool.borrowObject(drpcService);
            String result = client.execute(drpcService, drpcRequest);
            LOGGER.info("耗时: "+(System.currentTimeMillis()-start));
            LOGGER.debug(result);
            return result;
        } catch (TException e) {
            try {
                pool.invalidateObject(drpcService, client);
                isDestroy = true;
                LOGGER.info("销毁连接");
            } catch (Exception e1) {
                LOGGER.error("销毁client失败",e1);
            }
            retryAvailable(drpcService);
//            if (isRetry(e)) {
//                try {
//                    TimeUnit.SECONDS.sleep(30);
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//
//                }
//                LOGGER.info("drpc重试:{}", retryCount.get());
//                return execute(drpcService, drpcRequest);
//            }
            LOGGER.error("drpc服务服务暂不可用，TException",e);
            throw new ClientException(BoltResult.Error_405, "服务暂不可用，请稍后再试!!!");
        } catch (Exception e) {
            LOGGER.error("drpc请求失败,服务器异常",e);
            throw new ClientException(BoltResult.Error_405, "服务器异常!!!");
        } finally {
            if (client != null && !isDestroy) {
                pool.returnObject(drpcService, client);
            }
            retryCount.remove();
        }
    }


    public boolean isRetry(Exception e) {
        Integer count = retryCount.get();
        if (count == null) {
            count = 0;
        }
        if (count == RETRY_SIZE) {
            return false;
        }
        count++;
        retryCount.set(count);
        if (e instanceof TTransportException) {
            TTransportException te = (TTransportException) e;
            if (te.getType() == TTransportException.UNKNOWN) {
                if ("Read timed out".equals(te.getCause().getMessage().trim())) {
                    LOGGER.info("请求出错并重试请求", e);
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public <T> T execute(String drpcService, DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcService, drpcRequest), clazz);
    }

    @Override
    public void close()  {

    }
}
