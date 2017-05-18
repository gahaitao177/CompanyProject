package com.caiyi.nirvana.analyse.conf

import java.io.InputStream
import java.util.Properties

/**
 * 1、配置管理组件可以复杂，也可以很简单，对于简单的配置管理组件来说，只要开发一个类，可以在第一次访问它的
 * 时候，就从对应的properties文件中，读取配置项，并提供外界获取某个配置key对应的value的方法
 * 2、如果是特别复杂的配置管理组件，那么可能需要使用一些软件设计中的设计模式，比如单例模式、解释器模式
 * 可能需要管理多个不同的properties，甚至是xml类型的配置文件
 * Created by Socean on 2016/11/30.
 */
object ConfigurationManager {
  private val prop: Properties = new Properties

  try {
    // 通过一个“类名.class”的方式，就可以获取到这个类在JVM中对应的Class对象
    // 然后再通过这个Class对象的getClassLoader()方法，就可以获取到当初加载这个类的JVM
    // 中的类加载器（ClassLoader），然后调用ClassLoader的getResourceAsStream()这个方法
    // 就可以用类加载器，去加载类加载路径中的指定的文件
    // 最终可以获取到一个，针对指定文件的输入流（InputStream）
    val in: InputStream = ConfigurationManager.getClass.getClassLoader.getResourceAsStream("cassandra.properties")

    // 调用Properties的load()方法，给它传入一个文件的InputStream输入流
    // 即可将文件中的符合“key=value”格式的配置项，都加载到Properties对象中
    // 加载过后，此时，Properties对象中就有了配置文件中所有的key-value对了
    // 然后外界其实就可以通过Properties对象获取指定key对应的value
    prop.load(in)
  }
  catch {
    case e: Exception => {
      e.printStackTrace
    }
  }

  /**
   * 获取指定key对应的value
   *
   * @param key
   * @return value
   */
  def getProperty(key: String): String = {
    return prop.getProperty(key)
  }

  /**
   * 获取整数类型的配置项
   *
   * @param key
   * @return value
   */
  def getInteger(key: String): Integer = {
    val value: String = getProperty(key)
    try {
      return Integer.valueOf(value)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    return 0
  }

  /**
   * 获取boolean类型的配置项
   * @param key
   * @return value
   */
  def getBoolean(key: String): Boolean = {
    val value: String = getProperty(key)
    try {
      return value.toBoolean
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    return false
  }

  /**
   * 获取Long类型的配置项
   * @param key
   * @return
   */
  def getLong(key: String): Long = {
    val value: String = getProperty(key)
    try {
      return value.toLong
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    return 0L
  }
}