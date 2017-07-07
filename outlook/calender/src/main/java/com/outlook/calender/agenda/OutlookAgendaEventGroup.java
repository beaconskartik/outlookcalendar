package com.outlook.calender.agenda;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Parcel;
import android.provider.CalendarContract;

import com.outlook.calender.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/6/17.
 */

public class OutlookAgendaEventGroup extends OutlookAgendaItem
{
	public OutlookAgendaEventGroup(Context context, long timeMillis)
	{
		super(OutlookCalenderUtils.toDayString(context, timeMillis), timeMillis);
	}
	
	private OutlookAgendaEventGroup(Parcel source)
	{
		super(source);
	}
	
	public int itemCount()
	{
		if (mCursor == null || mCursor.getCount() == 0)
		{
			return 1; // has a no event item by default
		}
		return mCursor.getCount();
	}
	
	public OutlookAgendaEventItem getItem(int index)
	{
		if (mCursor == null || mCursor.getCount() == 0)
		{
			return new OutlookAgendaNoEvent(null, mTimeMillis, mTimeMillis);
		}
		
		mCursor.moveToPosition(index);
		return new OutlookAgendaEventItem(mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.TITLE)), mTimeMillis,
				mCursor.getLong(mCursor.getColumnIndex(CalendarContract.Events.DTSTART)),
				mCursor.getInt(mCursor.getColumnIndex(CalendarContract.Events.ALL_DAY)) == 1);
	}
	
	public void setCursor(Cursor cursor, OutlookEventObserver outlookEventObserver)
	{
		deactivate();
		cursor.registerContentObserver(mContentObserver);
		mCursor = cursor;
		mOutlookEventObserver = outlookEventObserver;
	}
	
	void deactivate()
	{
		if (mCursor != null)
		{
			mCursor.unregisterContentObserver(mContentObserver);
			mCursor.close();
			mCursor = null;
			mOutlookEventObserver = null;
		}
	}
	
	public static Creator<OutlookAgendaEventGroup> CREATOR = new Creator<OutlookAgendaEventGroup>()
	{
		@Override
		public OutlookAgendaEventGroup createFromParcel(Parcel source)
		{
			return new OutlookAgendaEventGroup(source);
		}
		
		@Override
		public OutlookAgendaEventGroup[] newArray(int size)
		{
			return new OutlookAgendaEventGroup[size];
		}
	};
	
	interface OutlookEventObserver
	{
		void onChange(long timeMillis);
	}
	
	private final ContentObserver mContentObserver = new ContentObserver(new Handler())
	{
		@Override
		public boolean deliverSelfNotifications()
		{
			return true;
		}
		
		@Override
		public void onChange(boolean selfChange)
		{
			mOutlookEventObserver.onChange(mTimeMillis);
		}
	};
	
	OutlookEventObserver mOutlookEventObserver;
	int mLastCursorCount = 0;
	Cursor mCursor;
}