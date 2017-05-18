package com.caiyi.financial.nirvana.core.client.pool;

import com.caiyi.financial.nirvana.core.client.pool.config.DrpcClientConf;
import com.caiyi.financial.nirvana.core.client.pool.heartbeat.DrpcClientKeyedFactoryHeartbeatListener;
import com.caiyi.financial.nirvana.core.exception.ClientException;
import com.caiyi.financial.nirvana.heartbeat.client.DrpcServer;
import com.caiyi.financial.nirvana.heartbeat.client.Scheduler;
import com.caiyi.financial.nirvana.heartbeat.client.impl.DefaultSchedulerBuilder;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.storm.utils.DRPCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wenshiliang on 2016/8/24.
 */
public class DrpcClientKeyedFactory extends BaseKeyedPooledObjectFactory<String,DRPCClient> {


    private final static Logger LOGGER = LoggerFactory.getLogger(DrpcClientKeyedFactory.class);

    private DrpcClientConf conf;
    private int timeout;
    private Map<String,Object> map;
    private boolean standAlone;//drpc是否只有一台
    private List<DrpcServer> servers;
    private DrpcServer server;
    private Scheduler scheduler;


    public List<DrpcServer> getServers() {
        return servers;
    }

    public boolean replaceServers(List<DrpcServer> servers){
        this.servers = servers;
        return true;
    }

    private AtomicInteger currentNum = new AtomicInteger(0);

    public DrpcClientKeyedFactory(Map<String,Object> map){
        this.map = map;
        timeout = Integer.parseInt(map.get("timeout").toString());
        List<String> list = (List<String>) map.get("servers");
        if(list!=null){
            int serverSize = list.size();
            servers = new ArrayList<>(serverSize);
            if(serverSize==0){
                throw new ClientException("servers is empty");
            }else if(serverSize==1){
                standAlone = true;
                String[] strs = list.get(0).split(":");
                server = new DrpcServer(map,strs[0],Integer.parseInt(strs[1]),timeout);
            }else{
                standAlone = false;
                 list.forEach(s->{
                    String[] strs = s.split(":");
                     DrpcServer server = new DrpcServer(map,strs[0],Integer.parseInt(strs[1]),timeout);
                    servers.add(server);
                });
            }
        }else{
            standAlone = true;
            String drpcIp = map.get("drpcIp").toString();
            int drpcPort = Integer.parseInt(map.get("drpcPort").toString());
            server = new DrpcServer(map,drpcIp,drpcPort,timeout);
        }


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("----------------------------\n初始化drpc client factory。\n");
        if(standAlone){
            stringBuilder.append("单机 drpc ：\n").append(server.getHost()).append(":").append(server.getPort()).append("\n");
        }else{
            stringBuilder.append("多机 drpc ：\n");
            servers.forEach(s -> {
                stringBuilder.append(s.getHost()).append(":").append(s.getPort()).append("\n");
            });
            List<DrpcServer> serverArrayList = new ArrayList<>(servers);
            scheduler = new DefaultSchedulerBuilder().addDrpcServer(serverArrayList).setListener(new DrpcClientKeyedFactoryHeartbeatListener(this)).build();
            scheduler.start();
        }
        stringBuilder.append("timeout：").append(timeout);
        LOGGER.info(stringBuilder.toString());
    }

    @Override
    public DRPCClient create(String key) throws Exception {
        DrpcServer server;
        if(standAlone){
            server = this.server;
        }else{
            synchronized (servers){
                int i = ThreadLocalRandom.current().nextInt(0,servers.size());
                server = servers.get(i);
            }
        }
        LOGGER.info("create one drpc client:{},server:{}:{},当前连接数{}",key,server.getHost(),server.getPort(),currentNum.incrementAndGet());
        return new DRPCClient(map,server.getHost(),server.getPort(),timeout);
    }

    @Override
    public PooledObject<DRPCClient> wrap(DRPCClient value) {
        return new DefaultPooledObject(value);
    }

    @Override
    public void destroyObject(String key, PooledObject<DRPCClient> p) throws Exception {
        DRPCClient client = p.getObject();
        LOGGER.info("destroy one drpc client:{} [{}:{}],当前连接数{}",key,client.getHost(),client.getPort(),currentNum.decrementAndGet());
        client.close();
    }


//    static class Server{
//        private String ip;
//        private int port;
//
//        public Server() {
//        }
//
//        public Server(String ip, int port) {
//            this.ip = ip;
//            this.port = port;
//        }
//
//        public String getIp() {
//            return ip;
//        }
//
//        public void setIp(String ip) {
//            this.ip = ip;
//        }
//
//        public int getPort() {
//            return port;
//        }
//
//        public void setPort(int port) {
//            this.port = port;
//        }
//    }

}
