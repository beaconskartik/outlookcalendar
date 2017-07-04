package com.outlook.calender.calender;

import java.util.Calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.outlook.calender.calender.OutlookMonthViewAdapter.OutlookOnDayCellClicked;
import com.outlook.calender.calender.OutlookMonthViewAdapter.SelectionPayload;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookMonthView extends RecyclerView
{
	public OutlookMonthView(Context context)
	{
		this(context, null);
	}
	
	public OutlookMonthView(Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	
	public OutlookMonthView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		initialSetup();
	}
	
	private void initialSetup()
	{
		setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
		setHasFixedSize(true);
		setCalendar(Calendar.getInstance());
	}
	
	void setOnDateChangeListener(OnDateChangeListener listener)
	{
		mListener = listener;
	}
	
	void setCalendar(Calendar calendar)
	{
		mCalendar = calendar;
		
		mAdapter = new OutlookMonthViewAdapter(calendar, new OutlookOnDayCellClicked()
		{
			@Override
			public void onClick(View view)
			{
				
			}
		});
		
		mAdapter.registerAdapterDataObserver(new AdapterDataObserver()
		{
			@Override
			public void onItemRangeChanged(int positionStart, int itemCount, Object payload)
			{
				if (mListener == null)
				{
					return;
				}
				if (payload != null && payload instanceof SelectionPayload)
				{
					mSelectedDay.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), ((SelectionPayload)payload).dayOfMonth);
					mListener.onSelectedDayChange(mSelectedDay);
				}
			}
		});
		
		setAdapter(mAdapter);
	}
	
	private void setSelectedDay(@Nullable Calendar selectedDay)
	{
		if (mCalendar == null)
		{
			return;
		}
		if (selectedDay == null)
		{
			mAdapter.setSelectedDay(null);
		}
		else if (mCalendar.get(Calendar.YEAR) == selectedDay.get(Calendar.YEAR) && mCalendar.get(Calendar.MONTH) == selectedDay.get(Calendar.MONTH))
		{
			mAdapter.setSelectedDay(selectedDay);
		}
		else
		{
			mAdapter.setSelectedDay(null);
		}
	}
	
	private Calendar getCalendar()
	{
		return mCalendar;
	}
	
	interface OnDateChangeListener
	{
		
		void onSelectedDayChange(@NonNull Calendar calendar);
	}
	
	private static int      SPAN_COUNT   = 7;
	private final  Calendar mSelectedDay = Calendar.getInstance();
	private Calendar                mCalendar;
	private OutlookMonthViewAdapter mAdapter;
	private OnDateChangeListener    mListener;
}