package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;

import java.util.concurrent.atomic.AtomicInteger;

import static com.example.android.eventtimer.TimerFragment.START_TIME_MILLIS;
import static com.example.android.eventtimer.TimerFragment.RESET_STATE;
import static com.example.android.eventtimer.TimerFragment.STOPPED_STATE;
import static com.example.android.eventtimer.TimerFragment.TIMING_STATE;
import static com.example.android.eventtimer.TimerFragment.TIMER_STATE;

public class Timer {

    private static final String LAST_SAVED_TIME = "lastSavedTime";
    private static final String LAST_SAVED_INDEX = "index";
    private long startTime;
    private long elapsedTime = 0;
    private Handler handler = new Handler();
    private SharedPreferences prefs;
    private static AtomicInteger autoIndex;

    public Timer(SharedPreferences prefs) {
        this.prefs = prefs;

        autoIndex = new AtomicInteger(prefs.getInt(LAST_SAVED_INDEX, 0));
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        prefs.edit().putLong(START_TIME_MILLIS, startTime).apply();
        handler.post(runnable);

        setTimerState(prefs, TIMING_STATE);
        saveLastTime();
    }

    public void resumeTimer() {
        startTime = prefs.getLong(START_TIME_MILLIS, SystemClock.uptimeMillis());
        handler.post(runnable);
    }

    public void stopTimer() {
        handler.removeCallbacks(runnable);

        setTimerState(prefs, STOPPED_STATE);
        saveLastTime();
    }

    public void resetTimer() {
        handler.removeCallbacks(runnable);
        elapsedTime = 0;

        setTimerState(prefs, RESET_STATE);
        saveLastTime();
    }

    public long reloadStopState() {
        setTimerState(prefs, STOPPED_STATE);

        elapsedTime = prefs.getLong(LAST_SAVED_TIME, 0);

        return elapsedTime;
    }

    public Event createEvent() {
        Event event = new Event(autoIndex.incrementAndGet(), elapsedTime);

        saveIndex();

        return event;
    }

    //TODO: refactor to modify LinearLayout subviews of h:m:s or m:s.ms
    public static String formatDuration(long durationMillis) {
        long curDurationSeconds = durationMillis / 1000L;

        int minutes = (int) (curDurationSeconds / 60L);
        int seconds = (int) (curDurationSeconds % 60);
        int millis = (int) (durationMillis % 1000);

        String time = String.format("%d:%02d.%d", minutes, seconds, millis / 100);
        return time;
    }

    public static String formatNotificationDuration(long durationMillis) {
        long curDurationSeconds = durationMillis / 1000L;

        int minutes = (int) (curDurationSeconds / 60L);
        int seconds = (int) (curDurationSeconds % 60);

        String time = String.format("%d:%02d", minutes, seconds);
        return time;
    }

    public void resetTimerIndex() {
        autoIndex.set(0);
        saveIndex();
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public static String getTimerState(SharedPreferences prefs) {
        return prefs.getString(TIMER_STATE, RESET_STATE);
    }

    public static void setTimerState(SharedPreferences prefs, String state) {
        prefs.edit().putString(TIMER_STATE, state).apply();
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            handler.postDelayed(this, 10);
        }

    };

    private void saveLastTime() {
        prefs.edit().putLong(LAST_SAVED_TIME, elapsedTime).apply();
    }

    private void saveIndex() {
        prefs.edit().putInt(LAST_SAVED_INDEX, autoIndex.get()).apply();
    }
}

