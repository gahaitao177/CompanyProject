package com.caiyi.nirvana.analyse.count.conf

import java.io.{FileNotFoundException, InputStream}
import java.util.Properties

/**
  * 配置管理组件可以复杂，也可以很简单，对于简单的配置管理组件来说，只要开发一个类，可以在第一次访问它的
  * 时候，就从对应的properties文件中，读取配置项，并提供外界获取某个配置key对应的value的方法
  *
  * Created by Socean on 2017/2/17.
  */
object ConfigurationManager {

  // Properties对象使用private来修饰，就代表了其是类私有的
  // 那么外界的代码，就不能直接通过ConfigurationManager.prop这种方式获取到Properties对象
  // 之所以这么做，是为了避免外界的代码不小心错误的更新了Properties中某个key对应的value

  private val prop: Properties = new Properties()

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
  } catch {
    case ex: FileNotFoundException => {
      println("Missing file exception")
    }
  }

  /**
    * 获取指定key对应的value
    *
    * @param key
    * @return
    */
  def getProperty(key: String): String = {
    prop.getProperty(key)
  }

  /**
    * 获取整数类型的配置项
    *
    * @param key
    * @return
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
    *
    * @param key
    * @return
    */
  def getBoolean(key: String): Boolean = {
    val value: String = getProperty(key)

    try {
      return value.toBoolean
    } catch {
      case e: Exception => {
        e.printStackTrace
      }
    }

    return false
  }

  /**
    * 获取Long类型的配置项
    *
    * @param key
    * @return
    */
  def getLong(key: String): Long = {
    val value: String = getProperty(key)

    try {
      return value.toLong
    } catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    0L
  }

}
