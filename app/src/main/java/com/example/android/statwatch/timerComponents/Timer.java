package com.example.android.statwatch.timerComponents;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;

import com.example.android.statwatch.eventComponents.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.android.statwatch.utils.Constants.PAUSED;
import static com.example.android.statwatch.utils.Constants.READY;
import static com.example.android.statwatch.utils.Constants.REFRESH_RATE;
import static com.example.android.statwatch.utils.Constants.TIMING;
import static com.example.android.statwatch.utils.Constants.LAST_SAVED_INDEX;
import static com.example.android.statwatch.utils.Constants.ELAPSED_TIME;
import static com.example.android.statwatch.utils.Constants.START_TIME_MILLIS;
import static com.example.android.statwatch.utils.Constants.TIMER_STATE;

public class Timer {
    private AtomicInteger autoIndex;
    private int currentIndex;
    private long startMillis;
    private long elapsedMillis;
    private long bufferMillis;
    private Handler handler = new Handler();
    private SharedPreferences prefs;
    private String state;

    Timer(SharedPreferences prefs) {
        this.prefs = prefs;
        autoIndex = new AtomicInteger(loadIndex());
        currentIndex = autoIndex.get();
        state = getState();
    }

    /*
     * Start of public methods
     */

    void onStart() {
        if(state.equals(READY)) {
            startMillis = SystemClock.uptimeMillis();
            prefs.edit().putLong(START_TIME_MILLIS, startMillis).apply();

            setState(TIMING);
            handler.post(timer);
        }
    }

    void onPause() {
        if(state.equals(TIMING)) {
            saveElapsedTime();
            bufferMillis = SystemClock.uptimeMillis();

            setState(PAUSED);
            handler.removeCallbacks(timer);
        }
    }

    void onResume() {
        if(state.equals(PAUSED)) {
            bufferMillis = SystemClock.uptimeMillis() - bufferMillis;
            startMillis += bufferMillis;

            setState(TIMING);
            handler.post(timer);
        }
    }

    void onReset() {
        if(state.equals(TIMING) || state.equals(PAUSED)) {
            elapsedMillis = 0;
            saveElapsedTime();

            setState(READY);
            handler.removeCallbacks(timer);
        }
    }

    void loadPausedTime() {
        elapsedMillis = loadElapsedTime();
    }

    Event createEvent() {
        Event event = null;

        if(state.equals(TIMING) || state.equals(PAUSED)) {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            currentIndex = autoIndex.incrementAndGet();
            event = new Event(currentIndex, elapsedMillis, date);
            saveIndex();
            onReset();
        }

        return event;
    }

    public static String formatDuration(long durationMillis) {
        return formatDuration(durationMillis, false);
    }

    static String formatDuration(long durationMillis, boolean truncateTenths) {
        long durationTenths = Math.round(durationMillis / 100f); // rounds millis to nearest tenth of a second
        long durationSeconds = durationTenths / 10L;
        int hours;
        int minutes = (int) (durationSeconds / 60L);
        int seconds = (int) (durationSeconds % 60);
        int tenths = (int) (durationTenths % 10);
        String formattedTime;

        if(minutes < 60) {
            formattedTime = truncateTenths ?
                    String.format(Locale.getDefault(), "%d:%02d", minutes, seconds) :
                    String.format(Locale.getDefault(), "%d:%02d.%01d", minutes, seconds, tenths);
        } else {
            hours = minutes / 60;
            minutes = minutes % 60;

            formattedTime = String.format(
                    Locale.getDefault(),
                    "%d:%02d:%02d.%01d",
                    hours, minutes, seconds, tenths
            );
        }

        return formattedTime;
    }

    void resetTimerIndex() {
        autoIndex.set(0);
        saveIndex();
    }

    void onUndo() {
        autoIndex.set(currentIndex);
        saveIndex();
    }

    long getTime() {
        return elapsedMillis;
    }

    String getState() {
        return prefs.getString(TIMER_STATE, READY);
    }

    /*
     * Start of private methods
     */

    private Runnable timer = new Runnable() {
        public void run() {
            elapsedMillis = SystemClock.uptimeMillis() - startMillis;
            handler.postDelayed(this, REFRESH_RATE);
        }

    };

    private long loadElapsedTime() {
        return prefs.getLong(ELAPSED_TIME, 0);
    }

    private void saveElapsedTime() {
        prefs.edit().putLong(ELAPSED_TIME, elapsedMillis).apply();
    }

    private int loadIndex() {
        return prefs.getInt(LAST_SAVED_INDEX, 0);
    }

    private void saveIndex() {
        prefs.edit().putInt(LAST_SAVED_INDEX, autoIndex.get()).apply();
    }

    private void setState(String state) {
        this.state = state;
        prefs.edit().putString(TIMER_STATE, state).apply();
    }
}

