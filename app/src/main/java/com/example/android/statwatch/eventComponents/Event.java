package com.example.android.statwatch.eventComponents;

public class Event {
    private int index;
    private long durationMillis;
    private String date;

    public Event(int index, long durationMillis, String date) {
        this.index = index;
        this.durationMillis = durationMillis;
        this.date = date;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public String getDate() {
        return date;
    }

    String getLabel() {
        return "Time " + String.valueOf(index);
    }
}