package com.caiyi.financial.nirvana.core.cassandra.io;

import com.caiyi.financial.nirvana.core.cassandra.client.CassandraClient;
import com.caiyi.financial.nirvana.core.cassandra.io.base.FileIO;
import com.caiyi.financial.nirvana.core.cassandra.io.mapper.Image;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Mario on 2016/10/20 0020.
 * --图片文件集中存储表:
 * create table IF NOT EXISTS TB_IMAGE_FILES(
 * id text primary key,
 * content blob,
 * filename text,
 * extension text,
 * add_date timestamp,
 * mod_date timestamp,
 * last_read_date timestamp
 * );
 */
@SuppressWarnings("unused")
public class ImageIO extends FileIO {

    public ImageIO(CassandraClient cassandraClient) throws Exception {
        super(cassandraClient);
    }

    /**
     * 初始化image表
     *
     * @return 是否执行成功
     */
    @Override
    public boolean setUp() {
        ResultSet rs = session.execute(
                "CREATE TABLE IF NOT EXISTS TB_IMAGE_FILES(\n" +
                        "id text primary key,\n" +
                        "content blob,\n" +
                        "filename text,\n" +
                        "extension text,\n" +
                        "add_date timestamp,\n" +
                        "mod_date timestamp,\n" +
                        "last_read_date timestamp\n" +
                        ");");
        return rs.wasApplied();
    }

    @Override
    public String getTableName() {
        return "TB_IMAGE_FILES";
    }

    /**
     * 新增图片
     *
     * @param totalName 文件全名
     * @param data      文件字节数组
     * @return 图片访问id
     */
    public String add(String totalName, byte[] data) {
        String filename = totalName.contains(".") ? totalName.split("\\.")[0] : totalName;
        String extension = totalName.contains(".") ? totalName.split("\\.")[1] : "";
        String id = UUID.randomUUID().toString() + filename;

        Image img = new Image();
        img.setFilename(filename);
        img.setExtension(extension);
        img.setAdd_date(new Date());
        img.setId(id);
        img.setContent(ByteBuffer.wrap(data));

        Session session = cassandraClient.connect(FileIO.FILES_KEYSPACE);
        MappingManager manager = new MappingManager(session);
        Mapper<Image> mapper = manager.mapper(Image.class);

        mapper.save(img);
        return img.getId();
    }

    /**
     * 读取图片
     *
     * @param id id
     * @return image
     */
    public Image read(String id) {
        Session session = cassandraClient.connect(FileIO.FILES_KEYSPACE);
        MappingManager manager = new MappingManager(session);
        Mapper<Image> mapper = manager.mapper(Image.class);
        Image temp = mapper.get(id);
        temp.setLast_read_date(new Date());
        mapper.save(temp);
        return temp;
    }

    /**
     * 更新图片
     *
     * @param id        id
     * @param totalName 新文件名
     * @param data      文件字节数组
     * @return id
     */
    public String update(String id, String totalName, byte[] data) {
        String filename = totalName.contains(".") ? totalName.split("\\.")[0] : totalName;
        String extension = totalName.contains(".") ? totalName.split("\\.")[1] : "";

        Image img = new Image();
        img.setFilename(filename);
        img.setExtension(extension);
        img.setMod_date(new Date());
        img.setId(id);
        img.setContent(ByteBuffer.wrap(data));

        Session session = cassandraClient.connect(FileIO.FILES_KEYSPACE);
        MappingManager manager = new MappingManager(session);
        Mapper<Image> mapper = manager.mapper(Image.class);

        mapper.save(img);
        return img.getId();
    }

    /**
     * 删除图片
     *
     * @param id id
     */
    public void delete(String id) {
        Session session = cassandraClient.connect(FileIO.FILES_KEYSPACE);
        MappingManager manager = new MappingManager(session);
        Mapper<Image> mapper = manager.mapper(Image.class);
        mapper.delete(id);
    }
}
