package stats.li

import java.util.Date

import com.caiyi.nirvana.analyse.DateUtils
import com.caiyi.nirvana.analyse.count.canstant.Constants
import com.caiyi.nirvana.analyse.count.conf.ConfigurationManager
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by li on 2017/2/22.
  */
object StartTimeStats {

  val date = new Date()
  val dateUtils = new DateUtils()
  val getTime = dateUtils.splitDate(dateUtils.getLastDay(date, -2))
  val getLastTime = dateUtils.splitDate(dateUtils.getLastDay(date, -2))

  //时间查询条件
  val queryTime = "year(ctime)=" + getTime("year") + " and lpad(month(ctime),2,0) =" + getTime("month") +
    " and lpad(day(ctime),2,0)=" + getTime("day")
  val queryLastTime = "year(ctime)=" + getLastTime("year") + " and lpad(month(ctime),2,0) =" + getLastTime("month") +
    " and lpad(day(ctime),2,0)=" + getLastTime("day")

  //cassandra地址
  val cassandraHost = ConfigurationManager.getProperty(Constants.CASSANDRA_CANTRACT_POINTS)

  //连接信息
  val nirvana = ConfigurationManager.getProperty(Constants.CASSANDRA_KEYSPACE_NAME)
  val appProfile = "app_profile"
  val newUserTable = "app_new_user"
  val sparkCassandra = "org.apache.spark.sql.cassandra"

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf(true).set("spark.cassandra.connection.host", cassandraHost)

    val session = SparkSession.builder().config(conf)
      .appName("AppDayStatisticsAnalyse")
      .master("local")
      .getOrCreate()

    //读取今天要计算的app_profile数据
    val nowData = session
      .read
      .format(sparkCassandra)
      .options(Map("keyspace" -> nirvana, "table" -> appProfile))
      .load()
      .where(queryTime)
    nowData.createOrReplaceTempView("now_data")

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

        if (iosFlag.equals(deviceType)) {
          for (i <- 0 until events.size) {
            startCount += 1
          }

          for (i <- 0 until history.size) {
            val list = history.get(i).toString.replace("]", "").split(",")
            if (list(2) != "-1" && list(1) < list(2)) {
              startTime = startTime + dateUtils.dateDiff(list(1), list(2))
            }
          }
        }

        if (androidFlag.equals(deviceType)) {
          startCount += 1

          for (i <- 0 until history.size - 1) {
            val list1 = history.get(i).toString.replace("]", "").split(",")
            val list2 = history.get(i + 1).toString.replace("]", "").split(",")
            if (dateUtils.dateDiff(list1(2), list2(1)) >= 30000) {
              startCount += 1
            }
          }

          for (i <- 0 until history.size) {
            val list = history.get(i).toString.replace("]", "").split(",")
            if (list(2) != "-1" && list(1) < list(2)) {
              startTime = startTime + dateUtils.dateDiff(list(1), list(2))
            }
          }
        }

        Some(startCount, startTime)
      }).createOrReplaceTempView("app_events")

    val events = session
      .sql("select coalesce(sum(_1),0) app_start_cnt," +
        "coalesce(sum(_2),0) app_time_all_user," +
        "coalesce(sum(_2)/count(1),0) app_time_avg_user," +
        "coalesce(sum(_2)/sum(_1),0) app_time_avg_per_start " +
        "from app_events")

    events.show()


  }


}
