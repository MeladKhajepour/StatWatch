package com.example.android.eventtimer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.ExpandStats;
import com.example.android.eventtimer.utils.StatsManager;
import com.example.android.eventtimer.utils.Timer;

import static com.example.android.eventtimer.utils.Constants.STATS_EXPANSION;
import static com.example.android.eventtimer.utils.Constants.USE_LIST_STATS;
import static com.example.android.eventtimer.utils.EventsManager.PREFS;
import static com.example.android.eventtimer.utils.TransitionHelper.ANIMATION_DURATION;

public class StatsFragment extends Fragment {
    private LinearLayout statsBar;
    private TextView shortestEventView;
    private TextView averageTimeView;
    private TextView longestEventView;
    private TextView moeView;
    private TextView stdDevView;
    private LinearLayout expandedStats;
    private LinearLayout moeButton;
    private SharedPreferences prefs;
    private boolean isExpanded;

    private ExpandStats expandStats;

    public static boolean useListStats;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        init((MainActivity) context);
    }

    public void init(MainActivity app) {
        expandStats = new ExpandStats();
        setupViews(app);
        updateViews();
    }

    private void setupViews(final MainActivity app) {
        statsBar = app.findViewById(R.id.stats);
        shortestEventView = app.findViewById(R.id.shortest_event_time);
        averageTimeView = app.findViewById(R.id.average_time_time);
        longestEventView = app.findViewById(R.id.longest_event_time);
        stdDevView = app.findViewById(R.id.std_dev_value);
        moeView = app.findViewById(R.id.margin_of_error_value);
        expandedStats = app.findViewById(R.id.expanded_stats);
        moeButton = app.findViewById(R.id.margin_of_error_button);
        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        statsBar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isExpanded = !isExpanded;
                toggleExpansion(v, isExpanded);
                prefs.edit().putBoolean(STATS_EXPANSION, isExpanded).apply();
            }
        });

        moeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(app);
                builder.setTitle("Select a new confidence interval. For details see Menu > More") //todo
                        .setSingleChoiceItems(new CharSequence[]{"99%", "95%", "90%", "80%"},
                                StatsManager.getConfidence(prefs),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        StatsManager.changeConfidence(prefs, which);
                                        updateMoe();
                                        dialog.dismiss();
                                    }
                                }).create()
                        .show();
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

    public void addEvent(Event event) {
        StatsManager.updateEventAdded(prefs, event);
        updateViews();
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

    public void updateMoe() {
        moeView.setText(Timer.formatDuration(StatsManager.getmoe(prefs)));
    }

    public void resetStats() {
        StatsManager.resetStats(prefs);

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
