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
    private TimerStatsFragment timerStatsFragment;
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
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
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
        timerStatsFragment.updateEventAdded();
    }

    @Override
    public void onEventRemoved(long eventMillis) {
        timerStatsFragment.updateEventRemoved(eventMillis);
    }

    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();

        timerFragment = (TimerFragment) fm.findFragmentByTag("timer_fragment");
        timerStatsFragment = (TimerStatsFragment) fm.findFragmentByTag("timer_stats_fragment");
        eventListFragment = (EventListFragment) fm.findFragmentByTag("event_list_fragment");

        if(timerFragment == null) {
            timerFragment = new TimerFragment();
            fm.beginTransaction().add(timerFragment, "timer_fragment").commit();
        }

        if(timerStatsFragment == null) {
            timerStatsFragment = new TimerStatsFragment();
            fm.beginTransaction().add(timerStatsFragment, "timer_stats_fragment").commit();
        }

        if(eventListFragment == null) {
            eventListFragment = new EventListFragment();
            fm.beginTransaction().add(eventListFragment, "event_list_fragment").commit();
        }

        timerFragment.init(this);
        timerStatsFragment.init(this);
        eventListFragment.init(this);
    }
}
