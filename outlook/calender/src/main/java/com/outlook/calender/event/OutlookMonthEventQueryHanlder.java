package com.outlook.calender.event;

import android.content.ContentResolver;
import android.database.Cursor;

import com.outlook.calender.OutlookEventCursor;
import com.outlook.calender.calender.OutlookCalendarCursorAdapter;

/**
 * Created by ksachan on 7/7/17.
 */

public class OutlookMonthEventQueryHanlder extends OutlookEventQueryHandler
{
	
	private final OutlookCalendarCursorAdapter mAdapter;
	
	public OutlookMonthEventQueryHanlder(ContentResolver cr, OutlookCalendarCursorAdapter adapter)
	{
		super(cr);
		mAdapter = adapter;
	}
	
	@Override
	protected void handleQueryComplete(int token, Object cookie, OutlookEventCursor cursor)
	{
		mAdapter.bindEvents((Long)cookie, cursor);
	}
}
