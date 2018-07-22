package com.example.android.eventtimer.utils;

public class Event {
    private int label;
    private long durationMillis;

    public Event(int label, long durationMillis) {
        this.label = label;
        this.durationMillis = durationMillis;
    }

    public String getLabelText() {
        return "Event " + String.valueOf(label);
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public String getFormattedDuration() {
        return Timer.formatDuration(durationMillis);
    }
}
