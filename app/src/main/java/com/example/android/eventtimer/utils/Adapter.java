package com.example.android.eventtimer.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.eventtimer.R;


public class Adapter extends ArrayAdapter {

    private TimerUtils timerUtils;

    public Adapter(Context context, TimerUtils timerUtils) {
        super(context, R.layout.time_row, timerUtils.getEvents());
        this.timerUtils = timerUtils;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout timeRow = (LinearLayout) inflater.inflate(R.layout.time_row, parent, false);

        TextView label = timeRow.findViewById(R.id.time_label);
        TextView time = timeRow.findViewById(R.id.time_time);

        Event event = timerUtils.getEventAt(position);

        label.setText("Time " + event.getLabel());
        time.setText(TimerUtils.formatDuration(event.getDurationMillis()));

        return timeRow;
    }
}
