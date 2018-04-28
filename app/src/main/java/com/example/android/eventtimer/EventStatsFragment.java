package com.example.android.eventtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.eventtimer.utils.EventManager;
import com.example.android.eventtimer.utils.Event;
import com.example.android.eventtimer.utils.Timer;

import java.util.List;

import static com.example.android.eventtimer.utils.EventManager.PREFS;

public class EventStatsFragment extends Fragment {

    private TextView shortestEventView;
    private TextView averageTimeView;
    private TextView longestEventView;
    private long shortestEventMillis;
    private long averageTimeMillis;
    private long longestEventMillis;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void init(MainActivity app) {
        setupViews(app);
        refreshStats();
    }

    public void updateEventAdded() {
        List<Event> eventList = EventManager.getEvents(prefs);
        long newTime = eventList.get(eventList.size() - 1).getDurationMillis();

        if(eventList.size() == 1) {
            handleSingleEvent(newTime);
        } else {
            averageTimeMillis = (averageTimeMillis * (eventList.size() - 1) + newTime) / eventList.size();
            longestEventMillis = newTime > longestEventMillis ? newTime : longestEventMillis;
            shortestEventMillis = newTime < shortestEventMillis ? newTime : shortestEventMillis;
        }

        updateViews();
    }

    public void updateEventRemoved(long removedEventTime) {
        List<Event> eventList = EventManager.getEvents(prefs);

        if(eventList.isEmpty()) {
            averageTimeMillis = 0;
            shortestEventMillis = 0;
            longestEventMillis = 0;
        } else if(eventList.size() ==1) {
            handleSingleEvent(eventList.get(0).getDurationMillis());
        } else {
            averageTimeMillis = (averageTimeMillis * (eventList.size() + 1) - removedEventTime) / eventList.size();

            if(removedEventTime == shortestEventMillis) {
                recalculateShortestTime();
            }

            if(removedEventTime == longestEventMillis) {
                recalculateLongestTime();
            }
        }

        updateViews();
    }

    public void updateClearAllEvents() {
        averageTimeMillis = 0;
        shortestEventMillis = 0;
        longestEventMillis = 0;

        updateViews();
    }

    private void setupViews(MainActivity app) {
        shortestEventView = app.findViewById(R.id.shortest_event_time);
        averageTimeView = app.findViewById(R.id.average_time_time);
        longestEventView = app.findViewById(R.id.longest_event_time);
        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private void refreshStats() {
        List<Event> eventList = EventManager.getEvents(prefs);
        averageTimeMillis = 0;
        longestEventMillis = 0;

        if(eventList.isEmpty()) {
            shortestEventMillis = 0;
        } else {
            long currentDuration;
            shortestEventMillis = (long) Double.POSITIVE_INFINITY;

            for (Event event : eventList) {
                currentDuration = event.getDurationMillis();
                averageTimeMillis += currentDuration;
                longestEventMillis = currentDuration > longestEventMillis ? currentDuration : longestEventMillis;
                shortestEventMillis = currentDuration < shortestEventMillis ? currentDuration : shortestEventMillis;
            }

            averageTimeMillis /= eventList.size();
        }

        updateViews();
    }

    private void recalculateShortestTime() {
        shortestEventMillis = (long) Double.POSITIVE_INFINITY;

        for(Event event : EventManager.getEvents(prefs)) {
            shortestEventMillis = event.getDurationMillis() < shortestEventMillis ?
                    event.getDurationMillis() : shortestEventMillis;
        }
    }

    private void recalculateLongestTime() {
        longestEventMillis = 0;

        for(Event event : EventManager.getEvents(prefs)) {
            longestEventMillis = event.getDurationMillis() > longestEventMillis ?
                    event.getDurationMillis() : longestEventMillis;
        }
    }

    private void handleSingleEvent(long time) {
        averageTimeMillis = time;
        longestEventMillis = time;
        shortestEventMillis = time;
    }

    private void updateViews() {
        shortestEventView.setText(Timer.formatDuration(shortestEventMillis));
        averageTimeView.setText(Timer.formatDuration(averageTimeMillis));
        longestEventView.setText(Timer.formatDuration(longestEventMillis));
    }
}
