package com.example.android.statwatch.utils;

import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

import com.example.android.statwatch.R;

import static com.example.android.statwatch.utils.Constants.ANIMATION_DURATION;
import static com.example.android.statwatch.utils.Constants.HIDE_BUTTONS_DURATION;

public class TransitionUtils {

    public static void showPauseButton(final View mainButton) {
        ((ImageButton) mainButton).setImageResource(R.drawable.icon_pause);

        if(mainButton.getBackground().getClass().equals(TransitionDrawable.class)) {
            mainButton.setBackground(Resources.TRANSITION_ACCENT_TO_GREY);
            ((TransitionDrawable) Resources.TRANSITION_ACCENT_TO_GREY).startTransition(ANIMATION_DURATION);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainButton.setBackground(Resources.BUTTON_MAIN_GREY);
                }
            }, ANIMATION_DURATION);
        }
    }

    public static void showStartButton(View startButton, boolean transitionColour) {
        ((ImageButton) startButton).setImageResource(R.drawable.icon_start);

        if(transitionColour) {
            startButton.setBackground(Resources.TRANSITION_GREY_TO_ACCENT);
            ((TransitionDrawable) Resources.TRANSITION_GREY_TO_ACCENT).startTransition(ANIMATION_DURATION);
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
