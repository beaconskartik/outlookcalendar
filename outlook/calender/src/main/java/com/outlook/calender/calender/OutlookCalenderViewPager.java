package com.outlook.calender.calender;

import java.util.Calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.outlook.calender.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookCalenderViewPager
		extends ViewPager
{
    public OutlookCalenderViewPager(Context context)
    {
        this(context, null);
    }
    
    public OutlookCalenderViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    
        View child = mAdapter.getMothView(getCurrentItem());
        if (child != null)
        {
            child.measure(widthMeasureSpec, heightMeasureSpec);
            int height = child.getMeasuredHeight();
            setMeasuredDimension(getMeasuredWidth(), height);
        }
    }
    
    public void setOnChangeListener(OnChangeListener listener)
    {
        mListener = listener;
    }
    
    private void init()
    {
        mAdapter = new OutlookCalenderAdapter(mDateChangeListener);
        setAdapter(mAdapter);
    
        addOnPageChangeListener(new SimpleOnPageChangeListener()
        {
            public boolean mDragging = false; // indicate if page change is from user
        
            @Override
            public void onPageSelected(int position)
            {
                if (mDragging)
                {
                    toFirstDay(position);
                    notifyDayChange(mAdapter.getCalendar(position));
                }
                mDragging = false;
                
                if (getVisibility() != VISIBLE)
                {
                    onPageScrollStateChanged(SCROLL_STATE_IDLE);
                }
            }
        
            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    syncPages(getCurrentItem());
                }
                else if (state == SCROLL_STATE_DRAGGING)
                {
                    mDragging = true;
                }
            }
        });
        setCurrentItem(mAdapter.getCount() / 2);
    }
    
    private void toFirstDay(int position)
    {
        Calendar calendar = mAdapter.getCalendar(position);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        mAdapter.setSelectedDay(position, calendar, true);
    }
    
    private void notifyDayChange(@NonNull Calendar calendar)
    {
        if (mListener != null)
        {
            mListener.onSelectedDayChange(calendar);
        }
    }
    
    /**
     * shift and recycle pages if we are currently at last or first,
     * ensure that users can peek hidden pages on 2 sides
     *
     * @param position current item position
     */
    private void syncPages(int position)
    {
        int first = 0, last = mAdapter.getCount() - 1;
        if (position == last)
        {
            mAdapter.shiftLeft();
            setCurrentItem(first + 1, false);
        }
        else if (position == 0)
        {
            mAdapter.shiftRight();
            setCurrentItem(last - 1, false);
        }
        else
        {
            // rebind neighbours due to shifting
            if (position > 0)
            {
                mAdapter.bind(position - 1);
            }
            if (position < mAdapter.getCount() - 1)
            {
                mAdapter.bind(position + 1);
            }
        }
    }
    
    public void setSelectedDay(@NonNull Calendar selectedDay)
    {
        // notify active page and its neighbors
        int position = getCurrentItem();
        if (OutlookCalenderUtils.monthBefore(selectedDay, mAdapter.getSelectedDay()))
        {
            mAdapter.setSelectedDay(position - 1, selectedDay, true);
            setCurrentItem(position - 1, true);
        }
        else if (OutlookCalenderUtils.monthAfter(selectedDay, mAdapter.getSelectedDay()))
        {
            mAdapter.setSelectedDay(position + 1, selectedDay, true);
            setCurrentItem(position + 1, true);
        }
        else
        {
            mAdapter.setSelectedDay(position, selectedDay, true);
        }
    }
    
    public interface OnChangeListener
    {
        void onSelectedDayChange(@NonNull Calendar calendar);
    }
    
    // member variables
    private final OutlookMonthView.OnDateChangeListener mDateChangeListener = new OutlookMonthView.OnDateChangeListener()
    {
        @Override
        public void onSelectedDayChange(@NonNull Calendar calendar)
        {
            mAdapter.setSelectedDay(getCurrentItem(), calendar, false);
            if (mListener != null)
            {
                mListener.onSelectedDayChange(calendar);
            }
        }
    };
    private OnChangeListener       mListener;
    private OutlookCalenderAdapter mAdapter;
}

