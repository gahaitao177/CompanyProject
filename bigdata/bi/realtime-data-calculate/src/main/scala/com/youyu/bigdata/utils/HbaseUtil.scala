package com.youyu.bigdata.utils

import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{CellUtil, HBaseConfiguration, HConstants, TableName}

import scala.collection.mutable

/**
  * Created by xiaxc on 2017/5/11.
  */
object HbaseUtil extends Serializable {
  private val conf = HBaseConfiguration.create()
  private val port = "2181"
  private val quorum = "gs-yy-slave1,gs-yy-slave2,gs-yy-slave3"
  conf.set(HConstants.ZOOKEEPER_CLIENT_PORT, port)
  conf.set(HConstants.ZOOKEEPER_QUORUM, quorum) // hosts
  private val connection = ConnectionFactory.createConnection(conf)

  def getHbaseConn: Connection = connection

  /**
    * 获取表中所有的rowkey
    *
    * @param tableName
    * @return
    */
  def getAllRowKey(tableName: String): mutable.Map[String, Integer] = {

    val table = connection.getTable(TableName.valueOf(tableName))
    val scan = new Scan
    import scala.collection.mutable.Map
    val map = Map[String, Integer]()
    val results = table.getScanner(scan)

    import scala.collection.JavaConversions._
    for (result <- results) {
      for (cell <- result.rawCells) {
        val key = new String(CellUtil.cloneRow(cell))
        map.put(key, 1)
      }
    }

    map
  }

  /**
    * 插入数据前先判断当前插入的rowkey在表中是否存在
    *
    * @param table
    * @param rowKey
    * @return
    */
  def isNotExistKey(table: Table, rowKey: String): Boolean = {
    var flag: Boolean = false
    try {
      val get: Get = new Get(rowKey.getBytes)
      val r: Result = table.get(get)

      flag = r.isEmpty
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }

    flag
  }

  /**
    * 插入一条数据
    *
    * @param table
    * @param rowKey
    * @param columnFamily
    * @param columns
    * @param values
    */
  def addRow(table: Table, rowKey: String, columnFamily: String, columns: Array[String], values: Array[String]) = {
    try {
      val put: Put = new Put(rowKey.getBytes)
      for (i <- 0 to (columns.length - 1)) {
        put.add(columnFamily.getBytes, columns(i).getBytes, values(i).getBytes)
      }
      table.put(put)

    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  /**
    * 计数器(amount为正数则计数器加，为负数则计数器减，为0则获取当前计数器的值)
    *
    * @param table
    * @param rowKey
    * @param columnFamily
    * @param column
    * @param amount
    * @return
    */
  def incrementColumnValues(table: Table, rowKey: String, columnFamily: String, column: String, amount: Long): Result = {
    val increment = new Increment(Bytes.toBytes(rowKey))
    var result: Result = new Result()
    try {
      increment.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(column), amount)
      result = table.increment(increment)
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }

    result
  }
}