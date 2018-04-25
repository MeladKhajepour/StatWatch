package com.example.android.eventtimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.eventtimer.utils.Event;

public class MainActivity extends AppCompatActivity implements TimerFragment.OnEventAddedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onEventAdded(Event event) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        EventListFragment eventListFragment = (EventListFragment) fm.findFragmentById(R.id.event_list_fragment);
        TimerStatsFragment timerStatsFragment = (TimerStatsFragment) fm.findFragmentById(R.id.timer_stats_fragment);

        eventListFragment.addEvent(event);
        timerStatsFragment.updateStats(event);
    }
}
