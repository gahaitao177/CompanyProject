package com.caiyi.financial.nirvana.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaohaitao on 2016/11/28.
 */
public class Constants {

    //Cassandra
    public final static List<String> CASSANDRA_DB_ADDRESS = new ArrayList<String>() {{
        add("192.168.1.71");
        add("192.168.1.80");
        add("192.168.1.88");
    }};

    /*public static List<InetAddress> DB_ADDRESS = new ArrayList<InetAddress>(){{
        try {
            add(InetAddress.getByName("192.168.1.89"));
            add(InetAddress.getByName("192.168.1.88"));
            add(InetAddress.getByName("192.168.1.51"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }};*/

    /*public final static List<String> CASSANDRA_DB_ADDRESS = new ArrayList<String>() {{
        add("localhost");
    }};*/

    public final static String CASSANDRA_KEYSPACE_NAME = "cassandra.keyspace.name";

    public final static String CASSANDRA_CLUSTER_PORT = "cassandra.cluster.port";

    public final static String CASSANDRA_LOCAL_CORE_CONNECT_NUM = "cassandra.local.core.connect.num";

    public final static String CASSANDRA_LOCAL_MAX_CONNECT_NUM = "cassandra.local.max.connect.num";

    public final static String CASSANDRA_REMOTE_CORE_CONNECT_NUM = "cassandra.remote.core.connect.num";

    public final static String CASSANDRA_REMOTE_MAX_CONNECT_NUM = "cassandra.remote.max.connect.num";

    public final static String CASSANDRA_HEARTBEAT_INTERVAL_SECONDS = "cassandra.heartbeat.interval.seconds";

    public final static String SPARK_APP_NAME = "CaiyiDailyAnalysis";

    public final static String ZOOKEEPER_LIST = "zookeeper.list";

    //Kafka
    public final static String KAFKA_TOPIC_NAME = "kafka.topic.name";

    public final static String KAFKA_GROUP_ID = "sessionGroupId";

    public final static String KAFKA_NUM_THREADS = "kafka.num.theads";

    public final static String KAFKA_LIST = "kafka.list";

    //bill账单
    public final static String BANK_REASON_TYPE = "bank.reason.type";

    public final static String USER_REASON_TYPE = "user.reason.type";

    public final static String BILL_REASON_TYPE = "bill.reason.type";

    public final static String BILL_RESULT_SUCCESS_FLAG = "bill.result.success.flag";

    public final static String BILL_RESULT_FAIL_FLAG = "bill.result.fail.flag";

    public final static String BILL_SUCCESS_TYPE_THREE = "bill.success.type.three";

    public final static String BILL_SUCCESS_TYPE_TWO = "bill.success.type.two";

    public final static String BILL_SUCCESS_VALUE_THREE = "bill.success.value.three";

    public final static String BILL_SUCCESS_VALUE_TWO = "bill.success.value.two";
}
