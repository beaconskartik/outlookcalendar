package com.outlook.calender.calender;

import java.util.Calendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.outlook.calender.calender.OutlookMonthViewAdapter.OutlookOnDayCellClicked;

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
	
	void setCalendar(Calendar calendar)
	{
		setAdapter(new OutlookMonthViewAdapter(calendar, new OutlookOnDayCellClicked()
		{
			@Override
			public void onClick(View view)
			{
				
			}
		}));
	}

	private static int SPAN_COUNT = 7;
}