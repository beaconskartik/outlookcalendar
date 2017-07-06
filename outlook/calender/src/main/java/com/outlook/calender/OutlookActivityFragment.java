package com.outlook.calender;

import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.outlook.calender.agenda.OutlookAgendaAdapter;
import com.outlook.calender.agenda.OutlookAgendaView;
import com.outlook.calender.calender.OutlookCalenderViewPager;
import com.outlook.calender.calender.OutlookCalenderViewPager.OnChangeListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class OutlookActivityFragment extends Fragment
{
    
    public OutlookActivityFragment()
    {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_outlook, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    
        mCalendarView = (OutlookCalenderViewPager) view.findViewById(R.id.calendar_view);
        mAgendaView = (OutlookAgendaView) view.findViewById(R.id.agenda_view);
        mToolbarCheckedTextView = (AppCompatCheckedTextView) view.findViewById(R.id.calender_toggle);
        
        init();
    }
    
    private void init()
    {
        // updating title
        updateTitle(Calendar.getInstance());
        
        mCalendarView.setOnChangeListener(new OnChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull Calendar calendar)
            {
               updateTitle(calendar);
            }
        });
        
        mToolbarCheckedTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mToolbarCheckedTextView.toggle();
                toggleCalendarView();
            }
        });
        
       // mOutlookAgendaCalenderManager = new OutlookAgendaCalenderManager(mToolbarCheckedTextView, mCalendarView, mAgendaView);
    }
    
    private void updateTitle(Calendar calendar)
    {
        final int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;
        final long millis = calendar.getTimeInMillis();
        mToolbarCheckedTextView.setText(DateUtils.formatDateRange(getActivity(), millis, millis, flags));
    }
    
    private void toggleCalendarView()
    {
        if (mToolbarCheckedTextView.isChecked())
        {
            mCalendarView.setVisibility(View.VISIBLE);
        }
        else
        {
            mCalendarView.setVisibility(View.GONE);
            
        }
    }
    
    private OutlookAgendaCalenderManager mOutlookAgendaCalenderManager;
    private OutlookCalenderViewPager mCalendarView;
    private OutlookAgendaView        mAgendaView;
    private OutlookAgendaAdapter     mAgendaAdapter;
    private AppCompatCheckedTextView mToolbarCheckedTextView;
}
