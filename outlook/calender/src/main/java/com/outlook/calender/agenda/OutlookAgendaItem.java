package com.outlook.calender.agenda;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ksachan on 7/5/17.
 */

public abstract class OutlookAgendaItem implements Parcelable
{
	
	final String mTitle;
	final long   mTimeMillis;
	
	public OutlookAgendaItem(String title, long timeMillis)
	{
		mTitle = title;
		mTimeMillis = timeMillis;
	}
	
	public OutlookAgendaItem(Parcel source)
	{
		mTitle = source.readString();
		mTimeMillis = source.readLong();
	}
	
	@Override
	public int describeContents()
	{
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mTitle);
		dest.writeLong(mTimeMillis);
	}
}
