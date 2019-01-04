package com.example.android.statwatch.statsComponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import static com.example.android.statwatch.utils.Constants.PREFS;
import static com.example.android.statwatch.utils.Constants.SELECTED_CONFIDENCE;

public class StatsFragment extends Fragment {
    private SharedPreferences prefs;
    private StatsManager statsManager;
    private StatsViews statsViews;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        statsManager = new StatsManager(prefs, getConfidence());
        statsViews = new StatsViews(this, getConfidence());
        refresh();
    }

    public void refresh() {
        StatsManager.Stats stats = statsManager.getStats();
        statsViews.refreshStats(stats);
    }

    void setConfidence(int ci) {
        prefs.edit().putInt(SELECTED_CONFIDENCE, ci).apply();
        statsManager.setAlpha(ci);
        refresh();
    }

    int getConfidence() {
        return prefs.getInt(SELECTED_CONFIDENCE, 1);
    }
}
