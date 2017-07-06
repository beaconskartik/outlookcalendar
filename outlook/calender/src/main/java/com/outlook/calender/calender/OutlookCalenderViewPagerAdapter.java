package com.outlook.calender.calender;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookCalenderViewPagerAdapter extends PagerAdapter
{
    public OutlookCalenderViewPagerAdapter(OutlookMonthView.OnDateChangeListener dateChangeListener)
    {
        mDateChangeListener = dateChangeListener;
        int mid = ITEM_COUNT / 2;
        for (int i = 0; i < getCount(); i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, i - mid);
            mCalendars.add(calendar);
            mMonthViews.add(null);
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
    
    public Calendar getCalendar(int position)
    {
        return mCalendars.get(position);
    }
    
    public void shiftLeft()
    {
        for (int i = 0; i < getCount() - 2; i++)
        {
            Calendar first = mCalendars.remove(0);
            first.add(Calendar.MONTH, getCount());
            mCalendars.add(first);
        }
    
        for (int i = 0; i <= 2; i++)
        {
            bind(i);
        }
    }
    
    public void shiftRight()
    {
        for (int i = 0; i < getCount() - 2; i++)
        {
            Calendar last = mCalendars.remove(getCount() - 1);
            last.add(Calendar.MONTH, -getCount());
            mCalendars.add(0, last);
        }
        
        for (int i = 0; i <= 2; i++)
        {
            bind(getCount() - 1 - i);
        }
    }
    
    public void bind(int position)
    {
        if (mMonthViews.get(position) != null)
        {
            mMonthViews.get(position).setCalendar(mCalendars.get(position));
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
    
    public void setSelectedDay(int position, @NonNull Calendar selectedDay, boolean notifySelf)
    {
        mSelectedDay.set(selectedDay.get(Calendar.YEAR), selectedDay.get(Calendar.MONTH), selectedDay.get(Calendar.DAY_OF_MONTH));
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
    
    public Calendar getSelectedDay()
    {
        return mSelectedDay;
    }
    
    // Member Variables
    
    static final  int                    ITEM_COUNT   = 5;
    private final List<OutlookMonthView> mMonthViews  = new ArrayList<>(getCount());
    private final List<Calendar>         mCalendars   = new ArrayList<>(getCount());
    private final Calendar               mSelectedDay = Calendar.getInstance();
    private OutlookMonthView.OnDateChangeListener mDateChangeListener;
}

