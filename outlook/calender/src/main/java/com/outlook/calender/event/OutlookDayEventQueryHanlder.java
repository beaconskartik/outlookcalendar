package com.outlook.calender.event;

import android.content.ContentResolver;
import android.database.Cursor;

import com.outlook.calender.agenda.OutlookAgendaCursorAdapter;

/**
 * Created by ksachan on 7/7/17.
 */
public class OutlookDayEventQueryHanlder extends OutlookEventQueryHandler
{
	
	private final OutlookAgendaCursorAdapter mAgendaCursorAdapter;
	
	public OutlookDayEventQueryHanlder(ContentResolver cr, OutlookAgendaCursorAdapter agendaCursorAdapter)
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
