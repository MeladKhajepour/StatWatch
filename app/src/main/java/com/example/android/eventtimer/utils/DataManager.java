package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DataManager {

    public static final String PREFS = "prefs";
    public static final String EVENTS = "events";
    public static final String INDEX = "index";

    private static List<Event> eventList;

    public static void addEvent(SharedPreferences prefs, Event event) {
        eventList.add(event);
        saveEvents(prefs);
    }

    public static void removeEvent(SharedPreferences prefs, Event event) {
        eventList.remove(event);
        saveEvents(prefs);
    }

    public static List<Event> loadEvents(SharedPreferences prefs) {
        Gson gson = new Gson();
        String json = prefs.getString(EVENTS, null);

        Type type = new TypeToken<ArrayList<Event>>() {}.getType();

        if(gson.fromJson(json, type) == null) {
            eventList = new ArrayList<>();
        } else {
            eventList = gson.fromJson(json, type);
        }

        return eventList;
    }

    private static void saveEvents(SharedPreferences prefs) {
        String json = new Gson().toJson(eventList);
        prefs.edit().putString(EVENTS, json).apply();
    }
}
