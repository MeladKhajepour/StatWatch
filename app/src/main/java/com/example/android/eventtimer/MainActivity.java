package com.example.android.eventtimer;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.android.eventtimer.utils.UpdateUIListener;

import static com.example.android.eventtimer.EventStatsFragment.STATS_EXPANSION;
import static com.example.android.eventtimer.utils.EventsManager.PREFS;

public class MainActivity extends AppCompatActivity implements UpdateUIListener {

    private TimerFragment timerFragment;
    private EventStatsFragment eventStatsFragment;
    private EventListFragment eventListFragment;

    private MenuItem recalculateStatsAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem autoUpdate = menu.findItem(R.id.action_auto_update);

        autoUpdate.setChecked(
                getSharedPreferences(PREFS, MODE_PRIVATE)
                        .getBoolean(EventStatsFragment.USE_LIST_STATS, false)
        );

        recalculateStatsAction = menu.findItem(R.id.action_recalculate_stats);

        recalculateStatsAction.setEnabled(!autoUpdate.isChecked());


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_clear_list:
                clearListAndRefresh();
                return true;

            case R.id.action_recalculate_stats:
                eventStatsFragment.recalculateListStats();
                return true;

            case R.id.action_auto_update:
                toggleAutoUpdate(item);
                return true;

            case R.id.action_reset_all:
                resetAll();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupFragments() {
        String EVENT_STATS_FRAGMENT = "timer_stats_fragment";
        String TIMER_FRAGMENT = "timer_fragment";
        String EVENT_LIST_FRAGMENT = "event_list_fragment";
        Bundle args = new Bundle();

        FragmentManager fm = getSupportFragmentManager();

        timerFragment = (TimerFragment) fm.findFragmentByTag(TIMER_FRAGMENT);
        eventStatsFragment = (EventStatsFragment) fm.findFragmentByTag(EVENT_STATS_FRAGMENT);
        eventListFragment = (EventListFragment) fm.findFragmentByTag(EVENT_LIST_FRAGMENT);

        if(timerFragment == null) {
            timerFragment = new TimerFragment();
            fm.beginTransaction().add(timerFragment, TIMER_FRAGMENT).commit();
        }

        if(eventStatsFragment == null) {
            args.putBoolean(
                    STATS_EXPANSION,
                    getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(STATS_EXPANSION, false)
            );
            eventStatsFragment = new EventStatsFragment();
            eventStatsFragment.setArguments(args);
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

    private void clearListAndRefresh() {
        eventListFragment.removeAllEvents();
        eventStatsFragment.updateViews();
    }

    private void toggleAutoUpdate(MenuItem item) {
        item.setChecked(!item.isChecked());
        recalculateStatsAction.setEnabled(!item.isChecked());

        eventStatsFragment.useListStats(item.isChecked());
    }

    private void resetAll() {
        eventListFragment.removeAllEvents();
        eventStatsFragment.resetStats();
        timerFragment.resetTimerIndex();
    }

    @Override
    public void updateListFragment() {
        eventListFragment.refreshEventList();
    }

    @Override
    public void updateStatsFragment() {
        eventStatsFragment.updateViews();
    }
}
