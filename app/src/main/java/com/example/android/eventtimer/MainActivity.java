package com.example.android.eventtimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.eventtimer.utils.Event;

public class MainActivity extends AppCompatActivity implements TimerFragment.OnEventAddedListener {

    public static final String PREFS = "prefs";
    public static final String EVENTS = "events";
    public static final String INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //retrieve event from timer fragment and send to list fragment
    @Override
    public void onEventAdded(Event event) {
        EventListFragment fragment = (EventListFragment) getSupportFragmentManager().
                findFragmentById(R.id.event_list_fragment);

        fragment.addEvent(event);
    }
}
