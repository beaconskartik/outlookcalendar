package com.outlook.calender.agenda;

import android.content.Context;
import android.os.Parcel;

import com.outlook.calender.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/5/17.
 */

public class OutlookAgendaItemHeader extends OutlookAgendaItem
{
	public static Creator<OutlookAgendaItemHeader> CREATOR = new Creator<OutlookAgendaItemHeader>()
	{
		@Override
		public OutlookAgendaItemHeader createFromParcel(Parcel source)
		{
			return new OutlookAgendaItemHeader(source);
		}
		
		@Override
		public OutlookAgendaItemHeader[] newArray(int size)
		{
			return new OutlookAgendaItemHeader[size];
		}
	};
	
	public OutlookAgendaItemHeader(Context context, long timeMillis)
	{
		super(OutlookCalenderUtils.toDayString(context, timeMillis), timeMillis);
	}
	
	private OutlookAgendaItemHeader(Parcel source)
	{
		super(source);
	}
}
