package com.outlook.calender.agenda;

import java.util.Calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
		setLayoutManager(new LinearLayoutManager(getContext()));
		addItemDecoration(new OutlookDividerDectorator(getContext()));
		mAdapter = new OutlookAgendaAdapter(getContext());
		setAdapter(mAdapter);
		getLayoutManager().scrollToPosition(OutlookAgendaAdapter.MONTH_SIZE * 2); // start of current month
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
		if (mPendingScrollPosition == position)
		{
			mPendingScrollPosition = NO_POSITION; // clear pending
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
