package com.sbr.visualization.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName DateUtil
 * @Description TODO 时间处理工具
 * @Author zxx
 * @Version 1.0
 */
public class DateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    /**
     * @param scope week七天  halfMonth半个月  month一个月 threeMonth三个月  halfYear半年  year一年
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Author zxx
     * @Description //TODO 获取不同范围时间
     * @Date 11:33 2020/7/1
     * @Param
     **/
    public static Map<String, String> getDateScope(String scope, Map<String, String> detail) {
        Map<String, String> dateMap = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String minDate = format.format(date);
        dateMap.put("minDate", minDate);

        Calendar c = Calendar.getInstance();
        switch (scope) {
            case "week"://七天
                c.setTime(new Date());
                c.add(Calendar.DATE, 7);
                Date d = c.getTime();
                String maxDate = format.format(d);
                dateMap.put("maxDate", maxDate);
                break;

            case "halfMonth"://半个月
                c.setTime(new Date());
                c.add(Calendar.DATE, 15);
                Date h = c.getTime();
                String halfMonth = format.format(h);
                dateMap.put("maxDate", halfMonth);
                break;

            case "month"://一个月
                c.setTime(new Date());
                c.add(Calendar.MONTH, 1);
                Date m = c.getTime();
                String mon = format.format(m);
                dateMap.put("maxDate", mon);
                break;

            case "threeMonth"://三个月
                c.setTime(new Date());
                c.add(Calendar.MONTH, 3);
                Date m3 = c.getTime();
                String mon3 = format.format(m3);
                dateMap.put("maxDate", mon3);
                break;

            case "halfYear"://半年
                c.setTime(new Date());
                c.add(Calendar.MONTH, 6);
                Date m6 = c.getTime();
                String mon6 = format.format(m6);
                dateMap.put("maxDate", mon6);
                break;

            case "year"://一年
                c.setTime(new Date());
                c.add(Calendar.YEAR, 1);
                Date y = c.getTime();
                String year = format.format(y);
                dateMap.put("maxDate", year);
                break;
            case "cus"://自定义
                dateMap = detail;
                break;

        }
        return dateMap;
    }


    /**
     * @param scope  10min 	10分钟前
     *               1h		1小时前
     *               3h		3小时前
     *               today	今天
     *               yesterday 昨天
     *               beforeyesterday前天
     * @param detail detail自定义
     * @param detail
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Author zxx
     * @Description //TODO
     * @Date 14:37 2020/7/1
     * @Param
     **/
    public static Map<String, String> getDateHHMMSSScope(String scope, Map<String, String> detail) {
        Map<String, String> dateMap = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        switch (scope) {
            case "10min"://十分钟前
                Date tenMinutes = new Date();
                c.setTime(tenMinutes);
                c.add(Calendar.MINUTE, -10);
                Date d = c.getTime();
                String min = format.format(d);
                dateMap.put("minDate", min);
                dateMap.put("maxDate", format.format(tenMinutes));
                break;

            case "1h"://一小时前
                Date oneHourDate = new Date();
                c.setTime(oneHourDate);
                c.add(Calendar.HOUR, -1);
                Date h = c.getTime();
                String halfMonth = format.format(h);
                dateMap.put("minDate", halfMonth);
                dateMap.put("maxDate", format.format(oneHourDate));
                break;

            case "3h"://三小时前
                Date hDate = new Date();
                c.setTime(hDate);
                c.add(Calendar.HOUR, -3);
                Date m = c.getTime();
                String mon = format.format(m);
                dateMap.put("minDate", mon);
                dateMap.put("maxDate", format.format(hDate));
                break;

            case "today"://今天
                buidDateScope(dateMap, 0);
                break;

            case "yesterday"://昨天
                buidDateScope(dateMap, -1);
                break;

            case "beforeyesterday"://前天
                buidDateScope(dateMap, -2);
                break;
            case "cus"://自定义
                dateMap = detail;
                break;

        }
        return dateMap;
    }

    /**
     * @param dateMap 结果
     * @param day     0 当天开始结束时间、1明天开始结果时间 -1 前天开始结束时间
     * @return void
     * @Author zxx
     * @Description //TODO 获取前后天的时间，返回、
     * @Date 15:34 2020/7/1
     **/
    private static void buidDateScope(Map<String, String> dateMap, int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, day);
        //一天的开始时间 yyyy:MM:dd 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dayStart = calendar.getTime();
        String startStr = format.format(dayStart);
        //一天的结束时间 yyyy:MM:dd 23:59:59
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date dayEnd = calendar.getTime();
        String endStr = format.format(dayEnd);
        dateMap.put("minDate", startStr);
        dateMap.put("maxDate", endStr);
    }


    /**
     * @param startTime 开始时间字符串
     * @param endTime   结束时间字符串
     * @return java.util.List<java.lang.String>
     * @Author zxx
     * @Description //TODO 获取时间集合
     * @Date 9:22 2020/6/6
     **/
    public static List<String> getDateListByDateStr(String startTime, String endTime) {
        List<String> result = new ArrayList<>();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(startTime);
            Date endDate = dateFormat.parse(endTime);
            result.add(dateFormat.format(startDate));//当前时间
            Calendar calBegin = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calBegin.setTime(startDate);
            Calendar calEnd = Calendar.getInstance();
            // 使用给定的 Date 设置此 Calendar 的时间
            calEnd.setTime(endDate);
            // 测试此日期是否在指定日期之后
            while (endDate.after(calBegin.getTime())) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                result.add(dateFormat.format(calBegin.getTime()));
            }
        } catch (ParseException e) {
            LOGGER.error("日期处理异常:", e);
        }
        return result;
    }


    /**
     * @param start 开始位置 -1 代表前一天 0代表当天 1 代表后一天
     * @param end   结束位置 -1 代表前一天 0代表当天 1 代表后一天
     * @return java.util.List<java.lang.String>
     * @Author zxx
     * @Description //TODO 获取时间范围集合字符串
     * @Date 9:22 2020/6/6
     **/
    public static List<String> getDateList(int start, int end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = getSomeDay(new Date(), start);//开始时间当天
        Date endDate = getSomeDay(new Date(), end);//结束时间
        List<String> result = new ArrayList<>();

        result.add(dateFormat.format(startDate));//当前时间
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(startDate);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(endDate);
        // 测试此日期是否在指定日期之后
        while (endDate.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            result.add(dateFormat.format(calBegin.getTime()));
        }
        return result;
    }

    /**
     * @param now   开始时间
     * @param start -1 代表前一天 0代表当天 1 代表后一天
     * @return java.util.Date
     * @Author zxx
     * @Description //TODO 获取指定时间
     * @Date 10:10 2020/6/8
     **/
    public static Date getSomeDay(Date now, int start) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, start);
        return calendar.getTime();
    }

}
