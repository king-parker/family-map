package com.kingpark.familymapclient.ui.activities;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kingpark.familymapclient.ui.fragments.MapFragment;

public class EventActivity extends FragmentActivity {
    static {
        TAG = "EventActivity";
    }
    
    public static final String ARG_EVENT_ID = "com.kingpark.familymapclient.event_event_id";
    public static final String ARG_SETTINGS = "com.kingpark.familymapclient.event_settings";
    
    public static Intent newIntent(Context packageContext,String eventId) {
        Intent intent = new Intent(packageContext,EventActivity.class);
        intent.putExtra(ARG_EVENT_ID,eventId);
        
        return intent;
    }
    
    @Override
    protected Fragment createFragment(Bundle args) {
        Intent eventIntent = getIntent();
        assert eventIntent != null && eventIntent.hasExtra(ARG_EVENT_ID);
        
        String focusEventId = eventIntent.getStringExtra(ARG_EVENT_ID);

        return MapFragment.newInstance(focusEventId);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}