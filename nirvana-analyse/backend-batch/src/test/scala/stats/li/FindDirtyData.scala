package stats.li

import com.caiyi.nirvana.analyse.DateUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
  * Created by li on 2017/2/22.
  */
object FindDirtyData {

  val dateUtils = new DateUtils

  //查找用户时长数据
  def startTime() = {

    val conf = new SparkConf(true).set("spark.cassandra.connection.host", "192.168.83.26")

    val session = SparkSession.builder().config(conf)
      .appName("AppDayStatisticsAnalyse")
      .master("local")
      .getOrCreate()

    val df = session
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "nirvana", "table" -> "app_profile"))
      .load()
      .where("to_date(ctime) = '2017-02-21' ")

    import session.implicits._

    val timeDetails = df.select("histories", "id")
      .flatMap(row => {
        val history = row.getList(0)
        var startTime = ""
        var endTime = ""

        for (i <- 0 until history.size) {
          val list = history.get(i).toString.replace("]", "").split(",")
          if (list(2) != "-1" && list(1) < list(2) && dateUtils.dateDiff(list(1), list(2)) >= 36000000) {
            startTime = list(1)
            endTime = list(2)
          }
        }

        Some(startTime, endTime, row.get(1).toString)
      })
    //    timeDetails.show()

    val result = timeDetails.where("_1 !='' and _2 !=''")
    result.foreach(row => println(row))
  }

  def main(args: Array[String]): Unit = {

    startTime()
  }

}
