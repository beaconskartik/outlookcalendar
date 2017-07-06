package com.outlook.calender.utils;

import java.util.Calendar;

import android.content.Context;
import android.text.format.DateUtils;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookCalenderUtils
{
    
    public static Calendar stripTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
    
    
    public static String toDayString(Context context, long timeMillis)
    {
        return DateUtils.formatDateTime(context, timeMillis, DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR);
    }
    
    public static String toMonthString(Context context, long timeMillis)
    {
        return DateUtils.formatDateRange(context, timeMillis, timeMillis,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR);
    }
    
    public static boolean monthBefore(Calendar calendar, Calendar other)
    {
        int day = other.get(Calendar.DAY_OF_MONTH);
        other.set(Calendar.DAY_OF_MONTH, 1);
        boolean before = calendar.getTimeInMillis() < other.getTimeInMillis();
        other.set(Calendar.DAY_OF_MONTH, day);
        return before;
    }
    
    public static boolean monthAfter(Calendar calendar, Calendar other)
    {
        int day = other.get(Calendar.DAY_OF_MONTH);
        other.set(Calendar.DAY_OF_MONTH, other.getActualMaximum(Calendar.DAY_OF_MONTH));
        boolean after = calendar.getTimeInMillis() > other.getTimeInMillis();
        other.set(Calendar.DAY_OF_MONTH, day);
        return after;
    }
}



