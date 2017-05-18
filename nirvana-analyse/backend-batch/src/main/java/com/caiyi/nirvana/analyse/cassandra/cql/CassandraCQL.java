package com.caiyi.nirvana.analyse.cassandra.cql;


/**
 * Created by Socean on 2016/11/29.
 */
public class CassandraCQL extends CassandraService {/*

    Session session = null;

    PreparedStatement preparedStatement = null;

    BoundStatement bindStatement = null;

    String keySpaceName = ConfigurationManager.getProperty(Constants.CASSANDRA_KEYSPACE_NAME);
    Integer heartbeatIntervalSeconds = ConfigurationManager.getInteger(Constants.CASSANDRA_HEARTBEAT_INTERVAL_SECONDS);
    Integer localCoreConnectNum = ConfigurationManager.getInteger(Constants.CASSANDRA_LOCAL_CORE_CONNECT_NUM);
    Integer localMaxConnectNum = ConfigurationManager.getInteger(Constants.CASSANDRA_LOCAL_MAX_CONNECT_NUM);
    Integer remoteCoreConnectNum = ConfigurationManager.getInteger(Constants.CASSANDRA_REMOTE_CORE_CONNECT_NUM);
    Integer remoteMaxConnectNum = ConfigurationManager.getInteger(Constants.CASSANDRA_REMOTE_MAX_CONNECT_NUM);
    Integer port = ConfigurationManager.getInteger(Constants.CASSANDRA_CLUSTER_PORT);

    //执行insert update delete select
    public void insertBankCQL(String cql) {

        CassandraPoolOptions conf = new CassandraPoolOptions();
        conf.setHeartbeatIntervalSeconds(heartbeatIntervalSeconds);
        conf.setLocalCoreConnectNum(localCoreConnectNum);
        conf.setLocalMaxConnectNum(localMaxConnectNum);
        conf.setRemoteCoreConnectNum(remoteCoreConnectNum);
        conf.setRemoteMaxConnectNum(remoteMaxConnectNum);
        conf.setPort(port);
        conf.setContactPoints(Constants.CASSANDRA_DB_ADDRESS);

        CassandraClient client = new CassandraClient(conf);

        try {
            //连接到KeySpace
            session = client.connect(keySpaceName);

            ResultSet rs = session.execute(cql);

            //Row row = rs.one();
            logger.info("是否保存成功:" + rs.wasApplied());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    //带有参数的修改 查询 ...select * from mykeyspace.tablename where a=? and b=?"
    //目前还没使用到的方法：modifyBankCQL
    public void modifyBankCQL(String cql, String param1, Integer param2) {

        try {

            preparedStatement = session.prepare(cql);

            bindStatement = new BoundStatement(preparedStatement).bind(param1, param2);

            ResultSet rs = session.execute(bindStatement);

            System.out.println();

            if (rs.wasApplied()) {
                logger.info(">>>>>>>>>>数据修改成功！<<<<<<<<<<<");
            } else {
                logger.info(">>>>>>>>>>数据修改失败！<<<<<<<<<<<");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }*/

}
