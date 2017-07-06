package com.outlook.calender;

import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.outlook.calender.agenda.OutlookAgendaView;
import com.outlook.calender.calender.OutlookCalenderViewPager;
import com.outlook.calender.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/5/17.
 */

public class OutlookAgendaCalenderManager
{
	public OutlookAgendaCalenderManager(AppCompatCheckedTextView toolbarCheckedTextView, OutlookCalenderViewPager calendarView,
										OutlookAgendaView agendaView)
	{
		if (mCalendarView != null)
		{
			mCalendarView.setOnChangeListener(null);
		}
		
		if (mAgendaView != null)
		{
			mAgendaView.setOnDateChangeListener(null);
		}
		
		mTextView = toolbarCheckedTextView;
		mCalendarView = calendarView;
		mAgendaView = agendaView;
		
		if (mSelectedDate == null)
		{
			mSelectedDate = Calendar.getInstance();
		}
		
		calendarView.setSelectedDay(mSelectedDate);
		agendaView.setSelectedDay(mSelectedDate);
		updateTitle(mSelectedDate);
		
		calendarView.setOnChangeListener(mCalendarListener);
		agendaView.setOnDateChangeListener(mAgendaListener);
	}
	
	private void sync(@NonNull Calendar calendar, View originator)
	{
		mSelectedDate = calendar;
		if (!originator.equals(mCalendarView))
		{
			mCalendarView.setSelectedDay(calendar);
		}
		
		if (!originator.equals(mAgendaView))
		{
			mAgendaView.setSelectedDay(calendar);
		}
		updateTitle(calendar);
	}
	
	private void updateTitle(Calendar calendar)
	{
		mTextView.setText(OutlookCalenderUtils.toMonthString(mTextView.getContext(), calendar.getTimeInMillis()));
	}
	
	private final OutlookCalenderViewPager.OnChangeListener mCalendarListener = new OutlookCalenderViewPager.OnChangeListener()
	{
		@Override
		public void onSelectedDayChange(@NonNull Calendar calendar)
		{
			Log.d("kartik", "Month : " + OutlookCalenderUtils.toMonthString(mTextView.getContext(), calendar.getTimeInMillis()));
			sync(calendar, mCalendarView);
		}
	};
	
	private final OutlookAgendaView.OnDateChangeListener mAgendaListener = new OutlookAgendaView.OnDateChangeListener()
	{
		@Override
		public void onSelectedDayChange(@NonNull Calendar calendar)
		{
			Log.d("kartik", "Month : " + OutlookCalenderUtils.toMonthString(mTextView.getContext(), calendar.getTimeInMillis())
			+ " Day : " + calendar.get(Calendar.DAY_OF_MONTH));
			sync(calendar, mAgendaView);
		}
	};
	
	private AppCompatCheckedTextView          mTextView;
	private OutlookCalenderViewPager mCalendarView;
	private OutlookAgendaView        mAgendaView;
	private Calendar      mSelectedDate;
}
