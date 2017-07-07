package com.outlook.calender.calender;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.outlook.calender.utils.OutlookCalenderUtils;

/**
 * Created by ksachan on 7/4/17.
 */

public class OutlookCalenderViewPager extends ViewPager
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
        mAdapter = new OutlookCalenderViewPagerAdapter(mDateChangeListener);
        setAdapter(mAdapter);
        setCurrentItem(mAdapter.getCount() / 2);
    
        addOnPageChangeListener(new SimpleOnPageChangeListener()
        {
            public boolean mDragging = false;
            @Override
            public void onPageSelected(int position)
            {
                if (mDragging)
                {
                    toFirstDay(position);
                    notifyDayChange(mAdapter.getMonth(position));
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
        mAdapter.setSelectedDay(position,
                OutlookCalenderUtils.monthFirstDay(mAdapter.getMonth(position)), true);
    }
    
    private void notifyDayChange(@NonNull long calendar)
    {
        if (mListener != null)
        {
            mListener.onSelectedDayChange(calendar);
        }
    }
    
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
    
    public void setSelectedDay(long dayMillis)
    {
        // notify active page and its neighbors
        int position = getCurrentItem();
        if (OutlookCalenderUtils.monthBefore(dayMillis, mAdapter.getSelectedDay()))
        {
            mAdapter.setSelectedDay(position - 1, dayMillis, true);
            setCurrentItem(position - 1, true);
        }
        else if (OutlookCalenderUtils.monthAfter(dayMillis, mAdapter.getSelectedDay()))
        {
            mAdapter.setSelectedDay(position + 1, dayMillis, true);
            setCurrentItem(position + 1, true);
        }
        else
        {
            mAdapter.setSelectedDay(position, dayMillis, true);
        }
    }
    
    public void setCalendarAdapter(@NonNull OutlookCalendarCursorAdapter adapter)
    {
        mCalendarAdapter = adapter;
        mCalendarAdapter.setCalendarView(this);
        loadEvents(getCurrentItem());
    }
    
    public interface OnChangeListener
    {
        void onSelectedDayChange(@NonNull long calendar);
    }
    
    private void loadEvents(int position) {
        if (mCalendarAdapter != null && mAdapter.getCursor(position) == null) {
            mCalendarAdapter.loadEvents(mAdapter.getMonth(position));
        }
    }
    
    protected void swapCursor(long monthMillis, Cursor cursor) {
        mAdapter.swapCursor(monthMillis, cursor, new PagerContentObserver(monthMillis));
    }
    
    class PagerContentObserver extends ContentObserver
    {
        
        private final long monthMillis;
        
        public PagerContentObserver(long monthMillis) {
            super(new Handler());
            this.monthMillis = monthMillis;
        }
        
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
        
        @Override
        public void onChange(boolean selfChange) {
            // invalidate previous cursor for given month
            mAdapter.swapCursor(monthMillis, null, null);
            // reload events if given month is active month
            // hidden months will be reloaded upon being swiped to
            if (OutlookCalenderUtils.sameMonth(monthMillis, mAdapter.getMonth(getCurrentItem()))) {
                loadEvents(getCurrentItem());
            }
        }
    }
    
    // member variables
    private final OutlookMonthView.OnDateChangeListener mDateChangeListener = new OutlookMonthView.OnDateChangeListener()
    {
        @Override
        public void onSelectedDayChange(long dayMillis)
        {
            mAdapter.setSelectedDay(getCurrentItem(), dayMillis, false);
            notifyDayChange(dayMillis);
        }
    };
    private OnChangeListener                mListener;
    private OutlookCalenderViewPagerAdapter mAdapter;
    private OutlookCalendarCursorAdapter mCalendarAdapter;
}

