package com.frio.tools.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public abstract class DateUtil {
    public static final String PATTERN_YEAR = "yyyy";
    public static final String PATTERN_YEAR2MONTH = "yyyyMM";
    public static final String PATTERN_YEAR2DAY = "yyyyMMdd";
    public static final String PATTERN_YEAR2_DAY = "yyyy-MM-dd";
    public static final String PATTERN_YEAR2SECOND = "yyyyMMddHHmmss";
    public static final String PATTERN_YEAR2MILLISECOND = "yyyyMMddHHmmssSSS";
    public static final String PATTERN_NORMAL_YEAR2SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_NORMAL_YEAR2MILLISECOND = "yyyy-MM-dd HH:mm:ss.SSS";

    protected static ThreadLocal<Map<String, DateFormat>> formatLocal = new ThreadLocal<Map<String, DateFormat>>();

    /**
     * 获取pattern样式的DateFormat 线程安全
     *
     * @param pattern
     * @return
     */
    protected static DateFormat getFormat(String pattern) {
        Map<String, DateFormat> formatMap = formatLocal.get();

        if (formatMap == null) {
            formatMap = new HashMap<String, DateFormat>();
            formatLocal.set(formatMap);
        }

        DateFormat format = formatMap.get(pattern);

        if (format == null) {
            format = new SimpleDateFormat(pattern);
            formatMap.put(pattern, format);
        }
        return format;
    }

    /**
     * 从long型毫秒数获取pattern样式日期字符串
     *
     * @param pattern
     * @return
     */
    public static String getFormat(Long milliseconds, String pattern) {

        Date dat = new Date(milliseconds);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String st = format.format(gc.getTime());
        return st;
    }

    /**
     * 校验
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static boolean valid(String dateStr, String pattern) {
        try {
            getFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * 解析日期字符串
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null) {
            return null;
        }
        try {
            return getFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 格式化
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return getFormat(pattern).format(date);
    }

    /**
     * 将时间转换成Long型
     *
     * @param date
     * @return
     */
    public static Long parseToLong(Date date, String pattern) {
        return Long.valueOf(format(date, pattern));
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    public static Long nowInMilliseconds() {
        return System.currentTimeMillis();
    }

    public static Integer nowInSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 获取一天个开始时间戳
     *
     * @return
     */
    public static Long getBeginDateOfDay(Date date) {
        String dateStr = DateUtil.format(date, DateUtil.PATTERN_YEAR2DAY);
        Date targetDate = DateUtil.parse(dateStr, DateUtil.PATTERN_YEAR2DAY);
        return targetDate.getTime();
    }

    /**
     * 获取下一天个开始时间戳
     *
     * @return
     */
    public static Long getBeginDateOfNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return getBeginDateOfDay(calendar.getTime());
    }

    public static Date getBeforeDays(Date date, int interval) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = simpleDateFormat.format(date);
        Date resultDate = null;
        try {
            Date newDate = simpleDateFormat.parse(str);
            resultDate = new Date(newDate.getTime() - 3600 * 1000 * 24 * interval);
        } catch (ParseException e) {
        }
        System.out.println("resultDate:" + simpleDateFormat.format(resultDate));
        return resultDate;
    }

    /**
     * 根据生日毫秒数获取年龄
     *
     * @return
     */
    public static Integer getAge(Long birthmilliseconds) {
        Integer birth = Integer.parseInt(getFormat(birthmilliseconds, PATTERN_YEAR));
        Integer now = Integer.parseInt(getFormat(System.currentTimeMillis(), PATTERN_YEAR));
        return now - birth;
    }

    /**
     * 获取间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int calculateNumberOfWeekendsInRange(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int counter = 0;
        while (!calendar.getTime().after(endDate)) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 1 || dayOfWeek == 7) {
                counter++;
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return counter;
    }

    /**
     * 获取间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int calculateNumberDays(Date startDate, Date endDate) {
        return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 磨平Date,变为00:00:00
     *
     * @return
     */
    public static Date flatDate(Date d) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sf.format(d);
        try {
            return sf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException();
        }
    }

    /**
     * 将时间转化为一天中的最后一秒
     * eg:
     * 2016-08-16 00:00:00 => 2016-08-16 23:59:59
     *
     * @param d
     * @return
     */
    public static Date flatEndDate(Date d) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sf.format(d) + " 23:59:59";
        try {
            return sf1.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException();
        }
    }
}