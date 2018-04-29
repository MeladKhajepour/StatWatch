package com.example.android.eventtimer;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.eventtimer.utils.Event;

public class MainActivity extends AppCompatActivity
        implements TimerFragment.AddEventListener, EventListFragment.RemoveEventListener {

    private TimerFragment timerFragment;
    private EventStatsFragment eventStatsFragment;
    private EventListFragment eventListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFragments();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_all:
                eventListFragment.removeAllEvents();
                eventStatsFragment.updateClearAllEvents();
                timerFragment.resetTimerIndex();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onEventReceived(Event event) {
        eventListFragment.addEvent(event);
        eventStatsFragment.updateEventAdded();
    }

    @Override
    public void onEventRemoved(long eventMillis) {
        eventStatsFragment.updateEventRemoved(eventMillis);
    }

    private void setupFragments() {
        String EVENT_STATS_FRAGMENT = "timer_stats_fragment";
        String TIMER_FRAGMENT = "timer_fragment";
        String EVENT_LIST_FRAGMENT = "event_list_fragment";

        FragmentManager fm = getSupportFragmentManager();

        timerFragment = (TimerFragment) fm.findFragmentByTag(TIMER_FRAGMENT);
        eventStatsFragment = (EventStatsFragment) fm.findFragmentByTag(EVENT_STATS_FRAGMENT);
        eventListFragment = (EventListFragment) fm.findFragmentByTag(EVENT_LIST_FRAGMENT);

        if(timerFragment == null) {
            timerFragment = new TimerFragment();
            fm.beginTransaction().add(timerFragment, TIMER_FRAGMENT).commit();
        }

        if(eventStatsFragment == null) {
            eventStatsFragment = new EventStatsFragment();
            fm.beginTransaction().add(eventStatsFragment, EVENT_STATS_FRAGMENT).commit();
        }

        if(eventListFragment == null) {
            eventListFragment = new EventListFragment();
            fm.beginTransaction().add(eventListFragment, EVENT_LIST_FRAGMENT).commit();
        }

        timerFragment.init(this);
        eventStatsFragment.init(this);
        eventListFragment.init(this);
    }
}
