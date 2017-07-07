package com.outlook.calendar.calendar;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.outlook.calendar.event.OutlookMonthEventQueryHanlder;
import com.outlook.calendar.utils.OutlookCalendarUtils;

/**
 * Created by ksachan on 7/7/17.
 */

/**
 * Adapter class for loading and binding calendar events asynchronously
 */
public class OutlookCalendarCursorAdapter
{
	public OutlookCalendarCursorAdapter(Context context)
	{
		mHandler = new OutlookMonthEventQueryHanlder(context.getContentResolver(), this);
	}
	
	
	void setCalendarView(OutlookCalendarViewPager calendarView)
	{
		mCalendarView = calendarView;
	}
	
	/**
	 * Loads events for given month. Should call {@link #bindEvents(long, Cursor)} on complete
	 *
	 * @param monthMillis month in milliseconds
	 * @see {@link #bindEvents(long, Cursor)}
	 */
	protected void loadEvents(long monthMillis)
	{
		long startTimeMillis = OutlookCalendarUtils.monthFirstDay(monthMillis),
				endTimeMillis = startTimeMillis + DateUtils.DAY_IN_MILLIS *
												  OutlookCalendarUtils.monthSize(monthMillis);
		mHandler.startQuery(monthMillis, startTimeMillis, endTimeMillis);
	}
	
	/**
	 * Binds events for given month that have been loaded via {@link #loadEvents(long)}
	 *
	 * @param monthMillis month in milliseconds
	 * @param cursor      {@link android.provider.CalendarContract.Events} cursor
	 */
	public final void bindEvents(long monthMillis, Cursor cursor)
	{
		mCalendarView.swapCursor(monthMillis, cursor);
	}
	
	private OutlookCalendarViewPager      mCalendarView;
	private OutlookMonthEventQueryHanlder mHandler;
}
