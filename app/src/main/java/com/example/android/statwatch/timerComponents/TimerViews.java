package com.example.android.statwatch.timerComponents;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.statwatch.MainActivity;
import com.example.android.statwatch.R;

import static com.example.android.statwatch.utils.Constants.PAUSED;
import static com.example.android.statwatch.utils.Constants.READY;
import static com.example.android.statwatch.utils.Constants.TIMING;
import static com.example.android.statwatch.utils.TransitionUtils.hideAddButton;
import static com.example.android.statwatch.utils.TransitionUtils.hideResetButton;
import static com.example.android.statwatch.utils.TransitionUtils.showAddButton;
import static com.example.android.statwatch.utils.TransitionUtils.showPauseButton;
import static com.example.android.statwatch.utils.TransitionUtils.showResetButton;
import static com.example.android.statwatch.utils.TransitionUtils.showStartButton;

class TimerViews {
    private TextView textview;
    private ViewGroup buttonBar;
    private View resetButton;
    private View startButton;
    private View addButton;

    TimerViews(final TimerFragment timerFragment) {
        MainActivity app = (MainActivity) timerFragment.requireContext();

        textview = app.findViewById(R.id.timer_textview);
        buttonBar = app.findViewById(R.id.button_bar);
        resetButton = app.findViewById(R.id.reset_button);
        startButton = app.findViewById(R.id.main_button);
        addButton = app.findViewById(R.id.add_button);

        View.OnClickListener btnListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                switch (view.getId()) {

                    case R.id.reset_button:
                        timerFragment.resetTimer();
                        break;

                    case R.id.main_button:

                        switch (timerFragment.getTimerState()) { // get timer state separately

                            case TIMING:
                                timerFragment.pauseTimer();
                                break;

                            case PAUSED:
                                timerFragment.resumeTimer();
                                break;

                            case READY:
                                timerFragment.startTimer();
                                break;
                        }
                        break;

                    case R.id.add_button:
                        timerFragment.addEvent();
                        break;
                }

                transitionViews(timerFragment.getTimerState()); // second call
            }
        };

        resetButton.setOnClickListener(btnListener);
        startButton.setOnClickListener(btnListener);
        addButton.setOnClickListener(btnListener);
    }

    void updateTime(long time) {
        textview.setText(Timer.formatDuration(time));
    }

    void transitionViews(String state) {//todo work on transitions, maybe dont have add button show/hide on pause/play

        switch (state) {

            case READY:
                showStartButton(startButton, true);
                hideResetButton(buttonBar, resetButton);
                hideAddButton(buttonBar, addButton);
                break;

            case TIMING:
                showPauseButton(startButton);
                showResetButton(buttonBar, resetButton);
                showAddButton(buttonBar, addButton);
                break;

            case PAUSED:
                showStartButton(startButton, false);
                showResetButton(buttonBar, resetButton);
                showAddButton(buttonBar, addButton);
                break;
        }
    }
}
