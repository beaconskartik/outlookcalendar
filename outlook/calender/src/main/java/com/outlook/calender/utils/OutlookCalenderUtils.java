package com.outlook.calender.utils;

import java.util.Calendar;

import android.content.Context;
import android.text.format.DateUtils;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookCalenderUtils
{
    public static String toDayString(Context context, long timeMillis)
    {
        return DateUtils.formatDateTime(context, timeMillis, DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR);
    }
    
    public static String toMonthString(Context context, long timeMillis)
    {
        return DateUtils.formatDateRange(context, timeMillis, timeMillis,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR);
    }
}



