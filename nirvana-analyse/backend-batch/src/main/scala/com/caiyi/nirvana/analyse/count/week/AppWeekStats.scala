package com.caiyi.nirvana.analyse.count.week

import java.text.SimpleDateFormat
import java.util.Date

import com.caiyi.nirvana.analyse.DateUtils
import com.caiyi.nirvana.analyse.count.canstant.Constants
import com.caiyi.nirvana.analyse.count.conf.ConfigurationManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * Created by Socean on 2017/2/13.
  */
object AppWeekStats {
  val dateUtils = new DateUtils()

  def main(args: Array[String]) {
    val date: Date = new Date
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val beforeOneDate = dateUtils.getLastDay(date, -1)
    //获取当前时间前一天
    val beforeEightDate = dateUtils.getLastDay(date, -8) //获取当前时间前八天


    val beforeOneYear = dateUtils.splitDate(beforeOneDate)("year")
    //获取当前时间前一天所属年的Code
    val beforeTwoYear = dateUtils.splitDate(beforeEightDate)("year") //获取当前时间前八天所属年的Code

    val beforeOneWeek = dateUtils.splitDate(beforeOneDate)("week")
    //获取当前时间前一天时间所属的周
    val beforeTwoWeek = dateUtils.splitDate(beforeEightDate)("week") //获取当前时间前八天所属的周

    val firstWhereCondition = "year(ctime) = " + beforeOneYear + " and lpad(weekofyear(ctime),2,0) = " + beforeOneWeek
    val secondWhereCondition = "year(ctime) =" + beforeTwoYear + " and lpad(weekofyear(ctime),2,0) = " + beforeTwoWeek

    val cassandraContactPoints = ConfigurationManager.getProperty(Constants.CASSANDRA_CANTRACT_POINTS)

    val conf = new SparkConf().setAppName("AppDayStaticsAnalyseTest").setMaster("local[2]")
    conf.set("spark.cassandra.connection.host", cassandraContactPoints)
    /*conf.set("spark.default.parallelism", "20")
    conf.set("spark.shuffle.io.maxRetries", "60")
    conf.set("spark.shuffle.io.retryWait", "60")*/

    val sparkSession = SparkSession.builder().config(conf).getOrCreate()

    val dataFrameReader = sparkSession.read.format("org.apache.spark.sql.cassandra").option("keyspace", "nirvana")

    //获取所有新用户表的数据
    val userDataFrame = dataFrameReader.option("table", "app_new_user").load().cache()
    //获取当前一周内新用户表的数据
    val appOneWeekNewUserDF = userDataFrame.where(firstWhereCondition)
    //获取当前一周前一周的新用户的数据
    val appTwoWeekNewUserDF = userDataFrame.where(secondWhereCondition)

    //获取原始数据app_profile表中的数据
    val appWeekProfileBankDF = dataFrameReader.option("table", "app_profile").load().where(firstWhereCondition)

    //将新用户DF注册成临时视图app_new_user_one
    userDataFrame.createOrReplaceTempView("app_all_user")
    appOneWeekNewUserDF.createOrReplaceTempView("app_new_user_one")
    appTwoWeekNewUserDF.createOrReplaceTempView("app_new_user_two")

    //将原始数据DF注册成临时视图app_profile
    appWeekProfileBankDF.createOrReplaceTempView("app_profile")

    //将当前统计周期内的用户 是新增用户的数据保留下来插入到新用户表中
    val newUserDF = sparkSession.sql("select '" + sdf.format(new Date()) + "' ctime, " +
      "pp.device_id device_id,pp.channel app_channel,pp.gps " +
      "app_gps,pp.ip app_ip,pp.key app_key,pp.app_name app_name,pp.network app_network," +
      "pp.source app_source,pp.version app_version," +
      "pp.city city, pp.city_code city_code," +
      "pp.brand device_brand,pp.model device_model," +
      "pp.os device_os,pp.res device_res,pp.type device_type," +
      "pp.province province, " +
      "pp.user_id user_id,pp.user_name user_name " +
      "from ( " +
      "select " +
      "app.device_id device_id, " +
      "app.city city, " +
      "app.city_code city_code, " +
      "app.province province, " +
      "app.device_type type, " +
      "app.device_os os, " +
      "app.device_model model, " +
      "app.device_brand brand, " +
      "app.device_res res, " +
      "app.app_version version, " +
      "app.app_name app_name, " +
      "app.app_source source, " +
      "app.app_channel channel, " +
      "app.app_network network, " +
      "app.app_gps gps, " +
      "app.user_id user_id, " +
      "app.user_name user_name, " +
      "app.app_ip ip, " +
      "app.app_key key " +
      "from " +
      "(select " +
      "device_id, device_type, device_os, device_model, device_brand, device_res, app_version, " +
      "city,city_code,province, " +
      "app_name, app_source, app_channel, app_network, app_gps, user_id, user_name, app_ip, app_key," +
      "ctime, " +
      "row_number() over(partition by device_id order by ctime) rank " +
      "from app_profile " +
      ") app where rank = 1 ) pp " +
      "left join app_all_user uu on uu.device_id = pp.device_id " +
      "where uu.device_id is null ")

    //將app_profile表和app_new_user表进行Union后去重，作为最终的宽表
    val appWeekProfileAndAppUserDF = sparkSession.sql("select app_source,app_name,app_key,app_version," +
      "device_type,device_model,device_os,coalesce(city, 'null') city " +
      "from app_profile pp " +
      "union  " +
      "select app_source,app_name,app_key,app_version,device_type,device_model,device_os,coalesce(city, 'null') city " +
      "from app_new_user_two uu ")

    //注冊一张宽表：app_profile_user
    appWeekProfileAndAppUserDF.createOrReplaceTempView("app_profile_user")

    //统计新增用户
    val appWeekNewAddUserDF = sparkSession.sql("select " +
      "app_source, " +
      "app_name, " +
      "app_key, " +
      "app_version, " +
      "device_type, " +
      "device_model, " +
      "device_os, " +
      "coalesce(city, 'null') city , " +
      "count(1) new_user_count " +
      "from app_new_user_one " +
      "where 1=1 " +
      "group by app_source,app_name,app_key,app_version,device_type,device_model,device_os,city")

    //将新增用户DF注册为一张临时视图
    appWeekNewAddUserDF.createOrReplaceTempView("add_new_user")

    //统计当周留存用户
    val weekRetentionUserDF = sparkSession.sql("select uu.app_source,uu.app_name,uu.app_key,uu.app_version," +
      "uu.device_type,uu.device_model,uu.device_os,coalesce(uu.city, 'null') city," +
      "round(sum(case when app.device_id is not null then 1 else 0 end)/count(uu.device_id),3) retention_rate " +
      "from app_new_user_two uu left join " +
      "(" +
      "select device_id, " +
      "ctime, " +
      "app_source, " +
      "app_name, " +
      "app_key, " +
      "app_version, " +
      "device_type, " +
      "device_model, " +
      "device_os, " +
      "city, " +
      "row_number() OVER (partition by device_id order by ctime) rank from " +
      "app_profile " +
      ") app on uu.device_id = app.device_id and app.rank = 1 " +
      "group by uu.app_source,uu.app_name,uu.app_key,uu.app_version,uu.device_type,uu.device_model, " +
      "uu.device_os,uu.city ")

    //将当周留存用户DF注册为一张临时视图
    weekRetentionUserDF.createOrReplaceTempView("retention_user")

    //统计活跃用户中登录用户和非登录用户数量
    val appWeekProfileDetailDF = sparkSession.sql("select " +
      "pp.app_source,pp.app_name,pp.app_key,pp.app_version,pp.device_type,pp.device_model,pp.device_os," +
      "coalesce(pp.city, 'null') city , " +
      "sum (( case when pp.user_id is not null or pp.user_id != '' or pp.user_name is not null or pp.user_name != '' " +
      "then 1 else 0 end )) login_count, " +
      "sum (( case when pp.user_id = 'null' or pp.user_id = '' and pp.user_name = 'null' or pp.user_name = '' " +
      "then 1 else 0 end )) no_login_count " +
      " from (" +
      "select " +
      "device_id, " +
      "ctime, " +
      "app_source, " +
      "app_name, " +
      "app_key, " +
      "app_version, " +
      "device_type, " +
      "device_model, " +
      "device_os, " +
      "city, " +
      "user_id, " +
      "user_name, " +
      "row_number() OVER (partition by device_id order by ctime) rank " +
      "from app_profile " +
      ") pp where rank = 1 " +
      "group by pp.app_source,pp.app_name,pp.app_key,pp.app_version,pp.device_type,pp.device_model," +
      "pp.device_os,pp.city")

    //将活跃用户<登录用户，非登录用户>注册成一张临时表
    appWeekProfileDetailDF.createOrReplaceTempView("app_login")

    //1->周用户统计
    //将活跃用户/留存用户/新增用户进行关联
    val weekAppUserStatsDF = sparkSession.sql("select '" +
      beforeOneYear + "' yearcode, '" +
      beforeOneYear + beforeOneWeek + "' statistic_timestamp, '" +
      sdf.format(new Date()) + "' adddate , " +
      "concat_ws('-',apu.app_source,apu.app_name,apu.app_key,apu.app_version,apu.device_type," +
      "apu.device_model,apu.device_os,apu.city) prefix_id, " +
      "apu.app_source,apu.app_name,apu.app_key,apu.app_version,apu.device_type,apu.device_model," +
      "apu.device_os,apu.city city_name, " +
      "coalesce(al.login_count,0) active_user_login, " +
      "coalesce(al.no_login_count,0) active_user_nologin, " +
      "coalesce(ru.retention_rate,0) retained_user_rate, " +
      "coalesce(anu.new_user_count,0) new_users " +
      "from app_profile_user apu " +
      "left join app_login al on al.app_source = apu.app_source and al.app_name = apu.app_name " +
      "and al.app_key = apu.app_key and al.app_version = apu.app_version " +
      "and al.device_type = apu.device_type and al.device_model = apu.device_model " +
      "and al.device_os = apu.device_os and al.city = apu.city " +
      "left join retention_user ru on ru.app_source = apu.app_source and ru.app_name = apu.app_name " +
      "and ru.app_key = apu.app_key and ru.app_version = apu.app_version " +
      "and ru.device_type = apu.device_type and ru.device_model = apu.device_model " +
      "and ru.device_os = apu.device_os and ru.city = apu.city " +
      "left join add_new_user anu on anu.app_source = apu.app_source and anu.app_name = apu.app_name " +
      "and anu.app_key = apu.app_key and anu.app_version = apu.app_version " +
      "and anu.device_type = apu.device_type and anu.device_model = apu.device_model " +
      "and anu.device_os = apu.device_os and anu.city = apu.city ")

    //2->周用户统计网络
    val appWeekNetWorkStatsDF = sparkSession.sql("select '" +
      beforeOneYear + "' yearcode, '" +
      beforeOneYear + beforeOneWeek + "' statistic_timestamp, '" +
      sdf.format(new Date()) + "' adddate , " +
      "coalesce(sum((CASE WHEN app_network != '2G' and app_network != '3G' and app_network != '4G' and app_network " +
      "!= 'WIFI' THEN 1 ELSE 0 END)),0) network_user_cnt_other, " +
      "coalesce(sum((CASE WHEN app_network ='2G' THEN 1 ELSE 0 END)),0) network_user_cnt_2g, " +
      "coalesce(sum((CASE WHEN app_network ='3G' THEN 1 ELSE 0 END)),0) network_user_cnt_3g, " +
      "coalesce(sum((CASE WHEN app_network ='4G' THEN 1 ELSE 0 END)),0) network_user_cnt_4g, " +
      "coalesce(sum((CASE WHEN app_network ='WIFI' THEN 1 ELSE 0 END)),0) network_user_cnt_wifi " +
      "from app_profile ")

    import sparkSession.implicits._

    val weekAppUserOnlineDF = appWeekProfileBankDF.select("device_id", "device_type", "ctime", "events", "histories")

    val weekUserOnlineStaticsDF = weekAppUserOnlineDF.flatMap(row => {

      val deviceType = row.get(1)
      val historyList = row.getList(4)

      val androidFlag = ConfigurationManager.getProperty(Constants.APP_DEVICE_TYPE_ANDROID)
      val iosFlag = ConfigurationManager.getProperty(Constants.APP_DEVICE_TYPE_IOS)

      var startNumsCount = 0
      var userVisitTimes = 0L

      if (androidFlag.equals(deviceType)) {
        startNumsCount += 1

        for (i <- 0 until historyList.size()) {
          val arr = historyList.get(i).toString.split(",")

          val enterTime = arr(1)
          val exitTime = arr(2)

          if (exitTime != "-1" && enterTime <= exitTime) {
            userVisitTimes += dateUtils.dateDiff(enterTime, exitTime)
          }
        }

        for (i <- 0 until historyList.size() - 1) {
          val arr1 = historyList.get(i).toString.split(",")
          val arr2 = historyList.get(i + 1).toString.split(",")

          val firstExitTime = arr1(2)
          val secondEnterTime = arr2(1)

          val accessTime = dateUtils.dateDiff(firstExitTime, secondEnterTime)

          if (accessTime >= 30000) {
            startNumsCount += 1
          }

        }

      }

      if (iosFlag.equals(deviceType)) {
        val eventsList = row.getList(3)

        for (i <- 0 until eventsList.size()) {
          startNumsCount += 1
        }

        for (i <- 0 until historyList.size()) {
          val arr = historyList.get(i).toString.split(",")

          val enterTime = arr(1)
          val exitTime = arr(2)

          if (exitTime != "-1" && enterTime <= exitTime) {
            userVisitTimes += dateUtils.dateDiff(enterTime, exitTime)
          }
        }

      }

      Some(startNumsCount, userVisitTimes, 1)
    })

    weekUserOnlineStaticsDF.createOrReplaceTempView("user_visit_time")

    //3—>统计周的启动时长和访问时间
    val countUserVisitTimeDF = sparkSession.sql("select '" +
      beforeOneYear + "' yearcode, '" +
      beforeOneYear + beforeOneWeek + "' statistic_timestamp, '" +
      sdf.format(new Date()) + "' adddate , " +
      "nvl(sum(_1),0) app_start_cnt, " +
      "nvl(sum(_2),0) app_time_all_user, " +
      //"nvl(sum(_3),0) userNum, " + 总用户数
      "coalesce((nvl(sum(_2),0) / nvl(sum(_3),0)),0) app_time_avg_user, " +
      "coalesce((nvl(sum(_2),0) / nvl(sum(_1),0)),0) app_time_avg_per_start " +
      "from user_visit_time")

    //保存到cassandra表中
    weekAppUserStatsDF.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "tb_app_users_stats_week")).mode(SaveMode.Append).save()

    appWeekNetWorkStatsDF.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "tb_app_network_stats_week")).mode(SaveMode.Append).save()

    countUserVisitTimeDF.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "tb_app_time_stats_week")).mode(SaveMode.Append).save()

    sparkSession.stop()
  }
}
