package com.example.android.eventtimer.utils;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.android.eventtimer.R;

import static com.example.android.eventtimer.utils.Constants.ANIMATION_DURATION;
import static com.example.android.eventtimer.utils.Constants.HIDE_BUTTONS_DURATION;

public class TransitionUtils {

    public static void onTimerStart(ViewGroup buttonBar, View resetBtn, ViewGroup startBtn) {
        showPauseBtn(startBtn);
        showResetBtn(buttonBar, resetBtn);
    }

    public static void onTimerPause(ViewGroup buttonBar, ViewGroup startBtn, View addBtn) {
        showAddBtn(buttonBar, addBtn);
        showStartBtn(startBtn);
        showDivider(buttonBar, startBtn);
    }

    public static void onTimerResume(ViewGroup buttonBar, ViewGroup startBtn, View addBtn) {
        hideAddBtn(buttonBar, addBtn);
        showPauseBtn(startBtn);
        hideDivider(buttonBar, startBtn);
    }

    public static void onTimerReset(ViewGroup buttonBar, View resetBtn, ViewGroup startBtn, View addBtn) {
        hideResetBtn(buttonBar, resetBtn);
        hideAddBtn(buttonBar, addBtn);
        showStartBtn(startBtn);
        hideDivider(buttonBar, startBtn);
    }

    public static void onEventAdded(ViewGroup buttonBar, View resetBtn, ViewGroup startBtn, View addBtn) {
        hideResetBtn(buttonBar, resetBtn);
        hideAddBtn(buttonBar, addBtn);
        showStartBtn(startBtn);
    }

    private static void showResetBtn(ViewGroup buttonBar, View resetBtn) {
        Fade transition = new Fade(Fade.IN);
        transition.setDuration(ANIMATION_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        resetBtn.setVisibility(View.VISIBLE);

        buttonBar.requestLayout();
    }

    private static void showPauseBtn(ViewGroup startBtn) {
        ((ImageView)startBtn.getChildAt(1)).setImageResource(R.drawable.pause_icon);
    }

    private static void showAddBtn(ViewGroup buttonBar, View addBtn) {
        Fade transition = new Fade(Fade.IN);
        transition.setDuration(ANIMATION_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        addBtn.setVisibility(View.VISIBLE);

        buttonBar.requestLayout();
    }

    private static void showStartBtn(ViewGroup startBtn) {
        ((ImageView)startBtn.getChildAt(1)).setImageResource(R.drawable.start_icon);
    }

    private static void showDivider(ViewGroup buttonBar, ViewGroup startBtn) {
        Fade transition = new Fade(Fade.IN);
        transition.setDuration(ANIMATION_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        startBtn.getChildAt(0).setVisibility(View.VISIBLE);

        buttonBar.requestLayout();
    }

    private static void hideResetBtn(ViewGroup buttonBar, View resetBtn) {
        Fade transition = new Fade(Fade.OUT);
        transition.setDuration(HIDE_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        resetBtn.setVisibility(View.INVISIBLE);

        buttonBar.requestLayout();
    }

    private static void hideAddBtn(ViewGroup buttonBar, View addBtn) {
        Fade transition = new Fade(Fade.OUT);
        transition.setDuration(HIDE_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        addBtn.setVisibility(View.INVISIBLE);

        buttonBar.requestLayout();
    }

    private static void hideDivider(ViewGroup buttonBar, ViewGroup startBtn) {
        Fade transition = new Fade(Fade.OUT);
        transition.setDuration(HIDE_BUTTONS_DURATION);
        transition.setInterpolator(new DecelerateInterpolator());

        TransitionManager.beginDelayedTransition(buttonBar, transition);

        startBtn.getChildAt(0).setVisibility(View.INVISIBLE);

        buttonBar.requestLayout();
    }

    public static void transitionStartBtn(Context ctx, View startBtn, int drawable) {
        startBtn.setBackground(ctx.getResources().getDrawable(drawable));
        TransitionDrawable transition = (TransitionDrawable) startBtn.getBackground();
        transition.startTransition(ANIMATION_DURATION);
    }
}
