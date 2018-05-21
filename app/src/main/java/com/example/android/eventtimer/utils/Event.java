package com.example.android.eventtimer.utils;

public class Event {
    private int label;
    private long durationMillis;

    public Event(int label, long durationMillis) {
        this.label = label;
        this.durationMillis = durationMillis;
    }

    public int getLabelNumber() {
        return label;
    }

    public String getLabelText() {
        return "Event " + String.valueOf(label);
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public String getFormattedDuration() {
        return Timer.formatDuration(durationMillis);
    }
}
