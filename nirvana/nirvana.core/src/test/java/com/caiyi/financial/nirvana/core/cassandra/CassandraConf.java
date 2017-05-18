package com.caiyi.financial.nirvana.core.cassandra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mario on 2016/10/10 0010.
 */
public class CassandraConf {
    public final static List<InetAddress> DB_ADDRESS = new ArrayList<InetAddress>(){{
        try {
            add(InetAddress.getByName("192.168.1.89"));
            add(InetAddress.getByName("192.168.1.88"));
            add(InetAddress.getByName("192.168.1.51"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }};
    public static final String CLUSTER_NAME = "hsk_cluster";
}
