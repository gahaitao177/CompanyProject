package com.caiyi.financial.nirvana.core.client.pool;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.financial.nirvana.core.bean.BoltResult;
import com.caiyi.financial.nirvana.core.bean.DrpcRequest;
import com.caiyi.financial.nirvana.core.client.IDrpcClient;
import com.caiyi.financial.nirvana.core.constant.Constant;
import com.caiyi.financial.nirvana.core.exception.ClientException;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.storm.thrift.TException;
import org.apache.storm.thrift.transport.TTransportException;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Created by wenshiliang on 2016/6/14.
 * RemotePooledObjectDrpcClientImpl 替代
 */
@Deprecated
public class RemoteDrpcClientImpl implements IDrpcClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDrpcClientImpl.class);

    private GenericObjectPool<DRPCClient> pool;

    private String drpcService;

    private static final int RETRY_SIZE = 3;

    ThreadLocal<Integer> retryCount = new ThreadLocal<>();

    public GenericObjectPool getPool() {
        return pool;
    }

    public void setPool(GenericObjectPool pool) {
        this.pool = pool;
    }

    public String getDrpcService() {
        return drpcService;
    }

    public void setDrpcService(String drpcService) {
        this.drpcService = drpcService;
    }


    private ExecutorService executorService;
    private Map<String,Boolean> drpcServiceMap;

    public RemoteDrpcClientImpl() {
        init();
    }

    public void init(){
        executorService = Executors.newCachedThreadPool();
        drpcServiceMap = new HashMap<>();

        Class c = Constant.class;
        Field[] fields = c.getDeclaredFields();
        for(int i=0;fields!=null && i<fields.length;i++) {
            // 成员变量描述符
            String modifier = Modifier.toString(fields[i].getModifiers());
            if (modifier != null && modifier.indexOf("final")> -1) {
                // 是常量 添加到列表中/
                try {
                    drpcServiceMap.put(fields[i].get(c).toString(),true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public boolean checkDrpcService(String drpcService){
        Boolean flag = drpcServiceMap.get(drpcService);
        if(flag==null){
            throw new ClientException("服务不存在");
        }
        for(int i =0;i<RETRY_SIZE;i++){
            if(flag){
                return flag;
            }else{
                try {
                    TimeUnit.SECONDS.sleep(5);
                    flag = drpcServiceMap.get(drpcService);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }

    /**
     * 重试服务，直到可用
     * @param drpcService
     */
    public void retryAvailable(String drpcService){

        synchronized (drpcServiceMap){
            drpcServiceMap.put(drpcService,false);
            executorService.submit(()->{
                DRPCClient client = null;
                try {
                    client = pool.borrowObject();
                    while(true){
                        try {
                            TimeUnit.SECONDS.sleep(10);
                            client.execute(drpcService,"{\"bolt\":\"null\"}");
                            drpcServiceMap.put(drpcService,true);
                            LOGGER.info("重试{}服务完成",drpcService);
                            break;
                        }catch (TException e){
                            LOGGER.warn("重试{}服务中；Exception：{}",drpcService,e.getMessage());
                        }

                    }


                } catch (Exception e) {
                    LOGGER.error("drpc重试服务异常",e);
                    drpcServiceMap.put(drpcService,true);
                }finally {
                    if(client!=null){
                        pool.returnObject(client);
                    }
                }
            });
        }
    }



    @Override
    public String execute(DrpcRequest drpcRequest) {
        return execute(drpcService,drpcRequest);
    }

    @Override
    public <T> T execute(DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcRequest),clazz);
    }

    @Override
    public String execute(String drpcService, DrpcRequest drpcRequest) {
        return execute(drpcService,drpcRequest.toRequest());
    }

    public String execute(String drpcService, String drpcRequest) {
        DRPCClient client = null;
        boolean isDestroy = false;
        if(!checkDrpcService(drpcService)){
            throw new ClientException(BoltResult.Error_405,"服务暂不可用，请稍后再试!!");
        }
        try {
            LOGGER.info("storm:{},request:{}",drpcService,drpcRequest);
            client = pool.borrowObject();
            String result = client.execute(drpcService,drpcRequest);
            return result;
        }catch (TException e){
            try {
                pool.invalidateObject(client);
                isDestroy = true;
                LOGGER.info("销毁连接");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            retryAvailable(drpcService);
            if(isRetry(e)){
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                LOGGER.info("drpc重试:{}",retryCount.get());
                return execute(drpcService,drpcRequest);
            }
            throw new ClientException(BoltResult.Error_405,"服务暂不可用，请稍后再试!!!");
        }catch (Exception e) {
//            if(isRetry(e)){
//                LOGGER.info("drpc重试:{}",retryCount.get());
//                return execute(drpcService,drpcRequest);
//            }
//            if(e instanceof TException){
//                LOGGER.error("drpc 连接出错了",e);
////                org.apache.storm.thrift.TApplicationException: execute failed: out of sequence response
////                TApplicationException
//                if(client!=null){
//                    try {
////                        LOGGER.info("----------处理client，是否放回连接池：{}", !isDestroy );
//                        pool.invalidateObject(client);
//                        isDestroy = true;
//                        LOGGER.info("销毁连接");
//                    } catch (Exception e1) {
//                        LOGGER.error("drpc 强制关闭出错",e1);
//                    }
//                }
//
//            }else{
//                LOGGER.error("execute 执行出错了",e);
//            }
            throw new ClientException(BoltResult.Error_405,"服务器异常!!!");
        } finally {
            if(client!=null && !isDestroy){
                pool.returnObject(client);
            }
            retryCount.remove();
        }
    }


    public boolean isRetry(Exception e){
        Integer count = retryCount.get();
        if(count==null){
            count = 0;
        }
        if(count==RETRY_SIZE){
            return false;
        }
        count++;
        retryCount.set(count);
        if(e instanceof TTransportException){
            TTransportException te = (TTransportException) e;
            if(te.getType()==TTransportException.UNKNOWN){
                if ("Read timed out".equals(te.getCause().getMessage().trim())){
                    LOGGER.info("请求出错并重试请求",e);
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public <T> T execute(String drpcService, DrpcRequest drpcRequest, Class<T> clazz) {
        return JSONObject.parseObject(execute(drpcService,drpcRequest),clazz);
    }

    @Override
    public void close() {

    }
}
