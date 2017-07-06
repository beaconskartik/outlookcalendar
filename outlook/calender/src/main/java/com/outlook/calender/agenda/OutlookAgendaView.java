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
		loadMore();
		notifyDateChange();
	}
	
	@Override
	public void onScrollStateChanged(int state)
	{
		super.onScrollStateChanged(state);
		if (state == SCROLL_STATE_IDLE && mPendingScrollPosition != NO_POSITION)
		{
			if (mPendingScrollPosition == ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition())
			{
				mPendingScrollPosition = NO_POSITION; // clear pending
				mAdapter.unlockBinding();
			}
		}
	}
	
	public void setOnDateChangeListener(OnDateChangeListener listener)
	{
		mListener = listener;
	}
	
	public void setSelectedDay(@NonNull Calendar calendar)
	{
		if (mAdapter == null)
		{
			return;
		}
		
		mPendingScrollPosition = mAdapter.getPosition(getContext(), calendar.getTimeInMillis());
		if (mPendingScrollPosition >= 0)
		{
			mAdapter.lockBinding();
			smoothScrollToPosition(mPendingScrollPosition);
		}
	}
	
	void loadMore()
	{
		if (mAdapter == null)
		{
			return;
		}
		if (((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition() == 0)
		{
			// once prepended first visible position will no longer be 0
			// which will negate the guard check
			mAdapter.prepend(getContext());
		}
		else if (((LinearLayoutManager)getLayoutManager()).findLastVisibleItemPosition() == mAdapter.getItemCount() - 1)
		{
			// once appended last visible position will no longer be last adapter position
			// which will negate the guard check
			mAdapter.append(getContext());
		}
	}
	
	@Override
	public void setAdapter(Adapter adapter)
	{
		if (adapter != null && !(adapter instanceof OutlookAgendaAdapter))
		{
			throw new IllegalArgumentException("Adapter must be an instance of AgendaAdapter");
		}
		mAdapter = (OutlookAgendaAdapter)adapter;
		if (mAdapter != null)
		{
			mAdapter.append(getContext());
		}
		super.setAdapter(mAdapter);
	}
	
	private void init()
	{
		setHasFixedSize(true);
		setLayoutManager(new AgendaLinearLayoutManager(getContext()));
		addItemDecoration(new OutlookDividerDectorator(getContext()));
		getLayoutManager().scrollToPosition(OutlookAgendaAdapter.MONTH_SIZE * 2);
		
		if (isInEditMode())
		{
			setAdapter(new OutlookAgendaAdapter(getContext())
			{
			});
		}
	}
	
	private void notifyDateChange()
	{
		int position = ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
		if (position < 0)
		{
			return;
		}
		long timeMillis = mAdapter.getAdapterItem(position).getTimeMillis();
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
	
	static class AgendaLinearLayoutManager
			extends LinearLayoutManager
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
