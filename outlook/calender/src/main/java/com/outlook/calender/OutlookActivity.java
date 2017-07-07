package com.outlook.calender;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnScrollChangeListener;
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
        
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.hideOverflowMenu();
    
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mAppBarLayout.setExpanded(false, false);
                mMenuItemExpandCalendar.setVisible(true);
            }
        });
        
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        mCalendarView = (OutlookCalenderViewPager)findViewById(R.id.calendar_view);
        mAgendaView = (OutlookAgendaView)findViewById(R.id.agenda_view);
    
        mAgendaView.setOnScrollChangeListener(new OnScrollChangeListener()
        {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                if (mMenuItemExpandCalendar != null)
                {
                    mMenuItemExpandCalendar.setVisible(true);
                }
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_outlook, menu);
        
        mMenuItemExpandCalendar = menu.findItem(R.id.expanded_calendar);
        mMenuItemExpandCalendar.setVisible(false);
        return true;
    }
    
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    
        mOutlookAgendaCalenderManager = new OutlookAgendaCalenderManager( mCalendarView,
                mAgendaView, mToolbar);
        
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
        else if (id == R.id.expanded_calendar)
        {
            mMenuItemExpandCalendar.setVisible(false);
            mAppBarLayout.setExpanded(true, true);
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    public void loadEvents()
    {
        mCalendarView.setCalendarAdapter(new OutlookCalendarCursorAdapter(this));
        mAgendaView.setAdapter(new OutlookAgendaCursorAdapter(this));
    }
    
    private MenuItem                     mMenuItemExpandCalendar;
    private CollapsingToolbarLayout     mCollapsingToolbarLayout;
    private Toolbar                      mToolbar;
    private AppBarLayout                 mAppBarLayout;
    private FloatingActionButton         mFab;
    private OutlookAgendaCalenderManager mOutlookAgendaCalenderManager;
    private OutlookCalenderViewPager     mCalendarView;
    private OutlookAgendaView            mAgendaView;
    private OutlookAgendaAdapter         mAgendaAdapter;
}
