package com.example.android.statwatch.statsComponents;

import android.content.SharedPreferences;

import com.example.android.statwatch.eventComponents.Event;
import com.example.android.statwatch.eventComponents.EventsManager;
import com.example.android.statwatch.timerComponents.Timer;

import org.apache.commons.math3.distribution.TDistribution;

import java.util.List;

class StatsManager {
    private SharedPreferences prefs;
    private long minTime;
    private long maxTime;
    private long shortest;
    private long longest;
    private long average;
    private long stdDev;
    private long maxMoe;
    private long moe;
    private float alpha;

    StatsManager(SharedPreferences prefs, int ci) {
        this.prefs = prefs;
        setAlpha(ci);
    }

    void setAlpha(int ci) {
        alpha = (100 - ci) / 100f;
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
            maxMoe = 0;

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
        } else {
            stdDev = -1;
        }
    }

    private void calculateMoe(List<Event> eventList) {
        int eventListSize = eventList.size();

        if(eventListSize > 1) {
            int df = eventListSize - 1;
            double p = 1 - (alpha / 2);

            TDistribution dist = new TDistribution(df);
            float t = (float) dist.inverseCumulativeProbability(p);
            float tMax = (float) dist.inverseCumulativeProbability(0.995);

            moe = (long) ((stdDev/ Math.sqrt(eventListSize)) * t);
            maxMoe = (long) ((stdDev/ Math.sqrt(eventListSize)) * tMax);
            minTime = average - moe;
            maxTime = average + moe;

            if(minTime < 0) {
                minTime = 0;
            }
        } else {
            minTime = -1;
            maxTime = -1;
            moe = -1;
            maxMoe = -1;
        }
    }

    class Stats {
        String minTime;
        String maxTime;
        String shortest;
        String longest;
        String average;
        String stdDev;
        String moe;
        double ratio;

        private Stats() {
            minTime = Timer.formatDuration(StatsManager.this.minTime, false, false);
            maxTime = Timer.formatDuration(StatsManager.this.maxTime, false, false);
            shortest = Timer.formatDuration(StatsManager.this.shortest, false, false);
            longest = Timer.formatDuration(StatsManager.this.longest, false, false);
            average = Timer.formatDuration(StatsManager.this.average, false, false);
            stdDev = Timer.formatDuration(StatsManager.this.stdDev, false, false);
            moe = "± " + Timer.formatDuration(StatsManager.this.moe, false, false);
            ratio = ((double) StatsManager.this.moe / StatsManager.this.maxMoe);
        }
    }
}