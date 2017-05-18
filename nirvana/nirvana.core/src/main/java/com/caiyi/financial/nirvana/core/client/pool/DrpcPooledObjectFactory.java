package com.caiyi.financial.nirvana.core.client.pool;

import com.caiyi.financial.nirvana.core.client.pool.config.DrpcClientConf;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wenshiliang on 2016/6/14.
 */
@Deprecated
public class DrpcPooledObjectFactory extends BasePooledObjectFactory<DRPCClient> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DrpcPooledObjectFactory.class);

    private DrpcClientConf conf;
    private String drpcIp;
    private int drpcPort;
    private int timeout;
    private Map<String,Object> map;

    private AtomicInteger currentNum = new AtomicInteger(0);

    public DrpcPooledObjectFactory(Map<String,Object> map){
        this.map = map;
        drpcIp = map.get("drpcIp").toString();
        drpcPort = Integer.parseInt(map.get("drpcPort").toString());
        timeout = Integer.parseInt(map.get("timeout").toString());
        LOGGER.info("----------------------------\n初始化drpc client factory。\ndrpcIp:{}\ndrpcPort:{}\ntimeout:{}\n----------------------------",drpcIp,drpcPort,timeout);
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getDrpcPort() {
        return drpcPort;
    }

    public void setDrpcPort(int drpcPort) {
        this.drpcPort = drpcPort;
    }

    public String getDrpcIp() {
        return drpcIp;
    }

    public void setDrpcIp(String drpcIp) {
        this.drpcIp = drpcIp;
    }

    @Override
    public DRPCClient create() throws Exception {
        LOGGER.info("创建一个drpc client,当前连接数{}",currentNum.incrementAndGet());
        return new DRPCClient(map,drpcIp,drpcPort,timeout);
    }

    @Override
    public PooledObject<DRPCClient> wrap(DRPCClient obj) {
        return new DefaultPooledObject(obj);
    }

    @Override
    public PooledObject<DRPCClient> makeObject() throws Exception {
        return super.makeObject();
    }

    @Override
    public void destroyObject(PooledObject<DRPCClient> p) throws Exception {
        LOGGER.info("销毁一个drpc client,当前连接数{}",currentNum.decrementAndGet());
        DRPCClient client = p.getObject();
        client.close();
        super.destroyObject(p);
    }

    @Override
    public boolean validateObject(PooledObject<DRPCClient> p) {
        LOGGER.info("validateObject");
        return super.validateObject(p);
    }

//    @Override
//    public void activateObject(PooledObject<DRPCClient> p) throws Exception {
//        LOGGER.info("activateObject");
//
//        super.activateObject(p);
//    }
//
//    @Override
//    public void passivateObject(PooledObject<DRPCClient> p) throws Exception {
//        LOGGER.info("passivateObject");
//        super.passivateObject(p);
//    }
}
