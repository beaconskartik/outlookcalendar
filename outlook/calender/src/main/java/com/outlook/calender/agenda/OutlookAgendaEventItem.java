package com.outlook.calender.agenda;

import android.os.Parcel;

/**
 * Created by ksachan on 7/6/17.
 */

public class OutlookAgendaEventItem
		extends OutlookAgendaItem
{
	public static Creator<OutlookAgendaEventItem> CREATOR = new Creator<OutlookAgendaEventItem>()
	{
		@Override
		public OutlookAgendaEventItem createFromParcel(Parcel source)
		{
			return new OutlookAgendaEventItem(source);
		}
		
		@Override
		public OutlookAgendaEventItem[] newArray(int size)
		{
			return new OutlookAgendaEventItem[size];
		}
	};
	private long mStartTimeMillis;
	
	public OutlookAgendaEventItem(String title, long timeMillis, long startTimeMillis)
	{
		super(title, timeMillis);
		this.mStartTimeMillis = startTimeMillis;
	}
	
	protected OutlookAgendaEventItem(Parcel source)
	{
		super(source);
		mStartTimeMillis = source.readLong();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		dest.writeLong(mStartTimeMillis);
	}
}