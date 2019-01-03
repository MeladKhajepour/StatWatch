package com.example.android.statwatch.eventComponents;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class EventsManager {
    private static final String EVENTS = "events";
    private static List<Event> eventList;

    public static void addEvent(SharedPreferences prefs, Event event) { // called in TimerService
        if(event != null) {
            eventList.add(0, event);
            saveEvents(prefs);
        }
    }

    static void undo(SharedPreferences prefs, List<Event> selectedEvents, List<Integer> selectedPositions) { // called in EventsFragment
        int i = 0;
        //todo - sort positions in ascending order and then sort events by index in descending order so the undo works properly
        // OR todo - copy the list of events before deleting them and on undo just set the eventList to the copied list
        for(Integer id : selectedPositions) {
            eventList.add(id, selectedEvents.get(i));
            i += 1;
        }
        saveEvents(prefs);
    }

    static void removeSelectedEvents(SharedPreferences prefs, List<Event> selectedEvents) { // called in EventsFragment
        eventList.removeAll(selectedEvents);
        saveEvents(prefs);
    }

    public static List<Event> getEvents(SharedPreferences prefs) {
        if(eventList == null) {
            loadEvents(prefs);
        }
        return eventList;
    }

    public static long getLatestTime(SharedPreferences prefs) { // called in TimerService
        if(eventList == null) {
            loadEvents(prefs);
        }
        return eventList.get(0).getDurationMillis();
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
