package com.example.android.eventtimer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.eventtimer.utils.Event;

import java.util.ArrayList;
import java.util.List;

public class TimerStatsFragment extends Fragment {

    private TextView shortestEventView;
    private TextView averageTimeView;
    private TextView longestEventView;
    private long shortestEventMillis;
    private long averageTimeMillis;
    private long longestEventMillis;
    private List<Event> eventList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timer_stats_fragment, container, false);

        shortestEventView = view.findViewById(R.id.shortest_event_time);
        averageTimeView = view.findViewById(R.id.average_time_time);
        longestEventView = view.findViewById(R.id.longest_event_time);

        //eventList = loadEvents();

        return view;
    }

    public void updateStats(Event event) {

    }
}
