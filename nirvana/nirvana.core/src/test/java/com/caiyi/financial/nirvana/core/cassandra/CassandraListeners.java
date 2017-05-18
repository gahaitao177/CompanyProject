package com.caiyi.financial.nirvana.core.cassandra;

import com.datastax.driver.core.*;
import org.junit.Test;

/**
 * Cassandra的三种监听对象
 * Created by Mario on 2016/10/9 0009.
 */
public class CassandraListeners {
    @Test
    /**
     * Host.StateListener
     * http://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/Host.StateListener.html
     */
    public void StateListener() {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .withClusterName(CassandraConf.CLUSTER_NAME)
                    .addContactPoints(CassandraConf.DB_ADDRESS)
                    .build()
                    .register(new Host.StateListener() {
                        @Override
                        public void onAdd(Host host) {
                            //有新节点添加到集群时触发
                            System.out.println("onAdd");
                        }

                        @Override
                        public void onUp(Host host) {
                            //有节点决定启动时触发
                            System.out.println("onUp");
                        }

                        @Override
                        public void onDown(Host host) {
                            //有节点决定关闭时触发
                            System.out.println("onDown");
                        }

                        @Override
                        public void onRemove(Host host) {
                            //有节点被删除时触发
                            System.out.println("onRemove");
                        }

                        @Override
                        public void onRegister(Cluster cluster) {
                            //跟踪器被集群注册，或者集群携带跟踪器启动时触发
                            System.out.println("onRegister");
                        }

                        @Override
                        public void onUnregister(Cluster cluster) {
                            //跟踪器被集群注销，或者集群关闭时触发
                            System.out.println("onUnregister");
                        }
                    });
            cluster.connect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }

    @Test
    /**
     * LatencyTracker
     * http://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/LatencyTracker.html
     */
    public void LatencyTracker() {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .withClusterName(CassandraConf.CLUSTER_NAME)
                    .addContactPoints(CassandraConf.DB_ADDRESS)
                    .build()
                    .register(new LatencyTracker() {
                        @Override
                        public void update(Host host, Statement statement, Exception e, long l) {
                            //每个对Cassandra节点的请求发生之后，都会触发，可以获取操作用时
                            System.out.println("update");
                        }

                        @Override
                        public void onRegister(Cluster cluster) {
                            //跟踪器被集群注册，或者集群携带跟踪器启动时触发
                            System.out.println("onRegister");
                        }

                        @Override
                        public void onUnregister(Cluster cluster) {
                            //跟踪器被集群注销，或者集群关闭时触发
                            System.out.println("onUnregister");
                        }
                    });
            cluster.connect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }

    @Test
    /**
     * SchemaChangeListener
     * http://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/SchemaChangeListener.html
     */
    public void SchemaChangeListener() {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .withClusterName(CassandraConf.CLUSTER_NAME)
                    .addContactPoints(CassandraConf.DB_ADDRESS)
                    .build()
                    .register(new SchemaChangeListener() {
                        @Override
                        public void onKeyspaceAdded(KeyspaceMetadata keyspaceMetadata) {
                            //新增keyspace时触发
                            System.out.println("onKeyspaceAdded");
                        }

                        @Override
                        public void onKeyspaceRemoved(KeyspaceMetadata keyspaceMetadata) {
                            //删除keyspace时触发
                            System.out.println("onKeyspaceAdded");
                        }

                        @Override
                        public void onKeyspaceChanged(KeyspaceMetadata keyspaceMetadata, KeyspaceMetadata keyspaceMetadata1) {
                            //当某个keyspace改变时
                            System.out.println("onKeyspaceChanged");
                        }

                        @Override
                        public void onTableAdded(TableMetadata tableMetadata) {
                            //添加表时触发
                            System.out.println("onTableAdded");
                        }

                        @Override
                        public void onTableRemoved(TableMetadata tableMetadata) {
                            //删除表时触发
                            System.out.println("onTableRemoved");
                        }

                        @Override
                        public void onTableChanged(TableMetadata tableMetadata, TableMetadata tableMetadata1) {
                            //修改表时触发
                            System.out.println("onTableChanged");
                        }

                        @Override
                        public void onUserTypeAdded(UserType userType) {
                            //用户自定义类型添加时触发
                            System.out.println("onUserTypeAdded");
                        }

                        @Override
                        public void onUserTypeRemoved(UserType userType) {
                            //用户自定义类型移除时触发
                            System.out.println("onUserTypeAdded");
                        }

                        @Override
                        public void onUserTypeChanged(UserType userType, UserType userType1) {
                            //用户自定义类型改变时触发
                            System.out.println("onUserTypeChanged");
                        }

                        @Override
                        public void onFunctionAdded(FunctionMetadata functionMetadata) {
                            //用户定义函数添加时触发
                            System.out.println("onFunctionAdded");
                        }

                        @Override
                        public void onFunctionRemoved(FunctionMetadata functionMetadata) {
                            //用户定义函数移除时触发
                            System.out.println("onFunctionRemoved");
                        }

                        @Override
                        public void onFunctionChanged(FunctionMetadata functionMetadata, FunctionMetadata functionMetadata1) {
                            //用户定义函数改变时触发
                            System.out.println("onFunctionChanged");
                        }

                        @Override
                        public void onAggregateAdded(AggregateMetadata aggregateMetadata) {
                            //用户定义集合添加时触发
                            System.out.println("onAggregateAdded");
                        }

                        @Override
                        public void onAggregateRemoved(AggregateMetadata aggregateMetadata) {
                            //用户定义集合移除时触发
                            System.out.println("onAggregateRemoved");
                        }

                        @Override
                        public void onAggregateChanged(AggregateMetadata aggregateMetadata, AggregateMetadata aggregateMetadata1) {
                            //用户定义集合改变时触发
                            System.out.println("onAggregateChanged");
                        }

                        @Override
                        public void onMaterializedViewAdded(MaterializedViewMetadata materializedViewMetadata) {
                            //实体化视图添加时触发
                            System.out.println("onMaterializedViewAdded");
                        }

                        @Override
                        public void onMaterializedViewRemoved(MaterializedViewMetadata materializedViewMetadata) {
                            //实体化视图移除时触发
                            System.out.println("onMaterializedViewRemoved");
                        }

                        @Override
                        public void onMaterializedViewChanged(MaterializedViewMetadata materializedViewMetadata, MaterializedViewMetadata materializedViewMetadata1) {
                            //实体化视图改变时触发
                            System.out.println("onMaterializedViewChanged");
                        }

                        @Override
                        public void onRegister(Cluster cluster) {
                            //跟踪器被集群注册，或者集群携带跟踪器启动时触发
                            System.out.println("onRegister");
                        }

                        @Override
                        public void onUnregister(Cluster cluster) {
                            //跟踪器被集群注销，或者集群关闭时触发
                            System.out.println("onUnregister");
                        }
                    });

            cluster.connect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }
}
