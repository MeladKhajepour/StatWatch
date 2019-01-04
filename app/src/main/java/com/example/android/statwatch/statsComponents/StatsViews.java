package com.example.android.statwatch.statsComponents;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.statwatch.MainActivity;
import com.example.android.statwatch.R;

class StatsViews {
    private TextView intervalMin;
    private TextView intervalMax;
    private TextView shortestTime;
    private TextView averageTime;
    private TextView longestTime;
    private TextView stdDev;
    private TextView ciPercentage;
    private TextView moeInterval;
    private View timeBar;
    private int maxWidth;

    StatsViews(final StatsFragment statsFragment, int ci) {
        final MainActivity app = (MainActivity) statsFragment.requireContext();
        intervalMin = app.findViewById(R.id.min_time);
        intervalMax = app.findViewById(R.id.max_time);
        shortestTime = app.findViewById(R.id.shortest_event_time);
        averageTime = app.findViewById(R.id.average_time);
        longestTime = app.findViewById(R.id.longest_event_time);
        stdDev = app.findViewById(R.id.std_dev_value);
        moeInterval = app.findViewById(R.id.moe);
        timeBar = app.findViewById(R.id.time_bar);
        timeBar.post(new Runnable() {
            @Override
            public void run() {
                maxWidth = timeBar.getWidth();
            }
        });

        ciPercentage = app.findViewById(R.id.confidence_interval_value);
        ciPercentage.setText(ci + "%");

        SeekBar confidenceSeekbar = app.findViewById(R.id.confidence_seekbar);
        confidenceSeekbar.setProgress(ci - 80);
        confidenceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 80;
                ciPercentage.setText(progress + "%");
                statsFragment.setConfidence(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void refreshStats(StatsManager.Stats stats) {
        intervalMin.setText(stats.minTime);
        intervalMax.setText(stats.maxTime);
        shortestTime.setText(stats.shortest);
        averageTime.setText(stats.average);
        longestTime.setText(stats.longest);
        stdDev.setText(stats.stdDev);
        moeInterval.setText(stats.moe);

        calculateTimeBar(stats.ratio);
    }

    private void calculateTimeBar(double ratio) {
        ViewGroup.LayoutParams params = timeBar.getLayoutParams();
        params.width = (int) (maxWidth * ratio);
        timeBar.setLayoutParams(params);
    }
}
