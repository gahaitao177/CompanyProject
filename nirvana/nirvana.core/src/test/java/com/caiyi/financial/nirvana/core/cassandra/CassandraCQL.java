package com.caiyi.financial.nirvana.core.cassandra;

import com.datastax.driver.core.*;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mario on 2016/10/9 0009.
 * 基本CQL命令执行
 */
public class CassandraCQL {

    @Test
    /**
     * execute 可以执行任意CQL
     */
    public void quickStart() {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .withClusterName(CassandraConf.CLUSTER_NAME)
                    .addContactPoints(CassandraConf.DB_ADDRESS)
                    .build();
            Session session = cluster.connect();
            ResultSet rs = session.execute("select release_version from system.local");
            Row row = rs.one();
            System.out.println(row.getString("release_version"));
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
     * 实现一个Initializer,用来初始化Cluster
     */
    public void initializerCluster() {
        Cluster.Initializer initializer = new Cluster.Initializer() {
            @Override
            public String getClusterName() {
                return CassandraConf.CLUSTER_NAME;
            }

            @Override
            public List<InetSocketAddress> getContactPoints() {
                return new ArrayList<InetSocketAddress>() {{
                    addAll(CassandraConf.DB_ADDRESS.stream().map(item -> new InetSocketAddress(item, 9042)).collect(Collectors.toList()));
                }};
            }

            @Override
            public Configuration getConfiguration() {
                return Configuration.builder().build();
            }

            @Override
            public Collection<Host.StateListener> getInitialListeners() {
                return new ArrayList() {{
                    add(new Host.StateListener() {
                        @Override
                        public void onAdd(Host host) {
                            System.out.println("onAdd");
                        }

                        @Override
                        public void onUp(Host host) {
                            System.out.println("onUp");
                        }

                        @Override
                        public void onDown(Host host) {
                            System.out.println("onDown");
                        }

                        @Override
                        public void onRemove(Host host) {
                            System.out.println("onRemove");
                        }

                        @Override
                        public void onRegister(Cluster cluster) {
                            System.out.println("onRegister");
                        }

                        @Override
                        public void onUnregister(Cluster cluster) {
                            System.out.println("onUnregister");
                        }
                    });
                }};
            }
        };
        Cluster cluster = Cluster.buildFrom(initializer);
        Session session = cluster.connect();
        ResultSet rs = session.execute("select release_version from system.local");
        Row row = rs.one();
        System.out.println(row.getString("release_version"));
        cluster.close();
    }

}
