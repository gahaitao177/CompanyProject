package com.caiyi.nirvana.analyse.demo

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * Created by root on 2017/2/13.
 */
object TimeDemo {
  def main(args: Array[String]) {
    var date: Date = new Date
    val strTime = "2017-02-11 03:11:11"
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    date = sdf.parse(strTime)

    val beforeOneDate = DateUtils2.getBeforeNumDay(date, -1) //获取当前时间前一天

    val beforeOneDay = DateUtils2.splitDate(beforeOneDate)("day") //获取当前时间的前一天是当月的第几天
    val beforeOneMonth = DateUtils2.splitDate(beforeOneDate)("month") //获取当前时间前一天所属月的Code
    val beforeOneYear = DateUtils2.splitDate(beforeOneDate)("year") //获取当前时间前一天所属年的Code

    val firstWhereCondition = "year(ctime) = " + beforeOneYear + " and lpad(month(ctime),2,0) = " + beforeOneMonth +
      " and lpad(day(ctime),2,0) = " + beforeOneDay

    val cassandraContactPoints = "192.168.83.26"

    val conf = new SparkConf().setAppName("AppDayStaticsAnalyseTest").setMaster("local[2]")
    conf.set("spark.cassandra.connection.host", cassandraContactPoints)

    val sparkSession = SparkSession.builder().config(conf).getOrCreate()

    val dataFrameReader = sparkSession.read.format("org.apache.spark.sql.cassandra").option("keyspace", "nirvana")

    val appDF = dataFrameReader.option("table", "app_profile").load()/*.where(firstWhereCondition)*/

    appDF.createOrReplaceTempView("app")

    val bbDF = sparkSession.sql("select count(distinct(device_id)) from app ")
    /*val bbDF = sparkSession.sql("select count(distinct(device_id)) from app where app_name = '车险计算器' ")*/

    val userDF = dataFrameReader.option("table", "app_new_user").load()/*.where(firstWhereCondition)*/

    userDF.createOrReplaceTempView("user")

    val uuDF = sparkSession.sql("select count(distinct(device_id)) from user ")

    println("*****************")
    uuDF.show()
    println("*****************")

    //val whereCondition = " to_date(2016-12-09) = '2016-12-09' and hour(ctime) = 3"

    //获取新新用户表中的数据 app_new_user
    /*val appDayNewUserDetailBankDF = dataFrameReader.option("table", "app_new_user").load().
      where("to_date(ctime) = '2016-12-09' and hour(ctime) = 11")*/


    //sparkSession.sql("select year('2020-01-01 10:10:34')  ").show()

    /*sparkSession.sql("select lpad (month('2016-01-09 03:11:11'),2,0) ").show()*/

    //sparkSession.sql("select month('2020-03-01 10:10:34') ").show()

    /*sparkSession.sql("select lpad (day('2016-01-09 03:11:11'),2,0)").show()

    sparkSession.sql("select round( 1/1,3)").show()*/

    /*/*val dateMap = DateUtilsDemo.splitDate(date)

    println("year=" + dateMap("year") + " month=" + dateMap("month") + " day=" + dateMap("day") + " hour="
      + dateMap("hour") + " week=" + dateMap("week"))*/

    val dateHour = DateUtilsDemo.getDateHour(date)
    println("-------" + dateHour)

    var weekYearCode = DateUtilsDemo.getNowWeekOfYear(date)
    val weekOfYear = Integer.valueOf(weekYearCode)
    println("当前时间所属的周=" + weekOfYear)

    val list = DateUtilsDemo.getDateToWeek(date)

    val startWeekDay = sdf.format(list.get(0))
    val endWeekDay = sdf.format(list.get(list.size() - 1))
    System.out.println("当前时间点所属周的 开始时间：" + startWeekDay + " 结束时间：" + endWeekDay)

    for (i <- 0 until list.size()) {
      val dayCode = sdf.format(list.get(i))
      println(dayCode)
    }*/


  }
}
