package com.example.android.eventtimer.utils;

import android.content.SharedPreferences;

import java.util.List;

import static com.example.android.eventtimer.EventStatsFragment.useListStats;

public class StatsManager {
    public static final String SHORTEST_EVENT = "shortestEvent";
    public static final String AVERAGE_TIME = "averageTime";
    public static final String LONGEST_EVENT = "longestEvent";

    public static void updateEventAdded(SharedPreferences prefs, Event event) {
        int eventListSize = EventsManager.getEventListSize(prefs);

        long newTime = event.getDurationMillis();

        if(eventListSize == 1) {
            updateSingleEvent(prefs, newTime);
        } else {
            long longestEventMillis = getLongestEvent(prefs);
            long shortestEventMillis = getShortestEvent(prefs);
            long averageTimeMillis = getAverageTime(prefs);

            averageTimeMillis = (averageTimeMillis * (eventListSize - 1) + newTime) / eventListSize;
            longestEventMillis = newTime > longestEventMillis ? newTime : longestEventMillis;
            shortestEventMillis = newTime < shortestEventMillis ? newTime : shortestEventMillis;

            setShortestEvent(prefs, shortestEventMillis);
            setAverageTime(prefs, averageTimeMillis);
            setLongestEvent(prefs, longestEventMillis);
        }
    }

    public static void updateEventsRemoved(SharedPreferences prefs, List<Event> removedEventsList) {
        if(useListStats) {
            int numEventsInList = EventsManager.getEventListSize(prefs);

            if(numEventsInList == 0) {
                StatsManager.setShortestEvent(prefs, 0);
                StatsManager.setAverageTime(prefs, 0);
                StatsManager.setLongestEvent(prefs, 0);
            } else if(numEventsInList ==1) {
                updateSingleEvent(prefs, EventsManager.getEvents(prefs).get(0).getDurationMillis());
            } else {
                long totalRemovedTime = 0;
                long averageTimeMillis = StatsManager.getAverageTime(prefs);
                boolean updateLongest = false;
                boolean updateShortest = false;
                for(Event removedEvent : removedEventsList) {
                    long removedTime = removedEvent.getDurationMillis();

                    totalRemovedTime += removedTime;

                    if(removedTime == StatsManager.getShortestEvent(prefs)) {
                        updateShortest = true;
                    }

                    if(removedTime == StatsManager.getLongestEvent(prefs)) {
                        updateLongest = true;
                    }
                }

                averageTimeMillis = (averageTimeMillis * (numEventsInList + removedEventsList.size()) - totalRemovedTime) / numEventsInList;

                if(updateShortest) {
                    recalculateShortestTime(prefs);
                }

                if(updateLongest) {
                    recalculateLongestTime(prefs);
                }

                StatsManager.setAverageTime(prefs, averageTimeMillis);
            }
        }
    }

    public static void recalculateListStats(SharedPreferences prefs) {
        List<Event> eventList = EventsManager.getEvents(prefs);
        long averageTimeMillis = 0;
        long longestEventMillis = 0;
        long shortestEventMillis = (long) Double.POSITIVE_INFINITY;

        if(eventList.isEmpty()) {
            shortestEventMillis = 0;
        } else {
            long currentDuration;

            for (Event event : eventList) {
                currentDuration = event.getDurationMillis();
                averageTimeMillis += currentDuration;
                longestEventMillis = currentDuration > longestEventMillis ? currentDuration : longestEventMillis;
                shortestEventMillis = currentDuration < shortestEventMillis ? currentDuration : shortestEventMillis;
            }

            averageTimeMillis /= eventList.size();
        }

        setShortestEvent(prefs, shortestEventMillis);
        setAverageTime(prefs, averageTimeMillis);
        setLongestEvent(prefs, longestEventMillis);
    }

    private static void recalculateShortestTime(SharedPreferences prefs) {
        long shortestEventMillis = (long) Double.POSITIVE_INFINITY;

        for(Event event : EventsManager.getEvents(prefs)) {
            shortestEventMillis = event.getDurationMillis() < shortestEventMillis ?
                    event.getDurationMillis() : shortestEventMillis;
        }

        StatsManager.setShortestEvent(prefs, shortestEventMillis);
    }

    private static void recalculateLongestTime(SharedPreferences prefs) {
        long longestEventMillis = 0;

        for(Event event : EventsManager.getEvents(prefs)) {
            longestEventMillis = event.getDurationMillis() > longestEventMillis ?
                    event.getDurationMillis() : longestEventMillis;
        }

        StatsManager.setLongestEvent(prefs, longestEventMillis);
    }

    private static void updateSingleEvent(SharedPreferences prefs, long time) {
        StatsManager.setShortestEvent(prefs, time);
        StatsManager.setAverageTime(prefs, time);
        StatsManager.setLongestEvent(prefs, time);
    }

    public static void setShortestEvent(SharedPreferences prefs, long time) {
        prefs.edit().putLong(SHORTEST_EVENT, time).apply();
    }

    public static void setAverageTime(SharedPreferences prefs, long time) {
        prefs.edit().putLong(AVERAGE_TIME, time).apply();
    }

    public static void setLongestEvent(SharedPreferences prefs, long time) {
        prefs.edit().putLong(LONGEST_EVENT, time).apply();
    }

    public static long getShortestEvent(SharedPreferences prefs) {
        return prefs.getLong(SHORTEST_EVENT, 0);
    }

    public static long getAverageTime(SharedPreferences prefs) {
        return prefs.getLong(AVERAGE_TIME, 0);
    }

    public static long getLongestEvent(SharedPreferences prefs) {
        return prefs.getLong(LONGEST_EVENT, 0);
    }
}
