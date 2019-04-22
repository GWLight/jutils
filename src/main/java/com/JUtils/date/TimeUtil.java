package com.shhn.pmm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * Created by GuryY on 2016/04/21.
 */
public class TimeUtil {

    // 商户发送交易时间 格式:YYYYMMDDhhmmss
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * 获取时间差字符串
     *
     * @param date 指定时间
     *
     * @return 可读性时间差
     */
    public static String getTimeDiff(Date date) {
        if (date == null) return "";
        Date now = new Date();
        Long diff = now.getTime() - date.getTime();
        if (diff > 0) {
            if (diff > 1000L * 60 * 60 * 24 * 30) {
                return diff / (1000L * 60 * 60 * 24 * 30) + "个月前";
            } else if (diff > 1000L * 60 * 60 * 24) {
                return diff / (1000L * 60 * 60 * 24) + "天前";
            } else {
                return diff / (1000L * 60 * 60) + "小时前";
            }
        } else if (diff == 0) {
            return "刚刚";
        } else {
            diff = Math.abs(diff);
            if (diff > 1000L * 60 * 60 * 24 * 30) {
                return diff / (1000L * 60 * 60 * 24 * 30) + "个月后";
            } else if (diff > 1000L * 60 * 60 * 24) {
                return diff / (1000L * 60 * 60 * 24) + "天后";
            } else {
                return diff / (1000L * 60 * 60) + "小时后";
            }
        }
    }

    /**
     * 时间加若干天
     *
     * @param date 指定时间
     *
     * @return 可读性时间差
     */
    public static Date getNextDay(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +i);//+i今天的时间加一天
        date = calendar.getTime();
        return date;
    }
}
