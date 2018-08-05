package com.example.android.eventtimer.utils;

public final class Constants {
    // MAIN ACTIVITY
    public static final String STATS_FRAGMENT = "timer_stats_fragment";
    public static final String TIMER_FRAGMENT = "timer_fragment";
    public static final String EVENTS_FRAGMENT = "event_list_fragment";

    // TIMER FRAGMENT
    public static final String TIMER_FRAGMENT_RECEIVER = "com.example.android.eventtimer.TimerFragment";
    public static final String START_TIMER = "start";
    public static final String PAUSE_TIMER = "pause";
    public static final String RESUME_TIMER = "resume";
    public static final String RESET_TIMER = "reset";
    public static final String ADD_EVENT = "addEvent";
    public static final String NOTIFICATION_DISMISSED = "dismissed";
    public static final String TIMER_STATE = "timerState";
    public static final String IS_TIMING = "isTiming";
    public static final String IS_READY = "isReset";
    public static final String IS_PAUSED = "isPaused";
    public static final String TV_TIME = "tvTime";
    public static final String START_TIME_MILLIS = "startTimeMillis";

    // STATS FRAGMENT
    public static final String USE_LIST_STATS = "useListStats";
    public static final String STATS_EXPANSION = "stats_expansion";

    // TIMER
    public static final String LAST_SAVED_TIME = "lastSavedTime";
    public static final String LAST_SAVED_INDEX = "index";

    // UTILS
    public static final int ANIMATION_DURATION = 150;
    public static final int HIDE_BUTTONS_DURATION = 150;
    public static final int FRAGMENT_REFRESH_RATE = 100;
    public static final int NOTIFICATION_REFRESH_RATE = 100;
}
