package com.example.android.eventtimer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.Timer;
import com.example.android.eventtimer.utils.TimerService;

import static com.example.android.eventtimer.utils.EventManager.PREFS;

public class TimerFragment extends Fragment {

    private final String START_TIME_MILLIS = "startTimeMillis";
    private final String LAST_TIME_MILLIS = "lastTimeMillis";
    private final String IS_TIMING = "isTiming";
    private final String IS_STOPPED = "isStopped";

    private FloatingActionButton timerBtn;
    private FloatingActionButton resetBtn;
    private Timer timer;
    private TimerService ts;
    private Intent intent;
    private SharedPreferences prefs;
    private AddEventListener mainActivityListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        intent = new Intent(getActivity(), TimerService.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mainActivityListener = (AddEventListener) context;
            prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddEventListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(prefs.getBoolean(IS_TIMING, false)) {
            resumeTimer();
        } else if(prefs.getBoolean(IS_STOPPED, false)) {
            loadStoppedState();
        }
    }

    public void init(MainActivity app) {
        setupViews(app);
        setupHandlers();
    }

    public void resetTimerIndex() {
        timer.resetTimerIndex();
    }

    public interface AddEventListener {
        void onEventReceived(Event event);
    }

    private void resumeTimer() {
        long originalStartTime = prefs.getLong(START_TIME_MILLIS, System.currentTimeMillis());

        timer.resumeTimer(originalStartTime);
        changeButton(R.color.stopButtonColour, R.drawable.stop_icon);
    }

    private void loadStoppedState() {
        long lastTimerMillis = prefs.getLong(LAST_TIME_MILLIS, 0);
        timer.reloadStoppedState(lastTimerMillis);

        changeButton(R.color.startButtonColour, R.drawable.add_icon);
        resetBtn.show();
    }

    private void setupViews(MainActivity app) {
        TextView timerTv = app.findViewById(R.id.timer_textview);
        timerBtn = app.findViewById(R.id.timer_btn);
        resetBtn = app.findViewById(R.id.timer_reset_btn);

        timer = new Timer(timerTv, app.getSharedPreferences(PREFS, Context.MODE_PRIVATE));
    }

    private void setupHandlers() {
        timerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (timer.getTimerState()) {

                    case RESET:
                        startTimer();
                        break;

                    case TIMING:
                        stopTimer();
                        break;

                    case STOPPED:
                        sendEventToActivity();
                        break;
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
    }

    private void startTimer() {
        timer.startTimer();

        changeButton(R.color.stopButtonColour, R.drawable.stop_icon);
        prefs.edit().putBoolean(IS_TIMING, true).apply();
        prefs.edit().putLong(START_TIME_MILLIS, timer.getStartTime()).apply();
    }

    private void stopTimer() {
        timer.stopTimer();

        changeButton(R.color.startButtonColour, R.drawable.add_icon);
        prefs.edit().putBoolean(IS_TIMING, false).apply();
        prefs.edit().putBoolean(IS_STOPPED, true).apply();
        prefs.edit().putLong(LAST_TIME_MILLIS, timer.getDuration()).apply();
        resetBtn.show();
    }

    private void sendEventToActivity() {
        resetButtons();
        mainActivityListener.onEventReceived(timer.createEvent());

        timer.resetTimer();
    }

    private void resetTimer() {
        timer.resetTimer();

        resetButtons();
    }

    private void resetButtons() {
        changeButton(R.color.startButtonColour, R.drawable.start_icon);
        resetBtn.hide();
    }

    private void changeButton(int colour, int icon) {
        setButtonColour(colour);
        setButtonIcon(icon);
    }

    private void setButtonColour(int colour) {
        timerBtn.setBackgroundTintList(getResources().getColorStateList(colour));
    }

    private void setButtonIcon(int icon) {
        timerBtn.setImageResource(icon);
    }
}