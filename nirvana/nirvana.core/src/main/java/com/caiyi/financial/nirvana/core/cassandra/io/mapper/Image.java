package com.caiyi.financial.nirvana.core.cassandra.io.mapper;

import com.caiyi.financial.nirvana.core.cassandra.io.base.FileIO;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by Mario on 2016/10/19 0019.
 * 图片entity
 */
@SuppressWarnings("unused")
@Table(keyspace = FileIO.FILES_KEYSPACE, name="tb_image_files")
public class Image {
    @PartitionKey
    private String id;
    private ByteBuffer content;
    private String filename;
    private String extension;
    private Date add_date;
    private Date mod_date;
    private Date last_read_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ByteBuffer getContent() {
        return content;
    }

    public void setContent(ByteBuffer content) {
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getAdd_date() {
        return add_date;
    }

    public void setAdd_date(Date add_date) {
        this.add_date = add_date;
    }

    public Date getMod_date() {
        return mod_date;
    }

    public void setMod_date(Date mod_date) {
        this.mod_date = mod_date;
    }

    public Date getLast_read_date() {
        return last_read_date;
    }

    public void setLast_read_date(Date last_read_date) {
        this.last_read_date = last_read_date;
    }
}
