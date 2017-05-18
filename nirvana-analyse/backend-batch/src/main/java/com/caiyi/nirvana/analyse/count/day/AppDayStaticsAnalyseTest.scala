package com.caiyi.nirvana.analyse.count.day

import java.text.SimpleDateFormat
import java.util.Date

import com.caiyi.nirvana.analyse.util.DateUtils2
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * Created by Socean on 2017/2/9.
 */
object AppDayStaticsAnalyseTest {
  def main(args: Array[String]) {
    val date: Date = new Date

    val strTime = "2016-12-11 03:11:11" //当前时间
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date1 = sdf.parse(strTime)

    val nowDate = sdf.format(date) //入库时间

    val beforeOneDate = DateUtils2.getBeforeNumDay(date1, -1) //获取当前时间前一天
    val beforeTwoDate = DateUtils2.getBeforeNumDay(date1, -2) //获取当前时间前2天

    val beforeOneYear = DateUtils2.splitDate(beforeOneDate)("year") //获取当前时间前一天所属年的Code

    val beforeOneMonth = DateUtils2.splitDate(beforeOneDate)("month") //获取当前时间前一天所属月的Code
    val beforeTwoMonth = DateUtils2.splitDate(beforeTwoDate)("month") //获取当前时间前两天所属月的Code

    val beforeOneDay = DateUtils2.splitDate(beforeOneDate)("day") //获取当前时间的前一天是当月的第几天
    val beforeTwoDay = DateUtils2.splitDate(beforeTwoDate)("day") //获取当前时间的前两天是当月的第几天

    val firstWhereCondition = "lpad(month(ctime),2,0)=" + beforeOneMonth + " and lpad(day(ctime),2,0)=" + beforeOneDay
    val secondWhereCondition = "lpad(month(ctime),2,0)=" + beforeTwoMonth + " and lpad(day(ctime),2,0)=" + beforeTwoDay

    val cassandraContactPoints = "192.168.1.88"

    val conf = new SparkConf().setAppName("AppDayStaticsAnalyseTest").setMaster("local[2]")
    conf.set("spark.cassandra.connection.host", cassandraContactPoints)

    val sparkSession = SparkSession.builder().config(conf).getOrCreate()

    val dataFrameReader = sparkSession.read.format("org.apache.spark.sql.cassandra").option("keyspace", "nirvana")

    //获取所有新用户表的数据
    val userDataFrame = dataFrameReader.option("table", "app_new_user").load().cache()
    //获取当昨天内新用户表的数据
    val appOneDayNewUserDF = userDataFrame.where(firstWhereCondition)
    //获取当前天的新用户的数据
    val appTwoDayNewUserDF = userDataFrame.where(secondWhereCondition)

    //获取原始数据app_profile表中的数据
    val appDayProfileBankDF = dataFrameReader.option("table", "app").load().where(firstWhereCondition)

    //将新用户DF注册成临时视图app_new_user_one
    userDataFrame.createOrReplaceTempView("app_all_user")
    appOneDayNewUserDF.createOrReplaceTempView("app_new_user_one")
    appTwoDayNewUserDF.createOrReplaceTempView("app_new_user_two")

    //将原始数据DF注册成临时视图app_profile
    appDayProfileBankDF.createOrReplaceTempView("app_profile")

    //将当前统计周期内的用户 是新增用户的数据保留下来插入到新用户表中
    val newUserDF = sparkSession.sql("select pp.ctime,pp.device_id,pp.channel app_channel,pp.gps app_gps," +
      "pp.ip app_ip,pp.key app_key,pp.app_name app_name,pp.network app_network,pp.source app_source," +
      "pp.version app_version,pp.city city, pp.city_code city_code,pp.brand device_brand,pp.model device_model," +
      "pp.os device_os,pp.res device_res,pp.type device_type,pp.province province, " +
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
      "app.app_key key, " +
      "app.ctime " +
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

    /*newUserDF.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "table_name")).mode(SaveMode.Append).save()*/

    //將app_profile表和app_new_user表进行Union后去重，作为最终的宽表
    val appDayProfileAndAppUserDF = sparkSession.sql("select app_source,app_name,app_key,app_version," +
      "device_type,device_model,device_os,city " +
      "from app_profile pp " +
      "union  " +
      "select app_source,app_name,app_key,app_version,device_type,device_model,device_os,city " +
      "from app_new_user_one uu ")

    //注冊一张宽表：app_profile_user
    appDayProfileAndAppUserDF.createOrReplaceTempView("app_profile_user")

    //统计新增用户
    val appDayNewAddUserDF = sparkSession.sql("select " +
      "app_source, " +
      "app_name, " +
      "app_key, " +
      "app_version, " +
      "device_type, " +
      "device_model, " +
      "device_os, " +
      "city, " +
      "count(1) new_user_count " +
      "from app_new_user_one " +
      "where 1=1 " +
      "group by app_source,app_name,app_key,app_version,device_type,device_model,device_os,city")

    //将新增用户DF注册为一张临时视图
    appDayNewAddUserDF.createOrReplaceTempView("add_new_user")

    //统计当天留存用户
    val dayRetentionUserDF = sparkSession.sql("select uu.app_source,uu.app_name,uu.app_key,uu.app_version," +
      "uu.device_type,uu.device_model,uu.device_os,uu.city," +
      "round( sum(case when app.device_id is not null then 1 else 0 end)/count(uu.device_id),3) retention_rate " +
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
    dayRetentionUserDF.createOrReplaceTempView("retention_user")

    //统计活跃用户中登录用户和非登录用户数量
    val appDayProfileDetailDF = sparkSession.sql("select " +
      "pp.app_source,pp.app_name,pp.app_key,pp.app_version,pp.device_type,pp.device_model,pp.device_os,pp.city, " +
      "sum (( case when pp.user_id != 'null' or pp.user_name != 'null' then 1 else 0 end )) login_count, " +
      "sum (( case when pp.user_id = 'null' and pp.user_name = 'null' then 1 else 0 end )) no_login_count " +
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
    appDayProfileDetailDF.createOrReplaceTempView("app_login")

    //1->日用户统计
    //将活跃用户/留存用户/新增用户进行关联
    val dayAppUserStatsDF = sparkSession.sql("select '" +
      beforeOneYear + beforeOneMonth + "' monthcode, '" +
      beforeOneYear + beforeOneMonth + beforeOneDay + "' statistic_timestamp, '" +
      nowDate + "' adddate , " +
      "apu.app_source,apu.app_name,apu.app_key," +
      "apu.app_version,apu.device_type,apu.device_model,apu.device_os,apu.city, " +
      "coalesce(al.login_count,0) login_count, " +
      "coalesce(al.no_login_count,0) no_login_count, " +
      "coalesce(ru.retention_rate,0) retention_rate, " +
      "coalesce(anu.new_user_count,0) new_user_count " +
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

    //保存到cassandra表中
    /*dayAppUserStatsDF.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "table_name")).mode(SaveMode.Append).save()*/

    //2->周用户统计网络
    val appDayNetWorkStatsDF = sparkSession.sql("select '" +
      beforeOneYear + beforeOneMonth + "' monthcode, '" +
      beforeOneYear + beforeOneMonth + beforeOneDay + "' statistic_timestamp, '" +
      nowDate + "' adddate , " +
      "sum((CASE WHEN app_network != '2G' and app_network != '3G' and app_network != '4G' and app_network " +
      "!= 'wifi' THEN 1 ELSE 0 END)) other, " +
      "sum((CASE WHEN app_network ='2G' THEN 1 ELSE 0 END)) 2count, " +
      "sum((CASE WHEN app_network ='3G' THEN 1 ELSE 0 END)) 3count, " +
      "sum((CASE WHEN app_network ='4G' THEN 1 ELSE 0 END)) 4count, " +
      "sum((CASE WHEN app_network ='wifi' THEN 1 ELSE 0 END)) wifiCount " +
      "from app_profile ")

    //保存到表中
    /* appDayNetWorkStatsDF.write
       .format("org.apache.spark.sql.cassandra")
       .options(Map("keyspace" -> "nirvana", "table" -> "table_name")).mode(SaveMode.Append).save()*/

    import sparkSession.implicits._

    val dayAppUserOnlineDF = appDayProfileBankDF.select("device_id", "device_type", "ctime", "events", "histories")

    val dayUserOnlineStaticsDF = dayAppUserOnlineDF.flatMap(row => {

      //val key = DateUtils.getDateDay(row.getTimestamp(2))

      val deviceId = row.get(0).toString
      val deviceType = row.get(1)
      val historyList = row.getList(4)

      val androidFlag = "0"
      val iosFlag = "1"

      var startNumsCount = 0
      var userVisitTimes = 0L

      if (androidFlag.equals(deviceType)) {
        startNumsCount += 1

        for (i <- 0 until historyList.size()) {
          val arr = historyList.get(i).toString.split(",")

          val enterTime = DateUtils2.parseTime(arr(1)).getTime
          val exitTime = DateUtils2.parseTime(arr(2)).getTime

          userVisitTimes += DateUtils2.compareTimeMillis(enterTime, exitTime)

        }

        for (i <- 0 until historyList.size() - 1) {
          val arr1 = historyList.get(i).toString.split(",")
          val arr2 = historyList.get(i + 1).toString.split(",")

          val firstExitTime = DateUtils2.parseTime(arr1(2)).getTime
          val secondEnterTime = DateUtils2.parseTime(arr2(1)).getTime

          val accessTime = DateUtils2.compareTimeMillis(firstExitTime, secondEnterTime)

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

          val enterTime = DateUtils2.parseTime(arr(1)).getTime
          val exitTime = DateUtils2.parseTime(arr(2)).getTime

          userVisitTimes += DateUtils2.compareTimeMillis(enterTime, exitTime)
        }

      }

      Some(startNumsCount, userVisitTimes, 1)
    })

    dayUserOnlineStaticsDF.createOrReplaceTempView("user_visit_time")

    //3—>统计周的启动时长和访问时间
    val countUserVisitTimeDF = sparkSession.sql("select '" +
      beforeOneYear + beforeOneMonth + "' monthcode, '" +
      beforeOneYear + beforeOneMonth + beforeOneDay + "' statistic_timestamp, '" +
      nowDate + "' adddate , " +
      "nvl(sum(_1),0) app_start_cnt, " +
      "nvl(sum(_2),0) app_time_all_user, " +
      //"nvl(sum(_3),0) userNum, " + 总用户数
      "coalesce((nvl(sum(_2),0) / nvl(sum(_3),0)),0) app_time_avg_user, " +
      "coalesce((nvl(sum(_2),0) / nvl(sum(_1),0)),0) app_time_avg_per_start " +
      "from user_visit_time")

    countUserVisitTimeDF.show()

    //保存到表中
    /*countUserVisitTimeDF.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "tb_app_time_stats_week")).mode(SaveMode.Append).save()*/

    sparkSession.stop()

  }

}
