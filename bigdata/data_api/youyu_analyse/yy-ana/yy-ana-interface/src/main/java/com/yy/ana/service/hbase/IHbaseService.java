package com.yy.ana.service.hbase;

/**
 * Created by User on 2017/5/27.
 */

import com.yy.ana.bean.KeyValue;

import java.util.List;
import java.util.Map;

/**
 * Created by sunqm on 2017/5/23.
 * <p>
 * 封装操作hbase的方法
 * </p>
 */
public interface IHbaseService {
    /**
     * @param rowKey
     * @param tableName
     * @param columnFamily
     * @param columns
     * @param values
     * @return
     * @throws Exception
     */
    boolean addData(String rowKey, String tableName, String columnFamily, String[] columns, String[] values) throws Exception;

    /**
     * @param tablename
     * @param rowkeys
     * @param familys
     * @return
     * @throws Exception
     */
    List<List<KeyValue>> getManyByRowKey(String tablename, List<String> rowkeys, String familys) throws Exception;

    /**
     * @param tablename
     * @param rowkey
     * @param familys
     * @return
     * @throws Exception
     */
    List<KeyValue> getByRowkeyFamily(String tablename, String rowkey, String familys) throws Exception;

    /**
     * @param tablename
     * @param rowkey
     * @param columns
     * @return
     * @throws Exception
     */
    List<KeyValue> getByRowkeyColumn(String tablename, String rowkey, String columns) throws Exception;

    /**
     * @param tableName
     * @param start
     * @param stop
     * @param filters
     * @param familys
     * @param columns
     * @return
     * @throws Exception
     */
    Map<String, List<KeyValue>> scanByFilter(String tableName, String start, String stop, String filters, String familys, String columns) throws Exception;

}

