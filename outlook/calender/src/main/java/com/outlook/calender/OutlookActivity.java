package com.outlook.calender;

import java.util.Calendar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.outlook.calender.agenda.OutlookAgendaAdapter;
import com.outlook.calender.agenda.OutlookAgendaCursorAdapter;
import com.outlook.calender.agenda.OutlookAgendaView;
import com.outlook.calender.calender.OutlookCalendarCursorAdapter;
import com.outlook.calender.calender.OutlookCalenderViewPager;

public class OutlookActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlook);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    
        mCalendarView = (OutlookCalenderViewPager)findViewById(R.id.calendar_view);
        mAgendaView = (OutlookAgendaView)findViewById(R.id.agenda_view);
        mToolbarCheckedTextView = (AppCompatCheckedTextView)findViewById(R.id.calender_toggle);
        
        init();
    }
    
    private void init()
    {
        // updating title
        updateTitle(Calendar.getInstance());
        
        mToolbarCheckedTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mToolbarCheckedTextView.toggle();
                toggleCalendarView();
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_outlook, menu);
        return true;
    }
    
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    
        mOutlookAgendaCalenderManager = new OutlookAgendaCalenderManager(mToolbarCheckedTextView, mCalendarView, mAgendaView);
        
        if (checkPermissions())
        {
            loadEvents();
        }
        else
        {
            requestPermissions();
        }
    }
    
    @Override
    public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
    }
    
    private boolean checkPermissions()
    {
        return (checkPermission(Manifest.permission.READ_CALENDAR) | checkPermission(Manifest.permission.WRITE_CALENDAR)) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 0);
    }
    
    protected int checkPermission(@NonNull String permission)
    {
        return ActivityCompat.checkSelfPermission(this, permission);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissions())
        {
            loadEvents();
        }
        else
        {
            Toast.makeText(this, "Please provide the Read Calender Permission to Run this App", Toast.LENGTH_SHORT).show();
        }
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
    
    public void loadEvents()
    {
        mCalendarView.setCalendarAdapter(new OutlookCalendarCursorAdapter(this));
        mAgendaView.setAdapter(new OutlookAgendaCursorAdapter(this));
    }
    
    private void updateTitle(Calendar calendar)
    {
        final int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;
        final long millis = calendar.getTimeInMillis();
        mToolbarCheckedTextView.setText(DateUtils.formatDateRange(this, millis, millis, flags));
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
    private OutlookCalenderViewPager     mCalendarView;
    private OutlookAgendaView            mAgendaView;
    private OutlookAgendaAdapter         mAgendaAdapter;
    private AppCompatCheckedTextView     mToolbarCheckedTextView;
}
