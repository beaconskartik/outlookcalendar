package com.outlook.calendar.agenda;

import android.os.Parcel;

/**
 * Created by ksachan on 7/6/17.
 */


public class OutlookAgendaNoEvent extends OutlookAgendaEventItem
{
	public static Creator<OutlookAgendaNoEvent> CREATOR = new Creator<OutlookAgendaNoEvent>()
	{
		@Override
		public OutlookAgendaNoEvent createFromParcel(Parcel source)
		{
			return new OutlookAgendaNoEvent(source);
		}
		
		@Override
		public OutlookAgendaNoEvent[] newArray(int size)
		{
			return new OutlookAgendaNoEvent[size];
		}
	};
	
	OutlookAgendaNoEvent(String title, long timeMillis)
	{
		super(title, timeMillis);
		mStartTimeMillis = timeMillis;
		mEndTimeMillis = timeMillis;
	}
	
	public OutlookAgendaNoEvent(Parcel source)
	{
		super(source);
	}
}