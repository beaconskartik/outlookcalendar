package com.outlook.calender;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

import com.outlook.calender.agenda.OutlookAgendaCursorAdapter;

/**
 * Created by ksachan on 7/6/17.
 */

public class OutlookCalendarEventQueryHandler
		extends AsyncQueryHandler
{
	
	private final OutlookAgendaCursorAdapter mAgendaCursorAdapter;
	
	public OutlookCalendarEventQueryHandler(ContentResolver cr, OutlookAgendaCursorAdapter agendaCursorAdapter)
	{
		super(cr);
		mAgendaCursorAdapter = agendaCursorAdapter;
	}
	
	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor)
	{
		mAgendaCursorAdapter.bindEvents((Long)cookie, cursor);
	}
}
