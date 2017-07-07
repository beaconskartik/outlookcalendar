package com.outlook.calender.agenda;

import android.os.Parcel;

/**
 * Created by ksachan on 7/6/17.
 */

public class OutlookAgendaEventItem
		extends OutlookAgendaItem
{
	public OutlookAgendaEventItem(String title, long timeMillis, long startTimeMillis, boolean allday)
	{
		super(title, timeMillis);
		mStartTimeMillis = startTimeMillis;
		mIsAllDay = allday;
	}
	
	protected OutlookAgendaEventItem(Parcel source)
	{
		super(source);
		mStartTimeMillis = source.readLong();
		mIsAllDay = source.readInt()  == 1;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		dest.writeLong(mStartTimeMillis);
		dest.writeInt(mIsAllDay ? 1 : 0);
	}
	
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
	
	protected  long mStartTimeMillis;
	protected boolean mIsAllDay;
}