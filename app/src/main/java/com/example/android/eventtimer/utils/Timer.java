package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.android.eventtimer.utils.Constants.IS_PAUSED;
import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.START_TIME_MILLIS;
import static com.example.android.eventtimer.utils.Constants.TIMER_STATE;

public class Timer {

    private static final String LAST_SAVED_TIME = "lastSavedTime";
    private static final String LAST_SAVED_INDEX = "index";
    private long startTime;
    private long elapsedTime;
    private long bufferTime;
    private Handler handler = new Handler();
    private SharedPreferences prefs;
    private static AtomicInteger autoIndex;
    private int lastIndex;

    Timer(SharedPreferences prefs) {
        this.prefs = prefs;

        autoIndex = new AtomicInteger(prefs.getInt(LAST_SAVED_INDEX, 0));
        lastIndex = autoIndex.get();
    }

    public void start() {
        startTime = SystemClock.uptimeMillis();
        prefs.edit().putLong(START_TIME_MILLIS, startTime).apply();
        handler.post(runnable);

        setState(prefs, IS_TIMING);
        saveElapsedTime();
    }

    public void pause() {
        handler.removeCallbacks(runnable);
        bufferTime = SystemClock.uptimeMillis();

        setState(prefs, IS_PAUSED);
        saveElapsedTime();
    }

    public void resume() {
        bufferTime = SystemClock.uptimeMillis() - bufferTime;
        startTime += bufferTime;
        handler.post(runnable);

        setState(prefs, IS_TIMING);
    }

    public Event createEvent() {
        lastIndex = autoIndex.incrementAndGet();
        Event event = new Event(lastIndex, elapsedTime);
        saveIndex();
        return event;
    }

    public void reset() {
        handler.removeCallbacks(runnable);
        elapsedTime = 0;

        setState(prefs, IS_READY);
        saveElapsedTime();
    }

    public void reloadPausedState() {
        setState(prefs, IS_PAUSED);

        elapsedTime = loadElapsedTime();
    }

    //TODO: refactor to modify LinearLayout subviews of h:m:s or m:s.ms
    public static String formatDuration(long durationMillis) {
        long roundedMillis = Math.round(durationMillis / 100f) * 100L;
        long durationSeconds = roundedMillis / 1000L;

        int minutes = (int) (durationSeconds / 60L);
        int seconds = (int) (durationSeconds % 60);
        long millis = (roundedMillis % 1000);

        return String.format(
                Locale.getDefault(),
                "%d:%02d.%01d",
                minutes, seconds, millis / 100L // todo - maybe a high accuracy mode
        );
    }

    public static String formatNotificationDuration(long durationMillis) {
        long roundedMillis = Math.round(durationMillis / 100f) * 100L;
        long durationSeconds = roundedMillis / 1000L;

        int minutes = (int) (durationSeconds / 60L);
        int seconds = (int) (durationSeconds % 60);

        return String.format(
                Locale.getDefault(),
                "%d:%02d",
                minutes, seconds
        );
    }

    public void resetTimerIndex() {
        autoIndex.set(0);
        saveIndex();
    }

    public void undoResetTimerIndex() {
        autoIndex.set(lastIndex);
        saveIndex();
    }

    public long getTime() {
        return elapsedTime;
    }

    public static String getState(SharedPreferences prefs) {
        return prefs.getString(TIMER_STATE, IS_READY);
    }

    public static void setState(SharedPreferences prefs, String state) {
        prefs.edit().putString(TIMER_STATE, state).apply();
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            handler.postDelayed(this, 100);//todo high accuracy mode for 100ths of sec
        }

    };

    private void saveElapsedTime() {
        prefs.edit().putLong(LAST_SAVED_TIME, elapsedTime).apply();
    }

    private long loadElapsedTime() {
        return prefs.getLong(LAST_SAVED_TIME, 0);
    }

    private void saveIndex() {
        prefs.edit().putInt(LAST_SAVED_INDEX, autoIndex.get()).apply();
    }
}

