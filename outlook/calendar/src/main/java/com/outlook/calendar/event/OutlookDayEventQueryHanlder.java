package com.outlook.calendar.event;

import android.content.ContentResolver;

import com.outlook.calendar.agenda.OutlookAgendaCursorAdapter;
import com.outlook.calendar.OutlookEventCursor;

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
	protected void handleQueryComplete(int token, Object cookie, OutlookEventCursor cursor)
	{
		mAgendaCursorAdapter.bindEvents((Long) cookie, cursor);
	}
}
