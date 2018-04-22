package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.eventtimer.MainActivity.ADD_BTN;
import static com.example.android.eventtimer.MainActivity.TIMER_BTN;
import static com.example.android.eventtimer.MainActivity.TIMER_COUNT;

public class TimerUtils {
    private long startTime;
    private long duration = 0;

    private HashMap<String, View> views;
    private Handler handler = new Handler();
    public List<Event> events;

    private final String EVENTS = "events";

    private SharedPreferences prefs;

    public TimerUtils(HashMap<String, View> views, SharedPreferences prefs) {
        this.views = views;
        this.prefs = prefs;
        events = loadEvents();
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.post(runnable);

        setMainButtonLabel("Stop");
    }

    public void resetTimer() {
        duration = 0;

        hideAddButton();

        setMainButtonLabel("Start");
        ((TextView)views.get(TIMER_COUNT)).setText("00:00.00");
    }

    public void pauseTimer() {
        duration += SystemClock.uptimeMillis() - startTime;
        handler.removeCallbacks(runnable);

        showAddButton();
        setMainButtonLabel("Reset");
    }

    public void addTime() {
        int label = 1; // add an auto increment index in shared pref
        if(!events.isEmpty()) {
            int lastIndex = events.get(events.size() - 1).getLabel();
            label = lastIndex + 1;
        }

        events.add(new Event(label, duration));

        saveEvents();
        resetTimer();
    }

    private void setMainButtonLabel(String text) {
        ((Button)views.get(TIMER_BTN)).setText(text);

    }
    private void hideAddButton() {
        addBtnVisible(false);
    }

    private void showAddButton() {
        addBtnVisible(true);
    }

    private void addBtnVisible(boolean b) {
        float resetBtnWeight = b ? 3 : 1;
        float addBtnWeight = b ? 1 : 0;

        views.get(TIMER_BTN).setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                resetBtnWeight
        ));
        views.get(ADD_BTN).setLayoutParams(
                new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                addBtnWeight
        ));
    }

    public List<Event> getEvents() {
        return events;
    }

    public Event getEventAt(int index) {
        return events.get(index);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            long curDurationMillis = SystemClock.uptimeMillis() - startTime;

            ((TextView)views.get(TIMER_COUNT)).setText(formatDuration(curDurationMillis));
            handler.post(this);
        }

    };

    public static String formatDuration(long durationMillis) {
        long curDurationSeconds = durationMillis / 1000L;

        int minutes = (int) (curDurationSeconds / 60L);
        int seconds = (int) (curDurationSeconds % 60);
        int millis = (int) (durationMillis % 1000);

        String time = String.format("%d:%02d.%02d", minutes, seconds, millis / 10);
        return time;
    }

    public void saveEvents() {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(events);
        editor.putString(EVENTS, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    private List<Event> loadEvents() {
        Gson gson = new Gson();
        String json = prefs.getString(EVENTS, null);
        Type type = new TypeToken<ArrayList<Event>>() {}.getType();
        if(gson.fromJson(json, type) == null) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(json, type);
        }
    }
}
