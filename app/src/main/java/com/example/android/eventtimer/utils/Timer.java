package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.android.eventtimer.utils.Constants.IS_PAUSED;
import static com.example.android.eventtimer.utils.Constants.IS_READY;
import static com.example.android.eventtimer.utils.Constants.IS_TIMING;
import static com.example.android.eventtimer.utils.Constants.LAST_SAVED_INDEX;
import static com.example.android.eventtimer.utils.Constants.LAST_SAVED_TIME;
import static com.example.android.eventtimer.utils.Constants.START_TIME_MILLIS;
import static com.example.android.eventtimer.utils.Constants.TIMER_STATE;

public class Timer {
    private static AtomicInteger autoIndex;
    private static int lastIndex;

    private long startTime;
    private long elapsedTime;
    private long bufferTime;
    private Handler handler = new Handler();
    private SharedPreferences prefs;

    private Runnable counter = new Runnable() {
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - startTime;
            handler.postDelayed(this, 100);//todo high accuracy mode for 100ths of sec
        }

    };

    Timer(SharedPreferences prefs) {
        this.prefs = prefs;
        autoIndex = new AtomicInteger(prefs.getInt(LAST_SAVED_INDEX, 0));
        lastIndex = autoIndex.get();
    }

    /*
     * Start of public methods
     */

    public void start() {
        startTime = SystemClock.uptimeMillis();
        prefs.edit().putLong(START_TIME_MILLIS, startTime).apply();
        handler.post(counter);

        setState(prefs, IS_TIMING);
        saveElapsedTime();
    }

    public void pause() {
        handler.removeCallbacks(counter);
        bufferTime = SystemClock.uptimeMillis();

        setState(prefs, IS_PAUSED);
        saveElapsedTime();
    }

    public void resume() {
        bufferTime = SystemClock.uptimeMillis() - bufferTime;
        startTime += bufferTime;
        handler.post(counter);

        setState(prefs, IS_TIMING);
    }

    public Event addEvent() {
        lastIndex = autoIndex.incrementAndGet();
        Event event = new Event(lastIndex, elapsedTime);
        saveIndex(prefs);
        reset();
        return event;
    }

    public void reset() {
        handler.removeCallbacks(counter);
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
        long roundedMillis = Math.round(durationMillis / 1000f) * 1000L;
        long durationSeconds = roundedMillis / 1000L;

        int minutes = (int) (durationSeconds / 60L);
        int seconds = (int) (durationSeconds % 60);

        return String.format(
                Locale.getDefault(),
                "%d:%02d",
                minutes, seconds
        );
    }

    public static void resetTimerIndex(SharedPreferences prefs) {
        autoIndex.set(0);
        saveIndex(prefs);
    }

    public static void undoResetTimerIndex(SharedPreferences prefs) {
        autoIndex.set(lastIndex);
        saveIndex(prefs);
    }

    public long getTime() {
        return elapsedTime;
    }

    public static String getState(SharedPreferences prefs) {
        return prefs.getString(TIMER_STATE, IS_READY);
    }

    /*
     * End of public methods
     */

    private void saveElapsedTime() {
        prefs.edit().putLong(LAST_SAVED_TIME, elapsedTime).apply();
    }

    private long loadElapsedTime() {
        return prefs.getLong(LAST_SAVED_TIME, 0);
    }

    private static void saveIndex(SharedPreferences prefs) {
        prefs.edit().putInt(LAST_SAVED_INDEX, autoIndex.get()).apply();
    }

    private static void setState(SharedPreferences prefs, String state) {
        prefs.edit().putString(TIMER_STATE, state).apply();
    }
}

