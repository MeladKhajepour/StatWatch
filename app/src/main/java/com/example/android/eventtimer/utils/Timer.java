package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.android.eventtimer.utils.EventManager.INDEX;

public class Timer {
    public enum TimerState {TIMING, STOPPED, RESET}

    private long startTime;
    private long duration = 0;
    private TextView textView;
    private Handler handler = new Handler();
    private SharedPreferences prefs;
    private TimerState timerState;
    private long currentDurationMillis;
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

    public void resumeTimer(long originalStartTime) {
        startTime = originalStartTime;
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

    public void reloadStoppedState(long lastTimerMillis) {
        timerState = TimerState.STOPPED;

        textView.setText(formatDuration(lastTimerMillis));
        duration = lastTimerMillis;
    }

    public Event createEvent() {
        int label = autoIndex.incrementAndGet();

        Event event = new Event(label, duration);

        saveIndex(label);

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

    public void resetTimerIndex() {
        autoIndex.set(0);
        saveIndex(autoIndex.get());
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            currentDurationMillis = SystemClock.uptimeMillis() - startTime;

            //TODO: refactor to change the time lin-layout sub-texviews into h:m:s or m:s.ms
            //TODO: like updateTime(...)
            textView.setText(formatDuration(currentDurationMillis));
            handler.post(this);
        }

    };

    private void saveIndex(int index) {
        prefs.edit().putInt(INDEX, index).apply();
    }
}

