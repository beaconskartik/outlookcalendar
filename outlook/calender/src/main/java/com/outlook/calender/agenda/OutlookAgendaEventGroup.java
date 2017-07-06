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

public class OutlookAgendaEventGroup extends OutlookAgendaItem {
	public static Creator<OutlookAgendaEventGroup> CREATOR = new Creator<OutlookAgendaEventGroup>() {
		@Override
		public OutlookAgendaEventGroup createFromParcel(Parcel source) {
			return new OutlookAgendaEventGroup(source);
		}
		
		@Override
		public OutlookAgendaEventGroup[] newArray(int size) {
			return new OutlookAgendaEventGroup[size];
		}
	};
	
	interface EventObserver {
		void onChange(long timeMillis);
	}
	
	private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			mEventObserver.onChange(mTimeMillis);
		}
	};
	private OutlookAgendaEventGroup.EventObserver mEventObserver;
	int mLastCursorCount = 0;
	Cursor mCursor;
	
	OutlookAgendaEventGroup(Context context, long timeMillis) {
		super(OutlookCalenderUtils.toDayString(context, timeMillis), timeMillis);
	}
	
	private OutlookAgendaEventGroup(Parcel source) {
		super(source);
	}
	
	int itemCount() {
		if (mCursor == null || mCursor.getCount() == 0) {
			return 1; // has a no event item by default
		}
		return mCursor.getCount();
	}
	
	OutlookAgendaEventItem getItem(int index) {
		if (mCursor == null || mCursor.getCount() == 0) {
			return new OutlookAgendaNoEvent(null, mTimeMillis, mTimeMillis);
		}
		mCursor.moveToPosition(index);
		// TODO use an object pool
		return new OutlookAgendaEventItem(
				mCursor.getString(mCursor.getColumnIndex(CalendarContract.Events.TITLE)),
				mTimeMillis,
				mCursor.getLong(mCursor.getColumnIndex(CalendarContract.Events.DTSTART)));
	}
	
	void setCursor(Cursor cursor, OutlookAgendaEventGroup.EventObserver eventObserver) {
		cursor.registerContentObserver(mContentObserver);
		mCursor = cursor;
		mEventObserver = eventObserver;
	}
	
	void deactivate() {
		if (mCursor != null) {
			mCursor.unregisterContentObserver(mContentObserver);
			mCursor.close();
			mCursor = null;
			mEventObserver = null;
		}
	}
}