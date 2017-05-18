package com.caiyi.financial.nirvana.core.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.Bytes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

/**
 * Created by Mario on 2016/10/10 0010.
 */
public class CassandraFile {
    /**
     * 文件存取
     */
    @Test
    public void fileOperation() {
        /**
         * 测试表：
         * CREATE TABLE test_file (
         *  file_id text PRIMARY KEY,
         *  content BLOB
         * );
         */

        this.addFile("nirvana.core.pom", "pom.xml");
        this.getFile("nirvana.core.pom");
    }


    /**
     * 保存文件
     *
     * @param fileId
     * @param filePath
     */
    public void addFile(String fileId, String filePath) {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .withClusterName(CassandraConf.CLUSTER_NAME)
                    .addContactPoints(CassandraConf.DB_ADDRESS)
                    .build();
            //连接到keyspace mario
            Session session = cluster.connect("mario");

            byte[] data = IOUtils.toByteArray(new FileInputStream(new File(filePath)));
            ByteBuffer buffer = ByteBuffer.wrap(data);

            PreparedStatement ps = session.prepare("insert into test_file(file_id, content) values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            ResultSet rs = session.execute(boundStatement.bind(fileId, buffer));
            System.out.println("是否保存成功:" + rs.wasApplied());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }

    /**
     * 读取文件
     *
     * @param fileId
     * @return
     */
    public void getFile(String fileId) {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .withClusterName(CassandraConf.CLUSTER_NAME)
                    .addContactPoints(CassandraConf.DB_ADDRESS)
                    .build();
            //连接到keyspace mario
            Session session = cluster.connect("mario");

            PreparedStatement ps = session.prepare("select * from test_file where file_id = ?");
            BoundStatement boundStatement = new BoundStatement(ps);
            ResultSet rs = session.execute(boundStatement.bind(fileId));
            Row row = rs.one();
            byte[] data = Bytes.getArray(row.getBytes("content"));
            File f = new File("src/test/resources/cassandra/" + row.getString("file_id") + ".xml");
            if(f.exists()){
                f.delete();
            }
            f.createNewFile();
            FileUtils.writeByteArrayToFile(f, data);
            System.out.println("文件已保存:" + f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }
}
