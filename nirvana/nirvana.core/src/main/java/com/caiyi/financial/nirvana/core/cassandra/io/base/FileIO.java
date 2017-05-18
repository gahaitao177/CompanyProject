package com.caiyi.financial.nirvana.core.cassandra.io.base;

import com.caiyi.financial.nirvana.core.cassandra.client.CassandraClient;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

/**
 * Created by Mario on 2016/10/14 0014.
 * CassandraFile基本接口
 */
@SuppressWarnings("unused")
public abstract class FileIO {

    /**
     * keySpace
     */
    public static final String FILES_KEYSPACE = "BINARY_FILES";

    public FileIO(CassandraClient cassandraClient) throws Exception {
        this.cassandraClient = cassandraClient;
        this.session = cassandraClient.connect();
        ResultSet resultSet = this.session.execute("CREATE KEYSPACE IF NOT EXISTS "
                + FileIO.FILES_KEYSPACE +
                " WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3}");
        if (!resultSet.wasApplied()) {
            throw new Exception("KEYSPACE：" + FileIO.FILES_KEYSPACE + " 建立失败");
        }
        this.session.close();
        this.session = this.cassandraClient.connect(FileIO.FILES_KEYSPACE);
        if (!this.setUp()) {
            throw new Exception("表：" + this.getTableName() + " 初始化失败");
        }
    }

    /**
     * session
     */
    protected Session session;


    /**
     * client
     */
    protected CassandraClient cassandraClient;

    public CassandraClient getCassandraClient() {
        return cassandraClient;
    }

    public void setCassandraClient(CassandraClient cassandraClient) {
        this.cassandraClient = cassandraClient;
    }

    /**
     * 初始化方法，在FILES_KEYSPACE中建表
     */
    public abstract boolean setUp();

    /**
     * 表名称
     */
    public abstract String getTableName();
}
