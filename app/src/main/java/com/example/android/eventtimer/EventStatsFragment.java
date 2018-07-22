package com.example.android.eventtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.eventtimer.databinding.EventStatsLayoutBinding;
import com.example.android.eventtimer.utils.ExpandStats;
import com.example.android.eventtimer.utils.StatsManager;
import com.example.android.eventtimer.utils.Timer;

import static com.example.android.eventtimer.utils.EventsManager.PREFS;
import static com.example.android.eventtimer.utils.TransitionHelper.ANIMATION_DURATION;

public class EventStatsFragment extends Fragment {
    public static String USE_LIST_STATS = "useListStats";
    public static String STATS_EXPANSION = "stats_expansion";

    private LinearLayout statsBar;
    private TextView shortestEventView;
    private TextView averageTimeView;
    private TextView longestEventView;
    private TextView moeView;
    private TextView stdDevView;
    private LinearLayout expandedStats;
    private SharedPreferences prefs;
    private boolean isExpanded;

    private ExpandStats expandStats;

    public static boolean useListStats;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EventStatsLayoutBinding binding = EventStatsLayoutBinding.inflate(inflater, container, false);
        binding.setEs(expandStats);

        return binding.getRoot();
    }

    public void init(MainActivity app) {
        expandStats = new ExpandStats();
        setupViews(app);
        updateViews();
    }

    private void setupViews(MainActivity app) {
        statsBar = app.findViewById(R.id.stats);
        shortestEventView = app.findViewById(R.id.shortest_event_time);
        averageTimeView = app.findViewById(R.id.average_time_time);
        longestEventView = app.findViewById(R.id.longest_event_time);
        stdDevView = app.findViewById(R.id.std_dev_value);
        moeView = app.findViewById(R.id.margin_of_error_value);
        expandedStats = app.findViewById(R.id.expanded_stats);
        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        statsBar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isExpanded = !isExpanded;
                toggleExpansion(v, isExpanded);
                prefs.edit().putBoolean(STATS_EXPANSION, isExpanded).apply();
            }
        });

        isExpanded = prefs.getBoolean(STATS_EXPANSION, false);
        useListStats = prefs.getBoolean(USE_LIST_STATS, false);

        toggleExpansion(statsBar, isExpanded);
    }

    public void useListStats(boolean b) {
        useListStats = b;
        prefs.edit().putBoolean(USE_LIST_STATS, b).apply();
        if(b) {
            recalculateListStats();
        }
    }

    public void updateViews() {
        String shortestEventText = Timer.formatDuration(StatsManager.getShortestEvent(prefs));

        if(StatsManager.getShortestEvent(prefs) == (long) Double.POSITIVE_INFINITY) {
            shortestEventText = Timer.formatDuration(0);
        }

        shortestEventView.setText(shortestEventText);
        averageTimeView.setText(Timer.formatDuration(StatsManager.getAverageTime(prefs)));
        longestEventView.setText(Timer.formatDuration(StatsManager.getLongestEvent(prefs)));
        stdDevView.setText(Timer.formatDuration(StatsManager.getStdDev(prefs)));
        moeView.setText(Timer.formatDuration(StatsManager.getmoe(prefs)));
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

    private void toggleExpansion(View v, boolean isExpanded){
        TransitionSet transition = new TransitionSet();
        transition.setDuration(ANIMATION_DURATION);
        transition.setOrdering(TransitionSet.ORDERING_TOGETHER);
        transition.setInterpolator(new DecelerateInterpolator());
        transition.addTransition(new ChangeBounds())
                .addTransition(new Fade(Fade.IN).setDuration(ANIMATION_DURATION))
                .addTransition(new Fade(Fade.OUT).setDuration(ANIMATION_DURATION/2));

        TransitionManager.beginDelayedTransition((ViewGroup) v.getRootView().findViewById(R.id.container), transition);

        expandedStats.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        statsBar.requestLayout();
    }
}
