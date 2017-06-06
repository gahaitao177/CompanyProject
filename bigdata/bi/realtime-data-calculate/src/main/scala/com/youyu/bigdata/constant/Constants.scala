package com.youyu.bigdata.constant

/**
  * Created by Socean on 2017/05/19.
  */
object Constants {
  /**
    * Spark相关配置
    */
  val SPARK_APP_NAME: String = "AppKafkaSparkStats"
  /**
    * Kafka相关配置
    */
  val KAFKA_LIST: String = "kafka.list"
  val KAFKA_GROUP_ID: String = "app_youyu_data_stats"

  /**
    * Table相关的配置
    */
  val HBASE_APP_DATA_DAILY: String = "hbase.app.data.daily"

  val HBASE_APP_NEW_USER: String = "hbase.app.new.user"

  val HBASE_APP_PKG_DIC: String = "hbase.app.pkg.dic"
}