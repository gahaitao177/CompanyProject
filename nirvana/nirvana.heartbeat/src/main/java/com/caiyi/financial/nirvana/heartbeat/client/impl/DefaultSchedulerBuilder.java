package com.caiyi.financial.nirvana.heartbeat.client.impl;

import com.caiyi.financial.nirvana.heartbeat.client.DrpcServer;
import com.caiyi.financial.nirvana.heartbeat.client.HeartbeatClient;
import com.caiyi.financial.nirvana.heartbeat.client.Listener;
import com.caiyi.financial.nirvana.heartbeat.client.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wenshiliang on 2016/10/11.
 */
public class DefaultSchedulerBuilder {
    public Scheduler build() {
        if (drpcServerList.isEmpty()) {
            throw new RuntimeException("尼玛！监听的drpc不能为空啊!");
        }
        if (listenerList == null) {
            throw new RuntimeException("尼玛！你都不加监听玩毛线啊！");
        }
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(5);
        }
        if (intervalUnit == null) {
            intervalUnit = TimeUnit.MINUTES;
        }
        if (interval <= 0) {
            interval = 1;
        }
        DefaultScheduler scheduler = new DefaultScheduler(drpcServerList, interval, intervalUnit, listenerList, executorService);
        return scheduler;
    }

    private List<DrpcServer> drpcServerList = new ArrayList<>();
    private ExecutorService executorService;
    private List<Listener> listenerList;
    private TimeUnit intervalUnit;
    private int interval;

    /**
     * 设置要监听的drpc 服务
     *
     * @param drpcServers
     */
    public DefaultSchedulerBuilder addDrpcServer(DrpcServer... drpcServers) {
        this.drpcServerList.addAll(Arrays.asList(drpcServers));
        return this;
    }

    public DefaultSchedulerBuilder setDrpcServer(DrpcServer... drpcServers) {
        this.drpcServerList = Arrays.asList(drpcServers);
        return this;
    }

    public DefaultSchedulerBuilder setDrpcServer(List<DrpcServer> drpcServerList) {
        this.drpcServerList = drpcServerList;
        return this;
    }

    public DefaultSchedulerBuilder addDrpcServer(List<DrpcServer> drpcServerList) {
        this.drpcServerList.addAll(drpcServerList);
        return this;
    }

    /**
     * 设置执行监听的线程池
     *
     * @param executorService
     */
    public DefaultSchedulerBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    /**
     * 设置监听
     *
     * @param listeners
     */
    public DefaultSchedulerBuilder setListener(Listener... listeners) {
        this.listenerList = Arrays.asList(listeners);
        return this;
    }

    /**
     * 设置心跳单位
     *
     * @param intervalUnit
     */
    public DefaultSchedulerBuilder setIntervalUnit(TimeUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
        return this;
    }

    /**
     * 设置心跳时长
     *
     * @param interval
     */
    public DefaultSchedulerBuilder setInterval(int interval) {
        this.interval = interval;
        return this;
    }

    static class DefaultScheduler implements Scheduler, Listener {
        private static final Logger LOGGER = LoggerFactory.getLogger(DefaultScheduler.class);

        private List<DrpcServer> drpcServerList;
        private ExecutorService executorService;
        private List<Listener> listenerList;
        private TimeUnit intervalUnit;
        private int interval;
        private boolean runing;

        List<HeartbeatClient> clientList;

        public DefaultScheduler(List<DrpcServer> drpcServerList, int interval, TimeUnit intervalUnit, List<Listener> listenerList, ExecutorService executorService) {
            this.drpcServerList = drpcServerList;
            this.interval = interval;
            this.intervalUnit = intervalUnit;
            this.listenerList = listenerList;
            this.executorService = executorService;
        }

        @Override
        public void start() {
            Thread thread = new Thread(() -> {
                clientList = new ArrayList<>();
                synchronized (clientList) {
                    drpcServerList.forEach(server -> {
                        try {
                            HeartbeatClient client = new HeartbeatClient(server);
                            clientList.add(client);
                        } catch (Exception e) {
                            LOGGER.error("hearbeat client create error", e);
                            LOGGER.error("hearbeat client create error {}", server.getAddress());
                            if(e instanceof RuntimeException){
                                throw (RuntimeException)e;
                            }
                            throw new RuntimeException(e);
                        }
                    });
                }
                LOGGER.info("heartbeat go go");
                while (runing) {
                    /**
                     * 循环
                     */
                    heartbeat();
                    try {
                        //休眠
                        intervalUnit.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            );
            thread.setName("DRPC DefaultScheduler");
            thread.setDaemon(true);
            runing = true;
            thread.start();
            LOGGER.info("heartbeat start");
        }


        @Override
        public boolean isRuning() {
            return runing;
        }

        private void heartbeat() {
            synchronized (clientList) {
                for (int i = clientList.size() - 1; i >= 0; i--) {
                    final HeartbeatClient c = clientList.get(i);
                    executorService.submit(() -> {
                        HeartbeatClient client = c;
                        if (!client.isConnected()) {
                            synchronized (clientList) {
                                try {
                                    client = new HeartbeatClient(client.getDrpcServer());
                                    client.close();
                                    clientList.remove(c);
                                    clientList.add(client);
                                } catch (Exception e) {
//                                    LOGGER.error("hearbeat client connected error", e);
                                    errorEvent(client, e);
                                }
                            }
                        }
                        try {
                            client.reconnect();
                            successEvent(client);
                            //一旦drpc server无法连接，长达30秒后抛出异常
                        } catch (Exception e) {
//                            LOGGER.error("hearbeat client connected error", e);
                            errorEvent(client, e);
                        }

                    });
                }
            }


        }

        @Override
        public void errorEvent(HeartbeatClient client, Exception ex) {
            LOGGER.info("heartbeat errorEvent");
            listenerList.forEach(listener -> {
                listener.errorEvent(client, ex);
            });
        }

        @Override
        public void successEvent(HeartbeatClient client) {
            LOGGER.info("heartbeat successEvent");
            listenerList.forEach(listener -> {
                listener.successEvent(client);
            });
        }

        @Override
        public void stop() {
            List<HeartbeatClient> clientList = this.clientList;
            synchronized (clientList) {
                for (int i = 0; i < clientList.size(); i++) {
                    clientList.get(i).close();
                }
            }
            this.clientList = null;
            runing = false;
        }

    }

}
