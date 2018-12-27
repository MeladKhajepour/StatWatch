package com.example.android.statwatch.utils;

public final class Constants {
    // MAIN ACTIVITY
    public static final String STATS_FRAGMENT = "timer_stats_fragment";
    public static final String TIMER_FRAGMENT = "timer_fragment";
    public static final String EVENTS_FRAGMENT = "event_list_fragment";

    // TIMER FRAGMENT
    public static final String TEXT_VIEW_RECEIVER = "com.example.android.statwatch.timerComponents.TimerFragment";
    public static final String START_TIMER = "start";
    public static final String PAUSE_TIMER = "pause";
    public static final String RESUME_TIMER = "resume";
    public static final String RESET_TIMER = "reset";
    public static final String ADD_EVENT = "refresh";
    public static final String NOTIFICATION_DISMISSED = "dismissed";
    public static final String TIMER_STATE = "timerState";
    public static final String TIMING = "isTiming";
    public static final String READY = "isReset";
    public static final String PAUSED = "isPaused";
    public static final String TIMER_DURATION = "BroadcastTime";
    public static final String START_TIME_MILLIS = "startTimeMillis";

    // EVENTS MANAGER
    public static final String PREFS = "prefs";

    //STATS FRAGMENT
    public static final String SELECTED_ALPHA = "selectedAlpha";

    // TIMER
    public static final String ELAPSED_TIME = "elapsedTime";
    public static final String LAST_SAVED_INDEX = "index";

    // UTILS
    public static final int ANIMATION_DURATION = 120;
    public static final int HIDE_BUTTONS_DURATION = 150;
    public static final int REFRESH_RATE = 100;
}
