package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class EventsManager {

    public static final String PREFS = "prefs";

    private static final String EVENTS = "events";
    private static List<Event> eventList;

    public static void addToList(SharedPreferences prefs, Event event) {
        eventList.add(0, event);
        saveEvents(prefs);

    }

    public static void undoRemoveEvents(SharedPreferences prefs, List<Event> removedEvents, List<Integer> removedEventIndices) {
        int i = 0;
        for(Integer id : removedEventIndices) {
            eventList.add(id, removedEvents.get(i));
            i += 1;
        }
        saveEvents(prefs);
    }

    public static void removeSelectedEvents(SharedPreferences prefs, List<Event> selectedEvents) {
        eventList.removeAll(selectedEvents);
        saveEvents(prefs);
    }

    public static List<Event> getAllEvents(SharedPreferences prefs) {
        if(eventList == null) {
            loadEvents(prefs);
        }
        return eventList;
    }

    public static int getEventListSize(SharedPreferences prefs) {
        if(eventList == null) {
            loadEvents(prefs);
        }
        return eventList.size();
    }

    private static void loadEvents(SharedPreferences prefs) {
        Gson gson = new Gson();
        String json = prefs.getString(EVENTS, null);

        Type type = new TypeToken<ArrayList<Event>>() {}.getType();

        if(gson.fromJson(json, type) == null) {
            eventList = new ArrayList<>();
        } else {
            eventList = gson.fromJson(json, type);
        }
    }

    private static void saveEvents(SharedPreferences prefs) {
        String json = new Gson().toJson(eventList);
        prefs.edit().putString(EVENTS, json).apply();
    }

}
