package com.outlook.calender;

import java.util.Calendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.outlook.calender.calender.OutlookCalender;
import com.outlook.calender.calender.OutlookCalender.OnChangeListener;

public class OutlookActivity extends AppCompatActivity
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlook);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    
        getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        
        init();
    }
    
    private void init()
    {
        mToolbarCheckedTextView = (AppCompatCheckedTextView)findViewById(R.id.toolbar_toggle);
        mToolbarCheckedTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mToolbarCheckedTextView.toggle();
                toggleCalendarView();
            }
        });
        
        mCalendarView = (OutlookCalender)findViewById(R.id.calendar_view);
        mCalendarView.setOnChangeListener(new OnChangeListener()
        {
            @Override
            public void onSelectedMonthChange(@NonNull Calendar calendar)
            {
                updateTitle(calendar);
            }
        });
    }
    
    private void updateTitle(Calendar calendar)
    {
        final int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;
        final long millis = calendar.getTimeInMillis();
        mToolbarCheckedTextView.setText(DateUtils.formatDateRange(OutlookActivity.this, millis, millis, flags));
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_outlook, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        
        if (id == R.id.action_settings)
        {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private AppCompatCheckedTextView mToolbarCheckedTextView;
    private OutlookCalender mCalendarView;
}
