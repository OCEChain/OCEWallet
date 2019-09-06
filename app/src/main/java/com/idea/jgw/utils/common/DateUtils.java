package com.idea.jgw.utils.common;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by phper on 2016/6/1.
 */
public class DateUtils {
    public static final String DATE_STYLE1 = "yyyy-MM-dd";
    public static final String DATE_STYLE2 = "yyyy/MM/dd";
    public static final String DATE_STYLE3 = "yyyy年MM月dd日";
    public static final String DATE_STYLE4 = "yyyyMMdd";

    public static final String TIME_STYLE1 = "HH:mm:ss";
    public static final String TIME_STYLE2 = "HHmmss";
    public static final String TIME_STYLE3 = "HH时mm分ss秒";

    public static final String DTIME_STYLE1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DTIME_STYLE2 = "yyyyMMdd HHmmss";
    public static final String DTIME_STYLE3 = "yyyy/MM/dd HH:mm:ss";
    public static final String DTIME_STYLE4 = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String DTIME_STYLE5 = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DTIME_STYLE6 = "yyyy年MM月dd日 HH:mm";
    public static final String DTIME_STYLE7 = "yyyyMMddHHmmss";
    public static final String DTIME_STYLE8 = "yyyyMMddHH:mm:ss:SSS";

    /**
     * 将Calendar转换成String
     *
     * @param calendar
     * @param styel
     * @return
     */
    public static String calendarToString(Calendar calendar, String styel)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(styel);
        String dateStr = sdf.format(calendar.getTime());
        return dateStr;
    }

    /**
     * 将String转换成 Calendar
     *
     * @param dateStr
     * @param styel
     * @return
     */
    public static Calendar stringToCalendar(String dateStr, String styel)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(styel);
        Date date = null;
        Calendar calendar = null;
        try
        {
            date = sdf.parse(dateStr);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        } catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return calendar;
    }/**
     * 将日期转换成标准日期格式
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String stringToDateString(String dateStr)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DTIME_STYLE1);
        Date date = null;
        try
        {
            date = formatter.parse(dateStr);
        } catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将Date转换成String
     *
     * @param date
     * @param styel
     * @return
     */
    public static String dateToString(Date date, String styel)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(styel);
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * 时间戳转为时间字符串
     * @param time 时间戳(单位秒)
     * @param style 字符串格式
     * @return 时间字符串
     */
    public static String longToString(long time, String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        sdf.setTimeZone(TimeZone.getDefault());
        String timeStr = sdf.format(new Date(time));
        return timeStr;
    }

    /**
     * 时间戳转为Date
     * @param time 时间戳
     * @param style Date格式
     * @return Date
     */
    public static Date longToDate(long time, String style) {
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        sdf.setTimeZone(TimeZone.getDefault());
        String timeStr = sdf.format(time);
        Date date = null;
        try {
            date = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将String转换成Date
     *
     * @param dateStr
     * @param styel
     * @return
     */
    public static Date stringToDate(String dateStr, String styel)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(styel);
        Date date = null;
        try
        {
            date = sdf.parse(dateStr);
        } catch (ParseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Date转化Calendar
     *
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Calendar转化Date
     *
     * @param calendar
     * @return
     */
    public static Date calendarToDate(Calendar calendar)
    {
        return calendar.getTime();
    }

    /**
     * String 转成 Timestamp
     *
     * @param timeStr
     * @return
     */
    public static Timestamp stringToTimestamp(String timeStr)
    {
        Timestamp ts = Timestamp.valueOf(timeStr);
        return ts;
    }

    /**
     * Date 转 TimeStamp
     *
     * @param date
     * @param styel
     * @return
     */
    public static Timestamp dateToTimestamp(Date date, String styel)
    {
        SimpleDateFormat df = new SimpleDateFormat(styel);
        String time = df.format(date);
        Timestamp ts = Timestamp.valueOf(time);
        return ts;
    }

    /**
     * 获取现在时间
     *
     * @return返回短时间格式 yyyy-MM-dd
     */
    public static Date getNowDateShort()
    {
        Date date = strToDate(getStringDateShort());
        return date;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate()
    {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DTIME_STYLE1);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy/MM/dd HH:mm:ss
     */
    public static String getStringDateStyle3()
    {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DTIME_STYLE3);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort()
    {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_STYLE1);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyyMMdd
     */
    public static String getDateShort(String dateStyle)
    {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateStyle);
        String dateStr = formatter.format(currentTime);
        return dateStr;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyyMMdd
     */
    public static String getDateShort()
    {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_STYLE4);
        String dateStr = formatter.format(currentTime);
        return dateStr;
    }/**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_STYLE1);
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DTIME_STYLE1);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DTIME_STYLE1);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStr(Date dateDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_STYLE1);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @param format
     * @return
     */
    public static String dateToStr(Date dateDate, String format)
    {
        if (dateDate == null)
        {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_STYLE1);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * <p>将短时间格式字符串转换为时间 yyyy-MM-dd</p>
     *
     * @param strDate 要转换的字符串
     * @param format 要转行的格式
     * @return 指定格式的date
     */
    public static Date strToDateWithFormatString(String strDate, String format)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
            ParsePosition pos = new ParsePosition(0);
            return formatter.parse(strDate, pos);
        } catch (Exception e)
        {
            // TODO: handle exception
            return null;
        }

    }

//    /**
//     * 得到现在日期时间
//     *
//     * @return
//     */
//    public static Date getNow()
//    {
//        Date currentTime = new Date();
//        return currentTime;
//    }

    /**
     * 获取当前日期(yyyy-MM-dd)
     * @return 放回指定格式的时间字符串
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNowDate(String format)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        calendar.setTimeInMillis(System.currentTimeMillis());
        return sdf.format(calendar.getTime());
    }

    /**
     * <p>得到现在时间 HH:mm:ss</p>
     *
     * @return 放回指定格式的时间
     */
    public static Date getNowTime()
    {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_STYLE1, Locale.CHINA);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(getTimeShort(), pos);
    }

    /**
     * 返回两个字符串时间相差的天数
     * @param begin 开始的字符串时间
     * @param end 结束的字符串时间
     * @return 天数
     */
    public static long getDayFromTwoDate(String begin, String end) {
        Date beginDate = strToDate(begin);
        Date endDate = strToDate(end);
        return (endDate.getTime() - beginDate.getTime())/(1000 * 60 * 60 * 24);
    }
}
