package com.outlook.calender;

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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    
        if (checkPermissions())
        {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            
            if (fragment instanceof OutlookActivityFragment)
            {
                ((OutlookActivityFragment)fragment).loadEvents();
            }
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
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (fragment instanceof OutlookActivityFragment)
            {
                ((OutlookActivityFragment)fragment).loadEvents();
            }
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
    
    private AppCompatCheckedTextView mToolbarCheckedTextView;
}
