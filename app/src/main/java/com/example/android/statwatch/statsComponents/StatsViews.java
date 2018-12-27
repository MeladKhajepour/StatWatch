package com.example.android.statwatch.statsComponents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.statwatch.MainActivity;
import com.example.android.statwatch.R;

class StatsViews {
    private TextView shortestEventView;
    private TextView averageTimeView;
    private TextView longestEventView;
    private TextView moeView;
    private TextView stdDevView;

    StatsViews(final StatsFragment statsFragment) {
        final MainActivity app = (MainActivity) statsFragment.requireContext();
        LinearLayout moeButton = app.findViewById(R.id.margin_of_error_button);
        shortestEventView = app.findViewById(R.id.shortest_event_time);
        averageTimeView = app.findViewById(R.id.average_time_time);
        longestEventView = app.findViewById(R.id.longest_event_time);
        stdDevView = app.findViewById(R.id.std_dev_value);
        moeView = app.findViewById(R.id.margin_of_error_value);

        moeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(app);
                builder.setTitle("Confidence interval. For details see Menu > More") //todo
                        .setSingleChoiceItems(new CharSequence[]{"99%", "95%", "90%", "80%"},
                                statsFragment.getAplhaSelection(),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        statsFragment.updateAlphaSelection(which);
                                        dialog.dismiss();
                                    }
                                }).create()
                        .show();
            }
        });
    }

    void setStats(StatsManager.Stats stats) {
        shortestEventView.setText(stats.shortest);
        averageTimeView.setText(stats.average);
        longestEventView.setText(stats.longest);
        stdDevView.setText(stats.stdDev);
        moeView.setText(stats.moe);
    }
}
