package com.caiyi.nirvana.analyse.demo

import java.text.{DecimalFormat, Format, SimpleDateFormat}
import java.util
import java.util.{Calendar, Date}

/**
 * Created by Socean on 2017/2/13.
 */
object DateUtils2 {
  val TIME_FORMAT: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val DATE_FORMAT: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val DATEKEY_FORMAT: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")

  /**
   * 获取年月日和小时 小时Code
   *
   * @param date  Date对象
   * @return （yyyyMMddHH）
   */
  def getDateHourCode(date: Date): String = {
    val dateTime = TIME_FORMAT.format(date)

    val date1 = dateTime.split(" ")(0)
    val hourMinuteSecond = dateTime.split(" ")(1)
    val hour = hourMinuteSecond.split(":")(0)

    var time = date1 + "-" + hour
    time = time.replaceAll("-", "")

    time
  }

  /**
   * 获取年月日和小时
   *
   * @param date Date对象
   * @return
   */
  def getDateHour(date: Date): String = {
    val dateTime = TIME_FORMAT.format(date)

    dateTime.substring(0, 14)
  }

  /**
   * 获取年月日
   *
   * @param date  Date对象
   * @return （yyyy-MM-dd）
   */
  def getDateDay(date: Date): String = {
    DATE_FORMAT.format(date)
  }

  /**
   * 获取年月日的Code
   *
   * @param date
   * @return
   */
  def getDateDayCode(date: Date): String = {
    DATEKEY_FORMAT.format(date)
  }

  /**
   * 获取年月的Code
   *
   * @param date  Date对象
   * @return （yyyyMM）
   */
  def getDateMonthCode(date: Date): String = {
    val time = TIME_FORMAT.format(date)
    val year = time.split(" ")(0).split("-")(0)
    val month = time.split(" ")(0).split("-")(1)

    val yearAndMonth = year.concat(month)

    yearAndMonth
  }

  /**
   * 获取年的Code
   *
   * @param date  Date对象
   * @return （yyyy）
   */
  def getDateYearCode(date: Date): String = {
    val time = TIME_FORMAT.format(date)
    val year = time.split(" ")(0).split("-")(0)

    year
  }

  /**
   * 获取当前时间前一小时
   *
   * @param date Date对象
   * @return
   */
  def getBeforeHourNow(date: Date): String = {
    val calender: Calendar = Calendar.getInstance()

    calender.setTime(date)
    calender.set(Calendar.HOUR_OF_DAY, calender.get(Calendar.HOUR_OF_DAY) - 1)

    val beforeHourTime = TIME_FORMAT.format(calender.getTime)

    beforeHourTime
  }

  /**
   * 获取当前时间前 num天的时间
   *
   * @param date
   * @param num
   * @return
   */
  def getBeforeNumDay(date: Date, num: Int): Date = {
    val calendar: Calendar = Calendar.getInstance()

    calendar.setTime(date)
    calendar.add(Calendar.DAY_OF_MONTH, num)
    val dateTime = calendar.getTime

    val beforeDay = TIME_FORMAT.format(dateTime)

    TIME_FORMAT.parse(beforeDay)
  }

  /**
   * 获取当前时间是今年的第几周Code
   *
   * @param date
   * @return
   */
  def getNowWeekOfYearCode(date: Date): String = {
    val calendar: Calendar = Calendar.getInstance()

    calendar.setTime(date)
    calendar.setFirstDayOfWeek(Calendar.MONDAY)
    calendar.setMinimalDaysInFirstWeek(4)

    var weekofYear = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));

    if (weekofYear.length() == 1) {
      weekofYear = "0" + weekofYear;
    }

    weekofYear
  }

  /**
   * 获取当前时间是今年的第几周
   *
   * @param date
   * @return
   */
  def getNowWeekOfYear(date: Date): String = {
    val calendar: Calendar = Calendar.getInstance()

    calendar.setTime(date)
    calendar.setFirstDayOfWeek(Calendar.MONDAY)
    calendar.setMinimalDaysInFirstWeek(4)

    val weekOfYear = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR))

    weekOfYear
  }

  /**
   * 获取当前周所在的时间范围
   *
   * @param date
   * @return
   */
  def getDateToWeek(date: Date): util.ArrayList[Date] = {
    val yearCode = getDateYearCode(date)

    var b = date.getDay
    if ("2017".equals(yearCode)) {
      b += 7
    }
    var fdate: Date = null

    val list = new util.ArrayList[Date]

    val fTime: Long = date.getTime - b * 24 * 3600000
    for (a <- 1 to 7) {
      fdate = new Date()
      fdate.setTime(fTime + (a * 24 * 3600000))

      list.add(a - 1, fdate)
    }

    list
  }

  /**
   * 解析时间字符串
   *
   * @param time 时间字符串
   * @return Date
   */
  def parseTime(time : String): Date ={
    TIME_FORMAT.parse(time)
  }

  /**
   * 获取当前时间所属的年 月 周 日 时
   *
   * @param date
   * @return
   */
  def splitDate(date: Date): Map[String, Any] = {
    val dateStr = TIME_FORMAT.format(date)

    val year = dateStr.substring(0, 4)
    val month = dateStr.substring(5, 7)
    val day = dateStr.substring(8, 10)
    val hour = dateStr.substring(11, 13)

    val calendar: Calendar = Calendar.getInstance()
    calendar.setTime(date)
    calendar.setFirstDayOfWeek(Calendar.MONDAY)
    calendar.setMinimalDaysInFirstWeek(4)

    val week = calendar.get(Calendar.WEEK_OF_YEAR)
    val f: Format = new DecimalFormat("00")

    val dateMap = Map("year" -> year, "month" -> month, "day" -> day, "hour" -> hour, "week" -> f.format(week))

    dateMap
  }

  /**
   * 比较两个时间相差的毫秒数
   *
   * @param date1
   * @param date2
   * @return
   */
  def compareTimeMillis(date1: Long, date2: Long): Long = {

    val diffTime = Math.abs(date2 - date1)

    diffTime
  }

}
