package com.example.android.eventtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.example.android.eventtimer.utils.EventsManager;
import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.StatsManager;
import com.example.android.eventtimer.utils.Timer;

import java.util.List;

import static com.example.android.eventtimer.utils.EventsManager.PREFS;

public class EventStatsFragment extends Fragment {
    public static String USE_LIST_STATS = "useListStats";

    private TextView shortestEventView;
    private TextView averageTimeView;
    private TextView longestEventView;
    private SharedPreferences prefs;

    public static boolean useListStats;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void init(MainActivity app) {
        setupViews(app);
        updateViews();

        useListStats = prefs.getBoolean(USE_LIST_STATS, false);
    }

    private void setupViews(MainActivity app) {
        shortestEventView = app.findViewById(R.id.shortest_event_time);
        averageTimeView = app.findViewById(R.id.average_time_time);
        longestEventView = app.findViewById(R.id.longest_event_time);
        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void useListStats(boolean b) {
        useListStats = b;
        prefs.edit().putBoolean(USE_LIST_STATS, b).apply();
        if(b) {
            recalculateListStats();
        }
    }

    public void updateViews() {
        shortestEventView.setText(Timer.formatDuration(StatsManager.getShortestEvent(prefs)));
        averageTimeView.setText(Timer.formatDuration(StatsManager.getAverageTime(prefs)));
        longestEventView.setText(Timer.formatDuration(StatsManager.getLongestEvent(prefs)));
    }

    public void resetStats() {
        StatsManager.setShortestEvent(prefs, 0);
        StatsManager.setAverageTime(prefs, 0);
        StatsManager.setLongestEvent(prefs, 0);

        updateViews();
    }

    public void recalculateListStats() {
        StatsManager.recalculateListStats(prefs);

        updateViews();
    }
}
