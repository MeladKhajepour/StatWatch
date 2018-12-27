package com.example.android.statwatch.statsComponents;

import android.content.SharedPreferences;

import com.example.android.statwatch.eventComponents.Event;
import com.example.android.statwatch.eventComponents.EventsManager;
import com.example.android.statwatch.timerComponents.Timer;

import org.apache.commons.math3.distribution.TDistribution;

import java.util.List;

class StatsManager {
    private SharedPreferences prefs;
    private long shortest;
    private long longest;
    private long average;
    private long stdDev;
    private long moe;
    private float alpha;

    StatsManager(SharedPreferences prefs, int alphaDialogItem) {
        this.prefs = prefs;
        setAlpha(alphaDialogItem);
    }

    void setAlpha(int alphaDialogItem) {

        switch (alphaDialogItem) {
            case 0: alpha = 0.01f;
                break;

            case 1: alpha = 0.05f;
                break;

            case 2: alpha = 0.1f;
                break;

            default: alpha = 0.2f;
                break;
        }
    }

    Stats getStats() {
        calculateStats();

        return new Stats();
    }

    private void calculateStats() {
        List<Event> eventList = EventsManager.getEvents(prefs);

        if(eventList.isEmpty()) {
            shortest = 0;
            longest = 0;
            average = 0;
            stdDev = 0;
            moe = 0;

        } else {
            long eventDuration;
            shortest = (long) Double.POSITIVE_INFINITY;
            longest = 0;
            average = 0;

            for (Event event : eventList) {
                eventDuration = event.getDurationMillis();

                shortest = eventDuration < shortest ? eventDuration : shortest;
                longest = eventDuration > longest ? eventDuration : longest;
                average += eventDuration;
            }

            average /= eventList.size();
            calculateStdDev(eventList);
            calculateMoe(eventList);
        }
    }

    private void calculateStdDev(List<Event> eventList) {

        if(eventList.size() > 1) {
            long variance = 0;

            for(Event event : eventList) {
                variance += Math.pow((event.getDurationMillis() - average), 2);
            }

            stdDev = (long) Math.sqrt(variance/(eventList.size() - 1));
        }
    }

    private void calculateMoe(List<Event> eventList) {
        int eventListSize = eventList.size();

        if(eventListSize > 1) {
            int df = eventListSize - 1;
            double p = 1 - (alpha / 2);

            TDistribution dist = new TDistribution(df);
            float t = (float) dist.inverseCumulativeProbability(p);

            moe = (long) ((stdDev/ Math.sqrt(eventListSize)) * t);
        }
    }

    class Stats {
        String shortest;
        String longest;
        String average;
        String stdDev;
        String moe;

        private Stats() {
            shortest = Timer.formatDuration(StatsManager.this.shortest);
            longest = Timer.formatDuration(StatsManager.this.longest);
            average = Timer.formatDuration(StatsManager.this.average);
            stdDev = Timer.formatDuration(StatsManager.this.stdDev);
            moe = Timer.formatDuration(StatsManager.this.moe);
        }
    }
}