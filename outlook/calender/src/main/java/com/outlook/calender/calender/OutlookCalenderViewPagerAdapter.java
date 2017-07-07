package com.outlook.calender.calender;

import android.database.ContentObserver;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.outlook.calender.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookCalenderViewPagerAdapter extends PagerAdapter
{
    public OutlookCalenderViewPagerAdapter(OutlookMonthView.OnDateChangeListener dateChangeListener)
    {
        mDateChangeListener = dateChangeListener;
        int mid = ITEM_COUNT / 2;
        long todayMillis = OutlookCalenderUtils.monthFirstDay(OutlookCalenderUtils.today());
        for (int i = 0; i < getCount(); i++)
        {
            mCalendars.add(OutlookCalenderUtils.addMonths(todayMillis, i - mid));
            mMonthViews.add(null);
            mCursors.add(null);
        }
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        OutlookMonthView view = new OutlookMonthView(container.getContext());
        view.setLayoutParams(new ViewPager.LayoutParams());
        view.setOnDateChangeListener(mDateChangeListener);
        mMonthViews.set(position, view);
        container.addView(view);
        bind(position);
        return view;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        // Removing listener
        ((OutlookMonthView)object).setOnDateChangeListener(null);
        container.removeView((View)object);
    }
    
    @Override
    public int getCount()
    {
        return ITEM_COUNT;
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view.equals(object);
    }
    
    public OutlookMonthView getMothView(int position)
    {
        return mMonthViews.get(position);
    }
    
    void shiftLeft()
    {
        for (int i = 0; i < getCount() - 2; i++)
        {
            mCalendars.add(OutlookCalenderUtils.addMonths(mCalendars.remove(0), getCount()));
        }
        // TODO only deactivate non reusable cursors
        for (int i = 0; i < getCount(); i++)
        {
            swapCursor(i, null, null);
        }
        // rebind current item (2nd) and 2 adjacent items
        for (int i = 0; i <= 2; i++)
        {
            bind(i);
        }
    }
    
    void shiftRight()
    {
        for (int i = 0; i < getCount() - 2; i++)
        {
            mCalendars.add(0, OutlookCalenderUtils.addMonths(mCalendars.remove(getCount() - 1), -getCount()));
            mCursors.add(0, mCursors.remove(getCount() - 1));
        }
        // TODO only deactivate non reusable cursors
        for (int i = 0; i < getCount(); i++)
        {
            swapCursor(i, null, null);
        }
        // rebind current item (2nd to last) and 2 adjacent items
        for (int i = 0; i <= 2; i++)
        {
            bind(getCount() - 1 - i);
        }
    }
    
    public void bind(int position)
    {
        if (mMonthViews.get(position) != null)
        {
            mMonthViews.get(position).setMonthMillis(mCalendars.get(position));
        }
        bindSelectedDay(position);
    }
    
    private void bindSelectedDay(int position)
    {
        if (mMonthViews.get(position) != null)
        {
            mMonthViews.get(position).setSelectedDay(mSelectedDay);
        }
    }
    
    public void setSelectedDay(int position, @NonNull long selectedDay, boolean notifySelf)
    {
        mSelectedDay = selectedDay;
        if (notifySelf)
        {
            bindSelectedDay(position);
        }
        
        if (position > 0)
        {
            bindSelectedDay(position - 1);
        }
        
        if (position < getCount() - 1)
        {
            bindSelectedDay(position + 1);
        }
    }
    
    void swapCursor(long monthMillis, @Nullable Cursor cursor, ContentObserver contentObserver)
    {
        for (int i = 0; i < mCalendars.size(); i++) {
            if (OutlookCalenderUtils.sameMonth(monthMillis, mCalendars.get(i)))
            {
                swapCursor(i, cursor, contentObserver);
                break;
            }
        }
    }
    
    private void bindCursor(int position)
    {
        if (mCursors.get(position) != null && mMonthViews.get(position) != null)
        {
            mMonthViews.get(position).swapCursor(mCursors.get(position));
        }
    }
    
    public void deactivate(Cursor cursor)
    {
        if (cursor != null)
        {
            cursor.unregisterContentObserver(mObservers.get(cursor));
            mObservers.remove(cursor);
            cursor.close();
        }
    }
    
    public Cursor getCursor(int position)
    {
        return mCursors.get(position);
    }
    
    long getMonth(int position)
    {
        return mCalendars.get(position);
    }
    
    public long getSelectedDay()
    {
        return mSelectedDay;
    }
    
    // Member Variables
    
    static final  int                               ITEM_COUNT   = 5;
    private final List<OutlookMonthView>            mMonthViews  = new ArrayList<>(getCount());
    private final List<Long>                        mCalendars   = new ArrayList<>(getCount());
    private       long                              mSelectedDay = OutlookCalenderUtils.today();
    private final List<Cursor>                      mCursors     = new ArrayList<>(getCount());
    private final ArrayMap<Cursor, ContentObserver> mObservers   = new ArrayMap<>(getCount());
    private OutlookMonthView.OnDateChangeListener mDateChangeListener;
}

