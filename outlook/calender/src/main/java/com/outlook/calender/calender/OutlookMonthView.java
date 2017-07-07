package com.outlook.calender.calender;

import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.outlook.calender.calender.OutlookMonthViewAdapter.OutlookOnDayCellClicked;
import com.outlook.calender.calender.OutlookMonthViewAdapter.SelectionPayload;
import com.outlook.calender.utils.OutlookCalenderUtils;

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
		setLayoutManager(new CustomGridLayoutManager(getContext(), SPAN_COUNT));
		setHasFixedSize(true);
		setMonthMillis(OutlookCalenderUtils.today());
	}
	
	public void setOnDateChangeListener(OnDateChangeListener listener)
	{
		mDateChangeListener = listener;
	}
	
	public void setMonthMillis(long monthMillis)
	{
		mMonthMillis = monthMillis;
		mAdapter = new OutlookMonthViewAdapter(getContext(), monthMillis, new OutlookOnDayCellClicked()
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
					// triggering the call for selected day change
					mDateChangeListener.onSelectedDayChange(((SelectionPayload) payload).timeMillis);
				}
			}
		});
		
		setAdapter(mAdapter);
	}
	
	void setSelectedDay(long dayMillis)
	{
		if (OutlookCalenderUtils.isNotTime(mMonthMillis))
		{
			return;
		}
		if (OutlookCalenderUtils.isNotTime(dayMillis))
		{
			mAdapter.setSelectedDay(OutlookCalenderUtils.NO_TIME_MILLIS);
		}
		else if (OutlookCalenderUtils.sameMonth(mMonthMillis, dayMillis))
		{
			mAdapter.setSelectedDay(dayMillis);
		}
		else
		{
			mAdapter.setSelectedDay(OutlookCalenderUtils.NO_TIME_MILLIS);
		}
	}
	
	public void swapCursor(@NonNull Cursor cursor)
	{
		mAdapter.swapCursor(cursor);
	}
	
	/**
	 * Interface to let client know that the selected day has been changed.
	 */
	interface OnDateChangeListener
	{
		void onSelectedDayChange(long dayMillis);
	}
	
	static public class CustomGridLayoutManager extends GridLayoutManager
	{
		public CustomGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
		{
			super(context, attrs, defStyleAttr, defStyleRes);
		}
		
		public CustomGridLayoutManager(Context context, int spanCount)
		{
			super(context, spanCount);
		}
		
		public CustomGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout)
		{
			super(context, spanCount, orientation, reverseLayout);
		}
		
		@Override
		public void onLayoutChildren(Recycler recycler, State state)
		{
			try
			{
				super.onLayoutChildren(recycler, state);
			}
			catch (IndexOutOfBoundsException e)
			{
				Log.e("outllook_debug", " oops encounter an issue : " + e);
			}
		}
	}
	
	private static int SPAN_COUNT = 7; // 7 days in a week so grid is of 7
	private long                    mMonthMillis;
	private OutlookMonthViewAdapter mAdapter;
	private OnDateChangeListener    mDateChangeListener;
}