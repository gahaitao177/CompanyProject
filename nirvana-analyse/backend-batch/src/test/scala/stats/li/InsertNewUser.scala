package stats.li

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * Created by li on 2017/2/22.
  */
object InsertNewUser {

  def main(args: Array[String]): Unit = {

    //配置连接信息读取数据
    val conf = new SparkConf(true).set("spark.cassandra.connection.host", "192.168.83.26")

    val session = SparkSession.builder().config(conf)
      .appName("AppHourStats")
      .master("local")
      .getOrCreate()

    //读取今天要计算的app_profile数据
    val nowData = session
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "app_profile"))
      .load()
    nowData.createOrReplaceTempView("now_data")

    //读取新用户表数据
    val allNewUser = session
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "app_new_user"))
      .load()
    allNewUser.createOrReplaceTempView("all_new_user")

    //判断app_profile表中用户是不是新用户
    val newUser = session
      .sql("select a.app_channel,a.app_gps," +
        "          a.app_ip,a.app_key," +
        "          a.app_name,a.app_network," +
        "          a.app_source,a.app_version," +
        "          a.city,a.city_code," +
        "          a.ctime,a.device_brand," +
        "          a.device_id,a.device_model," +
        "          a.device_os,a.device_res," +
        "          a.device_type,a.province," +
        "          a.user_id,a.user_name " +
        "from (" +
        "select app_channel,app_gps," +
        "       app_ip,app_key," +
        "       app_name,app_network," +
        "       app_source,app_version," +
        "       city,city_code," +
        "       ctime,device_brand," +
        "       device_id,device_model," +
        "       device_os,device_res," +
        "       device_type, province," +
        "       user_id,user_name," +
        "       row_number() over(partition by device_id order by ctime) rn " +
        "from now_data) a " +
        "left join " +
        "all_new_user b on a.device_id=b.device_id " +
        "where a.rn=1 " +
        "  and b.device_id is null")

    //将新用户插入表中
    newUser.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "app_new_user"))
      .mode(SaveMode.Append)
      .save()

    session.stop()

  }

}
