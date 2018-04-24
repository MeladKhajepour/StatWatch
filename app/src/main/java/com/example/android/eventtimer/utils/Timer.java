package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.android.eventtimer.MainActivity.INDEX;

public class Timer {
    public enum TimerState {TIMING, STOPPED, RESET}

    private long startTime;
    private long duration = 0;

    private TextView textView;
    private Handler handler = new Handler();
    private SharedPreferences prefs;
    private TimerState timerState;
    private AtomicInteger autoIndex;

    public Timer(TextView textView, SharedPreferences prefs) {
        this.textView = textView;
        this.prefs = prefs;

        int lastSavedIndex = prefs.getInt(INDEX, 0);
        autoIndex = new AtomicInteger(lastSavedIndex);

        timerState = TimerState.RESET;
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.post(runnable);

        timerState = TimerState.TIMING;
    }

    public void stopTimer() {
        duration = SystemClock.uptimeMillis() - startTime;
        handler.removeCallbacks(runnable);

        timerState = TimerState.STOPPED;
    }

    public void resetTimer() {
        duration = 0;

        textView.setText("0:00.00");

        timerState = TimerState.RESET;
    }

    public Event createEvent() {
        int label = autoIndex.incrementAndGet();
        prefs.edit().putInt(INDEX, label).apply();

        Event event = new Event(label, duration);

        resetTimer();

        return event;
    }

    //TODO: refactor to modify LinearLayout subviews of h:m:s or m:s.ms
    public static String formatDuration(long durationMillis) {
        long curDurationSeconds = durationMillis / 1000L;

        int minutes = (int) (curDurationSeconds / 60L);
        int seconds = (int) (curDurationSeconds % 60);
        int millis = (int) (durationMillis % 1000);

        String time = String.format("%d:%02d.%02d", minutes, seconds, millis / 10);
        return time;
    }

    public TimerState getTimerState() {
        return timerState;
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            long currentDurationMillis = SystemClock.uptimeMillis() - startTime;

            //TODO: refactor to change the time lin-layout sub-texviews into h:m:s or m:s.ms
            //TODO: like updateTime(...)
            textView.setText(formatDuration(currentDurationMillis));
            handler.post(this);
        }

    };
}

