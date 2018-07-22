package com.example.android.eventtimer.utils;

import android.view.View;
import android.view.ViewGroup;

public class TransitionHelper {
    public static int ANIMATION_DURATION = 150;

    public static void changeMarginTop(boolean expanded, View v) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();

        int newMarginTop;
        if(expanded) {
            newMarginTop = (params.topMargin * 3) / 2;
        } else {
            newMarginTop = (params.topMargin * 2) / 3;
        }

        params.topMargin = newMarginTop;
    }

    public static void transitionExpandedStats(boolean expanded, View v, int defaultHeight) {
        if(expanded) {
            v.getLayoutParams().height = defaultHeight;
            v.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
        } else {
            v.getLayoutParams().height = 0;
            v.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
        }
    }
}
