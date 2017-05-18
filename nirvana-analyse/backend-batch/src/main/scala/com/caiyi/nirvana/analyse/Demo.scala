package com.caiyi.nirvana.analyse.count.month

import java.text.SimpleDateFormat
import java.util.Date

import com.caiyi.nirvana.analyse.DateUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * Created by root on 2017/1/19.
  */
object Demo {
  //配置时间工具
  val date = new Date()
  val dateUtils = new DateUtils()
  val getTime = dateUtils.splitDate(dateUtils.getLastHour(date, -2))
  val getLastTime = dateUtils.splitDate(dateUtils.getLastHour(date, -3))
  val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  //cassandra地址
  val cassandraHost = "192.168.83.26"

  //连接信息
  val nirvana = "nirvana"
  val appProfile = "app_profile"
  val newUserTable = "app_new_user"

  def main(args: Array[String]): Unit = {

    //配置连接信息读取数据
    val conf = new SparkConf(true).set("spark.cassandra.connection.host", cassandraHost)

    val session = SparkSession.builder().config(conf)
      .appName("AppDayStatisticsAnalyse")
      .master("local")
      .getOrCreate()

    //读取今天要计算的app_profile数据
    val nowData = session
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace"->nirvana, "table"->appProfile))
      .load()
      .where("to_date(ctime) = '2017-02-20' and app_name='车险计算器'")
    nowData.createOrReplaceTempView("now_data")

    //读取新用户表数据
    val allNewUser = session
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace"->"nirvana", "table"->"app_new_user"))
      .load()
    allNewUser.createOrReplaceTempView("all_new_user")

    //读取新用户表前一天数据
    val lastNewUser = session
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace"->nirvana, "table"->newUserTable))
      .load()
      .where("to_date(ctime) = '" + getLastTime("year") + "-" + getLastTime("month") + "-" + getLastTime("day")
        + "' and lpad(hour(ctime),2,0) = " + getLastTime("hour"))
    lastNewUser.createOrReplaceTempView("last_new_user")

    session.sql("select count(distinct(device_id)) from now_data").show()

    session.stop()
  }


}
