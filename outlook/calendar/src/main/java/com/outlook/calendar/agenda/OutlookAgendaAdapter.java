package com.outlook.calendar.agenda;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.text.AllCapsTransformationMethod;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.outlook.calendar.OutlookEventCursor;
import com.outlook.calender.R;
import com.outlook.calendar.agenda.OutlookAgendaAdapter.AgendaViewHolder;
import com.outlook.calendar.agenda.OutlookAgendaEventGroup.OutlookEventObserver;
import com.outlook.calendar.utils.OutlookCalenderUtils;
import com.outlook.calendar.weather.OutlookWeather;

/**
 * Created by ksachan on 7/4/17.
 */

public abstract class OutlookAgendaAdapter extends Adapter<AgendaViewHolder>
{
	public OutlookAgendaAdapter(Context context)
	{
		mInflater = LayoutInflater.from(context);
		mTransparentColor = ContextCompat.getColor(context, android.R.color.transparent);
		TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorTertiary});
		mIconTint = ta.getColor(0, 0);
		ta.recycle();
		TypedArray colors = context.getResources().obtainTypedArray(R.array.calendar_colors);
		if (colors.length() > 0)
		{
			mColors = new int[colors.length()];
			for (int i = 0; i < colors.length(); i++)
			{
				mColors[i] = colors.getColor(i, mTransparentColor);
			}
		}
		else
		{
			mColors = new int[]{mTransparentColor};
		}
		colors.recycle();
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
	public void onBindViewHolder(AgendaViewHolder holder, int position)
	{
		final OutlookAgendaItem item = getAdapterItem(position);
		bindTitle(item, holder);
		if (item instanceof OutlookAgendaEventGroup)
		{
			loadEvents(position);
			bindWeather((OutlookAgendaEventGroup)item, (HeaderViewHolder)holder);
		}
		else
		{
			bindTime((OutlookAgendaEventItem)item, (ContentViewHolder)holder);
			bindColor((OutlookAgendaEventItem)item, (ContentViewHolder)holder);
			holder.itemView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					
				}
			});
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
	
	/**
	 * Load event for given day
	 *
	 * @param timeMillis
	 */
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
	public final void bindEvents(long timeMillis, OutlookEventCursor cursor)
	{
		if (!mLock)
		{
			Pair<OutlookAgendaEventGroup, Integer> pair = findGroup(timeMillis);
			if (pair != null)
			{
				pair.first.setCursor(cursor, mOutlookEventObserver);
				notifyEventsChanged(pair.first, pair.second);
			}
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
		if (!mEventGroups.isEmpty())
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
		}
		Pair<OutlookAgendaEventGroup, Integer> pair = findGroup(timeMillis);
		if (pair == null)
		{
			return RecyclerView.NO_POSITION;
		}
		return pair.second;
	}
	
	public OutlookAgendaItem getAdapterItem(int position)
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
		int count = BLOCK_SIZE;
		final int[] inserted = new int[1];
		inserted[0] = 0;
		for (int i = 0; i < count; i++)
		{
			OutlookAgendaEventGroup last = mEventGroups.get(mEventGroups.size() - 1 - i);
			OutlookAgendaEventGroup first = new OutlookAgendaEventGroup(context, last.mTimeMillis - daysMillis);
			inserted[0] += first.itemCount() + 1;
			mEventGroups.add(0, first);
		}
		
		final Runnable runnable = new Runnable()
		{
			public void run()
			{
				notifyItemRangeInserted(0, inserted[0]);
			}
		};
		mHandler.post(runnable);
		
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
		int count = BLOCK_SIZE;
		if (mEventGroups.isEmpty())
		{
			long today = OutlookCalenderUtils.today();
			for (int i = -count; i < count; i++)
			{
				mEventGroups.add(new OutlookAgendaEventGroup(context, today + DateUtils.DAY_IN_MILLIS * i));
			}
		}
		else
		{
			long daysMillis = mEventGroups.size() * DateUtils.DAY_IN_MILLIS;
			final int[] inserted = new int[1];
			inserted[0] = 0;
			for (int i = 0; i < count; i++)
			{
				OutlookAgendaEventGroup first = mEventGroups.get(i);
				OutlookAgendaEventGroup last = new OutlookAgendaEventGroup(context, first.mTimeMillis + daysMillis);
				inserted[0] += last.itemCount() + 1;
				mEventGroups.add(last);
			}
			
			final Runnable runnable = new Runnable()
			{
				public void run()
				{
					notifyItemRangeInserted(getItemCount() - inserted[0] + 1, inserted[0]);
				}
			};
			mHandler.post(runnable);
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
		
		final Runnable runnable = new Runnable()
		{
			public void run()
			{
				notifyItemRangeChanged(0, getItemCount());
			}
		};
		mHandler.post(runnable);
	}
	
	void setWeather(@Nullable OutlookWeather weather)
	{
		mWeather = weather;
		notifyItemRangeChanged(0, getItemCount());
	}
	
	private void bindTitle(OutlookAgendaItem item, ViewHolder holder)
	{
		if (item instanceof OutlookAgendaEventGroup)
		{
			((HeaderViewHolder)holder).textView.setText(item.mTitle);
		}
		else if (item instanceof OutlookAgendaNoEvent)
		{
			((ContentViewHolder)holder).textViewTitle.setText(R.string.no_event);
		}
		else
		{
			((ContentViewHolder)holder).textViewTitle.setText(item.mTitle);
		}
	}
	
	private void bindTime(OutlookAgendaEventItem eventItem, ContentViewHolder contentHolder)
	{
		if (eventItem instanceof OutlookAgendaNoEvent)
		{
			contentHolder.textViewTime.setVisibility(View.GONE);
			return;
		}
		contentHolder.textViewTime.setVisibility(View.VISIBLE);
		Context context = contentHolder.textViewTime.getContext();
		switch (eventItem.mDisplayType)
		{
			case OutlookAgendaEventItem.DISPLAY_TYPE_ALL_DAY:
				contentHolder.textViewTime.setText(R.string.all_day);
				break;
			case OutlookAgendaEventItem.DISPLAY_TYPE_START_TIME:
			default:
				contentHolder.textViewTime.setText(OutlookCalenderUtils.toTimeString(context, eventItem.mStartTimeMillis));
				break;
			case OutlookAgendaEventItem.DISPLAY_TYPE_END_TIME:
				String endTimeString = OutlookCalenderUtils.toTimeString(context, eventItem.mEndTimeMillis);
				contentHolder.textViewTime.setText(context.getString(R.string.end_time, endTimeString));
				break;
		}
	}
	
	private void bindColor(OutlookAgendaEventItem item, ContentViewHolder holder)
	{
		if (item instanceof OutlookAgendaNoEvent)
		{
			holder.background.setBackgroundColor(mTransparentColor);
		}
		else
		{
			int color = mColors[(int)(Math.abs(item.mCalendarId) % mColors.length)];
			holder.background.setBackgroundColor(color);
		}
	}
	
	private void bindWeather(OutlookAgendaEventGroup groupItem, final HeaderViewHolder holder)
	{
		// bind weather for today and tomorrow if exist, hide UI otherwise
		if (groupItem.mTimeMillis == OutlookCalenderUtils.today() && mWeather != null && mWeather.today != null)
		{
			bindWeatherInfo(holder.textViewMorning, mWeather.today.morning);
			bindWeatherInfo(holder.textViewAfternoon, mWeather.today.afternoon);
			bindWeatherInfo(holder.textViewNight, mWeather.today.night);
			holder.weather.setVisibility(View.VISIBLE);
		}
		else if (groupItem.mTimeMillis == OutlookCalenderUtils.today() + DateUtils.DAY_IN_MILLIS && mWeather != null && mWeather.tomorrow != null)
		{
			bindWeatherInfo(holder.textViewMorning, mWeather.tomorrow.morning);
			bindWeatherInfo(holder.textViewAfternoon, mWeather.tomorrow.afternoon);
			bindWeatherInfo(holder.textViewNight, mWeather.tomorrow.night);
			holder.weather.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.weather.setVisibility(View.GONE);
		}
	}
	
	private void bindWeatherInfo(TextView textView, OutlookWeather.WeatherInfo info)
	{
		Drawable icon = info.getIcon(textView.getContext(), mIconTint);
		textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		if (info.temperature != null)
		{
			textView.setText(textView.getContext().getString(R.string.fahrenheit, info.temperature));
		}
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
		
		final int[] start = new int[1];
		final int[] end = new int[1];
		
		start[0] = position + 1;
		end[0] = refreshCount;
		
		if (diff > 0)
		{
			start[0] = position + 1 + refreshCount;
			end[0] = diff;
		}
		else if (diff < 0)
		{
			start[0] = position + 1 + refreshCount;
			end[0] = -diff;
		}
		
		final Runnable runnable = new Runnable()
		{
			public void run()
			{
				notifyItemRangeChanged(start[0], end[0]);
			}
		};
		mHandler.post(runnable);
		group.mLastCursorCount = newCount;
	}
	
	private void loadEvents(int position)
	{
		if (!mLock)
		{
			OutlookAgendaEventGroup group = (OutlookAgendaEventGroup)getAdapterItem(position);
			if (group.mCursor == null)
			{
				loadEvents(group.mTimeMillis);
			}
		}
	}
	
	private void prune(final boolean start)
	{
		if (mEventGroups.size() <= MAX_SIZE)
		{
			return;
		}
		final int[] removed = new int[1];
		removed[0] = 0;
		int index = start ? 0 : MAX_SIZE;
		while (mEventGroups.size() > MAX_SIZE)
		{
			removed[0] += mEventGroups.get(index).itemCount() + 1;
			mEventGroups.remove(index);
		}
		
		final Runnable runnable = new Runnable()
		{
			public void run()
			{
				notifyItemRangeRemoved(start ? 0 : getItemCount(), removed[0]);
			}
		};
		mHandler.post(runnable);
	}
	
	public static class AgendaViewHolder
			extends RecyclerView.ViewHolder
	{
		public AgendaViewHolder(View itemView)
		{
			super(itemView);
		}
	}
	
	static class HeaderViewHolder
			extends AgendaViewHolder
	{
		final TextView textView;
		final TextView textViewMorning;
		final TextView textViewAfternoon;
		final TextView textViewNight;
		final View     weather;
		
		public HeaderViewHolder(View itemView)
		{
			super(itemView);
			textView = (TextView)itemView.findViewById(R.id.text_view_title);
			textView.setTransformationMethod(new AllCapsTransformationMethod(textView.getContext()));
			weather = itemView.findViewById(R.id.weather);
			textViewMorning = (TextView)itemView.findViewById(R.id.text_view_morning);
			textViewAfternoon = (TextView)itemView.findViewById(R.id.text_view_afternoon);
			textViewNight = (TextView)itemView.findViewById(R.id.text_view_night);
		}
	}
	
	static class ContentViewHolder
			extends AgendaViewHolder
	{
		final TextView textViewTitle;
		final TextView textViewTime;
		final View     background;
		
		public ContentViewHolder(View itemView)
		{
			super(itemView);
			textViewTitle = (TextView)itemView.findViewById(R.id.text_view_title);
			textViewTime = (TextView)itemView.findViewById(R.id.text_view_time);
			background = itemView.findViewById(R.id.background);
		}
	}
	
	public static final int MONTH_SIZE = 31;
	
	private Context mContext;
	private static final int BLOCK_SIZE        = MONTH_SIZE * 2;
	private static final int MAX_SIZE          = MONTH_SIZE * 3;
	private static final int VIEW_TYPE_HEADER  = 0;
	private static final int VIEW_TYPE_CONTENT = 1;
	
	private final OutlookEventObserver   mOutlookEventObserver = new OutlookEventObserver()
	{
		@Override
		public void onChange(long timeMillis)
		{
			if (!mLock)
			{
				loadEvents(timeMillis);
			}
		}
	};
	private final OutlookAgendaEventList mEventGroups          = new OutlookAgendaEventList(BLOCK_SIZE);
	private final LayoutInflater mInflater;
	private       boolean        mLock;
	private Handler mHandler = new Handler();
	private final int            mTransparentColor;
	private final int            mIconTint;
	private final int            mColors[];
	private       OutlookWeather mWeather;
}
