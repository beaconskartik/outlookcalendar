package com.outlook.calender.agenda;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.util.Pair;
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
import com.outlook.calender.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookAgendaAdapter extends Adapter<AgendaViewHolder>
{
	public OutlookAgendaAdapter(Context context)
	{
		mContext = context;
		mInflater = LayoutInflater.from(context);
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
				viewHolder = new ContentViewHolder(mInflater.inflate(R.layout.agenda_item_view, viewGroup, false));
				break;
			}
		}
		return viewHolder;
	}
	
	@Override
	public void onBindViewHolder(AgendaViewHolder agendaViewHolder, int position)
	{
		if (mLock)
		{
			return;
		}
		if (agendaViewHolder instanceof HeaderViewHolder)
		{
			loadEvents(position);
		}
		OutlookAgendaItem item = getAdapterItem(position);
		if (item instanceof OutlookAgendaNoEvent)
		{
			agendaViewHolder.textView.setText(R.string.no_event);
		}
		else
		{
			agendaViewHolder.textView.setText(item.mTitle);
		}
	}
	
	@Override
	public int getItemCount()
	{
		return mEventGroups.groupAndChildrenSize();
	}
	
	@Override
	public int getItemViewType(int position)
	{
		if (getAdapterItem(position) instanceof OutlookAgendaEventGroup)
		{
			return VIEW_TYPE_HEADER;
		}
		else
		{
			return VIEW_TYPE_CONTENT;
		}
	}
	
	protected void loadEvents(long timeMillis)
	{
		// override to load events
	}
	
	/**
	 * Binds events for given day, each event should either
	 * start and end within the day,
	 * or starts before and end within of after the day.
	 * Bound cursor should be deactivated via {@link #deactivate()} when appropriate
	 *
	 * @param timeMillis time in millis that represents day in agenda
	 * @param cursor     {@link android.provider.CalendarContract.Events} cursor
	 * @see {@link #loadEvents(long)}
	 * @see {@link #deactivate()}
	 */
	public final void bindEvents(long timeMillis, Cursor cursor)
	{
		Pair<OutlookAgendaEventGroup, Integer> pair = findGroup(timeMillis);
		if (pair != null)
		{
			pair.first.setCursor(cursor, mEventObserver);
			notifyEventsChanged(pair.first, pair.second);
		}
	}
	
	/**
	 * Closes bound cursors and unregisters their observers
	 * that have been previously bound by {@link #bindEvents(long, Cursor)}
	 *
	 * @see {@link #bindEvents(long, Cursor)}
	 */
	public final void deactivate()
	{
		mEventGroups.clear();
	}
	
	/**
	 * Gets adapter position for given day, prepends or appends days
	 * to the list if out of range
	 *
	 * @param context    resources provider
	 * @param timeMillis time in milliseconds representing given day
	 * @return adapter position or {@link RecyclerView#NO_POSITION} if not a valid day (no time)
	 */
	int getPosition(Context context, long timeMillis)
	{
		if (timeMillis < mEventGroups.get(0).mTimeMillis)
		{
			while (timeMillis < mEventGroups.get(0).mTimeMillis)
			{
				prepend(context);
			}
		}
		else if (timeMillis > mEventGroups.get(mEventGroups.size() - 1).mTimeMillis)
		{
			while (timeMillis > mEventGroups.get(mEventGroups.size() - 1).mTimeMillis)
			{
				append(context);
			}
		}
		Pair<OutlookAgendaEventGroup, Integer> pair = findGroup(timeMillis);
		if (pair == null)
		{
			return RecyclerView.NO_POSITION;
		}
		return pair.second;
	}
	
	OutlookAgendaItem getAdapterItem(int position)
	{
		return mEventGroups.getGroupOrItem(position);
	}
	
	/**
	 * Adds days to beginning of this adapter data set
	 * Added days should immediately precede current adapter days.
	 * Last days in adapter may be pruned to keep its size constantly small.
	 *
	 * @param context resources provider
	 * @see {@link #append(Context)}
	 */
	void prepend(Context context)
	{
		long daysMillis = mEventGroups.size() * DateUtils.DAY_IN_MILLIS;
		int count = MONTH_SIZE, inserted = 0;
		for (int i = 0; i < count; i++)
		{
			OutlookAgendaEventGroup last = mEventGroups.get(mEventGroups.size() - 1 - i);
			OutlookAgendaEventGroup first = new OutlookAgendaEventGroup(context, last.mTimeMillis - daysMillis);
			inserted += first.itemCount() + 1;
			mEventGroups.add(0, first);
		}
		notifyItemRangeInserted(0, inserted);
		prune(false);
	}
	
	/**
	 * Adds days to end of this adapter data set
	 * Added days should immediately succeed current adapter days.
	 * First days in adapter may be pruned to keep its size constantly small.
	 *
	 * @param context resources provider
	 * @see {@link #prepend(Context)}
	 */
	void append(Context context)
	{
		int count = MONTH_SIZE;
		if (mEventGroups.isEmpty())
		{
			long today = OutlookCalenderUtils.stripTime(Calendar.getInstance()).getTimeInMillis();
			for (int i = 0; i < count; i++)
			{
				mEventGroups.add(new OutlookAgendaEventGroup(context, today + DateUtils.DAY_IN_MILLIS * i));
			}
		}
		else
		{
			long daysMillis = mEventGroups.size() * DateUtils.DAY_IN_MILLIS;
			int inserted = 0;
			for (int i = 0; i < count; i++)
			{
				OutlookAgendaEventGroup first = mEventGroups.get(i);
				OutlookAgendaEventGroup last = new OutlookAgendaEventGroup(context, first.mTimeMillis + daysMillis);
				inserted += last.itemCount() + 1;
				mEventGroups.add(last);
			}
			notifyItemRangeInserted(getItemCount() - inserted + 1, inserted);
			prune(true);
		}
	}
	
	/**
	 * Temporarily locks view holder binding until {@link #unlockBinding()} is called.
	 * This can be used in case {@link RecyclerView} is being scrolled and binding
	 * needs to be disabled temporarily to prevent scroll offset changes
	 *
	 * @see {@link #unlockBinding()}
	 */
	void lockBinding()
	{
		mLock = true;
	}
	
	/**
	 * Unlocks view holder binding that may have been previously locked by {@link #lockBinding()},
	 * notifying adapter to rebind view holders as a result
	 *
	 * @see {@link #loadEvents(long)}
	 */
	void unlockBinding()
	{
		mLock = false;
		notifyDataSetChanged();
	}
	
	private Pair<OutlookAgendaEventGroup, Integer> findGroup(long timeMillis)
	{
		int position = 0;
		// TODO improve searching
		for (int i = 0; i < mEventGroups.size(); i++)
		{
			OutlookAgendaEventGroup group = mEventGroups.get(i);
			if (group.mTimeMillis == timeMillis)
			{
				return Pair.create(group, position);
			}
			else
			{
				position += group.itemCount() + 1;
			}
		}
		return null;
	}
	
	private void notifyEventsChanged(OutlookAgendaEventGroup group, int position)
	{
		int lastCount = group.mLastCursorCount, newCount = group.mCursor.getCount(), refreshCount = Math.min(newCount,
				lastCount), diff = newCount - lastCount;
		// either last or current count is 0
		// we need to swap no event placeholder
		// and insert/remove the rest - 1 positions
		if (refreshCount == 0)
		{
			refreshCount = 1;
			diff = Math.max(--diff, 0);
		}
		notifyItemRangeChanged(position + 1, refreshCount);
		if (diff > 0)
		{
			notifyItemRangeInserted(position + 1 + refreshCount, diff);
		}
		else if (diff < 0)
		{
			notifyItemRangeRemoved(position + 1 + refreshCount, -diff);
		}
		group.mLastCursorCount = newCount;
	}
	
	private void loadEvents(int position)
	{
		OutlookAgendaEventGroup group = (OutlookAgendaEventGroup)getAdapterItem(position);
		if (group.mCursor == null)
		{
			loadEvents(group.mTimeMillis);
		}
	}
	
	private void prune(boolean start)
	{
		if (mEventGroups.size() <= MAX_SIZE)
		{
			return;
		}
		int removed = 0, index = start ? 0 : MAX_SIZE;
		while (mEventGroups.size() > MAX_SIZE)
		{
			removed += mEventGroups.get(index).itemCount() + 1;
			mEventGroups.remove(index);
		}
		notifyItemRangeRemoved(start ? 0 : getItemCount(), removed);
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
	
	public static final int MONTH_SIZE = 31;
	
	private Context mContext;
	static final         int MAX_SIZE          = MONTH_SIZE * 2;
	private static final int VIEW_TYPE_HEADER  = 0;
	private static final int VIEW_TYPE_CONTENT = 1;
	
	private final OutlookAgendaEventGroup.EventObserver mEventObserver = new OutlookAgendaEventGroup.EventObserver()
	{
		@Override
		public void onChange(long timeMillis)
		{
			loadEvents(timeMillis);
		}
	};
	private final OutlookAgendaEventList                mEventGroups   = new OutlookAgendaEventList(MONTH_SIZE);
	private final LayoutInflater mInflater;
	private       boolean        mLock;
}
