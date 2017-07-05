package com.outlook.calender.agenda;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.support.v7.text.AllCapsTransformationMethod;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.outlook.calender.R;
import com.outlook.calender.agenda.OutlookAgendaAdapter.AgendaViewHolder;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookAgendaAdapter extends Adapter<AgendaViewHolder>
{
	public OutlookAgendaAdapter(Context context)
	{
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mBaseTimeMillis = Calendar.getInstance().getTimeInMillis();
		
		generate(context, mPrevMonth, -MONTH_SIZE);
		generate(context, mCurrMonth, 0);
		generate(context, mNextMonth, MONTH_SIZE);
	}
	
	@Override
	public AgendaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		AgendaViewHolder viewHolder = null;
		switch (viewType)
		{
			case VIEW_TYPE_HEADER:
			{
				viewHolder = new HeaderViewHolder(mInflater.inflate(R.layout.agenda_header_view, viewGroup, false));
				break;
			}
			case VIEW_TYPE_CONTENT:
			default:
			{
				viewHolder =  new ContentViewHolder(mInflater.inflate(R.layout.agenda_item_view, viewGroup, false));
			}
		}
		return viewHolder;
	}
	
	@Override
	public void onBindViewHolder(AgendaViewHolder agendaViewHolder, int position)
	{
		agendaViewHolder.textView.setText(getItem(position).mTitle);
		if (position == 0)
		{
			postPrepend(agendaViewHolder.textView.getContext());
		}
		else if (position == getItemCount() - 1)
		{
			postAppend(agendaViewHolder.textView.getContext());
		}
	}
	
	@Override
	public int getItemCount()
	{
		return mPrevMonth.size() + mCurrMonth.size() + mNextMonth.size();
	}
	
	@Override
	public int getItemViewType(int position)
	{
		if (getItem(position) instanceof OutlookAgendaItemHeader)
		{
			return VIEW_TYPE_HEADER;
		}
		else
		{
			return VIEW_TYPE_CONTENT;
		}
	}
	
	int getPosition(Context context, long timeMillis)
	{
		int start, end;
		if (timeMillis < getItem(0).mTimeMillis)
		{
			prepend(context);
			start = 0;
			end = mPrevMonth.size();
		}
		else if (timeMillis > getItem(getItemCount() - 1).mTimeMillis)
		{
			append(context);
			start = mPrevMonth.size() + mCurrMonth.size();
			end = getItemCount();
		}
		else
		{
			start = 0;
			end = getItemCount();
		}
		
		for (int i = start; i < end; i++)
		{
			if (getItem(i).mTimeMillis == timeMillis)
			{
				return i;
			}
		}
		return -1;
	}
	
	public OutlookAgendaItem getItem(int position)
	{
		if (position < mPrevMonth.size())
		{
			return mPrevMonth.get(position);
		}
		if (position < mPrevMonth.size() + mCurrMonth.size())
		{
			return mCurrMonth.get(position - mPrevMonth.size());
		}
		return mNextMonth.get(position - mCurrMonth.size() - mPrevMonth.size());
	}
	
	private void postPrepend(final Context context)
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				prepend(context);
			}
		});
	}
	
	private void prepend(Context context)
	{
		notifyItemRangeRemoved(mPrevMonth.size() + mCurrMonth.size(), mNextMonth.size());
		ArrayList<OutlookAgendaItem> prepended = mNextMonth;
		mNextMonth = mCurrMonth;
		mCurrMonth = mPrevMonth;
		mPrevMonth = prepended;
		mBaseTimeMillis -= DateUtils.DAY_IN_MILLIS * MONTH_SIZE;
		generate(context, prepended, -MONTH_SIZE);
		notifyItemRangeInserted(0, prepended.size());
	}
	
	private void postAppend(final Context context)
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				append(context);
			}
		});
	}
	
	private void append(Context context)
	{
		notifyItemRangeRemoved(0, mPrevMonth.size());
		ArrayList<OutlookAgendaItem> appended = mPrevMonth;
		mPrevMonth = mCurrMonth;
		mCurrMonth = mNextMonth;
		mNextMonth = appended;
		mBaseTimeMillis += DateUtils.DAY_IN_MILLIS * MONTH_SIZE;
		generate(context, appended, MONTH_SIZE);
		notifyItemRangeInserted(mPrevMonth.size() + mCurrMonth.size(), appended.size());
	}
	
	private void generate(Context context, List<OutlookAgendaItem> list, int offset)
	{
		list.clear();
		for (int i = offset; i < offset + MONTH_SIZE; i++)
		{
			long timeMillis = mBaseTimeMillis + DateUtils.DAY_IN_MILLIS * i;
			list.add(new OutlookAgendaItemHeader(context, timeMillis));
			list.add(new OutlookAgendaItemContent(context, timeMillis));
		}
	}
	
	public static class AgendaViewHolder
			extends RecyclerView.ViewHolder
	{
		public AgendaViewHolder(View itemView)
		{
			super(itemView);
			textView = (TextView)itemView;
		}
		
		final TextView textView;
	}
	
	static class HeaderViewHolder
			extends AgendaViewHolder
	{
		
		public HeaderViewHolder(View itemView)
		{
			super(itemView);
			textView.setTransformationMethod(new AllCapsTransformationMethod(textView.getContext()));
		}
	}
	
	static class ContentViewHolder
			extends AgendaViewHolder
	{
		
		public ContentViewHolder(View itemView)
		{
			super(itemView);
		}
	}
	
	public static final int MONTH_SIZE        = 31;
	
	private Context mContext;
	private static final int VIEW_TYPE_HEADER  = 0;
	private static final int VIEW_TYPE_CONTENT = 1;
	
	private ArrayList<OutlookAgendaItem> mCurrMonth = new ArrayList<>(MONTH_SIZE * 2);
	private ArrayList<OutlookAgendaItem> mPrevMonth = new ArrayList<>(MONTH_SIZE * 2);
	private ArrayList<OutlookAgendaItem> mNextMonth = new ArrayList<>(MONTH_SIZE * 2);
	
	private       long           mBaseTimeMillis; // start day of rmiddle block
	private final LayoutInflater mInflater;
	private final Handler mHandler = new Handler();
}
