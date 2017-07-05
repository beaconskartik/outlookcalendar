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
	
	public void setOnDateChangeListener(OnDateChangeListener listener)
	{
		mDateChangeListener = listener;
	}
	
	void setCalendar(Calendar calendar)
	{
		mCalendar = calendar;
		mAdapter = new OutlookMonthViewAdapter(getContext(), calendar, new OutlookOnDayCellClicked()
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
				if (mDateChangeListener == null)
				{
					return;
				}
				if (payload != null && payload instanceof SelectionPayload)
				{
					Calendar selectedDay = Calendar.getInstance();
					selectedDay.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), ((SelectionPayload)payload).dayOfMonth);
					
					// triggering the call for selected day change
					mDateChangeListener.onSelectedDayChange(selectedDay);
				}
			}
		});
		
		setAdapter(mAdapter);
	}
	
	/**
	 * Interface to let client know that the selected day has been changed.
	 */
	interface OnDateChangeListener
	{
		void onSelectedDayChange(@NonNull Calendar calendar);
	}
	
	private static int SPAN_COUNT = 7; // 7 months so grid is of 7
	private Calendar                mCalendar;
	private OutlookMonthViewAdapter mAdapter;
	private OnDateChangeListener    mDateChangeListener;
}