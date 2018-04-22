package com.example.android.eventtimer.utils;

public class Event {
    private int label;
    private long duration;

    public Event(int label, long duration) {
        this.label = label;
        this.duration = duration;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String formattedDuration() {
        return TimerUtils.formatDuration(this.duration);
    }
}
