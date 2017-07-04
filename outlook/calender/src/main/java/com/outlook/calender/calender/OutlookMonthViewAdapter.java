package com.outlook.calender.calender;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.outlook.calender.R;
import com.outlook.calender.calender.OutlookMonthViewAdapter.CellViewHolder;
import com.outlook.calender.decorator.OutlookCircleDecorator;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookMonthViewAdapter extends Adapter<CellViewHolder>
{
	public OutlookMonthViewAdapter(Calendar cal, OutlookOnDayCellClicked handler)
	{
		mWeekdays = DateFormatSymbols.getInstance().getShortWeekdays();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		mStartOffset = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek() + SPANS_COUNT;
		mDays = mStartOffset + cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		mClickHandler = handler;
	}
	
	@Override
	public CellViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType)
		{
			case VIEW_TYPE_HEADER:
				return new HeaderViewHolder(inflater.inflate(R.layout.month_header_view, parent, false));
			case VIEW_TYPE_CONTENT:
			default:
				return new ContentViewHolder(inflater.inflate(R.layout.month_item_view, parent, false));
		}
	}
	
	@Override
	public void onBindViewHolder(CellViewHolder holder, int position)
	{
		if (holder instanceof HeaderViewHolder)
		{
			((HeaderViewHolder)holder).textView.setText(mWeekdays[position + Calendar.SUNDAY]);
		}
		else if (holder instanceof ContentViewHolder)
		{
			if (position < mStartOffset)
			{
				((ContentViewHolder)holder).textView.setText(null);
			}
			else
			{
				final int adapterPosition = holder.getAdapterPosition();
				TextView textView = ((ContentViewHolder)holder).textView;
				String day = String.valueOf(adapterPosition - mStartOffset + 1);
				
				SpannableString spannable = new SpannableString(day);
				if (mSelectedPosition == adapterPosition)
				{
					spannable.setSpan(new OutlookCircleDecorator(textView.getContext()), 0, day.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				textView.setText(spannable, TextView.BufferType.SPANNABLE);
				textView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						setSelectedPosition(adapterPosition, true);
					}
				});
			}
		}
	}
	
	@Override
	public int getItemViewType(int position)
	{
		if (position < SPANS_COUNT)
		{
			return VIEW_TYPE_HEADER;
		}
		return VIEW_TYPE_CONTENT;
	}
	
	@Override
	public int getItemCount()
	{
		return mDays;
	}
	
	void setSelectedDay(@Nullable Calendar selectedDay)
	{
		setSelectedPosition(selectedDay == null ? -1 : mStartOffset + selectedDay.get(Calendar.DAY_OF_MONTH) - 1, false);
	}
	
	private void setSelectedPosition(int position, boolean notifyObservers)
	{
		int last = mSelectedPosition;
		if (position == last)
		{
			return;
		}
		mSelectedPosition = position;
		if (last >= 0)
		{
			notifyItemChanged(last);
		}
		if (position >= 0)
		{
			notifyItemChanged(position, notifyObservers ? new SelectionPayload(mSelectedPosition - mStartOffset + 1) : null);
		}
	}
	
	static abstract class CellViewHolder
			extends ViewHolder
	{
		
		public CellViewHolder(View itemView)
		{
			super(itemView);
		}
	}
	
	static class HeaderViewHolder
			extends CellViewHolder
	{
		
		final TextView textView;
		
		public HeaderViewHolder(View itemView)
		{
			super(itemView);
			textView = (TextView)itemView;
		}
	}
	
	static class ContentViewHolder
			extends CellViewHolder
	{
		
		final TextView textView;
		
		public ContentViewHolder(View itemView)
		{
			super(itemView);
			textView = (TextView)itemView;
		}
	}
	
	static class SelectionPayload
	{
		final int dayOfMonth;
		
		public SelectionPayload(int dayOfMonth)
		{
			this.dayOfMonth = dayOfMonth;
		}
	}
	
	public interface OutlookOnDayCellClicked
	{
		void onClick(View view);
	}
	
	private static final int VIEW_TYPE_HEADER  = 0;
	private static final int VIEW_TYPE_CONTENT = 1;
	private static final int SPANS_COUNT       = 7;
	private final String[] mWeekdays;
	private final int      mStartOffset;
	private final int      mDays;
	private int mSelectedPosition = -1;
	
	private OutlookOnDayCellClicked mClickHandler;
}
