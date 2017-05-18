package com.caiyi.nirvana.analyse.count.month

import java.text.SimpleDateFormat
import java.util.Date

import com.caiyi.nirvana.analyse.DateUtils
import com.caiyi.nirvana.analyse.count.canstant.Constants
import com.caiyi.nirvana.analyse.count.conf.ConfigurationManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * Created by root on 2017/1/19.
 */
object AppMonthStats {
  //配置时间工具
  val date = new Date()
  val dateUtils = new DateUtils()
  val getTime = dateUtils.splitDate(dateUtils.getLastMonth(date, -1))
  val getLastTime = dateUtils.splitDate(dateUtils.getLastMonth(date, -2))
  val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  //时间查询条件
  val queryTime = "year(ctime)=" + getTime("year") + " and lpad(month(ctime),2,0) =" + getTime("month")
  val queryLastTime = "year(ctime)=" + getLastTime("year") + " and lpad(month(ctime),2,0) =" + getLastTime("month")

  val yearCode = getTime("year")
  val statisticTimestamp = getTime("year").toString + getTime("month")

  //cassandra地址
  val cassandraHost = ConfigurationManager.getProperty(Constants.CASSANDRA_CANTRACT_POINTS)

  //连接信息
  val nirvana = ConfigurationManager.getProperty(Constants.CASSANDRA_KEYSPACE_NAME)
  val appProfile = "app_profile"
  val newUserTable = "app_new_user"
  val sparkCassandra = "org.apache.spark.sql.cassandra"

  //落地表名称
  val userTable = "tb_app_users_stats_month"
  val networkTable = "tb_app_network_stats_month"
  val timeTable = "tb_app_time_stats_month"

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
      .format(sparkCassandra)
      .options(Map("keyspace"->nirvana, "table"->appProfile))
      .load()
      .where(queryTime)
    nowData.createOrReplaceTempView("now_data")

    //读取新用户表数据
    val allNewUser = session
      .read
      .format(sparkCassandra)
      .options(Map("keyspace"->nirvana, "table"->newUserTable))
      .load()
      .where(queryTime)
    allNewUser.createOrReplaceTempView("now_new_user")

    //读取新用户表前一天数据
    val lastNewUser = session
      .read
      .format(sparkCassandra)
      .options(Map("keyspace"->nirvana, "table"->newUserTable))
      .load()
      .where(queryLastTime)
    lastNewUser.createOrReplaceTempView("last_new_user")

    //计算新用户指标
    val newUser = session
      .sql("select app_source,app_name," +
        "          app_key,app_version," +
        "          device_type,device_os," +
        "          coalesce(device_model,'null') device_model," +
        "          coalesce(city,'null') city," +
        "          count(device_id) new_user " +
        "from now_new_user " +
        "group by app_source,app_name,app_key,app_version," +
        "         device_type,device_model,device_os,city")
    newUser.createOrReplaceTempView("new_user")

    //计算活跃用户指标
    val activeUser = session
      .sql("select app_source,app_name," +
        "          app_key,app_version," +
        "          device_type,device_os," +
        "          coalesce(device_model,'null') device_model," +
        "          coalesce(city,'null') city," +
        "          sum(case when (user_id is not null and user_id != '') " +
        "                     or (user_name is not null and user_name !='') then 1 else 0 end) login_user," +
        "          sum(case when (user_id is null or user_id ='') " +
        "                    and (user_name is null or user_name ='') then 1 else 0 end) no_login_user " +
        "from (" +
        "select app_source,app_name," +
        "       app_key,app_version," +
        "       device_type,device_model," +
        "       device_os,city," +
        "       user_id,user_name," +
        "       row_number() over(partition by device_id order by ctime) rn " +
        "from now_data) a " +
        "where a.rn=1 " +
        "group by app_source,app_name,app_key,app_version," +
        "         device_type,device_model,device_os,city")
    activeUser.createOrReplaceTempView("active_user")

    //计算留存用户指标
    val remainUser = session
      .sql("select a.device_type,a.device_os," +
        "          a.app_version,a.app_name," +
        "          a.app_source,a.app_key," +
        "          coalesce(a.device_model,'null') device_model," +
        "          coalesce(a.city,'null') city," +
        "          round(sum(case when b.device_id is not null then 1 else 0 end)/count(a.device_id),3) remain_rate " +
        "from last_new_user a " +
        "left join " +
        "(select app_source,app_name," +
        "        app_key,app_version," +
        "        device_type,device_model," +
        "        device_os,device_id," +
        "        row_number() over(partition by device_id order by ctime) rn " +
        "from now_data ) b on a.device_id=b.device_id " +
        "where b.rn = 1 " +
        "group by a.device_type,a.device_os,a.device_model,a.app_version," +
        "         a.app_name,a.app_source,a.app_key,a.city")
    remainUser.createOrReplaceTempView("remain_user")

    //汇总当天所有维度到一张表
    val Tmp = session
      .sql("select app_source,app_name," +
        "          app_key,app_version," +
        "          device_type,device_os," +
        "          coalesce(device_model,'null') device_model," +
        "          coalesce(city,'null') city " +
        "from now_data " +
        "union " +
        "select app_source,app_name," +
        "       app_key,app_version," +
        "       device_type,device_os," +
        "       coalesce(device_model,'null') device_model," +
        "       coalesce(city,'null') city " +
        "from last_new_user")
    Tmp.createOrReplaceTempView("tmp")

    val result = session
      .sql("select " + yearCode + " yearcode,"
        + statisticTimestamp + " statistic_timestamp,"
        + "'" + sdf.format(new Date()) + "' adddate," +
        "concat_ws('-',a.app_source,a.app_name,a.app_key," +
        "       a.app_version,a.device_type,a.device_model,a.device_os,a.city) prefix_id," +
        "a.app_source,a.app_name," +
        "a.app_key,a.app_version," +
        "a.device_type,a.device_model," +
        "a.device_os,a.city city_name," +
        "coalesce(b.new_user,0) new_users," +
        "coalesce(c.login_user,0) active_user_login," +
        "coalesce(c.no_login_user,0) active_user_nologin," +
        "coalesce(d.remain_rate,0) retained_user_rate " +
        "from tmp a " +
        "left join new_user b on a.app_source=b.app_source and a.app_key=b.app_key " +
        "                    and a.app_version=b.app_version and a.device_type=b.device_type " +
        "                    and a.device_model=b.device_model and a.device_os=b.device_os " +
        "                    and a.city=b.city " +
        "left join active_user c on a.app_source=c.app_source and a.app_key=c.app_key " +
        "                       and a.app_version=c.app_version and a.device_type=c.device_type " +
        "                       and a.device_model=c.device_model and a.device_os=c.device_os " +
        "                       and a.city=c.city " +
        "left join remain_user d on a.app_source=d.app_source and a.app_key=d.app_key " +
        "                       and a.app_version=d.app_version and a.device_type=d.device_type " +
        "                       and a.device_model=d.device_model and a.device_os=d.device_os" +
        "                       and a.city=d.city")


    val network = session
      .sql("select " + yearCode + " yearcode,"
        + statisticTimestamp + " statistic_timestamp,"
        + "'" + sdf.format(new Date()) + "' adddate,"+
        "sum(case when app_network='2G' then 1 else 0 end) network_user_cnt_2g," +
        "sum(case when app_network='3G' then 1 else 0 end) network_user_cnt_3g," +
        "sum(case when app_network='4G' then 1 else 0 end) network_user_cnt_4g," +
        "sum(case when app_network='WIFI' then 1 else 0 end) network_user_cnt_wifi," +
        "sum(case when app_network!='2G' and app_network!='3G' and app_network!='4G' " +
        "          and app_network!='WIFI' then 1 else 0 end) network_user_cnt_other " +
        "from now_data ")

    import session.implicits._

    nowData.select("device_type", "events", "histories")
      .flatMap(row => {
        val iosFlag = ConfigurationManager.getProperty(Constants.APP_DEVICE_TYPE_IOS)
        val androidFlag = ConfigurationManager.getProperty(Constants.APP_DEVICE_TYPE_ANDROID)
        val deviceType = row.get(0)
        val events = row.getList(1)
        val history = row.getList(2)

        var startCount = 0
        var startTime = 0L

        if (iosFlag.equals(deviceType)){
          for (i <- 0 until events.size){
            startCount += 1
          }

          for (i <- 0 until history.size){
            val list = history.get(i).toString.replace("]","").split(",")
            if (list(2) != "-1" && list(1) < list(2)) {
              startTime = startTime + dateUtils.dateDiff(list(1), list(2))
            }
          }
        }

        if (androidFlag.equals(deviceType)){
          startCount += 1

          for (i <- 0 until history.size -1){
            val list1 = history.get(i).toString.replace("]","").split(",")
            val list2 = history.get(i + 1).toString.replace("]","").split(",")
            if (dateUtils.dateDiff(list1(2), list2(1)) > 30000){
              startCount += 1
            }
          }

          for (i <- 0 until history.size){
            val list = history.get(i).toString.replace("]","").split(",")
            if (list(2) != "-1" && list(1) < list(2)) {
              startTime = startTime + dateUtils.dateDiff(list(1), list(2))
            }
          }
        }

        Some(startCount, startTime)
      }).createOrReplaceTempView("app_events")

    val events = session
      .sql("select " + yearCode + " yearcode,"
        + statisticTimestamp + " statistic_timestamp,"
        + "'" + sdf.format(new Date()) + "' adddate," +
        "coalesce(sum(_1),0) app_start_cnt," +
        "coalesce(sum(_2),0) app_time_all_user," +
        "coalesce(sum(_2)/count(1),0) app_time_avg_user," +
        "coalesce(sum(_2)/sum(_1),0) app_time_avg_per_start " +
        "from app_events")

//    result.show()
//    network.show()
//    events.show()

    //将数据写入Cassandra
    result.write
      .format(sparkCassandra)
      .options(Map("keyspace"->nirvana, "table"->userTable))
      .mode(SaveMode.Append)
      .save()

    network.write
      .format(sparkCassandra)
      .options(Map("keyspace"->nirvana, "table"->networkTable))
      .mode(SaveMode.Append)
      .save()

    events.write
      .format(sparkCassandra)
      .options(Map("keyspace"->nirvana, "table"->timeTable))
      .mode(SaveMode.Append)
      .save()

    session.stop()
  }


}
