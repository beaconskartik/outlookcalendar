package com.outlook.calender.calender;

import android.os.Bundle;
import android.os.Parcelable;
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

public class OutlookCalenderAdapter extends PagerAdapter
{
    public OutlookCalenderAdapter()
    {
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
        mMonthViews.set(position, view);
        container.addView(view);
        bind(position);
        return view;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
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
    
    @Override
    public Parcelable saveState()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_MONTH, mCalendars.get(0).get(Calendar.MONTH));
        bundle.putInt(STATE_YEAR, mCalendars.get(0).get(Calendar.YEAR));
        return bundle;
    }
    
    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {
        Bundle savedState = (Bundle)state;
        if (savedState == null)
        {
            return;
        }
        
        int month = savedState.getInt(STATE_MONTH);
        int year = savedState.getInt(STATE_YEAR);
        for (int i = 0; i < getCount(); i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1);
            calendar.add(Calendar.MONTH, i);
            mCalendars.set(i, calendar);
        }
    }
    
    
    public OutlookMonthView getMothView(int position)
    {
        return  mMonthViews.get(position);
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
    }
    
    // Member Variables
    private static final String                 STATE_MONTH = "state:month";
    private static final String                 STATE_YEAR  = "state:year";
    static final         int                    ITEM_COUNT  = 5;
    private final        List<OutlookMonthView> mMonthViews = new ArrayList<>(getCount());
    private final        List<Calendar>         mCalendars  = new ArrayList<>(getCount());
}

