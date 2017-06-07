package com.yy.ana.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间格式化工具类
 *
 * @author sunqm
 */
public class DateUtil {
    public static final SimpleDateFormat YMD_HMSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat YMD_SDF = new SimpleDateFormat("yyyyMMdd");
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_STR_FORMAT = "yyyyMMdd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_STR_FORMAT = "yyyyMMddHHmmss";
    public static final String DATE_YM_FORMAT = "yyyy-MM";
    public static final String DATE_YM_STR_FORMAT = "yyyyMM";

    /**
     * 将日期转换为指定格式的日期字符串
     *
     * @param date   日期
     * @param format 格式化模型,如："yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     * @author sjq
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(format);
        String dateString = df.format(date);
        return dateString;
    }

    public static Date parseString2Date(String str, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(str);
    }

    public static Date parseString2Date(String str) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.parse(str);
    }

    /**
     * 将日期字符串转换成指定格式的日期
     *
     * @param dateString 日期字符串
     * @param format     格式化模型,如："yyyy-MM-dd HH:mm:ss"
     * @return date            指定格式的日期
     * @author sjq
     */
    /*public static Date parseDate(String dateString, String format) {
        Date date = null;
        if (!StringUtils.isEmpty(dateString)) {
            try {
                DateFormat df = new SimpleDateFormat(format);
                date = df.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }*/

    /**
     * 获取指定日期前n天的日期
     *
     * @param date   日期参数
     * @param amount 天数
     * @return 指定日期前n天的日期
     * @author sjq
     */
    public static Date getLastDay(Date date, int amount) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, amount * -1);
            return cal.getTime();
        }
        return null;
    }

    /**
     * 获取两个日期间相差的天数
     *
     * @param calendar1 日期1
     * @param calendar2 日期2
     * @return 两个日期间相差的天数
     * @author sjq
     */
    public static int getDaysBetween(Calendar calendar1, Calendar calendar2) {
        if (calendar1.after(calendar2)) {
            Calendar calendar = calendar1;
            calendar1 = calendar2;
            calendar2 = calendar;
        }

        int days = calendar2.get(6) - calendar1.get(6) + 1;
        int y2 = calendar2.get(1);

        if (calendar1.get(1) != y2) {
            calendar1 = (Calendar) calendar1.clone();
            do {
                days += calendar1.getActualMaximum(6);
                calendar1.add(1, 1);
            } while (calendar1.get(1) != y2);
        }

        return days;
    }

    /**
     * 获取指定日期的上几周周一的日期(默认为上周)
     *
     * @param current 当前日期
     * @param num     上num周的变量值,例如num为2,则表示上2周周一所在的日期
     * @return Calendar    当前日期的上周一的日期,Calendar格式
     * @author sjq
     */
    public static Calendar getLastWeekStartDate(Calendar current, int num) {
        num = num <= 0 ? 1 : num;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(current.getTime());
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        calendar.add(Calendar.DATE, -(dayofweek + 7 * num));
        return calendar;
    }

    /**
     * 获取指定日期的下几周周一的日期(默认为下周)
     *
     * @param current 当前日期
     * @param num     上num周的变量值,例如num为2,则表示下2周周一所在的日期
     * @return Calendar    当前日期的上周一的日期,Calendar格式
     * @author sjq
     */
    public static Calendar getNextWeekStartDate(Calendar current, int num) {
        num = num <= 0 ? 1 : num;
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(current.getTime());
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, (dayofweek - 1 + 7 * (num - 1)));
        return calendar;
    }


    /**
     * 获取自然日后的日期
     *
     * @param beginDate   开始日期
     * @param naturalDays 自然日天数
     * @return 计算结果
     */
    public static Date addWithNaturalDays(Date beginDate, int naturalDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(beginDate);
        c.add(Calendar.DATE, naturalDays);
        return c.getTime();
    }

}
