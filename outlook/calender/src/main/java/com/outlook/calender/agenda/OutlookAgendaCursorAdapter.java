package com.outlook.calender.agenda;

import android.content.Context;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;

import com.outlook.calender.OutlookCalendarEventQueryHandler;

/**
 * Created by ksachan on 7/6/17.
 */

public class OutlookAgendaCursorAdapter extends OutlookAgendaAdapter
{
	public OutlookAgendaCursorAdapter(Context context)
	{
		super(context);
		mHandler = new OutlookCalendarEventQueryHandler(context.getContentResolver(), this);
	}
	
	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView)
	{
		deactivate();
	}
	
	@Override
	protected void loadEvents(long timeMillis)
	{
		mHandler.startQuery(0, timeMillis, CalendarContract.Events.CONTENT_URI, EVENTS_PROJECTION,
				"("
				+ CalendarContract.Events.DTSTART + ">=? AND "
				+ CalendarContract.Events.DTSTART + "<?) OR ("
				+ CalendarContract.Events.DTSTART + "<? AND "
				+ CalendarContract.Events.DTEND + ">=?) AND "
				+ CalendarContract.Events.DELETED + "=?",
				new String[]{String.valueOf(timeMillis), String.valueOf(timeMillis + DateUtils.DAY_IN_MILLIS)
						 , String.valueOf(
						timeMillis), String.valueOf(timeMillis + DateUtils.DAY_IN_MILLIS), "0"},
				CalendarContract.Events.DTSTART + " ASC");
	}
	
	private static final String[] EVENTS_PROJECTION = new String[]{
			CalendarContract.Events._ID,
			CalendarContract.Events.CALENDAR_ID,
			CalendarContract.Events.DTSTART,
			CalendarContract.Events.DTEND,
			CalendarContract.Events.TITLE
	};
	
	private final OutlookCalendarEventQueryHandler mHandler;
}
