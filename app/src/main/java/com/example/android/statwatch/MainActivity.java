package com.example.android.statwatch;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.statwatch.eventComponents.EventsFragment;
import com.example.android.statwatch.statsComponents.StatsFragment;
import com.example.android.statwatch.timerComponents.TimerFragment;
import com.example.android.statwatch.utils.Resources;

import java.util.Objects;

import static com.example.android.statwatch.utils.Constants.EVENTS_FRAGMENT;
import static com.example.android.statwatch.utils.Constants.STATS_FRAGMENT;
import static com.example.android.statwatch.utils.Constants.TIMER_FRAGMENT;

public class MainActivity extends AppCompatActivity {

    private TimerFragment timerFragment;
    private StatsFragment statsFragment;
    private EventsFragment eventsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        new Resources(this);
        setupFragments();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clear:
                eventsFragment.deleteEvents();
                statsFragment.refresh();
                timerFragment.clearTimer();
                return true;

            case R.id.action_more:
                //todo start more activity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Start of public methods
     */

    public void refreshComponents() { //called when event is added
        statsFragment.refresh();
        eventsFragment.refresh();
    }

    public void removeEvent() {
        statsFragment.refresh();
    }

    public void undo() {
        timerFragment.undo();
        statsFragment.refresh();
    }

    /*
     * Start of private methods
     */

    private void setupFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        timerFragment = new TimerFragment();
        eventsFragment = new EventsFragment();
        statsFragment = new StatsFragment(); //todo implement new stats controller

        ft.add(timerFragment, TIMER_FRAGMENT)
                .add(eventsFragment, EVENTS_FRAGMENT)
                .add(statsFragment, STATS_FRAGMENT)
        .commit();
    }
}
