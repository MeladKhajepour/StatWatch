package com.example.android.statwatch.utils;

import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.android.statwatch.R;

import static com.example.android.statwatch.utils.Constants.ANIMATION_DURATION;
import static com.example.android.statwatch.utils.Constants.HIDE_BUTTONS_DURATION;

public class TransitionUtils {

    public static void showPauseButton(final View startButton) {
        ((ImageView)((ViewGroup) startButton).getChildAt(1)).setImageResource(R.drawable.pause_icon);

        if(startButton.getBackground().getClass().equals(TransitionDrawable.class)) {
            startButton.setBackground(Resources.accentToGreyTransition);
            ((TransitionDrawable) Resources.accentToGreyTransition).startTransition(ANIMATION_DURATION);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startButton.setBackground(Resources.backgroundColour);
                }
            }, ANIMATION_DURATION);
        }
    }

    public static void showStartButton(View startButton, boolean transitionColour) {
        ((ImageView)((ViewGroup) startButton).getChildAt(1)).setImageResource(R.drawable.start_icon);

        if(transitionColour) {
            startButton.setBackground(Resources.greyToAccentTransition);
            ((TransitionDrawable) Resources.greyToAccentTransition).startTransition(ANIMATION_DURATION);
        }
    }

    public static void showResetButton(ViewGroup buttonBar, View resetButton) {
        if(resetButton.getVisibility() == View.INVISIBLE) {
            Fade transition = new Fade(Fade.IN);
            transition.setDuration(ANIMATION_DURATION);
            transition.setInterpolator(new DecelerateInterpolator());

            TransitionManager.beginDelayedTransition(buttonBar, transition);

            resetButton.setVisibility(View.VISIBLE);

            buttonBar.requestLayout();
        }
    }

    public static void hideResetButton(ViewGroup buttonBar, View resetButton) {
        if(resetButton.getVisibility() == View.VISIBLE) {
            Fade transition = new Fade(Fade.OUT);
            transition.setDuration(HIDE_BUTTONS_DURATION);
            transition.setInterpolator(new DecelerateInterpolator());

            TransitionManager.beginDelayedTransition(buttonBar, transition);

            resetButton.setVisibility(View.INVISIBLE);

            buttonBar.requestLayout();
        }
    }

    public static void showAddButton(ViewGroup buttonBar, View addButton) {
        if(addButton.getVisibility() == View.INVISIBLE) {
            Fade transition = new Fade(Fade.IN);
            transition.setDuration(ANIMATION_DURATION);
            transition.setInterpolator(new DecelerateInterpolator());

            TransitionManager.beginDelayedTransition(buttonBar, transition);

            addButton.setVisibility(View.VISIBLE);

            buttonBar.requestLayout();
        }
    }

    public static void hideAddButton(ViewGroup buttonBar, View addButton) {
        if(addButton.getVisibility() == View.VISIBLE) {
            Fade transition = new Fade(Fade.OUT);
            transition.setDuration(HIDE_BUTTONS_DURATION);
            transition.setInterpolator(new DecelerateInterpolator());

            TransitionManager.beginDelayedTransition(buttonBar, transition);

            addButton.setVisibility(View.INVISIBLE);

            buttonBar.requestLayout();
        }
    }
}
