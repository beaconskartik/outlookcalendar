package com.outlook.calendar.calendar;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.outlook.calender.R;
import com.outlook.calendar.calendar.OutlookMonthViewAdapter.CellViewHolder;
import com.outlook.calendar.decorator.OutlookCircleDecorator;
import com.outlook.calendar.decorator.OutlookDotDecorator;
import com.outlook.calendar.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookMonthViewAdapter extends Adapter<CellViewHolder>
{
	public OutlookMonthViewAdapter(Context context, long monthMillis, OutlookOnDayCellClicked handler)
	{
		mWeekdays = DateFormatSymbols.getInstance().getShortWeekdays();
		mBaseTimeMillis = OutlookCalenderUtils.monthFirstDay(monthMillis);
		mStartOffset = OutlookCalenderUtils.monthFirstDayOffset(mBaseTimeMillis) + SPANS_COUNT;
		mDays = mStartOffset + OutlookCalenderUtils.monthSize(monthMillis);
		mDayCellClickHandler = handler;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	@Override
	public CellViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		CellViewHolder viewHolder = null;
		switch (viewType)
		{
			case VIEW_TYPE_HEADER:
			{
				viewHolder = new HeaderViewHolder(mLayoutInflater.inflate(R.layout.month_header_view, parent, false));
				break;
			}
			case VIEW_TYPE_CONTENT:
			default:
			{
				viewHolder = new ContentViewHolder(mLayoutInflater.inflate(R.layout.month_item_view, parent, false));
				break;
			}
		}
		return viewHolder;
	}
	
	@Override
	public void onBindViewHolder(CellViewHolder holder, int position)
	{
		int viewType = getItemViewType(position);
		
		if (viewType == VIEW_TYPE_HEADER)
		{
			((HeaderViewHolder)holder).textView.setText(mWeekdays[position + Calendar.SUNDAY]);
		}
		else if (viewType == VIEW_TYPE_CONTENT)
		{
			if (position < mStartOffset)
			{
				((ContentViewHolder)holder).textView.setText(null);
			}
			else
			{
				final int adapterPosition = holder.getAdapterPosition();
				TextView textView = ((ContentViewHolder)holder).textView;
				int dayIndex = adapterPosition - mStartOffset;
				
				String dayString = String.valueOf(dayIndex + 1);
				
				SpannableString spannable = new SpannableString(dayString);
				if (mSelectedPosition == adapterPosition)
				{
					spannable.setSpan(new OutlookCircleDecorator(textView.getContext()), 0, dayString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				else if (mEvents.contains(dayIndex))
				{
					spannable.setSpan(new OutlookDotDecorator(textView.getContext()), 0, dayString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				textView.setText(spannable, TextView.BufferType.SPANNABLE);
				textView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						setSelectedPosition(adapterPosition, true);
						
						if (mDayCellClickHandler != null)
						{
							mDayCellClickHandler.onClick(v);
						}
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
	
	public void setSelectedDay(long dayMillis)
	{
		setSelectedPosition(OutlookCalenderUtils.isNotTime(dayMillis) ? -1 : mStartOffset + OutlookCalenderUtils.dayOfMonth(dayMillis) - 1, false);
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
			long timeMillis = mBaseTimeMillis + (mSelectedPosition - mStartOffset) * DateUtils.DAY_IN_MILLIS;
			notifyItemChanged(position, notifyObservers ? new SelectionPayload(timeMillis) : null);
		}
	}
	
	void swapCursor(@NonNull Cursor cursor)
	{
		if (mCursor == cursor)
		{
			return;
		}
		mCursor = cursor;
		Iterator<Integer> iterator = mEvents.iterator();
		while (iterator.hasNext())
		{
			int dayIndex = iterator.next();
			iterator.remove();
			notifyItemChanged(dayIndex + mStartOffset);
		}
		if (!mCursor.moveToFirst())
		{
			return;
		}
		// TODO improve performance
		do
		{
			long start = mCursor.getLong(mCursor.getColumnIndexOrThrow(CalendarContract.Events.DTSTART));
			long end = mCursor.getLong(mCursor.getColumnIndexOrThrow(CalendarContract.Events.DTEND));
			boolean allDay = mCursor.getInt(mCursor.getColumnIndexOrThrow(CalendarContract.Events.ALL_DAY)) == 1;
			if (allDay)
			{
				end -= DateUtils.DAY_IN_MILLIS;
			}
			int startIndex = (int)((start - mBaseTimeMillis) / DateUtils.DAY_IN_MILLIS);
			int endIndex = (int)((end - mBaseTimeMillis) / DateUtils.DAY_IN_MILLIS);
			endIndex = Math.min(endIndex, getItemCount() - mStartOffset - 1);
			for (int dayIndex = startIndex; dayIndex <= endIndex; dayIndex++)
			{
				if (!mEvents.contains(dayIndex))
				{
					mEvents.add(dayIndex);
					notifyItemChanged(dayIndex + mStartOffset);
				}
			}
		}
		while (mCursor.moveToNext());
	}
	
	protected static class CellViewHolder
			extends ViewHolder
	{
		
		public CellViewHolder(View itemView)
		{
			super(itemView);
		}
	}
	
	protected static class HeaderViewHolder
			extends CellViewHolder
	{
		
		final TextView textView;
		
		public HeaderViewHolder(View itemView)
		{
			super(itemView);
			textView = (TextView)itemView;
		}
	}
	
	private static class ContentViewHolder
			extends CellViewHolder
	{
		
		final TextView textView;
		
		public ContentViewHolder(View itemView)
		{
			super(itemView);
			textView = (TextView)itemView;
		}
	}
	
	public static class SelectionPayload
	{
		final long timeMillis;
		
		public SelectionPayload(long timeMillis)
		{
			this.timeMillis = timeMillis;
		}
	}
	
	/**
	 * Interface to listen change in date
	 */
	public interface OutlookOnDayCellClicked
	{
		void onClick(View view);
	}
	
	private static final int VIEW_TYPE_HEADER  = 0;
	private static final int VIEW_TYPE_CONTENT = 1;
	private static final int SPANS_COUNT       = 7;
	
	// Constructor Initialization
	private final String[]       mWeekdays;
	private final int            mStartOffset;
	private final int            mDays;
	private       LayoutInflater mLayoutInflater;
	
	private int mSelectedPosition = RecyclerView.NO_POSITION;
	
	private OutlookOnDayCellClicked mDayCellClickHandler;
	private final Set<Integer> mEvents = new HashSet<>();
	private       Cursor mCursor;
	private final long   mBaseTimeMillis;
}
