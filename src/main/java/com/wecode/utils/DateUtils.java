package com.wecode.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间日期工具
 * @author wecode
 */
public class DateUtils {

    private static final String FORMAT_LONG_DATE_TIME = "yyyyMMddHHmmss";

    /**
     * 返回长整型格式的日期时间
     */
    public static Long getLongDateTime() {
        Date date = new Date();
        return Long.parseLong(formatDate(date, FORMAT_LONG_DATE_TIME));
    }

    /**
     * 根据所有时间与格式，格式化时间
     * @param d 日期类型日期
     * @param format 日期格式
     */
    public static String formatDate(Date d, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(d);
    }
}
