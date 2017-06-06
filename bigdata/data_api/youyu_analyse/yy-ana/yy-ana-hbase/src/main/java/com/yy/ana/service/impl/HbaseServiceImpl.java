package com.yy.ana.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yy.ana.bean.KeyValue;
import com.yy.ana.service.hbase.IHbaseService;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2017/5/27.
 */
@Service
public class HbaseServiceImpl implements IHbaseService {
    private static Configuration conf;
    private static HConnection hConnection;

    static {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "gs-yy-slave1,gs-yy-slave2,gs-yy-slave3");
        conf.set("hbase.master", "gs-yy-master:60000,gs-yy-slave1:60000");
        try {
            hConnection = HConnectionManager.createConnection(conf);
        } catch (ZooKeeperConnectionException e) {
            System.out.println("a");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addData(String rowKey, String tableName, String columnFamily, String[] columns, String[] values) throws Exception {
        HTableInterface table = null;
        try {
            table = hConnection.getTable(tableName);
            Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey

            for (int j = 0; j < columns.length; j++) {
                put.add(Bytes.toBytes(columnFamily), Bytes.toBytes(columns[j]),
                        Bytes.toBytes(values[j]));
            }
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public List<List<KeyValue>> getManyByRowKey(String tablename, List<String> rowkeys, String familys) throws Exception {
        HTableInterface table = null;
        try {
            table = hConnection.getTable(tablename);
            List<Get> gets = new ArrayList<Get>();
            for (String rowkey : rowkeys) {
                Get get = new Get(Bytes.toBytes(rowkey));

                if (!StringUtils.isBlank(familys)) {
                    String[] f = familys.split(",");
                    for (String f1 : f) {
                        get.addFamily(Bytes.toBytes(f1));
                    }
                }
                gets.add(get);
            }

            Result[] results = table.get(gets);

            List<List<KeyValue>> finalResult = new ArrayList<List<KeyValue>>();
            for (Result re : results) {
                List<KeyValue> r = new ArrayList<KeyValue>();
                for (org.apache.hadoop.hbase.KeyValue kv : re.raw()) {
                    r.add(new KeyValue(kv.getFamily(), kv.getQualifier(), kv.getValue()));
                }
                finalResult.add(r);
            }
            return finalResult;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<KeyValue> getByRowkeyFamily(String tablename, String rowkey, String familys) throws Exception {
        HTableInterface table = null;
        try {
            table = hConnection.getTable(tablename);
            Get get = new Get(Bytes.toBytes(rowkey));

            if (!StringUtils.isBlank(familys)) {
                String[] f = familys.split(",");
                for (String f1 : f) {
                    get.addFamily(Bytes.toBytes(f1));
                }
            }

            Result result = table.get(get);
            List<KeyValue> r = new ArrayList<KeyValue>();
            for (org.apache.hadoop.hbase.KeyValue kv : result.raw()) {
                r.add(new KeyValue(kv.getFamily(), kv.getQualifier(), kv.getValue(), kv.getTimestamp()));
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<KeyValue> getByRowkeyColumn(String tablename, String rowkey, String columns) throws Exception {
        HTableInterface table = null;
        try {
            table = hConnection.getTable(tablename);

            Get get = new Get(Bytes.toBytes(rowkey));

            if (!StringUtils.isBlank(columns)) {
                String[] c = columns.split(",");
                for (String c1 : c) {
                    String[] cs = c1.split(":");
                    get.addColumn(Bytes.toBytes(cs[0]), Bytes.toBytes(cs[1]));
                }
            }

            Result result = table.get(get);

            List<KeyValue> r = new ArrayList<KeyValue>();
            for (org.apache.hadoop.hbase.KeyValue kv : result.raw()) {
                r.add(new KeyValue(kv.getFamily(), kv.getQualifier(), kv.getValue()));
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String, List<KeyValue>> scanByFilter(String tableName, String start, String stop, String filters, String familys, String columns) throws Exception {
        Scan scan = new Scan();
        HTable table;
        try {
            table = new HTable(conf, tableName);

            scan.setCaching(500);
            scan.setCacheBlocks(false);
            //scan.setBatch(100);

            //设置开始key
            if (!StringUtils.isBlank(start)) {
                scan.setStartRow(Bytes.toBytes(start));
            }

            //设置结束key
            if (!StringUtils.isBlank(stop)) {
                scan.setStopRow(Bytes.toBytes(stop));
            }

            //设置过滤条件
            if (!StringUtils.isBlank(filters)) {
                String[] fs = filters.split(",");
                FilterList filterList = new FilterList();
                for (String v : fs) { // 各个条件之间是�?�与”的关系
                    String[] s = v.split(":");
                    filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes(s[0]),
                                    Bytes.toBytes(s[1]),
                                    CompareFilter.CompareOp.EQUAL, Bytes.toBytes(s[2])
                            )
                    );
                }
            }

            //设置要显示的family
            if (!StringUtils.isBlank(familys)) {
                String[] f = familys.split(",");
                for (String f1 : f) {
                    scan.addFamily(Bytes.toBytes(f1));
                }
            }

            //设置要显示的column
            if (!StringUtils.isBlank(columns)) {
                String[] c = columns.split(",");
                for (String c1 : c) {
                    String[] cs = c1.split(":");
                    scan.addColumn(Bytes.toBytes(cs[0]), Bytes.toBytes(cs[1]));
                }
            }

            ResultScanner rs = table.getScanner(scan);
            Map<String, List<KeyValue>> result = new HashMap<String, List<KeyValue>>();
            for (Result r : rs) {
                org.apache.hadoop.hbase.KeyValue[] kv = r.raw();
                if (kv.length < 1) {
                    continue;
                }
                List<KeyValue> rl = new ArrayList<KeyValue>();
                for (org.apache.hadoop.hbase.KeyValue k : kv) {
                    rl.add(new KeyValue(k.getFamily(), k.getQualifier(), k.getValue()));
                }
                result.put(Bytes.toString(kv[0].getRow()), rl);
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
