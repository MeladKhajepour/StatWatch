package com.example.android.statwatch.statsComponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import static com.example.android.statwatch.utils.Constants.PREFS;
import static com.example.android.statwatch.utils.Constants.SELECTED_ALPHA;

public class StatsFragment extends Fragment {
    private SharedPreferences prefs;
    private StatsManager statsManager;
    private StatsViews statsViews;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        statsManager = new StatsManager(prefs, getAplhaSelection());
        statsViews = new StatsViews(this);
        refresh();
    }

    public void refresh() {
        StatsManager.Stats stats = statsManager.getStats();
        statsViews.setStats(stats);
    }

    void updateAlphaSelection(int selectedMoe) {
        prefs.edit().putInt(SELECTED_ALPHA, selectedMoe).apply();
        statsManager.setAlpha(selectedMoe);
        refresh();
    }

    int getAplhaSelection() {
        return prefs.getInt(SELECTED_ALPHA, 1);
    }
}
