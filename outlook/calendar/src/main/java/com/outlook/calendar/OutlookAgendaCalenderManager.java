package com.outlook.calendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.outlook.calendar.agenda.OutlookAgendaView;
import com.outlook.calendar.calendar.OutlookCalendarViewPager;
import com.outlook.calendar.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/5/17.
 */

public class OutlookAgendaCalenderManager
{
	public OutlookAgendaCalenderManager(OutlookCalendarViewPager calendarView,
										OutlookAgendaView agendaView, Toolbar layout)
	{
		if (mCalendarView != null)
		{
			mCalendarView.setOnChangeListener(null);
		}
		
		if (mAgendaView != null)
		{
			mAgendaView.setOnDateChangeListener(null);
		}
		
		mTextView = null;
		mCalendarView = calendarView;
		mAgendaView = agendaView;
		mCollapsingToolbarLayout = layout;
		
		if (mSelectedDate < 0)
		{
			mSelectedDate = OutlookCalenderUtils.today();
		}
		
		calendarView.setSelectedDay(mSelectedDate);
		agendaView.setSelectedDay(mSelectedDate);
		updateTitle(mSelectedDate);
		
		calendarView.setOnChangeListener(mCalendarListener);
		agendaView.setOnDateChangeListener(mAgendaListener);
	}
	
	private void sync(@NonNull long calendar, View originator)
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
	
	private void updateTitle(long dayMillis)
	{
		CharSequence dayStr = OutlookCalenderUtils.toMonthString(mCollapsingToolbarLayout.getContext(), dayMillis);
		// mTextView.setText(dayStr);
		mCollapsingToolbarLayout.setTitle(dayStr);
	}
	
	private final OutlookCalendarViewPager.OnChangeListener mCalendarListener = new OutlookCalendarViewPager.OnChangeListener()
	{
		@Override
		public void onSelectedDayChange(@NonNull long calendar)
		{
			sync(calendar, mCalendarView);
		}
	};
	
	private final OutlookAgendaView.OnDateChangeListener mAgendaListener = new OutlookAgendaView.OnDateChangeListener()
	{
		@Override
		public void onSelectedDayChange(@NonNull long calendar)
		{
			
			sync(calendar, mAgendaView);
		}
	};
	
	private Toolbar                  mCollapsingToolbarLayout;
	private AppCompatCheckedTextView mTextView;
	private OutlookCalendarViewPager mCalendarView;
	private OutlookAgendaView        mAgendaView;
	private long      mSelectedDate = OutlookCalenderUtils.NO_TIME_MILLIS;
}
