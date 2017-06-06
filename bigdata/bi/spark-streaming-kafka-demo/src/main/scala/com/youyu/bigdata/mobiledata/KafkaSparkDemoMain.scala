package com.youyu.bigdata.mobiledata

import com.alibaba.fastjson.{JSON, JSONObject}
import kafka.serializer.StringDecoder
import org.apache.hadoop.hbase.TableName
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Duration, StreamingContext}

/**
  * Created by xiaxc on 2017/5/11.
  */
object KafkaSparkDemoMain {

  def main(args: Array[String]) {



    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("kafka-spark-demo")
    val scc = new StreamingContext(sparkConf, Duration(5000))
    scc.checkpoint(".")
    // 因为使用到了updateStateByKey,所以必须要设置checkpoint
    val topics = Set("youyu_mobile_data_after_etl")
    //我们需要消费的kafka数据的topic
    val kafkaParam = Map(
      "metadata.broker.list" -> "192.168.83.26:9475,192.168.83.25:9840,192.168.83.23:9164" // kafka的broker list地址
    )

    val stream: InputDStream[(String, String)] = createStream(scc, kafkaParam, topics)

    val reports = stream.flatMap(line => {
      val data = JSON.parseObject(line._2)
      Some(data)
    })

    sumNumByHour(reports)


    scc.start() // 真正启动程序
    scc.awaitTermination() //阻塞等待
  }

  def sumNumByHour(reports: DStream[JSONObject]) = {
    val report_num = reports.map(x=>(x.getString("reportTime").substring(0,13) , 1)).reduceByKey(_+_)
    report_num.foreachRDD(rdd =>{
      rdd.foreachPartition(partitionOfRecords => {
        val connection = HbaseUtil.getHbaseConn
        partitionOfRecords.foreach(pair => {
          val hour = pair._1
          val num = pair._2
          val tableName = TableName.valueOf("test_mobile_data_xiaxc")
          val t = connection.getTable(tableName)
          try {
            //row_key   family   column num
            t.incrementColumnValue(hour.getBytes,"data".getBytes,"num".getBytes,num)

          } catch {
            case e: Exception =>
              // log error
              e.printStackTrace()
          } finally {
            t.close()
          }
        })
      })
    })
  }

  /**
    * 创建一个从kafka获取数据的流.
    *
    * @param scc        spark streaming上下文
    * @param kafkaParam kafka相关配置
    * @param topics     需要消费的topic集合
    * @return
    */
  def createStream(scc: StreamingContext, kafkaParam: Map[String, String], topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](scc, kafkaParam, topics)
  }

}
