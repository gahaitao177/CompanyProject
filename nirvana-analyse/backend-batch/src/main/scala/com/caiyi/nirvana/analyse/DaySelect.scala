package com.caiyi.nirvana.analyse

import org.apache.spark.sql.SparkSession

/**
  * Created by li on 2017/2/9.
  */
object DaySelect {

  val du = new DateUtils

  def main(args: Array[String]): Unit = {
    val session = SparkSession.builder()
      .appName("AppDayStatisticsAnalyse")
      .master("local")
      .getOrCreate()

    session.sql("select day('2016-01-01')").show()
  }

}
