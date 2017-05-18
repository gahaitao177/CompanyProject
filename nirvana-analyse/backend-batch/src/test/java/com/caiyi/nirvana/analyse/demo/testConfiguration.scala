package com.caiyi.nirvana.analyse.demo

import com.caiyi.nirvana.analyse.conf.ConfigurationManager
import com.caiyi.nirvana.analyse.count.canstant.Constants

/**
 * Created by root on 2017/2/17.
 */
object testConfiguration {
  def main(args: Array[String]) {
    val points = ConfigurationManager.getProperty(Constants.CASSANDRA_CANTRACT_POINTS)

    val aa = ConfigurationManager.getInteger(Constants.CASSANDRA_CLUSTER_PORT)

    println("----------" + points + "     ===" + aa)
  }
}
