package com.example.android.eventtimer.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.eventtimer.R;

import java.util.List;


public class EventListAdapter extends ArrayAdapter<Event> {

    private static class ViewHolder {
        TextView label;
        TextView time;
    }

    public EventListAdapter(Context context, List<Event> eventList) {
        super(context, R.layout.time_row, eventList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.time_row, parent, false);

            viewHolder.label = convertView.findViewById(R.id.event_label);
            viewHolder.time = convertView.findViewById(R.id.event_time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String label = "Time " + String.valueOf(event.getLabel());

        viewHolder.label.setText(label);
        //TODO: add method to event obj to return formatted text of time instead of using timer
        //TODO: think more about ^ after refactoring the counter into a linear layout with h:m:s
        viewHolder.time.setText(Timer.formatDuration(event.getDurationMillis()));

        return convertView;
    }
}