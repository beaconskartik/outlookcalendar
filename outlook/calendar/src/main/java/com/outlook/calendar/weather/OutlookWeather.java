package com.outlook.calendar.weather;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;

import com.outlook.calender.R;

/**
 * Created by ksachan on 7/7/17.
 */

public class OutlookWeather
{
	public OutlookWeather(@NonNull String[] todayData, @NonNull String[] tomorrowData)
	{
		if (todayData.length == SPEC_SIZE)
		{
			today = new DayInfo(todayData);
		}
		if (tomorrowData.length == SPEC_SIZE)
		{
			tomorrow = new DayInfo(tomorrowData);
		}
	}
	
	/**
	 * Model for weather information in a day
	 */
	public static class DayInfo
	{
		
		/**
		 * Morning weather
		 */
		public final WeatherInfo morning   = new WeatherInfo();
		/**
		 * Afternoon weather
		 */
		public final WeatherInfo afternoon = new WeatherInfo();
		/**
		 * Night weather
		 */
		public final WeatherInfo night     = new WeatherInfo();
		
		DayInfo(String[] specs)
		{
			int index = 0;
			morning.icon = specs[index++];
			morning.temperature = toTemperature(specs[index++]);
			afternoon.icon = specs[index++];
			afternoon.temperature = toTemperature(specs[index++]);
			night.icon = specs[index++];
			night.temperature = toTemperature(specs[index]);
		}
		
		private Float toTemperature(String temperature)
		{
			if (TextUtils.isEmpty(temperature))
			{
				return null;
			}
			try
			{
				return Float.valueOf(temperature);
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}
	}
	
	/**
	 * View model for weather information
	 */
	public static class WeatherInfo
	{
		@Nullable
		public Drawable getIcon(Context context, @ColorInt int tint)
		{
			if (TextUtils.isEmpty(icon))
			{
				return null;
			}
			int drawableResId = R.drawable.ic_cloudy_24dp;
			if (ICON_MAP.containsKey(icon))
			{
				drawableResId = ICON_MAP.get(icon);
			}
			Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
			drawable = DrawableCompat.wrap(drawable);
			DrawableCompat.setTint(drawable, tint);
			return drawable;
		}
		
		private static final HashMap<String, Integer> ICON_MAP         = new HashMap<>();
		private static final String                   ICON_CLEAR_DAY   = "clear-day";
		private static final String                   ICON_CLEAR_NIGHT = "clear-night";
		private static final String                   ICON_CLOUDY      = "cloudy";
		private static final String                   ICON_RAIN        = "rain";
		private static final String                   ICON_SNOW        = "snow";
		private static final String                   ICON_WIND        = "wind";
		private static final String                   ICON_PARTLY_CLOUDY_DAY   = "partly-cloudy-day";
		private static final String                   ICON_PARTLY_CLOUDY_NIGHT = "partly-cloudy-night";
		
		static
		{
			ICON_MAP.put(ICON_CLEAR_DAY, R.drawable.ic_clear_day_24dp);
			ICON_MAP.put(ICON_CLEAR_NIGHT, R.drawable.ic_clear_night_24dp);
			ICON_MAP.put(ICON_CLOUDY, R.drawable.ic_cloudy_24dp);
			ICON_MAP.put(ICON_PARTLY_CLOUDY_DAY, R.drawable.ic_partly_cloudy_day_24dp);
			ICON_MAP.put(ICON_PARTLY_CLOUDY_NIGHT, R.drawable.ic_partly_cloudy_night_24dp);
			ICON_MAP.put(ICON_RAIN, R.drawable.ic_rain_24dp);
			ICON_MAP.put(ICON_SNOW, R.drawable.ic_snow_24dp);
			ICON_MAP.put(ICON_WIND, R.drawable.ic_wind_24dp);
		}
		
		String icon;
		public Float temperature;
	}
	
	private static final int SPEC_SIZE = 6;
	public DayInfo today    = null;
	public DayInfo tomorrow = null;
}
