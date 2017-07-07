package com.outlook.calendar.weather;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by ksachan on 7/7/17.
 */

public class OutlookWeatherWakefulBroadcastReceiver extends WakefulBroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		startWakefulService(context, new Intent(context, OutlookWeatherService.class));
	}
}
