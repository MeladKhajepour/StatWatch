package com.example.android.eventtimer.utils;

public class Event {
    private int label;
    private long durationMillis;

    public Event(int label, long durationMillis) {
        this.label = label;
        this.durationMillis = durationMillis;
    }

    //TODO: maybe add a method to return string formatted label
    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public long getDurationMillis() {
        return durationMillis;
    }
}
