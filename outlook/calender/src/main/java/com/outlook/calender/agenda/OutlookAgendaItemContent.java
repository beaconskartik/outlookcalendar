package com.outlook.calender.agenda;

import android.content.Context;
import android.os.Parcel;

import com.outlook.calender.R;

/**
 * Created by ksachan on 7/5/17.
 */

public class OutlookAgendaItemContent extends OutlookAgendaItem
{
	public static Creator<OutlookAgendaItemContent> CREATOR = new Creator<OutlookAgendaItemContent>()
	{
		@Override
		public OutlookAgendaItemContent createFromParcel(Parcel source)
		{
			return new OutlookAgendaItemContent(source);
		}
		
		@Override
		public OutlookAgendaItemContent[] newArray(int size)
		{
			return new OutlookAgendaItemContent[size];
		}
	};
	
	public OutlookAgendaItemContent(Context context, long timeMillis)
	{
		super(context.getString(R.string.no_event), timeMillis);
	}
	
	private OutlookAgendaItemContent(Parcel source)
	{
		super(source);
	}
}
