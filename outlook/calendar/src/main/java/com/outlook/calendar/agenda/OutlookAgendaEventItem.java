package com.outlook.calendar.agenda;

import android.os.Parcel;
import android.text.format.DateUtils;

import com.outlook.calendar.OutlookEventCursor;
import com.outlook.calendar.utils.OutlookCalendarUtils;

/**
 * Created by ksachan on 7/6/17.
 */

public class OutlookAgendaEventItem
		extends OutlookAgendaItem
{
	public OutlookAgendaEventItem(long timeMillis, OutlookEventCursor cursor)
	{
		super(cursor.getTitle(), timeMillis);
		mId = cursor.getId();
		mCalendarId = cursor.getCalendarId();
		mStartTimeMillis = cursor.getDateTimeStart();
		mEndTimeMillis = cursor.getDateTimeEnd();
		mIsAllDay = cursor.getAllDay();
		// all-day time in Calendar Provider is midnight in UTC, need to convert to local
		if (mIsAllDay)
		{
			mStartTimeMillis = OutlookCalendarUtils.toLocalTimeZone(mStartTimeMillis);
			mEndTimeMillis = OutlookCalendarUtils.toLocalTimeZone(mEndTimeMillis);
		}
		setDisplayType();
	}
	
	OutlookAgendaEventItem(String title, long timeMillis)
	{
		super(title, timeMillis);
	}
	
	protected OutlookAgendaEventItem(Parcel source)
	{
		super(source);
		mId = source.readLong();
		mCalendarId = source.readLong();
		mStartTimeMillis = source.readLong();
		mEndTimeMillis = source.readLong();
		mIsAllDay = source.readInt() == 1;
		mDisplayType = source.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		dest.writeLong(mId);
		dest.writeLong(mCalendarId);
		dest.writeLong(mStartTimeMillis);
		dest.writeLong(mEndTimeMillis);
		dest.writeInt(mIsAllDay ? 1 : 0);
		dest.writeInt(mDisplayType);
	}
	
	private void setDisplayType()
	{
		if (mIsAllDay)
		{
			mDisplayType = DISPLAY_TYPE_ALL_DAY;
		}
		else if (mStartTimeMillis >= mTimeMillis)
		{
			// start within agenda date
			mDisplayType = DISPLAY_TYPE_START_TIME;
		}
		else if (mEndTimeMillis < mTimeMillis + DateUtils.DAY_IN_MILLIS)
		{
			// start before, end within agenda date
			mDisplayType = DISPLAY_TYPE_END_TIME;
		}
		else
		{
			// start before, end after agenda date
			mDisplayType = DISPLAY_TYPE_ALL_DAY;
		}
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
	
	static final int DISPLAY_TYPE_START_TIME = 0;
	static final int DISPLAY_TYPE_ALL_DAY    = 1;
	static final int DISPLAY_TYPE_END_TIME   = 2;
	
	long    mId;
	long    mCalendarId;
	long    mStartTimeMillis;
	long    mEndTimeMillis;
	boolean mIsAllDay;
	int mDisplayType = DISPLAY_TYPE_START_TIME;
}