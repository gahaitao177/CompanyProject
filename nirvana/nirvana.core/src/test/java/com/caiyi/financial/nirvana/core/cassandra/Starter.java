package com.caiyi.financial.nirvana.core.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.Bytes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by been on 16/9/28.
 */

//create keyspace account_book with replication = {'class':'SimpleStrategy', 'replication_factor':3};
//use account_book;

//CREATE TABLE account_book_sync_file (
//    file_id uuid,
//    content blob,
//    PRIMARY KEY (file_id)
//) WITH compaction = { 'class' : 'LeveledCompactionStrategy' };

public class Starter {
    @Test
    public void test() throws Exception {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .withClusterName("account_book_cluster")
                    .addContactPoint("192.168.1.88")
                    .build();
            Session session = cluster.connect();
            ResultSet rs = session.execute("select release_version from system.local");
            Row row = rs.one();
            System.out.println(row.getString("release_version"));


            session = cluster.connect("account_book");

            String json = readContentFromZip("syncs/demo.zip");
            System.out.println(json);

            byte[] data = IOUtils.toByteArray(new FileInputStream(new File("syncs/demo.zip")));


            ByteBuffer buffer = ByteBuffer.wrap(data);
            UUID uuid = UUID.randomUUID();

            PreparedStatement ps = session.prepare("insert into  account_book_sync_file ( file_id, content ) values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(uuid, buffer));

            ps = session.prepare("select * from account_book_sync_file where file_id =?");
            boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(uuid));

            ResultSet rc = session.execute("select * from account_book_sync_file");
//            data = rc.one().getBytes("content").array();
            data = Bytes.getArray(rc.one().getBytes("content"));
            File dest = new File("syncs/demo2.zip");
            if (!dest.exists()){
                dest.createNewFile();
            }
            FileUtils.writeByteArrayToFile(dest, data);

            json = readContentFromZip("syncs/demo2.zip");
            System.out.println(json);

        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }

    public String readContentFromZip(String path) {
        String syncString = "";
        ZipFile zf = null;
        ZipInputStream zin = null;
        try {
            zf = new ZipFile(path);
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            zin = new ZipInputStream(in);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                } else {
                    byte[] bt = IOUtils.toByteArray(zin);
                    String jsonString = new String(bt, "utf-8");
                    syncString = jsonString;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(zin);
            IOUtils.closeQuietly(zf);
        }
        return syncString;

    }

    @Test
    public void testFile() {
        System.out.println(new File("yzm/hello.txt").getAbsolutePath().toString());

    }

}
