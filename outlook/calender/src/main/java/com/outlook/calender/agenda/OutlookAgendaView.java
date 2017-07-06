package com.outlook.calender.agenda;

import java.util.Calendar;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.outlook.calender.decorator.OutlookDividerDectorator;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookAgendaView extends RecyclerView
{
	public OutlookAgendaView(Context context)
	{
		this(context, null);
	}
	
	public OutlookAgendaView(Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	
	public OutlookAgendaView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	public void onScrolled(int dx, int dy)
	{
		notifyDateChange();
	}
	
	public void setOnDateChangeListener(OnDateChangeListener listener)
	{
		mListener = listener;
	}
	
	public void setSelectedDay(@NonNull Calendar calendar)
	{
		mPendingScrollPosition = mAdapter.getPosition(getContext(), calendar.getTimeInMillis());
		if (mPendingScrollPosition >= 0)
		{
			smoothScrollToPosition(mPendingScrollPosition);
		}
	}
	
	private void init()
	{
		setHasFixedSize(true);
		setLayoutManager(new AgendaLinearLayoutManager(getContext()));
		addItemDecoration(new OutlookDividerDectorator(getContext()));
		mAdapter = new OutlookAgendaAdapter(getContext());
		setAdapter(mAdapter);
		getLayoutManager().scrollToPosition(OutlookAgendaAdapter.MONTH_SIZE * 2);
	}
	
	private void notifyDateChange()
	{
		int position = ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
		if (position < 0)
		{
			return;
		}
		long timeMillis = mAdapter.getItem(position).getTimeMillis();
		if (mPrevTimeMillis != timeMillis)
		{
			mPrevTimeMillis = timeMillis;
			mSelectedDate.setTimeInMillis(timeMillis);
			// only notify listener if scroll is not triggered programmatically (i.e. no pending)
			if (mPendingScrollPosition == NO_POSITION && mListener != null)
			{
				mListener.onSelectedDayChange(mSelectedDate);
			}
		}
	}
	
	static class AgendaLinearLayoutManager extends LinearLayoutManager
	{
		
		public AgendaLinearLayoutManager(Context context)
		{
			super(context);
		}
		
		@Override
		public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
		{
			RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext())
			{
				@Override
				public PointF computeScrollVectorForPosition(int targetPosition)
				{
					return AgendaLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
				}
				
				@Override
				protected int getVerticalSnapPreference()
				{
					return SNAP_TO_START; // override base class behavior
				}
			};
			smoothScroller.setTargetPosition(position);
			startSmoothScroll(smoothScroller);
		}
	}
	
	public interface OnDateChangeListener
	{
		void onSelectedDayChange(@NonNull Calendar calendar);
	}
	
	private OnDateChangeListener mListener;
	private OutlookAgendaAdapter mAdapter;
	private final Calendar mSelectedDate          = Calendar.getInstance();
	private       int      mPendingScrollPosition = NO_POSITION; // represent top scroll position to be set programmatically
	private       long     mPrevTimeMillis        = -1;
}
