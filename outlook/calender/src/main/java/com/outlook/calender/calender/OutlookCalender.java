package com.outlook.calender.calender;

import java.util.Calendar;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CalendarView;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookCalender extends ViewPager
{
    public OutlookCalender(Context context)
    {
        this(context, null);
    }
    
    public OutlookCalender(Context context, AttributeSet attrs)
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
        if (listener != null)
        {
            listener.onSelectedMonthChange(mAdapter.getCalendar(getCurrentItem()));
        }
    }
    
    private void init()
    {
        mAdapter = new OutlookCalenderAdapter();
        setAdapter(mAdapter);
        addOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                
            }
            
            @Override
            public void onPageSelected(int position)
            {
                if (mListener != null)
                {
                    mListener.onSelectedMonthChange(mAdapter.getCalendar(position));
                }
            }
            
            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (state == ViewPager.SCROLL_STATE_IDLE)
                {
                    int position = getCurrentItem();
                    int first = 0;
                    int last = mAdapter.getCount() - 1;
                    
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
                        mAdapter.bind(position - 1);
                        mAdapter.bind(position + 1);
                    }
                }
            }
        });
        setCurrentItem(mAdapter.getCount() / 2);
    }
    
    public interface OnChangeListener
    {
        void onSelectedMonthChange(@NonNull Calendar calendar);
    }
    
    // member variables
    private OnChangeListener mListener;
    private OutlookCalenderAdapter mAdapter;
}

