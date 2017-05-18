package com.caiyi.nirvana.analyse.demo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期时间工具类
 *
 * @author Administrator
 */
public class DateUtils {

    public static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATEKEY_FORMAT =
            new SimpleDateFormat("yyyyMMdd");

    Date date = new Date();

    /**
     * 获取年月日和小时 小时Code
     *
     * @param date 时间（yyyy-MM-dd HH:mm:ss）
     * @return 结果（yyyyMMddHH）
     */
    public static String getDateHour(Date date) {
        String dateTime = TIME_FORMAT.format(date);

        String date1 = dateTime.split(" ")[0];
        String hourMinuteSecond = dateTime.split(" ")[1];
        String hour = hourMinuteSecond.split(":")[0];
        String time = date1 + "-" + hour;
        time = time.replaceAll("-", "");

        return time;
    }

    /**
     * 获取年月日 天的Code
     *
     * @param date
     * @return yyyyMMdd
     */
    public static String getDateDay(Date date) {
        return DATEKEY_FORMAT.format(date);
    }

    /**
     * 获取年月的Code
     *
     * @param date
     * @return yyyyMM
     */
    public static String getDateMonth(Date date) {
        String time = TIME_FORMAT.format(date);
        String year = time.split(" ")[0].split("-")[0];
        String month = time.split(" ")[0].split("-")[1];

        String dateMonth = year.concat(month);
        return dateMonth;
    }

    /**
     * 获取年的Code
     *
     * @param date
     * @return
     */
    public static String getDateYear(Date date) {
        String time = TIME_FORMAT.format(date);
        String year = time.split(" ")[0].split("-")[0];
        return year;
    }

    /**
     * 判断一个时间是否在另一个时间之前
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 判断结果
     */
    public static boolean before(String time1, String time2) {
        try {
            Date dateTime1 = TIME_FORMAT.parse(time1);
            Date dateTime2 = TIME_FORMAT.parse(time2);

            if (dateTime1.before(dateTime2)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断一个时间是否在另一个时间之后
     *
     * @param time1 第一个时间
     * @param time2 第二个时间
     * @return 判断结果
     */
    public static boolean after(String time1, String time2) {
        try {
            Date dateTime1 = TIME_FORMAT.parse(time1);
            Date dateTime2 = TIME_FORMAT.parse(time2);

            if (dateTime1.after(dateTime2)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算时间差值（单位为秒）
     *
     * @param time1 时间1
     * @param time2 时间2
     * @return 差值
     */
    public static int minus(String time1, String time2) {
        try {
            Date datetime1 = TIME_FORMAT.parse(time1);
            Date datetime2 = TIME_FORMAT.parse(time2);

            long millisecond = datetime1.getTime() - datetime2.getTime();

            return Integer.valueOf(String.valueOf(millisecond / 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当天日期（yyyy-MM-dd）
     *
     * @return 当天日期
     */
    public static String getTodayDate() {
        return DATE_FORMAT.format(new Date());
    }

    /**
     * 获取昨天的日期（yyyy-MM-dd）
     *
     * @return 昨天的日期
     */
    public static String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -1);

        Date date = cal.getTime();

        return DATE_FORMAT.format(date);
    }

    /**
     * 格式化日期（yyyy-MM-dd）
     *
     * @param date Date对象
     * @return 格式化后的日期
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * 格式化时间（yyyy-MM-dd HH:mm:ss）
     *
     * @param date Date对象
     * @return 格式化后的时间
     */
    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    /**
     * 解析时间字符串
     *
     * @param time 时间字符串
     * @return Date
     */
    public static Date parseTime(String time) {
        try {
            return TIME_FORMAT.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化日期key
     *
     * @param datekey
     * @return
     */
    public static Date parseDateKey(String datekey) {
        try {
            return DATEKEY_FORMAT.parse(datekey);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化时间，保留到分钟级别
     * yyyyMMddHHmm
     *
     * @param date
     * @return
     */
    public static String formatTimeMinute(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 获取当前时间的前一个小时
     *
     * @param date
     * @return
     */
    public static String getBeforeHourNow(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        //calendar.setMinimalDaysInFirstWeek(4);

        String beforeHourTime = df.format(calendar.getTime());

        return beforeHourTime;
    }

    /**
     * 获取当前时间的前一天
     *
     * @param date
     * @return
     */
    public static String getBeforeDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();

        String dayBefore = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(date);

        return dayBefore;
    }

    /**
     * 获取当前时间是今年的第几周
     *
     * @param date
     * @return
     */
    public static String getNowWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);

        String weekofYear = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));

        if (weekofYear.length() == 1) {
            weekofYear = "0" + weekofYear;
        }

        return weekofYear;
    }

    /*public static String getLastWeek(Date date, Integer num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, num);
        String result = df.format(calendar.getTime());

        return result;
    }*/

    /**
     * 获取当前时间是当年的第几个月
     *
     * @param date
     * @return
     */
    public static String getNowMonthOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String monthofYear = String.valueOf(calendar.get(Calendar.MONTH) + 1);

        if (monthofYear.length() == 1) {
            monthofYear = "0" + monthofYear;
        }

        return monthofYear;
    }

    /**
     * 获取当前时间所在周的时间范围
     *
     * @param date
     * @return
     */
    public static List<Date> getDateToWeek(Date date) {
        String yearCode = getDateYear(date);
        int b = date.getDay();
        if ("2017".equals(yearCode)) {
            b = b + 7;
        }

        Date fdate;
        List<Date> list = new ArrayList<Date>();

        Long fTime = date.getTime() - b * 24 * 3600000;
        for (int a = 1; a <= 7; a++) {
            fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            list.add(a - 1, fdate);
        }

        return list;
    }

    /**
     * 比较两个时间（毫秒）相差的毫秒数
     *
     * @param time1
     * @param time2
     * @return long
     * @author zrk
     */
    public static long compareTimeMillis(Long time1, Long time2) {
        return Math.abs(time2 - time1);
    }


}
