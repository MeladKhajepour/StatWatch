package com.example.android.eventtimer.utils;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.view.View;

import com.example.android.eventtimer.MainActivity;

import java.util.List;

public class ExpandStats extends BaseObservable {
    private static boolean isExpanded;
    private static int marginTop;

    public ExpandStats() {

    }

    private void getViews(MainActivity app) {

    }

    public void transition(List<View> views, boolean expanded, int topMargin) {
        isExpanded = expanded;

        for(View v : views) {
            TransitionHelper.changeMarginTop(expanded, v);
        }
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setLayoutMarginTop(View v, int topMargin) {
        marginTop = topMargin;
    }

    @Bindable
    public int getMarginTop() {
        return marginTop;
    }
}

