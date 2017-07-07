package com.outlook.calender.event;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.provider.CalendarContract;

/**
 * Created by ksachan on 7/7/17.
 */


public abstract class OutlookEventQueryHandler
		extends AsyncQueryHandler
{
	
	public OutlookEventQueryHandler(ContentResolver cr)
	{
		super(cr);
	}
	
	public final void startQuery(Object cookie, long startTimeMillis, long endTimeMillis)
	{
		startQuery(0, cookie, CalendarContract.Events.CONTENT_URI, EVENTS_PROJECTION,
				"(" + CalendarContract.Events.DTSTART + ">=? AND " + CalendarContract.Events.DTSTART + "<?) OR (" + CalendarContract.Events.DTSTART + "<? AND " + CalendarContract.Events.DTEND + ">=?) AND " + CalendarContract.Events.DELETED + "=?",
				new String[]{String.valueOf(startTimeMillis), String.valueOf(endTimeMillis), String.valueOf(startTimeMillis), String.valueOf(
						endTimeMillis), "0"}, CalendarContract.Events.DTSTART + " ASC");
	}
	
	private static final String[] EVENTS_PROJECTION = new String[]{
			CalendarContract.Events._ID,
			CalendarContract.Events.CALENDAR_ID,
			CalendarContract.Events.DTSTART,
			CalendarContract.Events.DTEND,
			CalendarContract.Events.ALL_DAY,
			CalendarContract.Events.TITLE};
}
