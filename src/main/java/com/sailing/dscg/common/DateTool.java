package com.sailing.dscg.common;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/**
 * @Description:
 * @Auther:史俊华
 * @Date:2018/7/1715
 */
public class DateTool {

    public static String DateFormat_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当前时间
     *
     * @return 毫秒
     */
    public static Long getCurrentLongTime() {
        return new Date().getTime();
    }

    /**
     * 获取转换后的MongoDB库的当前时间
     *
     * @return
     */
    public static Date getMongoDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        format.setCalendar(new GregorianCalendar(new SimpleTimeZone(0, "GMT")));
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * @param dateStr 日期字符串
     * @param formart 日期格式 默认为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long StringToDateLong(String dateStr, String formart) {
        if (StringUtils.isBlank(formart)) formart = "yyyy-MM-dd HH:mm:ss";
        Date date = StringToDate(dateStr, formart);
        return date == null ? 0l : date.getTime();
    }

    /**
     * 字符串转Date
     *
     * @param date 日期字符串 格式为yyyy-MM-dd HH:mm:ss
     * @return 日期
     */
    public static Date StringToDate(String date, String formart) {
        if (StringUtils.isBlank(formart)) formart = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转Date
     *
     * @param date 日期字符串 格式为yyyy-MM-dd HH:mm:ss
     * @return 日期
     */
    public static String Date2String(Date date, String formart) {
        if (StringUtils.isBlank(formart)) formart = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        return sdf.format(date);
    }

    /**
     * 获取当前时间字符串
     *
     * @param format 字符串格式
     * @return
     */
    public static String getCurrDateString(String format) {
        format = StringUtils.isBlank(format) ? "yyyy-MM-dd HH:mm:ss" : format;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前时间字符串 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrDateString() {
        return getCurrDateString("");
    }

    public static String getDateToString(Date date, String format) {
        format = StringUtils.isBlank(format) ? "yyyy-MM-dd HH:mm:ss" : format;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    //获取每周的第一天
    public static String getFirstOfWeek(String dataStr) throws ParseException {
        Calendar cal = Calendar.getInstance();

        cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dataStr));

        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        // 所在周开始日期
        String data1 = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        // 所在周结束日期
        //String data2 = new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
        return data1;

    }

    //获取传入日期所在月的第一天
    public static Date getFirstDayDateOfMonth(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int last = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
        return cal.getTime();
    }

    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String dateToWeek(Date datetime) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        try {
            cal.setTime(datetime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 字符串转Date
     *
     * @param date 日期字符串 格式为yyyy-MM-dd HH:mm:ss
     * @return 日期
     */
    public static String dateToString(Date date, String formart) {
        if (StringUtils.isBlank(formart)) formart = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(formart);
        return sdf.format(date);
    }


    public static void main(String[] args) {
        String curDate = "127.0.0.1:11";
        System.out.println(curDate.substring(0,curDate.indexOf(":")));
    }
}
